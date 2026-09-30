package com.example.flightsim.systems.engines;

import com.example.flightsim.core.SimComponent;
import com.example.flightsim.core.SimContext;
import com.example.flightsim.core.Signals;
import com.example.flightsim.core.math.FirstOrderLag;
import com.example.flightsim.core.units.Units;

/**
 * Twin-spool turbofan model for the fictional FS-200 training aircraft.
 */
public final class Engines implements SimComponent {

    /** Engine fire malfunction on engine 1. */
    public static final String ENG_1_FIRE = "engines.1.fire";
    /** Engine fire malfunction on engine 2. */
    public static final String ENG_2_FIRE = "engines.2.fire";
    /** Flameout malfunction on engine 1. */
    public static final String ENG_1_FLAMEOUT = "engines.1.flameout";
    /** Flameout malfunction on engine 2. */
    public static final String ENG_2_FLAMEOUT = "engines.2.flameout";
    /** N1 indication malfunction on engine 1. */
    public static final String ENG_1_N1_SENSOR_FAIL = "engines.1.n1-sensor-fail";
    /** N1 indication malfunction on engine 2. */
    public static final String ENG_2_N1_SENSOR_FAIL = "engines.2.n1-sensor-fail";
    /** EGT overtemperature malfunction on engine 1. */
    public static final String ENG_1_EGT_OVERTEMP = "engines.1.egt-overtemp";
    /** EGT overtemperature malfunction on engine 2. */
    public static final String ENG_2_EGT_OVERTEMP = "engines.2.egt-overtemp";

    private static final int ENGINE_COUNT = 2;
    private static final double SEA_LEVEL_DENSITY = 1.225;
    private static final double IDLE_N1_PCT = 24.0;
    private static final double IDLE_N2_PCT = 58.0;
    private static final double MAX_N1_PCT = 96.0;
    private static final double MAX_N2_PCT = 101.0;
    private static final double MAX_THRUST_N = 76_000.0;

    private final EngineState[] engines = {new EngineState(), new EngineState()};

    @Override
    public String name() {
        return "engines";
    }

    @Override
    public void initialise(SimContext context) {
        context.malfunctions().register(ENG_1_FIRE, "Engine 1 fire indication and thrust loss", 72);
        context.malfunctions().register(ENG_2_FIRE, "Engine 2 fire indication and thrust loss", 72);
        context.malfunctions().register(ENG_1_FLAMEOUT, "Engine 1 compressor flameout", 72);
        context.malfunctions().register(ENG_2_FLAMEOUT, "Engine 2 compressor flameout", 72);
        context.malfunctions().register(ENG_1_N1_SENSOR_FAIL, "Engine 1 N1 sensor frozen low", 72);
        context.malfunctions().register(ENG_2_N1_SENSOR_FAIL, "Engine 2 N1 sensor frozen low", 72);
        context.malfunctions().register(ENG_1_EGT_OVERTEMP, "Engine 1 EGT overtemperature", 72);
        context.malfunctions().register(ENG_2_EGT_OVERTEMP, "Engine 2 EGT overtemperature", 72);
    }

    @Override
    public void step(SimContext context, double dtSeconds) {
        double density = context.bus().read(Signals.AIR_DENSITY_KGPM3, SEA_LEVEL_DENSITY);
        double mach = context.bus().read(Signals.MACH, 0.0);
        double oatKelvin = context.bus().read(Signals.OUTSIDE_AIR_TEMP_K, Units.celsiusToKelvin(15.0));
        for (int index = 1; index <= ENGINE_COUNT; index++) {
            stepEngine(context, index, density, mach, oatKelvin, dtSeconds);
        }
    }

    @Override
    public void reset() {
        for (EngineState engine : engines) {
            engine.reset();
        }
    }

