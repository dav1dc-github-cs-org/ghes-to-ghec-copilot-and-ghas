package com.example.flightsim.scenario;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A training scenario: where the session starts, the weather, and the malfunctions the instructor
 * has scheduled.
 *
 * <p>Scenario files are instructor-facing, so values are stored in the units the instructor uses
 * (feet, knots, degrees). {@link #toConfigOverrides()} converts them into the simulation's
 * configuration keys.
 *
 * @param id                stable identifier, lower-case with hyphens
 * @param title             title shown on the instructor station
 * @param description       what the scenario trains
 * @param airport           ICAO identifier of the departure or destination airport
 * @param runway            runway designator, for example {@code 27L}
 * @param initialConditions aircraft state when the session starts
 * @param weather           weather when the session starts
 * @param malfunctions      malfunctions inserted automatically during the session
 * @param tags              syllabus tags used to search the library
 */
public record Scenario(
        String id,
        String title,
        String description,
        String airport,
        String runway,
        InitialConditions initialConditions,
        Weather weather,
        List<ScheduledMalfunction> malfunctions,
        List<String> tags) {

    /** Canonical constructor; copies the lists so the record is immutable. */
    public Scenario {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(title, "title");
        description = description == null ? "" : description;
        Objects.requireNonNull(initialConditions, "initialConditions");
        weather = weather == null ? Weather.standard() : weather;
        malfunctions = malfunctions == null ? List.of() : List.copyOf(malfunctions);
        tags = tags == null ? List.of() : List.copyOf(tags);
    }

    /**
     * Converts the scenario into simulation configuration overrides.
     *
     * @return configuration keys and values
     */
    public Map<String, String> toConfigOverrides() {
        Map<String, String> overrides = new LinkedHashMap<>();
        overrides.put("fdm.initial.altitude.ft", Double.toString(initialConditions.altitudeFt()));
        overrides.put("fdm.initial.ias.kt", Double.toString(initialConditions.indicatedAirspeedKt()));
        overrides.put("fdm.initial.heading.deg", Double.toString(initialConditions.headingDeg()));
        overrides.put("fdm.initial.on-ground", Boolean.toString(initialConditions.onGround()));
        overrides.put("fuel.initial.kg", Double.toString(initialConditions.fuelKg()));
        overrides.put("env.wind.from.deg", Double.toString(weather.windFromDeg()));
        overrides.put("env.wind.speed.kt", Double.toString(weather.windSpeedKt()));
        overrides.put("env.oat.degc", Double.toString(weather.temperatureC()));
        overrides.put("env.qnh.hpa", Double.toString(weather.qnhHpa()));
        return overrides;
    }

    /**
     * Aircraft state at the start of the session.
     *
     * @param altitudeFt          pressure altitude in feet
     * @param indicatedAirspeedKt indicated airspeed in knots
     * @param headingDeg          true heading in degrees
     * @param fuelKg              fuel on board in kilograms
     * @param grossMassKg         gross mass in kilograms
     * @param onGround            true if the session starts on the runway
     */
    public record InitialConditions(
            double altitudeFt,
            double indicatedAirspeedKt,
            double headingDeg,
            double fuelKg,
            double grossMassKg,
            boolean onGround) {
    }

    /**
     * Weather at the start of the session.
     *
     * @param windFromDeg   direction the wind blows from, degrees true
     * @param windSpeedKt   wind speed in knots
     * @param visibilityM   visibility in metres
     * @param cloudBaseFt   cloud base in feet above the airport
     * @param temperatureC  surface temperature in degrees Celsius
     * @param qnhHpa        altimeter setting in hectopascals
     */
    public record Weather(
            double windFromDeg,
            double windSpeedKt,
            double visibilityM,
            double cloudBaseFt,
            double temperatureC,
            double qnhHpa) {

        /**
         * ISA sea-level conditions with calm wind and unlimited visibility.
         *
         * @return standard weather
         */
        public static Weather standard() {
            return new Weather(0.0, 0.0, 10_000.0, 25_000.0, 15.0, 1013.25);
        }
    }

    /**
     * A malfunction the scenario inserts automatically.
     *
     * @param atSeconds      session time at which to insert it
     * @param malfunctionId  malfunction id as registered by the owning subsystem
     */
    public record ScheduledMalfunction(double atSeconds, String malfunctionId) {
    }
}
