package com.example.flightsim.systems.autopilot;

import com.example.flightsim.core.SimComponent;
import com.example.flightsim.core.SimContext;
import com.example.flightsim.core.Signals;
import com.example.flightsim.core.math.Angles;
import com.example.flightsim.core.math.PidController;
import com.example.flightsim.core.units.Units;

/**
 * Two-axis autopilot with pitch, altitude, vertical-speed, wings-level and heading-hold modes.
 */
public final class Autopilot implements SimComponent {

    /** Fault that prevents engagement and commands disconnect. */
    public static final String DISCONNECT_FAULT = "autopilot.disconnect-fault";
    /** Pitch servo runaway fault. */
    public static final String SERVO_RUNAWAY = "autopilot.servo-runaway";

    private static final double BANK_LIMIT_RAD = Units.degreesToRadians(25.0);
    private static final double PITCH_UP_LIMIT_RAD = Units.degreesToRadians(12.0);
    private static final double PITCH_DOWN_LIMIT_RAD = Units.degreesToRadians(-8.0);
    private static final double DEFAULT_IAS_MPS = 115.0;

    private final PidController pitchController = new PidController(2.2, 0.08, 0.4, -1.0, 1.0);
    private final PidController rollController = new PidController(2.0, 0.05, 0.25, -1.0, 1.0);
    private double capturedPitchRad;
    private double capturedAltitudeM;
    private double capturedHeadingRad;
    private boolean engaged;

    @Override
    public String name() {
        return "autopilot";
    }

    @Override
    public void initialise(SimContext context) {
        context.malfunctions().register(DISCONNECT_FAULT, "Autopilot disconnect fault", 22);
        context.malfunctions().register(SERVO_RUNAWAY, "Pitch servo runaway", 22);
    }

    @Override
    public void step(SimContext context, double dtSeconds) {
        boolean disconnect = context.bus().readFlag(Signals.AUTOPILOT_DISCONNECT)
                || context.malfunctions().isActive(DISCONNECT_FAULT);
        if (disconnect) {
            disengage(context);
            return;
        }
        if (!engaged && context.bus().readFlag(AutopilotSignals.ENGAGE_COMMAND)) {
            engage(context);
        }
        if (!engaged) {
            publishDisengaged(context);
            return;
        }

        double pitch = context.bus().read(Signals.PITCH_RAD, 0.0);
        double roll = context.bus().read(Signals.ROLL_RAD, 0.0);
        double heading = context.bus().read(Signals.HEADING_RAD, 0.0);
        double altitude = context.bus().read(Signals.ALTITUDE_M, 0.0);
        double verticalSpeed = context.bus().read(Signals.VERTICAL_SPEED_MPS, 0.0);
        double airspeed = Math.max(40.0, context.bus().read(Signals.INDICATED_AIRSPEED_MPS, DEFAULT_IAS_MPS));

        VerticalCommand vertical = verticalCommand(context, pitch, altitude, verticalSpeed, airspeed, dtSeconds);
        LateralCommand lateral = lateralCommand(context, roll, heading, dtSeconds);
        double elevator = vertical.elevator();
        if (context.malfunctions().isActive(SERVO_RUNAWAY)) {
            elevator = 1.0;
        }
        context.bus().publish(Signals.ELEVATOR_CMD, clamp(elevator, -1.0, 1.0));
        context.bus().publish(Signals.AILERON_CMD, clamp(lateral.aileron(), -1.0, 1.0));
        context.bus().publish(Signals.AUTOPILOT_ENGAGED, true);
        context.bus().publish(AutopilotSignals.VERTICAL_MODE, vertical.modeCode());
        context.bus().publish(AutopilotSignals.LATERAL_MODE, lateral.modeCode());
    }

    @Override
    public void reset() {
        engaged = false;
        pitchController.reset();
        rollController.reset();
        capturedPitchRad = 0.0;
        capturedAltitudeM = 0.0;
        capturedHeadingRad = 0.0;
    }

    private void engage(SimContext context) {
        engaged = true;
        capturedPitchRad = context.bus().read(Signals.PITCH_RAD, 0.0);
        capturedAltitudeM = context.bus().read(Signals.ALTITUDE_M, 0.0);
        capturedHeadingRad = context.bus().read(Signals.HEADING_RAD, 0.0);
        pitchController.reset();
        rollController.reset();
    }

    private void disengage(SimContext context) {
        engaged = false;
        pitchController.reset();
        rollController.reset();
        publishDisengaged(context);
    }

    private static void publishDisengaged(SimContext context) {
        context.bus().publish(Signals.AUTOPILOT_ENGAGED, false);
        context.bus().publish(AutopilotSignals.VERTICAL_MODE, 0.0);
        context.bus().publish(AutopilotSignals.LATERAL_MODE, 0.0);
    }

    private VerticalCommand verticalCommand(SimContext context, double pitch, double altitude, double verticalSpeed, double airspeed,
            double dtSeconds) {
        if (context.bus().has(AutopilotSignals.SELECTED_ALTITUDE_M)) {
            double selectedAltitude = context.bus().read(AutopilotSignals.SELECTED_ALTITUDE_M);
            double altitudeError = clamp(selectedAltitude - altitude, -450.0, 450.0);
            double targetVs = clamp(altitudeError / 45.0, -7.5, 7.5);
            double targetPitch = clamp(targetVs / airspeed, PITCH_DOWN_LIMIT_RAD, PITCH_UP_LIMIT_RAD);
            return new VerticalCommand(pitchController.update(targetPitch, pitch, dtSeconds), AutopilotSignals.MODE_ALTITUDE_HOLD);
        }
        if (context.bus().has(AutopilotSignals.SELECTED_VERTICAL_SPEED_MPS)) {
            double targetVs = clamp(context.bus().read(AutopilotSignals.SELECTED_VERTICAL_SPEED_MPS), -9.0, 9.0);
            double targetPitch = clamp((targetVs - verticalSpeed) / airspeed + pitch, PITCH_DOWN_LIMIT_RAD, PITCH_UP_LIMIT_RAD);
            return new VerticalCommand(pitchController.update(targetPitch, pitch, dtSeconds), AutopilotSignals.MODE_VERTICAL_SPEED);
        }
        return new VerticalCommand(pitchController.update(capturedPitchRad, pitch, dtSeconds), AutopilotSignals.MODE_PITCH_HOLD);
    }

    private LateralCommand lateralCommand(SimContext context, double roll, double heading, double dtSeconds) {
        if (context.bus().has(AutopilotSignals.SELECTED_HEADING_RAD)) {
            double headingError = Angles.difference(heading, context.bus().read(AutopilotSignals.SELECTED_HEADING_RAD));
            double targetRoll = clamp(headingError * 1.4, -BANK_LIMIT_RAD, BANK_LIMIT_RAD);
            return new LateralCommand(rollController.update(targetRoll, roll, dtSeconds), AutopilotSignals.MODE_HEADING_HOLD);
        }
        double targetRoll = context.bus().has(Signals.HEADING_RAD) ? 0.0 : capturedHeadingRad * 0.0;
        return new LateralCommand(rollController.update(targetRoll, roll, dtSeconds), AutopilotSignals.MODE_WINGS_LEVEL);
    }

    private static double clamp(double value, double minimum, double maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    private record VerticalCommand(double elevator, double modeCode) {
    }

    private record LateralCommand(double aileron, double modeCode) {
    }
}
