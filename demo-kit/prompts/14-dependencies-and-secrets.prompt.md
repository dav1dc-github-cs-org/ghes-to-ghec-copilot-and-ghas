---
description: "Session 1, item 4 (optional): Dependabot and secret scanning findings through the same MCP server."
agent: agent
tools: ['github/list_dependabot_alerts', 'github/get_dependabot_alert', 'github/list_secret_scanning_alerts', 'github/get_secret_scanning_alert']
---
For `DEMO_ORG/flight-sim-platform`:

1. **Dependencies.** Open Dependabot alerts as one table by package: ecosystem, current version, highest severity, first fixed version. Most urgent first.
2. **Secrets.** Open secret scanning alerts: what kind of secret, which file, and what the developer has to do. Say plainly that deleting the line doesn't remove the secret from Git history, so it has to be rotated.

Never print a secret value. Keep each part under 10 lines.
