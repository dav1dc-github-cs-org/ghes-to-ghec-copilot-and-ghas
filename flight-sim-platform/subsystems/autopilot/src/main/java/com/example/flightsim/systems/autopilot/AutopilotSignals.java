package com.example.flightsim.systems.autopilot;

/** Signal names owned by the autopilot subsystem. */
public final class AutopilotSignals {

    /** Autopilot engagement command. */
    public static final String ENGAGE_COMMAND = "ap.engage-cmd";
    /** Selected altitude in metres. */
    public static final String SELECTED_ALTITUDE_M = "ap.sel.altitude.m";
    /** Selected vertical speed in metres per second. */
    public static final String SELECTED_VERTICAL_SPEED_MPS = "ap.sel.vs.mps";
    /** Selected heading in radians. */
    public static final String SELECTED_HEADING_RAD = "ap.sel.heading.rad";
    /** Vertical mode numeric code. */
    public static final String VERTICAL_MODE = "ap.mode.vertical";
    /** Lateral mode numeric code. */
    public static final String LATERAL_MODE = "ap.mode.lateral";

    /** Pitch hold mode code. */
    public static final double MODE_PITCH_HOLD = 1.0;
    /** Altitude hold mode code. */
    public static final double MODE_ALTITUDE_HOLD = 2.0;
    /** Vertical-speed mode code. */
    public static final double MODE_VERTICAL_SPEED = 3.0;
    /** Wings-level mode code. */
    public static final double MODE_WINGS_LEVEL = 4.0;
    /** Heading hold mode code. */
    public static final double MODE_HEADING_HOLD = 5.0;

    private AutopilotSignals() {
    }
}
