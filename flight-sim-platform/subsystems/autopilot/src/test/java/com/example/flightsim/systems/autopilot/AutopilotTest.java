package com.example.flightsim.systems.autopilot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.flightsim.core.SimContext;
import com.example.flightsim.core.Signals;
import org.junit.jupiter.api.Test;

final class AutopilotTest {

    @Test
    void altitudeHoldCommandsNoseUpTowardHigherTarget() {
        SimContext context = SimContext.create(61L);
        Autopilot autopilot = new Autopilot();
        autopilot.initialise(context);
        context.bus().publish(AutopilotSignals.ENGAGE_COMMAND, true);
        context.bus().publish(AutopilotSignals.SELECTED_ALTITUDE_M, 3_300.0);
        context.bus().publish(Signals.ALTITUDE_M, 3_000.0);
        context.bus().publish(Signals.PITCH_RAD, 0.0);
        context.bus().publish(Signals.ROLL_RAD, 0.0);
        context.bus().publish(Signals.HEADING_RAD, 0.0);
        context.bus().publish(Signals.VERTICAL_SPEED_MPS, 0.0);
        context.bus().publish(Signals.INDICATED_AIRSPEED_MPS, 120.0);

        autopilot.step(context, 0.05);

        assertTrue(context.bus().readFlag(Signals.AUTOPILOT_ENGAGED));
        assertTrue(context.bus().read(Signals.ELEVATOR_CMD) > 0.05);
        assertEquals(AutopilotSignals.MODE_ALTITUDE_HOLD, context.bus().read(AutopilotSignals.VERTICAL_MODE), 0.001);
    }

    @Test
    void disconnectCommandDropsEngagement() {
        SimContext context = SimContext.create(62L);
        Autopilot autopilot = new Autopilot();
        autopilot.initialise(context);
        context.bus().publish(AutopilotSignals.ENGAGE_COMMAND, true);
        autopilot.step(context, 0.05);
        context.bus().publish(Signals.AUTOPILOT_DISCONNECT, true);

        autopilot.step(context, 0.05);

        assertEquals(0.0, context.bus().read(Signals.AUTOPILOT_ENGAGED), 0.001);
    }
}
