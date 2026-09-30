package com.example.flightsim.core;

/**
 * Names of the signals that cross module boundaries.
 *
 * <p>Conventions: lower-case, dot-separated, ending in the unit. SI units throughout — metres,
 * metres per second, kilograms, radians, pascals, kelvin — except where the unit suffix says
 * otherwise ({@code .pct}, {@code .psi}, {@code .volts}, {@code .dots}). Indexed families such as
 * engines are built with the helper methods so every module spells them the same way.
 */
public final class Signals {

    // ---- Flight dynamics --------------------------------------------------------------------

    /** Pressure altitude above mean sea level. */
    public static final String ALTITUDE_M = "fdm.altitude.m";
    /** Height of the main gear above the runway or terrain. */
    public static final String HEIGHT_AGL_M = "fdm.height-agl.m";
    /** True airspeed. */
    public static final String TRUE_AIRSPEED_MPS = "fdm.tas.mps";
    /** Indicated airspeed, derived from dynamic pressure. */
    public static final String INDICATED_AIRSPEED_MPS = "fdm.ias.mps";
    /** Ground speed. */
    public static final String GROUND_SPEED_MPS = "fdm.gs.mps";
    /** Rate of climb, positive up. */
    public static final String VERTICAL_SPEED_MPS = "fdm.vs.mps";
    /** Mach number. */
    public static final String MACH = "fdm.mach";
    /** Pitch attitude, positive nose up. */
    public static final String PITCH_RAD = "fdm.pitch.rad";
    /** Bank angle, positive right wing down. */
    public static final String ROLL_RAD = "fdm.roll.rad";
    /** True heading, 0 to 2&pi;. */
    public static final String HEADING_RAD = "fdm.heading.rad";
    /** Angle of attack. */
    public static final String ALPHA_RAD = "fdm.alpha.rad";
    /** Pitch rate, positive nose up. */
    public static final String PITCH_RATE_RADPS = "fdm.q.radps";
    /** Roll rate, positive right wing down. */
    public static final String ROLL_RATE_RADPS = "fdm.p.radps";
    /** Latitude, positive north. */
    public static final String LATITUDE_RAD = "fdm.lat.rad";
    /** Longitude, positive east. */
    public static final String LONGITUDE_RAD = "fdm.lon.rad";
    /** Gross weight including fuel. */
    public static final String GROSS_MASS_KG = "fdm.gross-mass.kg";
    /** Discrete: any gear leg is compressed. */
    public static final String WEIGHT_ON_WHEELS = "fdm.wow";

    // ---- Environment ------------------------------------------------------------------------

    /** Ambient air density at the aircraft. */
    public static final String AIR_DENSITY_KGPM3 = "env.density.kgpm3";
    /** Outside air temperature. */
    public static final String OUTSIDE_AIR_TEMP_K = "env.oat.k";
    /** Ambient static pressure. */
    public static final String STATIC_PRESSURE_PA = "env.static-pressure.pa";
    /** Wind speed. */
    public static final String WIND_SPEED_MPS = "env.wind.speed.mps";
    /** Direction the wind blows from, true. */
    public static final String WIND_FROM_RAD = "env.wind.from.rad";

    // ---- Flight controls (normalised) -------------------------------------------------------

    /** Elevator command, -1 (full nose down) to +1 (full nose up). */
    public static final String ELEVATOR_CMD = "ctl.elevator";
    /** Aileron command, -1 (full left) to +1 (full right). */
    public static final String AILERON_CMD = "ctl.aileron";
    /** Rudder command, -1 (full left) to +1 (full right). */
    public static final String RUDDER_CMD = "ctl.rudder";
    /** Flap lever, 0 (up) to 1 (full). */
    public static final String FLAP_CMD = "ctl.flap";
    /** Discrete: gear lever selected down. */
    public static final String GEAR_LEVER_DOWN = "ctl.gear-lever.down";
    /** Discrete: pilot has taken control, autopilot must disengage. */
    public static final String AUTOPILOT_DISCONNECT = "ctl.ap-disconnect";

    // ---- Autoflight -------------------------------------------------------------------------

    /** Discrete: autopilot engaged. */
    public static final String AUTOPILOT_ENGAGED = "ap.engaged";

    // ---- Landing gear -----------------------------------------------------------------------

    /** Discrete: all legs down and locked. */
    public static final String GEAR_DOWN_LOCKED = "gear.down-locked";
    /** Discrete: any leg in transit. */
    public static final String GEAR_IN_TRANSIT = "gear.in-transit";

    // ---- Fuel ---------------------------------------------------------------------------------

    /** Total usable fuel on board. */
    public static final String FUEL_TOTAL_KG = "fuel.total.kg";

    private Signals() {
    }

    /**
     * Engine signal, for example {@code engine(1, "n1.pct")} gives {@code eng.1.n1.pct}.
     *
     * @param index    engine number, starting at 1
     * @param quantity quantity and unit suffix
     * @return signal name
     */
    public static String engine(int index, String quantity) {
        return indexed("eng", index, quantity);
    }

    /**
     * Throttle lever for an engine, 0 (idle) to 1 (maximum).
     *
     * @param index engine number, starting at 1
     * @return signal name
     */
    public static String throttle(int index) {
        return indexed("ctl.throttle", index, "pos");
    }

    /**
     * Hydraulic system pressure, for example {@code hydraulicPressurePsi("green")}.
     *
     * @param system hydraulic system name
     * @return signal name
     */
    public static String hydraulicPressurePsi(String system) {
        return "hyd." + system + ".pressure.psi";
    }

    /**
     * Electrical bus voltage, for example {@code busVolts("dc-ess")}.
     *
     * @param bus bus name
     * @return signal name
     */
    public static String busVolts(String bus) {
        return "elec." + bus + ".volts";
    }

    /**
     * Discrete: fuel is available at an engine's feed line.
     *
     * @param engineIndex engine number, starting at 1
     * @return signal name
     */
    public static String fuelFeedAvailable(int engineIndex) {
        return indexed("fuel.feed", engineIndex, "available");
    }

    /**
     * Builds {@code prefix.index.quantity}.
     *
     * @param prefix   family prefix, such as {@code eng}
     * @param index    member number, starting at 1
     * @param quantity quantity and unit suffix
     * @return signal name
     */
    public static String indexed(String prefix, int index, String quantity) {
        if (index < 1) {
            throw new IllegalArgumentException("Indexes start at 1: " + index);
        }
        return prefix + "." + index + "." + quantity;
    }
}
