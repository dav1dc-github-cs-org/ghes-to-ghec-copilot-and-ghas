package com.example.flightsim.systems.fuel;

/** Signal names owned by the fuel subsystem. */
public final class FuelSignals {

    /** Centre tank pump command. */
    public static final String CENTRE_PUMP_ON = "fuel.centre-pump.on";
    /** Left tank pump command. */
    public static final String LEFT_PUMP_ON = "fuel.left-pump.on";
    /** Right tank pump command. */
    public static final String RIGHT_PUMP_ON = "fuel.right-pump.on";
    /** Crossfeed valve command. */
    public static final String CROSSFEED_OPEN = "fuel.crossfeed.open";
    /** Lateral fuel imbalance. */
    public static final String IMBALANCE_KG = "fuel.imbalance.kg";
    /** Fuel centre-of-gravity shift estimate. */
    public static final String CG_SHIFT_M = "fuel.cg-shift.m";

    private FuelSignals() {
    }
}
