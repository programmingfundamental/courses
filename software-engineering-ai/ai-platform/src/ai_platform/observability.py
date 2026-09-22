import threading
import numpy as np
import pandas as pd


class Metrics:
    """Per-process aggregate metrics; no request content, identifiers or user labels."""

    def __init__(self):
        self.lock = threading.Lock()
        self.request_count = 0
        self.error_count = 0
        self.prediction_latency = []

    def record(self, duration: float, status: int, is_prediction: bool) -> None:
        with self.lock:
            self.request_count += 1
            self.error_count += int(status >= 400)
            if is_prediction:
                self.prediction_latency.append(duration)
                self.prediction_latency = self.prediction_latency[-1000:]

    def snapshot(self, version: str) -> dict:
        with self.lock:
            return {
                "request_count": self.request_count,
                "error_count": self.error_count,
                "prediction_latency_ms_p95": float(
                    np.percentile(self.prediction_latency, 95) * 1000
                )
                if self.prediction_latency
                else None,
                "prediction_latency_samples": len(self.prediction_latency),
                "model_version": version,
            }


def drift_score(frame: pd.DataFrame, baseline: dict) -> float:
    # Simple normalized mean shift, not a proof of concept drift or model quality.
    scores = [
        abs(float(frame[c].mean()) - stats["mean"]) / max(stats["std"], 1e-9)
        for c, stats in baseline.items()
    ]
    return max(scores, default=0.0)
