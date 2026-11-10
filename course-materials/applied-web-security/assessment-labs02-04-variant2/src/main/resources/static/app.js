const $ = id => document.getElementById(id);
const state = { content: null, workspace: null, answers: {}, participant: '', current: 1, view: 'lab', busy: false, revision: 0, dirty: false };
let saveTimer, saveQueue = Promise.resolve();

function element(tag, text, className) {
  const node = document.createElement(tag);
  if (text !== undefined) node.textContent = text;
  if (className) node.className = className;
  return node;
}
function notice(message, error = false) {
  $('notice').textContent = message; $('notice').hidden = !message; $('notice').classList.toggle('error', error);
}
function redact(value) {
  if (Array.isArray(value)) return value.map(redact);
  if (value && typeof value === 'object') return Object.fromEntries(Object.entries(value).map(([key, item]) =>
    [key, /password|token|cookie|authorization|secret|sessionid/i.test(key) ? '[скрито]' : redact(item)]));
  return value;
}
async function jsonFetch(path, options = {}) {
  const response = await fetch(path, { credentials: 'same-origin', ...options });
  let data;
  try { data = await response.json(); } catch { throw new Error('Сървърът не върна JSON. Проверете връзката и опитайте отново.'); }
  return { response, data };
}
function progress() {
  const count = Object.values(state.answers).filter(value => value.trim()).length;
  $('progress').value = count; $('progress-text').textContent = `${count} / 5`;
  $('progress').textContent = `${count} от 5`;
  $('progress').setAttribute('aria-valuetext', `${count} от 5 попълнени отговора`);
  document.querySelectorAll('[data-case]').forEach(button => {
    const answered = Boolean(state.answers[button.dataset.case]?.trim());
    button.classList.toggle('answered', answered);
    button.classList.toggle('active', Number(button.dataset.case) === state.current);
    if (Number(button.dataset.case) === state.current) button.setAttribute('aria-current', 'step'); else button.removeAttribute('aria-current');
    button.querySelector('.case-check').textContent = answered ? '✓' : '›';
  });
}
function setView(view) {
  if (!['lab', 'prep', 'report'].includes(view)) view = 'lab';
  state.view = view;
  for (const name of ['lab', 'prep', 'report']) $(`${name}-view`).hidden = name !== view;
  document.querySelectorAll('.topbar [data-view]').forEach(button => {
    button.classList.toggle('active', button.dataset.view === view);
    if (button.dataset.view === view) button.setAttribute('aria-current', 'page'); else button.removeAttribute('aria-current');
  });
  if (view === 'report') renderReport();
  history.replaceState(null, '', `#${view}`);
}
function fillList(id, items) { $(id).replaceChildren(...items.map(text => element('li', text))); }
function renderContent() {
  const c = state.content;
  $('workspace-code').textContent = state.workspace.code;
  $('prep-intro').textContent = c.intro; $('prep-title').textContent = c.preparation.title;
  $('prep-text').textContent = c.preparation.text; $('submission').textContent = c.preparation.submission;
  fillList('prep-steps', c.preparation.steps);
  $('participant').value = state.participant;
  $('accounts-table').replaceChildren(...c.accounts.map(account => {
    const row = element('tr'); row.append(...[account.username, account.password, account.role].map(value => element('td', value))); return row;
  }));
  $('account-shortcuts').append(element('span', 'Попълни профил:', 'muted small'));
  c.accounts.forEach(account => {
    const button = element('button', account.username, 'account-button'); button.type = 'button';
    button.addEventListener('click', () => { $('login-user').value = account.username; $('login-password').value = account.password; });
    $('account-shortcuts').append(button);
  });
  $('case-nav').replaceChildren(...c.cases.map(item => {
    const button = element('button'); button.dataset.case = item.id;
    button.append(element('span', String(item.id).padStart(2, '0'), 'case-number'), element('span', item.short, 'case-name'), element('span', '›', 'case-check'));
    button.addEventListener('click', () => selectCase(item.id)); return button;
  }));
}
function loadPreset() {
  const c = state.content.cases[state.current - 1], preset = c.presets[Number($('preset').value)];
  $('method').value = preset.method; $('request-path').value = preset.path;
  $('request-body').value = preset.body ? JSON.stringify(preset.body, null, 2) : '{}';
  updateBody();
}
function updateBody() { $('body-label').hidden = $('method').value === 'GET'; }
async function selectCase(id) {
  if (state.busy) return;
  state.current = id; setView('lab');
  const c = state.content.cases[id - 1];
  $('case-source').textContent = `ПРОВЕРКА ${String(id).padStart(2, '0')} / 05 · ${c.source}`;
  $('case-title').textContent = c.title; $('case-description').textContent = c.description;
  fillList('case-steps', c.steps); $('question').textContent = c.question;
  $('answer').value = state.answers[id] || ''; $('answer-count').textContent = `${$('answer').value.length} / 5000`;
  $('preset').replaceChildren(...c.presets.map((p, i) => { const option = element('option', p.label); option.value = i; return option; }));
  loadPreset(); progress(); renderJournal();
  await runRequest('GET', '/auth/session');
}
async function refreshEvidence() {
  const { response, data } = await jsonFetch('/api/workspace');
  if (!response.ok) throw new Error(data.message || 'Дневникът не може да бъде прочетен.');
  if (data.code !== state.workspace.code) throw new Error('Работното място е сменено. Изтеглете запазените си данни и презаредете страницата.');
  state.workspace.evidence = data.evidence; state.workspace.runs = data.runs;
  renderJournal(); if (state.view === 'report') renderReport();
}
function setBusy(value) {
  state.busy = value;
  document.querySelectorAll('#login-form button, #request-form button, [data-case], #reset, #inspect-session').forEach(button => button.disabled = value);
  $('send').textContent = value ? 'Изпращане…' : 'Изпрати заявката →';
}
async function runRequest(method, path, body) {
  if (state.busy) return;
  const n = state.current;
  if (!/^\/[a-zA-Z0-9_/-]+(?:\?username=[a-z][a-z0-9_-]{2,31})?$/.test(path) || path.includes('..') || path.includes('//')) {
    notice('Въведете път в текущата проверка, например /tasks/101.', true); return;
  }
  setBusy(true); notice('');
  try {
    const options = { method };
    if (method !== 'GET') { options.headers = { 'Content-Type': 'application/json' }; options.body = JSON.stringify(body || {}); }
    const { response, data } = await jsonFetch(`/api/cases/${n}${path}`, options);
    const record = response.headers.get('X-Evidence-Id');
    $('response').textContent = JSON.stringify(redact(data), null, 2);
    $('response-status').textContent = `HTTP ${response.status}`;
    $('response-status').className = `status ${response.ok ? 'success' : 'refusal'}`;
    const before = response.headers.get('X-Lab-Session-Before') || 'none';
    const after = response.headers.get('X-Lab-Session-After') || 'none';
    $('response-meta').textContent = `${method} ${path}${record ? ` · запис #${record}` : ''} · сесия ${before} → ${after}`;
    $('identity').textContent = response.headers.get('X-Lab-Identity') || 'anonymous';
    $('session-label').textContent = after === 'none' ? '—' : after;
    if (response.status === 401 && !record) notice(data.message || 'Работното място не е достъпно. Презаредете страницата.', true);
    await refreshEvidence();
  } catch (error) { notice(error.message || 'Няма връзка със сървъра.', true); }
  finally { setBusy(false); }
}
function evidenceNode(entry) {
  const details = element('details', undefined, 'evidence-entry');
  const summary = element('summary');
  summary.append(element('span', `#${entry.id}`, 'entry-id'), element('span', `${entry.method} ${entry.path}`, 'entry-path'), element('span', String(entry.status), `status ${entry.status < 400 ? 'success' : 'refusal'}`));
  details.append(summary, element('p', `Опит ${entry.generation} · ${entry.identityBefore} → ${entry.identityAfter} · ${new Date(entry.at).toLocaleTimeString('bg-BG')}`, 'small muted'),
    element('p', `Сесия: ${entry.sessionBefore} → ${entry.sessionAfter}`, 'small'),
    element('pre', JSON.stringify({ authentication: entry.authentication, request: entry.request, response: entry.response }, null, 2)));
  return details;
}
function renderJournal() {
  const entries = state.workspace.evidence.filter(entry => entry.case === state.current);
  $('journal-count').textContent = `${entries.length} записа`;
  $('journal').replaceChildren(...entries.slice().reverse().map(evidenceNode));
  if (!entries.length) $('journal').append(element('p', 'Все още няма изпратени заявки.', 'empty'));
}
function changed() {
  state.revision++; state.dirty = true;
  $('save-status').textContent = 'Запазване…'; progress();
  clearTimeout(saveTimer); saveTimer = setTimeout(() => saveNow().catch(() => {}), 650);
}
function saveNow() {
  clearTimeout(saveTimer);
  if (!state.dirty) return saveQueue;
  const revision = state.revision, payload = { participant: state.participant, answers: { ...state.answers } };
  saveQueue = saveQueue.catch(() => {}).then(async () => {
    const { response, data } = await jsonFetch('/api/answers', { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
    if (!response.ok) throw new Error(data.message || 'Отговорът не е запазен.');
    if (revision === state.revision) { state.dirty = false; $('save-status').textContent = 'Запазено'; }
  }).catch(error => { $('save-status').textContent = 'Не е запазено'; notice(`${error.message} Отговорът остава в полето. Опитайте отново с „Подготви протокола“.`, true); throw error; });
  return saveQueue;
}
function renderReport() {
  const count = Object.values(state.answers).filter(answer => answer.trim()).length;
  $('report-summary').textContent = `Вариант 2 · Работно място ${state.workspace.code} · ${count} от 5 попълнени отговора · ${state.workspace.evidence.length} записа в дневника`;
  $('report-answers').replaceChildren(...state.content.cases.map(c => {
    const card = element('article', undefined, 'card report-answer');
    card.append(element('div', `ПРОВЕРКА ${c.id}`, 'eyebrow'), element('h2', c.title), element('p', c.question), element('p', state.answers[c.id] || 'Няма попълнен отговор.', 'answer-text'));
    const button = element('button', 'Към проверката →', 'text-button'); button.addEventListener('click', () => selectCase(c.id)); card.append(button); return card;
  }));
  $('report-evidence').replaceChildren(...state.workspace.evidence.map(evidenceNode));
}
async function exportReport() {
  if (state.busy) { notice('Изчакайте текущата заявка и опитайте отново.', true); return; }
  if (!state.participant.trim()) { notice('Попълнете име или факултетен номер за предаване.', true); $('participant').focus(); return; }
  $('export').disabled = true;
  try {
    await saveNow();
    const { response, data } = await jsonFetch('/api/export');
    if (!response.ok) throw new Error(data.message || 'Протоколът не може да бъде изтеглен.');
    let link = $('download-ready');
    if (!link) {
      link = element('a', 'Изтегли готовия JSON файл ↓', 'button quiet'); link.id = 'download-ready';
      link.href = '/api/export'; link.download = `assessment-variant2-${state.workspace.code}.json`;
      $('export').parentElement.append(link);
    }
    notice('Протоколът е запазен. Използвайте връзката „Изтегли готовия JSON файл“ и предайте получения файл по указания начин.');
  } catch (error) { notice(error.message, true); }
  finally { $('export').disabled = false; }
}

document.querySelectorAll('[data-view]').forEach(button => button.addEventListener('click', () => setView(button.dataset.view)));
$('login-form').addEventListener('submit', event => { event.preventDefault(); runRequest('POST', '/auth/login', { username: $('login-user').value, password: $('login-password').value }); });
$('logout').addEventListener('click', () => runRequest('POST', '/auth/logout', {}));
$('inspect-session').addEventListener('click', () => runRequest('GET', '/auth/session'));
$('preset').addEventListener('change', loadPreset); $('method').addEventListener('change', updateBody);
$('request-form').addEventListener('submit', event => {
  event.preventDefault(); let body;
  try { if ($('method').value !== 'GET') { body = JSON.parse($('request-body').value); if (!body || Array.isArray(body) || typeof body !== 'object') throw new Error(); } }
  catch { notice('JSON тялото трябва да е валиден обект, например {"summary":"Ново заглавие"}.', true); return; }
  runRequest($('method').value, $('request-path').value.trim(), body);
});
$('answer').addEventListener('input', () => { state.answers[state.current] = $('answer').value; $('answer-count').textContent = `${$('answer').value.length} / 5000`; changed(); });
$('participant').addEventListener('input', () => { state.participant = $('participant').value; changed(); });
$('reset').addEventListener('click', () => $('reset-dialog').showModal());
$('reset-dialog').addEventListener('close', async () => { if ($('reset-dialog').returnValue === 'reset') await runRequest('POST', '/reset', {}); });
$('export').addEventListener('click', exportReport);
$('print').addEventListener('click', async () => { try { await saveNow(); renderReport(); $('report-evidence').closest('details').open = true; document.querySelectorAll('#report-evidence details').forEach(entry => entry.open = true); window.print(); } catch { /* The save error is already displayed. */ } });
window.addEventListener('beforeunload', event => { if (state.dirty) { event.preventDefault(); event.returnValue = ''; } });
window.addEventListener('hashchange', () => setView(location.hash.slice(1)));

try {
  const [content, workspace] = await Promise.all([jsonFetch('/assignment.json'), jsonFetch('/api/workspace')]);
  if (!content.response.ok || !workspace.response.ok) throw new Error('Лабораторията не може да бъде заредена.');
  state.content = content.data; state.workspace = workspace.data; state.answers = workspace.data.answers; state.participant = workspace.data.participant;
  const view = location.hash.slice(1) || 'lab';
  renderContent(); await selectCase(1); setView(view);
} catch (error) { notice(error.message, true); $('case-title').textContent = 'Няма връзка с лабораторията'; }
