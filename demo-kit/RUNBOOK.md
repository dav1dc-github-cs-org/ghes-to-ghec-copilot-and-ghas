# Runbook

How to set up, rehearse and run both sessions with this kit. The plan behind it is in the
[session 1](../docs/cae-session-1-copilot-notes.md) and
[session 2](../docs/cae-session-2-ghas-notes.md) notes. Placeholders: `DEMO_ORG` is the sandbox
organisation, `DEMO_PRESENTER` your account with a Copilot Enterprise seat, and `DEMO_SEATLESS` an
account in the same organisation with **no** Copilot seat.

## The pieces

| Piece | Role |
|---|---|
| `flight-sim-platform` (showcase) | Everything on. Session 1 items 2–7; session 2 segments 7 and 9 |
| `flight-sim-platform-migrated` (migrated copy) | Same code "as it arrived". Session 1: nothing is scanned. Between sessions: switched on without AI features. Session 2: Part 1 screens and the step 7 payoff |
| Hero alert | `java/path-injection` in `ios-api/.../servlet/ScenarioAttachmentServlet.java`, in both repositories. Followed through both sessions |
| Homework | The showcase build fails on purpose: the Checkstyle config is fetched from `github-black.sim.example`. Fixed by Copilot Cloud Agent in session 1 |

What's planted — verified locally with the CodeQL code-scanning suites, the same queries default setup
runs (`demo-kit/scripts/verify-codeql-locally.sh`):

| Language | Alerts on `main` (15) |
|---|---|
| Java (8) | `java/path-injection` (the hero, `ScenarioAttachmentServlet`), `java/sql-injection` (`SessionRepository`), `java/ssrf` (`WeatherServlet`, reported at two sinks), `java/command-line-injection` (`DiagnosticsServlet`), `java/xss` (`BriefingServlet`), `java/xxe` (`ScenarioXmlImporter`), `java/unsafe-deserialization` (`SnapshotServlet`) |
| Python (7) | `py/path-injection`, `py/command-line-injection`, `py/full-ssrf`, `py/unsafe-deserialization` (YAML), `py/sql-injection`, `py/reflective-xss`, `py/flask-debug` — all in the QTG viewer |

The tar slip in `python/qtg-tools/src/qtg/packs.py` is reported only by the security-extended
suite: a talking point about query suites, not one of the counted alerts. PR A adds exactly one
alert (`java/zipslip`); `feature/gust-model` adds none.

Also planted: vulnerable Maven dependencies (SnakeYAML 1.33, jackson-databind 2.13.2, commons-text
1.9, commons-compress 1.20, H2 1.4.200) and pip dependencies (Flask 2.2.2, Werkzeug 2.2.2, Jinja2
3.1.2, requests 2.25.1). A licence key matching a custom pattern
(`ios-api/.../licensing/instructor-station.lic`). A database password only AI detection finds
(`data-recorder/.../recorder.properties`).

The local folders are the pristine **before** state. The homework is fixed on GitHub, never here;
don't re-sync the migrated copy after the session 1 fix.

---

## 1. Sandbox setup — by 2 October

### Organisation

1. **Accounts.** `DEMO_PRESENTER` with a Copilot Enterprise seat (Copilot Chat on alerts needs
   Enterprise). `DEMO_SEATLESS` with no seat, and admin on the migrated copy. Leave "Copilot code
   review for members without a licence" off, so `DEMO_SEATLESS` never sees a Copilot entry point.
2. **Copilot policies.** Enable Copilot Cloud Agent, Copilot Code Review and MCP servers. Leave
   previews (AI Scan for pull requests, extended metadata checks) off. Code Quality off.
3. **Teams** — the CODEOWNERS targets after the homework fix, each with **write** access to the
   showcase: `sim-core-maintainers`, `flight-model`, `hydraulics-electrical`, `fuel-engines`,
   `landing-gear`, `autoflight`, `navigation`, `scenario-tools`, `instructor-station`,
   `qtg-engineering`, `build-standards`.
