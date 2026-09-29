"""Verify course topology, notebook schema and relative Markdown links."""

import json
import re
from pathlib import Path

root = Path(__file__).parents[2]
labs = sorted(root.glob("lab[0-9][0-9]-*"))
assert len(labs) == 11
for index, lab in enumerate(labs, start=1):
    number = lab.name[3:5]
    assert int(number) == index, lab
    student = (lab / f"lab{number}.md").read_text(encoding="utf-8")
    notes = (lab / "instructor-notes.md").read_text(encoding="utf-8")
    assert re.match(r"# Упражнение \d+ — [^\n]+\n\n## Теория\n", student), lab
    assert student.startswith(f"# Упражнение {index} — "), lab
    assert re.findall(r"^## (.+)$", student, re.M) == [
        "Теория",
        "Примерен проблем",
        "Самостоятелни задачи",
    ], lab
    theory, practice = student.split("## Примерен проблем\n", 1)
    example, tasks = practice.split("## Самостоятелни задачи\n", 1)
    assert len(re.findall(r"^### \d+\.", theory, re.M)) >= 3, lab
    assert "   - **Пример:**" in theory, lab
    assert len(re.findall(r"^### Стъпка \d+\.", example, re.M)) == 5, lab
    assert len(re.findall(r"^### Задача \d+\.", tasks, re.M)) == 3, lab
    hints = (lab / "hint.md").read_text(encoding="utf-8")
    for heading in [
        "Учебни цели",
        "Предварителни знания",
        "Инструменти",
        "Архитектурен контекст",
        "Checkpoint",
        "Въпроси за анализ",
        "Checklist",
    ]:
        assert f"## {heading}\n" in hints, (lab, heading)
        assert f"## {heading}\n" not in student, (lab, heading)
    assert "**Аудитория:**" not in student and "110 минути" not in student, lab
    if index == 1:
        assert "[hint.md](hint.md)" in notes, lab
    else:
        assert len(re.findall(r"^## \d+\. ", notes, re.M)) == 15, lab
    assert (lab / "starter" / "README.md").exists(), lab
for path in [
    root / "README.md",
    root / "setup.md",
    root / "hint.md",
    *root.glob("architecture/*.md"),
    *root.glob("lab*/*.md"),
    *root.glob("lab*/starter/*.md"),
]:
    for link in re.findall(r"\]\(([^)]+)\)", path.read_text(encoding="utf-8")):
        if not link.startswith(("https:", "http:", "#")):
            assert (path.parent / link.split("#")[0]).exists(), (path, link)
notebook = json.loads((labs[1] / "starter" / "experiment.ipynb").read_text(encoding="utf-8"))
assert notebook["nbformat"] == 4
print("PASS: 11 labs, student/instructor sections, starters, links and notebook schema")
