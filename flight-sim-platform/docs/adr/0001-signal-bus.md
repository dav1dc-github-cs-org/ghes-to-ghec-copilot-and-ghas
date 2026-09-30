# ADR 0001: Components communicate only through the signal bus

- Status: accepted
- Date: 2019-03-12

## Context

The previous-generation host linked models directly to each other. Changing the step order or
running one model at a lower rate meant editing several models, and the instructor station had
to know every model's internals to show a value.

## Decision

Every model is a `SimComponent`. Components publish and read named `double` signals on the
`SimBus` and never hold references to each other. Signal names that cross module boundaries are
defined once in `core.Signals`.

## Consequences

- The executive owns the step order; it is visible in one place.
- The instructor station, data recorder and snapshots read the bus without knowing the models.
- Signals are untyped doubles, so the unit is part of the name (`fdm.altitude.m`,
  `hyd.green.pressure.psi`) and reviewers check it.
