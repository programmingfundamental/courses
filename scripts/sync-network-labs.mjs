import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const root = fileURLToPath(new URL('../', import.meta.url));
const sourceRoot = path.join(root, 'network-programming-java');
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
  console.error('Run node scripts/sync-network-labs.mjs and commit the updated student pages.');
  process.exitCode = 1;
} else {
  console.log(`${check ? 'Verified' : 'Synchronized'} ${folders.length} student labs; instructor notes are excluded.`);
}
