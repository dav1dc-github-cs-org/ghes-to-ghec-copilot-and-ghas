#!/usr/bin/env python3
"""Generate deterministic FS-200 QTG reference CSV data."""

from __future__ import annotations

import csv
import math
import random
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OUTPUT_DIR = ROOT / "reference-data"
SAMPLE_RATE_HZ = 20
ROWS = 15000


def main() -> None:
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    _write_takeoff(OUTPUT_DIR / "1b1_takeoff_acceleration.csv")
    _write_pitch_trim(OUTPUT_DIR / "2a1_pitch_trim_cruise.csv")
    _write_engine(OUTPUT_DIR / "3a1_engine_acceleration.csv")


def _time(index: int) -> float:
    return index / SAMPLE_RATE_HZ


def _write_csv(path: Path, fieldnames: list[str], rows: list[dict[str, float]]) -> None:
    with path.open("w", newline="", encoding="utf-8") as handle:
        writer = csv.DictWriter(handle, fieldnames=fieldnames)
        writer.writeheader()
        for row in rows:
            writer.writerow({key: f"{value:.6f}" for key, value in row.items()})


def _write_takeoff(path: Path) -> None:
    noise = random.Random(200101)
    rows: list[dict[str, float]] = []
    for index in range(ROWS):
        time_s = _time(index)
        run_s = min(time_s, 46.0)
        accel = max(0.015, 0.215 - 0.0021 * run_s + 0.004 * math.sin(run_s / 2.7))
        speed = min(132.0, 1.94384 * accel * run_s * 20.5)
        distance = 0.514444 * speed * run_s * 0.52
        rotation = max(0.0, min(1.0, (run_s - 38.0) / 5.5))
        rows.append(
            {
                "time": time_s,
                "distance_m": distance + noise.uniform(-0.08, 0.08),
                "ground_speed_kt": speed + noise.uniform(-0.025, 0.025),
                "longitudinal_accel_g": accel + noise.uniform(-0.0007, 0.0007),
                "n1_left_pct": 26.0 + 70.0 * (1.0 - math.exp(-run_s / 7.8)) + noise.uniform(-0.03, 0.03),
                "lateral_accel_g": 0.006 * math.sin(time_s / 3.6) + noise.uniform(-0.0005, 0.0005),
                "nosewheel_angle_deg": 2.0 * math.exp(-run_s / 12.0) * math.sin(time_s / 4.0) + noise.uniform(-0.02, 0.02),
                "bank_deg": 2.8 * math.sin(time_s / 8.0) * min(1.0, run_s / 20.0) + noise.uniform(-0.01, 0.01),
                "roll_rate_dps": 0.35 * math.cos(time_s / 8.0) * min(1.0, run_s / 20.0) + noise.uniform(-0.006, 0.006),
            }
        )
    _write_csv(
        path,
        [
            "time",
            "distance_m",
            "ground_speed_kt",
            "longitudinal_accel_g",
            "n1_left_pct",
            "lateral_accel_g",
            "nosewheel_angle_deg",
            "bank_deg",
            "roll_rate_dps",
        ],
        rows,
    )


def _write_pitch_trim(path: Path) -> None:
    noise = random.Random(200201)
    rows: list[dict[str, float]] = []
    for index in range(ROWS):
        time_s = _time(index)
        slow_wave = math.sin(time_s / 42.0)
        short_wave = math.sin(time_s / 7.5)
        rows.append(
            {
                "time": time_s,
                "altitude_ft": 31000.0 + 18.0 * slow_wave + noise.uniform(-0.35, 0.35),
                "airspeed_kt": 282.0 + 0.9 * math.sin(time_s / 30.0) + noise.uniform(-0.018, 0.018),
                "pitch_deg": 2.25 + 0.12 * slow_wave + 0.025 * short_wave + noise.uniform(-0.004, 0.004),
                "elevator_trim_deg": -1.45 + 0.08 * math.cos(time_s / 48.0) + noise.uniform(-0.003, 0.003),
                "vertical_speed_fpm": 12.0 * math.sin(time_s / 18.0) + noise.uniform(-0.45, 0.45),
                "elevator_deg": -0.32 + 0.05 * math.sin(time_s / 6.0) + noise.uniform(-0.003, 0.003),
                "stick_force_lbf": 4.8 + 0.35 * math.sin(time_s / 12.0) + noise.uniform(-0.015, 0.015),
                "angle_of_attack_deg": 4.4 + 0.11 * math.sin(time_s / 22.0) + noise.uniform(-0.005, 0.005),
            }
        )
    _write_csv(
        path,
        [
            "time",
            "altitude_ft",
            "airspeed_kt",
            "pitch_deg",
            "elevator_trim_deg",
            "vertical_speed_fpm",
            "elevator_deg",
            "stick_force_lbf",
            "angle_of_attack_deg",
        ],
        rows,
    )


def _write_engine(path: Path) -> None:
    noise = random.Random(200301)
    rows: list[dict[str, float]] = []
    for index in range(ROWS):
        time_s = _time(index)
        step_s = max(0.0, time_s - 4.0)
        response = 1.0 - math.exp(-step_s / 5.8)
        gear = min(100.0, max(0.0, (time_s - 120.0) * 14.0))
        rows.append(
            {
                "time": time_s,
                "n1_left_pct": 24.0 + 72.0 * response + noise.uniform(-0.025, 0.025),
                "n1_right_pct": 24.2 + 71.6 * (1.0 - math.exp(-step_s / 6.1)) + noise.uniform(-0.025, 0.025),
                "egt_left_c": 385.0 + 328.0 * response + noise.uniform(-0.16, 0.16),
                "egt_right_c": 386.0 + 325.0 * (1.0 - math.exp(-step_s / 6.0)) + noise.uniform(-0.16, 0.16),
                "fuel_flow_left_pph": 720.0 + 2240.0 * response + noise.uniform(-0.7, 0.7),
                "fuel_flow_right_pph": 725.0 + 2215.0 * (1.0 - math.exp(-step_s / 6.2)) + noise.uniform(-0.7, 0.7),
                "gear_position_pct": gear + noise.uniform(-0.02, 0.02),
                "hydraulic_pressure_psi": 3020.0 - 120.0 * math.sin(min(math.pi, gear / 100.0 * math.pi)) + noise.uniform(-1.5, 1.5),
            }
        )
    _write_csv(
        path,
        [
            "time",
            "n1_left_pct",
            "n1_right_pct",
            "egt_left_c",
            "egt_right_c",
            "fuel_flow_left_pph",
            "fuel_flow_right_pph",
            "gear_position_pct",
            "hydraulic_pressure_psi",
        ],
        rows,
    )


if __name__ == "__main__":
    main()
