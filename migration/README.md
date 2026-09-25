# Migration notes

This migration used the local working tree on branch `update-structure` as its
source of truth. No repository was cloned or fetched. No commit or push was made.

## Architecture and content

- Astro 7.3.3 and Starlight 0.42.1 replace Jekyll and Just the Docs.
- At the time of the initial migration, 583 source Markdown documents were retained
  in `src/content/docs/bg/` and `en/`, across 12 course areas in each language.
- Since then, all English discipline pages and the 10 English-only image assets were
  removed. The English collection now contains only `src/content/docs/en/index.md`.
  Legacy AEO page URLs for the retired English documents lead to that landing page;
  their records are marked `retired` in [routes.json](routes.json).
- The initial migration recorded six unpublished drafts in [drafts.json](drafts.json).
  Four English draft files were later removed with the English course content; the
  two Bulgarian drafts remain unpublished.
- Two Markdown indexes were added for the 22 lecture PDFs from OOP I and OOP II.
  The PDFs and both `lekcii.md` pages were intentionally deleted after migration;
  the deleted PDFs have also been removed from the active asset manifest.
- All 1,900 parsed fenced code blocks, including language identifiers, were verified
  unchanged against the original local files. Markdown bodies retain their original
  line endings; link and frontmatter changes are limited to migration needs.
- The initial migration preserved 279 static assets (110,904,973 bytes).
  After the lecture PDFs were deleted, 257 assets (37,980,535 bytes) remained.
  The later English-content removal deleted 10 English-only images (959,938 bytes),
  leaving 247 active assets. [assets.json](assets.json) records the retained files'
  original paths, new paths, and SHA-256 hashes. [preservation.json](preservation.json) is the historical
  snapshot of the initial migration, including its original asset counts.
- The local editing journal at `docs/BEO/programirane-za-mobilni-i-internet-ustroistva-kotlin/REDAKCIA-STATUS.md`
  was retained during migration and deleted afterwards with the lecture materials.

The old `_config.yml`, `Gemfile`, `Gemfile.lock`, root site `index.md`, and Mermaid
Jekyll include were removed after a successful complete build. Both workflows were
converted from Ruby/Jekyll to npm/Astro. Existing `.gitignore` entries were preserved.
Mermaid is bundled locally and loaded only on pages with diagrams.

## Navigation and languages

The localized homepages list courses as Starlight cards. A course sidebar shows its
overview, numbered lab groups, additional materials, and a collapsed list of other
courses. The Starlight route-data hook uses the content tree and migrated order
metadata; adding an ordinary Markdown lab does not require configuration edits.
Course and lab indexes list child pages that are not already linked in the source.
Starlight supplies search, responsive menus, the table of contents, code presentation,
and light/dark themes. Repeated page-title headings are avoided without deleting
the original Markdown headings.

The original English OOP I course used a different lab sequence from its directory
numbers. Its `nav_order` was preserved at migration time. All English course pages
have since been retired as described above.

The language selector reuses matching files and known `labN`,
`laboratorno-uprazhnenie-N`, and `laboratory-exercise-N` naming conventions. When no
counterpart exists, it opens the selected language's course overview. Starlight's
otherwise automatic Bulgarian fallback routes are static redirects, so Bulgarian
content is not published or searched as English. Alternate-language metadata points
only to existing counterparts. Missing translations were not generated.

Three original AEO Software Systems Lab 3 documents contained Bulgarian material.
They were preserved under the Bulgarian Lab 3 as `properties-and-events.md`,
`primerna-zadacha.md`, and `zadachi-properties.md`. The previous Bulgarian material
was preserved separately. Their former AEO URLs now follow the retired-English
redirect policy and lead to the English landing page.

## URL compatibility

The historical per-file mapping is in [routes.json](routes.json), covering 577
original public pages and their legacy aliases. Active pages map to canonical content
URLs; the 236 retired English pages redirect to the English landing page:

