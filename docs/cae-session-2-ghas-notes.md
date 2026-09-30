# Session 2 — GitHub Advanced Security on Black and Blue

**Part of [CAE developer enablement](cae-overview.md)**, which holds the
calendar, delivery model, shared vocabulary and the open items that affect
both sessions. Item numbers such as 2c or 6 refer to
[session 1's notes](cae-session-1-copilot-notes.md).

**Thursday 15 or Wednesday 21 October, 60 minutes** — the date is still open;
see **Open before materials lock**. Moved back from 30 September so it follows
the Copilot session.

**The invite is drafted, not sent** — see
[the session 2 invite](cae-session-2-ghas-invite.md). Its agenda maps
one-to-one to segments 1–10, so if a segment changes, change its line in the
invite too.

Built to Kevin's contract: for every capability, cover **what it does · how to
check whether it's on · how to turn it on · why it should be on.**

**Restructured 29 September to Kevin's shape: one session, two halves.** His
team is enabling GHAS on Black now, and found the two platforms "quite
similar": the base is the same — "Dependency Scanning, Code Scanning, Secret
Scanning", plus the dependency graph — and "GHAS Cloud has more goodies". He
asked to "start the session with the all stuff that is in common", to "make
sure that On-Prem gets their bite of the pie", and then to "say the next part
only applies to Cloud" and let on-prem developers go. His one hard line: "we
don't want to start chatting about Autofix while the On-Prem users are still
around." And who stays: "if they're on Black they'll just stay for the first
half, if they are on Blue they'll stay the whole way."

**The 23 September honest answer still holds, for both halves.** For most of
this room GHAS isn't *different*. It's *new*. On-prem had no GHAS until the
1,200-seat volume purchase was provisioned on 16 September, and the 14
September discovery found attendees didn't recognise the terms CodeQL, secret
scanning or Dependabot. So Part 1 teaches the shared product once, plainly, to
everyone, and Part 2 spends its half hour on what only GitHub.com has.

| Kevin's contract | Segment |
|---|---|
| What it does, and why it should be on | 2 |
| How to check whether it's on | 3, then 8 for Blue |
| How to turn it on | 4 |
| His direct question: on-prem vs cloud | The split itself, then 7 and 9 |

**Part 1 — everyone, Black and Blue (30 min)**

| # | Segment | Min |
|---|---|---|
| 1 | The same product on both | 2 |
| 2 | The three pillars | 12 |
| 3 | Is it on? The check, steps 1–3 | 6 |
| 4 | Turning it on — who owns what | 6 |
| 5 | Black's wrap-up, and the split | 4 |

**Part 2 — Blue only (30 min)**

| # | Segment | Min |
|---|---|---|
| 6 | What *didn't* come across — the security half | 5 |
| 7 | What GitHub.com adds | 11 |
| 8 | The rest of the check, steps 4–7 | 6 |
| 9 | Autofix → campaign → Copilot Cloud Agent (or CCA) | 5 |
| 10 | Wrap, champions and office hours | 3 |

> ⚠️ **Two rules for the whole hour.**
>
> 1. **Bucket 3 is never new.** Everything in bucket 3 of the reference below
>    exists on GHES 3.21 too. Teach it, but never as new. Fred's team already
>    corrected one GitHub assumption in August, and this room will catch an
>    over-claim.
> 2. **If Black can't have it, it waits for Part 2.** No Copilot Autofix,
>    AI-detected secrets, security campaigns, Copilot, MCP, Code Quality or
>    "always current" before the split — not on a slide, in a demo or in an
>    answer. Kevin asked for exactly this. If a Black developer asks, one
>    line: *"That's in the second half. Drop it in the chat and I'll answer it
>    there at the end."*

## Part 1 — everyone, Black and Blue

**Part 1 has to stand alone.** Black developers may not have been in session 1,
and they leave at the split, so nothing here calls back to session 1 or
forward to Part 2. Keep it to what Kevin expects: "pretty simple and straight
forward stuff". The reference tables below are presenter backup, not slides.

**Show Black developers their own screens.** Take Part 1's screenshots from a
GHES 3.21 instance with GHAS on — never Black itself. A GitHub.com screen could
put Copilot Autofix or Code Quality in front of Black before the split. If no
GHES 3.21 instance is available, fall back to a rehearsed GitHub.com sandbox
with Copilot Autofix disabled and Code Quality off. It has to be both, because
Autofix keeps running on Code Quality findings when it's disabled for
security. Then say once that on Black the tab is called **Security**.

## 1 — The same product on both (2 min)

Open with the honest line, reworded for a mixed room:

> *"GitHub Advanced Security is the same product on Black and on Blue. It's new
> to most of you because on-prem had no GHAS until this September — not
> because either platform invented it. For the next half hour, everything we
> show works on both. At [split time] we split: the second half is about what
> GitHub.com adds, so if you're on Black, you're free to go then."*

**Say the split time out loud, and put it in the invite**, so Black developers
can plan for half an hour rather than an hour.

## 2 — The three pillars (12 min)

Kevin's "what it does" and "why it should be on", under the three names his
team uses.

**Explain where findings land, from scratch.** Code scanning runs as a check on
the pull request. Its alerts show on the **Conversation** tab and against the
changed lines under **Files changed**, so the finding arrives in the review
you're already doing, not in a dashboard you have to go looking for.
Dependency review shows what a manifest change brings in, on the same pull
request.

| Pillar | Feature | What it finds or does | Why it should be on |
|---|---|---|---|
| **Dependency scanning** | Dependency graph | Lists what your repo depends on, from its manifest and lock files | Dependabot alerts, security updates and dependency review all depend on it |
| | Dependabot alerts | Known vulnerabilities in those dependencies | Most of the code you ship is someone else's |
| | Dependabot security and version updates | Pull requests that move a dependency to a fixed or newer version | The fix arrives as a pull request you review |
| | Dependency review | What a pull request's dependency changes bring in, before merge | You see a vulnerable package before it lands, not after |
| **Code scanning** | CodeQL | Vulnerabilities in your own code, by semantic analysis | It flags the flaw on the pull request, before merge |
| **Secret scanning** | Secret scanning alerts | Credentials already committed, across the repo's whole Git history | Deleting the line doesn't remove the secret from history, so it has to be rotated |

**Kevin's Dependabot options reference belongs here.** He asked for it on 23
September. It's the `dependabot.yml` reference, published for GHES 3.21 and
GitHub.com alike, so it serves both halves. Teach one option, **`registries`**:
it's how Dependabot signs in to a private registry such as Artifactory, and
both versions document it. Then link each platform's page separately in its
own handout. The GitHub.com page documents options the 3.21 page doesn't, such
as `update-types` under `allow`, so each half should work from its own.

- **Push protection** blocks new secrets at push time. It's phase 2, not
  today.
- **Don't call dependency review a required workflow here.** That's CAE's
  GitHub.com set-up, not Black's. Blue hears about it in segment 6.

> Rollout position agreed between Élodie and Patrick: **scanning-only first,
> policies and gates later.** Do not teach enforcement as today's reality.
> ⚠️ That position, and push protection as phase 2, were agreed for
> GitHub.com. Confirm with Kevin that both hold on Black before saying them to
> the whole room.

## 3 — Is it on? The check, steps 1–3 (6 min)

Kevin's "how to check whether it's on", and the first half of the takeaway
artefact. The three steps are the same on both platforms; only one label
differs. Demo them, then hand out the written version.

1. **Repo → Settings → Security → Advanced Security** on Black; the section
   is **Security and quality** on Blue. Confirm Dependabot alerts, code
   scanning and secret scanning are each on.
2. **Security tab → Code scanning → Tool status** shows when CodeQL last
   scanned and what share of files it covered. On Blue the tab is Security
   and quality.
3. **Insights → Dependency graph.** Confirm your dependencies are listed. An
   empty graph leaves Dependabot alerts and dependency review with nothing to
   check.

**Who fixes what.**

- **A switch in step 1 is off, and you're a repo admin:** segment 4 shows how.
- **Tool status shows no scan:** on Blue, turn on default setup (segment 4).
  On Black, default setup needs runners that Black's administrators provide,
  so ask them through the route in Black's handout.
- **No dependency graph on Black:** it's switched on once, for the whole
  instance. The 3.21 docs give no per-repository switch and say to ask an
  enterprise owner. Black's route is still to confirm with Kevin.

**One handout per platform.** Black's says Security, shows GHES screens and
stops at step 3. Blue's says Security and quality and carries on to step 7.

## 4 — Turning it on — who owns what (6 min)

Kevin's "how to turn it on", scoped to what a developer can actually change.

- **Default setup is the normal path on both platforms.** It picks the
  languages and queries for you. On Black it runs on self-hosted runners
  labelled `code-scanning`, which Black's administrators provide. On Blue
  it's what CAE already applies broadly, on GitHub-hosted runners, so there's
  no runner to request. Same setting, same result — only who provides the
  machine differs.
- **Advanced setup** is for when default setup fails, or a project needs
  custom configuration. If the build needs Artifactory, the runner has to
  reach it, and that's platform engineering's call.
- **If a setting won't change,** an enforced configuration is holding it. That
  works the same on both platforms, and it's by design; ask your organisation
  owners or security managers.
- **Anything about cost or licences** gets the standing redirect to your
  manager or platform engineering.

| Level | Who can turn it on |
|---|---|
| Repository | Repo admins |
| Organisation | Organisation owners and security managers |
| Enterprise | Enterprise owners |
| Black only: the dependency graph, GitHub Connect and runners | Black's administrators — through the route in Black's handout |

**If Kevin's team can supply a speaker**, give them three minutes here for
Black's side: what's live, what's coming, and how to ask for it. Your part
then shrinks to the bullets and the table.

> ⚠️ **What's live on Black by the session date?** Part 1 promises Black
> developers they can check GHAS and turn it on. That holds only if, by then,
> Black has GHAS licences applied, the dependency graph on, runners labelled
> `code-scanning`, and GitHub Connect — Dependabot alerts and dependency
> review both need it for advisory data. Dependabot updates also need Actions
> and self-hosted runners, which by default need internet access, so ask how
> Black handles that. This is question 1 for Kevin. If something won't be
> live, say so plainly, with his date for it if he has one.

## 5 — Black's wrap-up, and the split (4 min)

For Black developers this is the end of the session, so close it properly.

Three things to do before GHAS office hours in November:

1. Run steps 1–3 of the check on one repo you own.
2. Turn on what's missing, or ask for it if it's an instance switch.
3. Bring whatever broke.

Then the champions ask, and the office-hours rule: save the questions that
come up while you try this. That is what November is for. ⚠️ Say it to Black
only once Kevin confirms they're welcome at November's GHAS office hours — see
**Where developers take GHAS problems**.

**Take Black's questions live, before the split.** Hold two minutes for them.
Anything left over goes in the chat or to Kevin, and gets answered in the chat
at the end, so Black developers can read the answers without staying.

Then Kevin's dismissal line, adapted:

> *"That's everything that applies to Black. If you're on Black — on-prem —
> you're free to go. Leave any questions with Kevin or in the chat, and I'll
> answer them there at the end. If you're on Blue, stay: the next half hour is
> what GitHub.com adds."*

> ⚠️ **Split the recording here.** Stop it at the split and start a new one
> for Part 2, so Black's recording is Part 1 alone and holds nothing Black
> can't have. Kevin records, so it needs his OK — question 3.

## Part 2 — Blue only

**Everyone still here is on Blue.** Part 2 can lean on session 1 again, but
give each callback a one-line recap for anyone who missed it. There's no Black
column from here on. Blue developers have now heard the basics once, so go
deeper rather than repeating them.

## 6 — What *didn't* come across — the security half (5 min)

Opens as a callback rather than cold: session 1 covered what moved onto the
platform; this is what didn't. Engineering's repos arrived with CodeQL off.

Three reasons, and the third is the one nobody expects:

- **For most repos there was nothing to carry.** On-prem had no GHAS until 16
  September, so most repos have no findings, history or settings to bring.
  Where GHAS was on, GEI still doesn't migrate code scanning results,
  Dependabot alerts or secret scanning remediation states.
- **The organisation's default doesn't catch a migrated repo.** A default
  security configuration applies to newly created repositories. The docs say a
  transferred repo needs one applied by hand, and don't say how GEI imports are
  treated. ⚠️ Confirm in the sandbox before saying it as fact — see **Open
  before materials lock**.
- **Custom properties don't migrate, and rulesets key off them.** Those
  properties aren't standardised, so a migrated repo with none set silently
  falls outside the rulesets that govern everything else — code quality at
  error severity, base-branch protection, security scanning, linting, PR commit
  and label rules, repository structure and tags. Some of those rulesets target
  100+ repos. The repo looks fine and is governed by nothing.

Also flag the two **required workflows** — Microsoft Security DevOps and
dependency review — so people recognise them when they appear in checks.

**Then one line on organisation-level security configurations.** They exist on
3.21 too. What changes is reach: with CAE consolidating to a much smaller
number of organisations, one configuration covers far more repos. The second
reason above still applies — a migrated repo may need one applied by hand.
Offer Élodie a separate admin walkthrough rather than teaching configurations
to developers.

## 7 — What GitHub.com adds (11 min) ⭐

Kevin's "GHAS Cloud has more goodies", answered honestly: **the engine is the
same. What changes is where it runs, how current it is, and the AI on top.**
Every claim here is checked against the docs — see the reference at the end
of this session. Nothing from its "same on both" list belongs in this segment.

### Where it runs, and how current it is (4 min)

- **Nothing to provision.** On Black, default setup needs runners that
  Black's administrators provide, and Dependabot needs GitHub Connect plus its
  own self-hosted runners. On GitHub.com, both run on GitHub-hosted runners.
  Default setup runs as its own GitHub Actions workflow, next to Jenkins
  rather than instead of it, so "Jenkins stays" still holds.
- **The one new gotcha.** Hosted runners sit outside CAE's network, so by
  default they can't reach Artifactory or other private registries.
  `registries` in `dependabot.yml`, from Part 1, handles the sign-in; the
  network path is what's new. If Dependabot pull requests never arrive, or
  CodeQL can't resolve dependencies, that's a network question for platform
  engineering, not a fault in your repo. The fixes are theirs: self-hosted
  runners, Azure private networking or an IP allowlist.
- **Same engine, but GitHub.com runs the newest of it.** On 3.21, CodeQL is
  the 2.24.3 bundle unless someone syncs a newer one, and advisory data
  arrives over GitHub Connect. On GitHub.com both are always current, with
  nothing to sync.
- **Land the cadence point with the upgrade they just lived through.** Black
  needed a planned window to reach 3.21.4. Dependabot malware alerts are the
  example: available on GitHub.com now, on GHES only from 3.22. On Blue, a new
  capability arrives the day it ships.

### What only GitHub.com has — the AI layer (7 min)

| Capability | What it does | Needs a Copilot seat? |
|---|---|---|
| **Copilot Autofix** | Drafts a fix for a CodeQL code scanning alert | No |
| **AI-detected secrets** | Finds unstructured secrets, such as passwords, that pattern matching misses | No |
| **Security campaigns** | Groups alerts into a backlog with a named contact and a due date. Autofix runs on its code scanning alerts automatically | Only to hand alerts to CCA |
| **Copilot Chat on an alert** | Explains an alert and how to fix it | Yes — Copilot Enterprise, per the docs |

- **The seat column matters at CAE**, where Copilot seats are the binding
  constraint. Autofix and AI-detected secrets are available to any repo with
  GHAS on, whether or not its developers have a seat.
- **Campaigns get one line here.** They're the payoff, demoed in segment 9.
- **Code Quality is GitHub.com-only, but it isn't GHAS.** It's billed
  separately — a per-committer licence, AI credits and Actions minutes — so
  cost questions get the standing redirect. Don't demo its dashboard: Élodie
  sees data for only 22 repos, and "we already have Sonar" is waiting.
- **Previews are named only if asked, never demoed.** AI Scan for pull
  requests and extended metadata checks are both public previews. So is
  agentic autofix — see segment 9.

## 8 — The rest of the check, steps 4–7 (6 min)

Blue's half of the takeaway artefact. Steps 1–3 were Part 1's. These four check
CAE's GitHub.com governance and Copilot, so they only apply on Blue.

4. Confirm **repository custom properties** are set.
5. Confirm the repo is matched by the rulesets you expect.
6. Confirm required workflows (MSDO, dependency review) run on a PR.
7. **Ask Copilot what it now sees** — the same `code_security` MCP query from
   session 1. Then, on your repos, it returned nothing; now it returns this
   repo's findings. Land it with *"You asked for this in April."* It reads as
   a capability rather than a chore, and proves the repo is genuinely covered
   rather than merely configured. Note the `dependabot` toolset alongside it.

**Who fixes what.** Steps 4–6 go to platform engineering: rulesets and required
workflows are set above the repo, and the properties they key off aren't
standardised yet. Step 7 is yours.

**Demo step 7 in VS Code, then poll the room.** Kevin asked on 29 September
for a quick check of how many people use Visual Studio — not only the Vehicle
team, but people in other departments too. Then one line: *"If you're in
Visual Studio, the GitHub MCP server works there too, from version 17.14 —
the steps are in the handout."* Say "Visual Studio users", never "Vehicle".
Note the count: it tells November's office hours what to prepare for.

> ⚠️ Steps 4–6 only demo live on a repo that CAE's rulesets and required
> workflows actually govern. If the sandbox isn't one, use screenshots — see
> **Open before materials lock**.

## 9 — Autofix → campaign → CCA (5 min) ⭐

Kevin's on-prem-vs-cloud question, closed with a capability rather than a
list. Walk the chain once, end to end:

1. CodeQL finds vulnerabilities, and they pile up as open alerts on the
   default branch.
2. **Copilot Autofix** drafts fixes — inline on a pull request, and for that
   existing backlog too. No Copilot seat needed.
3. An organisation owner or security manager groups the backlog into a
   **security campaign**, with a named contact and a due date. Alerts with an
   autofix can be assigned to **CCA**, which opens a pull request and adds
   whoever assigned it as a reviewer.
4. That pull request is reviewed like any other — the human-in-the-loop model
   from session 1. Dependabot's pull requests, from Part 1, follow the same
   pattern: a bot opens it, a person reviews it.

Say it precisely: *"Autofix drafts the fix; in a campaign, those fixes can be
handed to CCA."* Two capabilities, not one agent. What a developer actually
sees is a campaign notification, an alert assigned to them, or a CCA pull
request on their repo.

That is the whole argument for the move in CAE's own words: Fred named Copilot,
Advanced Security and dependency auto-fix as the reason for going to SaaS.
Closing the programme on the capability the customer bought it for is a better
ending than a feature summary.

> ⚠️ **Demo requirement.** A campaign pre-created in the sandbox organisation,
> on alerts that already have autofixes, with CCA enabled and one finished CCA
> pull request to open. Build it beforehand, not live.

> ⚠️ **Re-check agentic autofix the week before.** It's a public preview on
> GitHub.com. Where CCA is available, assigning a code scanning alert to
> Copilot starts a CCA session instead of Copilot Autofix — billed as a CCA
> session, using AI credits. It needs both CCA and Autofix, and falls back to
> Autofix where CCA isn't available. Run step 3 in the sandbox and make sure
> the talk track matches what happens. Session 1's item 6 seed line —
> "starting from the fix Copilot Autofix has already drafted" — needs the same
> check before 8 October. If the preview shows up, name it as a preview, and
> give the AI credits the standing redirect.

## 10 — Wrap, champions and office hours (3 min)

Three things before GHAS office hours in November:

1. Run all seven steps of the check on one repo you own.
2. Turn on what's missing, and send steps 4–6 to platform engineering if they
   fail.
3. Bring whatever broke.

**Close the loop with Black.** Post the answers to their chat questions, as
promised at the split. Agree beforehand who writes them up — Kevin during
Part 2, or you straight after.

Then the champions ask. **Say the office-hours rule again:** save the
questions that come up while you try this. That is what November is for.

## Reference — GHAS on GHES 3.21 vs GitHub.com, checked 23 September

Checked page by page against GitHub Docs for GHES 3.21 and GitHub Enterprise
Cloud, not from memory. Black runs 3.21.4. Only the first bucket is a product
difference. Don't let anything from the third bucket into the talk track as
"new". The dependency graph and GitHub Connect rows were added on 29
September, checked the same way.

### 1 · Genuinely different on GitHub.com

| | GHES 3.21 | GitHub.com |
|---|---|---|
| **Where CodeQL runs** | Default setup needs self-hosted runners labelled `code-scanning`, with Git on the path and Python 3 for Python repos | GitHub-hosted runners, nothing to provision. Self-hosted is optional |
| **Where Dependabot runs** | Needs GitHub Connect plus dedicated self-hosted runners with TLS | GitHub-hosted runners by default |
| **Where the dependency graph is switched on** | Once, for the whole instance, by an enterprise owner — in the Management Console, or with `ghe-config app.dependency-graph.enabled true` in the administrative shell. Saving in the Management Console restarts services, which can mean user-visible downtime. No per-repository switch | Per repository — the docs give repo-level steps. No instance to switch |
| **What depends on GitHub Connect** | Dependabot alerts and dependency review both need it for advisory data | Nothing to connect |
| **How current it is** | CodeQL bundle tied to the release (2.24.3 for 3.21) unless someone syncs a newer one. Advisory data syncs hourly over GitHub Connect | Always current. Nothing to sync |
| **Copilot Autofix** | Not available | Suggested fixes on code scanning alerts — C#, C/C++, Go, Java/Kotlin, JS/TS, Python, Ruby, Rust, Swift. No Copilot subscription needed |
| **AI-detected secrets** | Not available | Finds unstructured secrets, such as passwords, that pattern matching misses. No Copilot subscription needed |
| **Security campaigns** | Not available | Burn down an alert backlog. Autofix runs automatically, and alerts can be assigned to a person or to CCA, which opens the pull request and adds the assigner as a reviewer. Campaign managers must be organisation owners or security managers |
| **Extended metadata checks** | Not available | Who owns a leaked secret and what it can reach. Public preview |
| **Dependabot malware alerts** | Arrives in GHES 3.22. Black is on 3.21.4 | Available |
| **AI Scan for pull requests** | Not available | Public preview. Findings for languages CodeQL doesn't cover. Needs a Copilot licence and consumes AI credits, so cost questions go to the account team |
| **Copilot on your findings** | Copilot Chat on alerts isn't available, and only the local MCP server supports GHES. CAE never used Copilot against GHES | MCP toolsets (session 1, item 4). Copilot Chat on alerts needs a Copilot Enterprise seat, and CAE has both Business and Enterprise |
| **Code Quality** | Not available — no 3.21 docs | In use at CAE, under the same tab. Not GHAS, and billed separately |
| **What the tab is called** | Security | Security and quality — the settings section too |

**Why Black makes this bigger.** Black is a closed network, which is why the
Marketplace was never really available (session 1, item 3). GHAS depends on
the same connectivity: without GitHub Connect there is no advisory feed, so no
Dependabot alerts. And a server without internet access gets CodeQL only
through the CodeQL action sync tool, which someone has to run again for each
newer release. **Not yet confirmed whether Black has Connect for advisories —
part of question 1 for Kevin; see Open before materials lock.** Backup only:
since 29 September no segment says this on stage.

**The one new gotcha.** Hosted scanning and Dependabot run outside CAE's
network, so by default they can't reach Artifactory or other private
registries. The fixes are self-hosted runners with the `dependabot` label,
Azure private networking or ARC, or an IP allowlist update. If "Dependabot on
self-hosted runners" is on and no labelled runner exists, jobs queue
indefinitely. Route to platform engineering.

### 2 · Different because of CAE's situation, not the product

- **GHAS itself is new to most of the room.** On-prem had no GHAS until the
  1,200-seat volume purchase was provisioned on 16 September, while GHAS is
  already running on CAE's existing GitHub.com footprint. For a
  migrant the change isn't "GHAS works differently". It's "GHAS is already
  running where you're going".
- **GitHub EXT never had Actions**, so default setup was never possible there.
  On GHES without Actions the only route is the CodeQL CLI in existing CI.
- **Rulesets and required workflows key off custom properties** (segment 6).
  That is CAE's design, not the product.
- **Fewer, bigger orgs** make organisation-level configurations worth more.
  The feature itself isn't new.

### 3 · The same on both — don't sell as new

Push protection · delegated bypass · custom patterns · validity checks ·
security configurations, including "default for newly created repositories" ·
security overview · rulesets · dependency review · default and advanced setup
· SARIF upload · CodeQL CLI · Dependabot alerts, security updates and version
updates · auto-triage rules · dependency graph and SBOM export.

**Irrelevant under EMU:** push protection for users, partner alerts and
repository security advisories are all public-repo features, and managed users
can't create public repositories.

### One-time migration facts, from the GEI docs

- **Not migrated:** code scanning results, Dependabot alerts, secret scanning
  remediation states, custom properties, rulesets, and Actions secrets,
  variables and environments.
- **Alert states can be carried separately.** `gh gei
  migrate-code-scanning-alerts` and `gh gei migrate-secret-alerts` copy alert
  history and triage decisions. That's irrelevant for repos that never had GHAS
  on GHES, but it matters if GHAS is switched on on-prem before a repo moves:
  any triage done there is lost unless the migration team runs them. Account
  item, not training content.
- **An org's default security configuration only catches newly created
  repositories.** The docs say transferred repos need one applied manually,
  and don't say how GEI imports are treated. That gap is why the
  post-migration check exists.

---

## What session 2 inherits from session 1

**This applies to Part 2 only.** Part 1 has to stand alone for Black
developers, so it inherits nothing and calls back to nothing. Part 2 is where
the inheritance pays — it's how what only GitHub.com has fits in half an hour:

| Already taught | What session 2 no longer has to explain |
|---|---|
| Managed user accounts and what they can do | Nothing to re-teach; go straight to the repo |
| What the importer carries and doesn't (item 2c) | Segment 6's opener becomes "…and here's the security half of that list". That half comes from the GEI docs, so 2c's open question doesn't touch it |
| Findings appear on the pull request | Where CodeQL and secret scanning surface. Part 1 explains it from scratch anyway, for Black, so Blue hears it twice — keep the second time brief |
| An agent can open a PR you then review | Dependabot PRs, and CCA's PRs from a security campaign |
| "Your repo is on the platform now" | Becomes "…and here's what didn't come with it" |
| The cloud agent's human-in-the-loop model | Why you review an Autofix suggestion, not merge it blind |
| **The `code_security` MCP query that returned nothing** | **Becomes the live payoff — same query, real findings (segment 8, step 7)** |
| What CodeQL, secret scanning and Dependabot each find | Part 1 teaches them from scratch, so for Blue it's a second pass — go deeper on the dependency graph and `dependabot.yml` rather than repeat |
| CCA itself | Handing campaign alerts to CCA (segment 9) needs no new explanation |

Build the callbacks into Part 2 explicitly, each with a one-line recap for
anyone who missed session 1 — they are what makes it feel like part two rather
than an unrelated security briefing.

**The MCP thread is the strongest of these.** It runs the whole programme:
CAE asked for CodeQL-over-MCP in April, session 1 shows the mechanism and an
empty result, and Part 2 fills it in as the last step of the check. A want the
customer raised five months earlier, answered across two sessions, lands
harder than any feature summary.

---

## Open before materials lock

Session 2's items. Those that affect both sessions, and the rule that every
demo uses a sandbox repo, are in
[the overview](cae-overview.md#open-before-materials-lock).

| Item | Why it matters |
|---|---|
| ✅ **Session 2 audience — settled 29 September** | Kevin asked on 23 September for the GHAS session to be "as relevant as possible for both GHES and GHEC". On 29 September he set the shape: the shared base first, for everyone, then on-prem developers leave and the rest "only applies to Cloud". Black stays for the first half, Blue for the whole hour. Session 2 now runs as Part 1 and Part 2 |
| ⚠️ **What's live on Black by the session date? — question 1** | Part 1 promises Black developers they can check GHAS and turn it on, so ask Kevin what will be live by then: GHAS licences applied; the dependency graph on; runners labelled `code-scanning`; GitHub Connect, which Dependabot alerts and dependency review need for advisory data; and self-hosted runners for Dependabot updates, which need internet access by default. Also ask whether scanning-only first and push protection as phase 2 hold on Black, whether someone from his team can speak for three minutes in segment 4, and whether his team has a non-production GHES instance for Part 1's screenshots |
| ⚠️ **Split the recording at the half? — question 3** | Kevin records. Stopping at the split and starting a new recording keeps Black's copy to Part 1 alone, with nothing Black can't have. Needs his OK — see segment 5 |
| ⚠️ **Session 2 date: 15 or 21 October?** | Kevin asked on 29 September for Thursday 15 October, a week after session 1. If it moves, the invite goes by **1 October** and segment 9's sandbox is needed by the 14th. Either date, the invite states the split time. You're free 09:00–10:30 and 13:00–14:30 ET that day |
| **Kevin is waiting on the agenda** | He asked "How's the session agenda look so far?" on 29 September, and expects "pretty simple and straight forward stuff". The two-part outline and the draft invite answer it; send both with questions 1 and 3 |
| **Dependabot options reference** | Kevin asked on 23 September for it to be included in the GHAS session. Now in segment 2: one option taught, `registries`, and each platform's page linked in its own handout, because the GitHub.com page documents options 3.21 lacks. Confirm with Kevin that's what he meant |
| **Where developers take GHAS problems** | Two routes now, one per handout. Blue's check sends steps 4–6 to platform engineering, and enforced settings to organisation owners or security managers. Black's sends instance switches and runners to Black's administrators. Each needs a real route — a named team, channel or queue. Also ask whether Black developers are welcome at November's GHAS office hours; segment 5 invites them only if so. Ask Kevin |
| **Session 2's sandbox needs more** | Segment 8, steps 4–6, need a repo that CAE's rulesets and required workflows govern, or screenshots. Segment 9 needs a campaign on alerts with autofixes, CCA enabled, and one finished CCA pull request. Part 1 needs GHES 3.21 screenshots, or the fallback sandbox with Copilot Autofix disabled and Code Quality off. Needed before session 2 — 15 or 21 October |
| **Default security configuration vs migrated repos** | The docs promise it only for newly created repos, and say transferred repos need a manual apply. Test one GEI migration in the sandbox, or ask Michael Vincent, so segment 6's "arrived with CodeQL off" has a known cause |
| **A GHES 3.21 instance for Part 1's screenshots** | No longer optional. Part 1 should show Black developers their own screens, from a GHES 3.21 instance with GHAS on — never Black itself. Fallback: a GitHub.com sandbox with Copilot Autofix disabled and Code Quality off, saying once that on Black the tab is Security |
| Scope cut, GHAS | Plan of record had 90 minutes; the slot is 60, and Part 2 — the GitHub.com half — has 30. The inheritance table above is how Part 2 fits — but say so rather than silently dropping content |
| ✅ Audience overlap | Resolved by the split. Part 1 stands alone for Black; Part 2's callbacks each get a one-line recap — see **What session 2 inherits** |

---

## Expect these in the room

- **"We already have Sonar."** Named as a competing contract slowing Code
  Quality adoption.

---

## Vocabulary

Session 2's own terms. The shared ones — Black and Blue, AI credits, GitHub
Copilot App, CCA and CCR — are in [the overview](cae-overview.md#vocabulary).

**Kevin also says On-Prem and Cloud.** At session 2's split, say both —
*"Black — on-prem"* — so nobody misses their cue to leave or stay.

**"Dependency scanning" is Kevin's umbrella.** Use it as the pillar heading in
session 2, then name the dependency graph, Dependabot and dependency review,
because those are the names on the settings page and in the docs.

**Say "MCP isn't available on Black", never "MCP is GitHub.com-only".** The
local GitHub MCP server does support GHES. Kevin confirmed on 29 September
that MCP doesn't exist for Black — that's CAE's set-up, not the product.