    private void stepEngine(SimContext context, int index, double density, double mach, double oatKelvin, double dtSeconds) {
        EngineState engine = engines[index - 1];
        double throttle = clamp(context.bus().read(Signals.throttle(index), 0.0), 0.0, 1.0);
        boolean fuelAvailable = !context.bus().has(Signals.fuelFeedAvailable(index))
                || context.bus().readFlag(Signals.fuelFeedAvailable(index));
        boolean fuelLeverOn = context.bus().readFlag(Signals.engine(index, EngineSignals.FUEL_LEVER))
                || !context.bus().has(Signals.engine(index, EngineSignals.FUEL_LEVER));
        boolean startSwitch = context.bus().readFlag(Signals.engine(index, EngineSignals.START_SWITCH));
        boolean fire = context.malfunctions().isActive(fireId(index));
        boolean flameout = context.malfunctions().isActive(flameoutId(index));

        if (!engine.running && startSwitch && fuelLeverOn && fuelAvailable && !flameout) {
            engine.running = true;
        }
        if (!fuelLeverOn || !fuelAvailable || flameout || fire) {
            engine.running = false;
        }

        double n2Target = engine.running ? IDLE_N2_PCT + throttle * (MAX_N2_PCT - IDLE_N2_PCT) : 0.0;
        double n1Target = engine.running ? IDLE_N1_PCT + throttle * (MAX_N1_PCT - IDLE_N1_PCT) : 0.0;
        double n2 = engine.n2.update(n2Target, dtSeconds);
        double n1 = engine.n1.update(n1Target, dtSeconds);
        double thrust = thrust(n1, density, mach, fire, engine.running);
        double fuelFlow = engine.running ? 0.11 + throttle * 0.72 + thrust / 260_000.0 : 0.0;
        double egtDegC = engine.running ? 360.0 + throttle * 430.0 + Math.max(0.0, oatKelvin - 288.15) * 0.7 : 80.0;
        if (context.malfunctions().isActive(egtId(index))) {
            egtDegC += 170.0;
        }
        double indicatedN1 = context.malfunctions().isActive(n1Id(index)) ? Math.min(18.0, n1) : n1;
        double oilPressure = engine.running ? 38.0 + 0.42 * n2 : Math.max(0.0, 0.25 * n2);

        context.bus().publish(Signals.engine(index, "n1.pct"), indicatedN1);
        context.bus().publish(Signals.engine(index, "n2.pct"), n2);
        context.bus().publish(Signals.engine(index, "egt.degc"), egtDegC);
        context.bus().publish(Signals.engine(index, "fuel-flow.kgps"), fuelFlow);
        context.bus().publish(Signals.engine(index, "thrust.n"), thrust);
        context.bus().publish(Signals.engine(index, "running"), engine.running);
        context.bus().publish(Signals.engine(index, "oil-pressure.psi"), oilPressure);
    }

    private static double thrust(double n1, double density, double mach, boolean fire, boolean running) {
        if (!running) {
            return 0.0;
        }
        double densityRatio = clamp(density / SEA_LEVEL_DENSITY, 0.25, 1.15);
        double machLapse = clamp(1.0 - 0.35 * mach, 0.62, 1.05);
        double spool = Math.pow(clamp((n1 - IDLE_N1_PCT) / (MAX_N1_PCT - IDLE_N1_PCT), 0.0, 1.0), 1.35);
        double fireFactor = fire ? 0.2 : 1.0;
        return MAX_THRUST_N * spool * densityRatio * machLapse * fireFactor;
    }

    private static String fireId(int index) {
        return index == 1 ? ENG_1_FIRE : ENG_2_FIRE;
    }

    private static String flameoutId(int index) {
        return index == 1 ? ENG_1_FLAMEOUT : ENG_2_FLAMEOUT;
    }

    private static String n1Id(int index) {
        return index == 1 ? ENG_1_N1_SENSOR_FAIL : ENG_2_N1_SENSOR_FAIL;
    }

    private static String egtId(int index) {
        return index == 1 ? ENG_1_EGT_OVERTEMP : ENG_2_EGT_OVERTEMP;
    }

    private static double clamp(double value, double minimum, double maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    private static final class EngineState {
        private final FirstOrderLag n1 = new FirstOrderLag(5.5, 0.0);
        private final FirstOrderLag n2 = new FirstOrderLag(3.0, 0.0);
        private boolean running;

        private void reset() {
            n1.reset(0.0);
            n2.reset(0.0);
            running = false;
        }
    }
}
