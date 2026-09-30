# Session 1 — Your repositories on GitHub.com

**Part of [CAE developer enablement](cae-overview.md)**, which holds the
calendar, delivery model, shared vocabulary and the open items that affect
both sessions.

*Was "GitHub Copilot on the platform" until the customer added teams,
permissions and Actions on 22 September. Copilot is still the centre of
gravity — 56 of 90 minutes — but the title should match the room.*

**Thursday 8 October, 13:00–14:30 ET, 90 minutes** — first in the sequence.
The 13:00 start answers Élodie's ask for an earlier slot, and the session ends
well before the room empties around 16h.

**The invite went out on 23 September as *GitHub SaaS Capabilities: What's New
in GitHub Cloud*.** Put that title on the title slide and the recording, so
anyone who finds the recording later can match it to the invite. **The plan is
what we teach** — validated with Kevin on 23 September. The email is kept as a
record only; see [the session 1 invite](cae-session-1-copilot-invite.md).

## The frame: one move, three consequences

⚠️ **This frame was rewritten after the customer added teams, permissions and
Actions.** The session is no longer purely about Copilot — it is platform
onboarding with a Copilot centre of gravity. Say the frame this way and the
additions feel designed rather than bolted on.

**One thing happened: your repositories moved to GitHub.com.** Three things
follow from it, and they are the whole session:

1. **They now live under a managed-user enterprise**, which has rules your
   GHES accounts did not — item 2a.
2. **They now live in a different org and team structure**, and alongside
   capabilities the closed appliance could not offer — items 2b and 3.
3. **Copilot and Advanced Security can now see them**, which is where most of
   the new capability sits — items 4 to 7.

**What did not change:** their Copilot licence, their EMU identity, the way
they work in VS Code, and their Jenkins and SPL build path.

| Worked before, on GHES | New, because the repo is on GitHub.com |
|---|---|
| Inline completions and next edit suggestions | Copilot can read your CodeQL, secret scanning and Dependabot findings |
| Ask, Edit and Agent mode on open files | Copilot Code Review (or CCR) on pull requests |
| `.github/copilot-instructions.md` | Copilot Cloud Agent (or CCA) — assign an issue, get a PR |
| Anything operating on the local working tree | Copilot on GitHub.com, Spaces and the GitHub Copilot App |
| Your Jenkins and SPL build path | GitHub-hosted runners, and a public Actions ecosystem a closed network could not reach |

> ⚠️ **Do not present the local-only capabilities as unlocked by the
> migration.** Fred's team already corrected one GitHub assumption in August;
> this room will catch an over-claim and it costs credibility for the rest of
> the programme.
>
> **Cross-org code search and issues-as-chat-context were cut** when the
> customer added their two topics. Both are genuinely new and genuinely good —
> the strongest candidates if time is ever found, given ~400 orgs are
> collapsing into a handful.

| # | Topic | Min | Status |
|---|---|---|---|
| 1 | What actually changed — and what didn't | 4 | frame |
| 2 | Teams, permissions and repository administration | 13 | new — customer-requested |
| 3 | Actions and CI/CD: what's now possible | 12 | new — customer-requested |
| 4 | **GHAS + Copilot: better together** | 16 | new |
| 5 | Copilot Code Review (or CCR) on pull requests | 15 | new |
| 6 | Copilot Cloud Agent (or CCA) | 15 | new |
| 7 | Copilot beyond the editor | 6 | new |
| 8 | What actually uses AI credits | 4 | — |
| 9 | Wrap, champions, office hours | 5 | — |

> **What was cut to fit.** Memory, custom agents and MCP lose their own segment
> — MCP configuration is already taught in item 4, and the registry isn't ready
> anyway, so this was the right thing to drop. The cloud agent goes 22 → 15.
> Code Review went 15 → 10, then back to 15 when five minutes moved over from
> item 2.
>
> **15 minutes is the floor for the cloud agent.** Below that you cannot show
> a session end to end, and a truncated agent demo is worse than none. If
> something else has to grow, take it from items 5 or 7 — not 6.

