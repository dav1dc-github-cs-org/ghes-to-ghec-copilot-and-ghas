package com.example.flightsim.recorder;

import com.example.flightsim.core.SimBus;
import com.example.flightsim.core.SimComponent;
import com.example.flightsim.core.SimContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Records selected signals into the session database while a session is running.
 *
 * <p>Samples are written in JDBC batches so the recorder never holds up the simulation frame;
 * a batch is flushed once a second and when recording stops.
 */
public final class FlightDataRecorder implements SimComponent {

    private final Connection connection;
    private final List<String> signals;
    private final int flushEverySamples;
    private PreparedStatement insert;
    private long sessionId = -1L;
    private int pending;
    private long samplesWritten;

    /**
     * Creates a recorder.
     *
     * @param database session database
     * @param settings recorder settings; the signal list and sample rate are used
     */
    public FlightDataRecorder(SessionDatabase database, RecorderSettings settings) {
        this.connection = database.connection();
        this.signals = settings.signals();
        this.flushEverySamples = Math.max(1, settings.sampleRateHz());
    }

    @Override
    public String name() {
        return "data-recorder";
    }

    /**
     * Starts recording into a session.
     *
     * @param id session id from {@link SessionRepository#startSession}
     * @throws SQLException if the insert statement cannot be prepared
     */
    public void startRecording(long id) throws SQLException {
        stopRecording();
        insert = connection.prepareStatement(
                "INSERT INTO signal_sample (session_id, sim_time_s, signal_name, signal_value) VALUES (?, ?, ?, ?)");
        sessionId = id;
        pending = 0;
    }

    /**
     * Stops recording and flushes anything not yet written.
     *
     * @throws SQLException if the final batch cannot be written
     */
    public void stopRecording() throws SQLException {
        if (insert != null) {
            flush();
            insert.close();
            insert = null;
        }
        sessionId = -1L;
    }

    /**
     * Tells whether a session is being recorded.
     *
     * @return true while recording
     */
    public boolean isRecording() {
        return insert != null;
    }

    /**
     * Returns the number of samples written since the recorder was created.
     *
     * @return sample count
     */
    public long samplesWritten() {
        return samplesWritten;
    }

    @Override
    public void step(SimContext context, double dtSeconds) {
        if (insert == null) {
            return;
        }
        SimBus bus = context.bus();
        double time = context.time().elapsedSeconds();
        try {
            for (String signal : signals) {
                if (bus.has(signal)) {
                    insert.setLong(1, sessionId);
                    insert.setDouble(2, time);
                    insert.setString(3, signal);
                    insert.setDouble(4, bus.read(signal));
                    insert.addBatch();
                }
            }
            if (++pending >= flushEverySamples) {
                flush();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Flight data recorder could not write to the session database", e);
        }
    }

    private void flush() throws SQLException {
        int[] counts = insert.executeBatch();
        samplesWritten += counts.length;
        pending = 0;
    }
}
