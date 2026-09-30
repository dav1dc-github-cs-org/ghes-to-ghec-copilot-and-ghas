---
description: "Session 2 morning: pass/fail check. Session 1's preflight in reverse — the migrated copy must now have findings."
agent: agent
tools: ['github/*', 'runCommands']
---
Run the session 2 preflight for `DEMO_ORG/flight-sim-platform` (the showcase) and `DEMO_ORG/flight-sim-platform-migrated` (the migrated copy). Read only — change nothing. Never print a secret value.

Report one table: number, check, ✅ or ❌, the evidence, and for every ❌ what to fix.

**Migrated copy (Part 1 and step 7)**
1. Open CodeQL alerts exist, including `java/path-injection` in `ScenarioAttachmentServlet.java`.
2. No security configuration attached: `gh api repos/DEMO_ORG/flight-sim-platform-migrated/code-security-configuration` returns nothing or 404.
3. No custom property values: `gh api repos/DEMO_ORG/flight-sim-platform-migrated/properties/values` returns `[]`.
4. No rules on `main`: `gh api repos/DEMO_ORG/flight-sim-platform-migrated/rules/branches/main` returns `[]`.
5. The pull request from `feature/scenario-pack-import` is open, labelled `demo-keep`, not authored by Copilot, and has a `java/zipslip` alert on its head (`code-scanning/alerts?ref=refs/pull/<n>/head`).
6. Dependency review sees `commons-io` 2.6 added: `gh api repos/DEMO_ORG/flight-sim-platform-migrated/dependency-graph/compare/main...feature/scenario-pack-import`.
7. The pull request from `chore/dependabot-artifactory` is open and labelled `demo-keep`.
8. At least one Dependabot security update pull request is open.
9. The licence key secret alert (custom pattern) is open. The recorder password is **not** flagged here — AI detection is off on this repository.

**Showcase (Part 2)**
10. The path traversal alert is open and has an autofix: `gh api repos/DEMO_ORG/flight-sim-platform/code-scanning/alerts/<n>/autofix`.
11. The recorder password is flagged by AI detection: secret scanning alerts with `secret_type=password`.
12. A security campaign is open with an end date after today, and contains the path traversal alert: `gh api orgs/DEMO_ORG/campaigns`.
13. The campaign's Copilot pull request is open, labelled `demo-keep`, and its checks passed.
14. If prompt 22 was run: `product-line` is `flight-sim` and the product-line ruleset applies to `main`.

End with one line: **READY** or **NOT READY**, plus the runbook's manual checks for the account with no Copilot seat.
