#!/usr/bin/env bash
# Builds flight-sim-platform-migrated/ — the repository "as it arrived" from github-black — from
# the showcase in flight-sim-platform/. Same code, same planted findings, but none of the files
# that were added for GitHub.com, and nothing that mentions Copilot, so the copy is safe to show
# to on-prem developers in session 2, Part 1.
#
# Usage:
#   demo-kit/scripts/sync-migrated-copy.sh          # regenerate the copy
#   demo-kit/scripts/sync-migrated-copy.sh --check  # exit 1 if the copy is out of date
set -euo pipefail

root="$(cd "$(dirname "$0")/../.." && pwd)"
showcase="$root/flight-sim-platform"
migrated="$root/flight-sim-platform-migrated"

# Added on GitHub.com, or Copilot-specific: never part of the migrated copy.
excludes=(
  --exclude=/.github/copilot-instructions.md
  --exclude=/.github/instructions/
  --exclude=/.github/workflows/
  --exclude=/.github/dependabot.yml
  --exclude=/.github/ISSUE_TEMPLATE/
  --exclude=/.vscode/
)
# Build and runtime output.
excludes+=(
  --exclude=target/ --exclude=.venv/ --exclude=__pycache__/ --exclude=.pytest_cache/
  --exclude='*.egg-info/' --exclude=/recordings/ --exclude=.DS_Store
)

if [[ "${1:-}" == "--check" ]]; then
  changes="$(rsync -a --delete --dry-run --itemize-changes "${excludes[@]}" "$showcase/" "$migrated/" | grep -v '^\.d' || true)"
  if [[ -n "$changes" ]]; then
    echo "flight-sim-platform-migrated is out of date:"
    echo "$changes"
    exit 1
  fi
else
  mkdir -p "$migrated"
  rsync -a --delete "${excludes[@]}" "$showcase/" "$migrated/"
fi

# The copy must never mention Copilot (Part 1 of session 2 is for on-prem developers).
if grep -rIil "copilot" "$migrated" --exclude-dir=target --exclude-dir=.venv; then
  echo "ERROR: the migrated copy mentions Copilot in the files above" >&2
  exit 1
fi
echo "flight-sim-platform-migrated is in sync with flight-sim-platform"
