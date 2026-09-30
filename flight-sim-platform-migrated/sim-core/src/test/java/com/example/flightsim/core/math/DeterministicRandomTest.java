package com.example.flightsim.core.math;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DeterministicRandomTest {

    @Test
    void sameSeedGivesSameSequence() {
        DeterministicRandom a = new DeterministicRandom(42L);
        DeterministicRandom b = new DeterministicRandom(42L);
        for (int i = 0; i < 1000; i++) {
            assertEquals(a.nextGaussian(), b.nextGaussian());
        }
    }

    @Test
    void forkedStreamsAreStableAndIndependent() {
        DeterministicRandom parent = new DeterministicRandom(7L);
        double first = parent.fork("turbulence").nextDouble();
        double again = new DeterministicRandom(7L).fork("turbulence").nextDouble();
        double other = new DeterministicRandom(7L).fork("sensor-noise").nextDouble();
        assertEquals(first, again);
        assertNotEquals(first, other);
    }

    @Test
    void gaussianHasRoughlyUnitVariance() {
        DeterministicRandom random = new DeterministicRandom(2024L);
        int n = 20_000;
        double sum = 0.0;
        double sumSquares = 0.0;
        for (int i = 0; i < n; i++) {
            double x = random.nextGaussian();
            sum += x;
            sumSquares += x * x;
        }
        double mean = sum / n;
        double variance = sumSquares / n - mean * mean;
        assertTrue(Math.abs(mean) < 0.03, "mean " + mean);
        assertTrue(Math.abs(variance - 1.0) < 0.05, "variance " + variance);
    }
}
