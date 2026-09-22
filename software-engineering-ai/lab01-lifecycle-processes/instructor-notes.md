# Преподавателски бележки — 1. Софтуерен жизнен цикъл и инженерни процеси

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА.**

## 1. Концептуална цел

Notebook → engineering backlog → reproducible pipeline. Студентът трябва да свърже инженерното решение с measurable quality и change/failure behavior.

Експериментът има висок training score, но само авторът може да го стартира. Dataset path е в клетка, няма tests и никой не знае дали последният модел отговаря на последната версия на кода. Екипът трябва да организира прехода към поддържана система.

## 2. Какво НЕ е основната цел

Обучаване на най-точен classifier или hyperparameter tuning. Не оценявайте броя layers/classes/tools като качество. Търсете мотивирана граница, reproducibility и тестируем резултат.

## 3. Подготовка преди часа

- Изпълнете setup по README и `python -m pip check`; използвайте Python 3.12 и committed dataset.
- Проверете `python scripts/check_notebook.py` от ai-platform.
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

Експериментът има висок training score, но само авторът може да го стартира. Dataset path е в клетка, няма tests и никой не знае дали последният модел отговаря на последната версия на кода. Екипът трябва да организира прехода към поддържана система.

Поискайте students да предвидят failure mode преди изпълнение. Разграничете наблюдение, inference и недоказана хипотеза.

## 6. Ключови концепции

SDLC (software development lifecycle) свързва requirements, design, implementation, verification, deployment, operation и retirement. AI lifecycle добавя data acquisition/validation, experimentation, model evaluation и наблюдение на статистическо качество. Итеративното развитие означава малки проверими increments; Agile не премахва документацията и acceptance criteria.

Research code оптимизира скоростта на проверка на хипотеза. Production code има users, contracts, versioned artifacts, error behavior и operational owner. Reproducibility изисква data identity, code/config/dependency identity, seed, split и инструкции; seed сам по себе си не е достатъчен. Technical debt е бъдеща цена от днешно решение; записвайте consequence, trigger, owner и repayment test. Definition of done (DoD) е обща проверка за завършен increment, а acceptance criteria са конкретни за отделното requirement.

Свържете всяка концепция с конкретен code/document/test anchor, а не само с дефиниция.

## 7. Чести грешки

- Обучаване на нови модели вместо engineering анализ.
- Training score се приема за продуктово качество.
- Backlog без acceptance criteria.
- Roadmap със задачи без зависимости.
- DoD е „кодът работи при мен“.
- Няма операция след deployment.

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

В този lab акцентът е **Notebook → engineering backlog → reproducible pipeline**. При lifecycle/SRS задачи диаграмата трябва да има decision/evidence gates; при code задачи — реални module dependencies и error contracts. Не налагайте microservice boundary без изискване.

## 10. Примерно решение

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА.**

Очаквайте lifecycle с verification gates, не само списък от Agile ceremonies. Примерен backlog: path/config, dataset manifest, held-out evaluation, module boundaries, contract tests, artifact version, build automation, readiness и owner. Примерен DoD item има id, evidence command, owner и pass criterion; автоматичната проверка не замества review на качеството.

Общият ai-platform съдържа runnable reference implementation за guided concepts. При проверка сравнявайте behavior, не source code равенство. Independent solutions трябва да добавят собствен contract/test, който не е просто изпълнение на готовия reference.

## 11. Очаквани тестове

- `python scripts/check_notebook.py`
- `pytest tests/test_data.py -q`

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

1. **Защо training score не е DoD?**
   Очакван отговор: Не измерва held-out quality, API contract или operational readiness.

2. **Как lifecycle се различава от pipeline?**
   Очакван отговор: Lifecycle включва човешки решения и feedback; pipeline автоматизира част от дейностите.

3. **Кога technical debt е приемлив?**
   Очакван отговор: Когато е съзнателен, ограничен и има owner/trigger за изплащане.

4. **Кои identities са нужни за reproducibility?**
   Очакван отговор: Data, source, environment, config, split/seed и artifact.

5. **Защо deployment не е краят?**
   Очакван отговор: Нужни са monitoring, maintenance, rollback и retirement.

6. **Какво е vertical slice?**
   Очакван отговор: Малък end-to-end increment с реална стойност и acceptance evidence.

## 15. Връзка със следващото упражнение

Backlog проблемите се превръщат в измерими requirements в lab02.
