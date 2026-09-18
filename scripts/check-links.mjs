import fs from 'node:fs';
import path from 'node:path';
import { load } from 'cheerio';

const root = path.resolve('dist');
const base = '/courses';
const origin = 'https://programmingfundamental.github.io';
const allFiles = fs.readdirSync(root, {recursive:true}).filter(file => fs.statSync(path.join(root,file)).isFile()).map(file=>file.replaceAll('\\','/'));
const exactPaths = new Set(allFiles);
const files = allFiles.filter(file => file.endsWith('.html'));
const documents = new Map(files.map(file => [file.replaceAll('\\','/'), load(fs.readFileSync(path.join(root,file),'utf8'))]));
const errors = [];
let references = 0;
const resolve = (pathname) => {
  const relative = decodeURIComponent(pathname).replace(/^\/courses(?:\/|$)/,'');
  const candidates = [relative, relative.replace(/\/$/,'') + '/index.html'];
  return candidates.find(file => exactPaths.has(file));
};
for (const [file, $] of documents) {
  const source = `${origin}${base}/${file.replace(/index\.html$/,'')}`;
  $('a[href],img[src],script[src],link[href],source[src]').each((_,element) => {
    const value = $(element).attr(element.name === 'a' || element.name === 'link' ? 'href' : 'src');
    if (!value || /^(mailto:|tel:|data:|javascript:)/i.test(value)) return;
    const url = new URL(value,source);
    if (url.origin !== origin) return;
    references++;
    if (!url.pathname.startsWith(base + '/') && url.pathname !== base) {
      errors.push({file,value,reason:'outside /courses base'}); return;
    }
    const target = resolve(url.pathname);
    if (!target) { errors.push({file,value,reason:'missing file'}); return; }
    if (url.hash && documents.has(target)) {
      const targetDoc = documents.get(target);
      if (targetDoc('meta[http-equiv="refresh"]').length) return;
      const anchor = decodeURIComponent(url.hash.slice(1));
      if (!targetDoc('[id],a[name]').toArray().some(node => targetDoc(node).attr('id') === anchor || targetDoc(node).attr('name') === anchor)) {
        errors.push({file,value,reason:'missing anchor'});
      }
    }
  });
}
if (errors.length) {
  console.error(JSON.stringify(errors,null,2));
  console.error(`${errors.length} broken references in ${files.length} HTML files.`);
  process.exitCode = 1;
} else console.log(`PASS: ${references} internal links/assets/anchors across ${files.length} HTML files, all under /courses.`);
