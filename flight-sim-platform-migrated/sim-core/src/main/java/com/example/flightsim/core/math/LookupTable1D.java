package com.example.flightsim.core.math;

import java.util.Arrays;

/**
 * One-dimensional lookup table with linear interpolation.
 *
 * <p>Outside the breakpoints the table clamps to the end values rather than extrapolating;
 * aerodynamic and engine data is only valid inside the range it was measured over.
 */
public final class LookupTable1D {

    private final double[] breakpoints;
    private final double[] values;

    /**
     * Creates a table.
     *
     * @param breakpoints strictly increasing breakpoints
     * @param values      value at each breakpoint
     */
    public LookupTable1D(double[] breakpoints, double[] values) {
        if (breakpoints.length < 2 || breakpoints.length != values.length) {
            throw new IllegalArgumentException("Need at least two breakpoints and one value per breakpoint");
        }
        for (int i = 1; i < breakpoints.length; i++) {
            if (!(breakpoints[i] > breakpoints[i - 1])) {
                throw new IllegalArgumentException("Breakpoints must be strictly increasing at index " + i);
            }
        }
        this.breakpoints = Arrays.copyOf(breakpoints, breakpoints.length);
        this.values = Arrays.copyOf(values, values.length);
    }

    /**
     * Interpolates the table.
     *
     * @param x input
     * @return interpolated value, clamped at the ends
     */
    public double lookup(double x) {
        int last = breakpoints.length - 1;
        if (x <= breakpoints[0]) {
            return values[0];
        }
        if (x >= breakpoints[last]) {
            return values[last];
        }
        int i = segment(breakpoints, x);
        double fraction = (x - breakpoints[i]) / (breakpoints[i + 1] - breakpoints[i]);
        return values[i] + fraction * (values[i + 1] - values[i]);
    }

    /**
     * Returns the lowest breakpoint.
     *
     * @return minimum input covered by the data
     */
    public double minimum() {
        return breakpoints[0];
    }

    /**
     * Returns the highest breakpoint.
     *
     * @return maximum input covered by the data
     */
    public double maximum() {
        return breakpoints[breakpoints.length - 1];
    }

    static int segment(double[] axis, double x) {
        int index = Arrays.binarySearch(axis, x);
        if (index >= 0) {
            return Math.min(index, axis.length - 2);
        }
        int insertion = -index - 1;
        return Math.max(0, Math.min(insertion - 1, axis.length - 2));
    }
}
