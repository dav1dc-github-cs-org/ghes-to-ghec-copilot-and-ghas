package com.example.flightsim.scenario;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Converts between the generic map form of a scenario (as read from YAML) and {@link Scenario}.
 *
 * <p>Keys carry their unit, for example {@code altitude_ft}, so a scenario author never has to
 * guess whether a number is feet or metres.
 */
final class ScenarioMapper {

    private ScenarioMapper() {
    }

    static Scenario fromMap(Map<?, ?> document) {
        Map<?, ?> initial = section(document, "initial");
        Map<?, ?> weather = document.containsKey("weather") ? section(document, "weather") : Map.of();

        Scenario.InitialConditions conditions = new Scenario.InitialConditions(
                number(initial, "altitude_ft", 0.0),
                number(initial, "ias_kt", 0.0),
                number(initial, "heading_deg", 0.0),
                number(initial, "fuel_kg", 4000.0),
                number(initial, "gross_mass_kg", 20_000.0),
                bool(initial, "on_ground", false));

        Scenario.Weather standard = Scenario.Weather.standard();
        Scenario.Weather wx = new Scenario.Weather(
                number(weather, "wind_from_deg", standard.windFromDeg()),
                number(weather, "wind_speed_kt", standard.windSpeedKt()),
                number(weather, "visibility_m", standard.visibilityM()),
                number(weather, "cloud_base_ft", standard.cloudBaseFt()),
                number(weather, "temperature_c", standard.temperatureC()),
                number(weather, "qnh_hpa", standard.qnhHpa()));

        List<Scenario.ScheduledMalfunction> malfunctions = new ArrayList<>();
        Object rawMalfunctions = document.get("malfunctions");
        if (rawMalfunctions instanceof List<?> list) {
            for (Object item : list) {
                if (!(item instanceof Map<?, ?> entry)) {
                    throw new ScenarioFormatException("Each malfunction must be a mapping with at_s and id");
                }
                malfunctions.add(new Scenario.ScheduledMalfunction(number(entry, "at_s", 0.0), text(entry, "id", null)));
            }
        }

        List<String> tags = new ArrayList<>();
        Object rawTags = document.get("tags");
        if (rawTags instanceof List<?> list) {
            for (Object tag : list) {
                tags.add(String.valueOf(tag));
            }
        }

        return new Scenario(
                text(document, "id", null),
                text(document, "title", null),
                text(document, "description", ""),
                text(document, "airport", ""),
                text(document, "runway", ""),
                conditions,
                wx,
                malfunctions,
                tags);
    }

    static Map<String, Object> toMap(Scenario scenario) {
        Map<String, Object> document = new LinkedHashMap<>();
        document.put("id", scenario.id());
        document.put("title", scenario.title());
        document.put("description", scenario.description());
        document.put("airport", scenario.airport());
        document.put("runway", scenario.runway());

        Scenario.InitialConditions ic = scenario.initialConditions();
        Map<String, Object> initial = new LinkedHashMap<>();
        initial.put("altitude_ft", ic.altitudeFt());
        initial.put("ias_kt", ic.indicatedAirspeedKt());
        initial.put("heading_deg", ic.headingDeg());
        initial.put("fuel_kg", ic.fuelKg());
        initial.put("gross_mass_kg", ic.grossMassKg());
        initial.put("on_ground", ic.onGround());
        document.put("initial", initial);

        Scenario.Weather w = scenario.weather();
        Map<String, Object> weather = new LinkedHashMap<>();
        weather.put("wind_from_deg", w.windFromDeg());
        weather.put("wind_speed_kt", w.windSpeedKt());
        weather.put("visibility_m", w.visibilityM());
        weather.put("cloud_base_ft", w.cloudBaseFt());
        weather.put("temperature_c", w.temperatureC());
        weather.put("qnh_hpa", w.qnhHpa());
        document.put("weather", weather);

        List<Map<String, Object>> malfunctions = new ArrayList<>();
        for (Scenario.ScheduledMalfunction m : scenario.malfunctions()) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("at_s", m.atSeconds());
            entry.put("id", m.malfunctionId());
            malfunctions.add(entry);
        }
        document.put("malfunctions", malfunctions);
        document.put("tags", new ArrayList<>(scenario.tags()));
        return document;
    }

    private static Map<?, ?> section(Map<?, ?> document, String key) {
        Object value = document.get(key);
        if (!(value instanceof Map<?, ?> map)) {
            throw new ScenarioFormatException("Scenario section '" + key + "' is missing or is not a mapping");
        }
        return map;
    }

    private static double number(Map<?, ?> map, String key, double fallback) {
        Object value = map.get(key);
        if (value == null) {
            return fallback;
        }
        if (value instanceof Number n) {
            return n.doubleValue();
        }
        try {
            return Double.parseDouble(value.toString().trim());
        } catch (NumberFormatException e) {
            throw new ScenarioFormatException("'" + key + "' must be a number but was '" + value + "'", e);
        }
    }

    private static boolean bool(Map<?, ?> map, String key, boolean fallback) {
        Object value = map.get(key);
        if (value == null) {
            return fallback;
        }
        if (value instanceof Boolean b) {
            return b;
        }
        return Boolean.parseBoolean(value.toString().trim());
    }

    private static String text(Map<?, ?> map, String key, String fallback) {
        Object value = map.get(key);
        if (value == null) {
            if (fallback == null) {
                throw new ScenarioFormatException("Required key '" + key + "' is missing");
            }
            return fallback;
        }
        return value.toString().trim();
    }
}
