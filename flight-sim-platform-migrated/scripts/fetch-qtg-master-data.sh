#!/usr/bin/env bash
# Downloads the approved QTG master reference data set (the latest release of
# qtg-reference-data) and unpacks it into the QTG tools reference-data directory.
# Usage: GHE_TOKEN=<token> scripts/fetch-qtg-master-data.sh [destination]
set -euo pipefail

API_URL="https://github-black.sim.example/api/v3"
REPO="flightsim/qtg-reference-data"
DEST="${1:-python/qtg-tools/reference-data}"

: "${GHE_TOKEN:?Set GHE_TOKEN to a token with read access to ${REPO}}"

asset_url="$(curl -fsSL -H "Authorization: token ${GHE_TOKEN}" \
  "${API_URL}/repos/${REPO}/releases/latest" \
  | python3 -c 'import json, sys; print(json.load(sys.stdin)["assets"][0]["url"])')"

archive="$(mktemp -t qtg-master.XXXXXX).tar.gz"
curl -fsSL -H "Authorization: token ${GHE_TOKEN}" -H "Accept: application/octet-stream" \
  -o "$archive" "$asset_url"

PYTHONPATH=python/qtg-tools/src python3 -m qtg.cli import-pack "$archive" --dest "$DEST"
rm -f "$archive"
