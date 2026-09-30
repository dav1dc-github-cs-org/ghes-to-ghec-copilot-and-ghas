package com.example.flightsim.fdm;

import com.example.flightsim.core.Signals;
import com.example.flightsim.core.SimBus;
import com.example.flightsim.core.SimComponent;
import com.example.flightsim.core.SimConfig;
import com.example.flightsim.core.SimContext;
import com.example.flightsim.core.units.Units;

/**
 * Flight dynamics component for the fictional FS-200 full-flight trainer.
 */
public final class FlightDynamicsModel implements SimComponent {

    /** Elevator jam malfunction id. */
    public static final String ELEVATOR_JAM = "flight-dynamics.elevator-jam";
    /** Pitot blocked malfunction id. */
    public static final String PITOT_BLOCKED = "flight-dynamics.pitot-blocked";

    private final AircraftParameters parameters;
    private final AeroModel aeroModel;
    private final TrimSolver trimSolver;
    private EquationsOfMotion equations;
    private AircraftState state;
    private AeroModel.Controls trimControls;
    private double trimThrustPerEngineN;
    private double fieldElevationM;
    private double frozenIasMps = Double.NaN;
    private double lastElevator;

    /** Creates a model using default FS-200 parameters. */
    public FlightDynamicsModel() {
        this(AircraftParameters.fs200());
    }

    /**
     * Creates a model with custom parameters.
     *
     * @param parameters aircraft parameters
     */
    public FlightDynamicsModel(AircraftParameters parameters) {
        this.parameters = parameters;
        this.aeroModel = new AeroModel(parameters);
        this.trimSolver = new TrimSolver(aeroModel);
    }

    @Override
    public String name() {
        return "flight-dynamics";
    }

    @Override
    public void initialise(SimContext context) {
        context.malfunctions().register(ELEVATOR_JAM, "Elevator control jam", 27);
        context.malfunctions().register(PITOT_BLOCKED, "Pitot system blocked; indicated airspeed freezes", 34);
        SimConfig config = context.config();
        fieldElevationM = Units.feetToMetres(config.getDouble("fdm.field.elevation.ft", 0.0));
        double altitudeM = Units.feetToMetres(config.getDouble("fdm.initial.altitude.ft", 5000.0));
        double iasMps = Units.knotsToMps(config.getDouble("fdm.initial.ias.kt", 220.0));
        double headingRad = Units.degreesToRadians(config.getDouble("fdm.initial.heading.deg", 90.0));
        double fuelKg = context.bus().read(Signals.FUEL_TOTAL_KG, config.getDouble("fdm.initial.fuel.kg", parameters.nominalFuelKg()));
        double massKg = Math.min(parameters.maxTakeoffMassKg(), parameters.emptyMassKg() + Math.max(0.0, fuelKg));
        TrimResult trim = trimSolver.solve(iasMps, Math.max(fieldElevationM, altitudeM), headingRad, massKg);
        state = trim.state();
        trimControls = trim.controls();
        lastElevator = trimControls.elevator();
        trimThrustPerEngineN = 0.5 * trim.totalThrustN();
        equations = new EquationsOfMotion(parameters, aeroModel, fieldElevationM);
        if (!context.bus().has(Signals.engine(1, "thrust.n"))) {
            context.bus().publish(Signals.engine(1, "thrust.n"), trimThrustPerEngineN);
        }
        if (!context.bus().has(Signals.engine(2, "thrust.n"))) {
            context.bus().publish(Signals.engine(2, "thrust.n"), trimThrustPerEngineN);
        }
        publish(context, Atmosphere.at(state.altitudeM()), iasMps);
    }

