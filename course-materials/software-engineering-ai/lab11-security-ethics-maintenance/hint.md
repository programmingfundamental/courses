# Насоки за преподавателя — Упражнение 11 — Сигурност, етика, технически дълг и поддръжка

## Учебни цели

След упражнението студентът:

- анализира trust boundaries и privacy risks;
- проектира risk/technical-debt register;
- реализира проверими security/ethics constraints;
- тества access control и log redaction;
- аргументира fairness/explainability ограничения;
- планира maintenance, deprecation и retirement;

## Предварителни знания

Python, основи на ML/Jupyter, Git, REST API, Docker, scikit-learn/pandas/numpy, Linux и бази данни. Използвайте резултатите от предходните 10 упражнения като engineering input. Не преговаряме елементарни Python конструкции.

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

**Фокус в това упражнение:** API/model/data trust boundaries → risk/debt register → maintenance/retirement. Вижте [общата архитектура](../architecture/system-overview.md). Отбележете данните, зависимостите и owner на всяка граница.

## Checkpoint

Има приоритизиран risk/debt register и доказани controls; model/data limitations и maintenance ownership са explicit.

Покажете working increment и кратък before/after diff. Ако има failure, класифицирайте го като environment, contract, quality или implementation проблем. Не преминавайте нататък само заради един green happy-path test.

## Automated tests

Начални runnable проверки:

```bash
pytest tests/test_api.py tests/test_repository.py -q
python -m pip check
```

Новите tests трябва да проверяват observable contract, negative/edge behavior и разрешения нормален flow. За документните задачи автоматизирайте структурните invariants, а смисъла проверете с peer review. За statistical/performance проверки запишете dataset/workload/seed/version/sample count; не твърдете универсална гаранция от малка synthetic извадка.

Добавете test/evidence traceability: **requirement ID → test name → command → actual result → limitation**. Поне една собствена проверка трябва да открива deliberate bad fixture или regression. След restore повторете suite; не променяйте assertions, за да прикриете failure.

## Въпроси за анализ

1. Защо checksum не обезопасява pickle?
2. Какво следва след изтекъл real secret?
3. Как ethical requirement става проверим?
4. Защо възрастта не е единствен retirement критерий?
5. Как измерваме debt?
6. Кога rollback е забранен?

## Очакван резултат

Завършен engineering increment по **Сигурност, етика, технически дълг и поддръжка**, checkpoint evidence, самостоятелната задача според acceptance criteria и нови automated checks. Предайте decision/trade-off analysis, а не само screenshot или model score. Данните, моделът и test environment трябва да са идентифицируеми.

## Checklist

- [ ] Анализирах проблемния starter и записах failure scenario.
- [ ] Избрах архитектурно/процесно решение с trade-offs.
- [ ] Реализирах guided increment и checkpoint.
- [ ] Самостоятелната задача е различна и покрива acceptance criteria.
- [ ] Tests покриват negative/edge и positive behavior.
- [ ] Evidence включва data/model/code/environment identity.
- [ ] Не включих реални secrets/PII или платени external dependencies.
- [ ] Описах limitations, technical debt и следваща стъпка.
