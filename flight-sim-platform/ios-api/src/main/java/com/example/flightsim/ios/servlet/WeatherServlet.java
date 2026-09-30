package com.example.flightsim.ios.servlet;

import com.example.flightsim.ios.JsonSupport;
import com.example.flightsim.ios.MetarParser;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

/**
 * Live weather for weather scenarios: fetches the latest METAR and returns it parsed, so the
 * instructor can set the simulator to today's conditions.
 *
 * <p>{@code GET /api/weather/metar} uses the configured met office feed; instructors at other
 * training centres pass their own feed with {@code ?source=<url>}.
 */
public final class WeatherServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final transient HttpClient httpClient;
    private final String defaultFeed;
    private final transient MetarParser parser = new MetarParser();

    /**
     * Creates the servlet.
     *
     * @param httpClient  HTTP client
     * @param defaultFeed METAR feed used when no source is given
     */
    public WeatherServlet(HttpClient httpClient, String defaultFeed) {
        this.httpClient = httpClient;
        this.defaultFeed = defaultFeed;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String source = request.getParameter("source");
        String feed = source == null || source.isBlank() ? defaultFeed : source;
        try {
            HttpRequest metarRequest = HttpRequest.newBuilder(URI.create(feed))
                    .timeout(Duration.ofSeconds(5))
                    .header("Accept", "text/plain")
                    .GET()
                    .build();
            HttpResponse<String> metar = httpClient.send(metarRequest, HttpResponse.BodyHandlers.ofString());
            if (metar.statusCode() != HttpServletResponse.SC_OK) {
                JsonSupport.write(response, HttpServletResponse.SC_BAD_GATEWAY,
                        Map.of("error", "Weather feed returned HTTP " + metar.statusCode()));
                return;
            }
            JsonSupport.write(response, parser.parse(metar.body()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        } catch (IOException | IllegalArgumentException e) {
            JsonSupport.write(response, HttpServletResponse.SC_BAD_GATEWAY, Map.of("error", "Weather feed unavailable"));
        }
    }
}