4. **Custom secret pattern** (organisation → Security → Secret protection → Custom patterns):
   name "Instructor station licence key", secret format `FSLIC-[0-9]{4}(-[A-Z0-9]{4}){4}`. Dry-run,
   then publish. Push protection off: it's phase 2.
5. **Security configuration** "Sandbox baseline": dependency graph, Dependabot alerts, secret
   scanning with **Scan for AI-detected secrets** on, push protection off, code scanning default
   setup on GitHub-hosted runners, Copilot Autofix on.

### Answer the open question first: does the org default reach imported repositories?

Do this on a **throwaway** repository before creating the real ones:

1. Set "Sandbox baseline" as the default for new repositories.
2. Migrate a throwaway repository into `DEMO_ORG` with GEI: from GHES if we have an instance,
   otherwise GitHub.com organisation to organisation (`gh gei migrate-repo`).
3. Record whether the configuration attached. That decides how session 2 segment 6's second
   reason is worded, and whether it holds for GEI imports at all.
4. Unset the default before step 5 below. Keep the throwaway: under an enforced configuration it
   provides segment 4's "greyed-out setting" screenshot.

### Create the repositories

```bash
demo-kit/scripts/split-into-repos.sh ~/fsdemo --push DEMO_ORG
```

Then, in this order:

1. **Migrated copy first check:** no security configuration attached and code scanning off. If a
   configuration attached, detach it before any scan runs.
2. **Showcase:** attach "Sandbox baseline". Dependabot security updates **off** (alerts on), to
   keep the pull request list clean. Automatic Copilot code review **off** — item 5's lesson is
   the Request click; show the ruleset setting page instead.
3. Re-set "Sandbox baseline" as the default for **new** repositories (the segment 6 story).
4. Labels in both repositories: `demo-run`, `demo-keep`, `migration`, `build`.
5. Wait for the first CodeQL scan on the showcase, then check alerts, Dependabot alerts and both
   secret alerts (preflight 02 does this).
6. The `Copilot Setup Steps` workflow runs on the first push. It must be green, and it proves LFS
   and Maven Central work on a hosted runner.

### VS Code demo profile

1. Create a profile "FS demo" with Copilot signed in as `DEMO_PRESENTER`.
2. `demo-kit/scripts/configure-prompts.sh DEMO_ORG DEMO_PRESENTER DEMO_SEATLESS`, then add the
   printed `chat.promptFilesLocations` entry to the profile's settings.
3. Clone the showcase, open it in the profile, start the `github` MCP server from
   `.vscode/mcp.json`, and sign in. Type `/` in chat: `/00-reset-demo-state` and the rest appear.

### Check the homework is solvable

```bash
PYTHON=python3.13 demo-kit/answer-keys/verify-homework-fix.sh DEMO_ORG
```

---

## 2. Timeline

| By | Session 2 on 15 Oct | Session 2 on 21 Oct |
|---|---|---|
| 2 Oct | Sandbox setup and the GEI test; dependency graph lists every seeded version | same |
| 5 Oct | Two cloud agent rehearsals of the homework (label the better pull request `demo-keep` as the backup); a code review rehearsal; the Copilot Space; assign one showcase alert to Copilot to check item 6's seed line (agentic autofix preview), then close that PR | same |
| 6 Oct | `02-preflight-check` passes → nothing changes after this (out on the 7th) | same |
| 8 Oct morning | `00` → `01` → `02` | same |
| 8 Oct session | Screenshot prompt 15's empty answer — session 2's step 7 recap | same |
| 9 Oct | Merge the reviewed homework PR (the showcase build goes green). Capture the migrated copy's "off" screens, then switch it on (section 4). Prompt `20` | same |
| 12 Oct | Prompt `21` and the campaign's cloud agent PR; prompt `22` or screenshots from the customer's platform team; GHES screenshots; assemble the Part 1 answer bank | 16 Oct |
| 13 Oct | Full rehearsal: the Part 1 sweep, segment 2 timed at 11 minutes, questions stopped on time, the window-share switch; re-check agentic autofix | 19 Oct |
| 14 Oct | `29-preflight-session-2` passes → lock | 20 Oct |

