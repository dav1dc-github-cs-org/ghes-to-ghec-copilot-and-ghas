package com.example.flightsim.core.units;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class UnitsTest {

    private static final double TOLERANCE = 1e-9;

    @Test
    void knotsRoundTrip() {
        assertEquals(0.514444444, Units.knotsToMps(1.0), 1e-9);
        assertEquals(250.0, Units.mpsToKnots(Units.knotsToMps(250.0)), TOLERANCE);
    }

    @Test
    void feetAndNauticalMiles() {
        assertEquals(3048.0, Units.feetToMetres(10_000.0), TOLERANCE);
        assertEquals(1852.0, Units.nauticalMilesToMetres(1.0), TOLERANCE);
        assertEquals(5.08, Units.fpmToMps(1000.0), 1e-9);
    }

    @Test
    void anglesAndTemperature() {
        assertEquals(Math.PI, Units.degreesToRadians(180.0), TOLERANCE);
        assertEquals(90.0, Units.radiansToDegrees(Math.PI / 2.0), TOLERANCE);
        assertEquals(288.15, Units.celsiusToKelvin(15.0), TOLERANCE);
    }

    @Test
    void pressure() {
        assertEquals(3000.0, Units.pascalsToPsi(Units.psiToPascals(3000.0)), 1e-9);
        assertEquals(101_325.0, Units.hectopascalsToPascals(1013.25), 1e-9);
    }
}
