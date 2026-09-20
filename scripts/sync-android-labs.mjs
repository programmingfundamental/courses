import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const root = fileURLToPath(new URL('../', import.meta.url));
const sourceRoot = path.join(root, 'android-mobile-technologies');
const targetRoot = path.join(root, 'src/content/docs/bg/android-bazirani-tekhnologii-za-mobilni-ustroistva');
const folders = [
  'lab01-lifecycle-state',
  'lab02-architecture-persistence',
  'lab03-background-work',
  'lab04-sensors',
  'lab05-location',
  'lab06-bluetooth-le',
  'lab07-performance',
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
  const content = `---\ntitle: ${JSON.stringify(title)}\nsidebar:\n  order: ${number}\n  label: Упражнение ${number}\n---\n\n${body}\n`;
  const target = path.join(targetRoot, `laboratorno-uprazhnenie-${number}`, 'index.md');
  const existing = fs.existsSync(target) ? fs.readFileSync(target, 'utf8').replaceAll('\r\n', '\n') : '';
  if (existing === content) continue;
  if (check) {
    console.error(`Outdated student page: ${path.relative(root, target)}`);
    stale++;
  } else {
    fs.mkdirSync(path.dirname(target), { recursive: true });
    fs.writeFileSync(target, content, 'utf8');
  }
}

if (stale) {
  console.error('Run node scripts/sync-android-labs.mjs and commit the updated student pages.');
  process.exitCode = 1;
} else {
  console.log(`${check ? 'Verified' : 'Synchronized'} ${folders.length} student labs; instructor notes are excluded.`);
}
