import fs from 'node:fs';
import { splitGeneratedLab, taskDocument } from './lib/lab-tasks.mjs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const root = fileURLToPath(new URL('../', import.meta.url));
const sourceRoot = path.join(root, 'mobile-internet-course');
const targetRoot = path.join(root, 'src/content/docs/bg/internet-za-mobilni-ustroistva');
const folders = [
  'lab01-connectivity',
  'lab02-mobile-api-client',
  'lab03-quarkus-api',
  'lab04-microservices',
  'lab05-api-gateway',
  'lab06-bff',
  'lab07-resilience',
  'lab08-offline-sync',
  'lab09-realtime-events',
  'lab10-security-observability',
];
const check = process.argv.includes('--check');
let stale = 0;

for (const [index, folder] of folders.entries()) {
  const number = index + 1;
  const source = path.join(sourceRoot, folder, `lab${String(number).padStart(2, '0')}.md`);
  const body = fs.readFileSync(source, 'utf8').replaceAll('\r\n', '\n').trimEnd();
  const title = body.match(/^# (.+)\n/)?.[1];
  if (!title) throw new Error(`Missing H1 in ${source}`);
  // Only the explicit student-file allowlist is published. Instructor notes stay outside content.
  const { theory, tasks } = splitGeneratedLab(body);
  const content = `---\ntitle: ${JSON.stringify(title)}\nsidebar:\n  order: ${number}\n  label: Упражнение ${number}\n---\n\n${theory.trimEnd()}\n`;
  const target = path.join(targetRoot, `laboratorno-uprazhnenie-${number}`, 'index.md');
  for (const [page, expected] of [[target, content], [path.join(path.dirname(target), 'zadachi.md'), taskDocument(tasks)]]) {
    const existing = fs.existsSync(page) ? fs.readFileSync(page, 'utf8').replaceAll('\r\n', '\n') : '';
    if (existing === expected) continue;
    if (check) {
      console.error(`Outdated student page: ${path.relative(root, page)}`);
      stale++;
    } else {
      fs.mkdirSync(path.dirname(target), { recursive: true });
      fs.writeFileSync(page, expected, 'utf8');
    }
  }
}

if (stale) {
  console.error('Run node scripts/sync-mobile-internet-labs.mjs and commit the updated student pages.');
  process.exitCode = 1;
} else {
  console.log(`${check ? 'Verified' : 'Synchronized'} ${folders.length} student labs; instructor notes are excluded.`);
}
