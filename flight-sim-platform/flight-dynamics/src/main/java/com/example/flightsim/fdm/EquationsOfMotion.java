package com.example.flightsim.fdm;

import com.example.flightsim.core.Signals;
import com.example.flightsim.core.SimBus;
import com.example.flightsim.core.math.Angles;
import com.example.flightsim.core.math.Vector3;
import com.example.flightsim.core.units.Units;

/**
 * Six-degree-of-freedom rigid-body equations for a flat-earth training area.
 */
public final class EquationsOfMotion {

    private static final double EARTH_RADIUS_M = 6371000.0;
    private static final double MIN_COS_THETA = 0.05;

    private final AircraftParameters parameters;
    private final AeroModel aeroModel;
    private final double fieldElevationM;
    private boolean weightOnWheels;

    /**
     * Creates equations of motion.
     *
     * @param parameters      aircraft parameters
     * @param aeroModel       aerodynamic model
     * @param fieldElevationM runway elevation
     */
    public EquationsOfMotion(AircraftParameters parameters, AeroModel aeroModel, double fieldElevationM) {
        this.parameters = parameters;
        this.aeroModel = aeroModel;
        this.fieldElevationM = fieldElevationM;
    }

    /**
     * Integrates one simulation step using fourth-order Runge-Kutta.
     *
     * @param state       current state
     * @param controls    controls
     * @param atmosphere  local atmosphere
     * @param bus         signal bus containing engine thrusts
     * @param gearDown    true when landing gear drag applies
     * @param dtSeconds   step length
     * @return integrated state
     */
    public AircraftState integrate(
            AircraftState state,
            AeroModel.Controls controls,
            Atmosphere.Sample atmosphere,
            SimBus bus,
            boolean gearDown,
            double dtSeconds) {
        Derivative k1 = derivative(state, controls, atmosphere, bus, gearDown);
        Derivative k2 = derivative(add(state, k1, 0.5 * dtSeconds), controls, atmosphere, bus, gearDown);
        Derivative k3 = derivative(add(state, k2, 0.5 * dtSeconds), controls, atmosphere, bus, gearDown);
        Derivative k4 = derivative(add(state, k3, dtSeconds), controls, atmosphere, bus, gearDown);
        AircraftState next = combine(state, k1, k2, k3, k4, dtSeconds).normalised();
        if (next.altitudeM() <= fieldElevationM) {
            Vector3 ned = bodyToNed(new Vector3(next.uMps(), next.vMps(), next.wMps()), next.phiRad(), next.thetaRad(), next.psiRad());
            if (ned.z() > 0.0) {
                Vector3 adjusted = nedToBody(new Vector3(ned.x() * 0.985, ned.y() * 0.985, 0.0),
                        next.phiRad(), next.thetaRad(), next.psiRad());
                next = new AircraftState(next.latitudeRad(), next.longitudeRad(), fieldElevationM, adjusted.x(), adjusted.y(), adjusted.z(),
                        next.phiRad(), next.thetaRad(), next.psiRad(), next.pRadps() * 0.5, next.qRadps() * 0.5,
                        next.rRadps() * 0.5, next.massKg()).normalised();
            }
            weightOnWheels = true;
        } else {
            weightOnWheels = false;
        }
        return next;
    }

    /**
     * Tells whether the last integration step was on the runway.
     *
     * @return true if weight is on wheels
     */
    public boolean weightOnWheels() {
        return weightOnWheels;
    }

    /**
     * Transforms a body-axis vector to local north-east-down axes.
     *
     * @param body     body-axis vector
     * @param phiRad   roll
     * @param thetaRad pitch
     * @param psiRad   heading
     * @return vector in north-east-down axes
     */
    public static Vector3 bodyToNed(Vector3 body, double phiRad, double thetaRad, double psiRad) {
        double cPhi = Math.cos(phiRad);
        double sPhi = Math.sin(phiRad);
        double cTheta = Math.cos(thetaRad);
        double sTheta = Math.sin(thetaRad);
        double cPsi = Math.cos(psiRad);
        double sPsi = Math.sin(psiRad);
        double x = cTheta * cPsi * body.x()
                + (sPhi * sTheta * cPsi - cPhi * sPsi) * body.y()
                + (cPhi * sTheta * cPsi + sPhi * sPsi) * body.z();
        double y = cTheta * sPsi * body.x()
                + (sPhi * sTheta * sPsi + cPhi * cPsi) * body.y()
                + (cPhi * sTheta * sPsi - sPhi * cPsi) * body.z();
        double z = -sTheta * body.x() + sPhi * cTheta * body.y() + cPhi * cTheta * body.z();
        return new Vector3(x, y, z);
    }

