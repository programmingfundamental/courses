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
