# 1. Упражнение 3 — Софтуерна архитектура и архитектурни стилове

**Аудитория:** IV курс, бакалавър „Изкуствен интелект“. **Време:** 110 минути.
Работи се само с предоставения CPU проект и synthetic dataset, без платени услуги.

## 2. Инженерен сценарий

Монолитният Python script обучава модел при import и после стартира API. Един restart на serving води до ново обучение; промяна в dataset променя production поведението без release.

## 3. Учебни цели

След упражнението студентът:

- анализира runtime coupling;
- проектира layered и pipeline архитектура;
- разделя batch training и online inference чрез архитектурен contract;
- аргументира embedded/service trade-offs;
- тества import и artifact boundaries;
- измерва operational последствия от design решение;

## 4. Предварителни знания

Python, основи на ML/Jupyter, Git, REST API, Docker, scikit-learn/pandas/numpy, Linux и бази данни. Използвайте резултатите от предходните 2 упражнения като engineering input. Не преговаряме елементарни Python конструкции.

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

**Фокус в това упражнение:** Data → offline training → immutable bundle → serving boundary. Вижте [общата архитектура](../architecture/system-overview.md). Отбележете данните, зависимостите и owner на всяка граница.

## 7. Теоретична подготовка

Architecture описва отговорности, dependencies, deployment units и качествени компромиси. Layered architecture разделя API/application/domain/infrastructure; pipeline architecture описва последователни transformation stages. Client–server отделя потребител от услуга, а service-oriented design поставя network boundaries между capabilities. Microservices са operational избор, не синоним на модулност.

Coupling е зависимост между компоненти; cohesion — доколко една отговорност е събрана на едно място. Batch inference обработва набори извън интерактивен request, online inference има latency/availability contract. Embedded model има по-малка network сложност; separate inference service позволява независимо scaling/version lifecycle, но добавя timeout, serialization, auth и observability нужди. Model artifact е versioned boundary между training и serving.

Следвайте [източниците и version scope](../architecture/references.md). Теорията трябва да обяснява engineering избора, не да замества evidence.

## 8. Лош / проблемен пример

```python
# module import:
data = load_data()
model = train(data)
app = FastAPI()
# всеки serving restart зависи от training data и training cost
```

Работещият starter и неговият TODO contract са в [starter/README.md](starter/README.md). Примерът е за анализ: първо запишете observable behavior и failure risks, после refactor-вайте. Не броим просто преименуване на файлове за архитектурна промяна.

## 9. Водена практическа задача

Командите за Python/pytest са от `ai-platform` при активирана среда. Процесът е **проблем → теория → анализ на лошо решение → практическа задача → самостоятелна задача → тестове → инженерна дискусия**.

### Стъпка 1

Изпълнете monolith.py --smoke. Прочетете top-level code и опишете какви side effects се случват преди първия request. Не добавяйте още sklearn tuning.

### Стъпка 2

Начертайте component и deployment diagrams отделно. Отбележете data ownership, training-only dependencies и model handoff. Използвайте SRS latency/availability constraints като аргумент.

### Стъпка 3

Дефинирайте artifact contract: version, feature schema/order, model hash, runtime compatibility, dataset/code/config lineage. Опишете поведение при missing/corrupted model.

### Стъпка 4

Планирайте миграция в два increments, така че service да продължи да има working demo. Сравнете предложението с ai-platform modules; проследете create_app/lifespan и offline cli train.

### Стъпка 5

Добавете architecture test, който import-ва API без dataset достъп и потвърждава, че train не е извикан. Проверете missing model: live200, ready503. Напишете ADR за избрания serving boundary.

Време: сценарий/теория15, анализ10, guided работа30, checkpoint5, самостоятелна работа25, tests15, дискусия10 минути — общо110. При 90 минути преподавателят подготвя environment и baseline evidence предварително.

## 10. Checkpoint

Training и serving могат да се стартират отделно; architecture decision има quality-attribute аргумент и artifact/error contract.

Покажете working increment и кратък before/after diff. Ако има failure, класифицирайте го като environment, contract, quality или implementation проблем. Не преминавайте нататък само заради един green happy-path test.

## 11. Самостоятелна задача

**Problem statement:** екип от двама разработчици обслужва 20 requests/sec на един CPU host, сменя модела веднъж седмично и няма дежурен infrastructure екип.

**Requirements:** сравнете embedded model и separate inference service по latency, scaling, reliability, deployment, security и complexity; изберете вариант с ADR.

**Constraints:** няма GPU или cloud managed serving; не обявявайте microservices за задължителни.

**Acceptance criteria:** поне 6 сравними измерения, workload assumptions, rejected alternative, failure behavior и migration trigger; един автоматизиран boundary/contract test подкрепя решението.

Предайте собствена реализация/спецификация, rationale и evidence. Пълно решение не е включено тук; готовият общ проект е reference baseline за сравнение на contracts, а starter TODO задачите изискват ваш diff и допълнителни проверки.

## 12. Automated tests

Начални runnable проверки:

```bash
pytest tests/test_api.py -q
python ../lab03-architecture/starter/monolith.py --smoke
```

Новите tests трябва да проверяват observable contract, negative/edge behavior и разрешения нормален flow. За документните задачи автоматизирайте структурните invariants, а смисъла проверете с peer review. За statistical/performance проверки запишете dataset/workload/seed/version/sample count; не твърдете универсална гаранция от малка synthetic извадка.

Добавете test/evidence traceability: **requirement ID → test name → command → actual result → limitation**. Поне една собствена проверка трябва да открива deliberate bad fixture или regression. След restore повторете suite; не променяйте assertions, за да прикриете failure.

## 13. Edge cases

- Serving restart няма достъп до training dataset.
- Artifact е нов, но feature order е стар.
- Service division увеличава latency повече от model compute.
- Нова model version е promoted, но вече стартираният API е pinned към старата.

Изберете поне един за нов автоматизиран test; за останалите опишете expected behavior и owner.

## 14. Въпроси за анализ

1. Каква е разликата module/service?
2. Защо serving не обучава при request?
3. Защо artifact contract включва schema?
4. Кога separate inference е оправдан?
5. Какво съдържа ADR?
6. Защо promotion не е deployment?

## 15. Очакван резултат

Завършен engineering increment по **Софтуерна архитектура и архитектурни стилове**, checkpoint evidence, самостоятелната задача според acceptance criteria и нови automated checks. Предайте decision/trade-off analysis, а не само screenshot или model score. Данните, моделът и test environment трябва да са идентифицируеми.

## 16. Checklist

- [ ] Анализирах проблемния starter и записах failure scenario.
- [ ] Избрах архитектурно/процесно решение с trade-offs.
- [ ] Реализирах guided increment и checkpoint.
- [ ] Самостоятелната задача е различна и покрива acceptance criteria.
- [ ] Tests покриват negative/edge и positive behavior.
- [ ] Evidence включва data/model/code/environment identity.
- [ ] Не включих реални secrets/PII или платени external dependencies.
- [ ] Описах limitations, technical debt и следваща стъпка.
