# Architecture

## The simulation loop

The `SimExecutive` in `sim-core` runs at a fixed 60 Hz frame rate. Each model is a
`SimComponent` registered with its own rate, which must divide the frame rate: the flight model
runs every frame, most aircraft systems at 20 or 30 Hz. Components are stepped in registration
order, so producers are registered before consumers.

Components never call each other. They publish and read named signals on the `SimBus`
([ADR 0001](adr/0001-signal-bus.md)). Shared signal names are defined in `core.Signals`; each
subsystem defines its own internal names in a `<Module>Signals` class.

```text
controls ──► flight-dynamics ──► fdm.*, env.* ──► autopilot ──► ctl.elevator / ctl.aileron
                 ▲                                    
engines ── eng.<n>.thrust.n                           navigation ◄── fdm.lat / fdm.lon
   ▲  │                                              
fuel ◄┘ eng.<n>.fuel-flow.kgps   hydraulics ◄── eng.<n>.n2.pct, elec.ac1.volts
                                  landing-gear ◄── hyd.green.pressure.psi, ctl.gear-lever.down
```

## Units and angles

Inside the simulation everything is SI and every angle is in radians. Scenario files, instructor
input and displays use feet, knots and degrees; they are converted at the edge with
`core.units.Units`. Unit mistakes are the most common defect class in this codebase, so review
every conversion.

## Randomness and replay

A recorded session must replay exactly, for the QTG and for debriefs. All randomness —
turbulence, sensor noise, failure timing — comes from the session's `DeterministicRandom`
([ADR 0004](adr/0004-deterministic-randomness.md)).

## Malfunctions

Each subsystem registers the malfunctions it models with the `MalfunctionRegistry`, with an ATA
chapter (29 hydraulic power, 24 electrical, 28 fuel, 72 engine, 32 landing gear, 22 autoflight,
34 navigation). The instructor inserts and clears them through the IOS API; scenarios can
schedule them.

## Instructor operator station (IOS) API

`ios-api` embeds Jetty and runs the simulation in real time next to the HTTP API used by the
instructor station UI.

| Endpoint | Purpose |
|---|---|
| `GET /health` | Status, uptime and licence details |
| `GET /api/scenarios`, `GET /api/scenarios/<id>` | Scenario library |
| `POST /api/scenarios/import` | Preview a scenario exported from the previous IOS (IOS v2 XML) |
| `GET /api/scenario-attachments?file=` | Charts and briefing notes for a scenario |
| `GET /api/sessions?trainee=` | Session history for debriefs |
| `GET`/`POST /api/malfunctions` | List, insert and clear malfunctions |
| `GET /api/weather/metar` | Live weather for weather scenarios |
| `GET /api/diagnostics/ping?host=` | Connectivity check for the visual and motion systems |
| `GET`/`POST /api/snapshots` | Save and restore the simulator state |
| `GET /briefing?scenario=&trainee=` | Printable pre-flight briefing |

## Flight data recorder

`data-recorder` samples configured signals at 20 Hz into the session database, an embedded H2
database on the IOS host that the debrief station opens after the session.

## QTG tools

`python/qtg-tools` compares simulator output against flight-test reference data for each QTG test
case, within the tolerances in `qtg/testcases.py`, and renders a report. The reference data is in
Git LFS. The QTG result viewer is a small Flask app used by simulator engineering on the training
network.

## Around this repository

- Visual system image generators and the motion platform controller are separate C++ products.
- The previous-generation simulator host is being retired; code worth keeping was ported into
  `legacy` ([ADR 0007](adr/0007-legacy-module.md)).
- Jenkins builds and publishes through Artifactory. Its pipeline and the SPL step are unchanged by
  the move to GitHub.com ([MIGRATION.md](MIGRATION.md)).
