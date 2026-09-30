package com.example.flightsim.legacy;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.flightsim.core.math.Vector3;
import com.example.flightsim.core.units.Units;

import java.io.StringReader;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests for the legacy motion cueing and scenario-card parser.
 */
final class LegacyMotionCueingTest {

    @Test
    void washoutReturnsCloseToNeutralAfterSustainedStep() {
        LegacyMotionCueing cueing = new LegacyMotionCueing();
        LegacyMotionCueing.Command command = null;
        for (int i = 0; i < 1200; i++) {
            command = cueing.step(new Vector3(1.2, 0.0, 0.0), Vector3.ZERO, 1.0 / 60.0);
        }

        assertTrue(Math.abs(command.surgeM()) < 0.03);
        assertTrue(Math.abs(command.pitchRad()) < Math.toRadians(12.1));
    }

    @Test
    void washoutRespectsPlatformLimits() {
        LegacyMotionCueing cueing = new LegacyMotionCueing();
        LegacyMotionCueing.Command command = cueing.step(new Vector3(60.0, -60.0, 35.0), new Vector3(8.0, -8.0, 9.0), 0.1);

        assertTrue(Math.abs(command.surgeM()) <= 0.62);
        assertTrue(Math.abs(command.swayM()) <= 0.62);
        assertTrue(Math.abs(command.heaveM()) <= 0.62);
        assertTrue(Math.abs(command.rollRad()) <= Math.toRadians(18.0));
        assertTrue(Math.abs(command.pitchRad()) <= Math.toRadians(18.0));
        assertTrue(Math.abs(command.yawRad()) <= Math.toRadians(18.0));
    }

    @Test
    void parsesValidScenarioCard() throws Exception {
        String card = String.format("%-7s %-3s %7.0f %5.1f %7.0f %5.0f %6.1f %6.1f %7.1f %8.1f",
                "FS2DEMO", "RW3", 54000.0, 24.5, 7800.0, 270.0, 18.0, 10.0, 15.0, 1013.2);
        LegacyScenarioCard parsed = new LegacyScenarioCardParser().parse(new StringReader(card));

        assertEquals("FS2DEMO", parsed.airportIdent());
        assertEquals("RW3", parsed.runway());
        assertEquals(Units.poundsToKg(54000.0), parsed.weightKg(), 0.01);
        assertEquals(24.5, parsed.cgPercentMac(), 0.01);
        assertEquals(Units.knotsToMps(18.0), parsed.windSpeedMps(), 0.001);
    }

    @Test
    void rejectsBadColumnWidths() {
        LegacyScenarioCardParser parser = new LegacyScenarioCardParser();

        assertThrows(IllegalArgumentException.class, () -> parser.parse(List.of("FS2DEMO RW3 54000")));
    }
}
