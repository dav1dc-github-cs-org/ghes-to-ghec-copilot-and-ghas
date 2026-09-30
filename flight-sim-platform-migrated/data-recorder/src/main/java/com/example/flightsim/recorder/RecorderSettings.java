package com.example.flightsim.recorder;

import com.example.flightsim.core.SimConfig;
import java.util.Arrays;
import java.util.List;

/**
 * Settings for the flight data recorder and its session database.
 *
 * @param jdbcUrl       JDBC URL of the session database
 * @param user          database user
 * @param password      database password
 * @param sampleRateHz  how often signals are sampled
 * @param retentionDays how long recordings are kept
 * @param signals       signals to record
 */
public record RecorderSettings(
        String jdbcUrl,
        String user,
        String password,
        int sampleRateHz,
        int retentionDays,
        List<String> signals) {

    /** Resource holding the defaults shipped with the instructor station. */
    public static final String DEFAULTS_RESOURCE = "/recorder.properties";

    /** Canonical constructor; copies the signal list. */
    public RecorderSettings {
        signals = List.copyOf(signals);
        if (sampleRateHz <= 0) {
            throw new IllegalArgumentException("Sample rate must be positive: " + sampleRateHz);
        }
    }

    /**
     * Loads the settings shipped with the instructor station.
     *
     * @return settings
     */
    public static RecorderSettings defaults() {
        return fromConfig(SimConfig.fromClasspath(DEFAULTS_RESOURCE));
    }

    /**
     * Reads settings from a configuration.
     *
     * @param config configuration with {@code recorder.*} keys
     * @return settings
     */
    public static RecorderSettings fromConfig(SimConfig config) {
        String signalList = config.getString("recorder.signals", "");
        List<String> signals = signalList.isBlank()
                ? List.of()
                : Arrays.stream(signalList.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
        return new RecorderSettings(
                config.getString("recorder.jdbc.url", "jdbc:h2:mem:recorder"),
                config.getString("recorder.jdbc.user", "sa"),
                config.getString("recorder.jdbc.password", ""),
                config.getInt("recorder.sample.rate.hz", 20),
                config.getInt("recorder.retention.days", 90),
                signals);
    }

    /**
     * Returns a copy pointing at another database, for tests and the debrief station.
     *
     * @param url JDBC URL
     * @return new settings
     */
    public RecorderSettings withJdbcUrl(String url) {
        return new RecorderSettings(url, user, password, sampleRateHz, retentionDays, signals);
    }

    /** Never print the password. */
    @Override
    public String toString() {
        return "RecorderSettings[jdbcUrl=" + jdbcUrl + ", user=" + user + ", sampleRateHz=" + sampleRateHz
                + ", retentionDays=" + retentionDays + ", signals=" + signals + "]";
    }
}