    /**
     * Transforms a north-east-down vector to body axes.
     *
     * @param ned      vector in north-east-down axes
     * @param phiRad   roll
     * @param thetaRad pitch
     * @param psiRad   heading
     * @return body-axis vector
     */
    public static Vector3 nedToBody(Vector3 ned, double phiRad, double thetaRad, double psiRad) {
        double cPhi = Math.cos(phiRad);
        double sPhi = Math.sin(phiRad);
        double cTheta = Math.cos(thetaRad);
        double sTheta = Math.sin(thetaRad);
        double cPsi = Math.cos(psiRad);
        double sPsi = Math.sin(psiRad);
        double x = cTheta * cPsi * ned.x() + cTheta * sPsi * ned.y() - sTheta * ned.z();
        double y = (sPhi * sTheta * cPsi - cPhi * sPsi) * ned.x()
                + (sPhi * sTheta * sPsi + cPhi * cPsi) * ned.y() + sPhi * cTheta * ned.z();
        double z = (cPhi * sTheta * cPsi + sPhi * sPsi) * ned.x()
                + (cPhi * sTheta * sPsi - sPhi * cPsi) * ned.y() + cPhi * cTheta * ned.z();
        return new Vector3(x, y, z);
    }

    private Derivative derivative(
            AircraftState state,
            AeroModel.Controls controls,
            Atmosphere.Sample atmosphere,
            SimBus bus,
            boolean gearDown) {
        double mass = Math.max(1.0, state.massKg());
        Vector3 aeroForce = aeroModel.bodyForce(state, controls, atmosphere, gearDown);
        double thrust = bus.read(Signals.engine(1, "thrust.n"), 0.0) + bus.read(Signals.engine(2, "thrust.n"), 0.0);
        Vector3 force = aeroForce.plus(new Vector3(thrust, 0.0, 0.0));
        if (state.altitudeM() <= fieldElevationM + 0.02) {
            double rolling = parameters.rollingFriction() * mass * Units.STANDARD_GRAVITY;
            force = force.plus(new Vector3(-Math.copySign(Math.min(Math.abs(force.x()), rolling), state.uMps()), 0.0, 0.0));
        }
        Vector3 gravityBody = nedToBody(new Vector3(0.0, 0.0, Units.STANDARD_GRAVITY),
                state.phiRad(), state.thetaRad(), state.psiRad());
        double uDot = force.x() / mass + state.rRadps() * state.vMps() - state.qRadps() * state.wMps() + gravityBody.x();
        double vDot = force.y() / mass + state.pRadps() * state.wMps() - state.rRadps() * state.uMps() + gravityBody.y();
        double wDot = force.z() / mass + state.qRadps() * state.uMps() - state.pRadps() * state.vMps() + gravityBody.z();

        Vector3 moment = aeroModel.bodyMoment(state, controls, atmosphere, gearDown);
        double pDot = (moment.x() + (parameters.inertiaYyKgM2() - parameters.inertiaZzKgM2()) * state.qRadps() * state.rRadps())
                / parameters.inertiaXxKgM2();
        double qDot = (moment.y() + (parameters.inertiaZzKgM2() - parameters.inertiaXxKgM2()) * state.pRadps() * state.rRadps())
                / parameters.inertiaYyKgM2();
        double rDot = (moment.z() + (parameters.inertiaXxKgM2() - parameters.inertiaYyKgM2()) * state.pRadps() * state.qRadps())
                / parameters.inertiaZzKgM2();

        double phi = state.phiRad();
        double theta = state.thetaRad();
        double cosTheta = Math.copySign(Math.max(Math.abs(Math.cos(theta)), MIN_COS_THETA), Math.cos(theta));
        double phiDot = state.pRadps() + Math.tan(theta) * (state.qRadps() * Math.sin(phi) + state.rRadps() * Math.cos(phi));
        double thetaDot = state.qRadps() * Math.cos(phi) - state.rRadps() * Math.sin(phi);
        double psiDot = (state.qRadps() * Math.sin(phi) + state.rRadps() * Math.cos(phi)) / cosTheta;

        Vector3 nedVelocity = bodyToNed(new Vector3(state.uMps(), state.vMps(), state.wMps()),
                state.phiRad(), state.thetaRad(), state.psiRad());
        double latDot = nedVelocity.x() / EARTH_RADIUS_M;
        double lonDot = nedVelocity.y() / (EARTH_RADIUS_M * Math.max(0.2, Math.cos(state.latitudeRad())));
        double altDot = -nedVelocity.z();
        return new Derivative(latDot, lonDot, altDot, uDot, vDot, wDot, phiDot, thetaDot, psiDot, pDot, qDot, rDot);
    }

