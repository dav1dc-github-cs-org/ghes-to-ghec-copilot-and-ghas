package com.example.flightsim.systems.hydraulics;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.flightsim.core.SimContext;
import com.example.flightsim.core.Signals;
import org.junit.jupiter.api.Test;

final class HydraulicsTest {

    @Test
    void pressureBuildsWithEnginePumpAndCollapsesOnFailure() {
        SimContext context = SimContext.create(41L);
        Hydraulics hydraulics = new Hydraulics();
        hydraulics.initialise(context);
        context.bus().publish(Signals.engine(1, "n2.pct"), 72.0);
        run(hydraulics, context, 5.0);
        assertTrue(context.bus().read(Signals.hydraulicPressurePsi("green")) > 2_600.0);

        context.malfunctions().activate(Hydraulics.GREEN_PUMP_FAIL);
        run(hydraulics, context, 24.0);

        assertTrue(context.bus().read(Signals.hydraulicPressurePsi("green")) < 800.0);
    }

    @Test
    void ptuRestoresGreenPressureFromYellowSystem() {
        SimContext context = SimContext.create(42L);
        Hydraulics hydraulics = new Hydraulics();
        hydraulics.initialise(context);
        context.bus().publish(Signals.engine(1, "n2.pct"), 0.0);
        context.bus().publish(Signals.engine(2, "n2.pct"), 80.0);

        run(hydraulics, context, 8.0);

        assertTrue(context.bus().readFlag(HydraulicSignals.PTU_ACTIVE));
        assertTrue(context.bus().read(Signals.hydraulicPressurePsi("green")) > 1_000.0);
        assertTrue(context.bus().read(Signals.hydraulicPressurePsi("yellow")) > 2_800.0);
    }

    private static void run(Hydraulics hydraulics, SimContext context, double seconds) {
        int steps = (int) Math.round(seconds * 20.0);
        for (int i = 0; i < steps; i++) {
            hydraulics.step(context, 0.05);
        }
    }
}
