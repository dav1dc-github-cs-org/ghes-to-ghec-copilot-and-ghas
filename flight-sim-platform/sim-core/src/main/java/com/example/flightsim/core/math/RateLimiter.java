package com.example.flightsim.core.math;

/** Limits how fast a value may change, for actuators and gauge needles. */
public final class RateLimiter {

    private final double maxRatePerSecond;
    private double output;

    /**
     * Creates a limiter.
     *
     * @param maxRatePerSecond largest allowed change per second, positive
     * @param initialOutput    starting output
     */
    public RateLimiter(double maxRatePerSecond, double initialOutput) {
        if (maxRatePerSecond <= 0.0) {
            throw new IllegalArgumentException("Rate limit must be positive: " + maxRatePerSecond);
        }
        this.maxRatePerSecond = maxRatePerSecond;
        this.output = initialOutput;
    }

    /**
     * Moves the output towards the target, no faster than the rate limit.
     *
     * @param target    desired value
     * @param dtSeconds step length
     * @return new output
     */
    public double update(double target, double dtSeconds) {
        double maxStep = maxRatePerSecond * dtSeconds;
        double delta = Math.max(-maxStep, Math.min(maxStep, target - output));
        output += delta;
        return output;
    }

    /**
     * Returns the current output.
     *
     * @return output
     */
    public double output() {
        return output;
    }

    /**
     * Forces the output.
     *
     * @param value new output
     */
    public void reset(double value) {
        output = value;
    }
}
