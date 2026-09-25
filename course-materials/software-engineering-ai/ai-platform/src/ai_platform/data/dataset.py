import hashlib
import json
from pathlib import Path
import numpy as np
import pandas as pd

FEATURES = ["token_count", "keyword_score", "previous_requests"]
COLUMNS = ["request_id", *FEATURES, "cohort", "label"]
BOUNDS = {"token_count": (1, 1000), "keyword_score": (0, 1), "previous_requests": (0, 50)}


def sha256(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()


def generate(path: Path, seed: int = 42, rows: int = 400) -> dict:
    """Synthetic fixtures, not a model of real people or real fairness."""
    rng = np.random.default_rng(seed)
    frame = pd.DataFrame(
        {
            "request_id": np.arange(rows),
            "token_count": rng.integers(20, 501, rows),
            "keyword_score": np.round(rng.uniform(0, 1, rows), 6),
            "previous_requests": rng.integers(0, 11, rows),
            "cohort": np.where(np.arange(rows) % 2 == 0, "A", "B"),
        }
    )
    score = frame.keyword_score + frame.token_count / 1000 + frame.previous_requests / 50
    frame["label"] = (score > 0.85).astype(int)
    path.parent.mkdir(parents=True, exist_ok=True)
    frame.to_csv(path, index=False, lineterminator="\n")
    manifest = {
        "version": path.stem,
        "seed": seed,
        "rows": rows,
        "sha256": sha256(path),
        "schema": COLUMNS,
        "source": "synthetic; no personal data",
        "license": "CC0-1.0",
    }
    path.with_suffix(".manifest.json").write_text(json.dumps(manifest, indent=2), encoding="utf-8")
    return manifest


def validate(frame: pd.DataFrame) -> None:
    if list(frame.columns) != COLUMNS or len(frame) < 40:
        raise ValueError("Dataset schema or row count invalid")
    if frame.isna().any().any() or frame.request_id.duplicated().any():
        raise ValueError("Missing values or duplicate IDs")
    for name, (low, high) in BOUNDS.items():
        if not pd.api.types.is_numeric_dtype(frame[name]):
            raise ValueError("Non-numeric feature")
        if not np.isfinite(frame[name]).all() or not frame[name].between(low, high).all():
            raise ValueError("Feature outside domain")
    if set(frame.label.unique()) != {0, 1} or not set(frame.cohort.unique()) <= {"A", "B"}:
        raise ValueError("Invalid labels or cohort")
    if frame.label.value_counts().min() < 4:
        raise ValueError("Insufficient class support")


def load(path: Path) -> tuple[pd.DataFrame, dict]:
    manifest = json.loads(path.with_suffix(".manifest.json").read_text(encoding="utf-8"))
    if sha256(path) != manifest["sha256"]:
        raise ValueError("Dataset checksum mismatch")
    frame = pd.read_csv(path)
    validate(frame)
    if manifest["rows"] != len(frame) or manifest["schema"] != COLUMNS:
        raise ValueError("Manifest mismatch")
    return frame, manifest
