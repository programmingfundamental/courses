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

Изпълнете starter/check.py и разгледайте requirements.json. Structural pass не означава качествена спецификация. Маркирайте undefined terms, липсваща мярка и неопределен workload.

### Стъпка 2

Дефинирайте FR за POST /predict: required features, bounds, label/probability/version response и invalid-input response. Задайте отделно batch limit и access requirement.

### Стъпка 3

Задайте учебни acceptance цели: held-out F1≥0.85 и recall≥0.85 при requests-v1, seed42, test_fraction0.25; candidate model не се оценява по training score. Документирайте кой dataset служи за acceptance и кога трябва независим final holdout.

### Стъпка 4

Опишете performance protocol: например p95<100 ms за batch 1/concurrency1 след 10 warmup,100 measured requests на записан hardware. Това е начална учебна цел, не гарантирана universal стойност. Availability SLO99.5%/30 дни се проектира, но не се „доказва“ с минутен smoke test.

### Стъпка 5

Свържете requirement IDs с test names и evidence. Допълнете checker така, че да отхвърля липсващи measurement/window/threshold полета за performance requirements. Напишете counterexample requirement, което структурно минава, но е недоказуемо.

Време: сценарий/теория15, анализ10, guided работа30, checkpoint5, самостоятелна работа25, tests15, дискусия10 минути — общо110. При 90 минути преподавателят подготвя environment и baseline evidence предварително.

## 10. Checkpoint

Има измерими FR/NFR/model/data requirements, traceability matrix и explicit measurement protocol.

Покажете working increment и кратък before/after diff. Ако има failure, класифицирайте го като environment, contract, quality или implementation проблем. Не преминавайте нататък само заради един green happy-path test.

## 11. Самостоятелна задача

**Problem statement:** съставете SRS за batch prediction module.

**Requirements:** functional behavior, NFR, model quality, data quality, error semantics и acceptance criteria; задайте throughput и maximum batch с unit и measurement protocol.

**Constraints:** няма streaming/cloud queue; output запазва input order; contract не зависи от sklearn class.

**Acceptance criteria:** поне 8 requirements с уникални IDs, без „бързо/достатъчно/надеждно“ без мярка; всяко има test/evidence owner; поне 2 автоматични contract/data checks.

Предайте собствена реализация/спецификация, rationale и evidence. Пълно решение не е включено тук; готовият общ проект е reference baseline за сравнение на contracts, а starter TODO задачите изискват ваш diff и допълнителни проверки.

## 12. Automated tests

Начални runnable проверки:

```bash
pytest tests/test_api.py tests/test_training.py -q
python ../lab02-requirements/starter/check.py
```

Новите tests трябва да проверяват observable contract, negative/edge behavior и разрешения нормален flow. За документните задачи автоматизирайте структурните invariants, а смисъла проверете с peer review. За statistical/performance проверки запишете dataset/workload/seed/version/sample count; не твърдете универсална гаранция от малка synthetic извадка.

Добавете test/evidence traceability: **requirement ID → test name → command → actual result → limitation**. Поне една собствена проверка трябва да открива deliberate bad fixture или regression. След restore повторете suite; не променяйте assertions, за да прикриете failure.

## 13. Edge cases

- Висока accuracy при силно небалансирани labels.
- p95 от само 5 measurements.
- Metric threshold без минимална cohort sample size.
- Timeout request се брои като successful response в availability.

Изберете поне един за нов автоматизиран test; за останалите опишете expected behavior и owner.

## 14. Въпроси за анализ

1. Защо F1 не е достатъчно requirement?
2. Каква е разликата SLA/SLO?
3. Кога latency числа са сравними?
4. Как се тества explainability constraint?
5. Какво е traceability?
6. Защо synthetic fairness е ограничено?

## 15. Очакван резултат

Завършен engineering increment по **Изисквания и спецификация на AI-базирани системи**, checkpoint evidence, самостоятелната задача според acceptance criteria и нови automated checks. Предайте decision/trade-off analysis, а не само screenshot или model score. Данните, моделът и test environment трябва да са идентифицируеми.

## 16. Checklist

- [ ] Анализирах проблемния starter и записах failure scenario.
- [ ] Избрах архитектурно/процесно решение с trade-offs.
- [ ] Реализирах guided increment и checkpoint.
- [ ] Самостоятелната задача е различна и покрива acceptance criteria.
- [ ] Tests покриват negative/edge и positive behavior.
- [ ] Evidence включва data/model/code/environment identity.
- [ ] Не включих реални secrets/PII или платени external dependencies.
- [ ] Описах limitations, technical debt и следваща стъпка.
