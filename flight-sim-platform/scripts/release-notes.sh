#!/usr/bin/env bash
# Drafts release notes from the pull requests merged since the last release tag.
# Usage: scripts/release-notes.sh [since-tag]
# Needs the GitHub CLI signed in to the server that hosts the repository.
set -euo pipefail

GHE_HOST="github-black.sim.example"
REPO="flightsim/flight-sim-platform"

since="${1:-$(git describe --tags --abbrev=0)}"
since_date="$(git log -1 --format=%cI "$since")"

echo "## Changes since ${since}"
echo
gh api --hostname "$GHE_HOST" -X GET search/issues \
  -f q="repo:${REPO} is:pr is:merged merged:>${since_date}" \
  --jq '.items[] | "- \(.title) (#\(.number))"'
