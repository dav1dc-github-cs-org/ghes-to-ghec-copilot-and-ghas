package com.example.flightsim.systems.landinggear;

/** Signal names owned by the landing-gear subsystem. */
public final class LandingGearSignals {

    /** Alternate gravity extension command. */
    public static final String ALTERNATE_EXTENSION = "gear.alt-extension";
    /** Gear disagree warning. */
    public static final String DISAGREE_WARNING = "gear.disagree";
    /** Nose door position. */
    public static final String NOSE_DOOR_POSITION = "gear.nose.door-position";
    /** Left door position. */
    public static final String LEFT_DOOR_POSITION = "gear.left.door-position";
    /** Right door position. */
    public static final String RIGHT_DOOR_POSITION = "gear.right.door-position";

    private LandingGearSignals() {
    }
}
