"""Comparison engine for simulator and QTG reference CSV data."""

from __future__ import annotations

import csv
from dataclasses import dataclass
from pathlib import Path
from typing import Iterable, Sequence

from .testcases import ChannelSpec, TestCase


@dataclass(frozen=True)
class TimeSeries:
    fieldnames: tuple[str, ...]
    rows: tuple[dict[str, float], ...]

    @property
    def times(self) -> tuple[float, ...]:
        return tuple(row["time"] for row in self.rows)


@dataclass(frozen=True)
class ChannelResult:
    name: str
    unit: str
    tolerance: str
    max_abs_error: float
    limit_at_max_error: float
    time_at_max_error: float
    passed: bool


@dataclass(frozen=True)
class ComparisonResult:
    test_id: str
    title: str
    reference_path: Path
    candidate_path: Path
    channels: tuple[ChannelResult, ...]

    @property
    def passed(self) -> bool:
        return all(channel.passed for channel in self.channels)


def load_csv(path: str | Path) -> TimeSeries:
    csv_path = Path(path)
    with csv_path.open(newline="", encoding="utf-8") as handle:
        reader = csv.DictReader(handle)
        if not reader.fieldnames or "time" not in reader.fieldnames:
            raise ValueError(f"{csv_path} must contain a time column")
        rows = tuple({key: float(value) for key, value in row.items()} for row in reader)
    if len(rows) < 2:
        raise ValueError(f"{csv_path} must contain at least two data rows")
    _validate_time_base(csv_path, (row["time"] for row in rows))
    return TimeSeries(tuple(reader.fieldnames), rows)


def compare_files(test_case: TestCase, candidate_path: str | Path) -> ComparisonResult:
    return compare_series(test_case, load_csv(test_case.reference_path), load_csv(candidate_path), Path(candidate_path))


def compare_series(
    test_case: TestCase,
    reference: TimeSeries,
    candidate: TimeSeries,
    candidate_path: str | Path,
) -> ComparisonResult:
    _require_channels(reference, test_case.channels, "reference")
    _require_channels(candidate, test_case.channels, "candidate")
    candidate_columns = {
        channel.name: tuple(row[channel.name] for row in candidate.rows)
        for channel in test_case.channels
    }
    candidate_times = candidate.times
    results: list[ChannelResult] = []
    for channel in test_case.channels:
        max_error = -1.0
        limit_at_max = 0.0
        time_at_max = 0.0
        passed = True
        values = candidate_columns[channel.name]
        for ref_row in reference.rows:
            time_value = ref_row["time"]
            ref_value = ref_row[channel.name]
            candidate_value = interpolate(candidate_times, values, time_value)
            error = abs(candidate_value - ref_value)
            limit = channel.tolerance.limit_for(ref_value)
            if error > limit:
                passed = False
            if error > max_error:
                max_error = error
                limit_at_max = limit
                time_at_max = time_value
        results.append(
            ChannelResult(
                name=channel.name,
                unit=channel.unit,
                tolerance=channel.tolerance.label,
                max_abs_error=max_error,
                limit_at_max_error=limit_at_max,
                time_at_max_error=time_at_max,
                passed=passed,
            )
        )
    return ComparisonResult(
        test_id=test_case.id,
        title=test_case.title,
        reference_path=test_case.reference_path,
        candidate_path=Path(candidate_path),
        channels=tuple(results),
    )


def interpolate(times: Sequence[float], values: Sequence[float], target_time: float) -> float:
    if target_time < times[0] or target_time > times[-1]:
        raise ValueError(f"Target time {target_time} outside candidate time range")
    low = 0
    high = len(times) - 1
    while low <= high:
        mid = (low + high) // 2
        if times[mid] < target_time:
            low = mid + 1
        elif times[mid] > target_time:
            high = mid - 1
        else:
            return values[mid]
    left = high
    right = low
    span = times[right] - times[left]
    if span == 0:
        return values[left]
    fraction = (target_time - times[left]) / span
    return values[left] + fraction * (values[right] - values[left])


def _validate_time_base(path: Path, times: Iterable[float]) -> None:
    previous: float | None = None
    for time_value in times:
        if previous is not None and time_value <= previous:
            raise ValueError(f"{path} time column must be strictly increasing")
        previous = time_value


def _require_channels(series: TimeSeries, channels: Sequence[ChannelSpec], label: str) -> None:
    missing = sorted(channel.name for channel in channels if channel.name not in series.fieldnames)
    if missing:
        raise ValueError(f"{label} data missing channels: {', '.join(missing)}")
