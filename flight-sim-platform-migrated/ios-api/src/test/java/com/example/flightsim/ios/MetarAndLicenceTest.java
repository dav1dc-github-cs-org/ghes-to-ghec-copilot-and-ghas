package com.example.flightsim.ios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class MetarAndLicenceTest {

    private final MetarParser parser = new MetarParser();

    @Test
    void parsesWindVisibilityCeilingTemperatureAndQnh() {
        MetarParser.MetarReport report = parser.parse("METAR XFSA 301250Z 25012G20KT 9999 FEW025 BKN040 18/11 Q1009 NOSIG");
        assertEquals("XFSA", report.station());
        assertEquals(250.0, report.windFromDeg());
        assertEquals(12.0, report.windSpeedKt());
        assertEquals(20.0, report.gustKt());
        assertEquals(9999.0, report.visibilityM());
        assertEquals(4000.0, report.cloudBaseFt());
        assertEquals(18.0, report.temperatureC());
        assertEquals(1009.0, report.qnhHpa());
    }

    @Test
    void handlesNegativeTemperaturesAndVariableWind() {
        MetarParser.MetarReport report = parser.parse("XFSB 020550Z VRB03KT 0800 FG OVC002 M02/M03 Q1027");
        assertEquals(0.0, report.windFromDeg());
        assertEquals(800.0, report.visibilityM());
        assertEquals(200.0, report.cloudBaseFt());
        assertEquals(-2.0, report.temperatureC());
    }

    @Test
    void rejectsTextWithoutAStation() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse("NIL"));
    }

    @Test
    void installedLicenceIsReadWithoutExposingTheKey() {
        LicenceCheck.LicenceInfo licence = LicenceCheck.load(LicenceCheck.DEFAULT_RESOURCE, LocalDate.of(2026, 10, 8));
        assertEquals("TC2", licence.site());
        assertEquals(4, licence.seats());
        assertTrue(licence.valid());
        assertFalse(licence.toString().contains("FSLIC"));
        assertFalse(LicenceCheck.load(LicenceCheck.DEFAULT_RESOURCE, LocalDate.of(2027, 4, 1)).valid());
    }
}
