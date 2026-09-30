# Demo kit

Everything that drives the demos but never appears on screen: numbered Copilot prompt files, the
issue and pull request texts, branch patches, governance templates, the answer key and the
scripts that turn the two sub-folders into repositories.

**Start with [RUNBOOK.md](RUNBOOK.md).** It has the sandbox setup, the timeline and a run sheet for
each session.

| Path | What it is |
|---|---|
| [`prompts/`](prompts) | Numbered prompt files, run in filename order. The first digit is the phase: `0` prep, `1` session 1, `2` between sessions, `3` session 2 live. Gaps leave room for additions |
| [`issues/`](issues) | The build-migration issue handed to Copilot Cloud Agent in session 1 |
| [`branches/`](branches) | Patches and notes for the demo branches: `feature/gust-model` (session 1, Copilot Code Review) and the two session 2 Part 1 pull requests |
| [`governance/`](governance) | Optional custom property, ruleset and required workflows for session 2's check, steps 4–6 |
| [`answer-keys/`](answer-keys) | The expected fix for the build-migration issue, and a script that proves it works |
| [`scripts/`](scripts) | `sync-migrated-copy.sh`, `configure-prompts.sh`, `split-into-repos.sh`, `verify-codeql-locally.sh` |

## The prompts

| File | When | Where |
|---|---|---|
| `00-reset-demo-state` | Before each run | Prep |
| `01-seed-demo-backlog` | Before each run | Prep |
| `02-preflight-check` | 6 Oct and the morning of 8 Oct | Prep |
| `10-orient-in-inherited-repo` | Session 1, item 2 (optional) | VS Code |
| `11-hand-homework-to-cca` | Session 1, item 3 | VS Code, then browser |
| `12-what-does-copilot-see` | Session 1, item 4 | VS Code |
| `13-explain-and-draft-a-fix` | Session 1, item 4 | VS Code |
| `14-dependencies-and-secrets` | Session 1, item 4 (optional) | VS Code |
| `15-same-question-just-migrated` | Session 1, item 4 close | VS Code |
| `16-open-pr-for-review` | Session 1, item 5 | VS Code, then browser |
| `17-review-the-agents-pr` | Session 1, item 6 | VS Code |
| `18-beyond-the-editor.web.md` | Session 1, item 7 | Browser paste-ins |
| `19-homework-ask-what-copilot-sees` | Take-away (session 1 item 9; session 2 step 7) | Attendees |
| `20-build-part-1-pull-requests` | Between sessions | Prep |
| `21-create-security-campaign` | Between sessions | Prep |
| `22-mirror-governance-pattern` | Between sessions (optional) | Prep |
| `28-reset-session-2` | Session 2 morning | Prep |
| `29-preflight-session-2` | Session 2 morning | Prep |
| `30-what-does-copilot-see-now` | Session 2, segment 8, step 7 | VS Code |
| `31-review-the-campaign-pr` | Session 2, segment 9 (optional) | VS Code |

Session 2 Part 1 has no prompts on purpose: nothing Copilot-related is shown before the split.

The prompts use three placeholders — `DEMO_ORG`, `DEMO_PRESENTER` and `DEMO_SEATLESS`. Fill them in
with `scripts/configure-prompts.sh`, which copies the kit to `~/.flightsim-demo` for the demo VS
Code profile. Never edit the copies; change the originals here and run it again.

## Rules for this kit

- Nothing here goes on screen. The customer's name appears only in [`../docs`](../docs), never in
  the sandbox repositories, prompts, pull requests or issues.
- `flight-sim-platform-migrated/` is generated. Change `flight-sim-platform/` and run
  `scripts/sync-migrated-copy.sh`.
- The broken build in `flight-sim-platform/` is the session 1 homework. Don't fix it here; the
  answer key shows the expected fix.
