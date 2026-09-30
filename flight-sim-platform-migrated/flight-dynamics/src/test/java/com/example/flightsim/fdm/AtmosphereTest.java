package com.example.flightsim.fdm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for the ISA atmosphere model.
 */
final class AtmosphereTest {

    @Test
    void seaLevelMatchesIsaReference() {
        Atmosphere.Sample sample = Atmosphere.at(0.0);

        assertEquals(288.15, sample.temperatureK(), 0.001);
        assertEquals(101325.0, sample.pressurePa(), 0.5);
        assertEquals(1.225, sample.densityKgPerM3(), 0.0005);
        assertEquals(340.29, sample.speedOfSoundMps(), 0.02);
    }

    @Test
    void tropopauseMatchesIsaReference() {
        Atmosphere.Sample sample = Atmosphere.at(11000.0);

        assertEquals(216.65, sample.temperatureK(), 0.001);
        assertEquals(22632.0, sample.pressurePa(), 3.0);
        assertEquals(0.3639, sample.densityKgPerM3(), 0.0005);
    }
}
