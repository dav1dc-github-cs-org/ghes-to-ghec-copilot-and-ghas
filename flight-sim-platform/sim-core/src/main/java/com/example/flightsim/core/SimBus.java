package com.example.flightsim.core;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Shared store of named simulation signals.
 *
 * <p>Every value is a {@code double} in SI units (metres, seconds, kilograms, radians, pascals,
 * kelvin) unless the signal name says otherwise, for example {@code eng.1.n1.pct}. Booleans are
 * published as {@code 1.0} or {@code 0.0}. Signal names are defined in {@link Signals}.
 *
 * <p>The simulation thread publishes; the instructor station and the data recorder read from
 * other threads, which is why the backing map is concurrent.
 */
public final class SimBus {

    private final Map<String, Double> values = new ConcurrentHashMap<>();

    /**
     * Publishes a value, replacing any previous value for the signal.
     *
     * @param signal signal name
     * @param value  value in SI units
     * @throws IllegalArgumentException if the value is NaN, which always indicates a model bug
     */
    public void publish(String signal, double value) {
        if (Double.isNaN(value)) {
            throw new IllegalArgumentException("NaN published on " + signal);
        }
        values.put(signal, value);
    }

    /**
     * Publishes a discrete as {@code 1.0} (true) or {@code 0.0} (false).
     *
     * @param signal signal name
     * @param value  discrete value
     */
    public void publish(String signal, boolean value) {
        publish(signal, value ? 1.0 : 0.0);
    }

    /**
     * Reads a signal that must already have been published.
     *
     * @param signal signal name
     * @return the latest value
     * @throws IllegalStateException if nothing has been published on the signal yet
     */
    public double read(String signal) {
        Double value = values.get(signal);
        if (value == null) {
            throw new IllegalStateException("No value published for signal " + signal);
        }
        return value;
    }

    /**
     * Reads a signal, or returns {@code fallback} if nothing has been published yet.
     *
     * @param signal   signal name
     * @param fallback value to use when the signal is absent
     * @return the latest value, or the fallback
     */
    public double read(String signal, double fallback) {
        return values.getOrDefault(signal, fallback);
    }

    /**
     * Reads a discrete. Absent signals read as false.
     *
     * @param signal signal name
     * @return true if the latest value is at least 0.5
     */
    public boolean readFlag(String signal) {
        return read(signal, 0.0) >= 0.5;
    }

    /**
     * Tells whether anything has been published on a signal.
     *
     * @param signal signal name
     * @return true if the signal has a value
     */
    public boolean has(String signal) {
        return values.containsKey(signal);
    }

    /**
     * Returns a sorted, read-only copy of every signal, for recording and diagnostics.
     *
     * @return snapshot of all signals
     */
    public Map<String, Double> snapshot() {
        return Collections.unmodifiableMap(new TreeMap<>(values));
    }

    /** Removes every signal. Used when the executive is reset. */
    public void clear() {
        values.clear();
    }
}
