# feature/scenario-pack-import (migrated copy) — session 2, Part 1, "PR A"

Shows on-prem developers where findings land: **a new CodeQL alert and dependency review on the
same pull request**. Pushed by prompt 20; opened and authored by the account with no Copilot
seat; labelled `demo-keep`; **never merged**.

## Commit message

    Import scenario packs from other training centres

## Pull request title

    Import scenario packs from other training centres

## Pull request body

    Adds POST /api/scenario-packs: an instructor uploads a scenario pack (zip) exported by
    another training centre, and its scenarios and attachments are unpacked into training-data/.
    Uses commons-io for the stream copy.

## What it shows

| Where | What |
|---|---|
| Conversation tab and Files changed | CodeQL `java/zipslip` in `ScenarioPackImporter.java`: the archive entry name reaches `new File(root, name)` and a `FileOutputStream`. The same class of bug as the hero alert on `main`, caught before merge this time |
| Files changed, `scenario-io/pom.xml` rich diff | Dependency review: `commons-io:commons-io:2.6` added, with known advisories |
| Checks | The CodeQL default setup analysis for the pull request |

Needs code scanning default setup on the migrated copy first (switched on between sessions).
Copilot Autofix must be **off** on the migrated copy, or the alert comment will carry a suggested
fix in Part 1.
