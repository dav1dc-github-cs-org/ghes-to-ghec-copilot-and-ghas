package com.example.flightsim.ios.servlet;

import com.example.flightsim.ios.Html;
import com.example.flightsim.scenario.BriefingTemplate;
import com.example.flightsim.scenario.Scenario;
import com.example.flightsim.scenario.ScenarioLibrary;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.NoSuchFileException;
import java.util.Locale;
import java.util.Map;

/**
 * Printable pre-flight briefing sheet:
 * {@code GET /briefing?scenario=engine-failure-after-v1&trainee=A.%20Moreau}.
 */
public final class BriefingServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final transient ScenarioLibrary library;
    private final transient BriefingTemplate instructorNote = new BriefingTemplate(
            "Brief the crew on ${title} at ${airport} runway ${runway} before engine start.");

    /**
     * Creates the servlet.
     *
     * @param library scenario library
     */
    public BriefingServlet(ScenarioLibrary library) {
        this.library = library;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String trainee = request.getParameter("trainee");
        Scenario scenario;
        try {
            scenario = library.load(request.getParameter("scenario"));
        } catch (IllegalArgumentException | NoSuchFileException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No such scenario");
            return;
        }
        Scenario.InitialConditions ic = scenario.initialConditions();
        Scenario.Weather wx = scenario.weather();

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html lang=\"en\"><head><meta charset=\"utf-8\"><title>Pre-flight briefing</title>");
        out.println("<link rel=\"stylesheet\" href=\"/static/briefing.css\"></head><body>");
        out.println("<h1>Pre-flight briefing: " + trainee + "</h1>");
        out.println("<h2>" + Html.escape(scenario.title()) + "</h2>");
        out.println("<p>" + Html.escape(scenario.description()) + "</p>");
        out.println("<table>");
        row(out, "Airport", scenario.airport() + " runway " + scenario.runway());
        row(out, "Start", ic.onGround() ? "On the runway" : format(ic.altitudeFt()) + " ft, " + format(ic.indicatedAirspeedKt()) + " kt");
        row(out, "Heading", format(ic.headingDeg()) + "°");
        row(out, "Fuel", format(ic.fuelKg()) + " kg");
        row(out, "Wind", format(wx.windFromDeg()) + "° / " + format(wx.windSpeedKt()) + " kt");
        row(out, "QNH", format(wx.qnhHpa()) + " hPa");
        out.println("</table>");
        String note = instructorNote.render(Map.of(
                "title", scenario.title(), "airport", scenario.airport(), "runway", scenario.runway()));
        out.println("<p class=\"instructor-note\">" + Html.escape(note) + "</p>");
        out.println("</body></html>");
    }

    private static void row(PrintWriter out, String label, String value) {
        out.println("<tr><th>" + Html.escape(label) + "</th><td>" + Html.escape(value) + "</td></tr>");
    }

    private static String format(double value) {
        return String.format(Locale.ROOT, "%.0f", value);
    }
}
