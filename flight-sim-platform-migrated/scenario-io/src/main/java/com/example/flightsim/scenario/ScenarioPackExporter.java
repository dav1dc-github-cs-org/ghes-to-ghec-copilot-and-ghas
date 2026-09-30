package com.example.flightsim.scenario;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

/**
 * Writes a scenario pack: a {@code .tar.gz} holding the scenario YAML and its attachments
 * (approach charts, briefing notes), for moving scenarios between training centres.
 */
public final class ScenarioPackExporter {

    private final ScenarioYaml yaml = new ScenarioYaml();

    /**
     * Writes a pack.
     *
     * @param scenario    scenario to export
     * @param attachments files to include under {@code attachments/}
     * @param out         destination; closed when the pack is complete
     * @throws IOException if an attachment cannot be read or the pack cannot be written
     */
    public void export(Scenario scenario, List<Path> attachments, OutputStream out) throws IOException {
        try (GzipCompressorOutputStream gzip = new GzipCompressorOutputStream(out);
             TarArchiveOutputStream tar = new TarArchiveOutputStream(gzip)) {
            tar.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
            addEntry(tar, "scenario.yaml", yaml.write(scenario).getBytes(StandardCharsets.UTF_8));
            for (Path attachment : attachments) {
                addEntry(tar, "attachments/" + attachment.getFileName(), Files.readAllBytes(attachment));
            }
            tar.finish();
        }
    }

    private static void addEntry(TarArchiveOutputStream tar, String name, byte[] content) throws IOException {
        TarArchiveEntry entry = new TarArchiveEntry(name);
        entry.setSize(content.length);
        tar.putArchiveEntry(entry);
        tar.write(content);
        tar.closeArchiveEntry();
    }
}
