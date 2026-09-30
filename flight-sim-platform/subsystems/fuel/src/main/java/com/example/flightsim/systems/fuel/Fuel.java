package com.example.flightsim.systems.fuel;

import com.example.flightsim.core.SimComponent;
import com.example.flightsim.core.SimContext;
import com.example.flightsim.core.Signals;
import com.example.flightsim.core.math.DeterministicRandom;

/**
 * Three-tank fuel system with boost pumps, crossfeed and deterministic leak variation.
 */
public final class Fuel implements SimComponent {

    /** Left boost pump failure. */
    public static final String LEFT_PUMP_FAIL = "fuel.left-pump-fail";
    /** Centre boost pump failure. */
    public static final String CENTRE_PUMP_FAIL = "fuel.centre-pump-fail";
    /** Right boost pump failure. */
    public static final String RIGHT_PUMP_FAIL = "fuel.right-pump-fail";
    /** Left tank leak. */
    public static final String LEFT_LEAK = "fuel.left-leak";
    /** Centre tank leak. */
    public static final String CENTRE_LEAK = "fuel.centre-leak";
    /** Right tank leak. */
    public static final String RIGHT_LEAK = "fuel.right-leak";
    /** Crossfeed valve stuck closed. */
    public static final String CROSSFEED_STUCK = "fuel.crossfeed-stuck";

    private static final double LEFT_CAPACITY_KG = 2_700.0;
    private static final double CENTRE_CAPACITY_KG = 2_400.0;
    private static final double RIGHT_CAPACITY_KG = 2_700.0;
    private static final double DEFAULT_INITIAL_KG = 6_000.0;
    private static final double MIN_FEED_KG = 8.0;

    private final Tank left = new Tank("left", LEFT_CAPACITY_KG);
    private final Tank centre = new Tank("centre", CENTRE_CAPACITY_KG);
    private final Tank right = new Tank("right", RIGHT_CAPACITY_KG);
    private boolean loaded;

    @Override
    public String name() {
        return "fuel";
    }

    @Override
    public void initialise(SimContext context) {
        context.malfunctions().register(LEFT_PUMP_FAIL, "Left fuel boost pump failed", 28);
        context.malfunctions().register(CENTRE_PUMP_FAIL, "Centre fuel boost pump failed", 28);
        context.malfunctions().register(RIGHT_PUMP_FAIL, "Right fuel boost pump failed", 28);
        context.malfunctions().register(LEFT_LEAK, "Left wing tank leak", 28);
        context.malfunctions().register(CENTRE_LEAK, "Centre tank leak", 28);
        context.malfunctions().register(RIGHT_LEAK, "Right wing tank leak", 28);
        context.malfunctions().register(CROSSFEED_STUCK, "Crossfeed valve stuck closed", 28);
        if (!loaded) {
            loadFuel(context.config().getDouble("fuel.initial.kg", DEFAULT_INITIAL_KG));
            loaded = true;
        }
    }

    @Override
    public void step(SimContext context, double dtSeconds) {
        ensureLoaded(context);
        boolean crossfeed = context.bus().readFlag(FuelSignals.CROSSFEED_OPEN)
                && !context.malfunctions().isActive(CROSSFEED_STUCK);
        boolean leftPump = pumpOn(context, FuelSignals.LEFT_PUMP_ON, LEFT_PUMP_FAIL);
        boolean centrePump = pumpOn(context, FuelSignals.CENTRE_PUMP_ON, CENTRE_PUMP_FAIL);
        boolean rightPump = pumpOn(context, FuelSignals.RIGHT_PUMP_ON, RIGHT_PUMP_FAIL);

        double flow1 = context.bus().read(Signals.engine(1, "fuel-flow.kgps"), 0.0) * dtSeconds;
        double flow2 = context.bus().read(Signals.engine(2, "fuel-flow.kgps"), 0.0) * dtSeconds;
        consumeForEngine(flow1, left, leftPump, centrePump, crossfeed ? right : null);
        consumeForEngine(flow2, right, rightPump, centrePump, crossfeed ? left : null);
        applyLeak(context, left, LEFT_LEAK, dtSeconds);
        applyLeak(context, centre, CENTRE_LEAK, dtSeconds);
        applyLeak(context, right, RIGHT_LEAK, dtSeconds);

        publish(context, leftPump, centrePump, rightPump, crossfeed);
    }

