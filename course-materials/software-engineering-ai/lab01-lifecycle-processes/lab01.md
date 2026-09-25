# 1. Упражнение 1 — Софтуерен жизнен цикъл и инженерни процеси

**Аудитория:** IV курс, бакалавър „Изкуствен интелект“. **Време:** 110 минути.
Работи се само с предоставения CPU проект и synthetic dataset, без платени услуги.

## 2. Инженерен сценарий

Експериментът има висок training score, но само авторът може да го стартира. Dataset path е в клетка, няма tests и никой не знае дали последният модел отговаря на последната версия на кода. Екипът трябва да организира прехода към поддържана система.

## 3. Учебни цели

След упражнението студентът:

- анализира разликата research code/production software;
- идентифицира technical debt и рискови assumptions;
- проектира iterative lifecycle за AI проекта;
- аргументира milestones и приоритети;
- реализира проверим definition of done;
- автоматизира clean-run проверка на експеримента;

## 4. Предварителни знания

Python, основи на ML/Jupyter, Git, REST API, Docker, scikit-learn/pandas/numpy, Linux и бази данни. ML алгоритмите са даденост; не се изисква избор или tuning на нов classifier. Не преговаряме елементарни Python конструкции.

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

**Фокус в това упражнение:** Notebook → engineering backlog → reproducible pipeline. Вижте [общата архитектура](../architecture/system-overview.md). Отбележете данните, зависимостите и owner на всяка граница.

## 7. Теоретична подготовка

SDLC (software development lifecycle) свързва requirements, design, implementation, verification, deployment, operation и retirement. AI lifecycle добавя data acquisition/validation, experimentation, model evaluation и наблюдение на статистическо качество. Итеративното развитие означава малки проверими increments; Agile не премахва документацията и acceptance criteria.

Research code оптимизира скоростта на проверка на хипотеза. Production code има users, contracts, versioned artifacts, error behavior и operational owner. Reproducibility изисква data identity, code/config/dependency identity, seed, split и инструкции; seed сам по себе си не е достатъчен. Technical debt е бъдеща цена от днешно решение; записвайте consequence, trigger, owner и repayment test. Definition of done (DoD) е обща проверка за завършен increment, а acceptance criteria са конкретни за отделното requirement.

Следвайте [източниците и version scope](../architecture/references.md). Теорията трябва да обяснява engineering избора, не да замества evidence.

## 8. Лош / проблемен пример

```python
data = pd.read_csv('../../ai-platform/data/requests-v1.csv')
x = StandardScaler().fit_transform(data[FEATURES])
model.fit(x, data.label)
print(model.score(x, data.label))  # training score != acceptance quality
```

Работещият starter и неговият TODO contract са в [starter/README.md](starter/README.md). Примерът е за анализ: първо запишете observable behavior и failure risks, после refactor-вайте. Не броим просто преименуване на файлове за архитектурна промяна.

## 9. Водена практическа задача

Командите за Python/pytest са от `ai-platform` при активирана среда. Процесът е **проблем → теория → анализ на лошо решение → практическа задача → самостоятелна задача → тестове → инженерна дискусия**.

### Стъпка 1

От ai-platform изпълнете `python scripts/check_notebook.py`. Отворете starter/experiment.ipynb в Jupyter при желание. Notebook-ът работи върху 400 synthetic rows, но няма отделна validation оценка; не го оптимизирайте като ML алгоритъм.

### Стъпка 2

Създайте inventory на code/config/data/runtime assumptions. За всеки проблем посочете конкретна клетка, failure scenario и засегнат stakeholder. Разграничете engineering defect от липса на научен резултат.

### Стъпка 3

Превърнете минимум 8 наблюдения в backlog items с acceptance test и dependency. Приоритизирайте reproducible run, contract и artifact identity преди графичен интерфейс.

### Стъпка 4

Планирайте три increments: reproducible offline pipeline, tested inference API, operated release. За всеки задайте вход, проверим output, owner и риск. Направете diagram requirements → validation → release feedback.

### Стъпка 5

Добавете малък machine-readable DoD checklist и test, който открива липсващ dataset/version/test-report item. Сравнете успешното изпълнение на notebook с доказателство за качествен продукт.

Време: сценарий/теория15, анализ10, guided работа30, checkpoint5, самостоятелна работа25, tests15, дискусия10 минути — общо110. При 90 минути преподавателят подготвя environment и baseline evidence предварително.

## 10. Checkpoint

Notebook cells работят от чист контекст; backlog съдържа поне 8 проследими items и три milestones с measurable exit criteria.

Покажете working increment и кратък before/after diff. Ако има failure, класифицирайте го като environment, contract, quality или implementation проблем. Не преминавайте нататък само заради един green happy-path test.

## 11. Самостоятелна задача

**Problem statement:** продуктът трябва да се предаде на втори екип след шест седмици.

**Requirements:** създайте project roadmap, DoD и engineering workflow с branch/review/test/release/rollback стъпки; дефинирайте поне един feedback loop от наблюдение към ново requirement.

**Constraints:** максимум 2 разработчици, CPU лаптоп, без облачен бюджет; не решавайте задачата с добавяне на microservices.

**Acceptance criteria:** всяка седмица има проверим increment; рисковете имат owner; roadmap съдържа maintenance/retirement; един автоматизиран test валидира задължителните DoD fields.

Предайте собствена реализация/спецификация, rationale и evidence. Пълно решение не е включено тук; готовият общ проект е reference baseline за сравнение на contracts, а starter TODO задачите изискват ваш diff и допълнителни проверки.

## 12. Automated tests

Начални runnable проверки:

```bash
python scripts/check_notebook.py
pytest tests/test_data.py -q
```

Новите tests трябва да проверяват observable contract, negative/edge behavior и разрешения нормален flow. За документните задачи автоматизирайте структурните invariants, а смисъла проверете с peer review. За statistical/performance проверки запишете dataset/workload/seed/version/sample count; не твърдете универсална гаранция от малка synthetic извадка.

Добавете test/evidence traceability: **requirement ID → test name → command → actual result → limitation**. Поне една собствена проверка трябва да открива deliberate bad fixture или regression. След restore повторете suite; не променяйте assertions, за да прикриете failure.

## 13. Edge cases

- Notebook клетки се изпълняват в различен ред.
- Dataset е сменен без промяна на filename.
- Seed е фиксиран, но dependency version е различна.

Изберете поне един за нов автоматизиран test; за останалите опишете expected behavior и owner.

## 14. Въпроси за анализ

1. Защо training score не е DoD?
2. Как lifecycle се различава от pipeline?
3. Кога technical debt е приемлив?
4. Кои identities са нужни за reproducibility?
5. Защо deployment не е краят?
6. Какво е vertical slice?

## 15. Очакван резултат

Завършен engineering increment по **Софтуерен жизнен цикъл и инженерни процеси**, checkpoint evidence, самостоятелната задача според acceptance criteria и нови automated checks. Предайте decision/trade-off analysis, а не само screenshot или model score. Данните, моделът и test environment трябва да са идентифицируеми.

## 16. Checklist

- [ ] Анализирах проблемния starter и записах failure scenario.
- [ ] Избрах архитектурно/процесно решение с trade-offs.
- [ ] Реализирах guided increment и checkpoint.
- [ ] Самостоятелната задача е различна и покрива acceptance criteria.
- [ ] Tests покриват negative/edge и positive behavior.
- [ ] Evidence включва data/model/code/environment identity.
- [ ] Не включих реални secrets/PII или платени external dependencies.
- [ ] Описах limitations, technical debt и следваща стъпка.
