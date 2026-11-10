"""Create variant 2 by replacing text slots in the retained variant 1 DOCX."""

import hashlib
import json
import re
from pathlib import Path
from zipfile import ZipFile

from docx import Document
from lxml import etree

ROOT = Path(__file__).resolve().parent
REFERENCE = ROOT / "software-engineering-lab04-ms-forms.docx"
OUTPUT = ROOT / "software-engineering-lab04-ms-forms-v2.docx"
REFERENCE_SHA256 = "02c804f776c32f3c12fcdab77415d4245b271ff5d050875bfc38661c80b0c54c"
NS = {"w": "http://schemas.openxmlformats.org/wordprocessingml/2006/main"}


def main():
    reference_bytes = REFERENCE.read_bytes()
    assert hashlib.sha256(reference_bytes).hexdigest() == REFERENCE_SHA256
    data = json.loads((ROOT / "ms-forms-test-v2.json").read_text(encoding="utf-8"))
    questions = data["questions"]
    assert [q["number"] for q in questions] == list(range(1, 11))
    assert [q["type"] for q in questions] == ["single"] * 4 + ["multiple"] * 4 + ["text"] * 2
    old = json.loads((ROOT / "ms-forms-test.json").read_text(encoding="utf-8"))
    assert not {q["prompt"] for q in questions} & {q["prompt"] for q in old["questions"]}
    notes = (ROOT / "hint.md").read_text(encoding="utf-8").split(
        "## Тест за Microsoft Forms — вариант 2\n", 1
    )[1]
    key = notes.split("### Ключ и обосновка — вариант 2", 1)[1].split("\n### ", 1)[0]
    rows = re.findall(r"^\| (\d+) \| ([^|]+) \|", key, re.MULTILINE)
    assert [int(n) for n, _ in rows] == list(range(1, 11))
    answers = {int(n): answer.strip() for n, answer in rows}
    assert answers[9] == "Contract failed" and answers[10] == "DOCUMENTATION"
    numbers = re.findall(r"^#### Вариант 2 — въпрос (\d+)\s*$", notes, re.MULTILINE)
    assert [int(n) for n in numbers] == list(range(1, 11))
    lesson = (ROOT / "lab04.md").read_text(encoding="utf-8")
    quotations = re.findall(r"^> (.+)$", notes, re.MULTILINE)
    assert quotations
    for quotation in quotations:
        assert quotation in lesson, f"Quotation absent from lab04.md: {quotation}"

    content = [data["title"], data["subtitle"], data["instructions"]]
    for question in questions:
        n = question["number"]
        options = question.get("options", [])
        count = {"single": 4, "multiple": 5, "text": 0}[question["type"]]
        assert len(options) == count
        assert [o[:3] for o in options] == [f"{chr(65 + i)}. " for i in range(count)]
        if count:
            correct = answers[n].split(", ")
            assert len(correct) == (1 if question["type"] == "single" else 2)
            assert len(set(correct)) == len(correct)
            assert all(letter in "ABCDE"[:count] for letter in correct)
        assert answers[n].isascii() and "\n" not in answers[n]
        content.append(f'{n}. {question["prompt"]}')
        content.extend(options or ["______________________________"])
        content.append(f"ANSWER: {answers[n]}")
    assert len(content) == 61

    with ZipFile(REFERENCE) as source:
        document = etree.fromstring(source.read("word/document.xml"))
        paragraphs = document.xpath("./w:body/w:p", namespaces=NS)
        assert len(paragraphs) == len(content)
        for paragraph, value in zip(paragraphs, content):
            text_nodes = paragraph.xpath(".//w:t", namespaces=NS)
            assert len(text_nodes) == 1, "Reference slot structure changed"
            text_nodes[0].text = value
        core = etree.fromstring(source.read("docProps/core.xml"))
        dc = {"dc": "http://purl.org/dc/elements/1.1/"}
        core.find("dc:title", dc).text = data["title"]
        core.find("dc:subject", dc).text = data["subtitle"]
        edited = {
            "word/document.xml": etree.tostring(document, encoding="UTF-8", xml_declaration=True, standalone=True),
            "docProps/core.xml": etree.tostring(core, encoding="UTF-8", xml_declaration=True, standalone=True),
        }
        with ZipFile(OUTPUT, "w") as result:
            for part in source.infolist():
                result.writestr(part, edited.get(part.filename, source.read(part.filename)))

    saved = Document(OUTPUT)
    assert [p.text for p in saved.paragraphs] == content
    assert not saved.tables and len(saved.sections) == 1
    assert len([s for s in content if re.match(r"^[A-E]\. ", s)]) == 36
    assert len([s for s in content if s.startswith("ANSWER: ")]) == 10
    assert len([s for s in content if s.startswith("____")]) == 2
    assert OUTPUT.stat().st_size < 10 * 1024 * 1024
    with ZipFile(REFERENCE) as source, ZipFile(OUTPUT) as result:
        assert source.namelist() == result.namelist()
        for name in source.namelist():
            if name not in edited:
                assert source.read(name) == result.read(name), f"Unintended change: {name}"
        before = etree.fromstring(source.read("word/document.xml"))
        after = etree.fromstring(result.read("word/document.xml"))
        for root in (before, after):
            for node in root.xpath(".//w:t", namespaces=NS):
                node.text = "SLOT"
        assert etree.tostring(before) == etree.tostring(after), "Non-text document structure changed"
    assert hashlib.sha256(REFERENCE.read_bytes()).hexdigest() == REFERENCE_SHA256
    print(f"Created {OUTPUT} ({OUTPUT.stat().st_size} bytes)")
    print("Verified: 10 new prompts; 4 single, 4 multiple, 2 short; 36 options; 10 answers.")
    print(f"Verified {len(quotations)} verbatim lesson excerpts, all formatting, and unchanged reference.")


if __name__ == "__main__":
    main()
