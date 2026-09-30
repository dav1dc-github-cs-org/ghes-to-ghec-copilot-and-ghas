package com.example.flightsim.legacy;

import com.example.flightsim.core.Signals;
import com.example.flightsim.core.SimBus;
import com.example.flightsim.core.SimComponent;
import com.example.flightsim.core.SimContext;
import com.example.flightsim.core.math.Vector3;

/**
 * Simulator component wrapper around the legacy motion-cueing washout filter.
 */
public final class LegacyMotionComponent implements SimComponent {

    private final LegacyMotionCueing cueing = new LegacyMotionCueing();
    private double previousTas;
    private double previousVs;
    private boolean hasPrevious;

    @Override
    public String name() {
        return "legacy-motion";
    }

    @Override
    public void initialise(SimContext context) {
        SimBus bus = context.bus();
        previousTas = bus.read(Signals.TRUE_AIRSPEED_MPS, 0.0);
        previousVs = bus.read(Signals.VERTICAL_SPEED_MPS, 0.0);
        hasPrevious = true;
    }

    @Override
    public void step(SimContext context, double dtSeconds) {
        SimBus bus = context.bus();
        double tas = bus.read(Signals.TRUE_AIRSPEED_MPS, previousTas);
        double vs = bus.read(Signals.VERTICAL_SPEED_MPS, previousVs);
        double longitudinal = hasPrevious ? (tas - previousTas) / dtSeconds : 0.0;
        double vertical = hasPrevious ? (vs - previousVs) / dtSeconds : 0.0;
        previousTas = tas;
        previousVs = vs;
        hasPrevious = true;
        Vector3 force = new Vector3(longitudinal, 0.0, vertical);
        Vector3 rates = new Vector3(
                bus.read(Signals.ROLL_RATE_RADPS, 0.0),
                bus.read(Signals.PITCH_RATE_RADPS, 0.0),
                0.0);
        LegacyMotionCueing.Command command = cueing.step(force, rates, dtSeconds);
        bus.publish("motion.surge.m", command.surgeM());
        bus.publish("motion.sway.m", command.swayM());
        bus.publish("motion.heave.m", command.heaveM());
        bus.publish("motion.roll.rad", command.rollRad());
        bus.publish("motion.pitch.rad", command.pitchRad());
        bus.publish("motion.yaw.rad", command.yawRad());
    }

    @Override
    public void reset() {
        cueing.reset();
        previousTas = 0.0;
        previousVs = 0.0;
        hasPrevious = false;
    }
}
