package com.example.flightsim.systems.navigation;

import com.example.flightsim.core.SimComponent;
import com.example.flightsim.core.SimContext;
import com.example.flightsim.core.Signals;
import com.example.flightsim.core.math.Angles;
import com.example.flightsim.core.units.Units;

/**
 * Navigation receivers for fictional VOR/DME and ILS stations using great-circle geometry.
 */
public final class Navigation implements SimComponent {

    /** NAV1 receiver failure. */
    public static final String NAV_1_FAIL = "navigation.nav1-fail";
    /** NAV2 receiver failure. */
    public static final String NAV_2_FAIL = "navigation.nav2-fail";
    /** DME receiver failure. */
    public static final String DME_FAIL = "navigation.dme-fail";
    /** Glideslope transmitter off. */
    public static final String GLIDESLOPE_OFF = "navigation.glideslope-off";

    private static final double EARTH_RADIUS_M = 6_371_000.0;
    private static final double VOR_RANGE_NM = 160.0;
    private static final double ILS_RANGE_NM = 25.0;
    private static final double LOCALIZER_FULL_SCALE_RAD = Units.degreesToRadians(2.5);
    private static final double GLIDESLOPE_FULL_SCALE_RAD = Units.degreesToRadians(0.7);
    private static final Navaid[] NAVAIDS = {
        new Navaid("FSA", Type.VOR_DME, 113.20, Units.degreesToRadians(40.000), Units.degreesToRadians(-75.000), 0.0),
        new Navaid("FSB", Type.VOR_DME, 116.40, Units.degreesToRadians(40.850), Units.degreesToRadians(-74.160), 0.0),
        new Navaid("IFS", Type.ILS, 109.10, Units.degreesToRadians(40.020), Units.degreesToRadians(-75.120),
                Units.degreesToRadians(90.0)),
        new Navaid("IWE", Type.ILS, 110.30, Units.degreesToRadians(39.920), Units.degreesToRadians(-74.920),
                Units.degreesToRadians(270.0))
    };

    @Override
    public String name() {
        return "navigation";
    }

    @Override
    public void initialise(SimContext context) {
        context.malfunctions().register(NAV_1_FAIL, "NAV1 receiver failed", 34);
        context.malfunctions().register(NAV_2_FAIL, "NAV2 receiver failed", 34);
        context.malfunctions().register(DME_FAIL, "DME receiver failed", 34);
        context.malfunctions().register(GLIDESLOPE_OFF, "ILS glideslope transmitter off", 34);
    }

    @Override
    public void step(SimContext context, double dtSeconds) {
        double latitude = context.bus().read(Signals.LATITUDE_RAD, Units.degreesToRadians(40.0));
        double longitude = context.bus().read(Signals.LONGITUDE_RAD, Units.degreesToRadians(-75.0));
        double altitude = context.bus().read(Signals.ALTITUDE_M, 0.0);
        double heading = context.bus().read(Signals.HEADING_RAD, 0.0);
        stepReceiver(context, 1, latitude, longitude, altitude, heading);
        stepReceiver(context, 2, latitude, longitude, altitude, heading);
    }

    private void stepReceiver(SimContext context, int receiver, double latitude, double longitude, double altitude, double heading) {
        String prefix = "nav." + receiver + ".";
        double tunedFrequency = context.bus().read(prefix + "freq.mhz", 0.0);
        Navaid station = findStation(tunedFrequency);
        boolean receiverFailed = context.malfunctions().isActive(receiver == 1 ? NAV_1_FAIL : NAV_2_FAIL);
        if (station == null || receiverFailed) {
            publishInvalid(context, prefix);
            return;
        }
        double distanceM = distanceMetres(latitude, longitude, station.latitudeRad(), station.longitudeRad());
        double distanceNm = Units.metresToNauticalMiles(distanceM);
        double bearingToStation = initialBearing(latitude, longitude, station.latitudeRad(), station.longitudeRad());
        double bearingFromStation = initialBearing(station.latitudeRad(), station.longitudeRad(), latitude, longitude);
        boolean valid = inRange(station, distanceNm, altitude);
        double selectedCourse = context.bus().read(prefix + NavigationSignals.COURSE_RAD,
                station.type() == Type.ILS ? station.courseRad() : heading);
        double courseDeviation = courseDeviationDots(station, selectedCourse, bearingFromStation);
        double glideslopeDeviation = context.malfunctions().isActive(GLIDESLOPE_OFF)
                ? 0.0
                : glideslopeDeviationDots(station, distanceM, altitude);

        context.bus().publish(prefix + NavigationSignals.COURSE_DEVIATION_DOTS, valid ? courseDeviation : 0.0);
        context.bus().publish(prefix + NavigationSignals.GLIDESLOPE_DEVIATION_DOTS, valid ? glideslopeDeviation : 0.0);
        context.bus().publish(prefix + NavigationSignals.DME_NM,
                valid && !context.malfunctions().isActive(DME_FAIL) ? distanceNm : 0.0);
        context.bus().publish(prefix + NavigationSignals.BEARING_RAD, valid ? bearingToStation : 0.0);
        context.bus().publish(prefix + NavigationSignals.VALID, valid);
    }

