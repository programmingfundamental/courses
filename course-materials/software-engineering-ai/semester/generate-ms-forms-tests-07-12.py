"""Build the six instructor import documents and a private download bundle."""

import json
import re
from pathlib import Path
from zipfile import ZipFile, ZIP_DEFLATED

from docx import Document
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from docx.shared import Cm, Pt, RGBColor

ROOT = Path(__file__).resolve().parent
MARK = "## Тест за Microsoft Forms"


def build(n, source):
    folder = source.parent
    lesson = source.read_text(encoding="utf-8").splitlines()
    data = json.loads((folder / "ms-forms-test.json").read_text(encoding="utf-8"))
    notes = (folder / "hint.md").read_text(encoding="utf-8").split(MARK + "\n", 1)[1]
    rows = re.findall(r"^\| (\d+) \| ([^|\n]+) \|$", notes.split("### Обосновка", 1)[0], re.M)
    keys = [v for _, v in rows]
    assert [int(k) for k, _ in rows] == list(range(1, 11))
    evidence = re.split(r"^#### Въпрос (\d+)\s*$", notes.split("### Настройване", 1)[0], flags=re.M)
    assert list(map(int, evidence[1::2])) == list(range(1, 11))
    refs = 0
    for i, section in enumerate(evidence[2::2]):
        assert f"**Отговор: {keys[i]}.**" in section
        citations = re.findall(r"· lab(\d+)\.md, ред(?:ове)? (\d+)(?:–(\d+))?:\n\n((?:> [^\n]+\n)+)", section)
        assert citations, (n, i + 1)
        for lab, start, end, quoted in citations:
            assert int(lab) == n
            expected = [line.strip() for line in lesson[int(start)-1:int(end or start)] if line.strip()]
            assert [line[2:] for line in quoted.splitlines()] == expected, (n, i + 1, start)
            refs += 1

    doc = Document()
    sec = doc.sections[0]
    sec.page_width, sec.page_height = Cm(21), Cm(29.7)
    sec.top_margin = sec.bottom_margin = Cm(1.8)
    sec.left_margin = sec.right_margin = Cm(2)
    for style in doc.styles:
        for b in style.element.xpath("./w:pPr/w:pBdr"):
            b.getparent().remove(b)
    for name, size in (("Normal", 11), ("Title", 21), ("Subtitle", 13)):
        st = doc.styles[name]
        st.font.name, st.font.size, st.font.color.rgb = "Calibri", Pt(size), RGBColor(0, 0, 0)
        lang = OxmlElement("w:lang")
        lang.set(qn("w:val"), "bg-BG")
        st.element.get_or_add_rPr().append(lang)
    normal = doc.styles["Normal"].paragraph_format
    normal.line_spacing, normal.space_after = 1.10, Pt(5)
    doc.add_paragraph(data["title"], "Title")
    doc.add_paragraph(data["subtitle"], "Subtitle")
    doc.add_paragraph(data["instructions"])
    actual = [data["title"], data["subtitle"], data["instructions"]]
    qs = data["questions"]
    assert [q["number"] for q in qs] == list(range(1, 11))
    assert [q["type"] for q in qs] == ["single"]*4 + ["multiple"]*4 + ["text"]*2
    for i, (q, key) in enumerate(zip(qs, keys), 1):
        opts = q.get("options", [])
        assert len(opts) == (4 if i <= 4 else 5 if i <= 8 else 0)
        assert [v[:3] for v in opts] == [f"{chr(65+j)}. " for j in range(len(opts))]
        if opts:
            correct = key.split(", ")
            assert len(set(correct)) == len(correct) == (1 if i <= 4 else 2)
            assert all(c in [v[0] for v in opts] for c in correct)
        else:
            assert key and key.isascii()
        prompt = f'{i}. {q["prompt"]}'
        p = doc.add_paragraph()
        p.add_run(prompt).bold = True
        pf = p.paragraph_format
        pf.keep_with_next = pf.keep_together = True
        pf.space_before, pf.space_after = Pt(8), Pt(5)
        # Keep full questions together on each of three balanced pages.
        if i in (4, 7):
            pf.page_break_before = True
        for v in opts or ["______________________________"]:
            p = doc.add_paragraph(v)
            pf = p.paragraph_format
            pf.keep_with_next = pf.keep_together = True
            pf.left_indent, pf.first_line_indent = Cm(.45), Cm(-.45)
            pf.space_after = Pt(4)
        answer = "ANSWER: " + key
        doc.add_paragraph().add_run(answer).bold = True
        actual.extend([prompt, *(opts or ["______________________________"]), answer])
    out = folder / f"software-engineering-lab{n:02}-ms-forms.docx"
    doc.core_properties.title = data["title"]
    doc.core_properties.subject = data["subtitle"]
    doc.core_properties.author = doc.core_properties.last_modified_by = ""
    doc.core_properties.comments = ""
    doc.save(out)
    saved = Document(out)
    assert not saved.tables and [p.text for p in saved.paragraphs] == actual
    assert out.stat().st_size < 10 * 1024 * 1024
    print(f"{n}: 10 questions, 36 options, 10 keys, {refs} verified source passages")
    return n, out, "# " + data["subtitle"] + "\n\n" + MARK + "\n" + notes, refs


def main():
    schedule = json.loads((ROOT / "schedule.json").read_text(encoding="utf-8"))
    outputs = [build(item["number"], ROOT.parent / item["file"]) for item in schedule if 7 <= item["number"] <= 12]
    assert [x[0] for x in outputs] == list(range(7, 13))
    archive = ROOT / "software-engineering-labs07-12-ms-forms.zip"
    with ZipFile(archive, "w", compression=ZIP_DEFLATED) as z:
        for n, path, notes, _ in outputs:
            z.write(path, f"{n:02}/{path.name}")
            z.writestr(f"{n:02}/hint.md", notes)
            lesson = path.parent / f"lab{n:02}.md"
            z.write(lesson, f"{n:02}/{lesson.name}")
    with ZipFile(archive) as z:
        assert z.testzip() is None and len(z.namelist()) == len(outputs)*3
        for n, path, notes, _ in outputs:
            assert z.read(f"{n:02}/{path.name}") == path.read_bytes()
            assert z.read(f"{n:02}/hint.md").decode("utf-8") == notes
    print(f"Bundle: {archive.name}; total source passages: {sum(x[3] for x in outputs)}")


if __name__ == "__main__":
    main()
