# 1. Упражнение 6 — Тестване на софтуер и AI компоненти

**Аудитория:** IV курс, бакалавър „Изкуствен интелект“. **Време:** 110 минути.
Работи се само с предоставения CPU проект и synthetic dataset, без платени услуги.

## 2. Инженерен сценарий

API тестът проверява само дали отговорът е 200. Повреден artifact, missing feature и променен feature order остават незабелязани. В друг test качеството варира, защото dataset split се сменя при всяко изпълнение.

## 3. Учебни цели

След упражнението студентът:

- проектира test pyramid за AI pipeline;
- реализира unit/integration/contract tests;
- тества data/schema/artifact failure paths;
- измерва model quality с фиксиран protocol;
- анализира deterministic и statistical guarantees;
- аргументира ограниченията на drift smoke test;

## 4. Предварителни знания

Python, основи на ML/Jupyter, Git, REST API, Docker, scikit-learn/pandas/numpy, Linux и бази данни. Използвайте резултатите от предходните 5 упражнения като engineering input. Не преговаряме елементарни Python конструкции.

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

**Фокус в това упражнение:** Data contract → preprocessing → artifact → service → HTTP. Вижте [общата архитектура](../architecture/system-overview.md). Отбележете данните, зависимостите и owner на всяка граница.

## 7. Теоретична подготовка

Unit test изолира малка отговорност, integration test проверява collaborators/IO, end-to-end test преминава реалните deployment boundaries. Contract test фиксира observable interface. Mocking е полезен за failure injection, но не доказва истинска serialization/serving compatibility.

Детерминистичният contract може да има exact assertions: missing feature →422, missing model→503. Statistical quality test има versioned held-out dataset, threshold и support; фиксираният seed не прави качеството универсална гаранция. Test pyramid държи много бързи проверки и малко скъпи end-to-end runs. Data drift е промяна на входното разпределение; model/concept drift засяга връзката между входове и цел. Mean-shift smoke сигнал е евтин индикатор, не доказателство за спад на F1; нужни са labels и наблюдение във времето.

Следвайте [източниците и version scope](../architecture/references.md). Теорията трябва да обяснява engineering избора, не да замества evidence.

## 8. Лош / проблемен пример

```python
def test_api():
    assert response.status_code == 200  # няма schema, version, range, failure checks
```

Работещият starter и неговият TODO contract са в [starter/README.md](starter/README.md). Примерът е за анализ: първо запишете observable behavior и failure risks, после refactor-вайте. Не броим просто преименуване на файлове за архитектурна промяна.

## 9. Водена практическа задача

Командите за Python/pytest са от `ai-platform` при активирана среда. Процесът е **проблем → теория → анализ на лошо решение → практическа задача → самостоятелна задача → тестове → инженерна дискусия**.

### Стъпка 1

Изпълнете starter happy-path test и обяснете какво остава непроверено. Създайте test matrix по boundaries: data, features, repository, Predictor, HTTP.

### Стъпка 2

Добавете preprocessing test: label/cohort не влизат във features и scaler statistics са от training rows. Проверете missing/duplicate/out-of-range data.

### Стъпка 3

Тествайте model loading с реален local artifact, повредени bytes, wrong runtime version и missing file. Използвайте fake repository за отделен service unit test.

### Стъпка 4

Проверете prediction schema, probability range, model_version, empty batch, missing feature и invalid types. Quality gate използва held-out F1/recall≥0.85; не сравнявайте всеки prediction с една фиксирана label за всички модели.

### Стъпка 5

Добавете drift smoke с controlled shifted copy, performance protocol без fragile wall-clock unit threshold и една deliberate mutation, която нов test открива. Класифицирайте тестовете по цена и confidence.

Време: сценарий/теория15, анализ10, guided работа30, checkpoint5, самостоятелна работа25, tests15, дискусия10 минути — общо110. При 90 минути преподавателят подготвя environment и baseline evidence предварително.

## 10. Checkpoint

Suite покрива preprocessing, model loading, HTTP/invalid input, quality/range/schema и drift smoke с ясни ограничения.

Покажете working increment и кратък before/after diff. Ако има failure, класифицирайте го като environment, contract, quality или implementation проблем. Не преминавайте нататък само заради един green happy-path test.

## 11. Самостоятелна задача

**Problem statement:** разработете test strategy за целия AI pipeline и добавете три липсващи проверки.

**Requirements:** unit/integration/E2E/data/model/performance/contract матрица, fixture ownership, test runtime budget и failure triage; новите tests трябва да включват failure injection и normal functionality.

**Constraints:** без истински лични данни, sleep-based retries в unit tests или training-on-request; не заменяйте quality gate с mocked metric.

**Acceptance criteria:** минимум 12 test cases в стратегията, 3 нови runnable tests, red→green evidence от една mutation и обяснение кои guarantees остават извън suite.

Предайте собствена реализация/спецификация, rationale и evidence. Пълно решение не е включено тук; готовият общ проект е reference baseline за сравнение на contracts, а starter TODO задачите изискват ваш diff и допълнителни проверки.

## 12. Automated tests

Начални runnable проверки:

```bash
pytest -q --cov=ai_platform --cov-fail-under=85
pytest ../lab06-testing/starter/test_happy_path.py -q
```

Новите tests трябва да проверяват observable contract, negative/edge behavior и разрешения нормален flow. За документните задачи автоматизирайте структурните invariants, а смисъла проверете с peer review. За statistical/performance проверки запишете dataset/workload/seed/version/sample count; не твърдете универсална гаранция от малка synthetic извадка.

Добавете test/evidence traceability: **requirement ID → test name → command → actual result → limitation**. Поне една собствена проверка трябва да открива deliberate bad fixture или regression. След restore повторете suite; не променяйте assertions, за да прикриете failure.

## 13. Edge cases

- Prediction е NaN, но JSON status е 200.
- Feature order е разменен без промяна на броя features.
- В test split липсва една class/cohort.
- Performance test е изпълнен на shared noisy runner.

Изберете поне един за нов автоматизиран test; за останалите опишете expected behavior и owner.

## 14. Въпроси за анализ

1. Защо mocking не доказва artifact compatibility?
2. Как се прави reproducible quality test?
3. Какво доказва drift smoke?
4. Защо coverage не е достатъчно?
5. Къде е E2E boundary?
6. Кога exact prediction assertion е риск?

## 15. Очакван резултат

Завършен engineering increment по **Тестване на софтуер и AI компоненти**, checkpoint evidence, самостоятелната задача според acceptance criteria и нови automated checks. Предайте decision/trade-off analysis, а не само screenshot или model score. Данните, моделът и test environment трябва да са идентифицируеми.

## 16. Checklist

- [ ] Анализирах проблемния starter и записах failure scenario.
- [ ] Избрах архитектурно/процесно решение с trade-offs.
- [ ] Реализирах guided increment и checkpoint.
- [ ] Самостоятелната задача е различна и покрива acceptance criteria.
- [ ] Tests покриват negative/edge и positive behavior.
- [ ] Evidence включва data/model/code/environment identity.
- [ ] Не включих реални secrets/PII или платени external dependencies.
- [ ] Описах limitations, technical debt и следваща стъпка.
