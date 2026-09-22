import json
import os
import re
import tempfile
from dataclasses import dataclass
from pathlib import Path
from typing import Any, Protocol
import joblib
import sklearn
from ai_platform.data.dataset import FEATURES, sha256


@dataclass(frozen=True)
class Bundle:
    model: Any
    metadata: dict


class ModelRepository(Protocol):
    def load(self, version: str) -> Bundle: ...


def validate_version(version: str) -> None:
    if not re.fullmatch(r"[A-Za-z0-9][A-Za-z0-9._-]{0,63}", version):
        raise ValueError("Invalid version")


class FileRepository:
    """Trusted local artifacts only. SHA-256 detects corruption, not authenticity.

    This is a single-writer teaching registry, not a multi-tenant registry.
    Never accept uploaded/untrusted pickle/joblib files.
    """

    def __init__(self, root: Path):
        self.root = root

    def load(self, version: str) -> Bundle:
        validate_version(version)
        folder = self.root / version
        metadata = json.loads((folder / "metadata.json").read_text(encoding="utf-8"))
        if metadata["version"] != version or metadata["features"] != FEATURES:
            raise ValueError("Artifact contract mismatch")
        if metadata["sklearn_version"] != sklearn.__version__:
            raise ValueError("Incompatible sklearn runtime")
        if sha256(folder / "model.joblib") != metadata["model_sha256"]:
            raise ValueError("Artifact checksum mismatch")
        return Bundle(joblib.load(folder / "model.joblib"), metadata)

    def save(self, version: str, model: Any, metadata: dict) -> Path:
        validate_version(version)
        self.root.mkdir(parents=True, exist_ok=True)
        target = self.root / version
        if target.exists():
            raise FileExistsError("Model versions are immutable")
        folder = Path(tempfile.mkdtemp(prefix=".pending-", dir=self.root))
        joblib.dump(model, folder / "model.joblib")
        full = {
            **metadata,
            "version": version,
            "features": FEATURES,
            "sklearn_version": sklearn.__version__,
            "model_sha256": sha256(folder / "model.joblib"),
        }
        (folder / "metadata.json").write_text(json.dumps(full, indent=2), encoding="utf-8")
        # Target must not exist; a failed interrupted staging directory is not a release.
        folder.rename(target)
        return target

    def promote(self, version: str, minimum_f1: float = 0.85) -> None:
        bundle = self.load(version)
        if bundle.metadata["metrics"]["f1"] < minimum_f1:
            raise ValueError("Model quality gate failed")
        current = self.root / "champion.json"
        previous = json.loads(current.read_text())["version"] if current.exists() else None
        self._pointer({"version": version, "previous": previous})

    def rollback(self) -> str:
        pointer = json.loads((self.root / "champion.json").read_text())
        previous = pointer["previous"]
        if not previous:
            raise ValueError("No previous champion")
        self.load(previous)
        self._pointer({"version": previous, "previous": pointer["version"]})
        return previous

    def _pointer(self, pointer: dict) -> None:
        path = self.root / ".champion.tmp"
        path.write_text(json.dumps(pointer), encoding="utf-8")
        os.replace(path, self.root / "champion.json")
