# Преподавателски бележки — 8. MLOps и управление на модели и данни

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА.**

## 1. Концептуална цел

Dataset manifest → experiment journal → immutable registry → promotion/rollback. Студентът трябва да свърже инженерното решение с measurable quality и change/failure behavior.

В директория има model-final.joblib и model-final2.joblib. Вторият има по-висока accuracy, но е обучен върху различен split; dataset origin липсва. Екипът не може да избере release или да възстанови предишното поведение.

## 2. Какво НЕ е основната цел

Обучаване на най-точен classifier или hyperparameter tuning. Не оценявайте броя layers/classes/tools като качество. Търсете мотивирана граница, reproducibility и тестируем резултат.

## 3. Подготовка преди часа

- Изпълнете setup по README и `python -m pip check`; използвайте Python 3.12 и committed dataset.
- Проверете `python -m ai_platform.cli validate` от ai-platform.
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

В директория има model-final.joblib и model-final2.joblib. Вторият има по-висока accuracy, но е обучен върху различен split; dataset origin липсва. Екипът не може да избере release или да възстанови предишното поведение.

Поискайте students да предвидят failure mode преди изпълнение. Разграничете наблюдение, inference и недоказана хипотеза.

## 6. Ключови концепции

MLOps свързва ML lifecycle с автоматизация и operations. Experiment tracking пази parameters, metrics, artifacts и контекста на run; model registry управлява version identities и promotion state. Lineage проследява model → dataset → source/config/dependencies → evaluation. Име на файл и timestamp не са достатъчни за възпроизводимост.

В курса local immutable experiment journal е еквивалент на основните tracking функции: всеки version folder съдържа model и metadata с params/metrics/data hash/git commit/source hash/lock hash. Git+CSV manifest е малък data-versioning workflow, еквивалентен за този dataset на DVC pointer/content workflow. Не е разпределен MLflow/DVC service. Champion е избраната версия; challenger е кандидатът. Promotion сменя registry pointer, deployment сменя running immutable bundle. Rollback трябва да запази стария artifact и runtime compatibility; само alias update не променя вече работещия процес.

Свържете всяка концепция с конкретен code/document/test anchor, а не само с дефиниция.

## 7. Чести грешки

- Version е timestamp без metadata.
- Comparison върху различни tests.
- Hash се представя за цифров подпис.
- Promotion се смята за автоматичен deployment.
- Изтрит стар model след release.
- Registry pointer се използва като concurrent database.

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

В този lab акцентът е **Dataset manifest → experiment journal → immutable registry → promotion/rollback**. При lifecycle/SRS задачи диаграмата трябва да има decision/evidence gates; при code задачи — реални module dependencies и error contracts. Не налагайте microservice boundary без изискване.

## 10. Примерно решение

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА.**

FileRepository metadata.json предоставя basic tracking еквивалент; не го наричайте пълноценен multi-user MLflow registry. Очаквайте студентът да открие dirty/unavailable lineage и да поиска clean commit преди release. Примерен promotion record съдържа candidate/champion IDs, common dataset/split, metrics, measured latency, reviewer и rollback target.

Общият ai-platform съдържа runnable reference implementation за guided concepts. При проверка сравнявайте behavior, не source code равенство. Independent solutions трябва да добавят собствен contract/test, който не е просто изпълнение на готовия reference.

## 11. Очаквани тестове

- `pytest tests/test_repository.py tests/test_training.py -q`
- `python -m ai_platform.cli validate`

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

1. **Какво прави comparison честно?**
   Очакван отговор: Еднакви evaluation data/protocol и ясно разкрити различия.

2. **Какво не доказва hash?**
   Очакван отговор: Доверен origin/publisher, ако metadata също е подменена.

3. **Защо git SHA е недостатъчен при dirty tree?**
   Очакван отговор: Некомитнатият код не се идентифицира от commit-а.

4. **Какво е разликата promotion/deployment?**
   Очакван отговор: Избор в registry срещу промяна на serving release.

5. **Защо rollback изисква old runtime?**
   Очакван отговор: Serialized model може да не е съвместим с новите зависимости.

6. **Кога да преминем към MLflow/DVC?**
   Очакван отговор: При множество users, големи artifacts/data, shared storage и operational governance.

## 15. Връзка със следващото упражнение

След като версиите са проследими, observability може да свързва поведението с конкретен release.
