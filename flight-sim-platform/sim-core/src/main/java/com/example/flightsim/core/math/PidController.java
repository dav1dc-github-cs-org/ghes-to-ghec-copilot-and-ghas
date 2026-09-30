package com.example.flightsim.core.math;

/**
 * PID controller with output limits and conditional-integration anti-windup.
 *
 * <p>The derivative acts on the measurement rather than the error, so a step in the setpoint does
 * not kick the output.
 */
public final class PidController {

    private final double kp;
    private final double ki;
    private final double kd;
    private final double outputMin;
    private final double outputMax;
    private double integral;
    private double previousMeasurement;
    private boolean hasPrevious;

    /**
     * Creates a controller.
     *
     * @param kp        proportional gain
     * @param ki        integral gain, per second
     * @param kd        derivative gain, seconds
     * @param outputMin lowest output
     * @param outputMax highest output
     */
    public PidController(double kp, double ki, double kd, double outputMin, double outputMax) {
        if (outputMin >= outputMax) {
            throw new IllegalArgumentException("outputMin must be below outputMax");
        }
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
        this.outputMin = outputMin;
        this.outputMax = outputMax;
    }

    /**
     * Computes the next output.
     *
     * @param setpoint    desired value
     * @param measurement measured value
     * @param dtSeconds   step length, positive
     * @return controller output, within the limits
     */
    public double update(double setpoint, double measurement, double dtSeconds) {
        double error = setpoint - measurement;
        double derivative = hasPrevious ? -(measurement - previousMeasurement) / dtSeconds : 0.0;
        previousMeasurement = measurement;
        hasPrevious = true;

        double unclamped = kp * error + ki * integral + kd * derivative;
        boolean saturatedHigh = unclamped >= outputMax && error > 0.0;
        boolean saturatedLow = unclamped <= outputMin && error < 0.0;
        if (!saturatedHigh && !saturatedLow) {
            integral += error * dtSeconds;
        }
        double output = kp * error + ki * integral + kd * derivative;
        return Math.max(outputMin, Math.min(outputMax, output));
    }

    /** Clears the integrator and derivative history, for example when a mode engages. */
    public void reset() {
        integral = 0.0;
        hasPrevious = false;
    }
}
