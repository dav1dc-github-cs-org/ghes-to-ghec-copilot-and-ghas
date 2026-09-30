---
description: "Session 1, item 5: open the gust-model pull request. Request Copilot Code Review in the browser."
agent: agent
tools: ['github/create_pull_request', 'github/list_pull_requests']
---
Open a pull request in `DEMO_ORG/flight-sim-platform` from `feature/gust-model` into `main`.

- Title: `Add discrete gust model for turbulence scenarios`
- Body, exactly:

  > Adds a one-minus-cosine discrete gust, with light continuous turbulence, for the crosswind and turbulence scenarios. The instructor sets the gust direction (degrees) and peak speed (knots) from the gust card on the weather page.
  >
  > Not wired into the flight model yet; that is the follow-up pull request.

  (Without the `>` quote markers.)
- Don't request any reviewers. I'll request Copilot in the browser.

If a pull request from that branch is already open, don't create another; give me its link. Reply with the link only.
