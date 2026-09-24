import fs from 'node:fs';
import crypto from 'node:crypto';
import { load } from 'cheerio';
import matter from 'gray-matter';
import assert from 'node:assert/strict';

const routes = JSON.parse(fs.readFileSync('migration/routes.json','utf8'));
const assets = JSON.parse(fs.readFileSync('migration/assets.json','utf8'));
const readPage = (url) => load(fs.readFileSync('dist/' + url.replace('/courses/','') + 'index.html','utf8'));
const docs = fs.readdirSync('src/content/docs',{recursive:true}).filter(f=>f.endsWith('.md')).map(file=>({id:file.replaceAll('\\','/').replace(/(?:\/index)?\.md$/,'').toLowerCase().replaceAll(' ','-'), ...matter(fs.readFileSync('src/content/docs/'+file,'utf8'))})).filter(d=>!d.data.draft);
for (const {target,sha256} of assets) {
  for (const file of [target, target.replace(/^public\//,'dist/')]) assert.equal(crypto.createHash('sha256').update(fs.readFileSync(file)).digest('hex'),sha256,file);
}
for (const {target,url,oldUrls} of routes) {
  assert.ok(fs.existsSync(target), target);
  const $ = readPage(url);
  assert.equal($('html').attr('lang'),url.split('/')[2],url);
  assert.equal($('link[rel="canonical"]').attr('href'),'https://programmingfundamental.github.io'+url,url);
  for (const old of oldUrls) {
    const html = fs.readFileSync('dist'+old+(old.endsWith('.html')?'':(old.endsWith('/')?'':'/')+'index.html'),'utf8');
    assert.ok(html.includes(url), 'Missing legacy redirect: '+old);
  }
}
let labs = 0;
for (const course of docs.filter(d=>d.id.split('/').length===2)) {
  const url = '/courses/'+course.id+'/';
  const sequence=docs.filter(d=>!d.data.sidebar?.hidden && d.id.startsWith(course.id+'/') && d.id.split('/').length===3 && /(?:laboratorno-uprazhnenie-|laboratory-exercise-|lab)\d+$/.test(d.id)).sort((a,b)=>(a.data.sidebar?.order??999)-(b.data.sidebar?.order??999)||a.id.localeCompare(b.id,undefined,{numeric:true}));
  for (const [i,lab] of sequence.entries()) {
    const $=readPage('/courses/'+lab.id+'/');
    assert.equal($('a[rel="prev"]').attr('href'),i?'/courses/'+sequence[i-1].id+'/':url,'Previous: '+lab.id);
    assert.equal($('a[rel="next"]').attr('href'),sequence[i+1]?'/courses/'+sequence[i+1].id+'/':undefined,'Next: '+lab.id);
    labs++;
  }
}
for (const doc of docs) {
  const $=readPage('/courses/'+doc.id+'/');
  const prefix='/courses/'+doc.id.split('/')[0]+'/';
  for(const a of $('nav.sidebar a[href]').toArray()) {
    const href = $(a).attr('href');
    if (href.startsWith('/courses/')) assert.ok(href.startsWith(prefix),'Mixed-language sidebar: '+doc.id);
  }
}
const search = JSON.parse(fs.readFileSync('dist/pagefind/pagefind-entry.json','utf8'));
for (const locale of ['bg','en']) {
  const count = docs.filter(doc=>doc.id.split('/')[0]===locale && doc.data.pagefind!==false).length;
  assert.equal(search.languages[locale].page_count,count,`Search coverage for ${locale}`);
}
const ids = new Set(docs.map(doc=>doc.id));
for (const doc of docs.filter(doc=>doc.id.startsWith('bg/'))) {
  const englishId=doc.id.replace(/^bg\//,'en/');
  if (ids.has(englishId)) continue;
  const $=readPage('/courses/'+englishId+'/');
  assert.ok($('meta[http-equiv="refresh"]').length,'Untranslated route must redirect: '+englishId);
  assert.equal($('[data-pagefind-body]').length,0,'Fallback content must not be indexed: '+englishId);
}
console.log(`PASS: ${routes.length} migrated public pages and legacy redirects, ${assets.length} byte-identical source/build assets, ${labs} lab sequences, and both language search indexes.`);
