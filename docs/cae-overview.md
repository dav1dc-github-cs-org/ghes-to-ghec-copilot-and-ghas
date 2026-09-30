# CAE developer enablement — overview

One programme: two training sessions, then two office hours. This file holds
what applies to all of it. Each session's plan and invite have their own file.

| File | Holds |
|---|---|
| This overview | Audience, sequence, calendar, delivery, open items that affect both sessions, account items and shared vocabulary |
| [Session 1 — Copilot notes](cae-session-1-copilot-notes.md) | What we teach on 8 October, and session 1's open items |
| [Session 1 — Copilot invite](cae-session-1-copilot-invite.md) | The attendee invite as sent, and our superseded draft |
| [Session 2 — GHAS notes](cae-session-2-ghas-notes.md) | What we teach in the GHAS session, the GHES 3.21 vs GitHub.com reference, and session 2's open items |
| [Session 2 — GHAS invite](cae-session-2-ghas-invite.md) | The attendee invite, and notes on it |

**Audience.** Session 1: engineers migrating GHES → GHEC. Session 2: Part 1
for everyone, Black and Blue; Part 2 for Blue only. Orange is out of scope
(DOD, data cannot leave the building) and no Orange staff attend.

> ✅ **Settled 29 September — session 2 is one session in two halves.** On 23
> September Kevin asked for the GHAS session to work for a mixed room —
> controlled (Black) and non-controlled (Blue) developers. On 29 September he
> set the shape: the GHAS both platforms share comes first, for everyone; then
> Black developers leave, and the second half covers what only GitHub.com has.
> Still open with Kevin: what's live on Black by the session date, and whether
> the recording can split at the half — see the open items in
> [session 2's notes](cae-session-2-ghas-notes.md#open-before-materials-lock).

**Sequence: Copilot first, then GHAS.** This matches Élodie's stated priority —
she has twice asked that training lead on the day-to-day difference between
local Copilot and the platform, with security and administration treated as
supporting content. It also creates a real teaching dependency: the second half
of the GHAS session gets shorter and lands harder because the concepts it needs
are already in the room. See **What session 2 inherits** in
[session 2's notes](cae-session-2-ghas-notes.md).

## Calendar

| Session | When | Length | Invite |
|---|---|---|---|
| 1 — Your repositories on GitHub.com | Thursday 8 October, 13:00–14:30 ET | 90 min | ✅ **Sent 23 September** as *GitHub SaaS Capabilities: What's New in GitHub Cloud* — record in [the session 1 invite](cae-session-1-copilot-invite.md). ✅ GHAS + Copilot re-added to the agenda 24 September |
| 2 — GHAS on Black and Blue | Wednesday 21 October, 14:00–15:00 ET — Kevin's placeholder. ⚠️ **On 29 September Kevin asked for Thursday 15 October** — his team wants it closer to session 1. You said the date follows a firm agenda | 60 min, in two halves | **Drafted 29 September, not sent** — see [the session 2 invite](cae-session-2-ghas-invite.md). It states the split time, so Black developers can plan for half an hour. Two weeks' notice means sending by **6 October** for the 21st (you're out on the 7th), or by **1 October** for the 15th |
| 3 — Copilot office hours | First week of November | 60 min | — |
| 4 — GHAS office hours | Third week of November | 60 min | — |

Thirteen days between the two training sessions, matching Kevin's roughly
two-week spacing, and neither falls on a Friday. GHAS moved back from 30
September so it follows Copilot; 30 September could not have held the Copilot
session in any case, because Élodie needs two weeks' notice for invitations.

The office-hours dates in Kevin's revised plan already place Copilot's ahead of
GHAS's, so they were written against this ordering. Flipping the two training
sessions makes the four-touchpoint sequence consistent rather than crossed —
and Wednesday 4 and Wednesday 18 November would hold the same spacing and the
same weekday as the GHAS session.

## Delivery

**Delivery model:** one delivery each, recorded for async. Kevin hosts the bridge
and records. ⚠️ Session 2 may split its recording at the half — see segment 5
of [session 2's notes](cae-session-2-ghas-notes.md). Office hours follow each
session after a deliberate gap, so attendees arrive with real implementation
questions rather than hypotheticals.

**Office hours audience is merged.** Élodie's preference is one room combining
newly-migrated and established users rather than separate tracks — the
experienced users answer half the questions themselves.

**Recruit champions from the recording.** "One and done" only scales through
people, and CAE started a formal champion network that lapsed and needs
follow-up. Patrick has offered help rebuilding it. Close both sessions by
naming the champions ask explicitly.

**Kevin is in the room and has offered to help.** He hosts the bridge, records,
and said he will flag anything missed. Brief him beforehand on the open items
below and in each session's notes, so he can field CAE-internal questions —
particularly the org model and anything about who owns security — rather than
leaving them hanging.

**Standing rule:** anything that smells of cost redirects to the attendee's
manager or Platform Engineering.

---

## Open before materials lock

These affect both sessions. Each session's own open items are at the end of
its notes:
[session 1](cae-session-1-copilot-notes.md#open-before-materials-lock) and
[session 2](cae-session-2-ghas-notes.md#open-before-materials-lock).

| Item | Why it matters |
|---|---|
| ⚠️ **Agentic autofix is a public preview** | Where CCA is available, assigning a code scanning alert to Copilot starts a CCA session instead of Copilot Autofix, billed as a CCA session using AI credits. Re-run segment 9's step 3 in the sandbox the week before session 2, and check session 1's item 6 seed line — "starting from the fix Copilot Autofix has already drafted" — before 8 October |
| **Visual Studio users** | ✅ Settled for session 2 on 29 September: demo in VS Code, poll the room, and one line on Visual Studio 17.14 — Kevin says it's used beyond the Vehicle team (segment 8). Session 1's item 4 stays VS Code-only, since that plan is validated. Kevin said on 24 September that the whole Vehicle team — a couple of hundred people — works in Visual Studio, and that he'd revise the session 1 description; not on the invite as of 29 September |
| **JetBrains users — ask Kevin** | Copilot telemetry for CAE's enterprise, April to August 2026, splits completions VS Code 52%, JetBrains 28%, Visual Studio 20%. JetBrains outweighs Visual Studio, yet only Visual Studio gets a nod. JetBrains IDEs support MCP, agent mode and CCR, and the GitHub MCP server there needs only the latest GitHub Copilot plugin — no version floor like Visual Studio's 17.14. Ask Kevin whether session 2's segment 8 polls for JetBrains too and adds one line — *"If you're in a JetBrains IDE, the GitHub MCP server works there too — the steps are in the handout"* — which means JetBrains steps in Blue's handout; and whether session 1's item 4 says it too. Caveats: the telemetry is flagged experimental, counts completions only, and leaves out September, when VS Code's count fell towards zero for no known reason. The feature matrix also lists custom instructions and next edit suggestions as preview in JetBrains, and session 1's frame lists both as already working |
| **Jenkins → Actions timing** | Kevin is finding out when the first Jenkins → Actions migration happens (23 September). It decides when the third session you offered, on Actions migration, would run |
| Confirm GHAS licences are **applied**, not just provisioned | Provisioned 16 September. If not applied, a live demo fails |
| Code Quality visibility gap | Élodie sees data for only 22 repos. Don't demo a dashboard that may look broken |

Use a sandbox repo for every demo, never a live CAE one.

### Account items surfaced while building this — not training work

Carry these to Patrick or handle as CSA; they do not belong in either session
but should not be lost.

| Item | Detail |
|---|---|
| **Catalin Sandolache's GitHub Connect questions are unanswered** | Asked 6 July 2026: GHAS licensing options across the GHES estate, GitHub Connect's communication model for restricted networks, and assurance that Connect cannot exfiltrate from the **Orange** network. Recorded only in Gainsight — nothing in the GitHub issue. Orange is DOD, stays on-prem, and is out of training scope, but a customer security question has sat open for two and a half months |
| **Technical companion is stale** | On `CAE Inc - Parent` (`001d0000023cWmlAAE`), the CO-covered record: `ghes_prod_version` still reads 3.19.2 (now 3.21.4); `known_limitations_or_blockers` still says "cannot mix licenses", resolved by the 15 Sep volume conversion; `str_ghes_ghec_migration` is **empty** despite being the headline programme; `str_core_gh_products_used` says GHAS Bundled, now volume at 1,200 seats; `str_copilot_features_enabled` omits Memory and MCP |
| **Orange GHAS entitlement** | The 1,200 volume licences cover GHEC and GHES. Orange is GHES that never migrates. Whether its instances are entitled, and how licences reach a network that cannot call GitHub, is unrecorded |
| **GEI and access mapping** | GEI's docs say team and user access aren't migrated from GHES. The 13 August debrief records the opposite, said live on the call. If the docs are right, CAE's access-mapping step (Terraform teams → repo access) has to grant access itself, or repos land with nobody on them. Raise with Jack and Patrick before batch 1 |
| **Alert-state migration** | If GHAS is switched on on-prem before repos move, triage decisions are lost unless the migration workflow runs `gh gei migrate-code-scanning-alerts` and `gh gei migrate-secret-alerts`. For CAE's migration team |

---

## Vocabulary

Use CAE's own words: **Black** (on-prem, controlled) and **Blue** (SaaS,
uncontrolled). Élodie framed the programme as moving "from black to blue" and
Kevin uses Controlled/Uncontrolled for the same split. "GitHub EXT" is a third
internal name for the externally-exposed server — avoid it in the room.

**Session 2 adds three terms of its own** — On-Prem and Cloud, "dependency
scanning", and how to say MCP isn't available on Black. They're in
[session 2's notes](cae-session-2-ghas-notes.md#vocabulary).

Use GitHub's current words for billing: **AI credits**, not premium requests.
Premium requests are now documented as request-based billing (legacy) and apply
only to Pro and Pro+ subscribers on an existing annual plan. CAE is on Business
and Enterprise, so AI credits are the only correct term for this audience.
Expect the old term in the room — several people will have read it in last
year's docs — and correct it once, lightly.

Always say **GitHub Copilot App** in full — never "Copilot app", "the desktop
app", or just "Copilot". This matters more at CAE than at most accounts: they
are a Microsoft estate running Teams, M365 and Azure, so "Copilot" unqualified
will be heard as Microsoft Copilot by at least part of the room. The same rule
applies verbally, in slide titles and in the recording, where a stray
"Copilot app" is uncorrectable afterwards.

**CCA and CCR are CAE's shorthand, so introduce them and then use them.** Both
are expanded on first appearance — in the framing table, the agenda, the
section headings and the attendee email — and the short forms are fair game
after that. Capitalise **Copilot Cloud Agent** and **Copilot Code Review** as
product names throughout.
