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
    topics: ['Lab Environment and Threat Modeling', 'Authentication with Spring Security', 'Authorization, Broken Access Control, and IDOR', 'Brute-Force Attacks and Authentication Protection', 'SQL Injection', 'Cross-Site Scripting (XSS)', 'CSRF, Cookies, and Browser Security', 'Cryptography and Sensitive Data Protection', 'JWT Security and Token Manipulation', 'Security Testing and Integrated Protection'],
  },
  {
    id: 'software-engineering-ai',
    title: 'Софтуерно инженерство за AI системи',
    english: 'Software Engineering for AI Systems',
    topics: ['Software Lifecycle and Engineering Processes', 'Requirements and Specifications for AI-Based Systems', 'Software Architecture and Architectural Styles', 'Modularity, Layers, and Separation of Responsibilities', 'Design Patterns and Code Quality Principles', 'Testing Software and AI Components', 'Version Control, CI/CD, and Automation', 'MLOps and Model and Data Management', 'Observability, Reliability, and Error Handling', 'Security, Ethics, Technical Debt, and Maintenance'],
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
  // Only the ten top-level student labs are published; nested working copies,
  // application code, and instructor notes are not content sources.
  const labs = fs.readdirSync(sourceRoot).filter(name => /^lab\d{2}-/.test(name)).sort().map((folder, index) => {
    const number = index + 1;
    if (!folder.startsWith(`lab${String(number).padStart(2, '0')}-`)) throw new Error(`Unexpected lab sequence: ${folder}`);
    return { number, file: path.join(sourceRoot, folder, `lab${String(number).padStart(2, '0')}.md`), route: `${course.id}/laboratorno-uprazhnenie-${number}` };
  });
  if (labs.length !== course.topics.length) throw new Error(`Expected ten labs in ${course.id}`);
  const routes = new Map(labs.map(lab => [lab.file, `/courses/bg/${lab.route}/`]));
  routes.set(path.join(sourceRoot, 'README.md'), `/courses/bg/${course.id}/`);

  function readStudentFile(file) {
    const body = fs.readFileSync(file, 'utf8').replaceAll('\r\n', '\n');
    const edits = [];
    function visit(node) {
      if (['link', 'image', 'definition'].includes(node.type) && !/^(?:[a-z][a-z\d+.-]*:|\/|#)/i.test(node.url)) {
        const [relative, fragment] = node.url.split('#');
        const target = path.resolve(path.dirname(file), decodeURI(relative));
        if (!fs.existsSync(target)) throw new Error(`Missing resource ${node.url} in ${file}`);
        const repoPath = path.relative(root, target).replaceAll('\\', '/');
        if (repoPath.startsWith('../')) throw new Error(`Resource outside repository: ${node.url}`);
        const url = (routes.get(target) || `https://github.com/programmingfundamental/courses/blob/main/${repoPath.split('/').map(encodeURIComponent).join('/')}`) + (fragment ? `#${fragment}` : '');
        const start = node.position.start.offset;
        const end = node.position.end.offset;
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
  write(`en/${course.id}/index.md`, document(course.english, 16 + index, `The course contains ten labs. The teaching materials are currently available in Bulgarian. Use the language selector to open them.\n\n## Labs\n\n${englishLinks}`));
  for (const lab of labs) {
    const body = readStudentFile(lab.file);
    const headings = parse(body).children.filter(node => node.type === 'heading');
    const title = plainText(headings[0]).replace(/^1\. /, '');
    const boundary = headings.find(node => /^9\. Водена практическа задача/.test(plainText(node)));
    if (!boundary) throw new Error(`Missing practical section: ${lab.file}`);
    const { theory, tasks } = splitRanges(body, [[boundary.position.start.offset, body.length]]);
    write(`bg/${lab.route}/index.md`, document(title, lab.number, theory, `Упражнение ${lab.number}`));
    write(`bg/${lab.route}/zadachi.md`, taskDocument(tasks));
    write(`en/${lab.route}/index.md`, document(`Lab ${lab.number} — ${course.topics[lab.number - 1]}`, lab.number, '', `Lab ${lab.number}`));
    write(`en/${lab.route}/zadachi.md`, taskDocument('', 'en'));
  }
}
if (stale) process.exitCode = 1;
else console.log(`${check ? 'Verified' : 'Synchronized'} 20 security and software engineering labs in both language catalogs.`);
