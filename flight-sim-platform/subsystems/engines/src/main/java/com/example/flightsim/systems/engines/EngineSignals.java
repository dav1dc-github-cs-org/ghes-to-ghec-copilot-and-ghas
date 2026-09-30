package com.example.flightsim.systems.engines;

/** Module-private signal names for the engine subsystem. */
public final class EngineSignals {

    /** Engine start switch command prefix; indexed with the shared signal helper. */
    public static final String START_SWITCH = "start-switch";
    /** Engine fuel lever command prefix; indexed with the shared signal helper. */
    public static final String FUEL_LEVER = "fuel-lever";

    private EngineSignals() {
    }
}
