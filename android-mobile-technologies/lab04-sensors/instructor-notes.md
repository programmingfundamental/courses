# Lab 4 — Бележки за преподавателя

Само за преподавателя; не се публикува в сайта.

## 1. Концептуална цел

Шум, sampling, lifetime и data loss са част от sensor signal interpretation. SensorManager синтаксисът е второстепенен спрямо ownership и обясним detector.

## 2. Какво НЕ е основна цел

Не разработвайте физичен модел на orientation, ML classifier или chart library. Няма нужда от gyroscope за задължителната задача; UI числови стойности и малък bounded trace са достатъчни.

## 3. Предварителна подготовка на преподавателя

Проверете accelerometer на поне един телефон, emulator virtual sensors и fake trace adapter. Подгответе stationary/rotation/10 ms spike/two-burst traces с labels и фиксирани timestamps. Осигурете Room migration scaffold и празни detector interfaces. До 100 Hz няма нужда от runtime sensor permission в избраната конфигурация; липсата на sensor се симулира чрез fake capability.

## 4. Разпределение на времето

| Дейност | Минути |
|---|---:|
| Проблем | 8 |
| Теория | 12 |
| Sensor/noise demo | 10 |
| Воден monitor/filter | 35 |
| Checkpoint | 5 |
| Самостоятелен detector | 35 |
| Edge cases/tests | 15 |
| Измерване | 10 |
| Анализ | 5 |
| **Общо** | **135** |

Подгответе прост UI scaffold предварително. Самостоятелните 35 минути са за algorithm/state decisions, не за рисуване на графики.

## 5. Как да се въведе проблемът

Телефонът не се движи — защо стойностите не са нула? Ако имаме 100 samples/s, колко UI updates са нужни? Кой спира sensor-а, когато screen изчезне?

## 6. Основни точки за обяснение

Device axes и gravity; requested/observed rates; borrowed SensorEvent values; filter latency; event detection преди presentation sampling; bounded queue/drop policy; cancellation до awaitClose; един hardware subscription независимо от downstream consumers.

## 7. Чести грешки

- SensorEvent или values array се пазят без snapshot.
- Raw samples се добавят в unbounded list.
- DB insert се изпълнява в callback.
- Listener остава регистриран след STOP.
- UI и persistence създават два cold-flow subscriptions.
- Threshold се прилага след UI throttling и пропуска motion.
- Gravity се интерпретира като shake.
- Drop-oldest queue се отчита погрешно като без загуби само защото trySend успява.

## 8. Насочващи въпроси

Кой притежава listener instance? Къде се копират bytes/values? Какъв е expected response на constant signal? Как ще различите кратък spike от motion? Как доказвате, че count не зависи от UI frequency?

## 9. Очаквана архитектура

```text
Lifecycle/user intent -> session controller
                             |
real/fake SensorSource -> bounded queue -> pure filter -> detector
                                               |           |
                                     UI projection     Room events
```

Един controller cancel-ва source; repository изолира platform callback. Clock и filters са тестируеми без hardware. DB writer е отделен и bounded.

## 10. Ключови части от примерно решение

Водената част копира timestamp/x/y/z в callback и unregister-ва в awaitClose. Low-pass използва actual dt, initialization от първия sample и constant-size state.

**НЕ показвай директно на студентите преди самостоятелната задача:** възможен detector оценява norm на high-pass residual след gravity low-pass, изисква превишаване за минимална продължителност/няколко consecutive samples, после cooldown и hysteresis за re-arm. Това е един вариант, не единствен верен алгоритъм. Cooldown е по monotonic time, не sample count. Event се записва с stable ID; Room transaction insert+trim запазва последните 100, а presentation използва отделно latest count.

## 11. Как да се демонстрират проблемните сценарии

Fake source връща Unsupported. Slow consumer и replay 100 Hz запълват capacity=128 и показват drops. Rotate 20 пъти и background-нете; active listener count трябва да се върне на 0. Подайте NaN и backward timestamp. Spike trace има пик за 10 ms, а sustained burst — по-дълъг маркиран интервал; те трябва да се разграничават по аргументиран критерий. Real device rotation демонстрира axis change без задължителен shake event.

## 12. Очаквани наблюдения

Observed rate варира; UI rate е по-нисък. Filter намалява noise, но забавя response. При overload се появяват отчетени drops, а memory остава bounded. При реален device trace не обещавайте нула false positives — сравнявайте labelled scenarios и ограничения. Fake correctness не доказва accuracy на физическия sensor.

## 13. Проверка на самостоятелната задача

- [ ] Алгоритъмът и units на threshold са описани.
- [ ] Има duration/window защита и cooldown, не само един if.
- [ ] Stationary/spike/bursts tests са предадени.
- [ ] Event count не зависи от Compose update frequency.
- [ ] Room history и migration са коректни и bounded.
- [ ] При STOP няма hardware subscription.

## 14. Оценяване

| Област | Точки |
|---|---:|
| Воден monitor/filter | 25 |
| Самостоятелен detector | 35 |
| Архитектура/code quality | 15 |
| Robustness/tests | 10 |
| Анализ/измервания | 10 |
| Устна защита | 5 |
| **Общо** | **100** |

## 15. Въпроси за устна защита

1. Защо magnitude при покой е около g? — Accelerometer включва gravity contribution.
2. Защо timestamp е важен за filter? — Sampling не е идеално постоянен; dt влияе на response.
3. Как спирате listener? — Cancellation достига awaitClose с точния instance.
4. Защо UI sampling не е преди detector? — Може да премахне кратък значим signal.
5. Какво значи drop count? — Измерена загуба по конкретна overflow policy, която ограничава conclusions.
6. Как ограничавате false positives? — Обяснен feature, window/duration/hysteresis и tests, не само произволен threshold.

## 16. Връзка със следващото упражнение

Lab 5 използва същите принципи за noisy location stream, но добавя permissions, uncertainty и distance accumulation. Bounded ownership и time semantics остават същите.