    @Override
    public void step(SimContext context, double dtSeconds) {
        SimBus bus = context.bus();
        double fuelKg = bus.read(Signals.FUEL_TOTAL_KG, state.massKg() - parameters.emptyMassKg());
        double massKg = Math.min(parameters.maxTakeoffMassKg(), parameters.emptyMassKg() + Math.max(0.0, fuelKg));
        state = state.withMass(massKg);
        double elevator = readControl(bus, Signals.ELEVATOR_CMD, trimControls.elevator());
        if (context.malfunctions().isActive(ELEVATOR_JAM)) {
            elevator = lastElevator;
        } else {
            lastElevator = elevator;
        }
        AeroModel.Controls controls = new AeroModel.Controls(
                elevator,
                readControl(bus, Signals.AILERON_CMD, trimControls.aileron()),
                readControl(bus, Signals.RUDDER_CMD, trimControls.rudder()),
                clamp(bus.read(Signals.FLAP_CMD, trimControls.flap()), 0.0, 1.0));
        boolean gearDown = bus.readFlag(Signals.GEAR_DOWN_LOCKED) || bus.readFlag(Signals.GEAR_LEVER_DOWN);
        Atmosphere.Sample atmosphere = Atmosphere.at(state.altitudeM(), context.config().getDouble("fdm.isa-deviation.k", 0.0));
        state = equations.integrate(state, controls, atmosphere, bus, gearDown, dtSeconds);
        double qbar = 0.5 * atmosphere.densityKgPerM3() * state.trueAirspeedMps() * state.trueAirspeedMps();
        double ias = Atmosphere.dynamicPressureToCas(qbar);
        if (context.malfunctions().isActive(PITOT_BLOCKED)) {
            if (Double.isNaN(frozenIasMps)) {
                frozenIasMps = ias;
            }
            ias = frozenIasMps;
        } else {
            frozenIasMps = Double.NaN;
        }
        publish(context, atmosphere, ias);
    }

    @Override
    public void reset() {
        state = null;
        equations = null;
        frozenIasMps = Double.NaN;
        lastElevator = 0.0;
    }

    /**
     * Returns the latest aircraft state.
     *
     * @return current state
     */
    public AircraftState state() {
        return state;
    }

    private void publish(SimContext context, Atmosphere.Sample atmosphere, double iasMps) {
        SimBus bus = context.bus();
        double windSpeed = Units.knotsToMps(context.config().getDouble("environment.wind.speed.kt", 0.0));
        double windFrom = Units.degreesToRadians(context.config().getDouble("environment.wind.from.deg", 0.0));
        bus.publish(Signals.ALTITUDE_M, state.altitudeM());
        bus.publish(Signals.HEIGHT_AGL_M, Math.max(0.0, state.altitudeM() - fieldElevationM));
        bus.publish(Signals.TRUE_AIRSPEED_MPS, state.trueAirspeedMps());
        bus.publish(Signals.INDICATED_AIRSPEED_MPS, iasMps);
        bus.publish(Signals.GROUND_SPEED_MPS, state.groundSpeedMps());
        bus.publish(Signals.VERTICAL_SPEED_MPS, state.verticalSpeedMps());
        bus.publish(Signals.MACH, state.trueAirspeedMps() / atmosphere.speedOfSoundMps());
        bus.publish(Signals.PITCH_RAD, state.thetaRad());
        bus.publish(Signals.ROLL_RAD, state.phiRad());
        bus.publish(Signals.HEADING_RAD, state.psiRad());
        bus.publish(Signals.ALPHA_RAD, state.alphaRad());
        bus.publish(Signals.PITCH_RATE_RADPS, state.qRadps());
        bus.publish(Signals.ROLL_RATE_RADPS, state.pRadps());
        bus.publish(Signals.LATITUDE_RAD, state.latitudeRad());
        bus.publish(Signals.LONGITUDE_RAD, state.longitudeRad());
        bus.publish(Signals.GROSS_MASS_KG, state.massKg());
        bus.publish(Signals.WEIGHT_ON_WHEELS, equations != null && equations.weightOnWheels());
        bus.publish(Signals.AIR_DENSITY_KGPM3, atmosphere.densityKgPerM3());
        bus.publish(Signals.OUTSIDE_AIR_TEMP_K, atmosphere.temperatureK());
        bus.publish(Signals.STATIC_PRESSURE_PA, atmosphere.pressurePa());
        bus.publish(Signals.WIND_SPEED_MPS, windSpeed);
        bus.publish(Signals.WIND_FROM_RAD, windFrom);
    }

    private static double readControl(SimBus bus, String signal, double fallback) {
        return clamp(bus.read(signal, fallback), -1.0, 1.0);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
