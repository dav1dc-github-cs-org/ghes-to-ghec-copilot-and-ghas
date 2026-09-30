package com.example.flightsim.systems.landinggear;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.flightsim.core.SimContext;
import com.example.flightsim.core.Signals;
import org.junit.jupiter.api.Test;

final class LandingGearTest {

    @Test
    void gearExtendsInAboutTenSecondsWithHydraulics() {
        SimContext context = SimContext.create(51L);
        LandingGear gear = new LandingGear();
        gear.initialise(context);
        context.bus().publish(Signals.GEAR_LEVER_DOWN, true);
        context.bus().publish(Signals.hydraulicPressurePsi("green"), 3_000.0);

        run(gear, context, 10.2);

        assertTrue(context.bus().readFlag(Signals.GEAR_DOWN_LOCKED));
        assertFalse(context.bus().readFlag(Signals.GEAR_IN_TRANSIT));
    }

    @Test
    void gravityExtensionWorksWithoutHydraulics() {
        SimContext context = SimContext.create(52L);
        LandingGear gear = new LandingGear();
        gear.initialise(context);
        context.bus().publish(Signals.GEAR_LEVER_DOWN, true);
        context.bus().publish(LandingGearSignals.ALTERNATE_EXTENSION, true);
        context.bus().publish(Signals.hydraulicPressurePsi("green"), 0.0);

        run(gear, context, 15.0);

        assertTrue(context.bus().readFlag(Signals.GEAR_DOWN_LOCKED));
        assertTrue(context.bus().read("gear.nose.position") > 0.98);
    }

    private static void run(LandingGear gear, SimContext context, double seconds) {
        int steps = (int) Math.round(seconds * 20.0);
        for (int i = 0; i < steps; i++) {
            gear.step(context, 0.05);
        }
    }
}
