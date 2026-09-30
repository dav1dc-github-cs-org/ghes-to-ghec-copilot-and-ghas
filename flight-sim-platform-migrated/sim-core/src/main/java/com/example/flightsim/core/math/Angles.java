package com.example.flightsim.core.math;

/** Angle helpers. All angles are radians. */
public final class Angles {

    private static final double TWO_PI = 2.0 * Math.PI;

    private Angles() {
    }

    /**
     * Wraps an angle into the half-open interval (-&pi;, &pi;].
     *
     * @param radians angle
     * @return wrapped angle
     */
    public static double wrapPi(double radians) {
        double wrapped = radians % TWO_PI;
        if (wrapped <= -Math.PI) {
            wrapped += TWO_PI;
        } else if (wrapped > Math.PI) {
            wrapped -= TWO_PI;
        }
        return wrapped;
    }

    /**
     * Wraps an angle into [0, 2&pi;), for headings and bearings.
     *
     * @param radians angle
     * @return wrapped angle
     */
    public static double wrapTwoPi(double radians) {
        double wrapped = radians % TWO_PI;
        return wrapped < 0.0 ? wrapped + TWO_PI : wrapped;
    }

    /**
     * Shortest signed difference {@code to - from}, in (-&pi;, &pi;]. Use this for heading errors
     * so a turn from 350&deg; to 010&deg; is +20&deg; rather than -340&deg;.
     *
     * @param from starting angle
     * @param to   target angle
     * @return signed difference
     */
    public static double difference(double from, double to) {
        return wrapPi(to - from);
    }
}
