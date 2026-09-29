# Насоки за преподавателя — Упражнение 2 — Софтуерен жизнен цикъл и инженерни процеси

## Учебни цели

След упражнението студентът:

- анализира разликата research code/production software;
- идентифицира technical debt и рискови assumptions;
- проектира iterative lifecycle за AI проекта;
- аргументира milestones и приоритети;
- реализира проверим definition of done;
- автоматизира clean-run проверка на експеримента;

## Предварителни знания

Python, основи на ML/Jupyter, Git, REST API, Docker, scikit-learn/pandas/numpy, Linux и бази данни. ML алгоритмите са даденост; не се изисква избор или tuning на нов classifier. Не преговаряме елементарни Python конструкции.

## Инструменти

Python 3.12, virtual environment, Jupyter Notebook по избор за notebook UI, pytest/coverage, Git, Docker, FastAPI/Pydantic и стандартните Python logging/JSON инструменти. Използвайте pinned environment от [подготовка на проекта](../setup.md). Training е върху 400 synthetic rows на CPU. Tracking/data versioning са local journal + Git/SHA manifest; не е необходим cloud account.

## Архитектурен контекст

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

**Фокус в това упражнение:** Notebook → engineering backlog → reproducible pipeline. Вижте [общата архитектура](../architecture/system-overview.md). Отбележете данните, зависимостите и owner на всяка граница.

## Checkpoint

Notebook cells работят от чист контекст; backlog съдържа поне 8 проследими items и три milestones с measurable exit criteria.

Покажете working increment и кратък before/after diff. Ако има failure, класифицирайте го като environment, contract, quality или implementation проблем. Не преминавайте нататък само заради един green happy-path test.

## Automated tests

Начални runnable проверки:

```bash
python scripts/check_notebook.py
pytest tests/test_data.py -q
```

Новите tests трябва да проверяват observable contract, negative/edge behavior и разрешения нормален flow. За документните задачи автоматизирайте структурните invariants, а смисъла проверете с peer review. За statistical/performance проверки запишете dataset/workload/seed/version/sample count; не твърдете универсална гаранция от малка synthetic извадка.

Добавете test/evidence traceability: **requirement ID → test name → command → actual result → limitation**. Поне една собствена проверка трябва да открива deliberate bad fixture или regression. След restore повторете suite; не променяйте assertions, за да прикриете failure.

## Въпроси за анализ

1. Защо training score не е DoD?
2. Как lifecycle се различава от pipeline?
3. Кога technical debt е приемлив?
4. Кои identities са нужни за reproducibility?
5. Защо deployment не е краят?
6. Какво е vertical slice?

## Очакван резултат

Завършен engineering increment по **Софтуерен жизнен цикъл и инженерни процеси**, checkpoint evidence, самостоятелната задача според acceptance criteria и нови automated checks. Предайте decision/trade-off analysis, а не само screenshot или model score. Данните, моделът и test environment трябва да са идентифицируеми.

## Checklist

- [ ] Анализирах проблемния starter и записах failure scenario.
- [ ] Избрах архитектурно/процесно решение с trade-offs.
- [ ] Реализирах guided increment и checkpoint.
- [ ] Самостоятелната задача е различна и покрива acceptance criteria.
- [ ] Tests покриват negative/edge и positive behavior.
- [ ] Evidence включва data/model/code/environment identity.
- [ ] Не включих реални secrets/PII или платени external dependencies.
- [ ] Описах limitations, technical debt и следваща стъпка.
