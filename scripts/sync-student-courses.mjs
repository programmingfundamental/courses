import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { parse, plainText, splitRanges, taskDocument } from './lib/lab-tasks.mjs';

const root = fileURLToPath(new URL('../', import.meta.url));
const check = process.argv.includes('--check');
let stale = 0;
let pages = 0;
const courses = [
  { source: 'applied-web-security', slug: 'ueb-sigurnost', title: 'Уеб сигурност (Приложна)', order: 11,
    intro: 'Курсът разглежда сигурността на уеб приложения чрез практически упражнения с Java, Spring Security и Docker. Ще анализирате уязвимости, ще реализирате защити и ще проверявате решенията с автоматизирани тестове в локална лабораторна среда.' },
  { source: 'software-engineering-ai', slug: 'software-engineering', title: 'Софтуерно инженерство за AI системи', order: 12,
    intro: 'Курсът разглежда превръщането на AI експеримент в поддържана софтуерна система с Python. Ще изучавате изисквания, архитектура, тестване, CI/CD, MLOps, наблюдаемост, сигурност и поддръжка.' },
];
function emit(relative, content) {
  const file = path.join(root, relative);
  pages++;
  if (fs.existsSync(file) && fs.readFileSync(file, 'utf8').replaceAll('\r\n', '\n') === content) return;
  if (check) { console.error(`Outdated student material: ${relative}`); stale++; }
  else { fs.mkdirSync(path.dirname(file), { recursive: true }); fs.writeFileSync(file, content); }
}
const document = (title, body, sidebar = {}) => `---\ntitle: ${JSON.stringify(title)}\nsidebar:\n${Object.entries(sidebar).map(([key, value]) => `  ${key}: ${JSON.stringify(value)}\n`).join('')}---\n\n${body.trim()}\n`;

for (const course of courses) {
  const sourceRoot = path.join(root, course.source);
  const target = `src/content/docs/bg/${course.slug}`;
  const base = `/courses/bg/${course.slug}/`;
  const folders = fs.readdirSync(sourceRoot).filter((name) => /^lab\d{2}-/.test(name)).sort();
  if (folders.length !== 10) throw new Error(`Expected ten student labs in ${course.source}`);
  const mappings = new Map([['README.md', 'podgotovka']]);
  const allowedDirs = ['architecture', ...folders.flatMap((folder) => [`${folder}/resources`, `${folder}/starter`])];
  const studentFiles = allowedDirs.filter((dir) => fs.existsSync(path.join(sourceRoot, dir))).flatMap((dir) =>
    fs.readdirSync(path.join(sourceRoot, dir)).filter((file) => file.endsWith('.md')).map((file) => `${dir}/${file}`));
  if (course.source === 'software-engineering-ai') studentFiles.push('ai-platform/data/README.md');
  for (const file of studentFiles) mappings.set(file, `materiali/${file.replace(/\.md$/, '').toLowerCase()}`);
  for (const [i, folder] of folders.entries()) mappings.set(`${folder}/lab${String(i + 1).padStart(2, '0')}.md`, `laboratorno-uprazhnenie-${i + 1}`);
  function rewrite(body, source) {
    const edits = [];
    const visit = (node) => {
      if ((node.type === 'link' || node.type === 'definition' || node.type === 'image') && node.url && !/^(?:[a-z]+:|#|\/)/i.test(node.url)) {
        const [relative, hash] = node.url.split('#');
        const resolved = path.posix.normalize(path.posix.join(path.posix.dirname(source), relative));
        const absolute = path.resolve(sourceRoot, resolved);
        if (!fs.existsSync(absolute)) throw new Error(`Missing resource ${source}: ${node.url}`);
        if (/instructor-notes/.test(resolved)) throw new Error(`Instructor material referenced by student page: ${source}`);
        const url = mappings.has(resolved) ? `${base}${mappings.get(resolved)}/${hash ? '#' + hash : ''}`
          : `https://github.com/programmingfundamental/courses/blob/main/${path.posix.normalize(course.source + '/' + resolved)}${hash ? '#' + hash : ''}`;
        const start = node.position.start.offset;
        const end = node.position.end.offset;
        const original = body.slice(start, end);
        const offset = original.lastIndexOf(node.url);
        edits.push([start, end, original.slice(0, offset) + url + original.slice(offset + node.url.length)]);
      }
      for (const child of node.children ?? []) visit(child);
    };
    visit(parse(body));
    for (const [start, end, replacement] of edits.sort((a, b) => b[0] - a[0])) body = body.slice(0, start) + replacement + body.slice(end);
    return body;
  }
  const links = [];
  for (const [i, folder] of folders.entries()) {
    const n = i + 1;
    const file = `${folder}/lab${String(n).padStart(2, '0')}.md`;
    const original = fs.readFileSync(path.join(sourceRoot, file), 'utf8').replaceAll('\r\n', '\n');
    const heading = parse(original).children.find((node) => node.type === 'heading' && node.depth === 1);
    const topic = plainText(heading).replace(/^1\. Упражнение \d+ — /, '');
    const title = `Лабораторно упражнение ${n} — ${topic}`;
    const body = rewrite(original.replace(/^# .+/, '# ' + title), file);
    const firstTask = parse(body).children.find((node) => node.type === 'heading' && /^9\. Водена практическа задача/.test(plainText(node)));
    if (!firstTask) throw new Error(`Missing task boundary: ${file}`);
    const { theory, tasks } = splitRanges(body, [[firstTask.position.start.offset, body.length]]);
    emit(`${target}/laboratorno-uprazhnenie-${n}/index.md`, document(title, theory, { order: n, label: `Упражнение ${n}` }));
    emit(`${target}/laboratorno-uprazhnenie-${n}/zadachi.md`, taskDocument(tasks));
    links.push(`- [${title}](${base}laboratorno-uprazhnenie-${n}/)`);
  }
  emit(`${target}/index.md`, document(course.title, `${course.intro}\n\n## Лабораторни упражнения\n\n${links.join('\n')}`, { order: course.order }));
  for (const [file, route] of mappings) {
    if (/\/lab\d+\.md$/.test(file)) continue;
    let body = rewrite(fs.readFileSync(path.join(sourceRoot, file), 'utf8').replaceAll('\r\n', '\n'), file);
    const title = file === 'README.md' ? 'Подготовка и стартиране' : plainText(parse(body).children.find((node) => node.type === 'heading') ?? { value: file });
    if (file === 'README.md') body = `# Подготовка и стартиране\n\nИзтеглете [учебния проект](https://github.com/programmingfundamental/courses/tree/main/${course.source}) чрез Git:\n\n\`\`\`sh\ngit clone https://github.com/programmingfundamental/courses.git\ncd courses/${course.source}\n\`\`\`\n\n` + body.replace(/^# .+\n/, '');
    emit(`${target}/${route}.md`, document(title, body, { order: file === 'README.md' ? 0 : 100 }));
  }
}
if (stale) process.exitCode = 1;
else console.log(`${check ? 'Verified' : 'Synchronized'} ${pages} student pages for two courses; instructor notes excluded.`);
