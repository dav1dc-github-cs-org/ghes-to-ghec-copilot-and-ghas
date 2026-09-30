package com.example.flightsim.ios;

import com.example.flightsim.core.MalfunctionRegistry;
import com.example.flightsim.core.SimBus;
import com.example.flightsim.core.SimConfig;
import com.example.flightsim.core.SimContext;
import com.example.flightsim.core.math.DeterministicRandom;
import com.example.flightsim.fdm.FlightDynamicsModel;
import com.example.flightsim.ios.servlet.BriefingServlet;
import com.example.flightsim.ios.servlet.DiagnosticsServlet;
import com.example.flightsim.ios.servlet.HealthServlet;
import com.example.flightsim.ios.servlet.MalfunctionServlet;
import com.example.flightsim.ios.servlet.ScenarioAttachmentServlet;
import com.example.flightsim.ios.servlet.ScenarioServlet;
import com.example.flightsim.ios.servlet.SessionSearchServlet;
import com.example.flightsim.ios.servlet.SnapshotServlet;
import com.example.flightsim.ios.servlet.WeatherServlet;
import com.example.flightsim.recorder.FlightDataRecorder;
import com.example.flightsim.recorder.RecorderSettings;
import com.example.flightsim.recorder.SessionDatabase;
import com.example.flightsim.recorder.SessionRepository;
import com.example.flightsim.scenario.ScenarioLibrary;
import com.example.flightsim.systems.autopilot.Autopilot;
import com.example.flightsim.systems.electrical.Electrical;
import com.example.flightsim.systems.engines.Engines;
import com.example.flightsim.systems.fuel.Fuel;
import com.example.flightsim.systems.hydraulics.Hydraulics;
import com.example.flightsim.systems.landinggear.LandingGear;
import com.example.flightsim.systems.navigation.Navigation;
import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDate;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The instructor operator station (IOS) server: runs the simulation in real time and serves the
 * HTTP API the instructor station UI talks to.
 */
public final class IosServer implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(IosServer.class);

    private final Server server;
    private final ServerConnector connector;
    private final SimulationHost host;
    private final SessionDatabase database;

    private IosServer(Server server, ServerConnector connector, SimulationHost host, SessionDatabase database) {
        this.server = server;
        this.connector = connector;
        this.host = host;
        this.database = database;
    }

    /**
     * Starts the simulation and the HTTP API.
     *
     * @param config           server configuration
     * @param recorderSettings flight data recorder settings
     * @return running server
     * @throws Exception if the database or the HTTP connector cannot be started
     */
    public static IosServer start(IosConfig config, RecorderSettings recorderSettings) throws Exception {
        LicenceCheck.LicenceInfo licence = LicenceCheck.load(LicenceCheck.DEFAULT_RESOURCE, LocalDate.now());
        if (!licence.valid()) {
            LOG.warn("Instructor station licence for site {} expired on {}", licence.site(), licence.expires());
        }

        SessionDatabase database = SessionDatabase.open(recorderSettings);
        SessionRepository sessions = new SessionRepository(database);

        SimContext context = new SimContext(new SimBus(), new MalfunctionRegistry(),
                new DeterministicRandom(config.sessionSeed()), SimConfig.empty());
        SimulationHost host = new SimulationHost(context);
        // Producers before consumers: the autopilot and engines feed the flight model, which
        // feeds navigation and the recorder.
        host.register(new Autopilot(), 30);
        host.register(new Engines(), 30);
        host.register(new Fuel(), 10);
        host.register(new Electrical(), 20);
        host.register(new Hydraulics(), 20);
        host.register(new LandingGear(), 20);
        host.register(new FlightDynamicsModel(), 60);
        host.register(new Navigation(), 10);
        host.register(new FlightDataRecorder(database, recorderSettings), recorderSettings.sampleRateHz());

        ScenarioLibrary library = new ScenarioLibrary(config.trainingDataRoot().resolve("scenarios"));
        HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setHost(config.bindAddress());
        connector.setPort(config.port());
        server.addConnector(connector);

        ServletContextHandler api = new ServletContextHandler();
        api.setContextPath("/");
        api.addServlet(new ServletHolder(new HealthServlet(host, licence)), "/health");
        api.addServlet(new ServletHolder(new ScenarioServlet(library)), "/api/scenarios/*");
        api.addServlet(new ServletHolder(new ScenarioAttachmentServlet(config.trainingDataRoot().resolve("attachments"))),
                "/api/scenario-attachments");
        api.addServlet(new ServletHolder(new SessionSearchServlet(sessions)), "/api/sessions");
        api.addServlet(new ServletHolder(new MalfunctionServlet(host.malfunctions())), "/api/malfunctions");
        api.addServlet(new ServletHolder(new WeatherServlet(httpClient, config.metarFeed())), "/api/weather/metar");
        api.addServlet(new ServletHolder(new DiagnosticsServlet()), "/api/diagnostics/ping");
        api.addServlet(new ServletHolder(new SnapshotServlet(host)), "/api/snapshots");
        api.addServlet(new ServletHolder(new BriefingServlet(library)), "/briefing");
        server.setHandler(api);

        server.start();
        host.start();
        LOG.info("Instructor station API listening on {}:{} (site {})", config.bindAddress(), connector.getLocalPort(),
                licence.site());
        return new IosServer(server, connector, host, database);
    }

    /**
     * Returns the port the API is listening on.
     *
     * @return local port
     */
    public int port() {
        return connector.getLocalPort();
    }

    /**
     * Returns the simulation host.
     *
     * @return host
     */
    public SimulationHost host() {
        return host;
    }

    /**
     * Blocks until the server stops.
     *
     * @throws InterruptedException if interrupted while waiting
     */
    public void join() throws InterruptedException {
        server.join();
    }

    @Override
    public void close() throws Exception {
        host.close();
        server.stop();
        database.close();
    }

    /**
     * Starts the instructor station with the installed configuration.
     *
     * @param args ignored; use {@code -Dios.*} system properties to override settings
     * @throws Exception if start-up fails
     */
    public static void main(String[] args) throws Exception {
        IosServer ios = start(IosConfig.load(), RecorderSettings.defaults());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                ios.close();
            } catch (Exception e) {
                LOG.warn("Error while stopping the instructor station", e);
            }
        }, "ios-shutdown"));
        ios.join();
    }
}
