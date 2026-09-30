#!/usr/bin/env bash
# Copies the demo prompts, and the files they link to, to a folder outside every repository and
# fills in the sandbox names. Point the demo VS Code profile at the copy, so the prompts are
# available while the showcase repository is open, without this internal repository on screen.
#
# Usage: demo-kit/scripts/configure-prompts.sh <organisation> <presenter-login> <seatless-login> [target-dir]
#   organisation     sandbox organisation that owns flight-sim-platform and flight-sim-platform-migrated
#   presenter-login  your account with a Copilot Enterprise seat (campaign manager)
#   seatless-login   the account with no Copilot seat, used for session 2 Part 1
#   target-dir       defaults to ~/.flightsim-demo
set -euo pipefail

kit="$(cd "$(dirname "$0")/.." && pwd)"
org="${1:?organisation}"
presenter="${2:?presenter login}"
seatless="${3:?seatless login}"
target="${4:-$HOME/.flightsim-demo}"

rm -rf "$target"
mkdir -p "$target"
cp -R "$kit/prompts" "$kit/issues" "$kit/branches" "$kit/governance" "$target/"

# Fill in the names everywhere except the patches, which must apply byte for byte.
export DEMO_ORG_VALUE="$org" DEMO_PRESENTER_VALUE="$presenter" DEMO_SEATLESS_VALUE="$seatless"
find "$target" -type f ! -name '*.patch' -print0 \
  | xargs -0 perl -pi -e 's/DEMO_ORG/$ENV{DEMO_ORG_VALUE}/g; s/DEMO_PRESENTER/$ENV{DEMO_PRESENTER_VALUE}/g; s/DEMO_SEATLESS/$ENV{DEMO_SEATLESS_VALUE}/g'

if grep -rIl "DEMO_ORG\|DEMO_PRESENTER\|DEMO_SEATLESS" "$target" --exclude='*.patch'; then
  echo "ERROR: placeholders left in the files above" >&2
  exit 1
fi

cat <<EOF
Prompts configured for $org in $target/prompts

In the demo VS Code profile's settings.json:

  "chat.promptFilesLocations": {
    "$target/prompts": true
  }

Then open a clone of $org/flight-sim-platform in that profile and type / in Copilot Chat:
the prompts appear as /00-reset-demo-state, /01-seed-demo-backlog, ...
EOF
