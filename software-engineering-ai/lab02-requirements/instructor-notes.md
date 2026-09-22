# Преподавателски бележки — 2. Изисквания и спецификация на AI-базирани системи

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА.**

## 1. Концептуална цел

Stakeholder intent → SRS → measurable acceptance contracts. Студентът трябва да свърже инженерното решение с measurable quality и change/failure behavior.

Възложителят казва: „Системата трябва да разпознава заявки добре и бързо.“ Разработчиците могат да докажат висок F1 на различен dataset или ниска latency при един request, но нито едно не определя приемането.

## 2. Какво НЕ е основната цел

Обучаване на най-точен classifier или hyperparameter tuning. Не оценявайте броя layers/classes/tools като качество. Търсете мотивирана граница, reproducibility и тестируем резултат.

## 3. Подготовка преди часа

- Изпълнете setup по README и `python -m pip check`; използвайте Python 3.12 и committed dataset.
- Проверете `python ../lab02-requirements/starter/check.py` от ai-platform.
- Прегледайте starter TODO и common reference tests; не раздавайте тези бележки като student handout.
- При нужда създайте нов model version с CLI; не overwrite-вайте съществуващ artifact.
- Подгответе local synthetic bad fixture и очакван failure; не използвайте истински credentials/PII.
- Препоръчайте отделен student working copy/branch и checkpoint преди refactoring.

## 4. Разпределение на времето

| Дейност | Минути |
|---|---:|
| Сценарий и теория | 15 |
| Анализ на лош пример | 10 |
| Guided работа | 30 |
| Checkpoint | 5 |
| Самостоятелна задача | 25 |
| Tests и evidence | 15 |
| Инженерна дискусия | 10 |
| **Общо** | **110** |

За 120 мин добавете peer review; за90 подгответе environment и началната characterization проверка. Не премахвайте independent task или negative testing.

## 5. Начален сценарий

Възложителят казва: „Системата трябва да разпознава заявки добре и бързо.“ Разработчиците могат да докажат висок F1 на различен dataset или ниска latency при един request, но нито едно не определя приемането.

Поискайте students да предвидят failure mode преди изпълнение. Разграничете наблюдение, inference и недоказана хипотеза.

## 6. Ключови концепции

Functional requirement описва действие/резултат; non-functional requirement задава качество или ограничение. AI-specific requirements включват model metrics върху versioned evaluation data, slice support и допустимо поведение при uncertainty. Accuracy е дял верни predictions; precision измерва надеждността на positive results, recall — намерените positives. F1 съчетава precision/recall, но не отчита самостоятелно различна бизнес цена на грешките.

SLO е цел за service indicator за определен прозорец; SLA е договор с последствия. p95 latency без hardware, workload, concurrency, batch size, warmup и sample count е непълно requirement. Availability трябва да определя denominator, maintenance windows и откази. Explainability/fairness се превръщат в проверими constraints само в ясно описан контекст; synthetic cohort metrics не доказват fairness за реални хора.

Свържете всяка концепция с конкретен code/document/test anchor, а не само с дефиниция.

## 7. Чести грешки

- F1 върху training set.
- Latency без workload.
- SLO се обявява за измерено с кратък тест.
- Accuracy като единствен quality критерий.
- Ethics само като пожелание.
- Acceptance criteria без test owner.

## 8. Насочващи въпроси

- Каква е причината този компонент да се промени?
- Кой contract трябва да остане стабилен?
- Каква dependency пречи на независимия test?
- Как ще различите model-quality regression от software defect?
- Какво показва positive control след поправката?
- Каква operational цена добавя избраното решение?

## 9. Очаквана архитектура

```text
Versioned data -> validation/features -> offline orchestration
                                  -> experiment/model metadata
                                  -> immutable registry
                                  -> inference -> API
                                  -> observability -> engineering feedback
```

В този lab акцентът е **Stakeholder intent → SRS → measurable acceptance contracts**. При lifecycle/SRS задачи диаграмата трябва да има decision/evidence gates; при code задачи — реални module dependencies и error contracts. Не налагайте microservice boundary без изискване.

## 10. Примерно решение

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА.**

Оценявайте operational definition, не избраното число. Примерен NFR: id=NFR-LAT-1, percentile95, max100ms, warmup10, measured100, concurrency1,batch 1,hardware записан. SRS на batch трябва да описва order, partial vs atomic failure и limits; приемайте обоснован избор, но test contract трябва да съответства.

Общият ai-platform съдържа runnable reference implementation за guided concepts. При проверка сравнявайте behavior, не source code равенство. Independent solutions трябва да добавят собствен contract/test, който не е просто изпълнение на готовия reference.

## 11. Очаквани тестове

- `pytest tests/test_api.py tests/test_training.py -q`
- `python ../lab02-requirements/starter/check.py`

Освен baseline очаквайте нов test по independent acceptance criteria, negative fixture и положителна проверка. За документни deliverables: structural automation + semantic review. За performance: explicit workload/measurement protocol. Проверете, че failing command действително връща nonzero exit status.

## 12. Проверка на самостоятелната задача

- [ ] Problem statement и requirements са покрити.
- [ ] Constraints са спазени и няма излишна infrastructure.
- [ ] Acceptance criteria имат traceable evidence.
- [ ] Има собствена автоматизирана проверка и negative case.
- [ ] Trade-offs/limitations са конкретни.
- [ ] Artifact/data/source/environment identity е отчетена.
- [ ] Студентът обяснява защо решението подобрява maintainability.

## 13. Критерии за оценяване

| Критерий | Точки |
|---|---:|
| Практическа guided реализация | 25 |
| Самостоятелна задача | 25 |
| Архитектурно/процесно решение | 20 |
| Automated tests | 15 |
| Анализ и trade-offs | 10 |
| Устна защита | 5 |
| **Общо** | **100** |

При lab01/02 реализация означава изпълним workflow/spec validation и engineering deliverables, не произволен допълнителен model. Green test без валиден contract не получава пълните test точки.

## 14. Въпроси за устна защита

1. **Защо F1 не е достатъчно requirement?**
   Очакван отговор: Нужни са dataset/split, error costs и други system constraints.

2. **Каква е разликата SLA/SLO?**
   Очакван отговор: SLO е цел; SLA е договорно обещание с условия/последствия.

3. **Кога latency числа са сравними?**
   Очакван отговор: При еднакви workload, hardware, protocol и измервателна точка.

4. **Как се тества explainability constraint?**
   Очакван отговор: Например contract за reason/limitations документация или стабилни allowed explanation fields.

5. **Какво е traceability?**
   Очакван отговор: Връзка stakeholder goal → requirement → implementation → test/evidence.

6. **Защо synthetic fairness е ограничено?**
   Очакван отговор: Не представя реални protected groups или социален контекст.

## 15. Връзка със следващото упражнение

Измеримите изисквания ограничават архитектурните варианти в lab03.
