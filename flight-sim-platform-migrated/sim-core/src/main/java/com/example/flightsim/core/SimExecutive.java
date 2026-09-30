package com.example.flightsim.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Fixed-frame simulation executive.
 *
 * <p>The executive runs at a fixed frame rate (60 Hz by default). Each component is registered
 * with its own rate, which must divide the frame rate exactly; a 20 Hz component on a 60 Hz
 * executive is stepped every third frame with a step length of three frames. Components are
 * stepped in registration order, so register producers before consumers.
 */
public final class SimExecutive {

    /** Default frame rate. */
    public static final int DEFAULT_FRAME_RATE_HZ = 60;

    private final SimContext context;
    private final int frameRateHz;
    private final double frameSeconds;
    private final List<Scheduled> scheduled = new ArrayList<>();
    private boolean initialised;

    /**
     * Creates an executive.
     *
     * @param context     context shared by every component
     * @param frameRateHz frame rate in hertz
     */
    public SimExecutive(SimContext context, int frameRateHz) {
        if (frameRateHz <= 0) {
            throw new IllegalArgumentException("Frame rate must be positive: " + frameRateHz);
        }
        this.context = context;
        this.frameRateHz = frameRateHz;
        this.frameSeconds = 1.0 / frameRateHz;
    }

    /**
     * Creates an executive at {@link #DEFAULT_FRAME_RATE_HZ}.
     *
     * @param context context shared by every component
     */
    public SimExecutive(SimContext context) {
        this(context, DEFAULT_FRAME_RATE_HZ);
    }

    /**
     * Registers a component.
     *
     * @param component component to step
     * @param rateHz    rate in hertz; must divide the frame rate exactly
     */
    public void register(SimComponent component, int rateHz) {
        if (rateHz <= 0 || rateHz > frameRateHz || frameRateHz % rateHz != 0) {
            throw new IllegalArgumentException(component.name() + ": rate " + rateHz
                    + " Hz does not divide the " + frameRateHz + " Hz frame rate");
        }
        for (Scheduled existing : scheduled) {
            if (existing.component().name().equals(component.name())) {
                throw new IllegalArgumentException("Component already registered: " + component.name());
            }
        }
        scheduled.add(new Scheduled(component, frameRateHz / rateHz));
        initialised = false;
    }

    /** Runs one frame: steps every component that is due, then advances time. */
    public void stepFrame() {
        if (!initialised) {
            for (Scheduled s : scheduled) {
                s.component().initialise(context);
            }
            initialised = true;
        }
        long frame = context.time().frame();
        for (Scheduled s : scheduled) {
            if (frame % s.divider() == 0) {
                s.component().step(context, s.divider() * frameSeconds);
            }
        }
        context.advanceTime(frameSeconds);
    }

    /**
     * Runs whole frames covering {@code seconds} of simulated time.
     *
     * @param seconds simulated time to run
     */
    public void runFor(double seconds) {
        long frames = Math.round(seconds * frameRateHz);
        for (long i = 0; i < frames; i++) {
            stepFrame();
        }
    }

    /** Resets every component and the clock. Signals are cleared; malfunctions are not. */
    public void reset() {
        for (Scheduled s : scheduled) {
            s.component().reset();
        }
        context.bus().clear();
        context.resetTime();
        initialised = false;
    }

    /**
     * Returns the registered component names in step order.
     *
     * @return component names
     */
    public List<String> componentNames() {
        List<String> names = new ArrayList<>();
        for (Scheduled s : scheduled) {
            names.add(s.component().name());
        }
        return Collections.unmodifiableList(names);
    }

    /**
     * Returns the frame rate.
     *
     * @return frame rate in hertz
     */
    public int frameRateHz() {
        return frameRateHz;
    }

    /**
     * Returns the shared context.
     *
     * @return context
     */
    public SimContext context() {
        return context;
    }

    private record Scheduled(SimComponent component, int divider) {
    }
}
