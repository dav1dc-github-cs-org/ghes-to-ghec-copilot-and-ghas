package com.example.flightsim.scenario;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Reads scenarios exported by the previous-generation instructor station ("IOS v2" XML export).
 *
 * <p>Training centres still hold several hundred scenarios in this format. The export uses
 * imperial units and names every field explicitly:
 *
 * <pre>{@code
 * <scenario id="engine-failure-v1" version="2">
 *   <title>Engine failure after V1</title>
 *   <airport icao="XFSA" runway="27"/>
 *   <initial altitude-ft="0" ias-kt="0" heading-deg="270" fuel-kg="4200" gross-mass-kg="21500" on-ground="true"/>
 *   <weather wind-from-deg="250" wind-speed-kt="12" visibility-m="9999" cloud-base-ft="2500" temperature-c="18" qnh-hpa="1009"/>
 *   <malfunction at-s="38" id="engines.1.flameout"/>
 * </scenario>
 * }</pre>
 */
public final class ScenarioXmlImporter {

    /**
     * Parses an IOS v2 export.
     *
     * @param xml the export document
     * @return the scenario
     * @throws IOException             if the stream cannot be read
     * @throws ScenarioFormatException if the document is not an IOS v2 scenario export
     */
    public Scenario parse(InputStream xml) throws IOException {
        Document document;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setIgnoringComments(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(xml);
        } catch (ParserConfigurationException | SAXException e) {
            throw new ScenarioFormatException("Not a valid IOS v2 scenario export: " + e.getMessage(), e);
        }
        return fromDocument(document);
    }

    private static Scenario fromDocument(Document document) {
        Element root = document.getDocumentElement();
        if (!"scenario".equals(root.getLocalName() == null ? root.getNodeName() : root.getLocalName())) {
            throw new ScenarioFormatException("Root element must be <scenario>");
        }
        Element airport = child(root, "airport");
        Element initial = child(root, "initial");
        Element weather = optionalChild(root, "weather");

        Scenario.InitialConditions conditions = new Scenario.InitialConditions(
                number(initial, "altitude-ft", 0.0),
                number(initial, "ias-kt", 0.0),
                number(initial, "heading-deg", 0.0),
                number(initial, "fuel-kg", 4000.0),
                number(initial, "gross-mass-kg", 20_000.0),
                Boolean.parseBoolean(initial.getAttribute("on-ground")));

        Scenario.Weather standard = Scenario.Weather.standard();
        Scenario.Weather wx = weather == null ? standard : new Scenario.Weather(
                number(weather, "wind-from-deg", standard.windFromDeg()),
                number(weather, "wind-speed-kt", standard.windSpeedKt()),
                number(weather, "visibility-m", standard.visibilityM()),
                number(weather, "cloud-base-ft", standard.cloudBaseFt()),
                number(weather, "temperature-c", standard.temperatureC()),
                number(weather, "qnh-hpa", standard.qnhHpa()));

        List<Scenario.ScheduledMalfunction> malfunctions = new ArrayList<>();
        NodeList nodes = root.getElementsByTagName("malfunction");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            malfunctions.add(new Scenario.ScheduledMalfunction(number(element, "at-s", 0.0), element.getAttribute("id")));
        }

        return new Scenario(
                root.getAttribute("id"),
                textOf(root, "title"),
                textOf(root, "description"),
                airport.getAttribute("icao"),
                airport.getAttribute("runway"),
                conditions,
                wx,
                malfunctions,
                List.of("imported", "ios-v2"));
    }

    private static Element child(Element parent, String name) {
        Element element = optionalChild(parent, name);
        if (element == null) {
            throw new ScenarioFormatException("Missing <" + name + "> element");
        }
        return element;
    }

    private static Element optionalChild(Element parent, String name) {
        NodeList nodes = parent.getElementsByTagName(name);
        return nodes.getLength() == 0 ? null : (Element) nodes.item(0);
    }

    private static String textOf(Element parent, String name) {
        Element element = optionalChild(parent, name);
        return element == null ? "" : element.getTextContent().trim();
    }

    private static double number(Element element, String attribute, double fallback) {
        String value = element.getAttribute(attribute);
        if (value.isEmpty()) {
            return fallback;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            throw new ScenarioFormatException("Attribute " + attribute + " must be a number", e);
        }
    }
}
