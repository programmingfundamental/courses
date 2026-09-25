"""Execute trusted local teaching notebook code cells without GPU/kernel state.

This validates plain-Python cells; it is not a Jupyter UI rendering test.
"""

import json
import os
from pathlib import Path

path = Path(__file__).parents[2] / "lab01-lifecycle-processes" / "starter" / "experiment.ipynb"
notebook = json.loads(path.read_text(encoding="utf-8"))
namespace = {"__name__": "__main__"}
os.chdir(path.parent)
for cell in notebook["cells"]:
    if cell["cell_type"] == "code":
        exec(compile("".join(cell["source"]), str(path), "exec"), namespace)
print("PASS: notebook cells execute in order from a clean namespace")