| Former route | Current canonical route |
| --- | --- |
| `/courses/` | `/courses/bg/` |
| `/courses/docs/BEO/<course>/` | `/courses/bg/<course>/` |
| `/courses/docs/AEO/<course>/` | `/courses/en/` (English course pages retired) |
| `.../<lesson>.html` | `.../<lesson>/` under the corresponding language |
| `.../README.html` or `.../index.html` | The corresponding directory overview |

Astro generates static directory/permalink redirects. `scripts/legacy-redirects.mjs`
adds real `.html` redirect files after the build, because Astro's directory output
format would otherwise turn an old `.html` route into a directory. These redirects
include canonical links, a clickable destination, a meta refresh, and fragment/query
preservation when JavaScript is enabled. GitHub Pages does not provide HTTP 301
responses for these static files. Original asset URLs remain unchanged under `/courses`.

Four Bulgarian OOP II standalone pages shared slugs with lab directory overviews.
Both versions are retained: standalone Labs 5, 9, and 13 are now
`<lab>/samostoyatelna-rabota/`, and standalone Lab 14 is `<lab>/dobri-praktiki/`.
Their old explicit permalinks and `.html` URLs redirect to these subpages.

Nineteen internal heading links were corrected for Astro's generated IDs; exact
changes are in [anchor-changes.json](anchor-changes.json). Existing outside bookmarks
using those old fragments may need the new fragment even though the page redirects.
No exhaustive compatibility claim is made for every historical heading fragment.

## Existing content limitations

- English Kotlin Lab 4 referenced five image files absent from the original tree:
  `studio_main.png`, `sdk_manager.png`, `avd_manager.png`, `emulator_running.png`, and
  `android_components_diagram.png`. Captions and descriptions remain; visible
  missing-file notices replace broken image tags. The original screenshots are
  still needed to restore the illustrations.
- Kotlin Lab 9 refers to `lab9_strings` and `lab9_images.zip`, neither present in
  the local repository. The existing warning remains; no resource was invented.
- At migration time, partial translations remained in English OOP I's project-link caption,
  Internet Technologies `model-mapper.md` and `repository-sloi.md`, OOP I Collections,
  Kotlin Lab 4's image captions, and Training Practice II Lab 13. English course pages
  were later retired as noted above.
- Several course/lab pages were only headings or skeletons. They remain available,
  with generated navigation where child pages exist; no syllabus was invented.
- External links, remote GitHub attachment images, and authenticated SharePoint
  downloads were preserved but were not exhaustively checked for remote availability.

## Validation and deployment

At the initial migration, the local production build and Astro type check passed. Both language homepages,
course navigation, consecutive labs, a lesson with 21 code blocks and three tables,
Mermaid rendering, five local images, dark/light themes, a 390px mobile layout/menu,
and search were inspected in the browser. Search returned Bulgarian results for
`наследяване` and English results for `servlet` at the initial migration. All emitted internal URLs, files,
anchors, and exact path casing are checked by `npm run check:links`.

`npm run check:migration` checks page canonical URLs, all recorded old redirects,
source and generated asset checksums, sidebar languages, and every lab overview's
previous/next links. The initial migration's link audit passed 49,330 internal references across
1,686 HTML files. At that time, Pagefind indexed 342 Bulgarian and 237 English pages;
the check also verifies that untranslated fallback routes redirect instead of being indexed.
`npm audit` reports zero vulnerabilities. The build still emits Starlight's standard
missing-custom-404 notice and a size warning for a lazily loaded Mermaid chunk;
neither prevents building or serving the site.

GitHub Actions builds and deploys only after a push/merge to `main` or a manual
workflow run. The recommended Astro action uploads the generated Pages artifact;
`actions/deploy-pages` publishes it at the existing `/courses/` URL. Repository Pages
settings and a real hosted deployment were not changed or tested in this local task.
