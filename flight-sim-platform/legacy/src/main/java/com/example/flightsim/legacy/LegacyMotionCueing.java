package com.example.flightsim.legacy;

import com.example.flightsim.core.math.RateLimiter;
import com.example.flightsim.core.math.Vector3;
import com.example.flightsim.core.units.Units;

/**
 * Classical washout cueing filter ported from the previous-generation simulator host.
 *
 * <p>The original motion software was written in C++ in the 2000s. This Java version keeps the
 * deterministic washout structure: high-pass translational and rotational cues, low-frequency
 * tilt coordination and hard platform excursion limits.
 */
public final class LegacyMotionCueing {

    private static final double TRANSLATION_LIMIT_M = 0.62;
    private static final double ROTATION_LIMIT_RAD = Math.toRadians(18.0);
    private static final double TILT_LIMIT_RAD = Math.toRadians(12.0);

    private final AxisWashout surge = new AxisWashout(0.75, 0.70);
    private final AxisWashout sway = new AxisWashout(0.75, 0.70);
    private final AxisWashout heave = new AxisWashout(0.55, 0.65);
    private final AxisWashout roll = new AxisWashout(1.15, 0.80);
    private final AxisWashout pitch = new AxisWashout(1.15, 0.80);
    private final AxisWashout yaw = new AxisWashout(1.30, 0.90);
    private final RateLimiter rollTiltLimiter = new RateLimiter(Math.toRadians(2.5), 0.0);
    private final RateLimiter pitchTiltLimiter = new RateLimiter(Math.toRadians(2.5), 0.0);

    /**
     * Advances the cueing filter.
     *
     * @param specificForceMps2 body specific force
     * @param angularRatesRadps body angular rates
     * @param dtSeconds         step length
     * @return platform actuator commands
     */
    public Command step(Vector3 specificForceMps2, Vector3 angularRatesRadps, double dtSeconds) {
        double x = clamp(surge.step(specificForceMps2.x(), dtSeconds) * 0.085, -TRANSLATION_LIMIT_M, TRANSLATION_LIMIT_M);
        double y = clamp(sway.step(specificForceMps2.y(), dtSeconds) * 0.085, -TRANSLATION_LIMIT_M, TRANSLATION_LIMIT_M);
        double z = clamp(heave.step(specificForceMps2.z(), dtSeconds) * 0.065, -TRANSLATION_LIMIT_M, TRANSLATION_LIMIT_M);

        double targetRollTilt = clamp(specificForceMps2.y() / Units.STANDARD_GRAVITY, -TILT_LIMIT_RAD, TILT_LIMIT_RAD);
        double targetPitchTilt = clamp(-specificForceMps2.x() / Units.STANDARD_GRAVITY, -TILT_LIMIT_RAD, TILT_LIMIT_RAD);
        double rollCommand = clamp(roll.step(angularRatesRadps.x(), dtSeconds) * 0.22
                + rollTiltLimiter.update(targetRollTilt, dtSeconds), -ROTATION_LIMIT_RAD, ROTATION_LIMIT_RAD);
        double pitchCommand = clamp(pitch.step(angularRatesRadps.y(), dtSeconds) * 0.22
                + pitchTiltLimiter.update(targetPitchTilt, dtSeconds), -ROTATION_LIMIT_RAD, ROTATION_LIMIT_RAD);
        double yawCommand = clamp(yaw.step(angularRatesRadps.z(), dtSeconds) * 0.25, -ROTATION_LIMIT_RAD, ROTATION_LIMIT_RAD);
        return new Command(x, y, z, rollCommand, pitchCommand, yawCommand);
    }

    /** Resets the filter to neutral. */
    public void reset() {
        surge.reset();
        sway.reset();
        heave.reset();
        roll.reset();
        pitch.reset();
        yaw.reset();
        rollTiltLimiter.reset(0.0);
        pitchTiltLimiter.reset(0.0);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static final class AxisWashout {
        private final double firstTau;
        private final double secondTau;
        private double low1;
        private double low2;

        AxisWashout(double firstTau, double secondTau) {
            this.firstTau = firstTau;
            this.secondTau = secondTau;
        }

        double step(double input, double dtSeconds) {
            double a1 = 1.0 - Math.exp(-dtSeconds / firstTau);
            low1 += a1 * (input - low1);
            double high1 = input - low1;
            double a2 = 1.0 - Math.exp(-dtSeconds / secondTau);
            low2 += a2 * (high1 - low2);
            return high1 - low2;
        }

        void reset() {
            low1 = 0.0;
            low2 = 0.0;
        }
    }

    /**
     * Six-axis platform command.
     *
     * @param surgeM   fore-aft platform position
     * @param swayM    lateral platform position
     * @param heaveM   vertical platform position
     * @param rollRad  platform roll
     * @param pitchRad platform pitch
     * @param yawRad   platform yaw
     */
    public record Command(double surgeM, double swayM, double heaveM, double rollRad, double pitchRad, double yawRad) {
    }
}
