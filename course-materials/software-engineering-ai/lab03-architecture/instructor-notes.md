# Преподавателски бележки — 3. Софтуерна архитектура и архитектурни стилове

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА.**

## 1. Концептуална цел

Data → offline training → immutable bundle → serving boundary. Студентът трябва да свърже инженерното решение с measurable quality и change/failure behavior.

Монолитният Python script обучава модел при import и после стартира API. Един restart на serving води до ново обучение; промяна в dataset променя production поведението без release.

## 2. Какво НЕ е основната цел

Обучаване на най-точен classifier или hyperparameter tuning. Не оценявайте броя layers/classes/tools като качество. Търсете мотивирана граница, reproducibility и тестируем резултат.

## 3. Подготовка преди часа

- Изпълнете setup по README и `python -m pip check`; използвайте Python 3.12 и committed dataset.
- Проверете `python ../lab03-architecture/starter/monolith.py --smoke` от ai-platform.
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

Монолитният Python script обучава модел при import и после стартира API. Един restart на serving води до ново обучение; промяна в dataset променя production поведението без release.

Поискайте students да предвидят failure mode преди изпълнение. Разграничете наблюдение, inference и недоказана хипотеза.

## 6. Ключови концепции

Architecture описва отговорности, dependencies, deployment units и качествени компромиси. Layered architecture разделя API/application/domain/infrastructure; pipeline architecture описва последователни transformation stages. Client–server отделя потребител от услуга, а service-oriented design поставя network boundaries между capabilities. Microservices са operational избор, не синоним на модулност.

Coupling е зависимост между компоненти; cohesion — доколко една отговорност е събрана на едно място. Batch inference обработва набори извън интерактивен request, online inference има latency/availability contract. Embedded model има по-малка network сложност; separate inference service позволява независимо scaling/version lifecycle, но добавя timeout, serialization, auth и observability нужди. Model artifact е versioned boundary между training и serving.

Свържете всяка концепция с конкретен code/document/test anchor, а не само с дефиниция.

## 7. Чести грешки

- Диаграма без artifact/schema boundary.
- Microservices без operational капацитет.
- Training при import.
- Promotion се приема за hot reload.
- HTTP API връща sklearn-specific objects.
- Design без failure path.

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

В този lab акцентът е **Data → offline training → immutable bundle → serving boundary**. При lifecycle/SRS задачи диаграмата трябва да има decision/evidence gates; при code задачи — реални module dependencies и error contracts. Не налагайте microservice boundary без изискване.

## 10. Примерно решение

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА.**

Очаквана минимална архитектура: offline CLI training, local registry, FastAPI с load-once serving. За сценария embedded estimator в API process е разумен избор; отделен service също се приема при конкретни допълнителни ограничения. Boundary test може да patch-не training entry point да fail-не при import, но по-силен тест стартира serving само с artifact и без data.

Общият ai-platform съдържа runnable reference implementation за guided concepts. При проверка сравнявайте behavior, не source code равенство. Independent solutions трябва да добавят собствен contract/test, който не е просто изпълнение на готовия reference.

## 11. Очаквани тестове

- `pytest tests/test_api.py -q`
- `python ../lab03-architecture/starter/monolith.py --smoke`

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

1. **Каква е разликата module/service?**
   Очакван отговор: Module е code boundary; service е runtime/network boundary.

2. **Защо serving не обучава при request?**
   Очакван отговор: Нарушава latency, reproducibility и release control.

3. **Защо artifact contract включва schema?**
   Очакван отговор: Prediction semantics зависят от имената/реда на features.

4. **Кога separate inference е оправдан?**
   Очакван отговор: При реална нужда от независимо scaling, owners или lifecycle.

5. **Какво съдържа ADR?**
   Очакван отговор: Контекст, решение, alternatives, consequences и trigger за преразглеждане.

6. **Защо promotion не е deployment?**
   Очакван отговор: Registry alias не сменя автоматично immutable version в работещия процес.

## 15. Връзка със следващото упражнение

Архитектурните граници стават реални Python modules и dependency direction в lab04.
