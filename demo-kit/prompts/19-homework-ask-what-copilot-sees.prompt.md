---
description: "Take-away (session 1 homework and session 2 check, step 7): ask Copilot what it sees in your repository's security findings."
agent: agent
tools: ['github/list_code_scanning_alerts', 'github/get_code_scanning_alert', 'github/list_dependabot_alerts']
---
What CodeQL findings are open on ${input:repository:owner/repository}, and which should I fix first?

- Use the repository's code scanning alerts (tool CodeQL, state open); don't guess from the code.
- If code scanning isn't enabled, or has no results yet, say so in one sentence and point me to Settings → Security and quality → Code scanning.
- Otherwise: a short table by severity and rule, then the three to fix first, each with file, line and one sentence on why.
- Finish with one line on open Dependabot alerts.

This needs the GitHub MCP server with the security toolsets turned on. In VS Code, add this to `.vscode/mcp.json`:

```json
{
  "servers": {
    "github": {
      "type": "http",
      "url": "https://api.githubcopilot.com/mcp/",
      "headers": {
        "X-MCP-Toolsets": "context,repos,issues,pull_requests,users,code_security,secret_protection,dependabot"
      }
    }
  }
}
```
