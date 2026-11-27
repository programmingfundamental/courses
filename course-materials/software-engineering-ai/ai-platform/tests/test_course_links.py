import runpy
from pathlib import Path

import pytest


verify_markdown_links = runpy.run_path(
    str(Path(__file__).parents[1] / "scripts" / "verify_course.py")
)["verify_markdown_links"]


@pytest.mark.parametrize(
    ("link", "target"),
    [
        (
            "/courses/downloads/task-manager-starter.zip",
            "public/downloads/task-manager-starter.zip",
        ),
        ("../setup.md#setup", "course/setup.md"),
    ],
)
@pytest.mark.parametrize("exists", [True, False])
def test_local_links_require_existing_target(tmp_path, link, target, exists):
    document = tmp_path / "course" / "lab01" / "hint.md"
    document.parent.mkdir(parents=True)
    document.write_text(f"[Resource]({link})", encoding="utf-8")
    if exists:
        resource = tmp_path / target
        resource.parent.mkdir(parents=True, exist_ok=True)
        resource.touch()
        verify_markdown_links([document], tmp_path)
    else:
        with pytest.raises(AssertionError) as error:
            verify_markdown_links([document], tmp_path)
        assert error.value.args[0] == (document, link)


def test_remote_links_and_anchors_are_not_local_files(tmp_path):
    document = tmp_path / "README.md"
    document.write_text(
        "[HTTPS](https://example.com/docs) [HTTP](http://example.com/docs) [Section](#setup)",
        encoding="utf-8",
    )
    verify_markdown_links([document], tmp_path)
