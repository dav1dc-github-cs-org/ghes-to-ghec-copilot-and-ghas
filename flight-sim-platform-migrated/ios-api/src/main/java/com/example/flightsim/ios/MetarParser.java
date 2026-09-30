package com.example.flightsim.ios;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses the parts of a METAR the simulator uses to set up weather: wind, visibility, the lowest
 * broken or overcast layer, temperature and QNH.
 *
 * <p>Example: {@code METAR XFSA 301250Z 25012G20KT 9999 FEW025 BKN040 18/11 Q1009 NOSIG}.
 * Groups are read one whitespace-separated token at a time.
 */
public final class MetarParser {

    private static final Pattern STATION = Pattern.compile("[A-Z][A-Z0-9]{3}");
    private static final Pattern WIND = Pattern.compile("(\\d{3}|VRB)(\\d{2,3})(?:G(\\d{2,3}))?KT");
    private static final Pattern VISIBILITY = Pattern.compile("\\d{4}");
    private static final Pattern CLOUD = Pattern.compile("(FEW|SCT|BKN|OVC)(\\d{3})(?:CB|TCU)?");
    private static final Pattern TEMPERATURE = Pattern.compile("(M?\\d{2})/(M?\\d{2})?");
    private static final Pattern QNH = Pattern.compile("Q(\\d{4})");

    /**
     * Parses a METAR.
     *
     * @param metar raw METAR text
     * @return parsed report; fields not present keep ISA/unlimited defaults
     * @throws IllegalArgumentException if the text does not identify a station
     */
    public MetarReport parse(String metar) {
        String[] tokens = metar.trim().split("\\s+");
        int index = 0;
        if (index < tokens.length && ("METAR".equals(tokens[index]) || "SPECI".equals(tokens[index]))) {
            index++;
        }
        if (index >= tokens.length || !STATION.matcher(tokens[index]).matches()) {
            throw new IllegalArgumentException("METAR does not start with a station identifier");
        }
        String station = tokens[index++];

        double windFrom = 0.0;
        double windSpeed = 0.0;
        double gust = 0.0;
        double visibility = 10_000.0;
        double cloudBase = 25_000.0;
        double temperature = 15.0;
        double qnh = 1013.25;

        for (; index < tokens.length; index++) {
            String token = tokens[index];
            Matcher m;
            if ((m = WIND.matcher(token)).matches()) {
                windFrom = "VRB".equals(m.group(1)) ? 0.0 : Double.parseDouble(m.group(1));
                windSpeed = Double.parseDouble(m.group(2));
                gust = m.group(3) == null ? 0.0 : Double.parseDouble(m.group(3));
            } else if ("CAVOK".equals(token)) {
                visibility = 10_000.0;
            } else if (VISIBILITY.matcher(token).matches()) {
                visibility = Math.min(10_000.0, Double.parseDouble(token));
            } else if ((m = CLOUD.matcher(token)).matches()) {
                boolean ceiling = "BKN".equals(m.group(1)) || "OVC".equals(m.group(1));
                double base = Double.parseDouble(m.group(2)) * 100.0;
                if (ceiling && base < cloudBase) {
                    cloudBase = base;
                }
            } else if ((m = TEMPERATURE.matcher(token)).matches()) {
                temperature = signed(m.group(1));
            } else if ((m = QNH.matcher(token)).matches()) {
                qnh = Double.parseDouble(m.group(1));
            }
        }
        return new MetarReport(station, windFrom, windSpeed, gust, visibility, cloudBase, temperature, qnh);
    }

    private static double signed(String value) {
        return value.startsWith("M") ? -Double.parseDouble(value.substring(1)) : Double.parseDouble(value);
    }

    /**
     * Weather extracted from a METAR, in the units used by scenario files.
     *
     * @param station       ICAO station
     * @param windFromDeg   wind direction, degrees; 0 when variable or calm
     * @param windSpeedKt   wind speed, knots
     * @param gustKt        gust speed, knots; 0 when not reported
     * @param visibilityM   visibility, metres, capped at 10 km
     * @param cloudBaseFt   lowest broken or overcast layer, feet
     * @param temperatureC  temperature, degrees Celsius
     * @param qnhHpa        altimeter setting, hectopascals
     */
    public record MetarReport(
            String station,
            double windFromDeg,
            double windSpeedKt,
            double gustKt,
            double visibilityM,
            double cloudBaseFt,
            double temperatureC,
            double qnhHpa) {
    }
}
