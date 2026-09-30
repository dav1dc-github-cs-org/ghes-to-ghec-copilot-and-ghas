package com.example.flightsim.scenario;

import java.io.Reader;
import java.util.Map;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

/** Reads and writes scenarios in the YAML format used by the scenario library. */
public final class ScenarioYaml {

    /**
     * Reads one scenario.
     *
     * @param reader YAML source
     * @return the scenario
     * @throws ScenarioFormatException if the document is not a valid scenario
     */
    public Scenario read(Reader reader) {
        Yaml yaml = new Yaml(new SafeConstructor(new LoaderOptions()));
        Object document = yaml.load(reader);
        if (!(document instanceof Map<?, ?> map)) {
            throw new ScenarioFormatException("A scenario file must contain a single YAML mapping");
        }
        return ScenarioMapper.fromMap(map);
    }

    /**
     * Writes one scenario.
     *
     * @param scenario scenario to write
     * @return YAML text
     */
    public String write(Scenario scenario) {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setIndent(2);
        options.setPrettyFlow(true);
        return new Yaml(options).dump(ScenarioMapper.toMap(scenario));
    }
}
