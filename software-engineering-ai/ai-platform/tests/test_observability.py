import pytest
from ai_platform.data.dataset import load
from ai_platform.observability import Metrics, drift_score


def test_metrics_bounded_and_error_count():
    metrics = Metrics()
    assert metrics.snapshot("v1")["prediction_latency_ms_p95"] is None
    for _ in range(1005):
        metrics.record(0.002, 200, True)
    metrics.record(0.01, 503, True)
    result = metrics.snapshot("v1")
    assert result["request_count"] == 1006
    assert result["error_count"] == 1
    assert result["prediction_latency_samples"] == 1000
    assert result["prediction_latency_ms_p95"] == pytest.approx(2.0)


def test_drift_smoke(trained, data_path):
    _, metadata = trained
    frame, _ = load(data_path)
    baseline = metadata["feature_baseline"]
    assert drift_score(frame, baseline) < 0.5
    shifted = frame.copy()
    shifted["token_count"] += 500
    assert drift_score(shifted, baseline) > 2.0
