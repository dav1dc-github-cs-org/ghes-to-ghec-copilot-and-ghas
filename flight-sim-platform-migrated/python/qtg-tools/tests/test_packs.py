from __future__ import annotations

import tarfile
from pathlib import Path

from qtg.packs import import_pack


def test_import_pack_extracts_benign_results_pack(tmp_path: Path) -> None:
    source_dir = tmp_path / "source"
    source_dir.mkdir()
    result_file = source_dir / "summary.json"
    result_file.write_text('{"test_id":"1.b.1","passed":true}', encoding="utf-8")
    archive_path = tmp_path / "pack.tar.gz"
    with tarfile.open(archive_path, "w:gz") as archive:
        archive.add(result_file, arcname="summary.json")

    destination = tmp_path / "dest"
    imported = import_pack(archive_path, destination)

    assert imported == [destination / "summary.json"]
    assert (destination / "summary.json").read_text(encoding="utf-8") == '{"test_id":"1.b.1","passed":true}'
