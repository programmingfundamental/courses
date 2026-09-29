# Насоки за преподавателя — Упражнение 7 — Тестване на софтуер и AI компоненти

## Учебни цели

След упражнението студентът:

- проектира test pyramid за AI pipeline;
- реализира unit/integration/contract tests;
- тества data/schema/artifact failure paths;
- измерва model quality с фиксиран protocol;
- анализира deterministic и statistical guarantees;
- аргументира ограниченията на drift smoke test;

## Предварителни знания

Python, основи на ML/Jupyter, Git, REST API, Docker, scikit-learn/pandas/numpy, Linux и бази данни. Използвайте резултатите от предходните 6 упражнения като engineering input. Не преговаряме елементарни Python конструкции.

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

**Фокус в това упражнение:** Data contract → preprocessing → artifact → service → HTTP. Вижте [общата архитектура](../architecture/system-overview.md). Отбележете данните, зависимостите и owner на всяка граница.

## Checkpoint

Suite покрива preprocessing, model loading, HTTP/invalid input, quality/range/schema и drift smoke с ясни ограничения.

Покажете working increment и кратък before/after diff. Ако има failure, класифицирайте го като environment, contract, quality или implementation проблем. Не преминавайте нататък само заради един green happy-path test.

## Automated tests

Начални runnable проверки:

```bash
pytest -q --cov=ai_platform --cov-fail-under=85
pytest ../lab07-testing/starter/test_happy_path.py -q
```

Новите tests трябва да проверяват observable contract, negative/edge behavior и разрешения нормален flow. За документните задачи автоматизирайте структурните invariants, а смисъла проверете с peer review. За statistical/performance проверки запишете dataset/workload/seed/version/sample count; не твърдете универсална гаранция от малка synthetic извадка.

Добавете test/evidence traceability: **requirement ID → test name → command → actual result → limitation**. Поне една собствена проверка трябва да открива deliberate bad fixture или regression. След restore повторете suite; не променяйте assertions, за да прикриете failure.

## Въпроси за анализ

1. Защо mocking не доказва artifact compatibility?
2. Как се прави reproducible quality test?
3. Какво доказва drift smoke?
4. Защо coverage не е достатъчно?
5. Къде е E2E boundary?
6. Кога exact prediction assertion е риск?

## Очакван резултат

Завършен engineering increment по **Тестване на софтуер и AI компоненти**, checkpoint evidence, самостоятелната задача според acceptance criteria и нови automated checks. Предайте decision/trade-off analysis, а не само screenshot или model score. Данните, моделът и test environment трябва да са идентифицируеми.

## Checklist

- [ ] Анализирах проблемния starter и записах failure scenario.
- [ ] Избрах архитектурно/процесно решение с trade-offs.
- [ ] Реализирах guided increment и checkpoint.
- [ ] Самостоятелната задача е различна и покрива acceptance criteria.
- [ ] Tests покриват negative/edge и positive behavior.
- [ ] Evidence включва data/model/code/environment identity.
- [ ] Не включих реални secrets/PII или платени external dependencies.
- [ ] Описах limitations, technical debt и следваща стъпка.
