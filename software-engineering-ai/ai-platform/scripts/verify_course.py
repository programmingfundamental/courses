"""Verify course topology, notebook schema and relative Markdown links."""

import json
import re
from pathlib import Path

root = Path(__file__).parents[2]
labs = sorted(root.glob("lab[0-9][0-9]-*"))
assert len(labs) == 10
for lab in labs:
    number = lab.name[3:5]
    student = (lab / f"lab{number}.md").read_text(encoding="utf-8")
    notes = (lab / "instructor-notes.md").read_text(encoding="utf-8")
    assert len(re.findall(r"^#{1,2} \d+\. ", student, re.M)) == 16, lab
    assert len(re.findall(r"^## \d+\. ", notes, re.M)) == 15, lab
    assert (lab / "starter" / "README.md").exists(), lab
for path in [
    root / "README.md",
    *root.glob("architecture/*.md"),
    *root.glob("lab*/*.md"),
    *root.glob("lab*/starter/*.md"),
]:
    for link in re.findall(r"\]\(([^)]+)\)", path.read_text(encoding="utf-8")):
        if not link.startswith(("https:", "http:", "#")):
            assert (path.parent / link.split("#")[0]).exists(), (path, link)
notebook = json.loads((labs[0] / "starter" / "experiment.ipynb").read_text(encoding="utf-8"))
assert notebook["nbformat"] == 4
print("PASS: 10 labs, student/instructor sections, starters, links and notebook schema")