    private static Navaid findStation(double frequencyMhz) {
        for (Navaid station : NAVAIDS) {
            if (Math.abs(station.frequencyMhz() - frequencyMhz) < 0.005) {
                return station;
            }
        }
        return null;
    }

    private static boolean inRange(Navaid station, double distanceNm, double altitudeM) {
        double rangeNm = station.type() == Type.ILS ? ILS_RANGE_NM : VOR_RANGE_NM;
        double radioHorizonNm = 1.23 * Math.sqrt(Math.max(0.0, Units.metresToFeet(altitudeM)) + 100.0) + 12.0;
        return distanceNm <= Math.min(rangeNm, radioHorizonNm);
    }

    private static double courseDeviationDots(Navaid station, double selectedCourse, double bearingFromStation) {
        double reference = station.type() == Type.ILS ? station.courseRad() : selectedCourse;
        double angularError = Angles.difference(reference, bearingFromStation);
        return clamp(angularError / LOCALIZER_FULL_SCALE_RAD * 2.5, -2.5, 2.5);
    }

    private static double glideslopeDeviationDots(Navaid station, double distanceM, double altitudeM) {
        if (station.type() != Type.ILS || distanceM < 1.0) {
            return 0.0;
        }
        double desiredPath = Units.degreesToRadians(3.0);
        double actualPath = Math.atan2(altitudeM, distanceM);
        return clamp((desiredPath - actualPath) / GLIDESLOPE_FULL_SCALE_RAD * 2.5, -2.5, 2.5);
    }

    private static void publishInvalid(SimContext context, String prefix) {
        context.bus().publish(prefix + NavigationSignals.COURSE_DEVIATION_DOTS, 0.0);
        context.bus().publish(prefix + NavigationSignals.GLIDESLOPE_DEVIATION_DOTS, 0.0);
        context.bus().publish(prefix + NavigationSignals.DME_NM, 0.0);
        context.bus().publish(prefix + NavigationSignals.BEARING_RAD, 0.0);
        context.bus().publish(prefix + NavigationSignals.VALID, false);
    }

    /**
     * Computes great-circle distance with the haversine formula.
     *
     * @param lat1Rad first latitude
     * @param lon1Rad first longitude
     * @param lat2Rad second latitude
     * @param lon2Rad second longitude
     * @return distance in metres
     */
    public static double distanceMetres(double lat1Rad, double lon1Rad, double lat2Rad, double lon2Rad) {
        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;
        double sinLat = Math.sin(dLat / 2.0);
        double sinLon = Math.sin(dLon / 2.0);
        double a = sinLat * sinLat + Math.cos(lat1Rad) * Math.cos(lat2Rad) * sinLon * sinLon;
        return EARTH_RADIUS_M * 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));
    }

    /**
     * Computes the initial great-circle bearing from one point to another.
     *
     * @param lat1Rad first latitude
     * @param lon1Rad first longitude
     * @param lat2Rad second latitude
     * @param lon2Rad second longitude
     * @return bearing in radians, wrapped to [0, 2pi)
     */
    public static double initialBearing(double lat1Rad, double lon1Rad, double lat2Rad, double lon2Rad) {
        double dLon = lon2Rad - lon1Rad;
        double y = Math.sin(dLon) * Math.cos(lat2Rad);
        double x = Math.cos(lat1Rad) * Math.sin(lat2Rad)
                - Math.sin(lat1Rad) * Math.cos(lat2Rad) * Math.cos(dLon);
        return Angles.wrapTwoPi(Math.atan2(y, x));
    }

    private static double clamp(double value, double minimum, double maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    private enum Type {
        VOR_DME,
        ILS
    }

    private record Navaid(String ident, Type type, double frequencyMhz, double latitudeRad, double longitudeRad, double courseRad) {
    }
}
