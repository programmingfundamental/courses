import numpy as np
import pytest
from ai_platform.data.dataset import load
from ai_platform.features.transform import feature_frame
from ai_platform.inference.repository import FileRepository
from ai_platform.training.pipeline import train
from ai_platform.training.strategies import build_model


@pytest.mark.integration
def test_quality_lineage_and_reproducibility(trained, data_path, tmp_path):
    root, first = trained
    second = train(data_path, tmp_path, "repeat-v1")
    assert first["metrics"]["f1"] >= 0.85
    assert first["metrics"]["recall"] >= 0.85
    assert first["dataset"]["sha256"] == second["dataset"]["sha256"]
    assert first["test_ids"] == second["test_ids"]
    assert first["metrics"] == second["metrics"]
    assert first["source_sha256"] != "unavailable"
    frame, _ = load(data_path)
    a = FileRepository(root).load("test-v1").model.predict_proba(feature_frame(frame))
    b = FileRepository(tmp_path).load("repeat-v1").model.predict_proba(feature_frame(frame))
    np.testing.assert_allclose(a, b, atol=1e-12)


def test_preprocessing_fitted_only_on_train(trained, data_path):
    root, metadata = trained
    frame, _ = load(data_path)
    training_rows = frame[~frame.request_id.isin(metadata["test_ids"])]
    scaler = FileRepository(root).load("test-v1").model.named_steps["scale"]
    np.testing.assert_allclose(scaler.mean_, feature_frame(training_rows).mean().to_numpy())


@pytest.mark.parametrize("strategy", ["linear", "tree", "forest"])
def test_strategy_contract(strategy, data_path):
    frame, _ = load(data_path)
    model = build_model(strategy, 42).fit(feature_frame(frame), frame.label)
    result = model.predict_proba(feature_frame(frame.head(3)))
    assert result.shape == (3, 2)
    assert ((result >= 0) & (result <= 1)).all()


def test_unknown_strategy():
    with pytest.raises(ValueError, match="Unknown"):
        build_model("not-registered", 42)
