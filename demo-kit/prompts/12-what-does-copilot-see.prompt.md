---
description: "Session 1, item 4: what CodeQL findings are open on this repo, and which should I fix first?"
agent: agent
tools: ['github/list_code_scanning_alerts', 'github/get_code_scanning_alert']
---
What CodeQL findings are open on `DEMO_ORG/flight-sim-platform`, and which should I fix first?

How to answer — this is on a projector:
- Use the repository's code scanning alerts (state open, tool CodeQL). Don't read the source code to guess.
- One table: severity, rule, file, count. Java first, then Python.
- Then **Fix first**: the top three, each with file and line and one sentence on why — whether it is reachable from the instructor station's HTTP API or the QTG viewer, its severity, and what an attacker could do with it.
- No more than 20 lines.
