package com.example.flightsim.core;

/**
 * Simulation time. {@code frame} counts executive frames since the last reset and
 * {@code elapsedSeconds} is simulated (not wall-clock) time.
 *
 * @param frame          executive frame number, starting at zero
 * @param elapsedSeconds simulated seconds since the last reset
 */
public record SimTime(long frame, double elapsedSeconds) {

    /** Time at power-on. */
    public static final SimTime ZERO = new SimTime(0L, 0.0);

    /**
     * Returns the time one frame later.
     *
     * @param frameSeconds length of one frame in seconds
     * @return the advanced time
     */
    public SimTime advance(double frameSeconds) {
        return new SimTime(frame + 1, elapsedSeconds + frameSeconds);
    }
}
