"""Bounded local benchmark. Measures client-observed sequential latency, not an SLO proof."""

import argparse
import json
import os
import time
import numpy as np
import httpx

parser = argparse.ArgumentParser()
parser.add_argument("--port", type=int, default=8000)
parser.add_argument("--count", type=int, default=100)
args = parser.parse_args()
if not 10 <= args.count <= 1000:
    parser.error("count must be 10..1000")
latencies = []
with httpx.Client(base_url=f"http://127.0.0.1:{args.port}", timeout=3) as client:
    for i in range(args.count + 10):
        started = time.perf_counter()
        response = client.post(
            "/predict",
            headers={"X-API-Key": os.environ["AI_API_KEY"]},
            json={
                "instances": [{"token_count": 140, "keyword_score": 0.8, "previous_requests": 2}]
            },
        )
        response.raise_for_status()
        if i >= 10:
            latencies.append(time.perf_counter() - started)
print(
    json.dumps(
        {
            "samples": len(latencies),
            "warmup": 10,
            "concurrency": 1,
            "p95_ms": float(np.percentile(latencies, 95) * 1000),
            "sequential_requests_per_second": len(latencies) / sum(latencies),
        },
        indent=2,
    )
)
