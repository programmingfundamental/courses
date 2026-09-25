# Преподавателски бележки — 5. Design Patterns и принципи за качествен код

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА.**

## 1. Концептуална цел

Training orchestration → estimator Strategy/Factory → sklearn Pipeline. Студентът трябва да свърже инженерното решение с measurable quality и change/failure behavior.

При всеки нов модел екипът редактира if/elif блокове в training, evaluation и serving. Част от branches използват различна preprocessing логика. Новият вариант работи в notebook, но чупи общия contract.

## 2. Какво НЕ е основната цел

Обучаване на най-точен classifier или hyperparameter tuning. Не оценявайте броя layers/classes/tools като качество. Търсете мотивирана граница, reproducibility и тестируем резултат.

## 3. Подготовка преди часа

- Изпълнете setup по README и `python -m pip check`; използвайте Python 3.12 и committed dataset.
- Проверете `python ../lab05-design-patterns/starter/conditional.py` от ai-platform.
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

При всеки нов модел екипът редактира if/elif блокове в training, evaluation и serving. Част от branches използват различна preprocessing логика. Новият вариант работи в notebook, но чупи общия contract.

Поискайте students да предвидят failure mode преди изпълнение. Разграничете наблюдение, inference и недоказана хипотеза.

## 6. Ключови концепции

SOLID е набор от design heuristics: single responsibility, open/closed, substitutability, interface segregation и dependency inversion. DRY цели един източник на знание, не забранява всяка повторена линия. KISS пази простотата, YAGNI отлага speculative features.

Strategy капсулира взаимозаменяемо поведение; Factory избира/създава implementation; Adapter уеднаквява чужд interface; Repository отделя storage; Pipeline организира последователни transformations. Pattern е полезен, ако създава stable extension point с реална нужда. За три малки варианта registry от callables е достатъчен; hierarchy от абстрактни класове може да увеличи complexity без стойност. Единният sklearn estimator contract позволява orchestration да не знае дали моделът е linear/tree/forest.

Свържете всяка концепция с конкретен code/document/test anchor, а не само с дефиниция.

## 7. Чести грешки

- Patterns за всяка функция.
- DRY събира несвързана логика.
- Factory връща shared fitted instance.
- Няма unknown-name error.
- API връща estimator-specific output.
- Сравняване само на accuracy вместо design cost.

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

В този lab акцентът е **Training orchestration → estimator Strategy/Factory → sklearn Pipeline**. При lifecycle/SRS задачи диаграмата трябва да има decision/evidence gates; при code задачи — реални module dependencies и error contracts. Не налагайте microservice boundary без изискване.

## 10. Примерно решение

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА.**

Reference registry FACTORIES е достатъчен; не изисквайте class hierarchy. Independent adapter трябва да гарантира class/probability ordering, seed configuration и fresh estimator instance. Оценете extension diff: allowed registration и new implementation, без нов if във train.

Общият ai-platform съдържа runnable reference implementation за guided concepts. При проверка сравнявайте behavior, не source code равенство. Independent solutions трябва да добавят собствен contract/test, който не е просто изпълнение на готовия reference.

## 11. Очаквани тестове

- `pytest tests/test_training.py -q`
- `python ../lab05-design-patterns/starter/conditional.py`

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

1. **Кога Strategy е полезен?**
   Очакван отговор: При реални взаимозаменяеми поведения със стабилен contract.

2. **Какво означава open/closed тук?**
   Очакван отговор: Нов implementation се включва без промяна на core orchestration.

3. **Кога DRY е вредно приложен?**
   Очакван отговор: Когато обединява различни concepts само защото кодът изглежда сходен.

4. **Защо contract test е нужен?**
   Очакван отговор: Еднакви method names не гарантират еднаква semantics.

5. **Как Adapter се различава от Factory?**
   Очакван отговор: Adapter превежда contract; Factory създава/избира object.

6. **Защо DummyClassifier е полезен без висок F1?**
   Очакван отговор: Дава baseline и проверява архитектурната extensibility, не печели моделно състезание.

## 15. Връзка със следващото упражнение

Стабилните contracts позволяват test pyramid, която покрива повече от happy path.
