---
description: "Between sessions, after the homework pull request is merged: create the security campaign on the showcase's CodeQL alerts."
agent: agent
tools: ['github/list_code_scanning_alerts', 'runCommands']
---
Create session 2's security campaign in the `DEMO_ORG` organisation. Show every `gh api` call before you run it.

1. From `DEMO_ORG/flight-sim-platform`, list the open CodeQL alerts in `ios-api/` and `scenario-io/` — Java only. The path traversal in `ScenarioAttachmentServlet.java` must be one of them. Take nothing from `flight-sim-platform-migrated`: its alert pages appear in Part 1 and must not carry a campaign badge.
2. Get the showcase's repository id: `gh api repos/DEMO_ORG/flight-sim-platform --jq .id`.
3. Create the campaign with `gh api -X POST orgs/DEMO_ORG/campaigns --input -`, sending:
   - `name`: "Instructor station input handling"
   - `description`: "Input from the instructor station API reaches files, SQL, the shell, the XML parser and deserialisation. Fix before the next IOS release."
   - `managers`: ["DEMO_PRESENTER"]
   - `ends_at`: "${input:endsAt:2026-11-30T00:00:00Z}"
   - `code_scanning_alerts`: [{ "repository_id": the id from step 2, "alert_numbers": the numbers from step 1 }]
4. Reply with the campaign link and the runbook's manual steps: in the campaign, assign the path traversal alert to Copilot, and one other alert to DEMO_PRESENTER.
