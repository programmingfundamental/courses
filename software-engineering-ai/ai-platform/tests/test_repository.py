import json
import shutil
import pytest
from ai_platform.inference.repository import FileRepository, validate_version


@pytest.mark.parametrize("version", ["../evil", "", "/absolute", "a/b", ".", ".."])
def test_bad_version(version):
    with pytest.raises(ValueError):
        validate_version(version)


def test_immutable_version(trained):
    root, metadata = trained
    repo = FileRepository(root)
    bundle = repo.load("test-v1")
    with pytest.raises(FileExistsError):
        repo.save("test-v1", bundle.model, metadata)


@pytest.mark.parametrize("damage", ["model", "runtime", "schema", "version"])
def test_artifact_integrity(trained, tmp_path, damage):
    root, _ = trained
    shutil.copytree(root / "test-v1", tmp_path / "test-v1")
    folder = tmp_path / "test-v1"
    if damage == "model":
        (folder / "model.joblib").write_bytes(b"not a model")
    else:
        metadata = json.loads((folder / "metadata.json").read_text())
        key = {"runtime": "sklearn_version", "schema": "features", "version": "version"}[damage]
        metadata[key] = "bad"
        (folder / "metadata.json").write_text(json.dumps(metadata))
    with pytest.raises(ValueError):
        FileRepository(tmp_path).load("test-v1")


def test_promotion_rollback_and_quality_gate(trained, tmp_path):
    root, _ = trained
    original = FileRepository(root).load("test-v1")
    repo = FileRepository(tmp_path)
    repo.save("v1", original.model, original.metadata)
    repo.save("v2", original.model, original.metadata)
    repo.save("bad", original.model, {**original.metadata, "metrics": {"f1": 0.1}})
    with pytest.raises(ValueError, match="gate"):
        repo.promote("bad")
    repo.promote("v1")
    with pytest.raises(ValueError, match="previous"):
        repo.rollback()
    repo.promote("v2")
    assert repo.rollback() == "v1"
    assert json.loads((tmp_path / "champion.json").read_text())["version"] == "v1"
