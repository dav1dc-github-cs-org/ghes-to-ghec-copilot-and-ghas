#!/usr/bin/env bash
# Turns flight-sim-platform/ and flight-sim-platform-migrated/ into two standalone Git
# repositories, ready to push to the sandbox organisation.
#
#   - Each repository gets one commit on main: "Import flight-sim-platform from github-black".
#   - The showcase also gets the feature/gust-model branch (session 1, item 5).
#   - The migrated copy's Part 1 branches are pushed later, between sessions, by prompt 20.
#   - Git LFS is enabled in both, so the QTG reference data is stored in LFS.
#
# Usage: demo-kit/scripts/split-into-repos.sh <out-dir> [--push <organisation>]
#   Without --push nothing leaves this machine. With --push the repositories are created on
#   GitHub.com as internal repositories and main plus the branches are pushed.
#
# Read the runbook before --push: the organisation's default security configuration applies to
# newly created repositories, and the migrated copy must arrive with code scanning OFF.
set -euo pipefail

kit="$(cd "$(dirname "$0")/.." && pwd)"
root="$(cd "$kit/.." && pwd)"
out="${1:?output directory}"
push_org=""
if [[ "${2:-}" == "--push" ]]; then
  push_org="${3:?organisation to push to}"
fi

command -v git-lfs >/dev/null 2>&1 || { echo "git-lfs is required" >&2; exit 1; }
"$kit/scripts/sync-migrated-copy.sh" --check

make_repo() {
  local name="$1" src="$2" dest="$out/$1"
  rm -rf "$dest"
  mkdir -p "$dest"
  rsync -a --exclude target/ --exclude .venv/ --exclude __pycache__/ --exclude .pytest_cache/ \
    --exclude '*.egg-info/' --exclude recordings/ --exclude .DS_Store "$src/" "$dest/"
  (
    cd "$dest"
    git init -q -b main
    git lfs install --local >/dev/null
    git add -A
    git commit -q -m "Import flight-sim-platform from github-black"
  )
  echo "created $dest"
}

add_branch() {
  local dest="$1" branch="$2" patch="$3" message="$4"
  (
    cd "$dest"
    git checkout -q -b "$branch" main
    git apply --index "$patch"
    git commit -q -m "$message"
    git checkout -q main
  )
  echo "  branch $branch"
}

mkdir -p "$out"
make_repo flight-sim-platform "$root/flight-sim-platform"
add_branch "$out/flight-sim-platform" feature/gust-model \
  "$kit/branches/showcase/feature-gust-model.patch" "Add discrete gust model for turbulence scenarios"
make_repo flight-sim-platform-migrated "$root/flight-sim-platform-migrated"

# Every branch must still compile; the build itself stays broken on purpose (the homework).
(
  cd "$out/flight-sim-platform"
  git checkout -q feature/gust-model
  ./mvnw -B -ntp -q -Dcheckstyle.skip -pl flight-dynamics -am test-compile
  git checkout -q main
)

if [[ -n "$push_org" ]]; then
  for name in flight-sim-platform flight-sim-platform-migrated; do
    (
      cd "$out/$name"
      gh repo create "$push_org/$name" --internal --source . --remote origin --push
      git push -q origin --all
    )
  done
  cat <<EOF

Pushed to $push_org. Now, before anything else:
  1. Check $push_org/flight-sim-platform-migrated has NO security configuration attached and
     code scanning OFF (Settings → Security and quality). Detach it if the organisation default
     attached one — session 1's "returns nothing" depends on it.
  2. Follow the runbook's "Sandbox setup" section for the showcase.
EOF
fi
