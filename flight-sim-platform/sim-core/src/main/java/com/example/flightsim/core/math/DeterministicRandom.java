package com.example.flightsim.core.math;

import java.nio.charset.StandardCharsets;
import java.util.SplittableRandom;

/**
 * Seeded random source for a training session.
 *
 * <p>Every random draw in the simulation — turbulence, sensor noise, failure timing — must come
 * from here so that a session replays exactly from its seed. Components that need their own
 * stream call {@link #fork(String)} with a stable name; forking does not disturb the parent
 * stream, so adding a new component does not change what the existing ones draw.
 */
public final class DeterministicRandom {

    private final long seed;
    private final SplittableRandom random;
    private double spareGaussian;
    private boolean hasSpare;

    /**
     * Creates a source.
     *
     * @param seed session seed
     */
    public DeterministicRandom(long seed) {
        this.seed = seed;
        this.random = new SplittableRandom(seed);
    }

    /**
     * Returns the seed this source was created with.
     *
     * @return seed
     */
    public long seed() {
        return seed;
    }

    /**
     * Uniform double in [0, 1).
     *
     * @return next value
     */
    public double nextDouble() {
        return random.nextDouble();
    }

    /**
     * Uniform double in [min, max).
     *
     * @param min lower bound, inclusive
     * @param max upper bound, exclusive
     * @return next value
     */
    public double nextDouble(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    /**
     * Uniform int in [0, bound).
     *
     * @param bound upper bound, exclusive
     * @return next value
     */
    public int nextInt(int bound) {
        return random.nextInt(bound);
    }

    /**
     * Standard normal value, by the Marsaglia polar method.
     *
     * @return next value, mean 0 and standard deviation 1
     */
    public double nextGaussian() {
        if (hasSpare) {
            hasSpare = false;
            return spareGaussian;
        }
        double u;
        double v;
        double s;
        do {
            u = 2.0 * random.nextDouble() - 1.0;
            v = 2.0 * random.nextDouble() - 1.0;
            s = u * u + v * v;
        } while (s >= 1.0 || s == 0.0);
        double multiplier = Math.sqrt(-2.0 * Math.log(s) / s);
        spareGaussian = v * multiplier;
        hasSpare = true;
        return u * multiplier;
    }

    /**
     * Creates an independent stream derived from this source's seed and a stable name.
     *
     * @param streamName stable name, usually the component name
     * @return independent source
     */
    public DeterministicRandom fork(String streamName) {
        long h = seed;
        for (byte b : streamName.getBytes(StandardCharsets.UTF_8)) {
            h = (h ^ b) * 0x100000001B3L;
        }
        return new DeterministicRandom(h);
    }
}
