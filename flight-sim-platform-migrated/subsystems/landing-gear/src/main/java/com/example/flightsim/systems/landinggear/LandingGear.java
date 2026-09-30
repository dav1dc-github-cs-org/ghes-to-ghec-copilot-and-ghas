package com.example.flightsim.systems.landinggear;

import com.example.flightsim.core.SimComponent;
import com.example.flightsim.core.SimContext;
import com.example.flightsim.core.Signals;
import com.example.flightsim.core.math.RateLimiter;

/**
 * Landing gear sequencing for nose, left and right main legs with hydraulic and gravity extension.
 */
public final class LandingGear implements SimComponent {

    /** Nose leg stuck up malfunction. */
    public static final String NOSE_STUCK_UP = "landing-gear.nose-stuck-up";
    /** Left leg stuck up malfunction. */
    public static final String LEFT_STUCK_UP = "landing-gear.left-stuck-up";
    /** Right leg stuck up malfunction. */
    public static final String RIGHT_STUCK_UP = "landing-gear.right-stuck-up";
    /** Uplock jam malfunction. */
    public static final String UPLOCK_JAM = "landing-gear.uplock-jam";
    /** Gear disagree indication failure. */
    public static final String DISAGREE_INDICATION_FAIL = "landing-gear.disagree-indication-fail";

    private static final double EXTEND_RATE = 1.0 / 10.0;
    private static final double RETRACT_RATE = 1.0 / 8.0;
    private static final double GRAVITY_EXTEND_RATE = 1.0 / 14.0;
    private static final double HYDRAULIC_MIN_PSI = 1_800.0;
    private static final double LOCK_TOLERANCE = 0.985;

    private final Leg nose = new Leg("nose");
    private final Leg left = new Leg("left");
    private final Leg right = new Leg("right");

    @Override
    public String name() {
        return "landing-gear";
    }

    @Override
    public void initialise(SimContext context) {
        context.malfunctions().register(NOSE_STUCK_UP, "Nose gear leg stuck up", 32);
        context.malfunctions().register(LEFT_STUCK_UP, "Left main gear leg stuck up", 32);
        context.malfunctions().register(RIGHT_STUCK_UP, "Right main gear leg stuck up", 32);
        context.malfunctions().register(UPLOCK_JAM, "Landing gear uplock jam", 32);
        context.malfunctions().register(DISAGREE_INDICATION_FAIL, "Gear disagree indication failed", 32);
    }

    @Override
    public void step(SimContext context, double dtSeconds) {
        boolean leverDown = context.bus().readFlag(Signals.GEAR_LEVER_DOWN);
        boolean alternate = context.bus().readFlag(LandingGearSignals.ALTERNATE_EXTENSION);
        boolean hydraulics = context.bus().read(Signals.hydraulicPressurePsi("green"), 0.0) >= HYDRAULIC_MIN_PSI;
        double rate = extensionRate(leverDown, alternate, hydraulics);
        stepLeg(context, nose, NOSE_STUCK_UP, leverDown, rate, dtSeconds);
        stepLeg(context, left, LEFT_STUCK_UP, leverDown, rate, dtSeconds);
        stepLeg(context, right, RIGHT_STUCK_UP, leverDown, rate, dtSeconds);
        publish(context, leverDown);
    }

    @Override
    public void reset() {
        nose.reset();
        left.reset();
        right.reset();
    }

    private static double extensionRate(boolean leverDown, boolean alternate, boolean hydraulics) {
        if (leverDown && hydraulics) {
            return EXTEND_RATE;
        }
        if (leverDown && alternate) {
            return GRAVITY_EXTEND_RATE;
        }
        if (!leverDown && hydraulics) {
            return -RETRACT_RATE;
        }
        return 0.0;
    }

    private void stepLeg(SimContext context, Leg leg, String stuckMalfunction, boolean leverDown, double rate, double dtSeconds) {
        double target = leverDown ? 1.0 : 0.0;
        if (leverDown && context.malfunctions().isActive(stuckMalfunction)) {
            target = 0.0;
        }
        if (!leverDown && context.malfunctions().isActive(UPLOCK_JAM)) {
            target = 1.0;
        }
        if (rate == 0.0) {
            leg.position.update(leg.position.output(), dtSeconds);
        } else {
            leg.position.update(target, dtSeconds);
        }
        leg.doorPosition = Math.min(1.0, Math.max(0.0, Math.abs(leg.position.output() - target) * 2.0));
    }

    private void publish(SimContext context, boolean leverDown) {
        boolean downLocked = lockedDown(nose) && lockedDown(left) && lockedDown(right);
        boolean upLocked = lockedUp(nose) && lockedUp(left) && lockedUp(right);
        boolean transit = !(downLocked || upLocked);
        boolean disagree = (leverDown && !downLocked) || (!leverDown && !upLocked);
        if (context.malfunctions().isActive(DISAGREE_INDICATION_FAIL)) {
            disagree = false;
        }
        context.bus().publish("gear.nose.position", nose.position.output());
        context.bus().publish("gear.left.position", left.position.output());
        context.bus().publish("gear.right.position", right.position.output());
        context.bus().publish(LandingGearSignals.NOSE_DOOR_POSITION, nose.doorPosition);
        context.bus().publish(LandingGearSignals.LEFT_DOOR_POSITION, left.doorPosition);
        context.bus().publish(LandingGearSignals.RIGHT_DOOR_POSITION, right.doorPosition);
        context.bus().publish(Signals.GEAR_DOWN_LOCKED, downLocked);
        context.bus().publish(Signals.GEAR_IN_TRANSIT, transit);
        context.bus().publish(LandingGearSignals.DISAGREE_WARNING, disagree);
    }

    private static boolean lockedDown(Leg leg) {
        return leg.position.output() >= LOCK_TOLERANCE;
    }

    private static boolean lockedUp(Leg leg) {
        return leg.position.output() <= 1.0 - LOCK_TOLERANCE;
    }

    private static final class Leg {
        private final String name;
        private final RateLimiter position = new RateLimiter(RETRACT_RATE, 0.0);
        private double doorPosition;

        private Leg(String name) {
            this.name = name;
        }

        private void reset() {
            position.reset(0.0);
            doorPosition = 0.0;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
