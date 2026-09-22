import logging
import pytest
from fastapi.testclient import TestClient
from pydantic import SecretStr
from ai_platform.api.app import create_app
from ai_platform.config import Settings

ROW = {"token_count": 140, "keyword_score": 0.8, "previous_requests": 2}
KEY = "synthetic-test-key-only"


@pytest.fixture
def client(trained):
    root, _ = trained
    settings = Settings(artifact_root=root, model_version="test-v1", api_key=SecretStr(KEY))
    with TestClient(create_app(settings)) as connection:
        yield connection


@pytest.mark.integration
def test_prediction_schema_and_version(client):
    response = client.post("/predict", headers={"X-API-Key": KEY}, json={"instances": [ROW]})
    assert response.status_code == 200
    prediction = response.json()[0]
    assert set(prediction) == {"label", "probability", "model_version"}
    assert prediction["label"] in [0, 1]
    assert 0 <= prediction["probability"] <= 1
    assert prediction["model_version"] == "test-v1"
    assert response.headers["X-Correlation-ID"]


@pytest.mark.parametrize(
    "body",
    [
        {"instances": []},
        {"instances": [{"keyword_score": 0.2}]},
        {"instances": [{**ROW, "token_count": -1}]},
        {"instances": [{**ROW, "secret": "NEVER-ECHO-THIS"}]},
        {"instances": [{**ROW, "keyword_score": "NaN"}]},
        {"instances": [ROW], "unexpected": 1},
    ],
)
def test_invalid_input(client, body):
    response = client.post("/predict", headers={"X-API-Key": KEY}, json=body)
    assert response.status_code == 422
    assert "NEVER-ECHO-THIS" not in response.text


def test_auth_batch_limit_and_metrics(client):
    assert client.post("/predict", json={"instances": [ROW]}).status_code == 401
    assert client.get("/metrics").status_code == 401
    assert (
        client.post(
            "/predict", headers={"X-API-Key": KEY}, json={"instances": [ROW] * 33}
        ).status_code
        == 413
    )
    metrics = client.get("/metrics", headers={"X-API-Key": KEY}).json()
    assert metrics["request_count"] >= 3 and metrics["error_count"] >= 3
    assert metrics["model_version"] == "test-v1"
    assert metrics["prediction_latency_ms_p95"] is not None


def test_missing_model_is_unready_not_dead(tmp_path):
    config = Settings(artifact_root=tmp_path, model_version="missing", api_key=SecretStr(KEY))
    with TestClient(create_app(config)) as client:
        assert client.get("/health/live").status_code == 200
        assert client.get("/health/ready").status_code == 503
        assert (
            client.post(
                "/predict", headers={"X-API-Key": KEY}, json={"instances": [ROW]}
            ).status_code
            == 503
        )


def test_missing_secret_fails_closed(trained):
    root, _ = trained
    with TestClient(create_app(Settings(artifact_root=root, model_version="test-v1"))) as client:
        assert client.get("/health/ready").status_code == 503
        assert client.post("/predict", json={"instances": [ROW]}).status_code == 401


def test_privacy_safe_logs(client, caplog):
    caplog.set_level(logging.INFO, logger="ai_platform.events")
    response = client.post(
        "/predict",
        headers={"X-API-Key": "PRIVATE-CREDENTIAL"},
        json={"sensitive": "PRIVATE-PAYLOAD"},
    )
    assert response.status_code == 401
    assert "request_complete" in caplog.text
    assert "PRIVATE-CREDENTIAL" not in caplog.text and "PRIVATE-PAYLOAD" not in caplog.text
    assert client.get("/health/ready").status_code == 200
