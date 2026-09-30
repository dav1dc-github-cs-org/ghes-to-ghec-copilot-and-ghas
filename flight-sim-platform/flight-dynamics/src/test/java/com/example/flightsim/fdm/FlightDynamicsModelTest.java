package com.example.flightsim.fdm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.flightsim.core.MalfunctionRegistry;
import com.example.flightsim.core.Signals;
import com.example.flightsim.core.SimBus;
import com.example.flightsim.core.SimConfig;
import com.example.flightsim.core.SimContext;
import com.example.flightsim.core.SimExecutive;
import com.example.flightsim.core.math.DeterministicRandom;
import com.example.flightsim.core.math.Vector3;
import com.example.flightsim.core.units.Units;

import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Integration tests for the FS-200 flight dynamics model.
 */
final class FlightDynamicsModelTest {

    @Test
    void trimConvergesWithLiftNearWeight() {
        AircraftParameters parameters = AircraftParameters.fs200();
        TrimSolver solver = new TrimSolver(new AeroModel(parameters));
        double mass = parameters.emptyMassKg() + 3000.0;

        TrimResult result = solver.solve(Units.knotsToMps(220.0), Units.feetToMetres(5000.0), 0.0, mass);

        assertEquals(mass * Units.STANDARD_GRAVITY, result.liftN(), mass * Units.STANDARD_GRAVITY * 0.015);
        assertTrue(Math.abs(result.controls().elevator()) < 0.35);
        assertTrue(result.totalThrustN() > 0.0);
    }

    @Test
    void handsOffTrimmedFlightHoldsAltitudeAndSpeedForOneMinute() {
        SimContext context = contextWithDefaults();
        FlightDynamicsModel model = new FlightDynamicsModel();
        SimExecutive executive = new SimExecutive(context);
        executive.register(model, 60);

        executive.stepFrame();
        double initialAltitude = context.bus().read(Signals.ALTITUDE_M);
        double initialSpeed = context.bus().read(Signals.TRUE_AIRSPEED_MPS);
        executive.runFor(60.0);

        assertEquals(initialAltitude, context.bus().read(Signals.ALTITUDE_M), 30.0);
        assertEquals(initialSpeed, context.bus().read(Signals.TRUE_AIRSPEED_MPS), 3.0);
    }

    @Test
    void elevatorUpCommandIncreasesPitch() {
        SimContext context = contextWithDefaults();
        FlightDynamicsModel model = new FlightDynamicsModel();
        SimExecutive executive = new SimExecutive(context);
        executive.register(model, 60);

        executive.stepFrame();
        double initialPitch = context.bus().read(Signals.PITCH_RAD);
        context.bus().publish(Signals.ELEVATOR_CMD, 0.35);
        executive.runFor(4.0);

        assertTrue(context.bus().read(Signals.PITCH_RAD) > initialPitch + Math.toRadians(0.5));
    }

    @Test
    void energyAndTransformSanityChecksStayFinite() {
        AircraftParameters parameters = AircraftParameters.fs200();
        AeroModel aero = new AeroModel(parameters);
        EquationsOfMotion equations = new EquationsOfMotion(parameters, aero, 0.0);
        TrimResult trim = new TrimSolver(aero).solve(Units.knotsToMps(200.0), 1000.0, Math.toRadians(35.0), 23000.0);
        SimBus bus = new SimBus();
        bus.publish(Signals.engine(1, "thrust.n"), trim.totalThrustN() / 2.0);
        bus.publish(Signals.engine(2, "thrust.n"), trim.totalThrustN() / 2.0);

        AircraftState next = equations.integrate(trim.state(), trim.controls(), Atmosphere.at(1000.0), bus, false, 1.0 / 60.0);
        Vector3 body = new Vector3(120.0, 3.0, -1.0);
        Vector3 ned = EquationsOfMotion.bodyToNed(body, 0.1, 0.05, 0.3);
        Vector3 restored = EquationsOfMotion.nedToBody(ned, 0.1, 0.05, 0.3);

        assertTrue(Double.isFinite(next.altitudeM()));
        assertTrue(next.trueAirspeedMps() > 40.0);
        assertEquals(body.x(), restored.x(), 1.0e-9);
        assertEquals(body.y(), restored.y(), 1.0e-9);
        assertEquals(body.z(), restored.z(), 1.0e-9);
    }

    private static SimContext contextWithDefaults() {
        SimConfig config = SimConfig.of(Map.of(
                "fdm.initial.altitude.ft", "5000",
                "fdm.initial.ias.kt", "220",
                "fdm.initial.heading.deg", "90",
                "fdm.initial.fuel.kg", "3200"));
        return new SimContext(new SimBus(), new MalfunctionRegistry(), new DeterministicRandom(42L), config);
    }
}
