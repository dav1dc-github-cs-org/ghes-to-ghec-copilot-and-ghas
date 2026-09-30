package com.example.flightsim.systems.fuel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.flightsim.core.SimContext;
import com.example.flightsim.core.Signals;
import org.junit.jupiter.api.Test;

final class FuelTest {

    @Test
    void fuelQuantityDropsByEngineFlowIntegral() {
        SimContext context = SimContext.create(21L);
        Fuel fuel = new Fuel();
        fuel.initialise(context);
        fuel.step(context, 0.1);
        double initial = context.bus().read(Signals.FUEL_TOTAL_KG);

        context.bus().publish(Signals.engine(1, "fuel-flow.kgps"), 0.6);
        context.bus().publish(Signals.engine(2, "fuel-flow.kgps"), 0.4);
        for (int i = 0; i < 100; i++) {
            fuel.step(context, 0.1);
        }

        assertEquals(initial - 10.0, context.bus().read(Signals.FUEL_TOTAL_KG), 0.2);
        assertTrue(context.bus().readFlag(Signals.fuelFeedAvailable(1)));
        assertTrue(context.bus().readFlag(Signals.fuelFeedAvailable(2)));
    }

    @Test
    void failedPumpRemovesFeedWhenCentreTankCannotHelp() {
        SimContext context = SimContext.create(22L);
        Fuel fuel = new Fuel();
        fuel.initialise(context);
        context.malfunctions().activate(Fuel.LEFT_PUMP_FAIL);
        context.bus().publish(FuelSignals.CENTRE_PUMP_ON, false);

        fuel.step(context, 0.1);

        assertEquals(0.0, context.bus().read(Signals.fuelFeedAvailable(1)), 0.001);
        assertTrue(context.bus().readFlag(Signals.fuelFeedAvailable(2)));
    }
}
