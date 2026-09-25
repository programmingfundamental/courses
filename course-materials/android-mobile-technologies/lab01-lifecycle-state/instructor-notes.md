# Lab 1 — Бележки за преподавателя

Само за преподавателя; не се публикува в студентските страници.

## 1. Концептуална цел

State има owner и lifetime, които не съвпадат непременно с composable или Activity. Оценявайте обяснението на границите на restore и истинността на Running state.

## 2. Какво НЕ е основна цел

Не отделяйте време за декоративен UI, Kotlin syntax или сложна navigation схема. Два прости екрана са инструмент за изследване на ownership.

## 3. Предварителна подготовка на преподавателя

Осигурете Compose template с зависимости от README, emulator API 33/37, adb и празен fake clock. Няма нужда от физически device/permissions/peripheral. Проверете ActivityScenario и process-kill процедурата на лабораторните AVD. Предоставете route scaffold и празни action методи, без Plan решение.

## 4. Разпределение на времето

| Дейност | Минути |
|---|---:|
| Проблем | 8 |
| Теория | 12 |
| Мини експеримент | 10 |
| Водена задача | 35 |
| Checkpoint | 5 |
| Самостоятелен Plan | 35 |
| Edge cases и tests | 15 |
| Наблюдения | 10 |
| Анализ | 5 |
| **Общо** | **135** |

На 70-тата минута започва самостоятелната част; ако водената задача изостава, намалете visual polish, не отнемайте нейното време. Setup е предварителен.

## 5. Как да се въведе проблемът

Какво се случва с timer-а при rotation? Ако екранът е пресъздаден, означава ли това нов experiment? Може ли UI да показва Running след process death без собственик на job?

## 6. Основни точки за обяснение

Activity/Composition/VM lifetimes; state hoisting; един action owner; monotonic interval; lifecycle-aware consumer срещу producer; saved state е reconstruction информация, не durable history. Process restore policy е част от продукта, а не incidental API behavior.

## 7. Чести грешки

- Job се стартира директно в composable body.
- Name се пази и локално, и във VM без commit policy.
- ViewModel се създава отделно на всяка route вместо в общ scope.
- Timer брои callbacks, вместо elapsed time.
- Running Boolean се възстановява без реален job.
- Голям state object се поставя в Bundle.
- ActivityScenario.recreate се представя като process death test.
- Double Apply променя state два пъти.

## 8. Насочващи въпроси

Кой има право да промени status? Какво трябва да преживее back navigation? Кое доказва, че само един ticker работи? Кои три малки стойности стигат за възстановяване на draft? Как бихте тествали времето без Thread.sleep?

## 9. Очаквана архитектура

```text
Tracker route ----+
                  +--> graph ViewModel -> StateFlow -> stateless screens
Summary/Plan -----+          |
                       fake/real clock
Plan draft owner ---- saved state
```

Plan може да има собствен VM, ако комуникацията към shared experiment е explicit. Не налагайте единствено решение, когато lifetime и tests са аргументирани.

## 10. Ключови части от примерно решение

За водената част: `elapsed = accumulated + (clock.nowMs() - startedAt)` само при Running; transitions serially update state; duplicate Start е no-op; onCleared cancel-ва VM jobs чрез scope.

**НЕ показвай директно на студентите преди самостоятелната задача:** Plan държи отделни `draft` и `committed` стойности. Apply валидира draft и изпраща една action с revision/identity guard; Discard възстановява committed. SavedStateHandle пази primitive draft fields, а owner scope определя кога те се изчистват. При различен текущ experiment ID старият draft не се apply-ва към нов experiment.

## 11. Как да се демонстрират проблемните сценарии

Натиснете Start 20 пъти през test action, rotate и отворете Summary. Покажете Logcat instance IDs. За invalid state въведете target=0 и back. За process death background-нете приложението, изпълнете `adb shell am kill <package>` и върнете task от Recents; проверете сменен PID/VM ID. Не използвайте force-stop като равностойно доказателство. Fake clock прескача 10 s без реално чакане. Наблюдавайте jobs след Stop и след pop на VM scope.

## 12. Очаквани наблюдения

Recomposition не изисква нов VM. Rotation сменя Activity, но правилно scoped VM остава. Process recreation създава нов VM и възстановява само saved snapshot. Timer callbacks могат да се забавят, докато clock-derived duration остава смислен. Не изисквайте точен recomposition count между различни tooling версии.

## 13. Проверка на самостоятелната задача

- [ ] Plan е функционален, а не още един read-only summary.
- [ ] Draft и committed state имат разграничени owners.
- [ ] Target/note се валидират и се възстановяват при rotation.
- [ ] Apply/Discard/back имат проверими semantics.
- [ ] Double Apply и несъвместим restored draft са тествани.

## 14. Оценяване

| Област | Точки |
|---|---:|
| Водена реализация | 25 |
| Самостоятелна задача | 35 |
| Архитектура и code quality | 15 |
| Robustness/tests | 10 |
| Анализ и наблюдения | 10 |
| Устна защита | 5 |
| **Общо** | **100** |

## 15. Въпроси за устна защита

1. Какво преживява ViewModel? — Configuration change в същия scope, не process death.
2. Защо remember не пази history? — Lifetime е composition; липсва durable storage.
3. Защо два VM instance-а са проблем тук? — Разделят общия experiment state и могат да създадат два jobs.
4. Защо не elapsed++? — Scheduler/ticks не са надежден часовник.
5. Какво се възстановява след kill? — Малък saved snapshot при system restore, не старият Job или гарантирано последното изменение.
6. Как защитихте Plan Apply? — Validation, един owner и idempotent/identity-aware commit.

## 16. Връзка със следващото упражнение

Lab 2 премества experiment history в Room, но запазва UI actions и draft ownership. Студентите вече трябва да могат да разграничат persistent facts от UI reconstruction state.
