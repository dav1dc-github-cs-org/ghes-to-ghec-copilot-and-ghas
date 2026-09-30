package com.example.flightsim.ios.servlet;

import com.example.flightsim.core.MalfunctionRegistry;
import com.example.flightsim.ios.JsonSupport;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Malfunction control.
 *
 * <ul>
 *   <li>{@code GET /api/malfunctions} — every malfunction the models offer, with its state</li>
 *   <li>{@code POST /api/malfunctions?id=<id>&action=activate|clear} — insert or remove one</li>
 * </ul>
 */
public final class MalfunctionServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final transient MalfunctionRegistry registry;

    /**
     * Creates the servlet.
     *
     * @param registry malfunction registry shared with the models
     */
    public MalfunctionServlet(MalfunctionRegistry registry) {
        this.registry = registry;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<MalfunctionView> views = new ArrayList<>();
        for (MalfunctionRegistry.Malfunction m : registry.available()) {
            views.add(new MalfunctionView(m.id(), m.description(), m.ataChapter(), registry.isActive(m.id())));
        }
        JsonSupport.write(response, views);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        String action = request.getParameter("action");
        boolean known = registry.available().stream().anyMatch(m -> m.id().equals(id));
        if (!known) {
            JsonSupport.write(response, HttpServletResponse.SC_NOT_FOUND, Map.of("error", "Unknown malfunction"));
            return;
        }
        if ("activate".equals(action)) {
            registry.activate(id);
        } else if ("clear".equals(action)) {
            registry.clear(id);
        } else {
            JsonSupport.write(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("error", "action must be activate or clear"));
            return;
        }
        JsonSupport.write(response, Map.of("active", registry.isActive(id)));
    }

    /**
     * A malfunction as shown on the instructor station.
     *
     * @param id          malfunction id
     * @param description description
     * @param ataChapter  ATA chapter
     * @param active      true if inserted
     */
    public record MalfunctionView(String id, String description, int ataChapter, boolean active) {
    }
}
