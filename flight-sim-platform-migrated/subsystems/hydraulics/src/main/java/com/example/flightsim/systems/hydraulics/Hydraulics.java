package com.example.flightsim.systems.hydraulics;

import com.example.flightsim.core.SimComponent;
import com.example.flightsim.core.SimContext;
import com.example.flightsim.core.Signals;
import com.example.flightsim.core.math.FirstOrderLag;

/**
 * Green, blue and yellow hydraulic systems with engine-driven pumps, an electric pump and PTU support.
 */
public final class Hydraulics implements SimComponent {

    /** Green engine-driven pump failure. */
    public static final String GREEN_PUMP_FAIL = "hydraulics.green-pump-fail";
    /** Blue electric pump failure. */
    public static final String BLUE_PUMP_FAIL = "hydraulics.blue-pump-fail";
    /** Yellow engine-driven pump failure. */
    public static final String YELLOW_PUMP_FAIL = "hydraulics.yellow-pump-fail";
    /** Green reservoir leak. */
    public static final String GREEN_LEAK = "hydraulics.green-leak";
    /** Blue reservoir leak. */
    public static final String BLUE_LEAK = "hydraulics.blue-leak";
    /** Yellow reservoir leak. */
    public static final String YELLOW_LEAK = "hydraulics.yellow-leak";
    /** Power transfer unit failure. */
    public static final String PTU_FAIL = "hydraulics.ptu-fail";

    private static final double NOMINAL_PRESSURE_PSI = 3_000.0;
    private static final double MIN_RESERVOIR_FOR_PRESSURE = 0.08;
    private static final double ENGINE_PUMP_ON_N2_PCT = 45.0;
    private static final double ELECTRIC_PUMP_MIN_VOLTS = 100.0;

    private final SystemState green = new SystemState();
    private final SystemState blue = new SystemState();
    private final SystemState yellow = new SystemState();

    @Override
    public String name() {
        return "hydraulics";
    }

    @Override
    public void initialise(SimContext context) {
        context.malfunctions().register(GREEN_PUMP_FAIL, "Green engine-driven pump failed", 29);
        context.malfunctions().register(BLUE_PUMP_FAIL, "Blue electric pump failed", 29);
        context.malfunctions().register(YELLOW_PUMP_FAIL, "Yellow engine-driven pump failed", 29);
        context.malfunctions().register(GREEN_LEAK, "Green hydraulic reservoir leak", 29);
        context.malfunctions().register(BLUE_LEAK, "Blue hydraulic reservoir leak", 29);
        context.malfunctions().register(YELLOW_LEAK, "Yellow hydraulic reservoir leak", 29);
        context.malfunctions().register(PTU_FAIL, "Power transfer unit failed", 29);
    }

    @Override
    public void step(SimContext context, double dtSeconds) {
        applyLeaks(context, dtSeconds);
        boolean greenPump = context.bus().read(Signals.engine(1, "n2.pct"), 0.0) > ENGINE_PUMP_ON_N2_PCT
                && !context.malfunctions().isActive(GREEN_PUMP_FAIL);
        boolean yellowPump = context.bus().read(Signals.engine(2, "n2.pct"), 0.0) > ENGINE_PUMP_ON_N2_PCT
                && !context.malfunctions().isActive(YELLOW_PUMP_FAIL);
        boolean bluePump = context.bus().read(Signals.busVolts("ac1"), 0.0) > ELECTRIC_PUMP_MIN_VOLTS
                && !context.malfunctions().isActive(BLUE_PUMP_FAIL);
        boolean ptu = ptuAvailable(context, greenPump, yellowPump);

        updateSystem(green, greenPump || (ptu && yellow.pressure.output() > 1_800.0), dtSeconds);
        updateSystem(yellow, yellowPump || (ptu && green.pressure.output() > 1_800.0), dtSeconds);
        updateSystem(blue, bluePump, dtSeconds);
        publish(context, ptu);
    }

    @Override
    public void reset() {
        green.reset();
        blue.reset();
        yellow.reset();
    }

    private void applyLeaks(SimContext context, double dtSeconds) {
        leak(context, green, GREEN_LEAK, dtSeconds);
        leak(context, blue, BLUE_LEAK, dtSeconds);
        leak(context, yellow, YELLOW_LEAK, dtSeconds);
    }

    private static void leak(SimContext context, SystemState system, String malfunction, double dtSeconds) {
        if (context.malfunctions().isActive(malfunction)) {
            system.reservoirQty = Math.max(0.0, system.reservoirQty - 0.035 * dtSeconds);
        }
    }

    private static boolean ptuAvailable(SimContext context, boolean greenPump, boolean yellowPump) {
        if (context.malfunctions().isActive(PTU_FAIL)) {
            return false;
        }
        return greenPump != yellowPump;
    }

    private static void updateSystem(SystemState system, boolean pumpAvailable, double dtSeconds) {
        boolean hasFluid = system.reservoirQty > MIN_RESERVOIR_FOR_PRESSURE;
        double target = pumpAvailable && hasFluid ? NOMINAL_PRESSURE_PSI : 0.0;
        double pressure = system.pressure.update(target, dtSeconds);
        if (!pumpAvailable) {
            system.accumulatorPsi = Math.max(0.0, system.accumulatorPsi - 90.0 * dtSeconds);
        }
        if (!pumpAvailable && pressure < 1_200.0) {
            system.pressure.reset(Math.max(pressure, system.accumulatorPsi));
        } else if (pumpAvailable) {
            system.accumulatorPsi = Math.min(NOMINAL_PRESSURE_PSI, system.accumulatorPsi + 120.0 * dtSeconds);
        }
    }

    private void publish(SimContext context, boolean ptu) {
        context.bus().publish(Signals.hydraulicPressurePsi("green"), green.pressure.output());
        context.bus().publish(Signals.hydraulicPressurePsi("blue"), blue.pressure.output());
        context.bus().publish(Signals.hydraulicPressurePsi("yellow"), yellow.pressure.output());
        context.bus().publish(HydraulicSignals.GREEN_RESERVOIR_QTY, green.reservoirQty);
        context.bus().publish(HydraulicSignals.BLUE_RESERVOIR_QTY, blue.reservoirQty);
        context.bus().publish(HydraulicSignals.YELLOW_RESERVOIR_QTY, yellow.reservoirQty);
        context.bus().publish(HydraulicSignals.PTU_ACTIVE, ptu);
    }

    private static final class SystemState {
        private final FirstOrderLag pressure = new FirstOrderLag(1.2, 0.0);
        private double reservoirQty = 1.0;
        private double accumulatorPsi = 1_200.0;

        private void reset() {
            pressure.reset(0.0);
            reservoirQty = 1.0;
            accumulatorPsi = 1_200.0;
        }
    }
}
