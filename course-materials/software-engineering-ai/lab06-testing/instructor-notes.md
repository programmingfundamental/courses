# Преподавателски бележки — 6. Тестване на софтуер и AI компоненти

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА.**

## 1. Концептуална цел

Data contract → preprocessing → artifact → service → HTTP. Студентът трябва да свърже инженерното решение с measurable quality и change/failure behavior.

API тестът проверява само дали отговорът е 200. Повреден artifact, missing feature и променен feature order остават незабелязани. В друг test качеството варира, защото dataset split се сменя при всяко изпълнение.

## 2. Какво НЕ е основната цел

Обучаване на най-точен classifier или hyperparameter tuning. Не оценявайте броя layers/classes/tools като качество. Търсете мотивирана граница, reproducibility и тестируем резултат.

## 3. Подготовка преди часа

- Изпълнете setup по README и `python -m pip check`; използвайте Python 3.12 и committed dataset.
- Проверете `pytest ../lab06-testing/starter/test_happy_path.py -q` от ai-platform.
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

API тестът проверява само дали отговорът е 200. Повреден artifact, missing feature и променен feature order остават незабелязани. В друг test качеството варира, защото dataset split се сменя при всяко изпълнение.

Поискайте students да предвидят failure mode преди изпълнение. Разграничете наблюдение, inference и недоказана хипотеза.

## 6. Ключови концепции

Unit test изолира малка отговорност, integration test проверява collaborators/IO, end-to-end test преминава реалните deployment boundaries. Contract test фиксира observable interface. Mocking е полезен за failure injection, но не доказва истинска serialization/serving compatibility.

Детерминистичният contract може да има exact assertions: missing feature →422, missing model→503. Statistical quality test има versioned held-out dataset, threshold и support; фиксираният seed не прави качеството универсална гаранция. Test pyramid държи много бързи проверки и малко скъпи end-to-end runs. Data drift е промяна на входното разпределение; model/concept drift засяга връзката между входове и цел. Mean-shift smoke сигнал е евтин индикатор, не доказателство за спад на F1; нужни са labels и наблюдение във времето.

Свържете всяка концепция с конкретен code/document/test anchor, а не само с дефиниция.

## 7. Чести грешки

- Happy path only.
- 100% coverage се нарича пълно качество.
- Random split без identity.
- Quality test върху training data.
- Unit tests правят network calls.
- Mean shift се обявява за model drift доказателство.

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

В този lab акцентът е **Data contract → preprocessing → artifact → service → HTTP**. При lifecycle/SRS задачи диаграмата трябва да има decision/evidence gates; при code задачи — реални module dependencies и error contracts. Не налагайте microservice boundary без изискване.

## 10. Примерно решение

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА.**

Common suite вече съдържа 41 baseline cases; студентът трябва да добави нови tests и mutation evidence. Добри independent additions: inference exception→503 без payload leakage, minimal class support, version mismatch after restart, fake repository load-once behavior. Не приемайте тест, който копира implementation формулата.

Общият ai-platform съдържа runnable reference implementation за guided concepts. При проверка сравнявайте behavior, не source code равенство. Independent solutions трябва да добавят собствен contract/test, който не е просто изпълнение на готовия reference.

## 11. Очаквани тестове

- `pytest -q --cov=ai_platform --cov-fail-under=85`
- `pytest ../lab06-testing/starter/test_happy_path.py -q`

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

1. **Защо mocking не доказва artifact compatibility?**
   Очакван отговор: Не зарежда действителния serialized model/runtime.

2. **Как се прави reproducible quality test?**
   Очакван отговор: Фиксирани data/code/config/dependencies/split с explicit threshold.

3. **Какво доказва drift smoke?**
   Очакван отговор: Чувствителност към избрана промяна на input distribution, не concept drift.

4. **Защо coverage не е достатъчно?**
   Очакван отговор: Executed lines може да нямат силни assertions и negative cases.

5. **Къде е E2E boundary?**
   Очакван отговор: Реален client → containerized API → реален artifact и response.

6. **Кога exact prediction assertion е риск?**
   Очакван отговор: Когато implementation може легитимно да се смени при запазен contract/quality.

## 15. Връзка със следващото упражнение

Тестовете стават автоматични quality gates на всеки commit в lab07.
