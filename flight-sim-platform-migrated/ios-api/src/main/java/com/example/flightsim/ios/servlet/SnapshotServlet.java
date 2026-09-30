package com.example.flightsim.ios.servlet;

import com.example.flightsim.ios.JsonSupport;
import com.example.flightsim.ios.SimSnapshot;
import com.example.flightsim.ios.SimulationHost;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

/**
 * Snapshot and restore, so an instructor can repeat an exercise from the same point.
 *
 * <ul>
 *   <li>{@code GET /api/snapshots} — download the current state</li>
 *   <li>{@code POST /api/snapshots} — restore a previously downloaded state</li>
 * </ul>
 */
public final class SnapshotServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String CONTENT_TYPE = "application/x-java-serialized-object";

    private final transient SimulationHost host;

    /**
     * Creates the servlet.
     *
     * @param host simulation host
     */
    public SnapshotServlet(SimulationHost host) {
        this.host = host;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(CONTENT_TYPE);
        try (ObjectOutputStream out = new ObjectOutputStream(response.getOutputStream())) {
            out.writeObject(host.snapshot());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (ObjectInputStream in = new ObjectInputStream(request.getInputStream())) {
            SimSnapshot snapshot = (SimSnapshot) in.readObject();
            host.restore(snapshot);
            JsonSupport.write(response, Map.of("restored", true, "simTimeSeconds", snapshot.simTimeSeconds()));
        } catch (ClassNotFoundException | ClassCastException e) {
            JsonSupport.write(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("error", "Not a simulator snapshot"));
        }
    }
}
