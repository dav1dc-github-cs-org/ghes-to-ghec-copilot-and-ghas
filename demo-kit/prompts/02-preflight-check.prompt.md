---
description: "Prep, 6 October and the morning of 8 October: pass/fail check that session 1's demo state is ready."
agent: agent
tools: ['github/*', 'runCommands']
---
Run the session 1 preflight for `DEMO_ORG/flight-sim-platform` (the showcase) and `DEMO_ORG/flight-sim-platform-migrated` (the migrated copy). Read only — change nothing.

Check each item and report one table: number, check, ✅ or ❌, the evidence, and for every ❌ what to fix.

1. The showcase has at least 12 open CodeQL alerts, including `java/path-injection` in `ScenarioAttachmentServlet.java` (the hero alert).
2. The showcase has open Dependabot alerts for both Maven and pip, at least one critical or high.
3. The showcase has open secret scanning alerts: the instructor station licence key (custom pattern) and the recorder database password (AI detection). Never print a secret value.
4. The migrated copy has **no** code scanning results: code scanning is not enabled, or there are no alerts.
5. Exactly one open issue labelled `migration` on the showcase, and it is unassigned.
6. Showcase `main` still points at the old server: `pom.xml` contains `github-black.sim.example`.
7. Branch `feature/gust-model` exists on the showcase and has no open pull request.
8. The latest `Copilot Setup Steps` run on the showcase succeeded: `gh run list -R DEMO_ORG/flight-sim-platform --workflow copilot-setup-steps.yml -L 1`.
9. A backup pull request from a rehearsal cloud agent session for the migration issue is open and labelled `demo-keep`.

End with one line: **READY** or **NOT READY**.
