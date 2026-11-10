"""Build the instructor Word import file with answers sourced from hint.md."""

import json
import re
from pathlib import Path

from docx import Document
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from docx.shared import Cm, Pt, RGBColor


ROOT = Path(__file__).resolve().parent
OUTPUT = ROOT / "software-engineering-lab04-ms-forms.docx"


def main():
    data = json.loads((ROOT / "ms-forms-test.json").read_text(encoding="utf-8"))
    questions = data["questions"]
    assert [q["number"] for q in questions] == list(range(1, 11))
    assert [q["type"] for q in questions] == ["single"] * 4 + ["multiple"] * 4 + ["text"] * 2
    instructor_notes = (ROOT / "hint.md").read_text(encoding="utf-8")
    key_section = instructor_notes.split("### Ключ и обосновка", 1)[1].split("\n### ", 1)[0]
    lesson = (ROOT / "lab04.md").read_text(encoding="utf-8")
    evidence_numbers = re.findall(r"^#### Въпрос (\d+)\s*$", instructor_notes, re.MULTILINE)
    assert [int(n) for n in evidence_numbers] == list(range(1, 11))
    quotations = re.findall(r"^> (.+)$", instructor_notes, re.MULTILINE)
    assert quotations
    for quotation in quotations:
        assert quotation in lesson, f"Quotation not found in lab04.md: {quotation}"
    key_rows = re.findall(r"^\| (\d+) \| ([^|]+) \|", key_section, re.MULTILINE)
    assert [int(n) for n, _ in key_rows] == list(range(1, 11))
    answers = {int(n): value.strip() for n, value in key_rows}
    for q in questions:
        count = {"single": 4, "multiple": 5, "text": 0}[q["type"]]
        options = q.get("options", [])
        assert len(options) == count
        assert [o[:3] for o in options] == [f"{chr(65 + i)}. " for i in range(count)]
        if count:
            correct = answers[q["number"]].split(", ")
            assert len(correct) == (1 if q["type"] == "single" else 2)
            assert len(set(correct)) == len(correct)
            assert all(letter in "ABCDE"[:count] for letter in correct)
        else:
            assert answers[q["number"]].isascii() and answers[q["number"]].isidentifier()

    doc = Document()
    for style in doc.styles:
        for border in style.element.xpath("./w:pPr/w:pBdr"):
            border.getparent().remove(border)
    section = doc.sections[0]
    section.page_width, section.page_height = Cm(21), Cm(29.7)
    section.top_margin = section.bottom_margin = Cm(1.8)
    section.left_margin = section.right_margin = Cm(2)

    for name in ("Normal", "Title", "Subtitle"):
        style = doc.styles[name]
        style.font.name = "Calibri"
        style.font.color.rgb = RGBColor(0, 0, 0)
        lang = OxmlElement("w:lang")
        lang.set(qn("w:val"), "bg-BG")
        style.element.get_or_add_rPr().append(lang)
    normal = doc.styles["Normal"]
    normal.font.size = Pt(11)
    normal.paragraph_format.line_spacing = 1.05
    normal.paragraph_format.space_after = Pt(5)
    normal.paragraph_format.widow_control = True
    doc.styles["Title"].font.size = Pt(20)
    doc.styles["Title"].paragraph_format.space_after = Pt(5)
    doc.styles["Subtitle"].font.size = Pt(12)
    doc.styles["Subtitle"].paragraph_format.space_after = Pt(9)

    doc.add_paragraph(data["title"], "Title")
    doc.add_paragraph(data["subtitle"], "Subtitle")
    intro = doc.add_paragraph(data["instructions"])
    intro.paragraph_format.space_after = Pt(10)
    for q in questions:
        p = doc.add_paragraph()
        p.paragraph_format.space_before = Pt(9)
        p.paragraph_format.space_after = Pt(6)
        p.paragraph_format.keep_together = True
        p.paragraph_format.keep_with_next = True
        p.add_run(f'{q["number"]}. {q["prompt"]}').bold = True
        options = q.get("options", [])
        for option in options:
            p = doc.add_paragraph(option)
            p.paragraph_format.left_indent = Cm(0.45)
            p.paragraph_format.first_line_indent = Cm(-0.45)
            p.paragraph_format.keep_together = True
            p.paragraph_format.keep_with_next = True
            p.paragraph_format.space_after = Pt(4)
        if not options:
            p = doc.add_paragraph("______________________________")
            p.paragraph_format.keep_with_next = True
            p.paragraph_format.space_after = Pt(7)
        p = doc.add_paragraph()
        p.paragraph_format.keep_together = True
        p.paragraph_format.space_before = Pt(2)
        p.paragraph_format.space_after = Pt(7)
        p.add_run(f'ANSWER: {answers[q["number"]]}').bold = True

    doc.core_properties.title = data["title"]
    doc.core_properties.subject = data["subtitle"]
    doc.core_properties.author = ""
    doc.core_properties.last_modified_by = ""
    doc.core_properties.comments = ""
    doc.save(OUTPUT)
    saved = Document(OUTPUT)
    assert not saved.tables
    prompts = [p.text for p in saved.paragraphs if re.match(r"^(?:[1-9]|10)\. ", p.text)]
    assert len(prompts) == 10
    assert len([p for p in saved.paragraphs if re.match(r"^[A-E]\. ", p.text)]) == 36
    assert len([p for p in saved.paragraphs if p.text.startswith("____")]) == 2
    answer_lines = [p.text for p in saved.paragraphs if p.text.startswith("ANSWER: ")]
    assert answer_lines == [f"ANSWER: {answers[i]}" for i in range(1, 11)]
    assert all(line.isascii() for line in answer_lines)
    assert OUTPUT.stat().st_size < 10 * 1024 * 1024
    print(f"Created {OUTPUT} ({OUTPUT.stat().st_size} bytes)")
    print("Verified: 4 single-choice, 4 two-answer questions, 2 short-answer; 36 options; 10 answer lines match hint.md.")
    print(f"Verified {len(quotations)} verbatim lesson excerpts covering all 10 questions.")


if __name__ == "__main__":
    main()


