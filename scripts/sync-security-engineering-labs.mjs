import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { parse, plainText, splitRanges, taskDocument } from './lib/lab-tasks.mjs';

const root = fileURLToPath(new URL('../', import.meta.url));
const check = process.argv.includes('--check');
const courses = [
  {
    id: 'applied-web-security',
    title: 'Уеб сигурност (Приложна)',
    english: 'Applied Web Security',
    topics: ['Introduction to Web Security', 'Lab Environment and Threat Modeling', 'Authentication with Spring Security', 'Authorization, Broken Access Control, and IDOR', 'Brute-Force Attacks and Authentication Protection', 'SQL Injection', 'Cross-Site Scripting (XSS)', 'CSRF, Cookies, and Browser Security', 'Cryptography and Sensitive Data Protection', 'JWT Security and Token Manipulation', 'Security Testing and Integrated Protection'],
  },
  {
    id: 'software-engineering-ai',
    title: 'Софтуерно инженерство за AI системи',
    english: 'Software Engineering for AI Systems',
    schedule: 'semester/schedule.json',
  },
];
let stale = 0;
function write(relative, expected) {
  const target = path.join(root, 'src/content/docs', relative);
  const existing = fs.existsSync(target) ? fs.readFileSync(target, 'utf8').replaceAll('\r\n', '\n') : '';
  if (existing === expected) return;
  if (check) {
    console.error(`Outdated student page: ${relative}`);
    stale++;
  } else {
    fs.mkdirSync(path.dirname(target), { recursive: true });
    fs.writeFileSync(target, expected);
  }
}
const document = (title, order, body = '', label) => `---\ntitle: ${JSON.stringify(title)}\nsidebar:\n  order: ${order}\n${label ? `  label: ${JSON.stringify(label)}\n` : ''}---\n${body ? `\n${body.trim()}\n` : ''}`;

