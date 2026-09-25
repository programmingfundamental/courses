"""Local CD with readiness gate and rollback. Touches only its labelled course container."""

import argparse
import json
import os
import subprocess
import time
import urllib.error
import urllib.request

NAME = "ai-platform-course"
LABEL = "ai-platform-course=true"


def docker(*args, check=True):
    result = subprocess.run(["docker", *args], check=False, text=True, capture_output=True)
    if check and result.returncode:
        raise RuntimeError("Docker operation failed: " + result.stderr.strip())
    return result


def exists(name):
    return docker("container", "inspect", name, check=False).returncode == 0


def owned(name):
    info = json.loads(docker("container", "inspect", name).stdout)[0]
    return info["Config"].get("Labels", {}).get("ai-platform-course") == "true"


def wait_ready(port):
    for _ in range(30):
        try:
            with urllib.request.urlopen(
                f"http://127.0.0.1:{port}/health/ready", timeout=1
            ) as response:
                if response.status == 200:
                    return True
        except (OSError, urllib.error.URLError):
            pass
        time.sleep(0.5)
    return False


def deploy(image, port):
    if not os.environ.get("AI_API_KEY"):
        raise SystemExit("AI_API_KEY must be set; no secret is printed")
    # Resolve tag once; run an immutable local image ID.
    image_id = json.loads(docker("image", "inspect", image).stdout)[0]["Id"]
    old = NAME + "-previous"
    if exists(old):
        raise SystemExit("Previous rollback container exists; inspect/remove it explicitly first")
    had_old = exists(NAME)
    if had_old:
        if not owned(NAME):
            raise SystemExit("Refusing to change an unrelated container")
        docker("stop", NAME)
        docker("rename", NAME, old)
    try:
        docker(
            "run",
            "-d",
            "--name",
            NAME,
            "--label",
            LABEL,
            "--env",
            "AI_API_KEY",
            "-p",
            f"127.0.0.1:{port}:8000",
            image_id,
        )
        if wait_ready(port):
            print(
                json.dumps(
                    {
                        "deployed_image_id": image_id,
                        "port": port,
                        "rollback_container": old if had_old else None,
                    }
                )
            )
            return
        raise RuntimeError("Readiness gate failed")
    except Exception:
        if exists(NAME) and owned(NAME):
            docker("rm", "-f", NAME)
        if had_old:
            docker("rename", old, NAME)
            docker("start", NAME)
            if not wait_ready(port):
                raise RuntimeError("Deployment failed and restored version is not ready") from None
        raise


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("image")
    parser.add_argument("--port", type=int, default=18090)
    args = parser.parse_args()
    if not 1024 <= args.port <= 65535:
        parser.error("Use an unprivileged local port")
    deploy(args.image, args.port)
