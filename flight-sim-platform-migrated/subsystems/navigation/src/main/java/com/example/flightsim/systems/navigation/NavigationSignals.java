package com.example.flightsim.systems.navigation;

/** Signal names owned by the navigation subsystem. */
public final class NavigationSignals {

    /** Selected course suffix in radians. */
    public static final String COURSE_RAD = "course.rad";
    /** Course deviation suffix in dots. */
    public static final String COURSE_DEVIATION_DOTS = "course-dev.dots";
    /** Glideslope deviation suffix in dots. */
    public static final String GLIDESLOPE_DEVIATION_DOTS = "glideslope-dev.dots";
    /** DME distance suffix in nautical miles. */
    public static final String DME_NM = "dme.nm";
    /** Bearing suffix in radians. */
    public static final String BEARING_RAD = "bearing.rad";
    /** Receiver validity suffix. */
    public static final String VALID = "valid";

    private NavigationSignals() {
    }
}
