# Преподавателски бележки — 10. Сигурност, етика, технически дълг и поддръжка

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА.**

## 1. Концептуална цел

API/model/data trust boundaries → risk/debt register → maintenance/retirement. Студентът трябва да свърже инженерното решение с measurable quality и change/failure behavior.

Review открива secret в source, стар model artifact без version, недокументиран dataset, sensitive logs и deprecated dependency. Високият F1 не отговаря дали системата е безопасна, поддържаема или подходяща за употреба.

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

Review открива secret в source, стар model artifact без version, недокументиран dataset, sensitive logs и deprecated dependency. Високият F1 не отговаря дали системата е безопасна, поддържаема или подходяща за употреба.

Поискайте students да предвидят failure mode преди изпълнение. Разграничете наблюдение, inference и недоказана хипотеза.

## 6. Ключови концепции

Secure coding включва input validation, server-side access control, bounded requests, trusted artifacts и secret handling. Pickle/joblib loading може да изпълни код; checksum не прави непознат artifact безопасен. API key е учебен access mechanism, не пълна identity/authorization система. Sensitive inputs не се логват; retention и deletion трябва да имат policy и owner.

Responsible AI изисква intended use, limitations, data provenance, fairness/explainability reasoning и human oversight според риска. Synthetic cohort A/B показва mechanics на slice analysis, не сертифицира fairness. Technical debt включва code/dependency debt, model debt (неясен lifecycle/validation) и data debt (липсващ provenance/quality ownership). Deprecation има срок, migration path и измерване на usage; retirement включва отказ на стар model, запазване/изтриване на artifacts според policy и комуникация с users.

Свържете всяка концепция с конкретен code/document/test anchor, а не само с дефиниция.

## 7. Чести грешки

- Fix на secret само чрез delete от source.
- Hash се приема за trusted signature.
- Fairness се приравнява на обща accuracy.
- Technical debt без owner/срок.
- Deprecated package се нарича CVE без evidence.
- Retirement е само изтриване на model файла.

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

В този lab акцентът е **API/model/data trust boundaries → risk/debt register → maintenance/retirement**. При lifecycle/SRS задачи диаграмата трябва да има decision/evidence gates; при code задачи — реални module dependencies и error contracts. Не налагайте microservice boundary без изискване.

## 10. Примерно решение

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА.**

Очакван register включва поне fixture secret, missing model identity, stale validation, undocumented data, sensitive logs и deprecated adapter. За model card разграничете synthetic demo от реална fairness оценка. Independent retirement gate може да е explicit approved/retired policy manifest, проверяван преди deployment; не изисквайте разрушително изтриване на artifacts.

Общият ai-platform съдържа runnable reference implementation за guided concepts. При проверка сравнявайте behavior, не source code равенство. Independent solutions трябва да добавят собствен contract/test, който не е просто изпълнение на готовия reference.

## 11. Очаквани тестове

- `pytest tests/test_api.py tests/test_repository.py -q`
- `python -m pip check`

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

1. **Защо checksum не обезопасява pickle?**
   Очакван отговор: Attacker може да предостави съответстващ hash; десериализацията остава executable.

2. **Какво следва след изтекъл real secret?**
   Очакван отговор: Revocation/rotation и incident review; само изтриване на файла не стига.

3. **Как ethical requirement става проверим?**
   Очакван отговор: С конкретен constraint, population/context, evidence и human-review boundary.

4. **Защо възрастта не е единствен retirement критерий?**
   Очакван отговор: Важни са current validity, data/context change, support и policy.

5. **Как измерваме debt?**
   Очакван отговор: Риск/impact, remediation effort, recurrence, owner, срок и acceptance evidence.

6. **Кога rollback е забранен?**
   Очакван отговор: Когато старата версия нарушава security/ethics/regulatory policy, въпреки че е технически работеща.

## 15. Връзка със следващото упражнение

Курсът завършва с проверим release, експлоатационен plan и прозрачни ограничения, а не с нов ML алгоритъм.
