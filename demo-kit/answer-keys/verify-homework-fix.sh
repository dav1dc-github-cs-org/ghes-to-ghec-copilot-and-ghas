#!/usr/bin/env bash
# Proves the session 1 homework is solvable: copies the showcase to a temporary directory,
# confirms the build fails the way the demo expects, applies the answer key, and runs the full
# build with the coding standard enforced plus the QTG tools tests.
#
# Usage: demo-kit/answer-keys/verify-homework-fix.sh [organisation]
set -euo pipefail

kit="$(cd "$(dirname "$0")/.." && pwd)"
showcase="$(cd "$kit/../flight-sim-platform" && pwd)"
org="${1:-flightsim-demo}"
work="$(mktemp -d -t homework-fix.XXXXXX)"
trap 'rm -rf "$work"' EXIT

rsync -a --exclude target --exclude .venv --exclude recordings "$showcase/" "$work/flight-sim-platform/"
cd "$work/flight-sim-platform"

echo "== 1. Before the fix, the build must fail on the old-server coding standard URL"
if ./mvnw -B -ntp -q validate > "$work/before.log" 2>&1; then
  echo "FAIL: the build passed before the fix; the homework has nothing to do" >&2
  exit 1
fi
grep -q "Unable to find configuration file at location: https://github-black.sim.example" "$work/before.log" \
  && echo "ok: fails with the expected Checkstyle error"

echo "== 2. Apply the answer key"
"$kit/answer-keys/apply-homework-fix.sh" "$work/flight-sim-platform" "$org"

echo "== 3. Full build with the coding standard enforced"
./mvnw -B -ntp verify > "$work/after.log" 2>&1 || { tail -40 "$work/after.log"; exit 1; }
grep -E "Tests run: [0-9]+, Failures: 0, Errors: 0, Skipped: [0-9]+$" "$work/after.log" | tail -1
grep -E "BUILD SUCCESS" "$work/after.log"

echo "== 4. QTG tools tests"
python_bin="${PYTHON:-python3}"
"$python_bin" -m venv "$work/venv"
"$work/venv/bin/pip" install -q -r python/qtg-tools/requirements-dev.txt
(cd python/qtg-tools && "$work/venv/bin/python" -m pytest -q)

echo "== Homework answer key verified for organisation '$org'"
