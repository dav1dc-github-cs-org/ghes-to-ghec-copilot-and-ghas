# Build migration: make the build work after the move to GitHub.com

This repository moved from github-black to GitHub.com. The build no longer works:

```text
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-checkstyle-plugin:3.6.0:check (flightsim-coding-standard) on project flight-sim-platform: Failed during checkstyle execution: Unable to find configuration file at location: https://github-black.sim.example/raw/platform/build-standards/main/checkstyle/flightsim-checks.xml
```

Update the files in this repository that still point at the old server, so the build works from
GitHub.com. **Jenkins stays, SPL stays and Artifactory stays** — see `docs/MIGRATION.md`.

## In scope

- [ ] Build files that fetch anything from the old server (use the copies in `build-tools/`)
- [ ] URL, SCM and issue-management entries in `pom.xml`
- [ ] `Jenkinsfile` checkout URL and commit-status API URL
- [ ] `.github/CODEOWNERS` teams: per-project organisations become teams in this organisation
- [ ] Scripts under `scripts/`, and the URLs in `python/qtg-tools/pyproject.toml`

## Acceptance criteria

- `./mvnw -B -ntp verify` passes with the Checkstyle coding standard still enforced (no skipping, no weaker rules).
- QTG tools tests pass: `cd python/qtg-tools && python -m pytest`.
- No references to the old server remain in the repository, except in `docs/MIGRATION.md`.
- No changes to Artifactory, the Maven mirror, the pip index or `ci/jenkins-settings.xml`.
- No changes to Jenkins stages, and no dependency version changes.
- Anything platform engineering must change (Jenkins job, credentials, runners) is listed in the pull request description, not changed.
