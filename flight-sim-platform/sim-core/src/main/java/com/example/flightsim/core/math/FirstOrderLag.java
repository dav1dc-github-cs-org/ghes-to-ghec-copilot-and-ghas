package com.example.flightsim.core.math;

/**
 * First-order lag, {@code tau · dy/dt + y = u}, integrated exactly for a constant input over the
 * step so it stays stable at any step length. Used for spool-up, pressure build-up and sensor lag.
 */
public final class FirstOrderLag {

    private final double timeConstantSeconds;
    private double output;

    /**
     * Creates a lag.
     *
     * @param timeConstantSeconds time constant; zero means no lag
     * @param initialOutput       starting output
     */
    public FirstOrderLag(double timeConstantSeconds, double initialOutput) {
        if (timeConstantSeconds < 0.0) {
            throw new IllegalArgumentException("Time constant must not be negative: " + timeConstantSeconds);
        }
        this.timeConstantSeconds = timeConstantSeconds;
        this.output = initialOutput;
    }

    /**
     * Advances the lag.
     *
     * @param input     input held over the step
     * @param dtSeconds step length
     * @return new output
     */
    public double update(double input, double dtSeconds) {
        if (timeConstantSeconds == 0.0) {
            output = input;
        } else {
            double alpha = 1.0 - Math.exp(-dtSeconds / timeConstantSeconds);
            output += alpha * (input - output);
        }
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
     * Forces the output, for example after a scenario reposition.
     *
     * @param value new output
     */
    public void reset(double value) {
        output = value;
    }
}
