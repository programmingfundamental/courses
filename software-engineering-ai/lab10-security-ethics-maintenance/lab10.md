# 1. Упражнение 10 — Сигурност, етика, технически дълг и поддръжка

**Аудитория:** IV курс, бакалавър „Изкуствен интелект“. **Време:** 110 минути.
Работи се само с предоставения CPU проект и synthetic dataset, без платени услуги.

## 2. Инженерен сценарий

Review открива secret в source, стар model artifact без version, недокументиран dataset, sensitive logs и deprecated dependency. Високият F1 не отговаря дали системата е безопасна, поддържаема или подходяща за употреба.

## 3. Учебни цели

След упражнението студентът:

- анализира trust boundaries и privacy risks;
- проектира risk/technical-debt register;
- реализира проверими security/ethics constraints;
- тества access control и log redaction;
- аргументира fairness/explainability ограничения;
- планира maintenance, deprecation и retirement;

## 4. Предварителни знания

Python, основи на ML/Jupyter, Git, REST API, Docker, scikit-learn/pandas/numpy, Linux и бази данни. Използвайте резултатите от предходните 9 упражнения като engineering input. Не преговаряме елементарни Python конструкции.

## 5. Инструменти

Python 3.12, virtual environment, Jupyter Notebook по избор за notebook UI, pytest/coverage, Git, Docker, FastAPI/Pydantic и стандартните Python logging/JSON инструменти. Използвайте pinned environment от [README](../README.md). Training е върху 400 synthetic rows на CPU. Tracking/data versioning са local journal + Git/SHA manifest; не е необходим cloud account.

## 6. Архитектурен контекст

```text
Dataset + manifest
       |
Validation / Features
       |
Offline Training Pipeline
       |
Experiment metadata + immutable Model Registry
       |
Inference Service -> FastAPI -> Client
       |
Logs / Metrics -> CI/CD and maintenance feedback
```

**Фокус в това упражнение:** API/model/data trust boundaries → risk/debt register → maintenance/retirement. Вижте [общата архитектура](../architecture/system-overview.md). Отбележете данните, зависимостите и owner на всяка граница.

## 7. Теоретична подготовка

Secure coding включва input validation, server-side access control, bounded requests, trusted artifacts и secret handling. Pickle/joblib loading може да изпълни код; checksum не прави непознат artifact безопасен. API key е учебен access mechanism, не пълна identity/authorization система. Sensitive inputs не се логват; retention и deletion трябва да имат policy и owner.

Responsible AI изисква intended use, limitations, data provenance, fairness/explainability reasoning и human oversight според риска. Synthetic cohort A/B показва mechanics на slice analysis, не сертифицира fairness. Technical debt включва code/dependency debt, model debt (неясен lifecycle/validation) и data debt (липсващ provenance/quality ownership). Deprecation има срок, migration path и измерване на usage; retirement включва отказ на стар model, запазване/изтриване на artifacts според policy и комуникация с users.

Следвайте [източниците и version scope](../architecture/references.md). Теорията трябва да обяснява engineering избора, не да замества evidence.

## 8. Лош / проблемен пример

```text
API_KEY = "SYNTHETIC-NOT-A-REAL-SECRET"
model-final-final.joblib; version = null
log: {"email": "student@example.invalid", "input": "..."}
# non-executable review fixture; няма реални credentials
```

Работещият starter и неговият TODO contract са в [starter/README.md](starter/README.md). Примерът е за анализ: първо запишете observable behavior и failure risks, после refactor-вайте. Не броим просто преименуване на файлове за архитектурна промяна.

## 9. Водена практическа задача

Командите за Python/pytest са от `ai-platform` при активирана среда. Процесът е **проблем → теория → анализ на лошо решение → практическа задача → самостоятелна задача → тестове → инженерна дискусия**.

### Стъпка 1

Прочетете starter/review.json; това е inert fixture, не реален compromised service. Класифицирайте поне6 problems като security/privacy/dependency/model/data debt.

### Стъпка 2

Проследете app trust boundaries: API key, schema, model repository, filesystem permissions, CI secrets. Обяснете защо joblib не трябва да приема uploads от users и защо SHA проверката е само integrity срещу случайна промяна при trusted metadata.

### Стъпка 3

