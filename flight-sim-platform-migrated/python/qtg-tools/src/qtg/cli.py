"""Command line interface for qtg-tools."""

from __future__ import annotations

import argparse
import json
from pathlib import Path

from .compare import compare_files
from .packs import import_pack
from .report import write_html_report
from .testcases import as_dict, get_test_case, list_test_cases


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(prog="qtg")
    subparsers = parser.add_subparsers(dest="command", required=True)

    list_parser = subparsers.add_parser("list", help="List QTG test cases")
    list_parser.add_argument("--json", action="store_true", help="Emit JSON")

    compare_parser = subparsers.add_parser("compare", help="Compare simulator output to reference data")
    compare_parser.add_argument("test_id")
    compare_parser.add_argument("candidate_csv")
    compare_parser.add_argument("--report", help="Write an HTML report")

    viewer_parser = subparsers.add_parser("viewer", help="Start the internal results viewer")
    viewer_parser.add_argument("--host", default="127.0.0.1")
    viewer_parser.add_argument("--port", type=int, default=8085)
    viewer_parser.add_argument("--debug", action="store_true")

    pack_parser = subparsers.add_parser("import-pack", help="Import a QTG results pack")
    pack_parser.add_argument("archive")
    pack_parser.add_argument("--dest", required=True)

    args = parser.parse_args(argv)
    if args.command == "list":
        cases = [as_dict(test_case) for test_case in list_test_cases()]
        if args.json:
            print(json.dumps(cases, indent=2))
        else:
            for test_case in list_test_cases():
                print(f"{test_case.id}\t{test_case.title}")
        return 0
    if args.command == "compare":
        result = compare_files(get_test_case(args.test_id), args.candidate_csv)
        if args.report:
            write_html_report(result, args.report)
        for channel in result.channels:
            state = "PASS" if channel.passed else "FAIL"
            print(
                f"{state} {channel.name}: max error {channel.max_abs_error:.6g} "
                f"{channel.unit} at t={channel.time_at_max_error:.2f}s "
                f"(limit {channel.limit_at_max_error:.6g}, tolerance {channel.tolerance})"
            )
        return 0 if result.passed else 1
    if args.command == "viewer":
        from .viewer.app import app

        app.run(host=args.host, port=args.port, debug=args.debug)
        return 0
    if args.command == "import-pack":
        imported = import_pack(args.archive, args.dest)
        for path in imported:
            print(Path(path))
        return 0
    parser.error(f"Unknown command: {args.command}")
    return 2


if __name__ == "__main__":
    raise SystemExit(main())
