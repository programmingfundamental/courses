from ai_platform.features.transform import feature_frame
from ai_platform.inference.repository import ModelRepository
import pandas as pd


class Predictor:
    def __init__(self, repository: ModelRepository, version: str):
        self.bundle = repository.load(version)
        self.version = self.bundle.metadata["version"]

    def predict(self, rows: list[dict]) -> list[dict]:
        frame = feature_frame(pd.DataFrame(rows))
        probabilities = self.bundle.model.predict_proba(frame)[:, 1]
        return [
            {"label": int(p >= 0.5), "probability": float(p), "model_version": self.version}
            for p in probabilities
        ]
