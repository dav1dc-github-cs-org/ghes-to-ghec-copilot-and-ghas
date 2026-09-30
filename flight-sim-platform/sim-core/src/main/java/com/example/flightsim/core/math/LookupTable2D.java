package com.example.flightsim.core.math;

import java.util.Arrays;

/**
 * Two-dimensional lookup table with bilinear interpolation, clamped at the edges.
 *
 * <p>{@code values[r][c]} is the value at {@code rowBreakpoints[r]} and {@code columnBreakpoints[c]};
 * aerodynamic tables are conventionally rows of angle of attack and columns of Mach number or
 * flap setting.
 */
public final class LookupTable2D {

    private final double[] rows;
    private final double[] columns;
    private final double[][] values;

    /**
     * Creates a table.
     *
     * @param rowBreakpoints    strictly increasing row breakpoints
     * @param columnBreakpoints strictly increasing column breakpoints
     * @param values            values indexed [row][column]
     */
    public LookupTable2D(double[] rowBreakpoints, double[] columnBreakpoints, double[][] values) {
        requireIncreasing(rowBreakpoints, "row");
        requireIncreasing(columnBreakpoints, "column");
        if (values.length != rowBreakpoints.length) {
            throw new IllegalArgumentException("Expected " + rowBreakpoints.length + " rows but got " + values.length);
        }
        this.rows = Arrays.copyOf(rowBreakpoints, rowBreakpoints.length);
        this.columns = Arrays.copyOf(columnBreakpoints, columnBreakpoints.length);
        this.values = new double[values.length][];
        for (int r = 0; r < values.length; r++) {
            if (values[r].length != columnBreakpoints.length) {
                throw new IllegalArgumentException("Row " + r + " has " + values[r].length
                        + " values; expected " + columnBreakpoints.length);
            }
            this.values[r] = Arrays.copyOf(values[r], values[r].length);
        }
    }

    /**
     * Interpolates the table.
     *
     * @param row    row input
     * @param column column input
     * @return interpolated value
     */
    public double lookup(double row, double column) {
        double r = clamp(row, rows);
        double c = clamp(column, columns);
        int i = LookupTable1D.segment(rows, r);
        int j = LookupTable1D.segment(columns, c);
        double fr = (r - rows[i]) / (rows[i + 1] - rows[i]);
        double fc = (c - columns[j]) / (columns[j + 1] - columns[j]);
        double top = values[i][j] + fc * (values[i][j + 1] - values[i][j]);
        double bottom = values[i + 1][j] + fc * (values[i + 1][j + 1] - values[i + 1][j]);
        return top + fr * (bottom - top);
    }

    private static double clamp(double x, double[] axis) {
        return Math.max(axis[0], Math.min(axis[axis.length - 1], x));
    }

    private static void requireIncreasing(double[] axis, String name) {
        if (axis.length < 2) {
            throw new IllegalArgumentException("Need at least two " + name + " breakpoints");
        }
        for (int i = 1; i < axis.length; i++) {
            if (!(axis[i] > axis[i - 1])) {
                throw new IllegalArgumentException(name + " breakpoints must be strictly increasing at index " + i);
            }
        }
    }
}
