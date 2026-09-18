import fs from 'node:fs';
import path from 'node:path';

const routes = JSON.parse(fs.readFileSync('migration/routes.json', 'utf8'));
const escape = (text) => text.replaceAll('&', '&amp;').replaceAll('"', '&quot;').replaceAll('<', '&lt;');
let count = 0;
for (const { oldUrls, url } of routes) {
  for (const old of oldUrls.filter((url) => url.endsWith('.html'))) {
    const target = path.resolve('dist', '.' + old);
    if (!target.startsWith(path.resolve('dist') + path.sep)) throw new Error(`Unsafe redirect path: ${old}`);
    // /index.html is already the output of Astro's directory redirect.
    if (fs.existsSync(target)) {
      if (!fs.readFileSync(target, 'utf8').includes(url)) throw new Error(`Redirect would overwrite ${target}`);
      continue;
    }
    fs.mkdirSync(path.dirname(target), {recursive: true});
    fs.writeFileSync(target, `<!doctype html><html lang="${url.includes('/bg/') ? 'bg' : 'en'}"><head><meta charset="utf-8"><meta name="robots" content="noindex"><meta http-equiv="refresh" content="0;url=${escape(url)}"><link rel="canonical" href="https://programmingfundamental.github.io${escape(url)}"><title>Page moved</title></head><body><a href="${escape(url)}">Continue to the course</a><script>location.replace(${JSON.stringify(url)} + location.search + location.hash)</script></body></html>`);
    count++;
  }
}
console.log(`Preserved ${count} legacy .html URLs (in addition to Astro directory redirects).`);
