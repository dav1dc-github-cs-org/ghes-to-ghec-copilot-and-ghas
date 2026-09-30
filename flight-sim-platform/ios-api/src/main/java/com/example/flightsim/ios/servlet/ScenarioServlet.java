package com.example.flightsim.ios.servlet;

import com.example.flightsim.ios.JsonSupport;
import com.example.flightsim.scenario.Scenario;
import com.example.flightsim.scenario.ScenarioFormatException;
import com.example.flightsim.scenario.ScenarioLibrary;
import com.example.flightsim.scenario.ScenarioXmlImporter;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Map;

/**
 * Scenario library API.
 *
 * <ul>
 *   <li>{@code GET /api/scenarios} — list the library</li>
 *   <li>{@code GET /api/scenarios/<id>} — one scenario</li>
 *   <li>{@code POST /api/scenarios/import} — preview a scenario exported from the previous
 *       instructor station (IOS v2 XML) before it is added to the library</li>
 * </ul>
 */
public final class ScenarioServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final transient ScenarioLibrary library;
    private final transient ScenarioXmlImporter importer = new ScenarioXmlImporter();

    /**
     * Creates the servlet.
     *
     * @param library scenario library
     */
    public ScenarioServlet(ScenarioLibrary library) {
        this.library = library;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getPathInfo();
        if (path == null || "/".equals(path)) {
            JsonSupport.write(response, library.list());
            return;
        }
        try {
            JsonSupport.write(response, library.load(path.substring(1)));
        } catch (IllegalArgumentException | NoSuchFileException e) {
            JsonSupport.write(response, HttpServletResponse.SC_NOT_FOUND, Map.of("error", "No such scenario"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!"/import".equals(request.getPathInfo())) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        try {
            Scenario preview = importer.parse(request.getInputStream());
            JsonSupport.write(response, preview);
        } catch (ScenarioFormatException e) {
            JsonSupport.write(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("error", "Not an IOS v2 scenario export"));
        }
    }
}
