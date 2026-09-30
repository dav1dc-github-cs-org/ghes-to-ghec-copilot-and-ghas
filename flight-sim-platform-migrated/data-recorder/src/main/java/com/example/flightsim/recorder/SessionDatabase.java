package com.example.flightsim.recorder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The session database: training sessions and the signals recorded during them.
 *
 * <p>An embedded H2 database on the instructor station host. The debrief station opens the same
 * file read-only after the session.
 */
public final class SessionDatabase implements AutoCloseable {

    private final Connection connection;

    private SessionDatabase(Connection connection) {
        this.connection = connection;
    }

    /**
     * Opens the database and creates the schema if it does not exist.
     *
     * @param settings recorder settings
     * @return open database
     * @throws SQLException if the database cannot be opened
     */
    public static SessionDatabase open(RecorderSettings settings) throws SQLException {
        Connection connection = DriverManager.getConnection(settings.jdbcUrl(), settings.user(), settings.password());
        SessionDatabase database = new SessionDatabase(connection);
        database.createSchema();
        return database;
    }

    /**
     * Returns the underlying connection.
     *
     * @return JDBC connection
     */
    public Connection connection() {
        return connection;
    }

    private void createSchema() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS training_session (
                        id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                        trainee     VARCHAR(120) NOT NULL,
                        instructor  VARCHAR(120) NOT NULL,
                        scenario_id VARCHAR(64)  NOT NULL,
                        started_at  TIMESTAMP    NOT NULL,
                        ended_at    TIMESTAMP,
                        grade       VARCHAR(16),
                        notes       VARCHAR(4000)
                    )""");
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS signal_sample (
                        session_id  BIGINT       NOT NULL,
                        sim_time_s  DOUBLE       NOT NULL,
                        signal_name VARCHAR(80)  NOT NULL,
                        signal_value DOUBLE      NOT NULL
                    )""");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_sample_session ON signal_sample (session_id, sim_time_s)");
        }
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }
}
