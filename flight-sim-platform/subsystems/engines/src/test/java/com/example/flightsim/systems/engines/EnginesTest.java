package com.example.flightsim.systems.engines;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.flightsim.core.SimContext;
import com.example.flightsim.core.Signals;
import org.junit.jupiter.api.Test;

final class EnginesTest {

    @Test
    void engineSpoolsUpAndThrustRisesWithThrottle() {
        SimContext context = SimContext.create(11L);
        Engines engines = new Engines();
        engines.initialise(context);
        context.bus().publish(Signals.engine(1, EngineSignals.START_SWITCH), true);
        context.bus().publish(Signals.throttle(1), 0.2);
        run(engines, context, 4.0);
        double idleThrust = context.bus().read(Signals.engine(1, "thrust.n"));

        context.bus().publish(Signals.throttle(1), 0.85);
        run(engines, context, 18.0);

        assertTrue(context.bus().readFlag(Signals.engine(1, "running")));
        assertTrue(context.bus().read(Signals.engine(1, "n2.pct")) > 80.0);
        assertTrue(context.bus().read(Signals.engine(1, "thrust.n")) > idleThrust + 25_000.0);
        assertTrue(context.bus().read(Signals.engine(1, "fuel-flow.kgps")) > 0.5);
    }

    @Test
    void fuelStarvationFlamesOutEngine() {
        SimContext context = SimContext.create(12L);
        Engines engines = new Engines();
        engines.initialise(context);
        context.bus().publish(Signals.engine(2, EngineSignals.START_SWITCH), true);
        context.bus().publish(Signals.throttle(2), 0.7);
        run(engines, context, 10.0);

        context.bus().publish(Signals.fuelFeedAvailable(2), false);
        engines.step(context, 1.0);

        assertEquals(0.0, context.bus().read(Signals.engine(2, "fuel-flow.kgps")), 0.001);
        assertEquals(0.0, context.bus().read(Signals.engine(2, "running")), 0.001);
    }

    private static void run(Engines engines, SimContext context, double seconds) {
        int steps = (int) Math.round(seconds * 20.0);
        for (int i = 0; i < steps; i++) {
            engines.step(context, 0.05);
        }
    }
}
