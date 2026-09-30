package com.example.flightsim.fdm;

import com.example.flightsim.core.math.Angles;

/**
 * Immutable six-degree-of-freedom aircraft state.
 *
 * @param latitudeRad   geodetic latitude
 * @param longitudeRad  geodetic longitude
 * @param altitudeM     altitude above mean sea level
 * @param uMps          body-axis forward velocity
 * @param vMps          body-axis right velocity
 * @param wMps          body-axis down velocity
 * @param phiRad        roll angle
 * @param thetaRad      pitch angle
 * @param psiRad        true heading
 * @param pRadps        roll rate
 * @param qRadps        pitch rate
 * @param rRadps        yaw rate
 * @param massKg        gross mass
 */
public record AircraftState(
        double latitudeRad,
        double longitudeRad,
        double altitudeM,
        double uMps,
        double vMps,
        double wMps,
        double phiRad,
        double thetaRad,
        double psiRad,
        double pRadps,
        double qRadps,
        double rRadps,
        double massKg) {

    /**
     * Returns true airspeed.
     *
     * @return true airspeed in m/s
     */
    public double trueAirspeedMps() {
        return Math.sqrt(uMps * uMps + vMps * vMps + wMps * wMps);
    }

    /**
     * Returns angle of attack.
     *
     * @return angle of attack in radians
     */
    public double alphaRad() {
        return Math.atan2(wMps, Math.max(1.0e-6, uMps));
    }

    /**
     * Returns sideslip angle.
     *
     * @return sideslip in radians
     */
    public double betaRad() {
        double tas = Math.max(1.0e-6, trueAirspeedMps());
        return Math.asin(Math.max(-1.0, Math.min(1.0, vMps / tas)));
    }

    /**
     * Returns vertical speed, positive upward.
     *
     * @return climb rate in m/s
     */
    public double verticalSpeedMps() {
        return -EquationsOfMotion.bodyToNed(new com.example.flightsim.core.math.Vector3(uMps, vMps, wMps), phiRad, thetaRad, psiRad).z();
    }

    /**
     * Returns ground speed.
     *
     * @return horizontal speed in m/s
     */
    public double groundSpeedMps() {
        com.example.flightsim.core.math.Vector3 ned =
                EquationsOfMotion.bodyToNed(new com.example.flightsim.core.math.Vector3(uMps, vMps, wMps), phiRad, thetaRad, psiRad);
        return Math.sqrt(ned.x() * ned.x() + ned.y() * ned.y());
    }

    /**
     * Returns flight-path angle.
     *
     * @return flight-path angle in radians
     */
    public double flightPathAngleRad() {
        return Math.atan2(verticalSpeedMps(), Math.max(1.0e-6, groundSpeedMps()));
    }

    /**
     * Returns a copy with a different mass.
     *
     * @param newMassKg new gross mass
     * @return updated state
     */
    public AircraftState withMass(double newMassKg) {
        return new AircraftState(latitudeRad, longitudeRad, altitudeM, uMps, vMps, wMps, phiRad, thetaRad, psiRad,
                pRadps, qRadps, rRadps, newMassKg);
    }

    /**
     * Returns a normalised copy with bounded attitudes and wrapped heading.
     *
     * @return normalised state
     */
    public AircraftState normalised() {
        double limitedTheta = Math.max(Math.toRadians(-85.0), Math.min(Math.toRadians(85.0), thetaRad));
        return new AircraftState(latitudeRad, longitudeRad, altitudeM, uMps, vMps, wMps, Angles.wrapPi(phiRad),
                limitedTheta, Angles.wrapTwoPi(psiRad), pRadps, qRadps, rRadps, massKg);
    }
}
