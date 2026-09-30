"""Tolerance models used by QTG comparisons."""

from __future__ import annotations

from dataclasses import dataclass
from typing import Literal

ToleranceKind = Literal["absolute", "percent"]


@dataclass(frozen=True)
class Tolerance:
    """Allowed channel error."""

    kind: ToleranceKind
    value: float

    def limit_for(self, reference_value: float) -> float:
        if self.kind == "absolute":
            return self.value
        if self.kind == "percent":
            return abs(reference_value) * (self.value / 100.0)
        raise ValueError(f"Unknown tolerance kind: {self.kind}")

    @property
    def label(self) -> str:
        suffix = "%" if self.kind == "percent" else ""
        return f"{self.value:g}{suffix}"


def absolute(value: float) -> Tolerance:
    return Tolerance("absolute", value)


def percent(value: float) -> Tolerance:
    return Tolerance("percent", value)
