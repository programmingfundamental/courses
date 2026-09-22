"""Hardcoded infrastructure and domain logic; executes on the synthetic dataset."""
from pathlib import Path
import pandas as pd
from sklearn.dummy import DummyClassifier


def predict_one():
    # TODO: inject a repository/protocol and separate data preparation.
    data = pd.read_csv(Path(__file__).parents[2] / "ai-platform/data/requests-v1.csv")
    features = ["token_count", "keyword_score", "previous_requests"]
    model = DummyClassifier(strategy="most_frequent").fit(data[features], data.label)
    return int(model.predict(data[features].head(1))[0])


if __name__ == "__main__":
    print({"prediction": predict_one()})
