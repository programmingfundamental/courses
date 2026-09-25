# 1. Упражнение 7 — Version Control, CI/CD и автоматизация

**Аудитория:** IV курс, бакалавър „Изкуствен интелект“. **Време:** 110 минути.
Работи се само с предоставения CPU проект и synthetic dataset, без платени услуги.

## 2. Инженерен сценарий

На лаптопа build-ът минава, но CI инсталира различни dependencies и моделът не се зарежда. Deployment използва tag latest и няма начин да се докаже кой artifact е бил serving преди регресията.

## 3. Учебни цели

След упражнението студентът:

- проектира Git/review workflow;
- автоматизира lint/tests/build quality gates;
- реализира reproducible dependency installation;
- тества immutable release и deployment readiness;
- аргументира CI спрямо CD и manual gates;
- измерва regression impact преди promotion;

## 4. Предварителни знания

Python, основи на ML/Jupyter, Git, REST API, Docker, scikit-learn/pandas/numpy, Linux и бази данни. Използвайте резултатите от предходните 6 упражнения като engineering input. Не преговаряме елементарни Python конструкции.

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

**Фокус в това упражнение:** Commit → lint → tests → model gate → immutable image → local deployment. Вижте [общата архитектура](../architecture/system-overview.md). Отбележете данните, зависимостите и owner на всяка граница.

## 7. Теоретична подготовка

CI интегрира малки промени с автоматични проверки; continuous delivery подготвя release с контролирана promotion/deployment стъпка, а continuous deployment публикува автоматично след gates. Pull request review оценява design, tests и риск; branch е изолирана линия работа, не заместител на review.

Semantic versioning следва compatibility на публичния API, докато model version идентифицира конкретен trained artifact; те не са едно и също. Lock file pin-ва resolved dependencies; reproducibility допълнително зависи от Python/OS и base image. Immutable release съдържа конкретен model bundle и image ID/digest. Tag latest е подвижен pointer. Coverage е quality signal, не доказателство за correctness. Deployment readiness gate проверява, че service може да обслужва request, не само че process съществува.

Следвайте [източниците и version scope](../architecture/references.md). Теорията трябва да обяснява engineering избора, не да замества evidence.

## 8. Лош / проблемен пример

```yaml
- run: pip install scikit-learn fastapi
- run: echo 'tests passed'
# unpinned dependencies, без tests, image/version или failure gate
```

Работещият starter и неговият TODO contract са в [starter/README.md](starter/README.md). Примерът е за анализ: първо запишете observable behavior и failure risks, после refactor-вайте. Не броим просто преименуване на файлове за архитектурна промяна.

## 9. Водена практическа задача

Командите за Python/pytest са от `ai-platform` при активирана среда. Процесът е **проблем → теория → анализ на лошо решение → практическа задача → самостоятелна задача → тестове → инженерна дискусия**.

### Стъпка 1

Разгледайте ci-incomplete.yml като fixture; не го активирайте. Сравнете с реалния workflow в корена `.github/workflows/software-engineering-ai.yml` и начертайте dependencies между steps.

### Стъпка 2

Изпълнете locally pinned install, pip check, ruff check/format-check, unit tests и integration tests. Изолирайте failure от lint спрямо model metric failure.

### Стъпка 3

Build-нете конкретен model version, проверете quality gate и embed-нете bundle в Docker image. API не трябва да има достъп до dataset за training. Не bake-вайте API key в image.

### Стъпка 4

Проучете local deploy script: image tag се resolve-ва до image ID, readiness се проверява, при failure се възстановява старият labelled container. Дискутирайте краткия downtime на този учебен single-host подход.

### Стъпка 5

Създайте PR checklist за tests/schema/model metadata/dependencies. Покажете failed build при deliberate schema regression и green след restore. Не push-вайте или deploy-вайте към публична услуга за упражнението.

Време: сценарий/теория15, анализ10, guided работа30, checkpoint5, самостоятелна работа25, tests15, дискусия10 минути — общо110. При 90 минути преподавателят подготвя environment и baseline evidence предварително.

## 10. Checkpoint

Една документирана команда/CI sequence валидира кода, dataset, model и Docker release; version е traceable и failure спира pipeline.

Покажете working increment и кратък before/after diff. Ако има failure, класифицирайте го като environment, contract, quality или implementation проблем. Не преминавайте нататък само заради един green happy-path test.

## 11. Самостоятелна задача

**Problem statement:** добавете нов quality gate за model/dependency regression.

**Requirements:** coverage threshold, model validation и dependency check да имат конкретни failure условия; добавете gate за missing lineage metadata или forbidden dependency в учебен fixture.

**Constraints:** без paid scanning/cloud credentials; pip check е consistency проверка, не CVE scanner; ако използвате audit tool, запишете database timestamp и scope.

**Acceptance criteria:** gate fail-ва върху supplied bad fixture, минава върху valid release и пази exit code; PR описва artifact promotion/rollback procedure и отчита текущите ограничения.

Предайте собствена реализация/спецификация, rationale и evidence. Пълно решение не е включено тук; готовият общ проект е reference baseline за сравнение на contracts, а starter TODO задачите изискват ваш diff и допълнителни проверки.

## 12. Automated tests

Начални runnable проверки:

```bash
ruff check src tests scripts
pytest --cov=ai_platform --cov-fail-under=85 -q
```

Новите tests трябва да проверяват observable contract, negative/edge behavior и разрешения нормален flow. За документните задачи автоматизирайте структурните invariants, а смисъла проверете с peer review. За statistical/performance проверки запишете dataset/workload/seed/version/sample count; не твърдете универсална гаранция от малка synthetic извадка.

Добавете test/evidence traceability: **requirement ID → test name → command → actual result → limitation**. Поне една собствена проверка трябва да открива deliberate bad fixture или regression. След restore повторете suite; не променяйте assertions, за да прикриете failure.

## 13. Edge cases

- Steps използват continue-on-error за quality failure.
- Docker build включва secrets от .env.
- PR untrusted code получава deployment credentials.
- Rollback alias е сменен, но image съдържа друг модел.

Изберете поне един за нов автоматизиран test; за останалите опишете expected behavior и owner.

## 14. Въпроси за анализ

1. Как CI се различава от CD?
2. Защо latest не е release identity?
3. Какво не доказва pip check?
4. Защо model version и API SemVer са различни?
5. Защо secret не се bake-ва в image?
6. Каква е rollback acceptance проверката?

## 15. Очакван резултат

Завършен engineering increment по **Version Control, CI/CD и автоматизация**, checkpoint evidence, самостоятелната задача според acceptance criteria и нови automated checks. Предайте decision/trade-off analysis, а не само screenshot или model score. Данните, моделът и test environment трябва да са идентифицируеми.

## 16. Checklist

- [ ] Анализирах проблемния starter и записах failure scenario.
- [ ] Избрах архитектурно/процесно решение с trade-offs.
- [ ] Реализирах guided increment и checkpoint.
- [ ] Самостоятелната задача е различна и покрива acceptance criteria.
- [ ] Tests покриват negative/edge и positive behavior.
- [ ] Evidence включва data/model/code/environment identity.
- [ ] Не включих реални secrets/PII или платени external dependencies.
- [ ] Описах limitations, technical debt и следваща стъпка.