for (const [index, course] of courses.entries()) {
  const sourceRoot = path.join(root, 'course-materials', course.id);
  // Only the top-level student labs are published; nested working copies,
  // application code, and instructor notes are not content sources.
  const schedule = course.schedule ? JSON.parse(fs.readFileSync(path.join(sourceRoot, course.schedule), 'utf8')) : null;
  if (schedule) course.topics = schedule.map(week => week.english);
  const labs = schedule ? schedule.map((week, index) => {
    if (week.number !== index + 1) throw new Error(`Unexpected week sequence in ${course.id}`);
    return { number: week.number, file: path.join(sourceRoot, week.file), route: `${course.id}/laboratorno-uprazhnenie-${week.number}`, aliases: week.aliases || [] };
  }) : fs.readdirSync(sourceRoot).filter(name => /^lab\d{2}-/.test(name)).sort().map((folder, index) => {
    const number = index + 1;
    if (!folder.startsWith(`lab${String(number).padStart(2, '0')}-`)) throw new Error(`Unexpected lab sequence: ${folder}`);
    return { number, file: path.join(sourceRoot, folder, `lab${String(number).padStart(2, '0')}.md`), route: `${course.id}/laboratorno-uprazhnenie-${number}` };
  });
  if (labs.length !== course.topics.length) throw new Error(`Expected ${course.topics.length} labs in ${course.id}`);
  // The schedule also defines which generated lab pages must no longer exist.
  if (schedule) {
    const activeFolders = new Set(labs.map(lab => path.basename(lab.route)));
    for (const locale of ['bg', 'en']) {
      const catalog = path.join(root, 'src/content/docs', locale, course.id);
      if (!fs.existsSync(catalog)) continue;
      for (const entry of fs.readdirSync(catalog, { withFileTypes: true })) {
        if (!entry.isDirectory() || !/^laboratorno-uprazhnenie-\d+$/.test(entry.name) || activeFolders.has(entry.name)) continue;
        const target = path.join(catalog, entry.name);
        const files = fs.readdirSync(target, { withFileTypes: true });
        if (files.some(file => !file.isFile() || !['index.md', 'zadachi.md'].includes(file.name))) {
          throw new Error(`Unexpected content in retired lab: ${target}`);
        }
        if (check) {
          console.error(`Retired student lab still published: ${locale}/${course.id}/${entry.name}`);
          stale++;
        } else {
          for (const file of files) fs.unlinkSync(path.join(target, file.name));
          fs.rmdirSync(target);
        }
      }
    }
  }
  const routes = new Map(labs.map(lab => [lab.file, `/courses/bg/${lab.route}/`]));
  for (const lab of labs) {
    for (const alias of lab.aliases || []) routes.set(path.join(sourceRoot, alias), `/courses/bg/${lab.route}/`);
  }
  routes.set(path.join(sourceRoot, 'README.md'), `/courses/bg/${course.id}/`);

  function readStudentFile(file) {
    const body = fs.readFileSync(file, 'utf8').replaceAll('\r\n', '\n');
    const edits = [];
    function visit(node) {
      if (['link', 'image', 'definition'].includes(node.type) && !/^(?:[a-z][a-z\d+.-]*:|\/|#)/i.test(node.url)) {
        const [relative, fragment] = node.url.split('#');
        const target = path.resolve(path.dirname(file), decodeURI(relative));
        if (['hint.md', 'instructor-notes.md'].includes(path.basename(target))) {
          throw new Error(`Instructor-only resource linked from student page: ${file}: ${node.url}`);
        }
        if (!fs.existsSync(target)) throw new Error(`Missing resource ${node.url} in ${file}`);
        const repoPath = path.relative(root, target).replaceAll('\\', '/');
        if (repoPath.startsWith('../')) throw new Error(`Resource outside repository: ${node.url}`);
        const start = node.position.start.offset;
        const end = node.position.end.offset;
        const route = routes.get(target);
        // Keep references to unpublished materials as text, without repository links.
        if (!route) {
          if (node.type !== 'link') throw new Error(`Unpublished resource must be an inline link: ${file}: ${node.url}`);
          edits.push([start, end, plainText(node)]);
          return;
        }
        const url = route + (fragment ? `#${fragment}` : '');
        const text = body.slice(start, end);
        const offset = node.type === 'definition' ? text.indexOf(node.url, text.indexOf(']:') + 2) : text.lastIndexOf(node.url);
        if (offset < 0) throw new Error(`Cannot rewrite link ${node.url}`);
        edits.push([start + offset, start + offset + node.url.length, url]);
      }
      for (const child of node.children || []) visit(child);
    }
    visit(parse(body));
    let result = body;
    for (const [start, end, url] of edits.sort((a, b) => b[0] - a[0])) result = result.slice(0, start) + url + result.slice(end);
    return result;
  }

  write(`bg/${course.id}/index.md`, document(course.title, 16 + index, readStudentFile(path.join(sourceRoot, 'README.md'))));
  const englishLinks = labs.map(lab => `- [Lab ${lab.number} — ${course.topics[lab.number - 1]}](/courses/en/${lab.route}/)`).join('\n');
  const overview = schedule ? 'This course covers the software lifecycle and Scrum teamwork for systems with AI components. As an AI engineer, you will extend Task Manager through requirements analysis, UML modeling, data preparation, model evaluation, deployment, and maintenance.' : `The course contains ${labs.length} labs on applied web security.`;
  write(`en/${course.id}/index.md`, document(course.english, 16 + index, `${overview} The teaching materials are available in Bulgarian via the language selector.\n\n## Labs\n\n${englishLinks}`));
  for (const lab of labs) {
    const body = readStudentFile(lab.file);
    // Publish downloadable Mermaid source from the same blocks that the lesson renders.
    // Only explicitly named examples are exported; teacher files are never scanned.
    if (course.id === 'software-engineering-ai') {
      for (const block of parse(body).children.filter(node => node.type === 'code' && node.lang === 'mermaid')) {
        const name = block.value.match(/^\s*%% file: ([a-z0-9-]+\.mmd)\s*$/m)?.[1];
        if (!name) continue;
        const target = path.join(root, 'public/diagrams/task-manager-mermaid', name);
        const expected = block.value.trim() + '\n';
        const existing = fs.existsSync(target) ? fs.readFileSync(target, 'utf8').replaceAll('\r\n', '\n') : '';
        if (existing === expected) continue;
        if (check) {
          console.error(`Outdated Mermaid source: ${name}`);
          stale++;
        } else {
          fs.mkdirSync(path.dirname(target), { recursive: true });
          fs.writeFileSync(target, expected);
        }
      }
    }
    const headings = parse(body).children.filter(node => node.type === 'heading');
    const title = plainText(headings[0]).replace(/^1\. /, '');
    const boundary = headings.find(node => node.depth === 2 && plainText(node) === 'Самостоятелни задачи');
    if (!boundary) throw new Error(`Missing practical section: ${lab.file}`);
    const { theory, tasks } = splitRanges(body, [[boundary.position.start.offset, body.length]]);
    write(`bg/${lab.route}/index.md`, document(title, lab.number, theory, `Упражнение ${lab.number}`));
    write(`bg/${lab.route}/zadachi.md`, taskDocument(tasks));
    write(`en/${lab.route}/index.md`, document(`Lab ${lab.number} — ${course.topics[lab.number - 1]}`, lab.number, '', `Lab ${lab.number}`));
    write(`en/${lab.route}/zadachi.md`, taskDocument('', 'en'));
  }
}
if (stale) process.exitCode = 1;
else console.log(`${check ? 'Verified' : 'Synchronized'} ${courses.reduce((sum, course) => sum + course.topics.length, 0)} security and software engineering labs in both language catalogs.`);
