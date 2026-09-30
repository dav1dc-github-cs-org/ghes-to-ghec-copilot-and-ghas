package com.example.flightsim.ios;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Simulator state saved by the instructor so an exercise can be repeated from the same point,
 * for example just before the engine failure.
 *
 * @param scenarioId         scenario that was running
 * @param simTimeSeconds     session time when the snapshot was taken
 * @param signals            every signal on the bus
 * @param activeMalfunctions malfunctions active at the time
 */
public record SimSnapshot(
        String scenarioId,
        double simTimeSeconds,
        Map<String, Double> signals,
        Set<String> activeMalfunctions) implements Serializable {

    private static final long serialVersionUID = 3L;

    /** Canonical constructor; takes sorted copies so snapshots compare and serialise predictably. */
    public SimSnapshot {
        signals = new TreeMap<>(signals);
        activeMalfunctions = new TreeSet<>(activeMalfunctions);
    }
}
