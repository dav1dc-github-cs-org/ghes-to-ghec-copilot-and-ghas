package com.example.flightsim.core.math;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class LookupTableTest {

    @Test
    void interpolatesBetweenBreakpoints() {
        LookupTable1D table = new LookupTable1D(new double[] {0.0, 10.0, 20.0}, new double[] {0.0, 1.0, 4.0});
        assertEquals(0.5, table.lookup(5.0), 1e-12);
        assertEquals(2.5, table.lookup(15.0), 1e-12);
        assertEquals(1.0, table.lookup(10.0), 1e-12);
    }

    @Test
    void clampsOutsideTheData() {
        LookupTable1D table = new LookupTable1D(new double[] {0.0, 10.0}, new double[] {2.0, 3.0});
        assertEquals(2.0, table.lookup(-100.0), 1e-12);
        assertEquals(3.0, table.lookup(100.0), 1e-12);
    }

    @Test
    void rejectsNonIncreasingBreakpoints() {
        assertThrows(IllegalArgumentException.class,
                () -> new LookupTable1D(new double[] {0.0, 0.0}, new double[] {1.0, 2.0}));
    }

    @Test
    void bilinearInterpolation() {
        LookupTable2D table = new LookupTable2D(
                new double[] {0.0, 1.0},
                new double[] {0.0, 1.0},
                new double[][] {{0.0, 1.0}, {2.0, 3.0}});
        assertEquals(1.5, table.lookup(0.5, 0.5), 1e-12);
        assertEquals(3.0, table.lookup(5.0, 5.0), 1e-12);
    }
}
