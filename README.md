# Programming Fundamentals — courses

An Astro + Starlight course library in Bulgarian and English, published at
[programmingfundamental.github.io/courses](https://programmingfundamental.github.io/courses/).

## Requirements

Node.js **22.12 or newer** and npm **9.6.5 or newer**. Node 24 LTS is recommended
and used in GitHub Actions. Use the committed `package-lock.json`.

## Install

```bash
npm install
```

## Development

```bash
npm run dev
```

Open the URL printed in the terminal and append `/courses/`. Bulgarian starts at
`/courses/bg/`; English starts at `/courses/en/`.

## Production build

```bash
npm run check
npm run build
npm run check:links
npm run check:migration
```

The build creates `dist/`, including Pagefind's static search index and compatibility
redirects. `check:links` checks generated links, anchors, assets, and path casing.
`check:migration` checks preserved routes, asset hashes, language navigation, and lab ordering.
Do not commit `dist/`, `.astro/`, or `node_modules/`.

## Preview

```bash
npm run preview
```

Open `/courses/` on the printed preview URL. Search is available in the production
preview. Current Astro starts the preview server in the background;
`npx astro preview stop` stops it.

## Content structure

```text
src/content/docs/bg/<course>/index.md     Bulgarian course overview
src/content/docs/en/<course>/index.md     English course overview
src/content/docs/<language>/<course>/<lab>/index.md
src/content/docs/<language>/<course>/<lab>/<lesson>.md
src/content/i18n/bg.json                  Bulgarian interface labels
public/assets/                           Shared images and downloads
public/docs/                             Existing lesson-local images
```

Course folders and existing lab filenames retain the local repository's names.
`index.md` is a directory's overview. Ordinary Markdown remains the authoring format.
Six legacy drafts are retained with `draft: true`; they are excluded from production.

## Adding a course

Create `src/content/docs/bg/<course>/index.md` (or `en` for English):

```yaml
---
title: Course title
sidebar:
  order: 13
---
```

Add the overview text below the frontmatter. The course appears automatically on
its language's homepage. Add only translations that actually exist. Use matching
paths across languages for new translations; the language selector falls back to
the other language's course overview when a lesson has no counterpart.

## Adding a lab

The Java networking master's course is authored in [`course-materials/network-programming-java/`](course-materials/network-programming-java/README.md).
Run `npm run sync:network-labs` after editing its student `labXX.md` files to update the seven site pages.
`npm run check` verifies that those pages match their sources. Instructor notes are kept outside the site content tree.

The Android master's course is authored in [`course-materials/android-mobile-technologies/`](course-materials/android-mobile-technologies/README.md).
Run `npm run sync:android-labs` after editing its student `labXX.md` files.
The same `npm run check` also verifies these seven pages; Android instructor notes stay outside the site content tree.

The Mobile Internet master's course is authored in [`course-materials/mobile-internet-course/`](course-materials/mobile-internet-course/README.md).
Its ten 90-minute labs share a buildable Android/Quarkus starter under `platform/`.
Run `npm run sync:mobile-internet-labs` after editing the student files; `npm run check` verifies the generated pages.
Instructor notes and starter source projects remain outside the site content tree.

Create `<course>/laboratorno-uprazhnenie-16/index.md` or `<course>/lab16/index.md`.
Set `title` and `sidebar.order: 16`, then write the lesson. Add extra Markdown
pages inside the lab folder with their own titles and sidebar orders. No sidebar
configuration changes are needed. Existing lab order follows migrated `nav_order`,
which sometimes differs from the old folder number.

Course and lab indexes automatically list immediate child pages not already linked
in their text. Nested lesson sections stay collapsed until opened. Lab overview
pages link directly to the preceding and following lab.

## Adding an image or download

Place new shared files in `public/assets/`. Reference them with the project base:

```markdown
![Description](/courses/assets/example.png)
[Download example](/courses/assets/example.zip)
[Next lab](/courses/bg/<course>/lab2/)
```

Use URL-encoded spaces in links. Do not use `/assets/...` or links ending in `.md`.
Existing files in `public/docs/` keep their original public paths. If intentionally
replacing a migrated asset, update its SHA-256 in `migration/assets.json`.

When intentionally deleting a migrated asset, remove its record from
`migration/assets.json` and remove any links or index pages that reference it.
For a deleted migrated page, also update `migration/routes.json` so its old URLs
do not redirect to a missing page. Keep `check:migration` enabled to detect
accidentally missing files. The lecture PDFs and their two `lekcii.md` index pages
have been removed; they are no longer part of the site or asset manifest.

Fenced code blocks retain their language tags. Mermaid diagrams use ordinary
`mermaid` fences and are rendered by a locally bundled dependency. Starlight note,
tip, and caution blocks are available using `:::note`, `:::tip`, and `:::caution`.

## Deployment

Pushes or merges to `main`, and manual runs of `.github/workflows/pages.yml`,
validate the site, run the official `withastro/action`, upload `dist/` as the Pages
artifact, and deploy with `actions/deploy-pages`. In GitHub repository settings,
**Pages → Source** must be **GitHub Actions**. Pull requests run validation without deployment.

The site origin and `/courses` base are configured in `astro.config.mjs`.
Directory redirects are handled by Astro; the build's `postbuild` script preserves
Jekyll `.html` URLs. If a migrated page is renamed later, update its target and URL
in `migration/routes.json` to keep existing bookmarks working.

See [migration notes](migration/README.md) for URL mapping, preserved drafts,
content limitations, and verification results.
