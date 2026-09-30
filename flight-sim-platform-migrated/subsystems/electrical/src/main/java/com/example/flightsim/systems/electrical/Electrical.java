package com.example.flightsim.systems.electrical;

import com.example.flightsim.core.SimComponent;
import com.example.flightsim.core.SimContext;
import com.example.flightsim.core.Signals;

/**
 * AC and DC electrical network with engine generators, APU generator, batteries and bus tie logic.
 */
public final class Electrical implements SimComponent {

    /** Engine 1 generator failure. */
    public static final String GEN_1_FAIL = "electrical.gen-1-fail";
    /** Engine 2 generator failure. */
    public static final String GEN_2_FAIL = "electrical.gen-2-fail";
    /** AC bus 1 fault. */
    public static final String AC_1_FAULT = "electrical.ac1-fault";
    /** AC bus 2 fault. */
    public static final String AC_2_FAULT = "electrical.ac2-fault";
    /** DC essential bus fault. */
    public static final String DC_ESS_FAULT = "electrical.dc-ess-fault";
    /** Battery degraded capacity. */
    public static final String BATTERY_DEGRADED = "electrical.battery-degraded";

    private static final double AC_VOLTS = 115.0;
    private static final double DC_VOLTS = 28.0;
    private static final double GENERATOR_ON_N2_PCT = 55.0;
    private static final double CHARGE_RATE_PCT_PER_SECOND = 0.015;
    private static final double DISCHARGE_RATE_PCT_PER_SECOND = 0.05;

    private double batteryChargePct = 92.0;

    @Override
    public String name() {
        return "electrical";
    }

    @Override
    public void initialise(SimContext context) {
        context.malfunctions().register(GEN_1_FAIL, "Engine 1 integrated drive generator failed", 24);
        context.malfunctions().register(GEN_2_FAIL, "Engine 2 integrated drive generator failed", 24);
        context.malfunctions().register(AC_1_FAULT, "AC bus 1 fault", 24);
        context.malfunctions().register(AC_2_FAULT, "AC bus 2 fault", 24);
        context.malfunctions().register(DC_ESS_FAULT, "DC essential bus fault", 24);
        context.malfunctions().register(BATTERY_DEGRADED, "Battery degraded capacity", 24);
    }

    @Override
    public void step(SimContext context, double dtSeconds) {
        boolean gen1 = generatorAvailable(context, 1, GEN_1_FAIL);
        boolean gen2 = generatorAvailable(context, 2, GEN_2_FAIL);
        boolean apu = context.bus().readFlag(ElectricalSignals.APU_GENERATOR_ON);
        boolean busTie = !context.bus().has(ElectricalSignals.BUS_TIE_AUTO) || context.bus().readFlag(ElectricalSignals.BUS_TIE_AUTO);
        boolean anyAcSource = gen1 || gen2 || apu;
        boolean ac1Powered = !context.malfunctions().isActive(AC_1_FAULT) && (gen1 || apu || (busTie && gen2));
        boolean ac2Powered = !context.malfunctions().isActive(AC_2_FAULT) && (gen2 || apu || (busTie && gen1));
        boolean batteryUsable = batteryChargePct > 4.0;
        boolean dc1Powered = ac1Powered || (batteryUsable && !anyAcSource);
        boolean dc2Powered = ac2Powered || (batteryUsable && !anyAcSource);
        boolean dcEssPowered = !context.malfunctions().isActive(DC_ESS_FAULT) && (dc1Powered || dc2Powered || batteryUsable);
        boolean loadShed = !anyAcSource && batteryUsable;

        updateBattery(anyAcSource, dcEssPowered, dtSeconds, context.malfunctions().isActive(BATTERY_DEGRADED));
        publish(context, ac1Powered, ac2Powered, dc1Powered, dc2Powered, dcEssPowered, batteryUsable, loadShed, batteryChargePct);
    }

    @Override
    public void reset() {
        batteryChargePct = 92.0;
    }

    private static boolean generatorAvailable(SimContext context, int engineIndex, String malfunction) {
        double n2 = context.bus().read(Signals.engine(engineIndex, "n2.pct"), 0.0);
        return n2 >= GENERATOR_ON_N2_PCT && !context.malfunctions().isActive(malfunction);
    }

    private void updateBattery(boolean anyAcSource, boolean dcEssPowered, double dtSeconds, boolean degraded) {
        double maximumCharge = degraded ? 55.0 : 100.0;
        if (anyAcSource) {
            batteryChargePct = Math.min(maximumCharge, batteryChargePct + CHARGE_RATE_PCT_PER_SECOND * dtSeconds);
        } else if (dcEssPowered) {
            batteryChargePct = Math.max(0.0, batteryChargePct - DISCHARGE_RATE_PCT_PER_SECOND * dtSeconds);
        }
    }

    private static void publish(SimContext context, boolean ac1Powered, boolean ac2Powered, boolean dc1Powered,
            boolean dc2Powered, boolean dcEssPowered, boolean batteryUsable, boolean loadShed, double batteryChargePct) {
        context.bus().publish(Signals.busVolts("ac1"), ac1Powered ? AC_VOLTS : 0.0);
        context.bus().publish(Signals.busVolts("ac2"), ac2Powered ? AC_VOLTS : 0.0);
        context.bus().publish(Signals.busVolts("dc1"), dc1Powered ? DC_VOLTS : 0.0);
        context.bus().publish(Signals.busVolts("dc2"), dc2Powered ? DC_VOLTS : 0.0);
        context.bus().publish(Signals.busVolts("dc-ess"), dcEssPowered ? DC_VOLTS : 0.0);
        context.bus().publish(Signals.busVolts("batt"), batteryUsable ? 24.0 : 0.0);
        context.bus().publish(ElectricalSignals.LOAD_SHED, loadShed);
        context.bus().publish(ElectricalSignals.BATTERY_CHARGE_PCT, batteryChargePct);
    }
}