---

## 3. Session 1 run sheet — Thursday 8 October

Share the VS Code window and one browser window, both in the demo profile.

| Item | Do | Show | If it goes wrong |
|---|---|---|---|
| Before | `/00-reset-demo-state` → "go", `/01-seed-demo-backlog`, `/02-preflight-check` | — | Fix every ❌ from its hint |
| 2 · Teams, permissions | Optional `/10-orient-in-inherited-repo`. Browser: the organisation's Teams page, then `.github/CODEOWNERS` | CODEOWNERS still names the old per-project organisations — "that's today's homework" | Skip prompt 10 if short of time |
| 3 · Actions | Open `.github/workflows/copilot-setup-steps.yml`: runner type (self-hosted is possible), `lfs: true`. Then `/11-hand-homework-to-cca` and leave it running | The agent session starting on the issue | Backup PR labelled `demo-keep` |
| 4 · GHAS + Copilot | Open `.vscode/mcp.json` (screenshot moment). `/12-what-does-copilot-see`, `/13-explain-and-draft-a-fix`, optional `/14-dependencies-and-secrets`, then `/15-same-question-just-migrated` | Real alerts; the fix drafted, not committed; the migrated copy returns nothing | **Afterwards run `git restore .`** — the hero alert must stay open until session 2 |
| 5 · Code Review | `/16-open-pr-for-review`. Browser: Reviewers → Copilot → **Request**; the severity labels; apply one suggestion; **Fix with Copilot** on the integer-division or degrees comment | Review in about 30 s; the Comment review, not an approval; the handoff to the agent | Talk through the answer key in `branches/showcase/feature-gust-model.md` |
| 6 · Cloud Agent | Browser: the homework session log (build fails, the agent reads the Checkstyle error, finds `build-tools/`, rebuilds). Open its PR. `/17-review-the-agents-pr`. Glance at the item 5 Fix with Copilot session | The agent never merges; you review | The `demo-keep` backup PR |
| 7 · Beyond the editor | `prompts/18-beyond-the-editor.web.md`: Copilot on GitHub.com, the Space, the GitHub Copilot App | — | Screenshots |
| 9 · Wrap | Take-away prompt `19` in the handout | — | — |

After the session: review and **merge** the homework PR, close the gust-model PR, and keep prompt
13's change discarded.

---

## 4. Between the sessions

1. **Migrated copy, "off" state.** Signed in as `DEMO_SEATLESS`: capture the Security and quality
   overview, Code scanning (not set up), Settings → Security and quality, and Insights → Dependency
   graph.
2. **Switch it on at repository level, capturing every click** (segment 4 fallback screens):
   dependency graph, Dependabot alerts, Dependabot security updates, secret scanning (**no** AI
   detection, **no** push protection), code scanning default setup (Java/Kotlin and Python
   detected), and **Copilot Autofix off**. Code Quality stays off. Attach no security
   configuration and set no custom properties.
3. After the first scan: alerts are there, Dependabot security update PRs appear (label one
   `demo-keep`), and the licence key alert is open. The password is *not* flagged.
4. `/20-build-part-1-pull-requests`, then open both PRs as `DEMO_SEATLESS` with the printed
   commands.
5. `/21-create-security-campaign`. In the campaign: assign the path traversal alert to Copilot
   (a cloud agent session opens a PR with you as reviewer — label it `demo-keep`), and one alert
   to `DEMO_PRESENTER`.
6. Optional: `/22-mirror-governance-pattern`. Otherwise ask platform engineering for screenshots of
   a governed repository.
7. **GHES 3.21**, if we have an instance: push the same code, and turn on GHAS with a
   `code-scanning` runner (Git and Python 3), GitHub Connect and the dependency graph. Take Part 1's
   screenshots there instead — on-prem developers' own screens, the same alerts.

### Part 1 answer bank

