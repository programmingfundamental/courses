from sklearn.linear_model import LogisticRegression
from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import RandomForestClassifier


def choose(name):
    # TODO: keep orchestration stable while extending model implementations.
    if name == "linear":
        return LogisticRegression(random_state=42)
    elif name == "tree":
        return DecisionTreeClassifier(random_state=42)
    elif name == "forest":
        return RandomForestClassifier(random_state=42, n_estimators=10)
    raise ValueError("Unknown model")


if __name__ == "__main__":
    print(type(choose("linear")).__name__)
