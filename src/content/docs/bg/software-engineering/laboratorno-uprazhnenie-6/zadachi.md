---
title: Задачи
taskPage: true
sidebar:
  label: Задачи
  order: 100
---

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
