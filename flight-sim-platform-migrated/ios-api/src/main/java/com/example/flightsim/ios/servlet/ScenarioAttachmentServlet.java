package com.example.flightsim.ios.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Serves scenario attachments — approach charts, briefing notes and weather sheets — from the
 * training data directory, so the instructor can print them for the trainee.
 *
 * <p>{@code GET /api/scenario-attachments?file=XFSA-RWY27-ILS.pdf}
 */
public final class ScenarioAttachmentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final transient Path attachmentsRoot;

    /**
     * Creates the servlet.
     *
     * @param attachmentsRoot directory holding the attachments
     */
    public ScenarioAttachmentServlet(Path attachmentsRoot) {
        this.attachmentsRoot = attachmentsRoot;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String file = request.getParameter("file");
        if (file == null || file.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The file parameter is required");
            return;
        }
        Path attachment = attachmentsRoot.resolve(file);
        response.setContentType(contentType(file));
        try (InputStream in = Files.newInputStream(attachment)) {
            in.transferTo(response.getOutputStream());
        } catch (NoSuchFileException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No such attachment");
        }
    }

    private static String contentType(String file) {
        String name = file.toLowerCase(Locale.ROOT);
        if (name.endsWith(".pdf")) {
            return "application/pdf";
        }
        if (name.endsWith(".png")) {
            return "image/png";
        }
        if (name.endsWith(".txt") || name.endsWith(".md")) {
            return "text/plain;charset=UTF-8";
        }
        return "application/octet-stream";
    }
}
