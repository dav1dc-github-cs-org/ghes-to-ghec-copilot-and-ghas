package com.example.flightsim.ios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.flightsim.recorder.RecorderSettings;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class IosServerSmokeTest {

    private static final String SCENARIO = """
            id: crosswind-landing
            title: Crosswind landing
            description: Gusting crosswind landing at the limit of the demonstrated crosswind.
            airport: XFSB
            runway: "09"
            initial:
              altitude_ft: 2000
              ias_kt: 160
              heading_deg: 90
              fuel_kg: 2400
              gross_mass_kg: 19500
            weather:
              wind_from_deg: 140
              wind_speed_kt: 25
            tags: [landing, crosswind]
            """;

    @TempDir
    Path trainingData;

    private final HttpClient client = HttpClient.newHttpClient();

    @Test
    void servesHealthScenariosAttachmentsAndMalfunctions() throws Exception {
        Files.createDirectories(trainingData.resolve("scenarios"));
        Files.writeString(trainingData.resolve("scenarios/crosswind-landing.yaml"), SCENARIO);
        Files.createDirectories(trainingData.resolve("attachments"));
        Files.writeString(trainingData.resolve("attachments/XFSB-RWY09-ILS.txt"), "ILS 09: 3.0 degree glidepath");

        IosConfig config = new IosConfig("127.0.0.1", 0, trainingData, "http://127.0.0.1:9/metar", 42L);
        RecorderSettings recorder = RecorderSettings.defaults()
                .withJdbcUrl("jdbc:h2:mem:" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");

        try (IosServer ios = IosServer.start(config, recorder)) {
            HttpResponse<String> health = get(ios, "/health");
            assertEquals(200, health.statusCode());
            assertTrue(health.body().contains("\"status\""));
            assertTrue(health.body().contains("\"TC2\""));

            HttpResponse<String> scenarios = get(ios, "/api/scenarios");
            assertEquals(200, scenarios.statusCode());
            assertTrue(scenarios.body().contains("crosswind-landing"));

            assertEquals(404, get(ios, "/api/scenarios/no-such-scenario").statusCode());

            HttpResponse<String> attachment = get(ios, "/api/scenario-attachments?file=XFSB-RWY09-ILS.txt");
            assertEquals(200, attachment.statusCode());
            assertEquals("ILS 09: 3.0 degree glidepath", attachment.body());

            HttpResponse<String> briefing = get(ios, "/briefing?scenario=crosswind-landing&trainee=Trainee%204");
            assertEquals(200, briefing.statusCode());
            assertTrue(briefing.body().contains("Crosswind landing"));

            assertEquals(200, get(ios, "/api/malfunctions").statusCode());
            assertEquals(200, get(ios, "/api/sessions").statusCode());
        }
    }

    private HttpResponse<String> get(IosServer ios, String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + ios.port() + path)).GET().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
