"""Generate the Word quiz with the answer key requested by the instructor."""

import json
import re
from pathlib import Path

from docx import Document
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from docx.shared import Cm, Pt, RGBColor


ROOT = Path(__file__).resolve().parent
SOURCE = ROOT / "ms-forms-test.json"
KEY_SOURCE = ROOT / "hint.md"
OUTPUT = ROOT / "web-security-lab06-ms-forms.docx"


def main():
    data = json.loads(SOURCE.read_text(encoding="utf-8"))
    questions = data["questions"]
    key_rows = re.findall(
        r"^\| (\d+) \| ([^|]+) \|", KEY_SOURCE.read_text(encoding="utf-8"), re.MULTILINE
    )
    assert [int(number) for number, _ in key_rows] == list(range(1, 11))
    answers = {int(number): answer.strip() for number, answer in key_rows}
    assert [q["number"] for q in questions] == list(range(1, 11))
    assert [q["type"] for q in questions] == ["single"] * 4 + ["multiple"] * 4 + ["text"] * 2
    for q in questions:
        count = {"single": 4, "multiple": 5, "text": 0}[q["type"]]
        assert len(q.get("options", [])) == count
        assert [o[:3] for o in q.get("options", [])] == [f"{chr(65+i)}. " for i in range(count)]
        answer = answers[q["number"]]
        if count:
            letters = answer.split(" и ")
            assert len(letters) == (1 if q["type"] == "single" else 2)
            assert len(set(letters)) == len(letters)
            assert all(letter in [chr(65+i) for i in range(count)] for letter in letters)
        else:
            assert answer

    doc = Document()
    # Remove title rules inherited from the bundled Word template.
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
        rpr = style.element.get_or_add_rPr()
        lang = OxmlElement("w:lang")
        lang.set(qn("w:val"), "bg-BG")
        rpr.append(lang)

    normal = doc.styles["Normal"]
    normal.font.size = Pt(11)
    normal.paragraph_format.line_spacing = 1.05
    normal.paragraph_format.space_after = Pt(5)
    normal.paragraph_format.widow_control = True
    doc.styles["Title"].font.size = Pt(20)
    doc.styles["Title"].paragraph_format.space_after = Pt(5)
    doc.styles["Subtitle"].font.size = Pt(12)
    doc.styles["Subtitle"].paragraph_format.space_after = Pt(10)

    doc.add_paragraph(data["title"], "Title")
    doc.add_paragraph(data["subtitle"], "Subtitle")
    introduction = doc.add_paragraph(data["instructions"])
    introduction.paragraph_format.space_after = Pt(12)
    for question in questions:
        p = doc.add_paragraph()
        p.paragraph_format.space_before = Pt(9)
        p.paragraph_format.space_after = Pt(6)
        p.paragraph_format.keep_together = True
        p.paragraph_format.keep_with_next = True
        p.add_run(f'{question["number"]}. {question["prompt"]}').bold = True
        options = question.get("options", [])
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
            p.paragraph_format.space_after = Pt(4)
        p = doc.add_paragraph()
        p.paragraph_format.keep_together = True
        p.paragraph_format.space_before = Pt(2)
        p.paragraph_format.space_after = Pt(7)
        # ANSWER is documented by Microsoft staff for single-choice imports.
        # Multi-select and text keys remain visible for post-import verification;
        # their automatic recognition is not confirmed (see hint.md).
        answer = answers[question["number"]].replace(" и ", ", ")
        p.add_run(f"ANSWER: {answer}").bold = True

    doc.core_properties.title = data["title"]
    doc.core_properties.subject = data["subtitle"]
    doc.core_properties.author = ""
    doc.core_properties.last_modified_by = ""
    doc.core_properties.comments = ""
    for paragraph in doc.paragraphs:
        for border in paragraph._p.xpath("./w:pPr/w:pBdr"):
            border.getparent().remove(border)
    doc.save(OUTPUT)
    saved = Document(OUTPUT)
    assert len(saved.tables) == 0
    assert sum(p.text.startswith(tuple(f"{i}. " for i in range(1, 11))) for p in saved.paragraphs) == 10
    key_paragraphs = [p.text for p in saved.paragraphs if p.text.startswith("ANSWER: ")]
    assert len(key_paragraphs) == 10
    assert [p.split(": ", 1)[1] for p in key_paragraphs] == [a.replace(" и ", ", ") for a in answers.values()]
    assert all(p.isascii() for p in key_paragraphs)
    assert OUTPUT.stat().st_size < 10 * 1024 * 1024
    print(f"Created {OUTPUT} ({OUTPUT.stat().st_size} bytes)")


if __name__ == "__main__":
    main()
