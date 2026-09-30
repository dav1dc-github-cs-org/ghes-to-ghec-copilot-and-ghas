# Attendee invitation — GHAS session

**Part of [CAE developer enablement](cae-overview.md).** "The plan" and segment
numbers below mean [session 2's notes](cae-session-2-ghas-notes.md).

## Draft for Kevin — 29 September 2026, not sent

Built from [session 1's pre-send draft](cae-session-1-copilot-invite.md), in
the same shape, for Kevin to send from CAE's Platform Engineering calendar.
**The plan is authoritative, not this email.** The agenda maps one-to-one to
segments 1–10, in order, so if a segment changes, change its line here too. No
commercial content, no admin framing, nothing unconfirmed.

> ⚠️ **Before it goes — settle four things, then fill the brackets.**
>
> 1. **Date and time.** Kevin's placeholder is Wednesday 21 October,
>    14:00–15:00 ET, which puts the split at 14:30. On 29 September he asked
>    for Thursday 15 October instead, when you're free 09:00–10:30 and
>    13:00–14:30 ET. Two weeks' notice means sending by **1 October** for the
>    15th, or by **6 October** for the 21st. The 15th also means segment 9's
>    sandbox is needed by the 14th.
> 2. **Office hours for Black.** The office-hours line invites everyone. Keep
>    it as written only if Kevin confirms Black developers are welcome in
>    November. Otherwise, start it with *"If you're on GitHub.com,"*.
> 3. **Naming Copilot Autofix.** Kevin doesn't want Autofix discussed while
>    on-prem developers are in the room. The draft names it only under Part 2,
>    labelled GitHub.com only — his own "say the next part only applies to
>    Cloud". If he'd rather Black's inboxes didn't see it at all, use *"From
>    alert to fix, on GitHub.com"* as Part 2's fourth agenda line, and
>    *"Follow an alert all the way to a pull request you review"* as its last
>    outcome.
> 4. **The recording.** If Kevin agrees to split it at the half — question 3
>    — add *"Each half is recorded separately."* to the recording line.
>
> Question 1 — what's live on Black by the date — doesn't block the send.
> Part 1's outcomes promise what people will know, not what will be switched
> on.

**Subject line options**

1. *GitHub Advanced Security: On-Prem and Cloud — [date] October*
2. *GitHub Advanced Security on Black and Blue — [date] October*
3. *Is security scanning on for your repository? — [date] October*

---

**GitHub Advanced Security — on-prem and on GitHub.com**
**[Thursday 15 / Wednesday 21] October · [start]–[end] ET · 60 minutes, in
two halves · recorded**

We're enabling GitHub Advanced Security (GHAS) on-prem (Black), and it's
already in use on GitHub.com (Blue). At its core it's the same product on
both: dependency scanning, code scanning and secret scanning. This session
teaches that core once, from scratch, for everyone — then spends its second
half on what GitHub.com adds.

**It comes in two halves, so you only stay for what applies to you.**

- **[start]–[split] · everyone, Black and Blue.** What each kind of scanning
  finds and why it should be on, how to check whether it's on for your
  repository, and how to turn it on.
- **[split]–[end] · GitHub.com (Blue) only.** What didn't come across when
  your repository moved, what GitHub.com adds, and how an alert becomes a
  fix.

**If all your work stays on Black, you're done at [split].** If any of your
repositories are on GitHub.com, or moving there, stay for the whole hour.

**No prior knowledge assumed.** If CodeQL, secret scanning and Dependabot are
new names to you, that's expected — we start from what each one finds. You
don't need to have been at the 8 October session either.

It's built for developers, technical leads and repository maintainers rather
than administrators — no licensing and no admin console.

**Agenda**

**Part 1 — everyone, Black and Blue · [start]–[split] ET**

| | |
|---|---|
| The same product on both platforms | 2 min |
| The three pillars: dependency scanning, code scanning and secret scanning | 12 min |
| Is it on? Checking your repository in three steps | 6 min |
| Turning it on — and who to ask when you can't | 6 min |
| Wrap-up, and questions before the split | 4 min |

**Part 2 — GitHub.com (Blue) only · [split]–[end] ET**

| | |
|---|---|
| What didn't come across when your repository moved | 5 min |
| What GitHub.com adds on top of the shared core | 11 min |
| The rest of the check: CAE's rules, and asking Copilot what it sees | 6 min |
| From alert to fix: Copilot Autofix, security campaigns and Copilot Cloud Agent (or CCA) | 5 min |
| Wrap-up | 3 min |

**You'll leave knowing how to**

*Everyone:*

- Understand what dependency scanning, code scanning and secret scanning each
  find, and why each should be on
- Spot a finding where it shows up — on your pull request, before it merges
- Check in three steps whether GHAS is on for a repository you own
- Turn on what's missing, or know who to ask when the switch isn't yours
- Tell Dependabot how to sign in to a private registry, such as Artifactory

*If you're on GitHub.com (Blue), Part 2 adds:*

- Check what didn't come across when your repository moved, and that CAE's
  rules still cover it
- Run the full seven-step check on a repository you own
- If you use Copilot, ask it what it sees in your repository's security
  findings — in VS Code, or in Visual Studio 17.14 or later
- Follow an alert all the way to a fix: Copilot Autofix drafts it, a security
  campaign groups the backlog, and CCA can open a pull request for you to
  review

**Practical**

- No preparation needed, and nothing to install.
- The session is recorded, so it's fine if you can't make the time.
- Leaving at the half? Put your questions in the meeting chat. They'll be
  answered there at the end, so you can read the answers without staying.
- **GHAS office hours follow in November** — an open session for the
  questions that come up once you've tried this on your own repositories.
  Save them up; that's what it's for.
- Please forward this to anyone on your team who works on Black or Blue.

---

### Notes for you, not for the email

- **The split time is the one line that must survive editing.** It's in the
  two-halves list, the "you're done at" line and both agenda headings, so a
  Black developer can book half an hour. Session 1 went out without per-topic
  timings; that's fine here too, as long as the split time stays.
- **Only "all your work stays on Black" sends someone home.** "Stay if any of
  your repositories are on GitHub.com, or moving there" keeps developers who
  work on both. Say Black, not just on-prem, here and in the forwarding line:
  Orange is on-prem too, and out of scope.
- **Two lines session 1's sent version lost are back — keep both.** "No prior
  knowledge assumed", because the 14 September discovery found the room
  didn't recognise CodeQL, secret scanning or Dependabot. And office hours,
  because Kevin's hold-your-questions design depends on people knowing
  November exists.
- **Part 1 names nothing Black can't have.** Copilot, Autofix, campaigns and
  CCA appear only under Part 2's GitHub.com-only label — the invite's version
  of the plan's rule 2.
- **Part 1's outcomes promise knowledge, not switches.** Dependabot alerts and
  dependency review need GitHub Connect on Black, so the invite promises
  neither — only where findings show up, how to check, and who to ask.
- **Copilot is conditional in Part 2.** Seats are CAE's binding constraint,
  hence "if you use Copilot". Autofix needs no seat, but that's segment 7's
  line to say, not the invite's — the email stays free of licensing.
- **Visual Studio is named on purpose.** Kevin asked on 29 September for a nod
  to Visual Studio users, who aren't only in Vehicle. The 17.14 floor is from
  the GitHub MCP server docs. In the email, say "Visual Studio", never
  "Vehicle".
- **The chat-questions line is a promise.** It sets up segment 5's dismissal
  and segment 10's loop-close, so agree beforehand who writes the answers.
- **Housekeeping, as for session 1.** Put the sent title on the title slide
  and the recording — both recordings, if it splits. Offer a French version.
  Expect CAE's export-control footer again; every demo stays in the sandbox.