    private static AircraftState add(AircraftState s, Derivative d, double h) {
        return new AircraftState(
                s.latitudeRad() + h * d.latDot(),
                s.longitudeRad() + h * d.lonDot(),
                s.altitudeM() + h * d.altDot(),
                s.uMps() + h * d.uDot(),
                s.vMps() + h * d.vDot(),
                s.wMps() + h * d.wDot(),
                s.phiRad() + h * d.phiDot(),
                s.thetaRad() + h * d.thetaDot(),
                Angles.wrapTwoPi(s.psiRad() + h * d.psiDot()),
                s.pRadps() + h * d.pDot(),
                s.qRadps() + h * d.qDot(),
                s.rRadps() + h * d.rDot(),
                s.massKg());
    }

    private static AircraftState combine(AircraftState s, Derivative k1, Derivative k2, Derivative k3, Derivative k4, double h) {
        return new AircraftState(
                s.latitudeRad() + h * weighted(k1.latDot(), k2.latDot(), k3.latDot(), k4.latDot()),
                s.longitudeRad() + h * weighted(k1.lonDot(), k2.lonDot(), k3.lonDot(), k4.lonDot()),
                s.altitudeM() + h * weighted(k1.altDot(), k2.altDot(), k3.altDot(), k4.altDot()),
                s.uMps() + h * weighted(k1.uDot(), k2.uDot(), k3.uDot(), k4.uDot()),
                s.vMps() + h * weighted(k1.vDot(), k2.vDot(), k3.vDot(), k4.vDot()),
                s.wMps() + h * weighted(k1.wDot(), k2.wDot(), k3.wDot(), k4.wDot()),
                s.phiRad() + h * weighted(k1.phiDot(), k2.phiDot(), k3.phiDot(), k4.phiDot()),
                s.thetaRad() + h * weighted(k1.thetaDot(), k2.thetaDot(), k3.thetaDot(), k4.thetaDot()),
                Angles.wrapTwoPi(s.psiRad() + h * weighted(k1.psiDot(), k2.psiDot(), k3.psiDot(), k4.psiDot())),
                s.pRadps() + h * weighted(k1.pDot(), k2.pDot(), k3.pDot(), k4.pDot()),
                s.qRadps() + h * weighted(k1.qDot(), k2.qDot(), k3.qDot(), k4.qDot()),
                s.rRadps() + h * weighted(k1.rDot(), k2.rDot(), k3.rDot(), k4.rDot()),
                s.massKg());
    }

    private static double weighted(double k1, double k2, double k3, double k4) {
        return (k1 + 2.0 * k2 + 2.0 * k3 + k4) / 6.0;
    }

    private record Derivative(
            double latDot,
            double lonDot,
            double altDot,
            double uDot,
            double vDot,
            double wDot,
            double phiDot,
            double thetaDot,
            double psiDot,
            double pDot,
            double qDot,
            double rDot) {
    }
}
