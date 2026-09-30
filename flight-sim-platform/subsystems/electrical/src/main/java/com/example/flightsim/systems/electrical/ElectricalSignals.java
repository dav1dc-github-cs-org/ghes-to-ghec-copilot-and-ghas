package com.example.flightsim.systems.electrical;

/** Signal names owned by the electrical subsystem. */
public final class ElectricalSignals {

    /** APU generator command. */
    public static final String APU_GENERATOR_ON = "elec.apu-gen.on";
    /** Automatic bus-tie enable command. */
    public static final String BUS_TIE_AUTO = "elec.bus-tie.auto";
    /** Battery state of charge signal. */
    public static final String BATTERY_CHARGE_PCT = "elec.battery.charge.pct";
    /** Load shedding active flag. */
    public static final String LOAD_SHED = "elec.load-shed";

    private ElectricalSignals() {
    }
}
