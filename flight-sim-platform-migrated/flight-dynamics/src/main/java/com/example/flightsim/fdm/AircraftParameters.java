package com.example.flightsim.fdm;

/**
 * Physical and aerodynamic constants for the fictional FS-200 regional jet.
 *
 * @param wingAreaM2              reference wing area
 * @param wingSpanM               wing span
 * @param meanChordM              mean aerodynamic chord
 * @param emptyMassKg             empty operating mass
 * @param nominalFuelKg           nominal fuel used for default scenarios
 * @param maxTakeoffMassKg        maximum permitted gross mass
 * @param inertiaXxKgM2           roll inertia
 * @param inertiaYyKgM2           pitch inertia
 * @param inertiaZzKgM2           yaw inertia
 * @param inertiaXzKgM2           product of inertia
 * @param maxThrustPerEngineN     maximum sea-level static thrust per engine
 * @param zeroLiftDrag            clean zero-lift drag coefficient
 * @param inducedDragFactor       induced drag factor
 * @param flapDragIncrement       drag increment at full flap
 * @param gearDragIncrement       landing gear drag increment
 * @param rollingFriction         ground rolling-friction coefficient
 * @param maxElevatorRad          elevator travel
 * @param maxAileronRad           aileron travel
 * @param maxRudderRad            rudder travel
 * @param flapMaxLiftIncrement    lift increment at full flap
 */
public record AircraftParameters(
        double wingAreaM2,
        double wingSpanM,
        double meanChordM,
        double emptyMassKg,
        double nominalFuelKg,
        double maxTakeoffMassKg,
        double inertiaXxKgM2,
        double inertiaYyKgM2,
        double inertiaZzKgM2,
        double inertiaXzKgM2,
        double maxThrustPerEngineN,
        double zeroLiftDrag,
        double inducedDragFactor,
        double flapDragIncrement,
        double gearDragIncrement,
        double rollingFriction,
        double maxElevatorRad,
        double maxAileronRad,
        double maxRudderRad,
        double flapMaxLiftIncrement) {

    /**
     * Returns the default FS-200 data set.
     *
     * @return aircraft parameters for the fictional twin-engine regional jet
     */
    public static AircraftParameters fs200() {
        return new AircraftParameters(
                74.0,
                27.8,
                2.75,
                19800.0,
                4200.0,
                35800.0,
                132000.0,
                715000.0,
                810000.0,
                12500.0,
                58500.0,
                0.022,
                0.043,
                0.055,
                0.030,
                0.025,
                Math.toRadians(24.0),
                Math.toRadians(20.0),
                Math.toRadians(28.0),
                0.82);
    }
}
