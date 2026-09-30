# chore/dependabot-artifactory (migrated copy) — session 2, Part 1, "PR B"

The Dependabot options reference the customer asked for: the one option taught is **`registries`**, shown with a
`maven-repository` entry for Artifactory. Pushed by prompt 20; opened by the account with no
Copilot seat; labelled `demo-keep`; **never merged** (the Artifactory host is fictional, and
Dependabot reads `dependabot.yml` from the default branch only).

## Commit message

    Let Dependabot sign in to Artifactory

## Pull request title

    Dependabot: sign in to Artifactory for Maven updates

## Pull request body

    Adds Dependabot version updates for the Java build, and the registries entry Dependabot needs
    to sign in to Artifactory. Before merging, add the ARTIFACTORY_USER and ARTIFACTORY_TOKEN
    Dependabot secrets (Settings → Secrets and variables → Dependabot).

## Talk track

- Part 1 (both platforms): `registries` is how Dependabot signs in to a private registry. The
  credentials are Dependabot secrets, never values in the file. Link each platform's
  `dependabot.yml` reference in its own handout.
- Part 2 (cloud only, segment 7): signing in is not the whole story on GitHub.com. Hosted runners sit
  outside the training network, so the network path to Artifactory is platform engineering's
  question.
