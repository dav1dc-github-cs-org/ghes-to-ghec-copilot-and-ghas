package com.example.flightsim.systems.electrical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.flightsim.core.SimContext;
import com.example.flightsim.core.Signals;
import org.junit.jupiter.api.Test;

final class ElectricalTest {

    @Test
    void engineGeneratorsPowerBothAcBusesThroughTie() {
        SimContext context = SimContext.create(31L);
        Electrical electrical = new Electrical();
        electrical.initialise(context);
        context.bus().publish(Signals.engine(1, "n2.pct"), 72.0);
        context.bus().publish(Signals.engine(2, "n2.pct"), 0.0);

        electrical.step(context, 1.0);

        assertEquals(115.0, context.bus().read(Signals.busVolts("ac1")), 0.001);
        assertEquals(115.0, context.bus().read(Signals.busVolts("ac2")), 0.001);
        assertEquals(28.0, context.bus().read(Signals.busVolts("dc-ess")), 0.001);
    }

    @Test
    void busFaultDepowersAffectedBusAndBatteryKeepsEssentialsAlive() {
        SimContext context = SimContext.create(32L);
        Electrical electrical = new Electrical();
        electrical.initialise(context);
        context.malfunctions().activate(Electrical.AC_1_FAULT);
        context.bus().publish(Signals.engine(1, "n2.pct"), 80.0);
        context.bus().publish(Signals.engine(2, "n2.pct"), 0.0);

        electrical.step(context, 1.0);

        assertEquals(0.0, context.bus().read(Signals.busVolts("ac1")), 0.001);
        assertTrue(context.bus().read(Signals.busVolts("dc-ess")) > 20.0);
    }
}