## 2 — Teams, permissions and repository administration (13 min) 🆕

Customer-requested, and they were right to ask. But three different kinds of
change get bundled under this heading, and only the first is actually a
GHES-versus-GitHub.com difference. Teach them as three things, because the
honest framing is also the clearer one.

### 2a · What is genuinely different: managed user accounts (6 min)

`caeglobal` is an **Enterprise Managed Users** enterprise. Teams, roles,
branch protection and the permissions model itself are essentially identical
between GHES and GitHub.com — **EMU is the real product delta**, and it is the
part that will generate support tickets.

**These constraints have been latent.** Developers already hold managed
accounts, because that is how they get Copilot. What they have not had is
*repositories* there. Moving the repos in is what makes the boundary real.

| What changes | Practical effect on a developer |
|---|---|
| **Read-only outside the enterprise** | From this account you can view public repos on GitHub.com but cannot push, open issues or pull requests, comment, star, watch or fork them |
| **No forking external repositories** | Patching an upstream dependency by forking it is no longer possible from this identity. You can fork internal and private repos inside the enterprise |
| **No gists** | Managed accounts cannot create or comment on gists. A common snippet-sharing habit simply stops working |
| **Repositories are private or internal only** | There is no public repository in an EMU org |
| **Hosted runners don't apply to user-owned repos** | They work in organisation-owned repos. **This is why Copilot Cloud Agent needs an org repo** — its environment is powered by Actions, and a personal repo has no hosted runners to give it |
| **Most GitHub Apps can't be installed on your account** | Repo-level installs are fine with admin access; org installs need an org owner |
| **Authentication is IdP-only** | No password, no GitHub-stored 2FA, and no sudo prompt on sensitive actions |
| **No personal profile, no user-level package storage** | Profiles are enterprise-visible only; packages cannot be published at user level |

Two things to say alongside that:

- **Outside collaborators are not an option.** Partners and contractors cannot
  be invited as personal GitHub accounts — they must come through the IdP, and
  the **guest collaborator** role exists to limit them to specific
  organisations without granting internal-repo access. Relevant for an account
  that works with delivery partners.
- 🔴 **Nested IdP groups are not supported, and CAE has already been bitten by
  this.** In May 2026 SCIM stopped swapping users between Copilot Business and
  Copilot Enterprise, and the cause was nested groups on the Entra ID side —
  documented as unsupported. If team membership is driven from nested groups,
  expect the same class of failure. Worth confirming with Michael Vincent
  before the session rather than discovering it live.

### 2b · What is different because CAE redesigned it (2 min)

Not a product difference — a deliberate topology change, and worth saying so.

- **Org-per-project becomes team-based separation.** On-prem, every project has
  its own organisation. The target consolidates that, with separation at the
  **team** level and governance moving from per-org settings to global policy.
- **Teams already exist**, pre-created with members and admins via the
  Terraform provider. Nobody is waiting on an administrator.
- **Access is intended to map across** — the level you held in an on-prem org
  becomes the equivalent level on the corresponding team.
- **Fewer organisations means names can collide.** Within any one organisation,
  `platform/utils` and `avionics/utils` cannot both be `utils`. Developers need
  the naming convention before their batch moves — and this is exactly what
  CODEOWNERS, below, is for once ownership no longer follows org boundaries.

✅ **Confirmed 23 September: CAE is consolidating to a much smaller number of
organisations.** That is the fact the segment needs, and it is enough to build
on. Teach the **direction and its consequences** — consolidation, team-level
separation, name collisions, ownership expressed per path — rather than a
count.

📌 **The exact number is still unconfirmed and does not need to be.** The
10 August email said ~400 organisations converging into **two**; the 13 August
architecture review described **one** with team-level separation. Caster's
question was never formally closed. Write the segment so it works either way,
and if you learn the number before 8 October, state it. **Never guess it from
the stage** — this room includes the people who decided it.

