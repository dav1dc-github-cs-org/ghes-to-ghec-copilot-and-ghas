---
description: "Between sessions (optional): recreate the custom property → ruleset → required workflows pattern, governing the showcase only."
agent: agent
tools: ['runCommands']
---
Recreate the governance pattern in the `DEMO_ORG` sandbox organisation so session 2's check steps 4–6 can be shown live. It illustrates the pattern with neutral names; it is not the customer's actual configuration. Show every command, and wait for my "go" before any command that changes something.

1. Create the internal repository `DEMO_ORG/platform-workflows` if it doesn't exist, and push to its `main` the two workflows in [governance/platform-workflows](../governance/platform-workflows/.github/workflows/): `dependency-review.yml` and `msdo.yml`.
2. Define the organisation custom property from [custom-property.json](../governance/custom-property.json): `gh api -X PUT orgs/DEMO_ORG/properties/schema/product-line --input <file>`.
3. Set `product-line` to `flight-sim` on `flight-sim-platform` only (`PATCH orgs/DEMO_ORG/properties/values`). Leave `flight-sim-platform-migrated` without a value — that gap is the point of segment 6.
4. Create the organisation ruleset from [ruleset.json](../governance/ruleset.json), replacing `PLATFORM_WORKFLOWS_REPO_ID` with the id of `platform-workflows`: `gh api -X POST orgs/DEMO_ORG/rulesets --input <file>`.
5. Verify: `gh api repos/DEMO_ORG/flight-sim-platform/rules/branches/main` lists the ruleset's rules, and the same call for `flight-sim-platform-migrated` lists none.
