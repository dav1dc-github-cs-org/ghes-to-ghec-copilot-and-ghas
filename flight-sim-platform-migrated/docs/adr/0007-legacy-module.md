# ADR 0007: Keep ported legacy code in its own module

- Status: accepted
- Date: 2023-11-20

## Context

The motion cueing filter and the scenario card parser were ported from the previous-generation
simulator host, whose motion software was written in C++. The ported code works and is covered by
tests, but it does not follow the current coding standard, and rewriting it before the old host is
retired would delay the retirement.

## Decision

Ported code lives in the `legacy` module and is checked against the relaxed rule set
(`flightsim-checks-legacy.xml`) until it is replaced. New code does not go into `legacy`.

## Consequences

- The relaxed rule set is configured in `legacy/pom.xml`, separately from the rest of the build.
- Anything moved out of `legacy` must meet the current standard first.
