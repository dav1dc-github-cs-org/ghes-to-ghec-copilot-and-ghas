package com.example.flightsim.ios.servlet;

import com.example.flightsim.ios.JsonSupport;
import com.example.flightsim.recorder.SessionRepository;
import com.example.flightsim.recorder.TrainingSession;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Session history for the debrief: {@code GET /api/sessions?trainee=Moreau}. Without a trainee,
 * returns the 50 most recent sessions.
 */
public final class SessionSearchServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final int RECENT_LIMIT = 50;

    private final transient SessionRepository repository;

    /**
     * Creates the servlet.
     *
     * @param repository session repository
     */
    public SessionSearchServlet(SessionRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String trainee = request.getParameter("trainee");
        try {
            List<TrainingSession> sessions = trainee == null || trainee.isBlank()
                    ? repository.recent(RECENT_LIMIT)
                    : repository.findByTrainee(trainee.trim());
            JsonSupport.write(response, sessions.stream().map(SessionView::of).toList());
        } catch (SQLException e) {
            log("Session search failed", e);
            JsonSupport.write(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of("error", "Session search failed"));
        }
    }

    /**
     * A session as returned by the API, with ISO-8601 timestamps.
     *
     * @param id         session id
     * @param trainee    trainee
     * @param instructor instructor
     * @param scenarioId scenario
     * @param startedAt  start, ISO-8601
     * @param endedAt    end, ISO-8601, or null
     * @param grade      grade, or null
     */
    public record SessionView(long id, String trainee, String instructor, String scenarioId, String startedAt,
                              String endedAt, String grade) {

        static SessionView of(TrainingSession s) {
            return new SessionView(s.id(), s.trainee(), s.instructor(), s.scenarioId(), s.startedAt().toString(),
                    s.endedAt() == null ? null : s.endedAt().toString(), s.grade());
        }
    }
}
