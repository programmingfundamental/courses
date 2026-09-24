import fs from 'node:fs';
import assert from 'node:assert/strict';
import matter from 'gray-matter';
import { assignmentHeading, parse, plainText } from './lib/lab-tasks.mjs';

const root = 'src/content/docs';
const docs = fs.readdirSync(root, { recursive: true }).filter((file) => file.endsWith('.md')).map((file) => ({
  id: file.replaceAll('\\', '/').replace(/(?:\/index)?\.md$/, '').toLowerCase().replaceAll(' ', '-'),
  ...matter(fs.readFileSync(`${root}/${file}`, 'utf8')),
})).filter((doc) => !doc.data.draft);
const labs = docs.filter((doc) => !doc.data.contentRedirect && /^(bg|en)\/[^/]+\/(?:laboratorno-uprazhnenie-|laboratory-exercise-|lab)\d+$/.test(doc.id));
for (const lab of labs) {
  const children = docs.filter((doc) => doc.id.startsWith(lab.id + '/'));
  const tasks = children.filter((doc) => doc.data.taskPage);
  assert.equal(tasks.length, 1, `Expected one task page: ${lab.id}`);
  const task = tasks[0];
  const title = lab.id.startsWith('bg/') ? 'Задачи' : 'Tasks';
  assert.equal(task.id.split('/').length, 4, `Task page must be a direct lab child: ${task.id}`);
  assert.equal(task.data.title, title, task.id);
  assert.equal(task.data.sidebar?.label, title, task.id);
  assert.ok(!task.data.sidebar?.hidden, `Task page must be visible: ${task.id}`);
  for (const doc of [lab, ...children.filter((doc) => !doc.data.taskPage && !doc.data.taskRedirect)]) {
    // Worked examples belong in the teaching material, not student assignments.
    if (/\/(?:primerna-zadacha|example(?:-task)?|.*upravlenie-na-danni)$/.test(doc.id)) continue;
    for (const node of parse(doc.content).children.filter((node) => node.type === 'heading')) {
      assert.ok(!assignmentHeading(plainText(node)), `Assignment left in teaching material: ${doc.id}: ${plainText(node)}`);
    }
  }
}
for (const doc of docs.filter((doc) => doc.data.taskRedirect)) {
  assert.ok(docs.some((target) => target.data.taskPage && `/courses/${target.id}/` === doc.data.taskRedirect), `Missing task redirect target: ${doc.id}`);
  assert.equal(doc.data.pagefind, false, doc.id);
  assert.equal(doc.data.sidebar?.hidden, true, doc.id);
}
for (const doc of docs.filter((doc) => doc.data.contentRedirect)) {
  assert.ok(docs.some((target) => !target.data.contentRedirect && `/courses/${target.id}/` === doc.data.contentRedirect), `Missing current student material: ${doc.id}`);
  assert.equal(doc.data.pagefind, false, doc.id);
  assert.equal(doc.data.sidebar?.hidden, true, doc.id);
}
console.log(`PASS: ${labs.length} labs each have one visible task page; no assignment headings remain in teaching material.`);
