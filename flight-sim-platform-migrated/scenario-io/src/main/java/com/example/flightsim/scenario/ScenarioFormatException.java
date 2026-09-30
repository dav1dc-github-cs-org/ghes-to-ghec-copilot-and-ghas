package com.example.flightsim.scenario;

/** A scenario file or export that cannot be read. */
public class ScenarioFormatException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception.
     *
     * @param message what is wrong with the file
     */
    public ScenarioFormatException(String message) {
        super(message);
    }

    /**
     * Creates the exception with a cause.
     *
     * @param message what is wrong with the file
     * @param cause   underlying parser error
     */
    public ScenarioFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
