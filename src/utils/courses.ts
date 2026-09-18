import { getCollection, type CollectionEntry } from 'astro:content';

export type Doc = CollectionEntry<'docs'>;
export const base = '/courses/';
export const urlFor = (id: string) => `${base}${id}/`;
export const docs = (await getCollection('docs')).filter((doc) => !doc.data.draft);
export const byId = new Map(docs.map((doc) => [doc.id, doc]));
export const isLab = (part: string) => /^(?:laboratorno-uprazhnenie-|laboratory-exercise-|lab)\d+$/.test(part);
export const labNumber = (part: string) => Number(part.match(/\d+$/)?.[0] || 0);
export const order = (doc: Doc) => doc.data.sidebar.order ?? Number.MAX_SAFE_INTEGER;
export const compare = (a: Doc, b: Doc) => order(a) - order(b) || a.id.localeCompare(b.id, undefined, { numeric: true });
export const coursesFor = (locale: string) => docs.filter((doc) => doc.id.startsWith(locale + '/') && doc.id.split('/').length === 2).sort(compare);
export const childrenOf = (id: string) => docs.filter((doc) => doc.id.startsWith(id + '/') && doc.id.split('/').length === id.split('/').length + 1 && !doc.data.sidebar.hidden).sort(compare);

// Share the academic-year grouping and subject order across both languages
// and between the homepage catalog and sidebar navigation.
const academicYears = [
  ['uchebna-praktika-1'],
  [
    'obektno-orientirano-programirane-1-chast',
    'obektno-orientirano-programirane-1-chast-project',
    'obektno-orientirano-programirane-2-chast',
    'software-systems',
    'uchebna-praktika-2',
  ],
  ['obektno-orientirano-programirane-2-chast-project', 'internet-tekhnologii-2023'],
  [
    'programirane-za-mobilni-i-internet-ustroistva-kotlin',
    'uchebna-praktika-3',
    'ueb-sigurnost',
    'software-engineering',
  ],
];

export function courseGroupsFor(locale: string) {
  const groups = academicYears.map((subjects, index) => ({
    id: `year-${index + 1}`,
    label: locale === 'bg' ? `${index + 1} курс` : `Year ${index + 1}`,
    courses: subjects.flatMap((subject) => {
      const course = byId.get(`${locale}/${subject}`);
      return course ? [course] : [];
    }),
  })).filter((group) => group.courses.length > 0);
  // Keep newly added subjects discoverable until their year is assigned.
  const assigned = new Set(academicYears.flat());
  const unassigned = coursesFor(locale).filter((course) => !assigned.has(course.id.split('/')[1]!));
  if (unassigned.length) groups.push({
    id: 'other-courses',
    label: locale === 'bg' ? 'Други дисциплини' : 'Other courses',
    courses: unassigned,
  });
  return groups;
}

// The legacy language trees use both `lab6` and `laboratorno-uprazhnenie-06`.
// Match these known naming conventions, but do not guess translated lesson titles.
const translationKey = (id: string) => id.split('/').slice(1).map((part) => isLab(part) ? `lab${labNumber(part)}` : part).join('/');
export function translationFor(id: string, locale: string): Doc | undefined {
  return docs.find((doc) => doc.id.startsWith(locale + '/') && translationKey(doc.id) === translationKey(id));
}
export function languageUrl(id: string, locale: string) {
  const counterpart = translationFor(id, locale);
  if (counterpart) return urlFor(counterpart.id);
  const course = byId.get(`${locale}/${id.split('/')[1]}`);
  return urlFor(course?.id || locale);
}
