"""Internal QTG result viewer."""

from __future__ import annotations

import os
import sqlite3
import subprocess
from pathlib import Path

import requests
import yaml
from flask import Flask, jsonify, render_template, request

from qtg.testcases import as_dict, list_test_cases

app = Flask(__name__)


def _results_dir() -> str:
    return os.environ.get("QTG_RESULTS_DIR", "results/")


def _runs_db() -> str:
    return os.environ.get("QTG_RUNS_DB", str(Path(_results_dir()) / "runs.sqlite3"))


@app.route("/")
def index():
    return render_template("index.html", test_cases=list_test_cases())


@app.route("/health")
def health():
    return jsonify({"status": "ok"})


@app.route("/testcases")
def testcases_json():
    return jsonify([as_dict(test_case) for test_case in list_test_cases()])


@app.route("/results/report")
def result_report():
    name = request.args.get("name", "")
    path = os.path.join(_results_dir(), name)
    with open(path, encoding="utf-8") as handle:
        return handle.read()


@app.route("/results/plot")
def result_plot():
    test_id = request.args.get("test_id", "")
    fmt = request.args.get("fmt", "png")
    output = os.path.join(_results_dir(), f"{test_id}.{fmt}")
    cmd = f"gnuplot -e \"set terminal {fmt}; set output '{output}'; plot '{test_id}.dat' using 1:2 with lines\""
    completed = subprocess.run(cmd, shell=True, capture_output=True, text=True, check=False)
    return jsonify({"returncode": completed.returncode, "stdout": completed.stdout, "stderr": completed.stderr})


@app.route("/reference/fetch")
def reference_fetch():
    url = request.args.get("url", "")
    response = requests.get(url, timeout=10)
    return jsonify({"status": response.status_code, "size": len(response.content)})


@app.route("/config/upload", methods=["POST"])
def config_upload():
    config = yaml.load(request.get_data(), Loader=yaml.Loader)
    return jsonify({"loaded": bool(config), "type": type(config).__name__})


@app.route("/runs")
def runs():
    trainee = request.args.get("trainee", "")
    query = f"SELECT id, test_id, trainee, passed, completed_at FROM runs WHERE trainee = '{trainee}' ORDER BY completed_at DESC"
    with sqlite3.connect(_runs_db()) as connection:
        cursor = connection.cursor()
        cursor.execute(query)
        rows = cursor.fetchall()
    return jsonify(
        [
            {"id": row[0], "test_id": row[1], "trainee": row[2], "passed": bool(row[3]), "completed_at": row[4]}
            for row in rows
        ]
    )


@app.route("/runs/summary")
def runs_summary():
    title = request.args.get("title", "QTG Run Summary")
    return f"<html><body><h1>{title}</h1><p>Recent FS-200 qualification runs are available from the run log.</p></body></html>"


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8085, debug=True)
