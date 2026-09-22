import json
import platform
import subprocess
from datetime import datetime, timezone
from pathlib import Path
from sklearn.metrics import accuracy_score, f1_score, precision_score, recall_score
from sklearn.model_selection import train_test_split
from ai_platform.data.dataset import load, sha256
from ai_platform.features.transform import feature_frame
from ai_platform.inference.repository import FileRepository
from ai_platform.training.strategies import build_model


def git_revision() -> str:
    try:
        revision = subprocess.check_output(
            ["git", "rev-parse", "HEAD"], text=True, stderr=subprocess.DEVNULL
        ).strip()
        dirty = subprocess.check_output(
            ["git", "status", "--porcelain"], text=True, stderr=subprocess.DEVNULL
        ).strip()
        return revision + ("-dirty" if dirty else "")
    except (subprocess.SubprocessError, FileNotFoundError):
        return "unavailable"  # Explicit, never a fabricated commit identifier.


def train(
    dataset: Path, root: Path, version: str, strategy: str = "linear", seed: int = 42
) -> dict:
    frame, manifest = load(dataset)
    train_rows, test_rows = train_test_split(
        frame, test_size=0.25, stratify=frame.label, random_state=seed
    )
    model = build_model(strategy, seed)
    # fit preprocessing ONLY on training split; test set never participates in fit.
    model.fit(feature_frame(train_rows), train_rows.label)
    predicted = model.predict(feature_frame(test_rows))
    metrics = {
        "accuracy": float(accuracy_score(test_rows.label, predicted)),
        "f1": float(f1_score(test_rows.label, predicted)),
        "precision": float(precision_score(test_rows.label, predicted, zero_division=0)),
        "recall": float(recall_score(test_rows.label, predicted, zero_division=0)),
    }
    cohorts = {}
    for name in ("A", "B"):
        mask = test_rows.cohort.to_numpy() == name
        cohorts[name] = {
            "n": int(mask.sum()),
            "positive_n": int(test_rows.label.to_numpy()[mask].sum()),
            "recall": float(
                recall_score(test_rows.label.to_numpy()[mask], predicted[mask], zero_division=0)
            ),
        }
    source_root = Path(__file__).parents[1]
    source_hash = (
        __import__("hashlib")
        .sha256(
            b"".join(
                p.relative_to(source_root).as_posix().encode() + p.read_bytes()
                for p in sorted(source_root.rglob("*.py"))
            )
        )
        .hexdigest()
    )
    lock = Path("requirements.lock")
    metadata = {
        "parameters": {"strategy": strategy, "seed": seed, "test_fraction": 0.25},
        "metrics": metrics,
        "cohort_metrics": cohorts,
        "dataset": manifest,
        "git_commit": git_revision(),
        "source_sha256": source_hash,
        "dependency_lock_sha256": sha256(lock) if lock.exists() else "unavailable",
        "python_version": platform.python_version(),
        "created_at": datetime.now(timezone.utc).isoformat(),
        "test_ids": sorted(test_rows.request_id.tolist()),
        "feature_baseline": {
            c: {"mean": float(train_rows[c].mean()), "std": float(train_rows[c].std())}
            for c in feature_frame(frame).columns
        },
    }
    folder = FileRepository(root).save(version, model, metadata)
    # Immutable metadata.json is the local experiment journal: parameters, metrics,
    # dataset identity, code identity, dependency identity, artifact and split IDs.
    return json.loads((folder / "metadata.json").read_text(encoding="utf-8"))
