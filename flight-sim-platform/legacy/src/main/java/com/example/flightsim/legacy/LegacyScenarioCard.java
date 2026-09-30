package com.example.flightsim.legacy;

/**
 * Structured representation of a previous-generation fixed-width scenario card.
 *
 * @param airportIdent       airport identifier
 * @param runway             runway designator
 * @param weightKg           gross weight
 * @param cgPercentMac       centre of gravity in percent mean aerodynamic chord
 * @param fuelKg             usable fuel
 * @param windFromRad        wind direction, true
 * @param windSpeedMps       wind speed
 * @param visibilityM        visibility
 * @param outsideAirTempK    outside air temperature
 * @param altimeterPa        altimeter setting
 */
public record LegacyScenarioCard(
        String airportIdent,
        String runway,
        double weightKg,
        double cgPercentMac,
        double fuelKg,
        double windFromRad,
        double windSpeedMps,
        double visibilityM,
        double outsideAirTempK,
        double altimeterPa) {
}
