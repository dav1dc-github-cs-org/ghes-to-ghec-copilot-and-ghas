package com.example.flightsim.core;

import com.example.flightsim.core.math.DeterministicRandom;

/**
 * Everything a {@link SimComponent} may touch while stepping: the signal bus, the malfunction
 * registry, the session's random source and the configuration.
 *
 * <p>Components must take randomness from {@link #random()} rather than {@code java.util.Random}
 * or {@code Math.random()}: a recorded session has to replay identically for the qualification
 * test guide, and that only works if every random draw comes from the seeded source.
 */
public final class SimContext {

    private final SimBus bus;
    private final MalfunctionRegistry malfunctions;
    private final DeterministicRandom random;
    private final SimConfig config;
    private SimTime time = SimTime.ZERO;

    /**
     * Creates a context.
     *
     * @param bus          signal bus
     * @param malfunctions malfunction registry
     * @param random       seeded random source for the session
     * @param config       configuration
     */
    public SimContext(SimBus bus, MalfunctionRegistry malfunctions, DeterministicRandom random, SimConfig config) {
        this.bus = bus;
        this.malfunctions = malfunctions;
        this.random = random;
        this.config = config;
    }

    /**
     * Creates a context with an empty bus, no malfunctions and an empty configuration.
     *
     * @param seed random seed for the session
     * @return new context
     */
    public static SimContext create(long seed) {
        return new SimContext(new SimBus(), new MalfunctionRegistry(), new DeterministicRandom(seed), SimConfig.empty());
    }

    /**
     * Returns the signal bus.
     *
     * @return bus
     */
    public SimBus bus() {
        return bus;
    }

    /**
     * Returns the malfunction registry.
     *
     * @return registry
     */
    public MalfunctionRegistry malfunctions() {
        return malfunctions;
    }

    /**
     * Returns the session's seeded random source.
     *
     * @return random source
     */
    public DeterministicRandom random() {
        return random;
    }

    /**
     * Returns the configuration.
     *
     * @return configuration
     */
    public SimConfig config() {
        return config;
    }

    /**
     * Returns the current simulation time.
     *
     * @return time
     */
    public SimTime time() {
        return time;
    }

    void advanceTime(double frameSeconds) {
        time = time.advance(frameSeconds);
    }

    void resetTime() {
        time = SimTime.ZERO;
    }
}
