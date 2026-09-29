"""Works, but training and serving are coupled. Run --smoke before refactoring."""
import argparse
from pathlib import Path
import pandas as pd
from sklearn.linear_model import LogisticRegression
from sklearn.pipeline import make_pipeline
from sklearn.preprocessing import StandardScaler
from fastapi import FastAPI

DATA = Path(__file__).parents[2] / "ai-platform/data/requests-v1.csv"
df = pd.read_csv(DATA)
# TODO: remove training from import/startup; define an artifact boundary.
model = make_pipeline(StandardScaler(), LogisticRegression(C=10, max_iter=300))
model.fit(df[["token_count", "keyword_score", "previous_requests"]], df.label)
app = FastAPI()


@app.get("/demo")
def demo():
    return {"label": int(model.predict(df[["token_count", "keyword_score", "previous_requests"]].head(1))[0])}


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--smoke", action="store_true")
    args = parser.parse_args()
    if args.smoke:
        print(demo())
    else:
        import uvicorn
        uvicorn.run(app, host="127.0.0.1", port=8010)
