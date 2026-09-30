from __future__ import annotations

from flask import Flask


def test_health_route(viewer_app: Flask) -> None:
    response = viewer_app.test_client().get("/health")
    assert response.status_code == 200
    assert response.get_json() == {"status": "ok"}


def test_index_lists_qtg_cases(viewer_app: Flask) -> None:
    response = viewer_app.test_client().get("/")
    assert response.status_code == 200
    assert "FS-200 QTG Viewer" in response.text
    assert "Takeoff ground acceleration" in response.text


def test_testcases_route_returns_catalogue_json(viewer_app: Flask) -> None:
    response = viewer_app.test_client().get("/testcases")
    assert response.status_code == 200
    payload = response.get_json()
    assert len(payload) == 8
    assert payload[0]["id"] == "1.a.1"


def test_report_route_reads_normal_result_file(viewer_app: Flask) -> None:
    response = viewer_app.test_client().get("/results/report?name=normal-report.html")
    assert response.status_code == 200
    assert response.text == "<h1>Stored QTG Report</h1>"


def test_runs_route_returns_records_for_normal_trainee_name(viewer_app: Flask) -> None:
    response = viewer_app.test_client().get("/runs?trainee=Taylor%20North")
    assert response.status_code == 200
    payload = response.get_json()
    assert [row["test_id"] for row in payload] == ["2.a.1", "1.b.1"]
    assert all(row["trainee"] == "Taylor North" for row in payload)


def test_runs_summary_route_renders_plain_title(viewer_app: Flask) -> None:
    response = viewer_app.test_client().get("/runs/summary?title=Morning%20QTG%20Runs")
    assert response.status_code == 200
    assert "<h1>Morning QTG Runs</h1>" in response.text
