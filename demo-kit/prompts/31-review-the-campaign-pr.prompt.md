---
description: "Session 2, segment 9 (optional): review the campaign's cloud agent pull request the way prompt 17 reviewed the homework."
agent: agent
tools: ['github/list_pull_requests', 'github/pull_request_read', 'github/list_code_scanning_alerts', 'github/get_code_scanning_alert']
---
In `DEMO_ORG/flight-sim-platform`, find the open pull request Copilot opened from the security campaign for the path traversal in `ScenarioAttachmentServlet.java`.

1. Fetch the alert and summarise its data flow in two lines.
2. Does the diff close that flow — resolve against the root, normalise, check the result stays under the root? Give file-and-line evidence.
3. Does it add a test for a `../` request? Does it change anything else?
4. Who is requested for review, and is it approved yet?

Don't approve, merge or comment. No more than 15 lines.