Collect these screenshots, cropped: PR A's Conversation tab and annotated diff, PR A's `pom.xml`
rich diff (dependency review), a Dependabot alert and its security update PR, the licence key
alert, PR B's `registries` diff, the three settings switches, Tool status, the dependency graph,
the "off" states from step 1, the default setup dialog and the greyed-out enforced setting. Every
answer in the pre-split questions comes from this set.

### Part 1 safety sweep (rehearsal)

As `DEMO_SEATLESS`, open every Part 1 page and look for: Copilot, Autofix, "Generate fix",
Campaigns, Code Quality, AI-detected. Crop or replace any page that shows one. In particular:

- the settings page lists Copilot Autofix and AI-detected secrets rows even when they're off —
  crop to the three switches;
- stay at repository level, because the organisation's security pages list Campaigns;
- the campaign must hold no alert from the migrated copy.

---

## 5. Session 2 run sheet

Before: `/28-reset-session-2` → "go", then `/29-preflight-session-2`.

**Part 1 (everyone) — screenshots, shared as the Part 1 window only.**

| Segment | Min | Show |
|---|---|---|
| 1 | 2 | — |
| 2 | 11 | Anchors: PR A (the finding on the PR, dependency review on the same PR) and PR B (`registries`). Quick looks: the Dependabot alert and PR; the licence key alert. Park questions for segment 5 |
| 3 | 6 | Settings, Tool status, dependency graph, and the "off" states |
| 4 | 6 | The switch-on sequence; the greyed-out enforced setting |
| 5 | 5 | Wrap-up, then 3 minutes of questions answered **only** from the answer bank. GitHub.com-only questions get rule 2's line; questions about the on-prem rollout go to the customer's host. Stop on time |

**The split.** The host restarts the recording. Stop sharing the Part 1 window and share the desktop,
where the Part 2 browser profile (`DEMO_PRESENTER`) and VS Code are already loaded.

**Part 2 (cloud only) — live.**

| Segment | Min | Show |
|---|---|---|
| 6 | 5 | Migrated copy: the organisation's security configurations page ("No configuration"), empty custom properties, no rules on `main` — "looks fine, governed by nothing". The showcase next to it, governed. Required workflows in a showcase PR's checks |
| 7 | 11 | Showcase Actions tab (CodeQL and Dependabot on hosted runners; the Jenkinsfile still in the repo); PR B for the network point; the **hero alert with its autofix**; the AI-detected password; Ask Copilot on an alert |
| 8 | 6 | Steps 4–6 in the browser (the copy fails, the showcase passes → platform engineering). Step 7: the 8 October screenshot of prompt 15's empty answer as the recap, then `/30-what-does-copilot-see-now`. Then the Visual Studio poll |
| 9 | 5 | The campaign → the hero alert with its autofix → assigned to Copilot → the finished PR (the CODEOWNERS team is requested). Optional `/31-review-the-campaign-pr` |
| 10 | 3 | Take-away prompt `19`; post the answers to the on-prem developers' chat questions |

---

## 6. Troubleshooting

| Symptom | Cause and fix |
|---|---|
| Prompts 12–15 say there are no security tools | `X-MCP-Toolsets` replaces the defaults. Check `.vscode/mcp.json`, restart the MCP server, and check the tools picker |
| Prompt 15 errors rather than saying "not enabled" | Fine: say "code scanning isn't on for this repo yet — that's session two" |
| The agent's build can't download dependencies | The firewall allowlist must include Maven Central and PyPI; check the setup-steps log |
| QTG tests fail with "Git LFS pointer" | The checkout has no LFS content; `lfs: true` in setup steps, `git lfs pull` locally |
| Autofix or Copilot visible on the migrated copy | Repository setting (Code scanning → Copilot Autofix: off), or you're signed in as the wrong account |
| No Dependabot PRs on the migrated copy | Security updates take a while after enabling; check Insights → Dependency graph → Dependabot |
| Local Python tests fail on 3.14 | The pinned Flask 2.2 predates 3.14. Use 3.10–3.13; CI and the agent use 3.12 |
