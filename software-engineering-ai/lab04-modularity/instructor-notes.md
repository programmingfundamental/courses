# Преподавателски бележки — 4. Модулност, слоеве и разделяне на отговорностите

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА.**

## 1. Концептуална цел

api → inference port → repository adapter; data/features отделно. Студентът трябва да свърже инженерното решение с measurable quality и change/failure behavior.

Една функция чете CSV, избира модел, обучава го и връща business result. За да тествате един prediction branch, трябва да имате filesystem, dataset и training runtime. Промяната на storage се разпространява до API.

## 2. Какво НЕ е основната цел

Обучаване на най-точен classifier или hyperparameter tuning. Не оценявайте броя layers/classes/tools като качество. Търсете мотивирана граница, reproducibility и тестируем резултат.

## 3. Подготовка преди часа

- Изпълнете setup по README и `python -m pip check`; използвайте Python 3.12 и committed dataset.
- Проверете `python ../lab04-modularity/starter/coupled.py` от ai-platform.
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

Една функция чете CSV, избира модел, обучава го и връща business result. За да тествате един prediction branch, трябва да имате filesystem, dataset и training runtime. Промяната на storage се разпространява до API.

Поискайте students да предвидят failure mode преди изпълнение. Разграничете наблюдение, inference и недоказана хипотеза.

## 6. Ключови концепции

Модулността намалява броя причини за промяна на компонент и позволява независими tests. Domain logic описва бизнес правила; infrastructure се занимава с filesystem, network, serialization и framework lifecycle. Dependency inversion означава високото ниво да зависи от contract, а adapter да реализира този contract.

Python Protocol описва structural typing: подходящ object удовлетворява интерфейса без общ base class. Dependency injection подава collaborator отвън; не изисква DI framework. Configuration е validated input на приложението, не глобален набор от hardcoded paths. Добрата граница следва отговорност и change rate, а не произволно разпределяне на функции в много файлове. Модулите data/features/training/inference/api/config имат различни allowed dependencies.

Свържете всяка концепция с конкретен code/document/test anchor, а не само с дефиниция.

## 7. Чести грешки

- Разделяне само по дължина на файла.
- Global state вместо injection.
- Protocol с десетки ненужни методи.
- API познава filesystem layout.
- Fake винаги връща успех.
- Configuration се чете при всеки prediction.

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

В този lab акцентът е **api → inference port → repository adapter; data/features отделно**. При lifecycle/SRS задачи диаграмата трябва да има decision/evidence gates; при code задачи — реални module dependencies и error contracts. Не налагайте microservice boundary без изискване.

## 10. Примерно решение

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА.**

Примерна зависимост: Predictor(ModelRepository,version); repository.load връща Bundle. Fake може да има calls list и фиксиран estimator с predict_proba. За independent tracker очаквайте injection в offline orchestration, strict metadata schema и policy дали tracking failure блокира release; файлът не трябва да се записва от API.

Общият ai-platform съдържа runnable reference implementation за guided concepts. При проверка сравнявайте behavior, не source code равенство. Independent solutions трябва да добавят собствен contract/test, който не е просто изпълнение на готовия reference.

## 11. Очаквани тестове

- `pytest tests/test_repository.py tests/test_api.py -q`
- `python ../lab04-modularity/starter/coupled.py`

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

1. **Защо много файлове не означават modularity?**
   Очакван отговор: Отговорностите и dependencies може да останат същите.

2. **Как Protocol помага за tests?**
   Очакван отговор: Позволява малък fake, който реализира нужния contract.

3. **Къде принадлежи joblib loading?**
   Очакван отговор: В infrastructure repository adapter, не в HTTP controller.

4. **Кой притежава model lifetime?**
   Очакван отговор: Serving application lifecycle, с load once и explicit version.

5. **Кога mock вреди?**
   Очакван отговор: Когато възпроизвежда implementation и скрива contract несъвместимости.

6. **Как се избягва circular dependency?**
   Очакван отговор: Ports/types се поставят на стабилна граница, dependencies сочат еднопосочно.

## 15. Връзка със следващото упражнение

След ясните modules можем да оценим кога design patterns намаляват реален coupling.