### 2b-bis · CODEOWNERS and repository limits (3 min) 🆕

Customer-requested on 23 September. Both are repository-hygiene topics rather
than Copilot ones, but they sit naturally here and neither takes long.

**CODEOWNERS for required reviewers.** A `CODEOWNERS` file names who owns which
paths, and those owners are **automatically requested for review** when a pull
request touches their files. Paired with a ruleset requiring review from code
owners, it becomes an enforced approval gate rather than a convention.

Why it lands for CAE specifically:

- Hundreds of organisations are collapsing into a handful. **Org membership
  stops being the thing that says who owns what** — CODEOWNERS is how ownership
  survives the consolidation, expressed per path instead of per org.
- Teams are already pre-created via Terraform, so owners can be **teams**
  rather than named individuals. No new administration.
- It pairs directly with the review gates in the rulesets their organisation
  already runs.

> 💡 **Presenter connection, worth one sentence to the room.** Copilot Code
> Review does **not** satisfy a CODEOWNERS requirement — it leaves a "Comment"
> review by default, not an approval. Same point made in item 5; this is where
> the two ideas meet, so say it once here and once there.

**Repository limits, and Git LFS as the answer.** Recommended maxima, and the
numbers are worth stating precisely rather than gesturing at:

| Limit | Recommended | Hard enforcement |
|---|---|---|
| Repository on-disk size (`.git`) | **10 GB** | — |
| Single object size | **1 MB** | **100 MB** |
| Push size | — | **2 GB** |
| Entries in one directory | 3,000 | — |
| Directory depth | 50 | — |
| Branches | 5,000 | — |

**The workaround is Git LFS for binaries**, plus keeping generated artefacts
out of Git entirely — object storage or a package registry, which for CAE
means Artifactory.

⚠️ **Tread carefully here, and know why.** LFS was the single hardest problem
in this migration: repositories whose LFS backend moved between GitHub and
Artifactory and back, leaving mixed history. The importer supports Git LFS but
**not LFS backed by Artifactory**, and CAE's platform team is consolidating to
one backend before migrating. So:

- **Teach LFS as the answer to large files in a repository.** That is correct
  and useful.
- **Do not advise anyone to change an LFS backend, or to start using LFS on a
  repository that has not yet migrated.** That is live platform-engineering
  work with a specific plan behind it. Route those questions to platform
  engineering, exactly as with cost questions.

Two limits worth flagging because this audience will hit them: **the maximum
files in a single diff is 300**, and a pull request diff caps at **20,000
lines** or 1 MB. A migration-era pull request touching a whole repository's
build files can exceed that and become unreviewable — a practical reason to
keep the per-repo build change small.

### 2c · What happens once, during the move (2 min)

Migration mechanics, not steady state. Answers "what happens to repository
ownership" precisely:

| Carried by the importer | Not carried |
|---|---|
| Collaborator levels — read, write, admin | Organisation owners |
| Team access and team-level permissions | Enterprise roles |
| Branch protection rules | Billing roles |
| Code, history, releases, issues, PRs, comments | SAML SSO mappings, secrets, SSH keys |

Low risk for CAE: they use built-in roles only, with few exceptions. And on
repo-level migration, if a team had access to a repo in the source org, the
importer recreates that team in the destination org when it does not already
exist and reapplies its permissions.

⚠️ **This conflicts with GitHub's current GEI docs. Verify with Jack before 8
October.** The table above follows what was said on the 13 August call. For a
**GHES source**, GEI's "Data that is not migrated" list includes **teams, user
access to the repository, and user or team access to the repository**. Its
migration overview adds that after a repository migration, teams must be
recreated and given access by hand, and GHES sources only support repository
migrations. If the docs are right, developers' access in the new org comes
from the teams CAE pre-creates with Terraform, not from their old repo
permissions, and "I lost access" is a team-membership question for platform
engineering. The same list also excludes pull request settings, tag
protection, and some branch-protection rules (bypass lists, lock branch, force
pushes). Until this is settled, don't present the first two rows of the left
column as fact.

