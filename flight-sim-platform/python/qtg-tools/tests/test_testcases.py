from __future__ import annotations

import pytest

from qtg.testcases import as_dict, get_test_case, list_test_cases


def test_catalogue_contains_required_qtg_cases() -> None:
    ids = [test_case.id for test_case in list_test_cases()]
    assert ids == ["1.a.1", "1.b.1", "2.a.1", "2.c.1", "2.d.1", "3.a.1", "4.a.1", "5.a.1"]


def test_each_case_has_reference_and_channels() -> None:
    for test_case in list_test_cases():
        assert test_case.reference_csv.endswith(".csv")
        assert len(test_case.channels) >= 4
        assert all(channel.name for channel in test_case.channels)
        assert all(channel.unit for channel in test_case.channels)


def test_get_test_case_rejects_unknown_id() -> None:
    with pytest.raises(KeyError, match="Unknown QTG test case"):
        get_test_case("9.z.9")


def test_as_dict_is_json_ready() -> None:
    data = as_dict(get_test_case("3.a.1"))
    assert data["id"] == "3.a.1"
    assert data["reference_csv"] == "3a1_engine_acceleration.csv"
    assert isinstance(data["channels"], list)
    assert {"name", "unit", "tolerance", "tolerance_kind"} <= set(data["channels"][0])
