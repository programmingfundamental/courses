# Преподавателски бележки — 9. Наблюдаемост, надеждност и управление на грешки

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА.**

## 1. Концептуална цел

Request → structured event + metrics → readiness → diagnostic decision. Студентът трябва да свърже инженерното решение с measurable quality и change/failure behavior.

След deployment p95 latency е пет пъти по-висока. Екипът вижда само „server started“ в console. Не е ясно дали причината е model compute, batch size, опашка, dependency или промяна в входните данни.

## 2. Какво НЕ е основната цел

Обучаване на най-точен classifier или hyperparameter tuning. Не оценявайте броя layers/classes/tools като качество. Търсете мотивирана граница, reproducibility и тестируем резултат.

## 3. Подготовка преди часа

- Изпълнете setup по README и `python -m pip check`; използвайте Python 3.12 и committed dataset.
- Проверете `pytest tests/test_observability.py -q` от ai-platform.
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

След deployment p95 latency е пет пъти по-висока. Екипът вижда само „server started“ в console. Не е ясно дали причината е model compute, batch size, опашка, dependency или промяна в входните данни.

Поискайте students да предвидят failure mode преди изпълнение. Разграничете наблюдение, inference и недоказана хипотеза.

## 6. Ключови концепции

Observability извежда вътрешното състояние от logs, metrics и traces. Logs са събития с контекст; metrics са агрегирани измервания; distributed tracing свързва spans на една операция през services. Prometheus/OpenTelemetry са стандартни ecosystems; този малък app предоставя JSON metrics, не се представя като Prometheus exporter.

Liveness показва, че process работи; readiness — че може да обслужва смислено. Request count/error count/latency и model version са базовите сигнали. Избягвайте unbounded labels като user ID/token. Retry е подходящ само за transient failure и ограничен budget; може да увеличи overload. Timeout без cancellation не спира непременно CPU работа. Graceful degradation трябва да е explicit, например 503 вместо fabricated prediction. Data drift не означава автоматично model drift; quality monitoring изисква delayed labels и evaluation protocol.

Свържете всяка концепция с конкретен code/document/test anchor, а не само с дефиниция.

## 7. Чести грешки

- Логване на целия request.
- Всички exceptions стават label0.
- Metrics без version.
- p95 без sample count.
- Retry на schema error.
- Data drift се обявява за quality failure без labels.

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

В този lab акцентът е **Request → structured event + metrics → readiness → diagnostic decision**. При lifecycle/SRS задачи диаграмата трябва да има decision/evidence gates; при code задачи — реални module dependencies и error contracts. Не налагайте microservice boundary без изискване.

## 10. Примерно решение

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА.**

Baseline metrics измерват server request duration за /predict, включително откази, и пазят последни1000 samples; не са histogram over long-term window. Benchmark е sequential client-observed latency. Очаквайте plan да провери batch/concurrency/hardware/queue/model/runtime/data; фактът за batch 16 е diagnostic clue, не окончателно доказателство.

Общият ai-platform съдържа runnable reference implementation за guided concepts. При проверка сравнявайте behavior, не source code равенство. Independent solutions трябва да добавят собствен contract/test, който не е просто изпълнение на готовия reference.

## 11. Очаквани тестове

- `pytest tests/test_observability.py tests/test_api.py -q`
- `python scripts/benchmark.py --count 100`

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

1. **Защо live200 не означава ready?**
   Очакван отговор: Process може да работи без usable model/credentials.

2. **Защо model_version е metric dimension?**
   Очакван отговор: Позволява свързване на regression с release.

3. **Какъв е рискът от user-ID metric label?**
   Очакван отговор: Unbounded cardinality и privacy exposure.

4. **Защо timeout не гарантира cancellation?**
   Очакван отговор: Background/CPU task може да продължи след отказа на клиента.

5. **Как се различава drift от performance incident?**
   Очакван отговор: Първото е статистическа промяна на data/quality, второто operational поведение.

6. **Какво означава safe degradation?**
   Очакван отговор: Ясен отказ/limited function с договор, не измислена успешна prediction.

## 15. Връзка със следващото упражнение

Последният lab разглежда сигурността, етиката и дълга като постоянни engineering отговорности.
