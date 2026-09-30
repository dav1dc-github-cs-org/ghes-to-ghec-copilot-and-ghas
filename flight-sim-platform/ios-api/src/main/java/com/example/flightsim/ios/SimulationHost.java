package com.example.flightsim.ios;

import com.example.flightsim.core.MalfunctionRegistry;
import com.example.flightsim.core.SimComponent;
import com.example.flightsim.core.SimContext;
import com.example.flightsim.core.SimExecutive;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runs the simulation executive in real time on its own thread and gives the HTTP layer a
 * thread-safe view of it.
 */
public final class SimulationHost implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(SimulationHost.class);

    private final SimContext context;
    private final SimExecutive executive;
    private final ScheduledExecutorService frameThread;
    private volatile String scenarioId = "none";
    private volatile boolean frozen;

    /**
     * Creates a host.
     *
     * @param context simulation context
     */
    public SimulationHost(SimContext context) {
        this.context = context;
        this.executive = new SimExecutive(context);
        this.frameThread = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "sim-executive");
            thread.setDaemon(true);
            return thread;
        });
    }

    /**
     * Registers a component with the executive. Call before {@link #start()}.
     *
     * @param component component
     * @param rateHz    rate in hertz
     */
    public synchronized void register(SimComponent component, int rateHz) {
        executive.register(component, rateHz);
    }

    /** Starts stepping frames in real time. */
    public void start() {
        long periodMicros = 1_000_000L / executive.frameRateHz();
        frameThread.scheduleAtFixedRate(this::frame, 0L, periodMicros, TimeUnit.MICROSECONDS);
        LOG.info("Simulation running at {} Hz with components {}", executive.frameRateHz(), executive.componentNames());
    }

    private synchronized void frame() {
        if (frozen) {
            return;
        }
        try {
            executive.stepFrame();
        } catch (RuntimeException e) {
            frozen = true;
            LOG.error("Simulation frozen after a model error at t={} s", context.time().elapsedSeconds(), e);
        }
    }

    /**
     * Takes a snapshot of the current state.
     *
     * @return snapshot
     */
    public synchronized SimSnapshot snapshot() {
        return new SimSnapshot(scenarioId, context.time().elapsedSeconds(), context.bus().snapshot(),
                context.malfunctions().activeIds());
    }

    /**
     * Restores a snapshot: republishes its signals and re-inserts its malfunctions.
     *
     * @param snapshot snapshot to restore
     */
    public synchronized void restore(SimSnapshot snapshot) {
        MalfunctionRegistry malfunctions = context.malfunctions();
        malfunctions.clearAll();
        for (String id : snapshot.activeMalfunctions()) {
            malfunctions.activate(id);
        }
        for (Map.Entry<String, Double> signal : snapshot.signals().entrySet()) {
            context.bus().publish(signal.getKey(), signal.getValue());
        }
        scenarioId = snapshot.scenarioId();
        frozen = false;
        LOG.info("Restored snapshot of scenario {} at t={} s", scenarioId, snapshot.simTimeSeconds());
    }

    /**
     * Records which scenario is loaded.
     *
     * @param id scenario id
     */
    public void setScenarioId(String id) {
        scenarioId = id;
    }

    /**
     * Returns the malfunction registry shared with the models.
     *
     * @return registry
     */
    public MalfunctionRegistry malfunctions() {
        return context.malfunctions();
    }

    /**
     * Returns the current session time.
     *
     * @return simulated seconds
     */
    public double simTimeSeconds() {
        return context.time().elapsedSeconds();
    }

    /**
     * Tells whether the simulation stopped after a model error.
     *
     * @return true if frozen
     */
    public boolean isFrozen() {
        return frozen;
    }

    @Override
    public void close() {
        frameThread.shutdownNow();
    }
}
