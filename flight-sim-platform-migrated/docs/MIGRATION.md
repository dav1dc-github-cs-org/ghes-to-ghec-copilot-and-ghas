# Moving to GitHub.com

This repository moved from github-black (GitHub Enterprise Server) to GitHub.com as part of the
platform consolidation: hundreds of per-project organisations are becoming a small number of
organisations, with separation by team. Most of the repository came across unchanged. This page
lists what needs attention afterwards and who owns each item.

## What does not change

- **Jenkins stays.** The Jenkins job keeps building this repository; platform engineering repoints
  the job and its credentials at GitHub.com.
- **SPL stays.**
- **Artifactory stays.** Jenkins keeps resolving and publishing through Artifactory
  (`ci/jenkins-settings.xml`). Do not change repository, mirror or index settings.

## Your change: files in this repository

Anything in the repository that reaches `github-black.sim.example` stops working after the move.
GitHub-hosted runners and machines outside the training network cannot reach it, and it is being
retired for this product line.

| Where | What to do |
|---|---|
| Build files that fetch from `github-black.sim.example` | Use the vendored copies in `build-tools/` (see its README) |
| `pom.xml` `<url>`, `<scm>` and `<issueManagement>` | Point at `https://github.com/<organisation>/flight-sim-platform` |
| `Jenkinsfile` checkout URL and commit-status API | Use the GitHub.com clone URL and `https://api.github.com` |
| `.github/CODEOWNERS` | Per-project organisations are gone: `@flightsim-<area>/<team>` becomes `@<organisation>/<team>`. The teams already exist, with the same members |
| `scripts/` | Use `github.com` and `api.github.com`, and `gh` without `--hostname` |
| `python/qtg-tools/pyproject.toml` | Point the Repository URL at GitHub.com; drop the github-black Pages documentation URL until the documentation site moves |

## Platform engineering's change: list it, don't change it

- The Jenkins job configuration and the `ghe-black-ci-token` credential, which needs a GitHub.com
  token.
- Artifactory, Maven mirrors and the pip index.
- Git LFS storage.
- Organisation rulesets, required workflows and repository custom properties.

## Things that behave differently on GitHub.com

- Your account is a managed user account: you cannot fork repositories outside the enterprise,
  create gists or create public repositories.
- History from before the move may show unmapped authors until mannequins are reclaimed.
- Code scanning results, Dependabot alerts and secret scanning states were not migrated.
