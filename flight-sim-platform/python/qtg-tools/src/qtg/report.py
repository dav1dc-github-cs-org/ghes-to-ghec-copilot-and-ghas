"""HTML report rendering for QTG comparison results."""

from __future__ import annotations

from pathlib import Path

from jinja2 import Environment, FileSystemLoader, select_autoescape

from .compare import ComparisonResult

TEMPLATE_DIR = Path(__file__).resolve().parent / "viewer" / "templates"


def render_html(result: ComparisonResult) -> str:
    environment = Environment(
        loader=FileSystemLoader(TEMPLATE_DIR),
        autoescape=select_autoescape(enabled_extensions=("html", "xml"), default_for_string=True),
    )
    template = environment.get_template("report.html")
    return template.render(result=result)


def write_html_report(result: ComparisonResult, output_path: str | Path) -> Path:
    path = Path(output_path)
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(render_html(result), encoding="utf-8")
    return path
