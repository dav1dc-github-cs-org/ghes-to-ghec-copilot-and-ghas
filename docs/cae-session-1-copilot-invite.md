# Attendee invitation — Copilot session

**Part of [CAE developer enablement](cae-overview.md).** "The plan" and item
numbers below mean [session 1's notes](cae-session-1-copilot-notes.md).

## ✅ Sent 23 September 2026

Approved by Dave Crawford with Kevin Cheung, and sent from CAE's Platform
Engineering calendar. The event was first created on 16 September; the
approved version went out on 23 September. **The plan is authoritative, not
this email.** Dave validated the session plan with Kevin on 23 September, so
where the two differ the plan wins. This section records what attendees were
told.

✅ **Corrected 24 September.** Kevin confirmed on 23 September that leaving the
GHAS + Copilot item off the agenda was a mistake, and re-added it the next
morning as *GitHub Advanced Security + Copilot*, between Actions and CCR. The
update reached your calendar at 10:23 ET. Checked 29 September: the outcomes
list is unchanged, so the MCP outcome and the "no prior knowledge" line did
not come back.

| | |
|---|---|
| **Title** | GitHub SaaS Capabilities: What's New in GitHub Cloud |
| **When** | Thursday 8 October 2026, 13:00–14:30 ET |
| **Where** | Microsoft Teams, recorded. Join details live in the invite — deliberately not copied here |
| **Organiser** | CAE Platform Engineering. Kevin hosts and records |
| **Audience named** | Developers, technical leads and repository maintainers |
| **Distribution** | Open — *"Please forward this invitation to anyone on your team who may benefit"* |
| **Language** | English only |

### What it lists, and where the plan covers each topic

| Topic, as sent | Plan item |
|---|---|
| What changed when your repositories moved to GitHub.com | 1 |
| Teams, permissions, and repository management | 2, including CODEOWNERS and repository limits |
| GitHub Actions and automation capabilities | 3 |
| GitHub Advanced Security + Copilot — *added 24 September* | 4 |
| Copilot Code Review (CCR) | 5 |
| Copilot Cloud Agent (CCA) | 6 |
| Copilot experiences beyond the IDE | 7 |
| AI credits and usage considerations | 8 |

| "By the end of this session, you'll understand how to…" | Plan item |
|---|---|
| Navigate the GitHub.com organization and repository model | 1, 2a, 2b |
| Manage and understand repository permissions and team access | 2a, 2b, 2b-bis |
| Explore new automation capabilities available through GitHub Actions | 3 |
| Use Copilot to review pull requests and accelerate development workflows | 5 |
| Leverage Copilot Cloud Agent for task execution and pull request creation | 6 |
| Understand which Copilot capabilities consume AI credits and which do not | 8 |

Every topic and outcome in the invite has a home in the plan, and since 24
September every plan item has a topic in the invite. Item 4 still has no
outcome line — see below.

### What the email leaves out — presenter tips

- **Item 4 is back on the agenda, but only as a topic.** Re-added 24
  September. The MCP outcome didn't come back, and nor did our line telling
  people it's fine if CodeQL, secret scanning and Dependabot are new names.
  Keep item 4's two-minute grounding — the room's unfamiliarity with those
  terms came from the 14 September discovery, not from the email.
- **"Your build pipelines are not changing" didn't make it.** The Actions topic
  now stands alone — which is exactly how a developer reads "your build is
  about to break." Flagged in **Not in scope, say so early** in
  [session 1's notes](cae-session-1-copilot-notes.md#not-in-scope-say-so-early).
- **Office hours aren't mentioned.** Kevin's hold-your-questions design depends
  on people knowing November exists. Flagged in item 9: say it at the open too.
- **The CCA environment note is gone** — org-owned repo, hosted runner by
  default, self-hosted possible. Item 6 already covers it; nothing to change.
- **CODEOWNERS and repository limits aren't named**, though both sit under
  "repository management". They were customer-requested, so 2b-bis stays.
- **No per-topic timings were sent**, so the 90-minute split can still move.

**CAE's export-control footer is on the invite.** It makes the host responsible
for ensuring no export-controlled or classified information is shared — one
more reason every demo runs in the sandbox, never a live CAE repo.

---

## Our pre-send draft — superseded

Kept for reference, and as the template for
[the GHAS invite](cae-session-2-ghas-invite.md). **Do not resend it.** Where it
differs from the record above, the record wins.

Written for Kevin to send. No commercial content, no admin framing, nothing
unconfirmed. Keep it short — this audience is busy and books weeks ahead.

**Subject line options**

1. *What changes for you now your repos are on GitHub.com — 8 October*
2. *GitHub Copilot after the move: 90 minutes, 8 October*
3. *Your editor just got access to your repository — session 8 October*

---

**GitHub Copilot on GitHub.com — what's actually new for you**
**Thursday 8 October · 90 minutes · recorded**

As your repositories move from GitHub Enterprise Server to GitHub.com, your
Copilot licence doesn't change and neither does the way you work in VS Code.
One thing does change: **Copilot can now see your repository** — your issues,
your pull requests, and the security findings on your code.

That single change unlocks a set of capabilities that simply weren't possible
before. This session is about those, and it's built for developers rather than
administrators — no licensing, no admin console, no policy debates. Just what
you can do next week that you couldn't do last month.

**Your build pipelines are not changing.** Jenkins stays, and so does the way
your builds run today. We'll cover what Actions makes possible alongside them,
not how to replace anything.

**A note on Copilot Cloud Agent (CCA).** It doesn't run on your laptop — it
works in its own temporary environment powered by GitHub Actions. That means it
needs a repository owned by an organisation rather than a personal one, and by
default it uses a GitHub-hosted runner. We'll cover how that environment is
configured, including pointing it at our own self-hosted runners.

**No prior knowledge assumed.** If CodeQL, secret scanning and Dependabot are
new names to you, that's expected — we cover what each one finds before we use
them.

**Agenda**

| | |
|---|---|
| What changed when your repo moved — and what didn't | 4 min |
| Teams, permissions and repository administration | 13 min |
| GitHub Actions and CI/CD: what's now possible | 12 min |
| **Advanced Security + Copilot: better together** | 16 min |
| Copilot Code Review (or CCR) on your pull requests | 15 min |
| Copilot Cloud Agent (or CCA) | 15 min |
| Copilot beyond the editor: GitHub.com, Spaces and the GitHub Copilot App | 6 min |
| What actually uses AI credits — and what's free | 4 min |
| Wrap-up | 5 min |

**You'll leave knowing how to**

- Find your repository, your team and your permissions — and know what a
  managed account can and can't do on GitHub.com
- Use a CODEOWNERS file so the right people are automatically asked to review
  changes to the code they own
- Keep a repository inside GitHub's size limits, and know when a large file
  belongs in Git LFS rather than in Git
- Understand what's now possible with Actions — hosted runners, the public
  ecosystem, and sharing workflows across teams — and how all of it sits
  alongside the Jenkins pipelines that aren't going anywhere
- Configure the GitHub MCP server so Copilot can see your CodeQL, secret
  scanning and Dependabot findings — and ask it which to fix first
- Get a Copilot review on a pull request before a colleague sees it
- Hand a well-described issue to the cloud agent and review the pull request
  it opens
- Tell the difference between what draws down AI credits and what's unlimited

**Practical**

- Nothing to install and no preparation needed — bring a laptop if you'd like
  to follow along.
- The session is recorded, so it's fine if you can't make the time.
- **Copilot office hours follow in November** — an open session for the
  questions that surface once you've tried this on your own work. Save them up;
  that's what it's for.

---

### Notes for you, not for the email

*These explain choices in our draft. Where the sent version differs, the record
above wins.*

- **"Better together" is the honest framing and also the strongest one.** It is
  a Copilot capability story rather than a security session, which keeps faith
  with Élodie's request that security sit behind Copilot — while answering
  CAE's own April ask for CodeQL over MCP.
- **The agenda no longer claims anything that worked on GHES.** Ask, Edit and
  Agent modes are out; every line is something the migration genuinely
  unlocked.
- **The org model no longer blocks the send.** CAE confirmed on 23 September it
  is consolidating to a much smaller number of organisations; the segment
  teaches that direction rather than a count.
- **The Actions line is deliberately paired with "your pipelines aren't
  changing."** Without that, "Actions and CI/CD" on an agenda reads to a
  developer as "your build is about to break." **The sent version dropped the
  pairing**, so the presenter now has to say it aloud.
- **Custom agents are described as a direction, not a feature**, because their
  enablement is still unverified.
- **Consider sending a French version too.** Several stakeholders correspond in
  French and the engineering base is Montreal — offer, don't assume. Session 1
  went out in English only; carry the offer to
  [the GHAS invite](cae-session-2-ghas-invite.md).
