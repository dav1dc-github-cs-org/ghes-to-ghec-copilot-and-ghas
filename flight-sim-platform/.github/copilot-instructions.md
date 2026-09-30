# Copilot instructions — Flight Sim Platform

Software for the FS-200 full-flight training simulator: Java 17 (Maven multi-module) plus a
Python tool for the Qualification Test Guide in `python/qtg-tools`. Start with
`docs/architecture.md` for the module map and `docs/MIGRATION.md` for the move to GitHub.com.

## Build and test

- Java, from the repository root: `./mvnw -B -ntp verify`. Always use the wrapper.
- One module and what it depends on: `./mvnw -B -ntp -pl <module> -am verify`.
- Python: `cd python/qtg-tools && pip install -r requirements-dev.txt && python -m pytest`.
- The Checkstyle coding standard runs in the `validate` phase and must stay enforced. Never skip
  it (`-Dcheckstyle.skip`) or weaken the rules to make a build pass.
- QTG reference data under `python/qtg-tools/reference-data/` is stored in Git LFS. If the
  reference data tests report LFS pointer files, the checkout is missing LFS content — do not
  change the tests or the data.

## Where builds run

- Jenkins builds this repository inside the training network and resolves everything through
  Artifactory (`ci/jenkins-settings.xml`). Builds on GitHub-hosted runners — including yours —
  resolve from Maven Central and PyPI.
- Do not change Artifactory, Maven mirror, pip index or Jenkins stage configuration. Those belong
  to platform engineering; list anything they need to change in the pull request description.

## Simulation conventions

- SI units inside the simulation: metres, m/s, kg, newtons, pascals, kelvin. **Angles are
  radians.** Convert only at the edges (scenario files, instructor input, display) with
  `com.example.flightsim.core.units.Units`.
- All randomness comes from `SimContext.random()` or a `fork(name)` of it, so a recorded session
  replays exactly (ADR 0004). Never use `java.util.Random`, `Math.random()` or an unseeded Python
  `random` in simulation code.
- Components talk only through the `SimBus`; shared signal names live in `core.Signals`
  (ADR 0001).
- Malfunction ids are `<component>.<failure>` and are registered with their ATA chapter.

## Security expectations for new and changed code

- SQL: parameterised statements only (`PreparedStatement`; `?` placeholders in Python).
- Files: resolve user-supplied names against a fixed root, `normalize()` and check the result is
  still under the root.
- Never pass request input to a shell, `Runtime.exec` or `subprocess` with `shell=True`.
- XML: disable DTDs and external entities. Python YAML: `yaml.safe_load`.
- No Java serialization of data that comes from outside the process.
- Never commit credentials.

## Pull requests

- Keep changes scoped to the issue. Say which modules you touched and how you tested them.
- Add or update tests with every behaviour change.
