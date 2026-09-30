# ADR 0004: All randomness comes from the session's seeded source

- Status: accepted
- Date: 2021-06-02

## Context

A QTG run and a debrief replay must reproduce a session exactly. Two models were drawing from
`java.util.Random` created without a seed, so a replayed engine-failure scenario diverged after a
few seconds of turbulence, and a QTG test that had passed could not be reproduced for the
regulator.

## Decision

Every random draw in the simulation comes from `SimContext.random()`, a `DeterministicRandom`
seeded per session. A component that needs its own stream calls `fork(name)` with a stable name,
which does not disturb the parent stream.

## Consequences

- `java.util.Random`, `Math.random()` and unseeded Python `random` are not used in simulation
  code; reviewers reject them.
- Adding a component does not change what existing components draw.