**Warn them about attribution.** Until mannequins are reclaimed, history shows
unmapped authors. Say it before they see it, or the tickets write themselves.

## 3 — Actions and CI/CD: what's now possible (12 min) 🆕

Customer-requested. Scope it as **awareness, not migration mechanics** — which
is how they scoped it themselves, asking for "CI/CD possibilities" and
"differences from on-prem limitations" rather than how to port anything.

**Open with what is not changing, or the room will panic.** Jenkins stays. SPL
stays. The application build path is deliberately untouched and Jenkins →
Actions is explicitly a later phase. Their migration task remains the per-repo
build file change, nothing more. Their ARC-on-OpenShift runners keep working —
anything below is an addition, not a replacement.

### What is genuinely different between GHES and GitHub.com

Only two of these are big, and both are big:

1. **GitHub-hosted runners exist.** GHES is self-hosted-only — there is no such
   thing as a hosted runner on an appliance. On GitHub.com they are available
   alongside CAE's existing fleet, including larger, ARM and GPU classes that
   have no on-prem equivalent. This was flagged in the August architecture
   review as a new consumption line, so expect it to be noticed.
2. **The public Actions ecosystem is natively reachable.** This is the one that
   matters most for CAE, and the reason is their own topology: on GHES,
   Marketplace actions require **GitHub Connect** reaching out to github.com,
   or manual vendoring. **GitHub Black is a closed network with no internet**,
   so in practice the Marketplace has been unavailable to them. On GitHub.com
   you reference an action and it resolves. No Connect, no mirror, no
   exception.
3. **Release cadence.** New Actions capability arrives continuously rather than
   at an upgrade window — the same point made in session 2 about GHAS.

### What is different because of CAE's situation, not the product

Say these plainly rather than dressing them as product wins:

- **If you came from GitHub EXT, Actions is entirely new.** Actions was never
  enabled on that instance — it fronted Azure DevOps pipelines. A meaningful
  part of the room has never written a workflow, while GitHub Black users have.
  Pitch to both.
- **Reusable workflows become practical, not possible.** They already existed
  on GHES. What changes is that hundreds of organisations are collapsing into
  a handful, so shared build and check logic is finally worth centralising.
  That is a topology consequence — do not sell it as a new feature.

### Two EMU wrinkles worth thirty seconds

- **Hosted runners do not apply to user-owned repositories** — only
  organisation-owned ones. This is also why the Copilot Cloud Agent needs an
  org repo, so it pays off in item 6.
- **Managed user accounts cannot create workflow templates.**

✅ **Good news worth landing here, because platform engineering will ask.**
Copilot Cloud Agent's environment is powered by GitHub Actions and defaults to
a GitHub-hosted runner — but it **can** be pointed at CAE's own **self-hosted
runners** via `.github/workflows/copilot-setup-steps.yml`, and **organisation
owners can set the default runner type** for Cloud Agent across every
repository, choosing whether repos may override it. The same file pre-installs
tools and dependencies, can request larger runners, switch to Windows, and
**enable Git LFS** — directly relevant given LFS was the hardest problem in the
migration. Copilot Code Review reuses the same setup file by default, or takes
a dedicated `copilot-code-review.yml`.

For an aerospace and defence customer, "the agent does not have to run on
GitHub's compute" is a materially different conversation from "it does." Expect
Maxim or the platform team to ask.

⚠️ **Pair capability with governance in the same breath — this is not
optional.** Michaël confirmed on 14 September that CAE has **no formal policy
blocking arbitrary actions**, that preferred practice is trusted or known
publishers, and that unfamiliar actions should be vetted by security first.
Formalising that policy is still open on CAE's side. Item 2 above hands an
aerospace and defence audience a public ecosystem their network previously
denied them — teaching that without the trusted-publisher rule attached would
be actively harmful. State it as part of the capability, not as a footnote.

