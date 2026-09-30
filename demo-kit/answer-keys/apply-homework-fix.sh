#!/usr/bin/env bash
# Answer key for the session 1 homework issue ("Build migration: make the build work after the
# move to GitHub.com"). Applies the fix we expect Copilot cloud agent to make, so you can check
# the task is solvable, compare the agent's pull request, or use it as a fallback.
#
# Usage: apply-homework-fix.sh <path-to-flight-sim-platform-checkout> <github-organisation>
# Never run it against the demo repository itself before the session: the broken build is the demo.
set -euo pipefail

repo="${1:?path to a flight-sim-platform checkout}"
org="${2:?GitHub.com organisation that owns the repository}"
cd "$repo"

# 1. Coding standard: use the vendored copies in build-tools/ (parent, legacy, ios-api).
root_ref='${maven.multiModuleProjectDirectory}/build-tools/checkstyle'
sed -i.bak "s#https://github-black.sim.example/raw/platform/build-standards/main/checkstyle/flightsim-checks.xml#${root_ref}/flightsim-checks.xml#" pom.xml
sed -i.bak "s#https://github-black.sim.example/raw/platform/build-standards/main/checkstyle/flightsim-checks-legacy.xml#${root_ref}/flightsim-checks-legacy.xml#" legacy/pom.xml
sed -i.bak "s#https://github-black.sim.example/raw/platform/build-standards/v3.2/checkstyle/flightsim-checks.xml#${root_ref}/v3.2/flightsim-checks.xml#" ios-api/pom.xml

# 2. Project URLs in the parent POM.
sed -i.bak \
  -e "s#scm:git:https://github-black.sim.example/flightsim/flight-sim-platform.git#scm:git:https://github.com/${org}/flight-sim-platform.git#" \
  -e "s#scm:git:ssh://git@github-black.sim.example/flightsim/flight-sim-platform.git#scm:git:ssh://git@github.com/${org}/flight-sim-platform.git#" \
  -e "s#https://github-black.sim.example/flightsim/flight-sim-platform#https://github.com/${org}/flight-sim-platform#g" \
  pom.xml

# 3. Jenkinsfile: clone URL, repository slug and commit-status API. The credential id and the
#    Jenkins job itself belong to platform engineering and are listed in the PR instead.
sed -i.bak \
  -e "s#https://github-black.sim.example/api/v3#https://api.github.com#" \
  -e "s#https://github-black.sim.example/flightsim/flight-sim-platform.git#https://github.com/${org}/flight-sim-platform.git#" \
  -e "s#REPO_SLUG      = 'flightsim/flight-sim-platform'#REPO_SLUG      = '${org}/flight-sim-platform'#" \
  Jenkinsfile

# 4. CODEOWNERS: per-project organisations become teams in the new organisation.
sed -i.bak -E "s#@flightsim-[a-z]+/#@${org}/#g" .github/CODEOWNERS

# 5. Scripts.
sed -i.bak \
  -e 's#^GHE_HOST="github-black.sim.example"$##' \
  -e "s#^REPO=\"flightsim/flight-sim-platform\"#REPO=\"${org}/flight-sim-platform\"#" \
  -e 's#gh api --hostname "$GHE_HOST" -X GET#gh api -X GET#' \
  -e 's#Needs the GitHub CLI signed in to the server that hosts the repository.#Needs the GitHub CLI signed in to GitHub.com.#' \
  scripts/release-notes.sh
sed -i.bak \
  -e 's#^API_URL="https://github-black.sim.example/api/v3"#API_URL="https://api.github.com"#' \
  -e "s#^REPO=\"flightsim/qtg-reference-data\"#REPO=\"${org}/qtg-reference-data\"#" \
  -e 's#GHE_TOKEN#GITHUB_TOKEN#g' \
  scripts/fetch-qtg-master-data.sh

# 6. QTG tools project URLs.
sed -i.bak \
  -e "s#https://github-black.sim.example/flightsim/flight-sim-platform#https://github.com/${org}/flight-sim-platform#" \
  -e '/^Documentation = "https:\/\/github-black.sim.example/d' \
  python/qtg-tools/pyproject.toml

find . -name '*.bak' -delete

echo "Remaining references to the old server (only docs/MIGRATION.md expected):"
grep -rIl "github-black.sim.example" --exclude-dir=target --exclude-dir=.git . || true
