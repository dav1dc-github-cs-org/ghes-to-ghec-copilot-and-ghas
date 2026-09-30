# Flight Sim Platform

Software for the FS-200 full-flight training simulator: the flight model, the aircraft systems,
scenario tooling, flight data recording and the instructor operator station (IOS) API, plus the
Python tools used to run the Qualification Test Guide (QTG).

The FS-200 is a generic twin-engine regional jet. The visual system image generators and the
motion platform controller are separate products, written in C++, and live in their own
repositories.

## Modules

| Module | What it does |
|---|---|
| [`sim-core`](sim-core) | Simulation executive, signal bus, units, maths and the malfunction registry |
| [`flight-dynamics`](flight-dynamics) | ISA atmosphere, aerodynamics, six-degree-of-freedom equations of motion, trim |
| [`subsystems`](subsystems) | Hydraulics, electrical, fuel, engines, landing gear, autopilot and navigation |
| [`scenario-io`](scenario-io) | Scenario model and YAML library, IOS v2 XML import, scenario packs |
| [`data-recorder`](data-recorder) | Session database and flight data recorder |
| [`ios-api`](ios-api) | HTTP API for the instructor operator station |
| [`legacy`](legacy) | Components ported from the previous-generation simulator host |
| [`python/qtg-tools`](python/qtg-tools) | QTG comparison tools and the QTG result viewer |
| [`build-tools`](build-tools) | Vendored copies of the platform build standards |
| [`training-data`](training-data) | Sample scenarios and attachments for running the IOS locally |

See [docs/architecture.md](docs/architecture.md) for how they fit together.

## Building

Java 17, using the Maven wrapper:

```bash
./mvnw -B -ntp verify
```

Jenkins builds with `-s ci/jenkins-settings.xml`, which resolves everything through Artifactory.
Builds anywhere else resolve from Maven Central.

QTG tools, with Python 3.10 or later:

```bash
cd python/qtg-tools
python3 -m venv .venv && . .venv/bin/activate
pip install -r requirements-dev.txt
python -m pytest
```

The QTG reference data is stored in Git LFS. Run `git lfs pull` after cloning, or the reference
data tests fail.

## Running the instructor station locally

```bash
scripts/run-ios-local.sh
curl http://localhost:8090/health
```

Scenarios and attachments are served from `training-data/`; sessions are recorded to
`recordings/` (not committed).

## Contributing

- Code owners in [.github/CODEOWNERS](.github/CODEOWNERS) are requested for review automatically.
- The coding standard is enforced by Checkstyle in the `validate` phase.
- This repository moved from github-black to GitHub.com; see [docs/MIGRATION.md](docs/MIGRATION.md).
