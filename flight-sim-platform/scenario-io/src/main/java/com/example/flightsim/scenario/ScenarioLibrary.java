package com.example.flightsim.scenario;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * The scenario library: one YAML file per scenario in a directory on the instructor station host.
 */
public final class ScenarioLibrary {

    private static final Pattern SCENARIO_ID = Pattern.compile("[a-z0-9][a-z0-9-]{0,63}");
    private static final String EXTENSION = ".yaml";

    private final Path root;
    private final ScenarioYaml yaml = new ScenarioYaml();

    /**
     * Creates a library over a directory.
     *
     * @param root directory holding {@code <id>.yaml} files
     */
    public ScenarioLibrary(Path root) {
        this.root = root.toAbsolutePath().normalize();
    }

    /**
     * Lists the scenarios in the library, sorted by id.
     *
     * @return scenario summaries
     * @throws IOException if the directory cannot be read
     */
    public List<ScenarioSummary> list() throws IOException {
        List<ScenarioSummary> summaries = new ArrayList<>();
        if (!Files.isDirectory(root)) {
            return summaries;
        }
        try (Stream<Path> files = Files.list(root)) {
            List<Path> scenarioFiles = files
                    .filter(p -> p.getFileName().toString().endsWith(EXTENSION))
                    .sorted(Comparator.comparing(Path::toString))
                    .toList();
            for (Path file : scenarioFiles) {
                Scenario scenario = read(file);
                summaries.add(new ScenarioSummary(scenario.id(), scenario.title(), scenario.airport(), scenario.tags()));
            }
        }
        return summaries;
    }

    /**
     * Loads one scenario by id.
     *
     * @param id scenario id, lower-case letters, digits and hyphens
     * @return the scenario
     * @throws IOException              if the file cannot be read
     * @throws IllegalArgumentException if the id is not a valid scenario id
     */
    public Scenario load(String id) throws IOException {
        if (id == null || !SCENARIO_ID.matcher(id).matches()) {
            throw new IllegalArgumentException("Not a valid scenario id");
        }
        Path file = root.resolve(id + EXTENSION).normalize();
        if (!file.startsWith(root)) {
            throw new IllegalArgumentException("Not a valid scenario id");
        }
        return read(file);
    }

    /**
     * Returns the library directory.
     *
     * @return absolute, normalised directory
     */
    public Path root() {
        return root;
    }

    private Scenario read(Path file) throws IOException {
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return yaml.read(reader);
        }
    }

    /**
     * What the instructor station lists before a scenario is opened.
     *
     * @param id      scenario id
     * @param title   title
     * @param airport airport identifier
     * @param tags    syllabus tags
     */
    public record ScenarioSummary(String id, String title, String airport, List<String> tags) {
    }
}
