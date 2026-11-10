import fs from 'node:fs';
import { splitGeneratedLab, taskDocument } from './lib/lab-tasks.mjs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const root = fileURLToPath(new URL('../', import.meta.url));
const sourceRoot = path.join(root, 'course-materials', 'network-programming-java');
const targetRoot = path.join(root, 'src/content/docs/bg/programirane-v-mrezhova-sreda');
const folders = [
  'lab01-tcp-protocol',
  'lab02-concurrent-server',
  'lab03-reliable-udp',
  'lab04-java-nio',
  'lab05-backpressure',
  'lab06-tls-security',
  'lab07-performance',
];
const check = process.argv.includes('--check');
let stale = 0;

function syncPage(target, expected) {
  const existing = fs.existsSync(target) ? fs.readFileSync(target, 'utf8').replaceAll('\r\n', '\n') : '';
  if (existing === expected) return;
  if (check) {
    console.error(`Outdated student page: ${path.relative(root, target)}`);
    stale++;
  } else {
    fs.mkdirSync(path.dirname(target), { recursive: true });
    fs.writeFileSync(target, expected, 'utf8');
  }
}

// Publish only the named student handout and labs.
const projectRoute = 'obsht-proekt-chat';
let project = fs.readFileSync(path.join(sourceRoot, 'chat-project.md'), 'utf8').replaceAll('\r\n', '\n').trimEnd();
const projectTitle = project.match(/^# (.+)\n/)?.[1];
if (!projectTitle) throw new Error('Missing H1 in chat-project.md');

for (const [index, folder] of folders.entries()) {
  const number = index + 1;
  const sourceName = `lab${String(number).padStart(2, '0')}.md`;
  const source = path.join(sourceRoot, folder, sourceName);
  const body = fs.readFileSync(source, 'utf8').replaceAll('\r\n', '\n').trimEnd();
  const title = body.match(/^# (.+)\n/)?.[1];
  if (!title) throw new Error(`Missing H1 in ${source}`);
  const { theory, tasks } = splitGeneratedLab(body);
  const content = `---\ntitle: ${JSON.stringify(title)}\nsidebar:\n  order: ${number}\n  label: Упражнение ${number}\n---\n\n${theory.trimEnd()}\n`;
  const labRoute = `laboratorno-uprazhnenie-${number}`;
  const target = path.join(targetRoot, labRoute);
  const projectLink = `[Общ проект — чат, етап ${number}](../../${projectRoute}/#lab-${number})`;
  syncPage(path.join(target, 'index.md'), content);
  syncPage(path.join(target, 'zadachi.md'), taskDocument(`${projectLink}\n\n${tasks}`));
  project = project.replaceAll(`](${folder}/${sourceName})`, `](../${labRoute}/zadachi/)`);
}

syncPage(path.join(targetRoot, projectRoute, 'index.md'),
  `---\ntitle: ${JSON.stringify(projectTitle)}\nsidebar:\n  label: Общ проект — чат\n---\n\n${project}\n`);

if (stale) {
  console.error('Run node scripts/sync-network-labs.mjs and commit the updated student pages.');
  process.exitCode = 1;
} else {
  console.log(`${check ? 'Verified' : 'Synchronized'} ${folders.length} student labs and the chat project.`);
}
