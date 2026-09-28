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

Изпълнете monolith.py --smoke. Прочетете top-level code и опишете какви side effects се случват преди първия request. Не добавяйте още sklearn tuning.

### Стъпка 2

Начертайте component и deployment diagrams отделно. Отбележете data ownership, training-only dependencies и model handoff. Използвайте SRS latency/availability constraints като аргумент.

### Стъпка 3

Дефинирайте artifact contract: version, feature schema/order, model hash, runtime compatibility, dataset/code/config lineage. Опишете поведение при missing/corrupted model.

### Стъпка 4

Планирайте миграция в два increments, така че service да продължи да има working demo. Сравнете предложението с ai-platform modules; проследете create_app/lifespan и offline cli train.

### Стъпка 5

Добавете architecture test, който import-ва API без dataset достъп и потвърждава, че train не е извикан. Проверете missing model: live200, ready503. Напишете ADR за избрания serving boundary.

Време: сценарий/теория15, анализ10, guided работа30, checkpoint5, самостоятелна работа25, tests15, дискусия10 минути — общо110. При 90 минути преподавателят подготвя environment и baseline evidence предварително.

## 10. Checkpoint

Training и serving могат да се стартират отделно; architecture decision има quality-attribute аргумент и artifact/error contract.

Покажете working increment и кратък before/after diff. Ако има failure, класифицирайте го като environment, contract, quality или implementation проблем. Не преминавайте нататък само заради един green happy-path test.

## 11. Самостоятелна задача

**Problem statement:** екип от двама разработчици обслужва 20 requests/sec на един CPU host, сменя модела веднъж седмично и няма дежурен infrastructure екип.

**Requirements:** сравнете embedded model и separate inference service по latency, scaling, reliability, deployment, security и complexity; изберете вариант с ADR.

**Constraints:** няма GPU или cloud managed serving; не обявявайте microservices за задължителни.

**Acceptance criteria:** поне 6 сравними измерения, workload assumptions, rejected alternative, failure behavior и migration trigger; един автоматизиран boundary/contract test подкрепя решението.

Предайте собствена реализация/спецификация, rationale и evidence. Пълно решение не е включено тук; готовият общ проект е reference baseline за сравнение на contracts, а starter TODO задачите изискват ваш diff и допълнителни проверки.

## 12. Automated tests

Начални runnable проверки:

```bash
pytest tests/test_api.py -q
python ../lab03-architecture/starter/monolith.py --smoke
```

Новите tests трябва да проверяват observable contract, negative/edge behavior и разрешения нормален flow. За документните задачи автоматизирайте структурните invariants, а смисъла проверете с peer review. За statistical/performance проверки запишете dataset/workload/seed/version/sample count; не твърдете универсална гаранция от малка synthetic извадка.

Добавете test/evidence traceability: **requirement ID → test name → command → actual result → limitation**. Поне една собствена проверка трябва да открива deliberate bad fixture или regression. След restore повторете suite; не променяйте assertions, за да прикриете failure.

## 13. Edge cases

- Serving restart няма достъп до training dataset.
- Artifact е нов, но feature order е стар.
- Service division увеличава latency повече от model compute.
- Нова model version е promoted, но вече стартираният API е pinned към старата.

Изберете поне един за нов автоматизиран test; за останалите опишете expected behavior и owner.

## 14. Въпроси за анализ

1. Каква е разликата module/service?
2. Защо serving не обучава при request?
3. Защо artifact contract включва schema?
4. Кога separate inference е оправдан?
5. Какво съдържа ADR?
6. Защо promotion не е deployment?

## 15. Очакван резултат

Завършен engineering increment по **Софтуерна архитектура и архитектурни стилове**, checkpoint evidence, самостоятелната задача според acceptance criteria и нови automated checks. Предайте decision/trade-off analysis, а не само screenshot или model score. Данните, моделът и test environment трябва да са идентифицируеми.

## 16. Checklist

- [ ] Анализирах проблемния starter и записах failure scenario.
- [ ] Избрах архитектурно/процесно решение с trade-offs.
- [ ] Реализирах guided increment и checkpoint.
- [ ] Самостоятелната задача е различна и покрива acceptance criteria.
- [ ] Tests покриват negative/edge и positive behavior.
- [ ] Evidence включва data/model/code/environment identity.
- [ ] Не включих реални secrets/PII или платени external dependencies.
- [ ] Описах limitations, technical debt и следваща стъпка.
