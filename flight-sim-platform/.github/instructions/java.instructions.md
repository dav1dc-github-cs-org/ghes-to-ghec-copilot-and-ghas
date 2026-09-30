---
applyTo: "**/*.java"
---

# Java conventions

- Java 17 language level; the build compiles with `--release 17`.
- Checkstyle (`build-tools/checkstyle/flightsim-checks.xml`) fails the build on tabs, unused or
  redundant imports and `equals` without `hashCode`. It also expects braces on every block, no
  star imports and lines of at most 140 characters.
- Javadoc on public types and methods. Prefer `final` classes and records.
- Units: SI inside the simulation and radians for angles. Use `Units` for every conversion —
  never inline factors such as `0.5144`, `1.94` or `57.3`.
- Randomness: `SimContext.random()` only.
- Tests: JUnit Jupiter (`org.junit.jupiter.api`). One behaviour per test; physics checks use
  explicit tolerances and assert something.
- `ios-api` servlets return JSON through `JsonSupport`. Server-rendered HTML must pass every value
  through `Html.escape`.
