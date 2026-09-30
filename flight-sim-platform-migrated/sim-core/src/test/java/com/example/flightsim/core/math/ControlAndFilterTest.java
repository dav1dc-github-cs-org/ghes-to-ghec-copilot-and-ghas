package com.example.flightsim.core.math;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ControlAndFilterTest {

    @Test
    void firstOrderLagReachesAboutSixtyThreePercentAfterOneTimeConstant() {
        FirstOrderLag lag = new FirstOrderLag(2.0, 0.0);
        for (int i = 0; i < 120; i++) {
            lag.update(1.0, 2.0 / 120.0);
        }
        assertEquals(1.0 - Math.exp(-1.0), lag.output(), 1e-9);
    }

    @Test
    void rateLimiterNeverExceedsItsRate() {
        RateLimiter limiter = new RateLimiter(10.0, 0.0);
        assertEquals(1.0, limiter.update(100.0, 0.1), 1e-12);
        assertEquals(2.0, limiter.update(100.0, 0.1), 1e-12);
        assertEquals(1.5, limiter.update(1.5, 0.1), 1e-12);
    }

    @Test
    void pidDrivesAFirstOrderPlantToTheSetpoint() {
        PidController pid = new PidController(2.0, 0.5, 0.0, -1.0, 1.0);
        double value = 0.0;
        for (int i = 0; i < 2000; i++) {
            double command = pid.update(1.0, value, 0.01);
            value += command * 0.01;
        }
        assertEquals(1.0, value, 1e-3);
    }

    @Test
    void anglesWrapTheShortWayRound() {
        double from = Math.toRadians(350.0);
        double to = Math.toRadians(10.0);
        assertEquals(Math.toRadians(20.0), Angles.difference(from, to), 1e-12);
        assertTrue(Angles.wrapTwoPi(-0.1) > 6.0);
    }
}
