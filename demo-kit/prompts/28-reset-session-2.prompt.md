---
description: "Session 2 morning: close what the rehearsals left behind. Every demo-keep asset stays."
agent: agent
tools: ['github/*', 'runCommands']
---
Reset `DEMO_ORG/flight-sim-platform` and `DEMO_ORG/flight-sim-platform-migrated` after session 2 rehearsals.

1. List open issues and pull requests labelled `demo-run`, and open pull requests authored by Copilot that are not labelled `demo-keep`.
   Never include anything labelled `demo-keep`: the Part 1 pull requests, the Dependabot pull requests on the migrated copy, and the campaign's cloud agent pull request.
2. Show them as one table and **wait until I reply "go"**. Then close them with the comment "Closed by demo reset".
3. On the showcase, list any security campaign alerts dismissed or closed during rehearsals; the hero alert (path traversal in `ScenarioAttachmentServlet.java`) must still be open. Report it; don't change it.
