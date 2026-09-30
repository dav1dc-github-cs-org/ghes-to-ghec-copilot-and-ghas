package com.example.flightsim.ios;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** JSON responses for the instructor station API. */
public final class JsonSupport {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private JsonSupport() {
    }

    /**
     * Writes a 200 response.
     *
     * @param response servlet response
     * @param body     value to serialise
     * @throws IOException if the response cannot be written
     */
    public static void write(HttpServletResponse response, Object body) throws IOException {
        write(response, HttpServletResponse.SC_OK, body);
    }

    /**
     * Writes a response with a status.
     *
     * @param response servlet response
     * @param status   HTTP status
     * @param body     value to serialise
     * @throws IOException if the response cannot be written
     */
    public static void write(HttpServletResponse response, int status, Object body) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        MAPPER.writeValue(response.getOutputStream(), body);
    }

    /**
     * Returns the shared mapper, for tests.
     *
     * @return object mapper
     */
    public static ObjectMapper mapper() {
        return MAPPER;
    }
}
