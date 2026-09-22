# Преподавателски бележки — 7. Version Control, CI/CD и автоматизация

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА.**

## 1. Концептуална цел

Commit → lint → tests → model gate → immutable image → local deployment. Студентът трябва да свърже инженерното решение с measurable quality и change/failure behavior.

На лаптопа build-ът минава, но CI инсталира различни dependencies и моделът не се зарежда. Deployment използва tag latest и няма начин да се докаже кой artifact е бил serving преди регресията.

## 2. Какво НЕ е основната цел

Обучаване на най-точен classifier или hyperparameter tuning. Не оценявайте броя layers/classes/tools като качество. Търсете мотивирана граница, reproducibility и тестируем резултат.

## 3. Подготовка преди часа

- Изпълнете setup по README и `python -m pip check`; използвайте Python 3.12 и committed dataset.
- Проверете `python -m pip check` от ai-platform.
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

На лаптопа build-ът минава, но CI инсталира различни dependencies и моделът не се зарежда. Deployment използва tag latest и няма начин да се докаже кой artifact е бил serving преди регресията.

Поискайте students да предвидят failure mode преди изпълнение. Разграничете наблюдение, inference и недоказана хипотеза.

## 6. Ключови концепции

CI интегрира малки промени с автоматични проверки; continuous delivery подготвя release с контролирана promotion/deployment стъпка, а continuous deployment публикува автоматично след gates. Pull request review оценява design, tests и риск; branch е изолирана линия работа, не заместител на review.

Semantic versioning следва compatibility на публичния API, докато model version идентифицира конкретен trained artifact; те не са едно и също. Lock file pin-ва resolved dependencies; reproducibility допълнително зависи от Python/OS и base image. Immutable release съдържа конкретен model bundle и image ID/digest. Tag latest е подвижен pointer. Coverage е quality signal, не доказателство за correctness. Deployment readiness gate проверява, че service може да обслужва request, не само че process съществува.

Свържете всяка концепция с конкретен code/document/test anchor, а не само с дефиниция.

## 7. Чести грешки

- Unpinned pip install.
- CI само echo вместо exit-code gate.
- Image latest вместо immutable identity.
- Secrets в Dockerfile.
- Deployment без readiness.
- Публикуване на PR secrets към untrusted code.

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

В този lab акцентът е **Commit → lint → tests → model gate → immutable image → local deployment**. При lifecycle/SRS задачи диаграмата трябва да има decision/evidence gates; при code задачи — реални module dependencies и error contracts. Не налагайте microservice boundary без изискване.

## 10. Примерно решение

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА.**

Workflow е реално добавен в parent repository; при standalone копиране трябва да се адаптира working-directory и paths. Gate order: install/lint/unit/integration/data/course/train+quality/build/smoke. Independent gate може да проверява metadata fields и dependency denylist fixture; CVE audit е допълнителен, а не да се представя pip check като security audit.

Общият ai-platform съдържа runnable reference implementation за guided concepts. При проверка сравнявайте behavior, не source code равенство. Independent solutions трябва да добавят собствен contract/test, който не е просто изпълнение на готовия reference.

## 11. Очаквани тестове

- `ruff check src tests scripts`
- `pytest --cov=ai_platform --cov-fail-under=85 -q`

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

1. **Как CI се различава от CD?**
   Очакван отговор: CI валидира интеграция; CD подготвя/изпълнява delivery и deployment.

2. **Защо latest не е release identity?**
   Очакван отговор: Tag може да сочи нов image без промяна на името.

3. **Какво не доказва pip check?**
   Очакван отговор: Не проверява vulnerability advisories или reachability.

4. **Защо model version и API SemVer са различни?**
   Очакван отговор: Едното е artifact identity, другото compatibility contract.

5. **Защо secret не се bake-ва в image?**
   Очакван отговор: Image layers/registry могат да го разкрият.

6. **Каква е rollback acceptance проверката?**
   Очакван отговор: Старият image/model version отново е ready и спазва contract.

## 15. Връзка със следващото упражнение

CI automation трябва да проследява data/experiments/models и да управлява promotion решения в lab08.
