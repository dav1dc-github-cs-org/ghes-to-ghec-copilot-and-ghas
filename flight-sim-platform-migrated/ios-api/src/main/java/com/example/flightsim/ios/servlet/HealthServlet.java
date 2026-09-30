package com.example.flightsim.ios.servlet;

import com.example.flightsim.ios.JsonSupport;
import com.example.flightsim.ios.LicenceCheck;
import com.example.flightsim.ios.SimulationHost;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/** Health endpoint polled by the training centre's monitoring: {@code GET /health}. */
public final class HealthServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final transient SimulationHost host;
    private final transient LicenceCheck.LicenceInfo licence;
    private final long startedAtMillis = System.currentTimeMillis();

    /**
     * Creates the servlet.
     *
     * @param host    simulation host
     * @param licence installed licence
     */
    public HealthServlet(SimulationHost host, LicenceCheck.LicenceInfo licence) {
        this.host = host;
        this.licence = licence;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> health = new LinkedHashMap<>();
        health.put("status", host.isFrozen() ? "frozen" : "ok");
        health.put("uptimeSeconds", (System.currentTimeMillis() - startedAtMillis) / 1000L);
        health.put("simTimeSeconds", host.simTimeSeconds());
        health.put("licence", Map.of(
                "site", licence.site(),
                "seats", licence.seats(),
                "expires", licence.expires().toString(),
                "valid", licence.valid()));
        JsonSupport.write(response, health);
    }
}
