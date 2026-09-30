package com.example.flightsim.legacy;

import com.example.flightsim.core.units.Units;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * Parser for the legacy fixed-width text scenario card format.
 */
public final class LegacyScenarioCardParser {

    private static final int CARD_WIDTH = 70;

    /**
     * Parses a card from a reader.
     *
     * @param reader reader supplying at least one card line
     * @return parsed card
     * @throws IOException if the reader fails
     */
    public LegacyScenarioCard parse(Reader reader) throws IOException {
        try (BufferedReader buffered = new BufferedReader(reader)) {
            String line = buffered.readLine();
            if (line == null) {
                throw new IllegalArgumentException("Scenario card is empty");
            }
            return parseLine(line);
        }
    }

    /**
     * Parses a card from lines already held in memory.
     *
     * @param lines source lines
     * @return parsed card
     */
    public LegacyScenarioCard parse(List<String> lines) {
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("Scenario card is empty");
        }
        return parseLine(lines.get(0));
    }

    private LegacyScenarioCard parseLine(String line) {
        if (line.length() != CARD_WIDTH) {
            throw new IllegalArgumentException("Scenario card must be exactly " + CARD_WIDTH + " columns but was " + line.length());
        }
        requireSpaces(line, 7, 8);
        requireSpaces(line, 11, 12);
        requireSpaces(line, 19, 20);
        requireSpaces(line, 25, 26);
        requireSpaces(line, 33, 34);
        requireSpaces(line, 39, 40);
        requireSpaces(line, 46, 47);
        requireSpaces(line, 53, 54);
        requireSpaces(line, 61, 62);
        String airport = field(line, 0, 7);
        String runway = field(line, 8, 11);
        double weightKg = Units.poundsToKg(number(line, 12, 19, "weight"));
        double cg = number(line, 20, 25, "cg");
        double fuelKg = Units.poundsToKg(number(line, 26, 33, "fuel"));
        double windFromRad = Units.degreesToRadians(number(line, 34, 39, "wind direction"));
        double windSpeedMps = Units.knotsToMps(number(line, 40, 46, "wind speed"));
        double visibilityM = Units.nauticalMilesToMetres(number(line, 47, 53, "visibility"));
        double oatK = Units.celsiusToKelvin(number(line, 54, 61, "temperature"));
        double altimeterPa = Units.hectopascalsToPascals(number(line, 62, 70, "altimeter"));
        validatePositive(weightKg, "weight");
        validatePositive(fuelKg, "fuel");
        return new LegacyScenarioCard(airport, runway, weightKg, cg, fuelKg, windFromRad, windSpeedMps, visibilityM, oatK, altimeterPa);
    }

    private static String field(String line, int start, int end) {
        String value = line.substring(start, end).trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Required scenario card field is blank at columns " + start + "-" + (end - 1));
        }
        return value;
    }

    private static double number(String line, int start, int end, String name) {
        String value = field(line, start, end);
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Scenario card " + name + " is not numeric: " + value, e);
        }
    }

    private static void requireSpaces(String line, int start, int end) {
        for (int i = start; i < end; i++) {
            if (line.charAt(i) != ' ') {
                throw new IllegalArgumentException("Scenario card column " + i + " must be a separator space");
            }
        }
    }

    private static void validatePositive(double value, String name) {
        if (value <= 0.0) {
            throw new IllegalArgumentException("Scenario card " + name + " must be positive");
        }
    }
}