Also name the **required workflows already in force** — Microsoft Security
DevOps and dependency review — so people recognise them in their checks.

> 📌 This is a change of customer position worth noting internally. On
> 14 September the agreed method was that Actions content waits for the Jenkins
> migration. The likely driver is the DevOps initiative recorded the same day,
> which may replace Azure tooling with GitHub workflows. Awareness-level
> content does not contradict "Jenkins stays" — but do not let it drift into
> migration mechanics, which would.

## 4 — GHAS + Copilot: better together (16 min, new) ⭐

The headline Copilot segment. CAE asked for exactly this on **17 April 2026** —
reaching CodeQL and Code Quality through an MCP server. Say so out loud:
*"you asked for this in April."*

**Start with two minutes of grounding, and do not skip it.** The 14 September
discovery found attendees **did not recognise the terms CodeQL, secret scanning
or Dependabot** — a separate group owns security at CAE. One line each on what
class of problem each finds, then move on. Depth belongs to session 2.

**The configuration is the real takeaway.** The GitHub MCP server ships three
default toolsets — `repos`, `issues`, `pull_requests`. The security ones are
**not** included and must be enabled explicitly:

| Toolset | Gives Copilot |
|---|---|
| `code_security` | Code scanning alerts — your CodeQL findings |
| `secret_protection` | Secret scanning. Note `run_secret_scanning` currently sits on the `copilot` toolset and must be added as an extra tool |
| `dependabot` | Dependency vulnerability scanning against the GitHub Advisory Database |

In VS Code, add `"X-MCP-Toolsets": "code_security"` to the GitHub MCP server
headers, or pick the toolset from the selector in Copilot Chat. Show the config
on screen — people will screenshot it.

**Then the payoff.** With the toolset on, *"what CodeQL findings are open on
this repo, and which should I fix first"* is a question asked in the editor,
against real results, with the code in context. The scanner finds it, Copilot
explains it and drafts the fix, you decide. That is "better together"
demonstrated rather than asserted.

**Close on what they cannot do yet.** On 8 October most CAE repos return
nothing, because scanning is only just being switched on:

> *"This is the question you'll want to ask. Right now, on your repos, it
> returns nothing — because CodeQL isn't running yet. That's session two."*

⚠️ **Demo requirement, with lead time:** the sandbox repo needs CodeQL, secret
scanning and Dependabot enabled with **real findings** well before 8 October.
If this demo returns an empty list, the headline segment fails.

## 5 — Copilot Code Review, or CCR (15 min, new)

Grew from 10 to 15 by trimming item 2. The extra time buys the "Fix with
Copilot" beat, which is the best transition in the session.

**Distinguish the two, because the local one is not the new one:**

- **Reviewing uncommitted work in the editor** — available before.
- **Review on the pull request** — server-side, on every PR. Entirely new, and
  where all the time goes.

### Requesting a review (3 min)

Under **Reviewers**, next to **Copilot**, click **Request**. Usually back in
under 30 seconds. Automatic review for every PR is configurable, and
**re-review on each push is a ruleset setting** — worth naming for an audience
whose organisation already runs rulesets heavily.

Also mention **review effort levels**, so people know thoroughness is a dial
rather than a fixed behaviour.

### Reading the review (3 min)

- Comments carry a **High / Medium / Low severity label**, so there is a
  built-in triage order.
- **By default Copilot leaves a "Comment" review, not an approval** — it does
  **not** count toward required approvals. It can be configured to approve, but
  do not let anyone leave thinking Copilot can satisfy their review
  requirement. In an aerospace and defence context that misunderstanding is
  worth heading off explicitly.
- Comments behave like human ones — react, reply, resolve, hide. **One
  asymmetry to state plainly:** your replies are visible to colleagues but
  *not* to Copilot, and it will not reply. People will otherwise try to argue
  with it.

