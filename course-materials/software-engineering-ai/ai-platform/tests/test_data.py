import json
import numpy as np
import pytest
from ai_platform.data.dataset import FEATURES, generate, load, validate
from ai_platform.features.transform import feature_frame


def test_versioned_dataset(data_path):
    frame, manifest = load(data_path)
    assert len(frame) == manifest["rows"] == 400
    assert list(feature_frame(frame).columns) == FEATURES
    assert "label" not in feature_frame(frame) and "cohort" not in feature_frame(frame)


def test_generation_reproducible(tmp_path):
    a, b = tmp_path / "a.csv", tmp_path / "b.csv"
    assert generate(a)["version"] == "a"
    assert generate(b)["version"] == "b"
    assert a.read_bytes() == b.read_bytes()


@pytest.mark.parametrize("change", ["missing", "duplicate", "nan", "range", "label", "text"])
def test_invalid_data_rejected(data_path, change):
    frame, _ = load(data_path)
    if change == "missing":
        frame = frame.drop(columns="token_count")
    elif change == "duplicate":
        frame.loc[1, "request_id"] = frame.loc[0, "request_id"]
    elif change == "nan":
        frame.loc[0, "keyword_score"] = np.nan
    elif change == "range":
        frame.loc[0, "token_count"] = -1
    elif change == "label":
        frame["label"] = 0
    else:
        frame["token_count"] = "text"
    with pytest.raises(ValueError):
        validate(frame)


def test_checksum_catches_changed_data(tmp_path):
    path = tmp_path / "data.csv"
    generate(path)
    path.write_bytes(path.read_bytes() + b"\n")
    with pytest.raises(ValueError, match="checksum"):
        load(path)


def test_manifest_row_contract(tmp_path):
    path = tmp_path / "data.csv"
    generate(path)
    manifest_path = path.with_suffix(".manifest.json")
    manifest = json.loads(manifest_path.read_text())
    manifest["rows"] = 1
    manifest_path.write_text(json.dumps(manifest))
    with pytest.raises(ValueError, match="Manifest"):
        load(path)
