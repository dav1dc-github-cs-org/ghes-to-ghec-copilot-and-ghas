package com.example.flightsim.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Malfunctions the instructor can insert during a session.
 *
 * <p>Each subsystem registers the malfunctions it models at start-up and polls
 * {@link #isActive(String)} while stepping. Ids are {@code <component>.<failure>}, for example
 * {@code hydraulics.green.edp-fail}. The ATA chapter groups malfunctions on the instructor station
 * the way the aircraft maintenance manual does.
 */
public final class MalfunctionRegistry {

    private final Map<String, Malfunction> known = new ConcurrentHashMap<>();
    private final Set<String> active = ConcurrentHashMap.newKeySet();

    /**
     * Makes a malfunction available to the instructor.
     *
     * @param id          unique id, {@code <component>.<failure>}
     * @param description text shown on the instructor station
     * @param ataChapter  ATA 100 chapter, for example 29 for hydraulic power
     */
    public void register(String id, String description, int ataChapter) {
        Malfunction previous = known.putIfAbsent(id, new Malfunction(id, description, ataChapter));
        if (previous != null && !previous.description().equals(description)) {
            throw new IllegalStateException("Malfunction id registered twice with different meanings: " + id);
        }
    }

    /**
     * Returns every registered malfunction, sorted by ATA chapter and then id.
     *
     * @return registered malfunctions
     */
    public Collection<Malfunction> available() {
        List<Malfunction> list = new ArrayList<>(known.values());
        list.sort((a, b) -> a.ataChapter() != b.ataChapter()
                ? Integer.compare(a.ataChapter(), b.ataChapter())
                : a.id().compareTo(b.id()));
        return Collections.unmodifiableList(list);
    }

    /**
     * Inserts a malfunction.
     *
     * @param id malfunction id
     * @throws IllegalArgumentException if the id is not registered
     */
    public void activate(String id) {
        if (!known.containsKey(id)) {
            throw new IllegalArgumentException("Unknown malfunction: " + id);
        }
        active.add(id);
    }

    /**
     * Removes a malfunction. Removing an inactive malfunction does nothing.
     *
     * @param id malfunction id
     */
    public void clear(String id) {
        active.remove(id);
    }

    /** Removes every active malfunction. */
    public void clearAll() {
        active.clear();
    }

    /**
     * Tells whether a malfunction is currently inserted.
     *
     * @param id malfunction id
     * @return true if active
     */
    public boolean isActive(String id) {
        return active.contains(id);
    }

    /**
     * Returns the ids of the active malfunctions.
     *
     * @return active ids, read-only
     */
    public Set<String> activeIds() {
        return Collections.unmodifiableSet(active);
    }

    /**
     * A malfunction the instructor can insert.
     *
     * @param id          unique id
     * @param description instructor-facing description
     * @param ataChapter  ATA 100 chapter
     */
    public record Malfunction(String id, String description, int ataChapter) {
    }
}
