---
description: "Session 2, segment 8, step 7: the same question as session 1's prompt 15, on the same repository. Now it has findings."
agent: agent
tools: ['github/list_code_scanning_alerts', 'github/get_code_scanning_alert', 'github/list_dependabot_alerts']
---
What CodeQL findings are open on `DEMO_ORG/flight-sim-platform-migrated`, and which should I fix first?

How to answer — this is on a projector:
- Use the repository's code scanning alerts (state open, tool CodeQL). Don't read the code to guess.
- One short table by severity and rule, then the three to fix first, each with file, line and one sentence on why.
- Then one line on open Dependabot alerts: how many, and the most severe package.
- No more than 20 lines.