Създайте findings с Risk, Impact, Technical Debt, Mitigation, Priority и Owner. За всеки добавете acceptance evidence, due date и residual risk. Не измисляйте CVE за фиктивния deprecated adapter.

### Стъпка 4

Реализирайте поне два controls/tests: например fail-closed missing key, sensitive input не се echo-ва/log-ва, stale model metadata gate. Сравнете с baseline и покажете red→green за собствената поправка.

### Стъпка 5

Напишете кратък model/data card: intended use, prohibited use, dataset origin, evaluation protocol, slice support, limitations, retention и retirement owner. Превърнете поне едно ethical constraint в автоматична проверка и обяснете какво остава за human review.

Време: сценарий/теория15, анализ10, guided работа30, checkpoint5, самостоятелна работа25, tests15, дискусия10 минути — общо110. При 90 минути преподавателят подготвя environment и baseline evidence предварително.

## 10. Checkpoint

Има приоритизиран risk/debt register и доказани controls; model/data limitations и maintenance ownership са explicit.

Покажете working increment и кратък before/after diff. Ако има failure, класифицирайте го като environment, contract, quality или implementation проблем. Не преминавайте нататък само заради един green happy-path test.

## 11. Самостоятелна задача

**Problem statement:** планирайте поддръжката и retirement на версия, която вече не покрива product requirements.

**Requirements:** минимум 5 проблема с Risk/Impact/Technical Debt/Mitigation/Priority/Owner; добавете deprecation deadline, migration path, artifact/data retention и rollback boundary.

**Constraints:** не използвайте реални PII/secrets; не твърдете fairness само от synthetic accuracy; не изтривайте незаменими audit/reproducibility evidence без policy.

**Acceptance criteria:** поне2 automated acceptance checks, всеки проблем има owner и измерим completion criterion; retirement test показва, че retired version не се deploy-ва, докато approved version работи. Документирайте human-review decisions.

Предайте собствена реализация/спецификация, rationale и evidence. Пълно решение не е включено тук; готовият общ проект е reference baseline за сравнение на contracts, а starter TODO задачите изискват ваш diff и допълнителни проверки.

## 12. Automated tests

Начални runnable проверки:

```bash
pytest tests/test_api.py tests/test_repository.py -q
python -m pip check
```

Новите tests трябва да проверяват observable contract, negative/edge behavior и разрешения нормален flow. За документните задачи автоматизирайте структурните invariants, а смисъла проверете с peer review. За statistical/performance проверки запишете dataset/workload/seed/version/sample count; не твърдете универсална гаранция от малка synthetic извадка.

Добавете test/evidence traceability: **requirement ID → test name → command → actual result → limitation**. Поне една собствена проверка трябва да открива deliberate bad fixture или regression. След restore повторете suite; не променяйте assertions, за да прикриете failure.

## 13. Edge cases

- Secret е махнат от последния commit, но остава в Git history.
- Artifact е стар, но дата сама по себе си не доказва непригодност.
- Slice има твърде малко positive labels за надежден recall.
- Rollback връща версия, която вече е забранена по policy.

Изберете поне един за нов автоматизиран test; за останалите опишете expected behavior и owner.

## 14. Въпроси за анализ

1. Защо checksum не обезопасява pickle?
2. Какво следва след изтекъл real secret?
3. Как ethical requirement става проверим?
4. Защо възрастта не е единствен retirement критерий?
5. Как измерваме debt?
6. Кога rollback е забранен?

## 15. Очакван резултат

Завършен engineering increment по **Сигурност, етика, технически дълг и поддръжка**, checkpoint evidence, самостоятелната задача според acceptance criteria и нови automated checks. Предайте decision/trade-off analysis, а не само screenshot или model score. Данните, моделът и test environment трябва да са идентифицируеми.

## 16. Checklist

- [ ] Анализирах проблемния starter и записах failure scenario.
- [ ] Избрах архитектурно/процесно решение с trade-offs.
- [ ] Реализирах guided increment и checkpoint.
- [ ] Самостоятелната задача е различна и покрива acceptance criteria.
- [ ] Tests покриват negative/edge и positive behavior.
- [ ] Evidence включва data/model/code/environment identity.
- [ ] Не включих реални secrets/PII или платени external dependencies.
- [ ] Описах limitations, technical debt и следваща стъпка.
