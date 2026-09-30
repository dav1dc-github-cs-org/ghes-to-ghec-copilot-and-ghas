package com.example.flightsim.ios.servlet;

import com.example.flightsim.ios.JsonSupport;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Connectivity check the instructor runs when the visual system or the motion platform stops
 * responding: {@code GET /api/diagnostics/ping?host=visual-ig-1}.
 */
public final class DiagnosticsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String host = request.getParameter("host");
        if (host == null || host.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The host parameter is required");
            return;
        }
        Process ping = Runtime.getRuntime().exec("ping -c 3 -W 1 " + host);
        String output;
        try (InputStream in = ping.getInputStream()) {
            output = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        boolean finished;
        try {
            finished = ping.waitFor(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            finished = false;
        }
        if (!finished) {
            ping.destroyForcibly();
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("reachable", finished && ping.exitValue() == 0);
        result.put("output", output);
        JsonSupport.write(response, result);
    }
}