    @Override
    public void reset() {
        left.quantityKg = 0.0;
        centre.quantityKg = 0.0;
        right.quantityKg = 0.0;
        loaded = false;
    }

    private void ensureLoaded(SimContext context) {
        if (!loaded) {
            loadFuel(context.config().getDouble("fuel.initial.kg", DEFAULT_INITIAL_KG));
            loaded = true;
        }
    }

    private void loadFuel(double initialKg) {
        double remaining = Math.max(0.0, initialKg);
        double wingEach = Math.min(Math.min(LEFT_CAPACITY_KG, RIGHT_CAPACITY_KG), remaining / 2.0);
        left.quantityKg = wingEach;
        right.quantityKg = wingEach;
        remaining -= 2.0 * wingEach;
        centre.quantityKg = Math.min(CENTRE_CAPACITY_KG, remaining);
        if (remaining > CENTRE_CAPACITY_KG) {
            double overflow = remaining - CENTRE_CAPACITY_KG;
            left.quantityKg = Math.min(LEFT_CAPACITY_KG, left.quantityKg + overflow / 2.0);
            right.quantityKg = Math.min(RIGHT_CAPACITY_KG, right.quantityKg + overflow / 2.0);
        }
    }

    private static boolean pumpOn(SimContext context, String signal, String malfunction) {
        boolean commanded = !context.bus().has(signal) || context.bus().readFlag(signal);
        return commanded && !context.malfunctions().isActive(malfunction);
    }

    private void consumeForEngine(double requestedKg, Tank primary, boolean primaryPump, boolean centrePump, Tank crossfeedTank) {
        double remaining = Math.max(0.0, requestedKg);
        if (centrePump && centre.quantityKg > 0.0) {
            remaining = centre.draw(remaining);
        }
        if (primaryPump && remaining > 0.0) {
            remaining = primary.draw(remaining);
        }
        if (crossfeedTank != null && remaining > 0.0) {
            crossfeedTank.draw(remaining);
        }
    }

    private static void applyLeak(SimContext context, Tank tank, String malfunction, double dtSeconds) {
        if (context.malfunctions().isActive(malfunction)) {
            DeterministicRandom random = context.random().fork(malfunction);
            double leakRate = 0.08 + 0.04 * random.nextDouble();
            tank.draw(leakRate * dtSeconds);
        }
    }

    private void publish(SimContext context, boolean leftPump, boolean centrePump, boolean rightPump, boolean crossfeed) {
        double total = left.quantityKg + centre.quantityKg + right.quantityKg;
        boolean leftFeed = left.quantityKg > MIN_FEED_KG && (leftPump || (crossfeed && rightPump) || centrePump);
        boolean rightFeed = right.quantityKg > MIN_FEED_KG && (rightPump || (crossfeed && leftPump) || centrePump);
        if (centre.quantityKg > MIN_FEED_KG && centrePump) {
            leftFeed = true;
            rightFeed = true;
        }
        context.bus().publish("fuel.left.kg", left.quantityKg);
        context.bus().publish("fuel.centre.kg", centre.quantityKg);
        context.bus().publish("fuel.right.kg", right.quantityKg);
        context.bus().publish(Signals.FUEL_TOTAL_KG, total);
        context.bus().publish(Signals.fuelFeedAvailable(1), leftFeed);
        context.bus().publish(Signals.fuelFeedAvailable(2), rightFeed);
        context.bus().publish(FuelSignals.IMBALANCE_KG, left.quantityKg - right.quantityKg);
        double cgShift = total == 0.0 ? 0.0 : (centre.quantityKg - 0.5 * (left.quantityKg + right.quantityKg)) / total;
        context.bus().publish(FuelSignals.CG_SHIFT_M, cgShift);
    }

    private static final class Tank {
        private final String name;
        private final double capacityKg;
        private double quantityKg;

        private Tank(String name, double capacityKg) {
            this.name = name;
            this.capacityKg = capacityKg;
        }

        private double draw(double requestedKg) {
            double taken = Math.min(quantityKg, requestedKg);
            quantityKg = Math.max(0.0, Math.min(capacityKg, quantityKg - taken));
            return requestedKg - taken;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
