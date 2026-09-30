---
description: "Between sessions: push the two Part 1 branches to the migrated copy. The account with no Copilot seat opens the pull requests."
agent: agent
tools: ['runCommands']
---
Prepare session 2 Part 1's pull requests on `DEMO_ORG/flight-sim-platform-migrated`. Use the terminal and show each command before you run it.

1. Clone `DEMO_ORG/flight-sim-platform-migrated` into a new temporary directory.
2. For each branch, start from `main`, apply the patch from this kit with `git apply --index`, and commit with the given message:
   - `feature/scenario-pack-import` ← [feature-scenario-pack-import.patch](../branches/migrated/feature-scenario-pack-import.patch), message "Import scenario packs from other training centres"
   - `chore/dependabot-artifactory` ← [chore-dependabot-artifactory.patch](../branches/migrated/chore-dependabot-artifactory.patch), message "Let Dependabot sign in to Artifactory"
   Use my local git identity. Never add a `Co-authored-by` trailer: these commits are shown to on-prem developers.
3. Push both branches.
4. Don't open the pull requests. Print the two `gh pr create` commands for me to run after `gh auth switch --user DEMO_SEATLESS`, each with `--label demo-keep`. Take the titles and bodies from [the PR A notes](../branches/migrated/feature-scenario-pack-import.md) and [the PR B notes](../branches/migrated/chore-dependabot-artifactory.md).
