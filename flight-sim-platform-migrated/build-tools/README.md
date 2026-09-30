# build-tools

Vendored copies of the platform build standards, so a build does not depend on
reaching the server that publishes them.

| Standard | Published at | Vendored copy |
|---|---|---|
| Current coding standard | `platform/build-standards` @ `main`, `checkstyle/flightsim-checks.xml` | `checkstyle/flightsim-checks.xml` |
| Relaxed standard for ported code | `platform/build-standards` @ `main`, `checkstyle/flightsim-checks-legacy.xml` | `checkstyle/flightsim-checks-legacy.xml` |
| Coding standard v3.2 | `platform/build-standards` @ `v3.2`, `checkstyle/flightsim-checks.xml` | `checkstyle/v3.2/flightsim-checks.xml` |

The platform team refreshes these copies when a standard changes. Do not edit
them in place; raise a change on `platform/build-standards` instead.

Maven sets `maven.multiModuleProjectDirectory` to the repository root (the
directory that contains `.mvn/`), so a module can reference a vendored copy as
`${maven.multiModuleProjectDirectory}/build-tools/checkstyle/<file>`.
