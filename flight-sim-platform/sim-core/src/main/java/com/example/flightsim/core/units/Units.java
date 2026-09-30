package com.example.flightsim.core.units;

/**
 * Unit conversions. The platform works in SI internally; convert at the edges only — when
 * reading instructor input, loading scenario files or formatting values for display.
 *
 * <p>Angles are radians everywhere inside the simulation. Airspeeds on the instructor station
 * are knots, altitudes are feet and vertical speeds are feet per minute.
 */
public final class Units {

    /** Metres per second in one knot (exactly 1852 m per hour). */
    public static final double MPS_PER_KNOT = 1852.0 / 3600.0;
    /** Metres in one foot (exact). */
    public static final double METRES_PER_FOOT = 0.3048;
    /** Metres in one nautical mile (exact). */
    public static final double METRES_PER_NAUTICAL_MILE = 1852.0;
    /** Kilograms in one pound (exact). */
    public static final double KG_PER_POUND = 0.45359237;
    /** Pascals in one pound per square inch. */
    public static final double PASCALS_PER_PSI = 6894.757293168;
    /** Pascals in one hectopascal (millibar). */
    public static final double PASCALS_PER_HECTOPASCAL = 100.0;
    /** Radians in one degree. */
    public static final double RADIANS_PER_DEGREE = Math.PI / 180.0;
    /** Kelvin offset from Celsius. */
    public static final double KELVIN_OFFSET = 273.15;
    /** Standard gravity, m/s&sup2;. */
    public static final double STANDARD_GRAVITY = 9.80665;

    private Units() {
    }

    /**
     * Converts knots to metres per second.
     *
     * @param knots speed in knots
     * @return speed in m/s
     */
    public static double knotsToMps(double knots) {
        return knots * MPS_PER_KNOT;
    }

    /**
     * Converts metres per second to knots.
     *
     * @param mps speed in m/s
     * @return speed in knots
     */
    public static double mpsToKnots(double mps) {
        return mps / MPS_PER_KNOT;
    }

    /**
     * Converts feet to metres.
     *
     * @param feet length in feet
     * @return length in metres
     */
    public static double feetToMetres(double feet) {
        return feet * METRES_PER_FOOT;
    }

    /**
     * Converts metres to feet.
     *
     * @param metres length in metres
     * @return length in feet
     */
    public static double metresToFeet(double metres) {
        return metres / METRES_PER_FOOT;
    }

    /**
     * Converts feet per minute to metres per second.
     *
     * @param fpm vertical speed in ft/min
     * @return vertical speed in m/s
     */
    public static double fpmToMps(double fpm) {
        return fpm * METRES_PER_FOOT / 60.0;
    }

    /**
     * Converts metres per second to feet per minute.
     *
     * @param mps vertical speed in m/s
     * @return vertical speed in ft/min
     */
    public static double mpsToFpm(double mps) {
        return mps * 60.0 / METRES_PER_FOOT;
    }

    /**
     * Converts nautical miles to metres.
     *
     * @param nm distance in nautical miles
     * @return distance in metres
     */
    public static double nauticalMilesToMetres(double nm) {
        return nm * METRES_PER_NAUTICAL_MILE;
    }

    /**
     * Converts metres to nautical miles.
     *
     * @param metres distance in metres
     * @return distance in nautical miles
     */
    public static double metresToNauticalMiles(double metres) {
        return metres / METRES_PER_NAUTICAL_MILE;
    }

    /**
     * Converts pounds to kilograms.
     *
     * @param pounds mass in pounds
     * @return mass in kilograms
     */
    public static double poundsToKg(double pounds) {
        return pounds * KG_PER_POUND;
    }

    /**
     * Converts kilograms to pounds.
     *
     * @param kg mass in kilograms
     * @return mass in pounds
     */
    public static double kgToPounds(double kg) {
        return kg / KG_PER_POUND;
    }

    /**
     * Converts pounds per square inch to pascals.
     *
     * @param psi pressure in psi
     * @return pressure in pascals
     */
    public static double psiToPascals(double psi) {
        return psi * PASCALS_PER_PSI;
    }

    /**
     * Converts pascals to pounds per square inch.
     *
     * @param pascals pressure in pascals
     * @return pressure in psi
     */
    public static double pascalsToPsi(double pascals) {
        return pascals / PASCALS_PER_PSI;
    }

    /**
     * Converts hectopascals (millibars) to pascals.
     *
     * @param hpa pressure in hPa
     * @return pressure in pascals
     */
    public static double hectopascalsToPascals(double hpa) {
        return hpa * PASCALS_PER_HECTOPASCAL;
    }

    /**
     * Converts degrees to radians.
     *
     * @param degrees angle in degrees
     * @return angle in radians
     */
    public static double degreesToRadians(double degrees) {
        return degrees * RADIANS_PER_DEGREE;
    }

    /**
     * Converts radians to degrees.
     *
     * @param radians angle in radians
     * @return angle in degrees
     */
    public static double radiansToDegrees(double radians) {
        return radians / RADIANS_PER_DEGREE;
    }

    /**
     * Converts Celsius to kelvin.
     *
     * @param celsius temperature in &deg;C
     * @return temperature in K
     */
    public static double celsiusToKelvin(double celsius) {
        return celsius + KELVIN_OFFSET;
    }

    /**
     * Converts kelvin to Celsius.
     *
     * @param kelvin temperature in K
     * @return temperature in &deg;C
     */
    public static double kelvinToCelsius(double kelvin) {
        return kelvin - KELVIN_OFFSET;
    }
}