### Acting on it — and the handoff to CCA (5 min) ⭐

- Where possible, feedback arrives as **suggested changes** you can apply in a
  couple of clicks — singly, or a group of them committed together.
- **"Fix with Copilot" hands the comment to Copilot Cloud Agent.** It opens a
  draft comment where you say what you want addressed, then produces either a
  commit on the same pull request or a new pull request against your branch.
  Requires both CCR and CCA enabled — and both are confirmed on at CAE.

This is the natural bridge into item 6: the reviewer finds it, the agent fixes
it, you approve it. Run the demo so it ends here, and open item 6 from the
result rather than starting cold.

### Configuration worth knowing (3 min)

CCR runs in an ephemeral environment powered by Actions. It **reuses
`copilot-setup-steps.yml` by default**, or takes a dedicated
`copilot-code-review.yml` to configure its environment independently — the same
mechanism covered in item 3, so this is a callback rather than new material.

### Seed session 2 (1 min)

Security findings surface in this same place, on the same pull request.

> 💡 **Worth knowing, though not a teaching beat.** Copilot code review can be
> made available to organisation members **without a Copilot licence** when an
> enterprise administrator or organisation owner enables it. Given seat
> capacity is the binding constraint on this account, that is relevant — but it
> is a conversation for Patrick and Élodie, not for this room. If it comes up,
> take the standing redirect.

## 6 — Copilot Cloud Agent, or CCA (15 min, new)

The largest single block, because it is the biggest behavioural change and has
no local equivalent at all. Assign an issue, the agent works in its own
environment, opens a PR, and a human reviews it.

Two control points to teach: you review the PR like any other, and the agent
never merges. Seed session 2 again — in a security campaign, the same agent can
be handed security alerts, starting from the fix Copilot Autofix has already
drafted. That's where session 2 ends.

**Use their migration homework as the task.** This is the demo rescued from the
cut modes segment, and it is stronger here than it was there. Maxim's words:
*"there are changes we need to do in files inside each repo for the build to
actually work."* Hand the agent exactly that — the per-repo build change every
person in the room faces, times 12,000 — and let it find the files, make the
change, run the build, read the failure and fix it.

Nothing else in either session is this close to the job they have actually been
given. Budget time to let it run rather than cutting to a finished result.

## 7 — Copilot beyond the editor (6 min, new)

Kevin named the **GitHub Copilot App** explicitly. Group it with Copilot on
github.com and Spaces, since all three are "Copilot outside your editor" — a category that did
not exist for them before, because there was no platform to run it on.

Close with one sentence each on **Copilot Memory** and **custom agents**, which
lost their own segment to the two new topics. Both are enabled but neither is
demonstrable: there is **no completed shared MCP registry** — Michaël has one
in development — and custom agents appear in the technical companion without
having been confirmed in the 14 September discovery. Name the direction, promise
nothing, and let office hours carry it once the registry lands.

## 8 — What actually uses AI credits (4 min)

Corrects a misconception; not a cost conversation. Usage is measured in **AI
credits**, not premium requests (premium requests are legacy and apply only to
Pro/Pro+ annual subscribers).

- **Completions and next edit suggestions are not billed at all** — unlimited
  on every paid plan. Anyone who thinks each keystroke costs something can stop
  rationing.
- **Credits pool at the billing entity, not per person.** Business contributes
  1,900 a month per licence and Enterprise 3,900, pooled — heavy users draw
  more while lighter users offset them. The "my USD 19" model developers
  described in discovery is wrong, and it is suppressing exactly the
  experimentation this programme exists to encourage.
- **What does consume credits** is chat, CLI, the cloud agent and Spaces —
  the agentic work, priced by the model it runs.

Stop there. How many credits CAE holds, what happens at exhaustion, who pays —
all take the standing redirect.

## 9 — Wrap, champions and office hours (5 min)

