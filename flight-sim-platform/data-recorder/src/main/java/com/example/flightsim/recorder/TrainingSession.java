package com.example.flightsim.recorder;

import java.time.Instant;

/**
 * A training session as stored in the session database.
 *
 * @param id         database id
 * @param trainee    trainee name as entered by the instructor
 * @param instructor instructor name
 * @param scenarioId scenario that was flown
 * @param startedAt  session start
 * @param endedAt    session end, or null while the session is running
 * @param grade      instructor's grade, or null before debrief
 * @param notes      instructor's notes, may be empty
 */
public record TrainingSession(
        long id,
        String trainee,
        String instructor,
        String scenarioId,
        Instant startedAt,
        Instant endedAt,
        String grade,
        String notes) {
}
