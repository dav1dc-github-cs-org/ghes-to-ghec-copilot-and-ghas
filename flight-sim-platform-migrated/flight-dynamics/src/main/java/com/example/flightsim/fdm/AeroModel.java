package com.example.flightsim.fdm;

import com.example.flightsim.core.math.LookupTable2D;
import com.example.flightsim.core.math.Vector3;

/**
 * Low-order aerodynamic model for stable, real-time FS-200 training scenarios.
 */
public final class AeroModel {

    private final AircraftParameters parameters;
    private final LookupTable2D liftTable;

    /**
     * Creates an aerodynamic model.
     *
     * @param parameters aircraft parameters
     */
    public AeroModel(AircraftParameters parameters) {
        this.parameters = parameters;
        double[] alpha = {
            Math.toRadians(-12.0), Math.toRadians(-6.0), 0.0, Math.toRadians(6.0), Math.toRadians(12.0),
            Math.toRadians(16.0), Math.toRadians(20.0), Math.toRadians(24.0)
        };
        double[] flap = {0.0, 0.5, 1.0};
        double[][] cl = {
            {-0.65, -0.38, -0.16},
            {-0.22, 0.05, 0.28},
            {0.25, 0.56, 0.86},
            {0.78, 1.10, 1.42},
            {1.28, 1.58, 1.88},
            {1.45, 1.76, 2.04},
            {1.18, 1.48, 1.72},
            {0.78, 0.96, 1.10}
        };
        liftTable = new LookupTable2D(alpha, flap, cl);
    }

    /**
     * Computes coefficients at the current state.
     *
     * @param state       aircraft state
     * @param controls    flight controls
     * @param atmosphere  local atmosphere
     * @param gearDown    true when landing gear is down
     * @return aerodynamic coefficients
     */
    public Coefficients coefficients(AircraftState state, Controls controls, Atmosphere.Sample atmosphere, boolean gearDown) {
        double tas = Math.max(1.0, state.trueAirspeedMps());
        double alpha = state.alphaRad();
        double beta = state.betaRad();
        double flap = clamp(controls.flap(), 0.0, 1.0);
        double qHat = state.qRadps() * parameters.meanChordM() / (2.0 * tas);
        double pHat = state.pRadps() * parameters.wingSpanM() / (2.0 * tas);
        double rHat = state.rRadps() * parameters.wingSpanM() / (2.0 * tas);

        double cl = liftTable.lookup(alpha, flap) + 0.18 * controls.elevator();
        double cd = parameters.zeroLiftDrag() + parameters.inducedDragFactor() * cl * cl
                + parameters.flapDragIncrement() * flap * flap + (gearDown ? parameters.gearDragIncrement() : 0.0);
        double cm = -0.045 - 1.05 * (alpha - Math.toRadians(2.0)) + 1.18 * controls.elevator() - 12.0 * qHat;
        double cy = -0.72 * beta + 0.18 * controls.rudder();
        double clRoll = -0.55 * pHat - 0.10 * beta + 0.22 * controls.aileron() + 0.035 * controls.rudder();
        double cn = -0.18 * rHat + 0.16 * beta - 0.025 * controls.aileron() + 0.11 * controls.rudder();
        double dynamicPressure = 0.5 * atmosphere.densityKgPerM3() * tas * tas;
        return new Coefficients(cl, cd, cm, cy, clRoll, cn, dynamicPressure);
    }

    /**
     * Computes body-axis aerodynamic force in newtons.
     *
     * @param state       aircraft state
     * @param controls    flight controls
     * @param atmosphere  local atmosphere
     * @param gearDown    true when gear is down
     * @return body-axis force, x forward, y right, z down
     */
    public Vector3 bodyForce(AircraftState state, Controls controls, Atmosphere.Sample atmosphere, boolean gearDown) {
        Coefficients c = coefficients(state, controls, atmosphere, gearDown);
        double qArea = c.dynamicPressurePa() * parameters.wingAreaM2();
        double alpha = state.alphaRad();
        double lift = c.lift() * qArea;
        double drag = c.drag() * qArea;
        double x = -drag * Math.cos(alpha) + lift * Math.sin(alpha);
        double z = -lift * Math.cos(alpha) - drag * Math.sin(alpha);
        double y = c.sideForce() * qArea;
        return new Vector3(x, y, z);
    }

    /**
     * Computes aerodynamic moments in newton-metres.
     *
     * @param state       aircraft state
     * @param controls    flight controls
     * @param atmosphere  local atmosphere
     * @param gearDown    true when gear is down
     * @return roll, pitch and yaw moments
     */
    public Vector3 bodyMoment(AircraftState state, Controls controls, Atmosphere.Sample atmosphere, boolean gearDown) {
        Coefficients c = coefficients(state, controls, atmosphere, gearDown);
        double qArea = c.dynamicPressurePa() * parameters.wingAreaM2();
        return new Vector3(
                c.rollMoment() * qArea * parameters.wingSpanM(),
                c.pitchMoment() * qArea * parameters.meanChordM(),
                c.yawMoment() * qArea * parameters.wingSpanM());
    }

    AircraftParameters parameters() {
        return parameters;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Flight controls normalised to -1..1 except flap.
     *
     * @param elevator normalised elevator
     * @param aileron  normalised aileron
     * @param rudder   normalised rudder
     * @param flap     normalised flap lever
     */
    public record Controls(double elevator, double aileron, double rudder, double flap) {
        /** Neutral controls. */
        public static final Controls NEUTRAL = new Controls(0.0, 0.0, 0.0, 0.0);
    }

    /**
     * Aerodynamic coefficients.
     *
     * @param lift              lift coefficient
     * @param drag              drag coefficient
     * @param pitchMoment       pitching moment coefficient
     * @param sideForce         side-force coefficient
     * @param rollMoment        rolling moment coefficient
     * @param yawMoment         yawing moment coefficient
     * @param dynamicPressurePa dynamic pressure
     */
    public record Coefficients(
            double lift,
            double drag,
            double pitchMoment,
            double sideForce,
            double rollMoment,
            double yawMoment,
            double dynamicPressurePa) {
    }
}
