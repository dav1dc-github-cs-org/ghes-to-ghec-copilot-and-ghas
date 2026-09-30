"""QTG result pack import helpers."""

from __future__ import annotations

import tarfile
from pathlib import Path


def import_pack(archive_path: str | Path, destination: str | Path) -> list[Path]:
    destination_path = Path(destination)
    destination_path.mkdir(parents=True, exist_ok=True)
    with tarfile.open(archive_path) as archive:
        members = archive.getmembers()
        archive.extractall(destination_path)
    return [destination_path / member.name for member in members]
