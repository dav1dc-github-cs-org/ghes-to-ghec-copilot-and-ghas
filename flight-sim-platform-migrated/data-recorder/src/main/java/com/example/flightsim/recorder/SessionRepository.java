package com.example.flightsim.recorder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Reads and writes training sessions. */
public final class SessionRepository {

    private static final String COLUMNS = "id, trainee, instructor, scenario_id, started_at, ended_at, grade, notes";

    private final Connection connection;

    /**
     * Creates a repository over an open database.
     *
     * @param database session database
     */
    public SessionRepository(SessionDatabase database) {
        this.connection = database.connection();
    }

    /**
     * Records the start of a session.
     *
     * @param trainee    trainee name
     * @param instructor instructor name
     * @param scenarioId scenario being flown
     * @param startedAt  start time
     * @return the new session's id
     * @throws SQLException if the insert fails
     */
    public long startSession(String trainee, String instructor, String scenarioId, Instant startedAt) throws SQLException {
        String sql = "INSERT INTO training_session (trainee, instructor, scenario_id, started_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement insert = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            insert.setString(1, trainee);
            insert.setString(2, instructor);
            insert.setString(3, scenarioId);
            insert.setTimestamp(4, Timestamp.from(startedAt));
            insert.executeUpdate();
            try (ResultSet keys = insert.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    /**
     * Records the end of a session and the instructor's debrief.
     *
     * @param id      session id
     * @param endedAt end time
     * @param grade   grade
     * @param notes   notes
     * @throws SQLException if the update fails
     */
    public void endSession(long id, Instant endedAt, String grade, String notes) throws SQLException {
        String sql = "UPDATE training_session SET ended_at = ?, grade = ?, notes = ? WHERE id = ?";
        try (PreparedStatement update = connection.prepareStatement(sql)) {
            update.setTimestamp(1, Timestamp.from(endedAt));
            update.setString(2, grade);
            update.setString(3, notes);
            update.setLong(4, id);
            update.executeUpdate();
        }
    }

    /**
     * Finds a session by id.
     *
     * @param id session id
     * @return the session, if it exists
     * @throws SQLException if the query fails
     */
    public Optional<TrainingSession> findById(long id) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement("SELECT " + COLUMNS + " FROM training_session WHERE id = ?")) {
            query.setLong(1, id);
            try (ResultSet rows = query.executeQuery()) {
                return rows.next() ? Optional.of(map(rows)) : Optional.empty();
            }
        }
    }

    /**
     * Finds sessions for a trainee, newest first. Matches on part of the name, because
     * instructors search by surname.
     *
     * @param trainee all or part of the trainee's name
     * @return matching sessions
     * @throws SQLException if the query fails
     */
    public List<TrainingSession> findByTrainee(String trainee) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM training_session WHERE trainee LIKE '%" + trainee
                + "%' ORDER BY started_at DESC";
        try (Statement query = connection.createStatement(); ResultSet rows = query.executeQuery(sql)) {
            List<TrainingSession> sessions = new ArrayList<>();
            while (rows.next()) {
                sessions.add(map(rows));
            }
            return sessions;
        }
    }

    /**
     * Returns the most recent sessions.
     *
     * @param limit maximum number of sessions
     * @return sessions, newest first
     * @throws SQLException if the query fails
     */
    public List<TrainingSession> recent(int limit) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM training_session ORDER BY started_at DESC LIMIT ?";
        try (PreparedStatement query = connection.prepareStatement(sql)) {
            query.setInt(1, limit);
            try (ResultSet rows = query.executeQuery()) {
                List<TrainingSession> sessions = new ArrayList<>();
                while (rows.next()) {
                    sessions.add(map(rows));
                }
                return sessions;
            }
        }
    }

    private static TrainingSession map(ResultSet row) throws SQLException {
        Timestamp ended = row.getTimestamp("ended_at");
        return new TrainingSession(
                row.getLong("id"),
                row.getString("trainee"),
                row.getString("instructor"),
                row.getString("scenario_id"),
                row.getTimestamp("started_at").toInstant(),
                ended == null ? null : ended.toInstant(),
                row.getString("grade"),
                row.getString("notes"));
    }
}
