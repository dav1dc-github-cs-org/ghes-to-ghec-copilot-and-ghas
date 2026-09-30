# feature/gust-model (showcase) — session 1, item 5

The pull request Copilot code review reviews on stage. Created from
`feature-gust-model.patch` by `scripts/split-into-repos.sh`; opened live by prompt 16.

**Presenter-only.** The defects list below is the answer key. Never open this file on screen and
never give it to Copilot as context.

## Commit message

    Add discrete gust model for turbulence scenarios

## Pull request title

    Add discrete gust model for turbulence scenarios

## Pull request body

    Adds a one-minus-cosine discrete gust, with light continuous turbulence, for the crosswind and
    turbulence scenarios. The instructor sets the gust direction (degrees) and peak speed (knots)
    from the gust card on the weather page.

    Not wired into the flight model yet; that is the follow-up pull request.

## Planted defects (answer key)

| # | Where (`GustModel.java` unless noted) | Defect | Rule it breaks | Expect |
|---|---|---|---|---|
| 1 | `step()`: `peakKnots * 1.852` | Knots to km/h, not m/s. Gusts are 3.6 times too strong | `Units.knotsToMps` (java.instructions.md) | High |
| 2 | `step()`: `step / rampSteps` | Integer division: the ramp is 0 until the last ramp step, so the gust starts abruptly | — | High |
| 3 | `step()`: `Math.cos(directionDeg)`, `Math.sin(directionDeg)` | Degrees passed to trig functions | Radians everywhere (copilot-instructions.md) | High |
| 4 | Field `new Random()` | Unseeded random: sessions no longer replay | ADR 0004 | Medium |
| 5 | `loadProfile()` | `BufferedReader` never closed | — | Medium or Low |
| 6 | `loadProfile()` | `IOException` and `NumberFormatException` swallowed: a broken profile silently disables gusts | — | Medium |
| 7 | `loadProfile()` | `getResourceAsStream` may return null | — | Low |
| 8 | `GustModelTest` | The test asserts nothing | java.instructions.md | Low |

Good candidates for **Fix with Copilot**: 2 (integer division) or 3 (degrees vs radians). Both
are one-line fixes the agent can verify with a real assertion.
