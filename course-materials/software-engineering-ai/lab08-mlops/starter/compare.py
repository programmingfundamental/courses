"""Compare real local immutable run metadata. No automatic promotion by accuracy alone."""
import argparse
import json
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument("versions", nargs=2)
args = parser.parse_args()
for version in args.versions:
    metadata = json.loads((Path("artifacts") / version / "metadata.json").read_text())
    print(json.dumps({k: metadata[k] for k in ("version", "metrics", "cohort_metrics", "dataset")}, indent=2))
# TODO: compare dataset/split, artifact size, measured latency and rollback readiness.