Session 1 had no written close — session 2 has its homework block and this
should match it.

Three things to do before Copilot office hours in November:

1. Sign in with your managed account and find one repository you own.
2. Turn on the `code_security` MCP toolset and ask Copilot what it sees. On
   most repos it will return nothing yet — that is the point, and it is what
   session 2 fixes.
3. Hand one small, well-described issue to the cloud agent and review the pull
   request it opens.

Then the champions ask, and a forward pointer to session 2. Its second half is
for GitHub.com, so anyone whose repositories are moving should stay for the
whole hour.

**Say the office-hours rule explicitly**, because Kevin is planning the gap
deliberately: save the questions that come up while you try this. That is what
November is for.

**Say it at the open as well as here.** The invite email doesn't mention office
hours, and people can't hold questions for a session they don't know exists.

## Delivery notes

- **Demo items 4 and 5 rather than running them as labs.** Don't spend the
  room's pooled allowance live.
- **Copilot CLI gets a mention, not a segment.** Permitted, but discovery found
  very few developers use it.
- **Sandbox repo, built to look like their world** — simulation-style code, a
  Jenkins-shaped build path, real inherited complexity. Not a greenfield demo
  app, and not a small repo: their pain is scale and unfamiliar code.

## Confirmed enabled (14 September discovery)

Copilot Cloud Agent (CCA) · Copilot Code Review (CCR) · issue assignment to Copilot ·
Code Quality · Memory · MCP. GitHub Copilot App and CLI permitted. SDK not blocked but
no known usage. Agent Apps left alone as preview.

Technical companion additionally lists **custom agents** — verify before it
goes in the deck.

---

## Open before materials lock

Session 1's items. Those that affect both sessions, and the rule that every
demo uses a sandbox repo, are in
[the overview](cae-overview.md#open-before-materials-lock).

| Item | Why it matters |
|---|---|
| Re-verify the enablement list | Michael Vincent had just returned from leave and flagged settings may have moved |
| **Sandbox needs CodeQL with real findings** | Required for the session-1 MCP beat. Takes lead time — have it working by **6 October**, since you're out on the 7th |
| 🟡 **Exact number of target organisations** | **No longer a blocker.** Confirmed 23 September that CAE is consolidating to a much smaller number of orgs, which is all the training needs. The precise count — the 10 Aug email said two, the 13 Aug review said one — remains unconfirmed and Caster's question was never closed. Worth having for accuracy; do not guess it on stage |
| **Are IdP groups nested?** | If team membership is driven from nested Entra ID groups it will misbehave — nested groups are unsupported and CAE already hit this in May 2026 with Copilot seat assignment. Confirm with Michael Vincent |
| **Actions governance** | CAE has no formal policy restricting actions to trusted publishers, and formalising one is still open on their side. Teach the trusted-publisher rule as part of the capability |
| ⚠️ **GEI and access — session 1, item 2c** | The plan says GEI carries collaborator levels and team access, and recreates teams. GEI's docs say the opposite for a GHES source. Settle it with Jack before 8 October: it is the one claim in session 1 the docs contradict |

---

## Expect these in the room

- **Copilot alternatives** — Kiro and Anthropic are both recorded as
  competitive presences on this account.
- **A wider room than the plan assumed.** The invite asks recipients to forward
  it to anyone on their team, and names technical leads and repository
  maintainers alongside developers. That suits items 2 and 2b-bis, and the
  "what didn't change" frame covers anyone whose repos haven't moved yet.

---

## Not in scope, say so early

Jenkins stays. SPL stays. The application build path is deliberately untouched
and Jenkins → Actions is a later phase. GHES is not being switched off — it is
a permanent compliance requirement. Saying this in the first two minutes
shrinks the perceived blast radius more than anything else on the agenda.

**The invite email doesn't say it**, so for most of the room this is the first
time they hear "Jenkins stays". Say it clearly.

The developer's actual migration task is narrow: the per-repo file changes that
keep their build working.
