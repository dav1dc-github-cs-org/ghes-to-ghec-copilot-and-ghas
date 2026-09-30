"""QTG test case catalogue for the FS-200 simulator."""

from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path

from .tolerances import Tolerance, absolute, percent

REFERENCE_DATA_DIR = Path(__file__).resolve().parents[2] / "reference-data"


@dataclass(frozen=True)
class ChannelSpec:
    name: str
    unit: str
    tolerance: Tolerance


@dataclass(frozen=True)
class TestCase:
    id: str
    title: str
    objective: str
    reference_csv: str
    channels: tuple[ChannelSpec, ...]

    @property
    def reference_path(self) -> Path:
        return REFERENCE_DATA_DIR / self.reference_csv


_CATALOGUE: tuple[TestCase, ...] = (
    TestCase(
        id="1.a.1",
        title="Minimum radius ground turn",
        objective="Verify low-speed steering response and turning radius on a dry runway.",
        reference_csv="1b1_takeoff_acceleration.csv",
        channels=(
            ChannelSpec("ground_speed_kt", "kt", absolute(1.8)),
            ChannelSpec("bank_deg", "deg", absolute(1.0)),
            ChannelSpec("lateral_accel_g", "g", absolute(0.025)),
            ChannelSpec("nosewheel_angle_deg", "deg", absolute(1.0)),
        ),
    ),
    TestCase(
        id="1.b.1",
        title="Takeoff ground acceleration time and distance",
        objective="Compare takeoff acceleration, distance, and engine response through rotation.",
        reference_csv="1b1_takeoff_acceleration.csv",
        channels=(
            ChannelSpec("distance_m", "m", percent(2.0)),
            ChannelSpec("ground_speed_kt", "kt", absolute(2.0)),
            ChannelSpec("longitudinal_accel_g", "g", absolute(0.025)),
            ChannelSpec("n1_left_pct", "%", absolute(1.2)),
        ),
    ),
    TestCase(
        id="2.a.1",
        title="Pitch trim at cruise",
        objective="Verify elevator trim and pitch attitude in stable cruise.",
        reference_csv="2a1_pitch_trim_cruise.csv",
        channels=(
            ChannelSpec("altitude_ft", "ft", absolute(35.0)),
            ChannelSpec("airspeed_kt", "kt", absolute(1.5)),
            ChannelSpec("pitch_deg", "deg", absolute(0.35)),
            ChannelSpec("elevator_trim_deg", "deg", absolute(0.20)),
        ),
    ),
    TestCase(
        id="2.c.1",
        title="Longitudinal static stability",
        objective="Confirm stick force and speed trend following pitch displacement.",
        reference_csv="2a1_pitch_trim_cruise.csv",
        channels=(
            ChannelSpec("airspeed_kt", "kt", absolute(2.0)),
            ChannelSpec("pitch_deg", "deg", absolute(0.5)),
            ChannelSpec("elevator_deg", "deg", absolute(0.4)),
            ChannelSpec("stick_force_lbf", "lbf", absolute(1.5)),
        ),
    ),
    TestCase(
        id="2.d.1",
        title="Stall characteristics",
        objective="Assess stall warning onset and recovery cues in clean configuration.",
        reference_csv="2a1_pitch_trim_cruise.csv",
        channels=(
            ChannelSpec("airspeed_kt", "kt", absolute(3.0)),
            ChannelSpec("angle_of_attack_deg", "deg", absolute(0.6)),
            ChannelSpec("pitch_deg", "deg", absolute(0.8)),
            ChannelSpec("vertical_speed_fpm", "ft/min", absolute(90.0)),
        ),
    ),
    TestCase(
        id="3.a.1",
        title="Engine acceleration",
        objective="Compare thrust response from flight idle to takeoff setting.",
        reference_csv="3a1_engine_acceleration.csv",
        channels=(
            ChannelSpec("n1_left_pct", "%", absolute(1.0)),
            ChannelSpec("n1_right_pct", "%", absolute(1.0)),
            ChannelSpec("egt_left_c", "C", absolute(12.0)),
            ChannelSpec("egt_right_c", "C", absolute(12.0)),
            ChannelSpec("fuel_flow_left_pph", "lb/h", percent(3.0)),
            ChannelSpec("fuel_flow_right_pph", "lb/h", percent(3.0)),
        ),
    ),
    TestCase(
        id="4.a.1",
        title="Gear extension time",
        objective="Validate landing gear extension timing and transient drag effects.",
        reference_csv="3a1_engine_acceleration.csv",
        channels=(
            ChannelSpec("gear_position_pct", "%", absolute(2.0)),
            ChannelSpec("hydraulic_pressure_psi", "psi", absolute(90.0)),
            ChannelSpec("n1_left_pct", "%", absolute(1.5)),
            ChannelSpec("fuel_flow_left_pph", "lb/h", percent(3.5)),
        ),
    ),
    TestCase(
        id="5.a.1",
        title="Roll response",
        objective="Verify roll rate and bank angle response to aileron input.",
        reference_csv="3a1_engine_acceleration.csv",
        channels=(
            ChannelSpec("bank_deg", "deg", absolute(1.0)),
            ChannelSpec("roll_rate_dps", "deg/s", absolute(1.3)),
            ChannelSpec("nosewheel_angle_deg", "deg", absolute(1.0)),
            ChannelSpec("lateral_accel_g", "g", absolute(0.025)),
        ),
    ),
)


def list_test_cases() -> tuple[TestCase, ...]:
    return _CATALOGUE


def get_test_case(test_id: str) -> TestCase:
    for test_case in _CATALOGUE:
        if test_case.id == test_id:
            return test_case
    raise KeyError(f"Unknown QTG test case: {test_id}")


def as_dict(test_case: TestCase) -> dict[str, object]:
    return {
        "id": test_case.id,
        "title": test_case.title,
        "objective": test_case.objective,
        "reference_csv": test_case.reference_csv,
        "channels": [
            {
                "name": channel.name,
                "unit": channel.unit,
                "tolerance": channel.tolerance.label,
                "tolerance_kind": channel.tolerance.kind,
            }
            for channel in test_case.channels
        ],
    }
