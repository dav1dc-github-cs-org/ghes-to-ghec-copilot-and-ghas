---
description: "Session 1, item 6: review the cloud agent's pull request against its issue — the review you'd give a colleague."
agent: agent
tools: ['github/list_issues', 'github/issue_read', 'github/list_pull_requests', 'github/pull_request_read', 'github/get_file_contents']
---
In `DEMO_ORG/flight-sim-platform`, find the open pull request Copilot opened for the open issue labelled `migration`. If there are several, use the newest one that isn't labelled `demo-keep`.

Review it against the issue's acceptance criteria:

1. One line per criterion: ✅ or ❌, with evidence from the diff (file and line).
2. Anything outside the issue's scope: Artifactory or mirror settings, Jenkins stages, dependency versions, Checkstyle skipped or weakened, tests changed just to pass.
3. What the pull request says platform engineering has to do.
4. Your recommendation, in one sentence.

Don't approve, merge or comment on the pull request. No more than 20 lines.
