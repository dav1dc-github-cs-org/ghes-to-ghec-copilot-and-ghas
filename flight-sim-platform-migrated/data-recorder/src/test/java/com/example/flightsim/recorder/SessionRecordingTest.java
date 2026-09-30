package com.example.flightsim.recorder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.flightsim.core.SimContext;
import com.example.flightsim.core.SimExecutive;
import com.example.flightsim.core.Signals;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SessionRecordingTest {

    private SessionDatabase database;
    private RecorderSettings settings;

    @BeforeEach
    void openDatabase() throws SQLException {
        settings = RecorderSettings.defaults().withJdbcUrl("jdbc:h2:mem:" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
        database = SessionDatabase.open(settings);
    }

    @AfterEach
    void closeDatabase() throws SQLException {
        database.close();
    }

    @Test
    void defaultsNeverPrintThePassword() {
        assertFalse(RecorderSettings.defaults().toString().contains(RecorderSettings.defaults().password()));
        assertTrue(RecorderSettings.defaults().signals().contains(Signals.ALTITUDE_M));
    }

    @Test
    void storesAndFindsSessions() throws SQLException {
        SessionRepository repository = new SessionRepository(database);
        long first = repository.startSession("A. Moreau", "Instructor 2", "engine-failure-after-v1", Instant.parse("2026-09-01T09:00:00Z"));
        long second = repository.startSession("B. Tremblay", "Instructor 2", "crosswind-landing", Instant.parse("2026-09-02T09:00:00Z"));
        repository.endSession(first, Instant.parse("2026-09-01T10:30:00Z"), "3", "Good engine-out handling");

        assertEquals("3", repository.findById(first).orElseThrow().grade());
        assertEquals(List.of(second, first), repository.recent(10).stream().map(TrainingSession::id).toList());
        assertEquals(1, repository.findByTrainee("Moreau").size());
        assertEquals(0, repository.findByTrainee("Nobody").size());
    }

    @Test
    void recordsConfiguredSignalsWhileRecording() throws SQLException {
        SessionRepository repository = new SessionRepository(database);
        long session = repository.startSession("A. Moreau", "Instructor 2", "engine-failure-after-v1", Instant.now());
        FlightDataRecorder recorder = new FlightDataRecorder(database, settings);
        SimContext context = SimContext.create(11L);
        SimExecutive executive = new SimExecutive(context, 60);
        executive.register(new ConstantSignals(), 60);
        executive.register(recorder, 20);

        recorder.startRecording(session);
        executive.runFor(2.0);
        recorder.stopRecording();

        try (Statement statement = database.connection().createStatement();
             ResultSet rows = statement.executeQuery("SELECT COUNT(*) FROM signal_sample")) {
            rows.next();
            // 2 s at 20 Hz, two of the configured signals are published
            assertEquals(80, rows.getInt(1));
        }
        assertFalse(recorder.isRecording());
    }

    private static final class ConstantSignals implements com.example.flightsim.core.SimComponent {
        @Override
        public String name() {
            return "constant-signals";
        }

        @Override
        public void step(SimContext context, double dtSeconds) {
            context.bus().publish(Signals.ALTITUDE_M, 1500.0);
            context.bus().publish(Signals.INDICATED_AIRSPEED_MPS, 80.0);
        }
    }
}
