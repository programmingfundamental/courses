# 1. Упражнение 8 — MLOps и управление на модели и данни

**Аудитория:** IV курс, бакалавър „Изкуствен интелект“. **Време:** 110 минути.
Работи се само с предоставения CPU проект и synthetic dataset, без платени услуги.

## 2. Инженерен сценарий

В директория има model-final.joblib и model-final2.joblib. Вторият има по-висока accuracy, но е обучен върху различен split; dataset origin липсва. Екипът не може да избере release или да възстанови предишното поведение.

## 3. Учебни цели

След упражнението студентът:

- анализира model/data lineage;
- реализира experiment tracking с пълни metadata;
- автоматизира data/model version checks;
- сравнява release кандидати по инженерни критерии;
- тества immutable versions и rollback;
- аргументира champion/challenger promotion;

## 4. Предварителни знания

Python, основи на ML/Jupyter, Git, REST API, Docker, scikit-learn/pandas/numpy, Linux и бази данни. Използвайте резултатите от предходните 7 упражнения като engineering input. Не преговаряме елементарни Python конструкции.

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

**Фокус в това упражнение:** Dataset manifest → experiment journal → immutable registry → promotion/rollback. Вижте [общата архитектура](../architecture/system-overview.md). Отбележете данните, зависимостите и owner на всяка граница.

## 7. Теоретична подготовка

MLOps свързва ML lifecycle с автоматизация и operations. Experiment tracking пази parameters, metrics, artifacts и контекста на run; model registry управлява version identities и promotion state. Lineage проследява model → dataset → source/config/dependencies → evaluation. Име на файл и timestamp не са достатъчни за възпроизводимост.

В курса local immutable experiment journal е еквивалент на основните tracking функции: всеки version folder съдържа model и metadata с params/metrics/data hash/git commit/source hash/lock hash. Git+CSV manifest е малък data-versioning workflow, еквивалентен за този dataset на DVC pointer/content workflow. Не е разпределен MLflow/DVC service. Champion е избраната версия; challenger е кандидатът. Promotion сменя registry pointer, deployment сменя running immutable bundle. Rollback трябва да запази стария artifact и runtime compatibility; само alias update не променя вече работещия процес.

Следвайте [източниците и version scope](../architecture/references.md). Теорията трябва да обяснява engineering избора, не да замества evidence.

## 8. Лош / проблемен пример

```text
models/model-final.joblib
models/model-final-new.joblib
# няма dataset identity, split, params, metrics или source version
```

Работещият starter и неговият TODO contract са в [starter/README.md](starter/README.md). Примерът е за анализ: първо запишете observable behavior и failure risks, после refactor-вайте. Не броим просто преименуване на файлове за архитектурна промяна.

## 9. Водена практическа задача

Командите за Python/pytest са от `ai-platform` при активирана среда. Процесът е **проблем → теория → анализ на лошо решение → практическа задача → самостоятелна задача → тестове → инженерна дискусия**.

### Стъпка 1

Прегледайте requests-v1.csv и manifest. Изпълнете data validation, после два runs с distinct versions `compare-v1` и `compare-v2`, еднакъв dataset/seed и linear/tree strategy. Това е lifecycle comparison, не задача за hyperparameter tuning.

### Стъпка 2

Използвайте starter/compare.py compare-v1 compare-v2. Проверете parameters, metrics, cohort support, test_ids, dataset hash, source/git/lock identity и artifact hash. Ако commit е unavailable/dirty, обяснете защо release lineage е непълен.

### Стъпка 3

Докажете immutable save: повторно training със същата version трябва да откаже overwrite. Проверете checksums преди load; обяснете защо hash не доказва доверен publisher.

### Стъпка 4

Изберете candidate според minimum F1/recall, slice support, artifact size, измерена latency, compatibility и operational risk. Запишете decision record, а не само sort по accuracy.

### Стъпка 5

Promote-нете само преминал quality gate модел; сменете champion към втори допустим version и изпълнете rollback. Сравнете pointer с observable model_version на API: deployment трябва да е отделна explicit стъпка.

Време: сценарий/теория15, анализ10, guided работа30, checkpoint5, самостоятелна работа25, tests15, дискусия10 минути — общо110. При 90 минути преподавателят подготвя environment и baseline evidence предварително.

## 10. Checkpoint

Всеки run има съпоставими metadata; dataset/model versions са проверими; promotion и rollback са демонстрирани без overwrite.

Покажете working increment и кратък before/after diff. Ако има failure, класифицирайте го като environment, contract, quality или implementation проблем. Не преминавайте нататък само заради един green happy-path test.

## 11. Самостоятелна задача

**Problem statement:** изберете коя от две версии да бъде promoted и подгответе rollback plan.

**Requirements:** comparison по качество, slices, latency, artifact/runtime size, lineage completeness и failure behavior; добавете gate за липсващ dataset/git/lock identity.

**Constraints:** еднакъв evaluation protocol; не приемайте по-висока accuracy като автоматичен победител; не презаписвайте съществуваща версия.

**Acceptance criteria:** decision включва rejected alternative и остатъчен риск; test отказва incomplete-lineage fixture; old version остава loadable; rollback е доказан с version check, не само с успешно изпълнена команда.

Предайте собствена реализация/спецификация, rationale и evidence. Пълно решение не е включено тук; готовият общ проект е reference baseline за сравнение на contracts, а starter TODO задачите изискват ваш diff и допълнителни проверки.

## 12. Automated tests

Начални runnable проверки:

```bash
pytest tests/test_repository.py tests/test_training.py -q
python -m ai_platform.cli validate
```

Новите tests трябва да проверяват observable contract, negative/edge behavior и разрешения нормален flow. За документните задачи автоматизирайте структурните invariants, а смисъла проверете с peer review. За statistical/performance проверки запишете dataset/workload/seed/version/sample count; не твърдете универсална гаранция от малка synthetic извадка.

Добавете test/evidence traceability: **requirement ID → test name → command → actual result → limitation**. Поне една собствена проверка трябва да открива deliberate bad fixture или regression. След restore повторете suite; не променяйте assertions, за да прикриете failure.

## 13. Edge cases

- Две версии имат различни dataset splits.
- Checksum е обновен заедно със злонамерен artifact — няма authenticity.
- Champion pointer се обновява едновременно от два writers.
- Старият artifact е наличен, но sklearn runtime е несъвместим.

Изберете поне един за нов автоматизиран test; за останалите опишете expected behavior и owner.

## 14. Въпроси за анализ

1. Какво прави comparison честно?
2. Какво не доказва hash?
3. Защо git SHA е недостатъчен при dirty tree?
4. Какво е разликата promotion/deployment?
5. Защо rollback изисква old runtime?
6. Кога да преминем към MLflow/DVC?

## 15. Очакван резултат

Завършен engineering increment по **MLOps и управление на модели и данни**, checkpoint evidence, самостоятелната задача според acceptance criteria и нови automated checks. Предайте decision/trade-off analysis, а не само screenshot или model score. Данните, моделът и test environment трябва да са идентифицируеми.

## 16. Checklist

- [ ] Анализирах проблемния starter и записах failure scenario.
- [ ] Избрах архитектурно/процесно решение с trade-offs.
- [ ] Реализирах guided increment и checkpoint.
- [ ] Самостоятелната задача е различна и покрива acceptance criteria.
- [ ] Tests покриват negative/edge и positive behavior.
- [ ] Evidence включва data/model/code/environment identity.
- [ ] Не включих реални secrets/PII или платени external dependencies.
- [ ] Описах limitations, technical debt и следваща стъпка.
