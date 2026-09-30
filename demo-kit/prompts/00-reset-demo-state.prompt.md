---
description: "Prep, before each run: close what the last rehearsal left behind. Kept demo assets are never touched."
agent: agent
tools: ['github/*', 'runCommands']
---
Reset the demo repositories after a rehearsal: `DEMO_ORG/flight-sim-platform` (the showcase) and `DEMO_ORG/flight-sim-platform-migrated` (the migrated copy).

1. In both repositories, list:
   - open issues and pull requests labelled `demo-run`;
   - open pull requests whose head branch is `feature/gust-model`;
   - open pull requests authored by Copilot that are **not** labelled `demo-keep`;
   - branches named `copilot/*` whose pull request is closed.
   Never include anything labelled `demo-keep`.
2. Show everything you would close or delete as one table — type, number or branch, title, reason — then **stop and wait until I reply "go"**.
3. After "go": close those issues and pull requests with the comment "Closed by demo reset", and delete those branches with `gh api -X DELETE repos/<owner>/<repo>/git/refs/heads/<branch>`. Never delete `feature/gust-model` itself.
4. Confirm that `main` on the showcase still has the migration homework undone: `pom.xml` must still contain `github-black.sim.example`. If it doesn't, stop and tell me — the session 1 cloud agent demo depends on it.
