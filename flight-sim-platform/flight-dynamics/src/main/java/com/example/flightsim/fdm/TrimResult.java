package com.example.flightsim.fdm;

/**
 * Result of a steady, wings-level trim calculation.
 *
 * @param state              trimmed state
 * @param controls           trimmed controls
 * @param totalThrustN       total thrust required
 * @param throttle           approximate throttle fraction
 * @param liftN              trimmed lift
 * @param dragN              trimmed drag
 */
public record TrimResult(
        AircraftState state,
        AeroModel.Controls controls,
        double totalThrustN,
        double throttle,
        double liftN,
        double dragN) {
}
