import { unified } from 'unified';
import remarkParse from 'remark-parse';

const parser = unified().use(remarkParse);
export const parse = (body) => parser.parse(body);
export const plainText = (node) => node.value ?? node.children?.map(plainText).join('') ?? '';
export const assignmentHeading = (text) => /^(?:(?:\d+[.)]|[A-Z]+[.)])\s*)?(?:(?:самостоятелн[а-я]*|практическ[а-я]*|допълнителн[а-я]*|основна лабораторна|водена практическа)\s+)?задач[аи](?:\s|:|$)/i.test(text)
  || /^(?:(?:\d+[.)])\s*)?(?:(?:additional|bonus|independent|individual|practical|OCP|SRP)\s+)?tasks?(?:\s|:|$)/i.test(text)
  || /^(Самостоятелна работа|Independent work|Practice)\s*:?$/i.test(text);

// Parse Markdown so headings inside code examples never become section boundaries.
export function splitSections(body, select) {
  const headings = parse(body).children.filter((node) => node.type === 'heading');
  const ranges = [];
  for (const [index, heading] of headings.entries()) {
    if (!select(plainText(heading), heading)) continue;
    const start = heading.position.start.offset;
    const end = headings.slice(index + 1).find((next) => next.depth <= heading.depth)?.position.start.offset ?? body.length;
    if (!ranges.some(([a, b]) => start >= a && end <= b)) ranges.push([start, end]);
  }
  return splitRanges(body, ranges);
}

export function splitRanges(body, ranges) {
  let cursor = 0;
  let theory = '';
  let tasks = '';
  for (const [start, end] of ranges.sort((a, b) => a[0] - b[0])) {
    if (start < cursor) throw new Error('Overlapping assignment sections');
    theory += body.slice(cursor, start);
    tasks += body.slice(start, end);
    cursor = end;
  }
  theory += body.slice(cursor);
  return { theory, tasks, ranges };
}

export function splitGeneratedLab(body) {
  const first = parse(body).children.find((node) => node.type === 'heading' && /^(6|7)\. (Начален|Мини) експеримент/.test(plainText(node)));
  if (!first) throw new Error('Missing practical section boundary in generated lab');
  return splitRanges(body, [[first.position.start.offset, body.length]]);
}

export function taskDocument(body, locale = 'bg') {
  const title = locale === 'bg' ? 'Задачи' : 'Tasks';
  return `---\ntitle: ${title}\ntaskPage: true\nsidebar:\n  label: ${title}\n  order: 100\n---\n\n${body.trim()}\n`;
}
