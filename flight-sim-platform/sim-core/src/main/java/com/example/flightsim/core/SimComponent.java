package com.example.flightsim.core;

/**
 * A model that is stepped by the {@link SimExecutive} at a fixed rate.
 *
 * <p>Components communicate only through the {@link SimBus}; they never hold references to
 * each other. That keeps the step order explicit and lets the executive run a component at a
 * lower rate than the frame rate without the other components noticing.
 */
public interface SimComponent {

    /** Short, stable identifier used in logs, recordings and malfunction ids. */
    String name();

    /**
     * Called once before the first step, and again after the executive is reset.
     *
     * @param context shared simulation context
     */
    default void initialise(SimContext context) {
        // most components have nothing to initialise
    }

    /**
     * Advances the model by {@code dtSeconds}.
     *
     * @param context   shared simulation context
     * @param dtSeconds step length in seconds, always positive
     */
    void step(SimContext context, double dtSeconds);

    /** Returns the component to its power-on state. */
    default void reset() {
        // stateless by default
    }
}
