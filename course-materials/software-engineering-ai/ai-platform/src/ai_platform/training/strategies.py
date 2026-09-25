from collections.abc import Callable
from typing import Any
from sklearn.linear_model import LogisticRegression
from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import RandomForestClassifier
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import StandardScaler

# Factory registry: orchestration depends on sklearn estimator contract.
FACTORIES: dict[str, Callable[[int], Any]] = {
    "linear": lambda seed: LogisticRegression(random_state=seed, max_iter=300, C=10),
    "tree": lambda seed: DecisionTreeClassifier(random_state=seed, max_depth=5),
    "forest": lambda seed: RandomForestClassifier(
        random_state=seed, n_estimators=40, max_depth=6, n_jobs=1
    ),
}


def build_model(name: str, seed: int) -> Pipeline:
    if name not in FACTORIES:
        raise ValueError("Unknown strategy")
    return Pipeline([("scale", StandardScaler()), ("model", FACTORIES[name](seed))])
