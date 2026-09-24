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

Изпълнете conditional.py и намерете knowledge duplication, която ще се появи при повторение на избора в няколко modules. Разграничете benign if от unstable selection logic.

### Стъпка 2

Опишете required Strategy contract: fit, predict_proba, class ordering и reproducible seed. Добавете contract test за всяка налична implementation.

### Стъпка 3

Refactor-нете selection към registry/factory; unknown name трябва да бъде explicit error, не silent default. Orchestration подава config и получава съвместим estimator.

### Стъпка 4

Поставете shared preprocessing в Pipeline, така че fit да остане върху training split. Избягвайте паралелен набор custom transformations в serving.

### Стъпка 5

Сравнете diff и extension cost преди/след refactoring. Добавете test за отказ на несъвместим strategy adapter и опишете кога бихте оставили прост conditional.

Време: сценарий/теория15, анализ10, guided работа30, checkpoint5, самостоятелна работа25, tests15, дискусия10 минути — общо110. При 90 минути преподавателят подготвя environment и baseline evidence предварително.

## 10. Checkpoint

Един orchestration flow работи с linear/tree/forest strategies и общи contract tests; няма estimator-specific HTTP response.

Покажете working increment и кратък before/after diff. Ако има failure, класифицирайте го като environment, contract, quality или implementation проблем. Не преминавайте нататък само заради един green happy-path test.

## 11. Самостоятелна задача

**Problem statement:** добавете нова model implementation чрез Adapter/Factory без промяна на train orchestration.

**Requirements:** използвайте даден sklearn estimator, например DummyClassifier като проверим baseline, или adapter към class с различно име на predict метода; запазете stable probability contract.

**Constraints:** не оптимизирайте hyperparameters; промяна на factory registration е позволена, но промяна на основния orchestration за новото име — не.

**Acceptance criteria:** новият вариант се създава по config, покрива common contract tests, а API response schema остава същата; обяснете защо ниско quality baseline не трябва да бъде promoted.

Предайте собствена реализация/спецификация, rationale и evidence. Пълно решение не е включено тук; готовият общ проект е reference baseline за сравнение на contracts, а starter TODO задачите изискват ваш diff и допълнителни проверки.

## 12. Automated tests

Начални runnable проверки:

```bash
pytest tests/test_training.py -q
python ../lab05-design-patterns/starter/conditional.py
```

Новите tests трябва да проверяват observable contract, negative/edge behavior и разрешения нормален flow. За документните задачи автоматизирайте структурните invariants, а смисъла проверете с peer review. За statistical/performance проверки запишете dataset/workload/seed/version/sample count; не твърдете универсална гаранция от малка synthetic извадка.

Добавете test/evidence traceability: **requirement ID → test name → command → actual result → limitation**. Поне една собствена проверка трябва да открива deliberate bad fixture или regression. След restore повторете suite; не променяйте assertions, за да прикриете failure.

## 13. Edge cases

- Estimator няма predict_proba.
- Class ordering е [1,0], а consumer приема [0,1].
- Unknown config name тихо използва друг модел.
- Factory връща споделен mutable estimator между два runs.

Изберете поне един за нов автоматизиран test; за останалите опишете expected behavior и owner.

## 14. Въпроси за анализ

1. Кога Strategy е полезен?
2. Какво означава open/closed тук?
3. Кога DRY е вредно приложен?
4. Защо contract test е нужен?
5. Как Adapter се различава от Factory?
6. Защо DummyClassifier е полезен без висок F1?

## 15. Очакван резултат

Завършен engineering increment по **Design Patterns и принципи за качествен код**, checkpoint evidence, самостоятелната задача според acceptance criteria и нови automated checks. Предайте decision/trade-off analysis, а не само screenshot или model score. Данните, моделът и test environment трябва да са идентифицируеми.

## 16. Checklist

- [ ] Анализирах проблемния starter и записах failure scenario.
- [ ] Избрах архитектурно/процесно решение с trade-offs.
- [ ] Реализирах guided increment и checkpoint.
- [ ] Самостоятелната задача е различна и покрива acceptance criteria.
- [ ] Tests покриват negative/edge и positive behavior.
- [ ] Evidence включва data/model/code/environment identity.
- [ ] Не включих реални secrets/PII или платени external dependencies.
- [ ] Описах limitations, technical debt и следваща стъпка.
