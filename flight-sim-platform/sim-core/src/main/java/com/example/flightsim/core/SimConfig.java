package com.example.flightsim.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * Read-only configuration loaded from {@code .properties} files.
 *
 * <p>Typed getters always take a fallback so a missing key never stops a training session;
 * a malformed value, on the other hand, fails fast because it means the file is wrong.
 */
public final class SimConfig {

    private final Properties properties;

    private SimConfig(Properties properties) {
        this.properties = properties;
    }

    /**
     * Returns a configuration with no keys.
     *
     * @return empty configuration
     */
    public static SimConfig empty() {
        return new SimConfig(new Properties());
    }

    /**
     * Builds a configuration from a map, mainly for tests.
     *
     * @param values keys and values
     * @return configuration
     */
    public static SimConfig of(Map<String, String> values) {
        Properties properties = new Properties();
        properties.putAll(values);
        return new SimConfig(properties);
    }

    /**
     * Loads a configuration from the classpath.
     *
     * @param resource resource name, for example {@code /flightsim/defaults.properties}
     * @return configuration
     * @throws IllegalArgumentException if the resource does not exist
     */
    public static SimConfig fromClasspath(String resource) {
        try (InputStream in = SimConfig.class.getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalArgumentException("Configuration resource not found: " + resource);
            }
            Properties properties = new Properties();
            properties.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            return new SimConfig(properties);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read configuration resource " + resource, e);
        }
    }

    /**
     * Loads a configuration from a file.
     *
     * @param path file to read
     * @return configuration
     * @throws IOException if the file cannot be read
     */
    public static SimConfig fromFile(Path path) throws IOException {
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        return new SimConfig(properties);
    }

    /**
     * Returns a copy with one key replaced.
     *
     * @param key   key to set
     * @param value new value
     * @return new configuration
     */
    public SimConfig withOverride(String key, String value) {
        Properties copy = new Properties();
        copy.putAll(properties);
        copy.setProperty(key, value);
        return new SimConfig(copy);
    }

    /**
     * Reads a string.
     *
     * @param key      key
     * @param fallback value if the key is absent
     * @return value
     */
    public String getString(String key, String fallback) {
        String value = properties.getProperty(key);
        return value == null ? fallback : value.trim();
    }

    /**
     * Reads a double.
     *
     * @param key      key
     * @param fallback value if the key is absent
     * @return value
     */
    public double getDouble(String key, double fallback) {
        String value = properties.getProperty(key);
        if (value == null) {
            return fallback;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Configuration key " + key + " is not a number: " + value, e);
        }
    }

    /**
     * Reads an integer.
     *
     * @param key      key
     * @param fallback value if the key is absent
     * @return value
     */
    public int getInt(String key, int fallback) {
        String value = properties.getProperty(key);
        if (value == null) {
            return fallback;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Configuration key " + key + " is not an integer: " + value, e);
        }
    }

    /**
     * Reads a boolean. Accepts {@code true}/{@code false} and {@code yes}/{@code no}.
     *
     * @param key      key
     * @param fallback value if the key is absent
     * @return value
     */
    public boolean getBoolean(String key, boolean fallback) {
        String value = properties.getProperty(key);
        if (value == null) {
            return fallback;
        }
        switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "true":
            case "yes":
                return true;
            case "false":
            case "no":
                return false;
            default:
                throw new IllegalArgumentException("Configuration key " + key + " is not a boolean: " + value);
        }
    }

    /**
     * Tells whether a key is present.
     *
     * @param key key
     * @return true if present
     */
    public boolean contains(String key) {
        return properties.containsKey(key);
    }
}
