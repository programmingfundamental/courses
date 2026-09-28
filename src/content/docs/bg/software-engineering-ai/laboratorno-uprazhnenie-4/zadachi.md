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

Изпълнете coupled.py. Избройте всички отговорности на predict_one и dependencies, нужни за unit test. Нарисувайте before dependency graph.

### Стъпка 2

Разделете pure feature selection от IO, training orchestration от estimator creation и API validation от inference. Запазете observable demo result с characterization test.

### Стъпка 3

Дефинирайте малък ModelRepository Protocol с load(version), който връща model и metadata. Подайте repository в Predictor; не импортвайте concrete storage във всеки consumer.

### Стъпка 4

Създайте fake repository за unit tests и FileRepository adapter за integration. Изнесете artifact root, model version и batch size в Settings с validation; изрично задайте ownership на lifetime.

### Стъпка 5

Докажете, че fake test не отваря файлове и не обучава модел. Добавете error contract за missing version и тест, че concrete adapter може да се смени без промяна в Predictor.

Време: сценарий/теория15, анализ10, guided работа30, checkpoint5, самостоятелна работа25, tests15, дискусия10 минути — общо110. При 90 минути преподавателят подготвя environment и baseline evidence предварително.

## 10. Checkpoint

Модулите имат еднозначни отговорности; inference се тества с fake port без training/filesystem.

Покажете working increment и кратък before/after diff. Ако има failure, класифицирайте го като environment, contract, quality или implementation проблем. Не преминавайте нататък само заради един green happy-path test.

## 11. Самостоятелна задача

**Problem statement:** изнесете tightly coupled experiment metadata writer зад ExperimentTracker Protocol.

**Requirements:** contract за запис на parameters/metrics/lineage; in-memory fake за tests и local-file adapter; training orchestration приема tracker отвън.

**Constraints:** без глобален singleton, без MLflow dependency в domain кода; не добавяйте abstract factory за всяка функция.

**Acceptance criteria:** независим unit test проверява изпратените metadata; integration test доказва persistence; tracking failure policy е explicit и тествана; съществуващият inference contract остава същият.

Предайте собствена реализация/спецификация, rationale и evidence. Пълно решение не е включено тук; готовият общ проект е reference baseline за сравнение на contracts, а starter TODO задачите изискват ваш diff и допълнителни проверки.

## 12. Automated tests

Начални runnable проверки:

```bash
pytest tests/test_repository.py tests/test_api.py -q
python ../lab04-modularity/starter/coupled.py
```

Новите tests трябва да проверяват observable contract, negative/edge behavior и разрешения нормален flow. За документните задачи автоматизирайте структурните invariants, а смисъла проверете с peer review. За statistical/performance проверки запишете dataset/workload/seed/version/sample count; не твърдете универсална гаранция от малка synthetic извадка.

Добавете test/evidence traceability: **requirement ID → test name → command → actual result → limitation**. Поне една собствена проверка трябва да открива deliberate bad fixture или regression. След restore повторете suite; не променяйте assertions, за да прикриете failure.

## 13. Edge cases

- Protocol implementation връща model без version metadata.
- Settings се сменят след model load.
- Fake не спазва error contract на реалния adapter.
- Circular import между training и api.

Изберете поне един за нов автоматизиран test; за останалите опишете expected behavior и owner.

## 14. Въпроси за анализ

1. Защо много файлове не означават modularity?
2. Как Protocol помага за tests?
3. Къде принадлежи joblib loading?
4. Кой притежава model lifetime?
5. Кога mock вреди?
6. Как се избягва circular dependency?

## 15. Очакван резултат

Завършен engineering increment по **Модулност, слоеве и разделяне на отговорностите**, checkpoint evidence, самостоятелната задача според acceptance criteria и нови automated checks. Предайте decision/trade-off analysis, а не само screenshot или model score. Данните, моделът и test environment трябва да са идентифицируеми.

## 16. Checklist

- [ ] Анализирах проблемния starter и записах failure scenario.
- [ ] Избрах архитектурно/процесно решение с trade-offs.
- [ ] Реализирах guided increment и checkpoint.
- [ ] Самостоятелната задача е различна и покрива acceptance criteria.
- [ ] Tests покриват negative/edge и positive behavior.
- [ ] Evidence включва data/model/code/environment identity.
- [ ] Не включих реални secrets/PII или платени external dependencies.
- [ ] Описах limitations, technical debt и следваща стъпка.
