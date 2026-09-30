from __future__ import annotations

import sqlite3
from pathlib import Path

import pytest


@pytest.fixture
def fixtures_dir() -> Path:
    return Path(__file__).parent / "fixtures"


@pytest.fixture
def sample_runs_db(tmp_path: Path) -> Path:
    db_path = tmp_path / "runs.sqlite3"
    with sqlite3.connect(db_path) as connection:
        connection.execute(
            "CREATE TABLE runs (id INTEGER PRIMARY KEY, test_id TEXT, trainee TEXT, passed INTEGER, completed_at TEXT)"
        )
        connection.executemany(
            "INSERT INTO runs (test_id, trainee, passed, completed_at) VALUES (?, ?, ?, ?)",
            [
                ("1.b.1", "Taylor North", 1, "2026-01-14T09:15:00Z"),
                ("2.a.1", "Taylor North", 1, "2026-01-15T10:20:00Z"),
                ("3.a.1", "Jordan West", 0, "2026-01-15T11:35:00Z"),
            ],
        )
    return db_path


@pytest.fixture
def viewer_app(monkeypatch: pytest.MonkeyPatch, tmp_path: Path, sample_runs_db: Path):
    from qtg.viewer.app import app

    results_dir = tmp_path / "results"
    results_dir.mkdir()
    (results_dir / "normal-report.html").write_text("<h1>Stored QTG Report</h1>", encoding="utf-8")
    monkeypatch.setenv("QTG_RESULTS_DIR", str(results_dir))
    monkeypatch.setenv("QTG_RUNS_DB", str(sample_runs_db))
    app.config.update(TESTING=True)
    return app
