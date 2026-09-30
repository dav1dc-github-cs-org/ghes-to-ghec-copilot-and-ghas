package com.example.flightsim.fdm;

/**
 * International Standard Atmosphere model for the FS-200 training envelope.
 *
 * <p>The model covers the troposphere and lower stratosphere used by the simulator. Inputs outside
 * the supported range are clamped so downstream aerodynamic code receives finite, plausible values.
 */
public final class Atmosphere {

    /** Sea-level standard temperature, K. */
    public static final double SEA_LEVEL_TEMPERATURE_K = 288.15;
    /** Sea-level standard pressure, Pa. */
    public static final double SEA_LEVEL_PRESSURE_PA = 101325.0;
    /** Sea-level standard density, kg/m3. */
    public static final double SEA_LEVEL_DENSITY_KGPM3 = 1.225;

    private static final double MIN_ALTITUDE_M = -1000.0;
    private static final double TROPOPAUSE_M = 11000.0;
    private static final double MAX_ALTITUDE_M = 20000.0;
    private static final double LAPSE_RATE_KPM = -0.0065;
    private static final double GAS_CONSTANT_AIR = 287.05287;
    private static final double GAMMA = 1.4;
    private static final double GRAVITY = 9.80665;
    private static final double TROPOPAUSE_TEMPERATURE_K = SEA_LEVEL_TEMPERATURE_K + LAPSE_RATE_KPM * TROPOPAUSE_M;
    private static final double TROPOPAUSE_PRESSURE_PA = SEA_LEVEL_PRESSURE_PA
            * Math.pow(TROPOPAUSE_TEMPERATURE_K / SEA_LEVEL_TEMPERATURE_K, -GRAVITY / (LAPSE_RATE_KPM * GAS_CONSTANT_AIR));

    private Atmosphere() {
    }

    /**
     * Computes standard-day air properties.
     *
     * @param altitudeM pressure altitude above mean sea level
     * @return air properties at the clamped altitude
     */
    public static Sample at(double altitudeM) {
        return at(altitudeM, 0.0);
    }

    /**
     * Computes air properties with an ISA temperature deviation.
     *
     * <p>Pressure follows the standard atmosphere at the pressure altitude; density uses the
     * deviated temperature, matching common flight simulator practice.
     *
     * @param altitudeM       pressure altitude above mean sea level
     * @param isaDeviationK   temperature deviation from ISA
     * @return air properties at the clamped altitude
     */
    public static Sample at(double altitudeM, double isaDeviationK) {
        double h = clamp(altitudeM, MIN_ALTITUDE_M, MAX_ALTITUDE_M);
        double standardTemperature;
        double pressure;
        if (h <= TROPOPAUSE_M) {
            standardTemperature = SEA_LEVEL_TEMPERATURE_K + LAPSE_RATE_KPM * h;
            pressure = SEA_LEVEL_PRESSURE_PA
                    * Math.pow(standardTemperature / SEA_LEVEL_TEMPERATURE_K, -GRAVITY / (LAPSE_RATE_KPM * GAS_CONSTANT_AIR));
        } else {
            standardTemperature = TROPOPAUSE_TEMPERATURE_K;
            pressure = TROPOPAUSE_PRESSURE_PA * Math.exp(-GRAVITY * (h - TROPOPAUSE_M) / (GAS_CONSTANT_AIR * standardTemperature));
        }
        double temperature = Math.max(150.0, standardTemperature + isaDeviationK);
        double density = pressure / (GAS_CONSTANT_AIR * temperature);
        double speedOfSound = Math.sqrt(GAMMA * GAS_CONSTANT_AIR * temperature);
        return new Sample(h, temperature, pressure, density, speedOfSound);
    }

    static double dynamicPressureToCas(double dynamicPressurePa) {
        return Math.sqrt(Math.max(0.0, 2.0 * dynamicPressurePa / SEA_LEVEL_DENSITY_KGPM3));
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Atmosphere sample.
     *
     * @param altitudeM        clamped pressure altitude
     * @param temperatureK     static temperature
     * @param pressurePa       static pressure
     * @param densityKgPerM3   density
     * @param speedOfSoundMps  local speed of sound
     */
    public record Sample(double altitudeM, double temperatureK, double pressurePa, double densityKgPerM3, double speedOfSoundMps) {
    }
}
