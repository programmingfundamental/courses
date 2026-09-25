import json
import logging
import secrets
import time
import uuid
from contextlib import asynccontextmanager
from typing import Annotated
from fastapi import Depends, FastAPI, Header, HTTPException, Request
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse
from pydantic import BaseModel, ConfigDict, Field
from ai_platform.config import Settings
from ai_platform.inference.repository import FileRepository, ModelRepository
from ai_platform.inference.service import Predictor
from ai_platform.observability import Metrics

logger = logging.getLogger("ai_platform.events")
logger.setLevel(logging.INFO)
if not logger.handlers:
    event_handler = logging.StreamHandler()
    event_handler.setFormatter(logging.Formatter("%(message)s"))
    logger.addHandler(event_handler)


class Features(BaseModel):
    model_config = ConfigDict(extra="forbid", allow_inf_nan=False, strict=True)
    token_count: int = Field(ge=1, le=1000)
    keyword_score: float = Field(ge=0, le=1)
    previous_requests: int = Field(ge=0, le=50)


class PredictionRequest(BaseModel):
    model_config = ConfigDict(extra="forbid")
    instances: list[Features] = Field(min_length=1, max_length=128)


class Prediction(BaseModel):
    label: int
    probability: float
    model_version: str


def create_app(
    settings: Settings | None = None, repository: ModelRepository | None = None
) -> FastAPI:
    cfg = settings or Settings()
    repo = repository or FileRepository(cfg.artifact_root)
    metrics = Metrics()

    @asynccontextmanager
    async def lifespan(app: FastAPI):
        app.state.predictor = None
        try:
            app.state.predictor = Predictor(repo, cfg.model_version)
        except Exception:
            logger.error(
                json.dumps({"event": "model_load_failed", "model_version": cfg.model_version})
            )
        yield
        app.state.predictor = None

    app = FastAPI(title="AI Prediction Platform", version="0.1.0", lifespan=lifespan)

    def authorized(x_api_key: Annotated[str | None, Header()] = None):
        expected = cfg.api_key.get_secret_value()
        if not expected or not x_api_key or not secrets.compare_digest(expected, x_api_key):
            raise HTTPException(401, "Invalid credentials")

    @app.middleware("http")
    async def observe(request: Request, call_next):
        started = time.perf_counter()
        correlation = str(uuid.uuid4())
        status = 500
        try:
            response = await call_next(request)
            status = response.status_code
            response.headers["X-Correlation-ID"] = correlation
            return response
        finally:
            elapsed = time.perf_counter() - started
            is_prediction = request.url.path == "/predict"
            metrics.record(elapsed, status, is_prediction)
            logger.info(
                json.dumps(
                    {
                        "event": "request_complete",
                        "correlation_id": correlation,
                        "operation": "predict" if is_prediction else "other",
                        "status": status,
                        "duration_ms": round(elapsed * 1000, 3),
                        "model_version": cfg.model_version,
                    }
                )
            )

    @app.exception_handler(RequestValidationError)
    async def invalid_request(request, exc):
        # Do not echo input values or Pydantic ctx; they can contain sensitive data.
        return JSONResponse(status_code=422, content={"detail": "Invalid prediction schema"})

    @app.get("/health/live")
    def live():
        return {"status": "alive"}

    @app.get("/health/ready")
    def ready():
        if app.state.predictor is None or not cfg.api_key.get_secret_value():
            raise HTTPException(503, "Not ready")
        return {"status": "ready", "model_version": cfg.model_version}

    @app.post("/predict", response_model=list[Prediction], dependencies=[Depends(authorized)])
    def predict(body: PredictionRequest):
        if len(body.instances) > cfg.max_batch:
            raise HTTPException(413, "Batch too large")
        if app.state.predictor is None:
            raise HTTPException(503, "Model unavailable")
        try:
            return app.state.predictor.predict([row.model_dump() for row in body.instances])
        except Exception:
            raise HTTPException(503, "Inference unavailable") from None

    @app.get("/metrics", dependencies=[Depends(authorized)])
    def observations():
        return metrics.snapshot(cfg.model_version)

    return app


app = create_app()
