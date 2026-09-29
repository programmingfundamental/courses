import json
from pathlib import Path

spec = json.loads(Path(__file__).with_name("requirements.json").read_text())
assert len({r["id"] for r in spec["requirements"]}) == len(spec["requirements"])
print("Structural check passed; TODO: reject vague/unmeasurable requirements")
