// Editable, local SVG teaching examples. No remote renderer is required.
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const output = fileURLToPath(new URL('../public/diagrams/task-manager-uml/', import.meta.url));
fs.mkdirSync(output, { recursive: true });
const esc = value => String(value).replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;').replaceAll('"', '&quot;');
const text = (x, y, value, attrs = '') => `<text x="${x}" y="${y}" ${attrs}>${esc(value)}</text>`;
const rect = (x, y, w, h, attrs = '') => `<rect x="${x}" y="${y}" width="${w}" height="${h}" ${attrs}/>`;
const line = (x1, y1, x2, y2, attrs = '') => `<line x1="${x1}" y1="${y1}" x2="${x2}" y2="${y2}" ${attrs}/>`;
const arrow = (x1, y1, x2, y2, dashed = false) => line(x1, y1, x2, y2, `marker-end="url(#arrow)" ${dashed ? 'stroke-dasharray="7 5"' : ''}`);
const poly = (points, attrs = '') => `<polyline points="${points}" fill="none" ${attrs}/>`;
const circle = (x, y, r, attrs = '') => `<circle cx="${x}" cy="${y}" r="${r}" ${attrs}/>`;
const box = (x, y, w, h, title, details = [], attrs = '') => rect(x, y, w, h, attrs) + text(x + w / 2, y + 27, title, 'text-anchor="middle" font-weight="600"') + (details.length ? line(x, y + 40, x + w, y + 40) + details.map((v, i) => text(x + 14, y + 66 + 23 * i, v)).join('') : '');
const named = (x, y, w, h, stereotype, name) => rect(x, y, w, h) + text(x + w / 2, y + 26, stereotype, 'text-anchor="middle" font-size="14"') + text(x + w / 2, y + 53, name, 'text-anchor="middle" font-weight="600"');
const action = (x, y, w, label) => rect(x, y, w, 46, 'rx="12"') + text(x + w / 2, y + 29, label, 'text-anchor="middle"');
const initial = (x, y) => circle(x, y, 8, 'style="fill:#17324d"');
const final = (x, y) => circle(x, y, 13) + circle(x, y, 8, 'style="fill:#17324d"');
const diamond = (x, y) => `<polygon points="${x},${y - 22} ${x + 28},${y} ${x},${y + 22} ${x - 28},${y}"/>`;
const ref = (x, y, w, label) => rect(x, y, w, 50) + poly(`${x},${y + 19} ${x + 39},${y + 19} ${x + 49},${y + 9} ${x + 49},${y}`) + text(x + 6, y + 14, 'ref', 'font-size="12"') + text(x + w / 2, y + 35, label, 'text-anchor="middle"');
const save = (name, title, body, h = 420) => {
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="900" height="${h}" viewBox="0 0 900 ${h}" role="img" aria-labelledby="title desc">
<title id="title">${esc(title)} — Task Manager</title><desc id="desc">Учебен UML пример на планираната система; обяснение и нотация в тема 2.</desc>
<defs><marker id="arrow" markerWidth="10" markerHeight="10" refX="9" refY="5" orient="auto"><path d="M1 1 L9 5 L1 9" fill="none" stroke="#17324d" stroke-width="1.5"/></marker><marker id="solid" markerWidth="12" markerHeight="12" refX="10" refY="6" orient="auto"><path d="M1 1 L10 6 L1 11 Z" fill="#17324d"/></marker></defs>
<style>text{font-family:Arial,sans-serif;font-size:16px;fill:#17324d;stroke:none}rect,ellipse,circle,polygon{fill:#fff;stroke:#17324d;stroke-width:1.6}line,polyline,path{stroke:#17324d;stroke-width:1.6} .note{font-size:14px;fill:#425b73}</style>
<rect width="900" height="${h}" fill="#f5f8fc" stroke="none"/>
${text(30, 35, title, 'font-size="22" font-weight="700"')}
${body}
${text(30, h - 18, 'Task Manager · UML · учебен проект', 'class="note"')}
</svg>\n`;
  fs.writeFileSync(path.join(output, `${name}.svg`), svg);
};

save('class', 'Class — типове и асоциация',
  box(60, 90, 310, 220, 'Task', ['- id: Long', '- summary: String', '- status: TaskStatus', '- confirmedCategory: Category [0..1]']) +
  line(60, 242, 370, 242) + text(74, 278, '+ changeStatus(next): void') +
  box(575, 120, 250, 125, 'Report', ['- id: Long']) +
  line(370, 185, 575, 185) + text(389, 173, '1') + text(530, 173, '0..*') + text(444, 208, 'reports') +
  text(60, 356, 'status и confirmedCategory се добавят в седмица 4.', 'class="note"'));

save('object', 'Object — моментна снимка',
  box(60, 95, 320, 190, '', ['id = 42', 'summary = "Fix login validation"', 'status = IN_PROGRESS', 'confirmedCategory = BUG']) +
  text(220, 122, 'task42:Task', 'text-anchor="middle" text-decoration="underline"') +
  box(575, 120, 250, 115, '', ['id = 7']) + text(700, 147, 'report7:Report', 'text-anchor="middle" text-decoration="underline"') +
  line(380, 185, 575, 185) + text(435, 172, 'reports'));

const pkg = (x, y, label) => rect(x, y, 70, 20) + rect(x, y + 20, 225, 70) + text(x + 112, y + 62, label, 'text-anchor="middle"');
save('package', 'Package — посока на зависимостите',
  pkg(35, 90, 'controller') + pkg(335, 90, 'service') + pkg(635, 90, 'domain') + pkg(335, 270, 'ai.adapter') +
  arrow(260, 145, 335, 145, true) + arrow(560, 145, 635, 145, true) + arrow(447, 270, 447, 180, true));

save('component', 'Component — части и договори',
  named(55, 130, 235, 110, '«component»', 'Task API') + named(590, 130, 260, 110, '«component»', 'Category Service') +
  arrow(290, 185, 590, 185, true) + text(440, 161, 'CategorySuggester', 'text-anchor="middle"') +
  text(55, 320, 'Зависимостта сочи предоставящия договора компонент.', 'class="note"'));

save('composite-structure', 'Composite Structure — вътрешни части',
  rect(70, 80, 760, 260) + text(95, 110, 'CategoryFacade') + rect(62, 172, 16, 16) + text(38, 158, 'input') +
  box(160, 145, 240, 85, 'validator:Validator') + box(530, 145, 235, 85, 'predictor:Predictor') +
  line(78, 180, 160, 180) + line(400, 180, 530, 180) + text(430, 166, 'text'));

const node = (x, y, w, label, artifact) => rect(x, y, w, 175) + poly(`${x},${y} ${x + 15},${y - 15} ${x + w + 15},${y - 15} ${x + w + 15},${y + 160} ${x + w},${y + 175}`) + line(x + w, y, x + w + 15, y - 15) + text(x + w / 2, y + 30, '«executionEnvironment»', 'text-anchor="middle" font-size="14"') + text(x + w / 2, y + 56, label, 'text-anchor="middle" font-weight="600"') + named(x + 15, y + 85, w - 30, 67, '«artifact»', artifact);
save('deployment', 'Deployment — вариант с отделна AI услуга',
  node(35, 110, 235, 'JVM / Spring', 'task-manager.jar') + node(340, 110, 235, 'Python / WSGI', 'model-v1.joblib') + node(645, 110, 220, 'PostgreSQL', 'tasks schema') +
  line(270, 165, 340, 165) + text(279, 150, 'HTTP', 'font-size="13"') +
  poly('145,285 145,330 755,330 755,285') + text(448, 353, 'JDBC · вътрешна мрежа', 'text-anchor="middle" class="note"'));

save('profile', 'Profile — разширение на UML метаклас',
  rect(35, 75, 830, 270) + text(58, 105, '«profile» TaskManagerProfile') +
  box(70, 145, 310, 150, '«stereotype» ModelService', ['modelVersion: String', '{modelVersion not empty}']) +
  named(585, 165, 230, 100, '«metaclass»', 'Component') +
  line(380, 214, 585, 214, 'marker-end="url(#solid)"') + text(451, 195, 'extension'));

const actor = (x, y, label) => circle(x, y, 15) + line(x, y + 15, x, y + 65) + line(x - 30, y + 35, x + 30, y + 35) + line(x, y + 65, x - 25, y + 100) + line(x, y + 65, x + 25, y + 100) + text(x, y + 130, label, 'text-anchor="middle"');
const useCase = (x, y, label) => `<ellipse cx="${x}" cy="${y}" rx="155" ry="35"/>` + text(x, y + 6, label, 'text-anchor="middle"');
save('use-case', 'Use Case — цели на потребителя',
  rect(310, 65, 500, 300) + text(335, 95, 'Task Manager') + actor(135, 145, 'Потребител') +
  line(165, 180, 385, 140) + line(165, 180, 385, 230) + line(165, 180, 385, 320) +
  useCase(540, 140, 'Създаване на задача') + useCase(540, 230, 'Получаване на предложение') + useCase(540, 320, 'Потвърждаване на категория'));

save('activity', 'Activity — създаване на задача',
  initial(180, 80) + arrow(180, 89, 180, 120) + action(65, 120, 230, 'Проверка на входа') +
  arrow(180, 166, 180, 206) + diamond(180, 228) + arrow(208, 228, 490, 228) + text(330, 211, '[valid]') +
  action(490, 205, 260, 'Записване на задача') + arrow(180, 250, 180, 295) + text(192, 278, '[invalid]') +
  action(65, 295, 230, 'Показване на грешки') + arrow(295, 318, 620, 318) + arrow(620, 251, 620, 305) + final(620, 318));

save('state-machine', 'State Machine — жизнен цикъл на Task',
  initial(55, 160) + arrow(64, 160, 110, 160) + action(110, 137, 155, 'OPEN') + action(355, 137, 205, 'IN_PROGRESS') + action(660, 137, 155, 'DONE') +
  arrow(265, 160, 355, 160) + text(284, 143, 'start') + arrow(560, 160, 660, 160) + text(575, 143, 'complete') +
  poly('737,183 737,275 187,275 187,183', 'marker-end="url(#arrow)"') + text(408, 261, 'reopen') +
  text(110, 340, 'DONE допуска повторно отваряне и не е краен UML възел.', 'class="note"'));

let seq = '';
for (const [x, name] of [[150, 'client:UI'], [450, 'api:TaskAPI'], [750, 'ai:CategoryService']]) {
  seq += box(x - 105, 75, 210, 45, name) + line(x, 120, x, 345, 'stroke-dasharray="7 5"');
}
seq += rect(445, 152, 10, 165) + rect(745, 195, 10, 75) + line(150, 155, 445, 155, 'marker-end="url(#solid)"') + text(205, 145, 'suggest(taskId)') + line(455, 200, 745, 200, 'marker-end="url(#solid)"') + text(518, 190, 'suggest(text)') + arrow(745, 268, 455, 268, true) + text(502, 255, 'category, version') + arrow(445, 315, 150, 315, true) + text(220, 302, 'Suggestion');
save('sequence', 'Sequence — предложение без запис', seq);

save('communication', 'Communication — същият сценарий по връзки',
  box(45, 100, 220, 55, ':UI') + box(570, 100, 275, 55, ':TaskAPI') + box(570, 285, 275, 55, ':CategoryService') +
  line(265, 130, 570, 130) + arrow(295, 105, 530, 105) + text(305, 91, '1: suggest(taskId)') +
  line(707, 155, 707, 285) + arrow(745, 178, 745, 255) + text(486, 218, '1.1: suggest(text)') +
  text(45, 310, 'Номерата задават реда и вложеността.', 'class="note"'));

save('interaction-overview', 'Interaction Overview — общ процес',
  initial(220, 70) + arrow(220, 79, 220, 100) + ref(100, 100, 240, 'CreateTask') + arrow(220, 150, 220, 177) +
  diamond(220, 199) + arrow(248, 199, 520, 199) + text(346, 182, '[suggest]') + ref(520, 174, 240, 'SuggestCategory') +
  arrow(220, 221, 220, 273) + text(229, 258, '[manual]') + diamond(220, 295) +
  poly('640,224 640,295 248,295', 'marker-end="url(#arrow)"') + arrow(220, 317, 220, 340) +
  ref(100, 340, 240, 'ConfirmCategory') + arrow(220, 390, 220, 414) + final(220, 428), 485);

save('timing', 'Timing — договорен бюджет при отказ',
  text(35, 100, 'request:CategoryRequest', 'font-weight="600"') +
  text(55, 150, 'Fallback') + text(55, 215, 'Waiting') + text(55, 280, 'Idle') +
  line(190, 322, 840, 322, 'marker-end="url(#arrow)"') + text(800, 349, 't (ms)') +
  poly('190,275 270,275 270,210 730,210 730,145 830,145', 'stroke-width="3"') +
  line(270, 120, 270, 322, 'stroke-dasharray="4 5"') + line(730, 120, 730, 322, 'stroke-dasharray="4 5"') +
  text(264, 346, '0') + text(712, 346, '1500') + line(270, 118, 730, 118) +
  text(500, 105, '{waiting duration ≤ 1500 ms}', 'text-anchor="middle"') +
  text(190, 377, 'Учебна цел; проверява се с реално измерване.', 'class="note"'));

console.log(`Generated 14 UML examples in ${output}`);
