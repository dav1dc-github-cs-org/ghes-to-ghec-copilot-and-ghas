package com.example.flightsim.fdm;

import com.example.flightsim.core.units.Units;

/**
 * Steady wings-level trim solver for the FS-200.
 */
public final class TrimSolver {

    private final AeroModel aeroModel;
    private final AircraftParameters parameters;

    /**
     * Creates a solver.
     *
     * @param aeroModel aerodynamic model
     */
    public TrimSolver(AeroModel aeroModel) {
        this.aeroModel = aeroModel;
        this.parameters = aeroModel.parameters();
    }

    /**
     * Solves for a steady level-flight trim point.
     *
     * @param speedMps    true airspeed
     * @param altitudeM   altitude
     * @param headingRad  heading
     * @param massKg      aircraft mass
     * @return trim result
     */
    public TrimResult solve(double speedMps, double altitudeM, double headingRad, double massKg) {
        Atmosphere.Sample atmosphere = Atmosphere.at(altitudeM);
        double weight = massKg * Units.STANDARD_GRAVITY;
        double low = Math.toRadians(-4.0);
        double high = Math.toRadians(14.0);
        for (int i = 0; i < 60; i++) {
            double mid = 0.5 * (low + high);
            double lift = liftAt(mid, speedMps, altitudeM, headingRad, massKg, atmosphere);
            if (lift < weight) {
                low = mid;
            } else {
                high = mid;
            }
        }
        double alpha = 0.5 * (low + high);
        double elevator = clamp((0.045 + 1.05 * (alpha - Math.toRadians(2.0))) / 1.18, -1.0, 1.0);
        AeroModel.Controls controls = new AeroModel.Controls(elevator, 0.0, 0.0, 0.0);
        AircraftState state = stateFor(alpha, speedMps, altitudeM, headingRad, massKg);
        AeroModel.Coefficients coefficients = aeroModel.coefficients(state, controls, atmosphere, false);
        double qArea = coefficients.dynamicPressurePa() * parameters.wingAreaM2();
        double lift = coefficients.lift() * qArea;
        double drag = coefficients.drag() * qArea;
        double totalThrust = Math.max(0.0, drag / Math.max(0.1, Math.cos(alpha)));
        double throttle = clamp(totalThrust / (2.0 * parameters.maxThrustPerEngineN()), 0.0, 1.0);
        return new TrimResult(state, controls, totalThrust, throttle, lift, drag);
    }

    private double liftAt(double alpha, double speedMps, double altitudeM, double headingRad, double massKg, Atmosphere.Sample atmosphere) {
        AircraftState state = stateFor(alpha, speedMps, altitudeM, headingRad, massKg);
        AeroModel.Controls controls = new AeroModel.Controls(
                clamp((0.045 + 1.05 * (alpha - Math.toRadians(2.0))) / 1.18, -1.0, 1.0), 0.0, 0.0, 0.0);
        AeroModel.Coefficients coefficients = aeroModel.coefficients(state, controls, atmosphere, false);
        return coefficients.lift() * coefficients.dynamicPressurePa() * parameters.wingAreaM2();
    }

    private static AircraftState stateFor(double alpha, double speedMps, double altitudeM, double headingRad, double massKg) {
        double theta = alpha;
        return new AircraftState(0.0, 0.0, altitudeM, speedMps * Math.cos(alpha), 0.0, speedMps * Math.sin(alpha),
                0.0, theta, headingRad, 0.0, 0.0, 0.0, massKg);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
