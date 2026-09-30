package com.example.flightsim.systems.navigation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.flightsim.core.SimContext;
import com.example.flightsim.core.Signals;
import com.example.flightsim.core.units.Units;
import org.junit.jupiter.api.Test;

final class NavigationTest {

    @Test
    void haversineDistanceMatchesOneDegreeAtEquator() {
        double distance = Navigation.distanceMetres(0.0, 0.0, 0.0, Units.degreesToRadians(1.0));

        assertEquals(111_195.0, distance, 556.0);
    }

    @Test
    void tunedReceiverPublishesValidDmeAndBearing() {
        SimContext context = SimContext.create(71L);
        Navigation navigation = new Navigation();
        navigation.initialise(context);
        context.bus().publish(Signals.LATITUDE_RAD, Units.degreesToRadians(40.2));
        context.bus().publish(Signals.LONGITUDE_RAD, Units.degreesToRadians(-75.0));
        context.bus().publish(Signals.ALTITUDE_M, 2_000.0);
        context.bus().publish(Signals.HEADING_RAD, 0.0);
        context.bus().publish("nav.1.freq.mhz", 113.20);

        navigation.step(context, 0.1);

        assertTrue(context.bus().readFlag("nav.1.valid"));
        assertTrue(context.bus().read("nav.1.dme.nm") > 10.0);
        assertEquals(Math.PI, context.bus().read("nav.1.bearing.rad"), 0.05);
    }
}
