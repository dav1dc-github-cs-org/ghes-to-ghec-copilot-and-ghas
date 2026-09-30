package com.example.flightsim.scenario;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Checks a scenario against the limits of the FS-200 and the malfunctions the simulator models. */
public final class ScenarioValidator {

    /** Maximum usable fuel, kg (left 2,700 + centre 2,400 + right 2,700). */
    public static final double MAX_FUEL_KG = 7_800.0;
    /** Maximum take-off mass, kg. */
    public static final double MAX_TAKEOFF_MASS_KG = 23_000.0;
    /** Maximum operating altitude, ft. */
    public static final double MAX_ALTITUDE_FT = 37_000.0;
    /** Maximum operating speed, kt. */
    public static final double MAX_IAS_KT = 320.0;

    private final Set<String> knownMalfunctions;

    /**
     * Creates a validator.
     *
     * @param knownMalfunctions ids of the malfunctions registered by the subsystems
     */
    public ScenarioValidator(Set<String> knownMalfunctions) {
        this.knownMalfunctions = Set.copyOf(knownMalfunctions);
    }

    /**
     * Validates a scenario.
     *
     * @param scenario scenario to check
     * @return problems found, empty if the scenario is valid
     */
    public List<String> validate(Scenario scenario) {
        List<String> problems = new ArrayList<>();
        Scenario.InitialConditions ic = scenario.initialConditions();
        if (ic.fuelKg() < 0.0 || ic.fuelKg() > MAX_FUEL_KG) {
            problems.add("Fuel " + ic.fuelKg() + " kg is outside 0-" + MAX_FUEL_KG + " kg");
        }
        if (ic.grossMassKg() > MAX_TAKEOFF_MASS_KG) {
            problems.add("Gross mass " + ic.grossMassKg() + " kg exceeds maximum take-off mass");
        }
        if (ic.altitudeFt() < -1_000.0 || ic.altitudeFt() > MAX_ALTITUDE_FT) {
            problems.add("Altitude " + ic.altitudeFt() + " ft is outside the operating envelope");
        }
        if (ic.indicatedAirspeedKt() < 0.0 || ic.indicatedAirspeedKt() > MAX_IAS_KT) {
            problems.add("Airspeed " + ic.indicatedAirspeedKt() + " kt is outside the operating envelope");
        }
        if (ic.onGround() && ic.indicatedAirspeedKt() > 0.0) {
            problems.add("A scenario starting on the ground must start at zero airspeed");
        }
        if (ic.headingDeg() < 0.0 || ic.headingDeg() >= 360.0) {
            problems.add("Heading must be in [0, 360)");
        }
        for (Scenario.ScheduledMalfunction m : scenario.malfunctions()) {
            if (!knownMalfunctions.contains(m.malfunctionId())) {
                problems.add("Unknown malfunction '" + m.malfunctionId() + "'");
            }
            if (m.atSeconds() < 0.0) {
                problems.add("Malfunction '" + m.malfunctionId() + "' is scheduled before the session starts");
            }
        }
        return problems;
    }
}
