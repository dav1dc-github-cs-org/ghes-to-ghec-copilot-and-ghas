---
description: "Session 1, item 4: explain the top alert's data flow and draft a fix. You decide whether to keep it."
agent: agent
tools: ['github/get_code_scanning_alert', 'github/list_code_scanning_alerts', 'search', 'usages', 'edit']
---
Take the open `java/path-injection` alert in `ios-api/src/main/java/com/example/flightsim/ios/servlet/ScenarioAttachmentServlet.java` on `DEMO_ORG/flight-sim-platform`.

1. Fetch the alert and explain its data flow in plain English, from the request parameter to the file read, in three or four short steps with line numbers.
2. Open the file in this workspace and draft the smallest fix that keeps the feature working: resolve the requested name against the attachments directory, normalise it, return 404 for anything that ends up outside that directory, and keep the content types as they are.
3. Add a test to `IosServerSmokeTest` that requests `../` and expects 404.
4. Stop there. Don't run the build and don't commit — I'll decide whether to keep it.
