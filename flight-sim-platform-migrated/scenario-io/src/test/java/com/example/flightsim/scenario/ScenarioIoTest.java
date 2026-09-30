package com.example.flightsim.scenario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ScenarioIoTest {

    @TempDir
    Path temp;

    @Test
    void readsTheBundledScenario() throws IOException {
        Scenario scenario = readBundled();
        assertEquals("engine-failure-after-v1", scenario.id());
        assertEquals("XFSA", scenario.airport());
        assertTrue(scenario.initialConditions().onGround());
        assertEquals(1, scenario.malfunctions().size());
        assertEquals("engines.1.flameout", scenario.malfunctions().get(0).malfunctionId());
        assertEquals("250.0", scenario.toConfigOverrides().get("env.wind.from.deg"));
    }

    @Test
    void yamlRoundTrip() throws IOException {
        ScenarioYaml yaml = new ScenarioYaml();
        Scenario original = readBundled();
        Scenario copy = yaml.read(new StringReader(yaml.write(original)));
        assertEquals(original, copy);
    }

    @Test
    void rejectsADocumentThatIsNotAMapping() {
        ScenarioYaml yaml = new ScenarioYaml();
        assertThrows(ScenarioFormatException.class, () -> yaml.read(new StringReader("- just\n- a list\n")));
    }

    @Test
    void libraryListsAndLoadsById() throws IOException {
        Files.writeString(temp.resolve("engine-failure-after-v1.yaml"), new ScenarioYaml().write(readBundled()));
        ScenarioLibrary library = new ScenarioLibrary(temp);
        assertEquals(1, library.list().size());
        assertEquals("Engine failure after V1", library.load("engine-failure-after-v1").title());
        assertThrows(IllegalArgumentException.class, () -> library.load("../etc/passwd"));
    }

    @Test
    void validatorFlagsUnknownMalfunctionsAndEnvelopeExceedances() throws IOException {
        Scenario scenario = readBundled();
        assertEquals(List.of(), new ScenarioValidator(Set.of("engines.1.flameout")).validate(scenario));
        List<String> problems = new ScenarioValidator(Set.of()).validate(scenario);
        assertEquals(1, problems.size());
        assertTrue(problems.get(0).contains("engines.1.flameout"));
    }

    @Test
    void importsAnIosV2Export() throws IOException {
        String xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <scenario id="gear-extension-fault" version="2">
                  <title>Gear extension fault</title>
                  <description>Left main gear fails to extend; alternate extension.</description>
                  <airport icao="XFSB" runway="09"/>
                  <initial altitude-ft="3000" ias-kt="170" heading-deg="90" fuel-kg="2600" gross-mass-kg="19800"/>
                  <weather wind-from-deg="100" wind-speed-kt="8" temperature-c="11" qnh-hpa="1021"/>
                  <malfunction at-s="5" id="landing-gear.left-stuck-up"/>
                </scenario>
                """;
        Scenario scenario = new ScenarioXmlImporter().parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        assertEquals("gear-extension-fault", scenario.id());
        assertEquals(3000.0, scenario.initialConditions().altitudeFt());
        assertEquals(1021.0, scenario.weather().qnhHpa());
        assertEquals("landing-gear.left-stuck-up", scenario.malfunctions().get(0).malfunctionId());
    }

    @Test
    void briefingTemplateLeavesUnknownPlaceholdersVisible() {
        BriefingTemplate template = new BriefingTemplate("Brief ${trainee} on ${scenario}. Checked by ${checker}.");
        String text = template.render(Map.of("trainee", "Trainee 4", "scenario", "Engine failure after V1"));
        assertEquals("Brief Trainee 4 on Engine failure after V1. Checked by ${checker}.", text);
    }

    @Test
    void exportsAPackWithTheScenarioAndItsAttachments() throws IOException {
        Path chart = Files.writeString(temp.resolve("XFSA-RWY27-ILS.txt"), "ILS 27 approach notes");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ScenarioPackExporter().export(readBundled(), List.of(chart), out);

        List<String> names = new ArrayList<>();
        try (TarArchiveInputStream tar = new TarArchiveInputStream(
                new GzipCompressorInputStream(new ByteArrayInputStream(out.toByteArray())))) {
            TarArchiveEntry entry;
            while ((entry = tar.getNextTarEntry()) != null) {
                names.add(entry.getName());
            }
        }
        assertEquals(List.of("scenario.yaml", "attachments/XFSA-RWY27-ILS.txt"), names);
    }

    private static Scenario readBundled() throws IOException {
        try (InputStream in = ScenarioIoTest.class.getResourceAsStream("/scenarios/engine-failure-after-v1.yaml");
             Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            return new ScenarioYaml().read(reader);
        }
    }
}
