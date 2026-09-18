import { defineConfig } from 'astro/config';
import starlight from '@astrojs/starlight';
import { readFileSync } from 'node:fs';
import remarkMermaid from './src/plugins/remark-mermaid.mjs';
import { unified } from '@astrojs/markdown-remark';

const routeMap = JSON.parse(readFileSync(new URL('./migration/routes.json', import.meta.url), 'utf8'));
const redirects = Object.fromEntries(routeMap.flatMap(({ oldUrls, url }) => oldUrls.filter((old) => !old.endsWith('.html')).map((old) => [old, url])));
redirects['/'] = '/courses/bg/';

export default defineConfig({
  site: 'https://programmingfundamental.github.io',
  base: '/courses',
  trailingSlash: 'always',
  redirects,
  markdown: { processor: unified({ remarkPlugins: [remarkMermaid] }) },
  integrations: [starlight({
    title: 'Programming Fundamentals',
    defaultLocale: 'bg',
    locales: { bg: { label: 'Български', lang: 'bg' }, en: { label: 'English', lang: 'en' } },
    editLink: { baseUrl: 'https://github.com/programmingfundamental/courses/edit/main/' },
    social: [{ icon: 'github', label: 'GitHub', href: 'https://github.com/programmingfundamental/courses' }],
    customCss: ['./src/styles/custom.css'],
    routeMiddleware: './src/route-middleware.ts',
    components: { LanguageSelect: './src/components/LanguageSelect.astro', MarkdownContent: './src/components/MarkdownContent.astro', PageTitle: './src/components/PageTitle.astro' },
    tableOfContents: { minHeadingLevel: 2, maxHeadingLevel: 3 },
    expressiveCode: { shiki: { langAlias: { JavaScript: 'javascript', TypeScript: 'typescript', XML: 'xml', Markdown: 'markdown', Dockerfile: 'dockerfile', koitlin: 'kotlin' } } },
  })],
});
