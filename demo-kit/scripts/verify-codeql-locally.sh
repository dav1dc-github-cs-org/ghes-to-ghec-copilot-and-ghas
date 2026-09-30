#!/usr/bin/env bash
# Runs the CodeQL code-scanning query suites locally — the queries default setup runs — against
# the showcase and the demo branches, and checks the planted findings are still reported:
#   main                          15 alerts (8 Java, 7 Python), including the hero alert
#   feature/scenario-pack-import  exactly one new alert, java/zipslip (session 2 Part 1, PR A)
#   feature/gust-model            no new alerts (session 1 item 5 is a code review demo)
#
# Needs the GitHub CLI with the CodeQL extension: gh extension install github/gh-codeql
# Usage: demo-kit/scripts/verify-codeql-locally.sh
set -euo pipefail

kit="$(cd "$(dirname "$0")/.." && pwd)"
root="$(cd "$kit/.." && pwd)"
work="$(mktemp -d -t codeql-verify.XXXXXX)"
trap 'rm -rf "$work"' EXIT

copy() {
  rsync -a --exclude target/ --exclude .venv/ --exclude __pycache__/ --exclude .pytest_cache/ "$1/" "$2/"
}

analyse() {
  local name="$1" source="$2" language="$3"
  gh codeql database create "$work/db-$name-$language" --language="$language" --build-mode=none \
    --source-root="$source" --overwrite >/dev/null 2>&1
  gh codeql database analyze "$work/db-$name-$language" \
    "codeql/${language}-queries:codeql-suites/${language}-code-scanning.qls" --download \
    --format=sarif-latest --output="$work/$name-$language.sarif" --threads=0 >/dev/null 2>&1
}

echo "Analysing main (Java and Python)..."
copy "$root/flight-sim-platform" "$work/main"
analyse main "$work/main" java
analyse main "$work/main" python

echo "Analysing feature/scenario-pack-import and feature/gust-model (Java)..."
copy "$root/flight-sim-platform-migrated" "$work/pr-a"
(cd "$work/pr-a" && patch -s -p1 < "$kit/branches/migrated/feature-scenario-pack-import.patch")
analyse pr-a "$work/pr-a" java
copy "$root/flight-sim-platform" "$work/gust"
(cd "$work/gust" && patch -s -p1 < "$kit/branches/showcase/feature-gust-model.patch")
analyse gust "$work/gust" java

python3 - "$work" <<'EOF'
import json, sys
work = sys.argv[1]

def results(name):
    found = []
    for run in json.load(open(f"{work}/{name}.sarif"))["runs"]:
        for r in run.get("results", []):
            loc = r["locations"][0]["physicalLocation"]
            found.append((r["ruleId"], loc["artifactLocation"]["uri"], loc["region"]["startLine"]))
    return found

main_java, main_python = results("main-java"), results("main-python")
for rule, path, line in main_java + main_python:
    print(f"  {rule:32} {path}:{line}")

def new(branch):
    base = {(rule, path) for rule, path, _ in main_java}
    return [r for r in results(branch) if (r[0], r[1]) not in base]

checks = [
    ("main: 15 alerts", len(main_java) + len(main_python) == 15),
    ("main: hero alert present", any(r == "java/path-injection" and p.endswith("ScenarioAttachmentServlet.java")
                                     for r, p, _ in main_java)),
    ("PR A: exactly one new alert, java/zipslip", [r for r, _, _ in new("pr-a-java")] == ["java/zipslip"]),
    ("gust-model: no new alerts", new("gust-java") == []),
]
failed = False
for label, ok in checks:
    print(("PASS " if ok else "FAIL ") + label)
    failed |= not ok
sys.exit(1 if failed else 0)
EOF
