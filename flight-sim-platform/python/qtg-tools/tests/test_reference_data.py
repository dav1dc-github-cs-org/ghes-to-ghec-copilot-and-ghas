from __future__ import annotations

import csv
from pathlib import Path

import pytest

from qtg.compare import compare_files
from qtg.testcases import get_test_case

POINTER_MESSAGE = (
    "Reference data is a Git LFS pointer, not data. Run `git lfs pull` "
    "(or enable LFS in the checkout) and re-run the tests."
)

REFERENCE_CASES = [
    ("1.b.1", "1b1_takeoff_acceleration.csv", "ground_speed_kt"),
    ("2.a.1", "2a1_pitch_trim_cruise.csv", "pitch_deg"),
    ("3.a.1", "3a1_engine_acceleration.csv", "n1_left_pct"),
]


@pytest.mark.parametrize(("test_id", "filename", "out_channel"), REFERENCE_CASES)
def test_reference_csv_is_materialized_data(test_id: str, filename: str, out_channel: str) -> None:
    del test_id, out_channel
    path = Path("reference-data") / filename
    assert path.exists()
    prefix = path.read_text(encoding="utf-8", errors="ignore")[:128]
    assert not prefix.startswith("version https://git-lfs.github.com/spec/v1"), POINTER_MESSAGE


@pytest.mark.parametrize(("test_id", "filename", "out_channel"), REFERENCE_CASES)
def test_reference_csv_header_and_row_count(test_id: str, filename: str, out_channel: str) -> None:
    del test_id, out_channel
    path = Path("reference-data") / filename
    _assert_not_lfs_pointer(path)
    with path.open(newline="", encoding="utf-8") as handle:
        reader = csv.reader(handle)
        header = next(reader)
        rows = sum(1 for _ in reader)
    assert header[0] == "time"
    assert len(header) >= 7
    assert rows >= 10000


@pytest.mark.parametrize(("test_id", "filename", "out_channel"), REFERENCE_CASES)
def test_in_tolerance_candidate_derived_from_reference_passes(
    tmp_path: Path, test_id: str, filename: str, out_channel: str
) -> None:
    reference_path = Path("reference-data") / filename
    _assert_not_lfs_pointer(reference_path)
    candidate = tmp_path / f"{filename}.candidate.csv"
    _write_candidate(reference_path, candidate, perturb_channel=out_channel, perturb=0.001)
    result = compare_files(get_test_case(test_id), candidate)
    assert result.passed


@pytest.mark.parametrize(("test_id", "filename", "out_channel"), REFERENCE_CASES)
def test_out_of_tolerance_candidate_derived_from_reference_fails(
    tmp_path: Path, test_id: str, filename: str, out_channel: str
) -> None:
    reference_path = Path("reference-data") / filename
    _assert_not_lfs_pointer(reference_path)
    candidate = tmp_path / f"{filename}.candidate.csv"
    _write_candidate(reference_path, candidate, perturb_channel=out_channel, perturb=500.0)
    result = compare_files(get_test_case(test_id), candidate)
    assert not result.passed
    assert not next(channel for channel in result.channels if channel.name == out_channel).passed


def _assert_not_lfs_pointer(path: Path) -> None:
    prefix = path.read_text(encoding="utf-8", errors="ignore")[:128]
    assert not prefix.startswith("version https://git-lfs.github.com/spec/v1"), POINTER_MESSAGE


def _write_candidate(reference_path: Path, candidate_path: Path, perturb_channel: str, perturb: float) -> None:
    with reference_path.open(newline="", encoding="utf-8") as source, candidate_path.open(
        "w", newline="", encoding="utf-8"
    ) as target:
        reader = csv.DictReader(source)
        assert reader.fieldnames is not None
        writer = csv.DictWriter(target, fieldnames=reader.fieldnames)
        writer.writeheader()
        for row in reader:
            row[perturb_channel] = f"{float(row[perturb_channel]) + perturb:.6f}"
            writer.writerow(row)
