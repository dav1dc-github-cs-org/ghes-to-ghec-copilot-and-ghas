from __future__ import annotations

from pathlib import Path

import pytest

from qtg.compare import TimeSeries, compare_series, interpolate, load_csv
from qtg.testcases import get_test_case


def test_interpolate_returns_exact_sample() -> None:
    assert interpolate((0.0, 1.0, 2.0), (10.0, 20.0, 30.0), 1.0) == 20.0


def test_interpolate_between_samples() -> None:
    assert interpolate((0.0, 1.0, 2.0), (10.0, 20.0, 40.0), 1.5) == 30.0


def test_interpolate_rejects_out_of_range() -> None:
    with pytest.raises(ValueError, match="outside candidate time range"):
        interpolate((0.0, 1.0), (10.0, 20.0), 1.5)


def test_load_csv_validates_time_column(tmp_path: Path) -> None:
    path = tmp_path / "missing-time.csv"
    path.write_text("speed\n1\n2\n", encoding="utf-8")
    with pytest.raises(ValueError, match="time column"):
        load_csv(path)


def test_load_csv_validates_increasing_time(tmp_path: Path) -> None:
    path = tmp_path / "bad-time.csv"
    path.write_text("time,speed\n0,1\n0,2\n", encoding="utf-8")
    with pytest.raises(ValueError, match="strictly increasing"):
        load_csv(path)


def test_compare_series_passes_with_interpolated_candidate() -> None:
    test_case = get_test_case("1.b.1")
    reference = TimeSeries(
        ("time", "distance_m", "ground_speed_kt", "longitudinal_accel_g", "n1_left_pct"),
        (
            {
                "time": 0.0,
                "distance_m": 0.0,
                "ground_speed_kt": 0.0,
                "longitudinal_accel_g": 0.20,
                "n1_left_pct": 30.0,
            },
            {
                "time": 1.0,
                "distance_m": 17.5,
                "ground_speed_kt": 20.0,
                "longitudinal_accel_g": 0.21,
                "n1_left_pct": 40.0,
            },
            {
                "time": 2.0,
                "distance_m": 35.0,
                "ground_speed_kt": 40.0,
                "longitudinal_accel_g": 0.22,
                "n1_left_pct": 50.0,
            },
        ),
    )
    candidate = TimeSeries(
        reference.fieldnames,
        (
            {
                "time": 0.0,
                "distance_m": 0.0,
                "ground_speed_kt": 0.2,
                "longitudinal_accel_g": 0.201,
                "n1_left_pct": 30.1,
            },
            {
                "time": 2.0,
                "distance_m": 35.2,
                "ground_speed_kt": 40.2,
                "longitudinal_accel_g": 0.221,
                "n1_left_pct": 50.1,
            },
        ),
    )
    result = compare_series(test_case, reference, candidate, "candidate.csv")
    assert result.passed
    assert {channel.name for channel in result.channels} == {
        "distance_m",
        "ground_speed_kt",
        "longitudinal_accel_g",
        "n1_left_pct",
    }


def test_compare_series_fails_when_channel_exceeds_tolerance() -> None:
    test_case = get_test_case("2.a.1")
    fields = (
        "time",
        "altitude_ft",
        "airspeed_kt",
        "pitch_deg",
        "elevator_trim_deg",
    )
    reference = TimeSeries(
        fields,
        (
            {
                "time": 0.0,
                "altitude_ft": 31000.0,
                "airspeed_kt": 280.0,
                "pitch_deg": 2.0,
                "elevator_trim_deg": -1.4,
            },
            {
                "time": 1.0,
                "altitude_ft": 31000.0,
                "airspeed_kt": 280.0,
                "pitch_deg": 2.0,
                "elevator_trim_deg": -1.4,
            },
        ),
    )
    candidate = TimeSeries(
        fields,
        (
            {
                "time": 0.0,
                "altitude_ft": 31080.0,
                "airspeed_kt": 280.0,
                "pitch_deg": 2.0,
                "elevator_trim_deg": -1.4,
            },
            {
                "time": 1.0,
                "altitude_ft": 31080.0,
                "airspeed_kt": 280.0,
                "pitch_deg": 2.0,
                "elevator_trim_deg": -1.4,
            },
        ),
    )
    result = compare_series(test_case, reference, candidate, "candidate.csv")
    assert not result.passed
    altitude = next(channel for channel in result.channels if channel.name == "altitude_ft")
    assert not altitude.passed
    assert altitude.max_abs_error == 80.0
