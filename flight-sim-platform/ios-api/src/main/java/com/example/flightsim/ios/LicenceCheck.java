package com.example.flightsim.ios;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Reads the instructor station licence installed with the software.
 *
 * <p>The licence key is checked for shape only; the licensing desk validates it when the site
 * renews. The key itself is never logged or returned by the API.
 */
public final class LicenceCheck {

    /** Where the licence file is installed. */
    public static final String DEFAULT_RESOURCE = "/licensing/instructor-station.lic";

    private static final Pattern KEY_FORMAT = Pattern.compile("FSLIC-\\d{4}(?:-[A-Z0-9]{4}){4}");

    private LicenceCheck() {
    }

    /**
     * Loads and checks a licence.
     *
     * @param resource classpath resource
     * @param today    date to check the expiry against
     * @return licence details, without the key
     */
    public static LicenceInfo load(String resource, LocalDate today) {
        Properties licence = new Properties();
        try (InputStream in = LicenceCheck.class.getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalStateException("No instructor station licence installed at " + resource);
            }
            licence.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException("Could not read the instructor station licence", e);
        }
        String key = licence.getProperty("licence.key", "");
        if (!KEY_FORMAT.matcher(key).matches()) {
            throw new IllegalStateException("The instructor station licence key is malformed");
        }
        LocalDate expires = LocalDate.parse(licence.getProperty("expires", "1970-01-01"));
        return new LicenceInfo(
                licence.getProperty("site", "unknown"),
                Integer.parseInt(licence.getProperty("seats", "1")),
                expires,
                !today.isAfter(expires));
    }

    /**
     * Licence details that are safe to show.
     *
     * @param site    training centre
     * @param seats   number of instructor stations licensed
     * @param expires last valid day
     * @param valid   true if not expired
     */
    public record LicenceInfo(String site, int seats, LocalDate expires, boolean valid) {
    }
}
