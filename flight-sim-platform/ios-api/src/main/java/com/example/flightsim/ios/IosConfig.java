package com.example.flightsim.ios;

import com.example.flightsim.core.SimConfig;
import java.nio.file.Path;

/**
 * Instructor station server settings, from {@code ios.properties} with {@code -D} overrides.
 *
 * @param bindAddress      address the HTTP connector binds to
 * @param port             HTTP port; 0 picks a free port
 * @param trainingDataRoot directory holding {@code scenarios/} and {@code attachments/}
 * @param metarFeed        default live-weather feed
 * @param sessionSeed      random seed for the simulation session
 */
public record IosConfig(String bindAddress, int port, Path trainingDataRoot, String metarFeed, long sessionSeed) {

    /** Resource holding the defaults. */
    public static final String DEFAULTS_RESOURCE = "/ios.properties";

    /**
     * Loads the defaults and applies any {@code -Dios.*} system property overrides.
     *
     * @return configuration
     */
    public static IosConfig load() {
        SimConfig config = SimConfig.fromClasspath(DEFAULTS_RESOURCE);
        for (String key : new String[] {"ios.http.bind", "ios.http.port", "ios.training-data.root", "ios.metar.feed", "ios.session.seed"}) {
            String override = System.getProperty(key);
            if (override != null) {
                config = config.withOverride(key, override);
            }
        }
        return fromConfig(config);
    }

    /**
     * Reads the configuration.
     *
     * @param config configuration with {@code ios.*} keys
     * @return server configuration
     */
    public static IosConfig fromConfig(SimConfig config) {
        return new IosConfig(
                config.getString("ios.http.bind", "127.0.0.1"),
                config.getInt("ios.http.port", 8090),
                Path.of(config.getString("ios.training-data.root", "training-data")),
                config.getString("ios.metar.feed", ""),
                Long.parseLong(config.getString("ios.session.seed", "1")));
    }
}
