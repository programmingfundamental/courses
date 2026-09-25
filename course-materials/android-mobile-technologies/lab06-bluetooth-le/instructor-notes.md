# Lab 6 — Бележки за преподавателя

Само за преподавателя; не се публикува в сайта.

## 1. Концептуална цел

BLE се управлява чрез asynchronous state machine с explicit ownership, operation ordering и failure recovery. Измерването е полезно само ако знаем от коя session идва и дали е свежо.

## 2. Какво НЕ е основна цел

Не пишете ESP32 firmware или пълен Android GATT server в часа. Не изследвайте pairing internals, MTU tuning или vendor-specific protocols. UI list е средство за selection, не основната задача.

## 3. Предварителна подготовка на преподавателя

Проверете API 37 central device и peripheral с UUID/format от lab-а. На втори Android телефон потвърдете advertising support и готов server; peripheral има собствени BLUETOOTH_ADVERTISE permissions. Ако липсва hardware, предоставете FakeBleTransport с queued callbacks и injectable clock. API 33–36 използват fake; deprecated connectGatt overloads не са част от решението. Подгответе permission launcher, scan filter и protocol test vectors, без student reconnect reducer.

За да се вмести задачата в 135 минути, осигурете предварително свързани Compose screens, Room migration/history scaffold и test harness, който подава callback/timer events. Студентите реализират GATT ordering във водената част, а transitions, timeout/retry policy и history integration — самостоятелно. Подготвените fixtures не съдържат готова recovery transition table.

## 4. Разпределение на времето

| Дейност | Минути |
|---|---:|
| Проблем | 8 |
| Теория | 12 |
| Async workflow demo | 10 |
| Воден BLE workflow | 35 |
| Checkpoint | 5 |
| Самостоятелна state machine/history | 35 |
| Edge cases/tests | 15 |
| Измерване | 10 |
| Анализ | 5 |
| **Общо** | **135** |

Radio проблем не трябва да изяде времето за state machine: след кратка диагностика преминете към fake и запишете причината. Hardware setup е предварителен.

## 5. Как да се въведе проблемът

Защо виждам един device пет пъти? Мога ли да чета characteristic веднага след connect call? Кой callback има право да промени текущия screen след reconnect?

## 6. Основни точки за обяснение

Advertising срещу connection; stages и callback completion; property validation; local notification flag + remote CCCD; current byte-value overloads; един GATT operation in flight; generation invalidation; scan/retry budgets; no automatic restoration на hardware session след process death.

## 7. Чести грешки

- Използва се deprecated BluetoothAdapter.getDefaultAdapter или стар write/value callback.
- Read се изпраща преди successful service discovery.
- CCCD не се записва или status се игнорира.
- Няколко requests са едновременно in flight.
- Scan остава активен след device selection.
- GATT object не се close-ва след disconnect/timeout.
- Late callback променя нова session.
- Reconnect се изпълнява и след manual Disconnect/Home.
- Адресът се приема за постоянна и удостоверена identity.

## 8. Насочващи въпроси

Кое събитие доказва, че subscription е готов? Кой timeout важи в това състояние? Как отменяте стар retry job? Какво става, ако UUID съществува, но property е различна? Как ще тествате липсващ callback без radio randomness?

## 9. Очаквана архитектура

```text
user/lifecycle/callback/timer events
                 |
          single state reducer -> UiState
                 |
       bounded operation queue -> real/fake transport
                 |
         validated readings -> Room history
```

Reducer и operation serialization могат да са actor-style coroutine с bounded channel. Overflow на control events не се игнорира: session се прекратява с explicit error. Notification data има отделен bounded path/drop counter. Един owner държи current GATT и generation.

## 10. Ключови части от примерно решение

Водената последователност е connect callback→discover callback→read callback→control write callback→local notify enable→CCCD write callback→Ready. Проверявайте immediate API acceptance и asynchronous status поотделно.

**НЕ показвай директно на студентите преди самостоятелната задача:** reducer приема callback само ако `(gatt === currentGatt && generation == currentGeneration)`. Timeout инвалидира generation, cancel-ва pending operations/timers и close-ва стария GATT. Retry е нов session attempt след bounded delay, само при active intent/visibility/grant. Permission denial/unsupported schema са terminal до user/config change; transient disconnect може да retry-не. Reset на retry budget е след успешен устойчив Ready, не след всяко кратко STATE_CONNECTED, иначе flapping дава безкрайни опити.

## 11. Как да се демонстрират проблемните сценарии

Fake изпраща duplicate scan events, забавя discovery и отказва read-before-ready. Не потвърждавайте CCCD write и проверете stage timeout. Доставете callback от old generation след нов connection. Изключете real peripheral или fake link; после натиснете Disconnect по време на scheduled retry. Изключете Bluetooth/revoke grant и проверете cleanup, включително SecurityException races. Изпратете 8 вместо 9 bytes и NaN float, за да проверите decoder guard.

## 12. Очаквани наблюдения

Discovery/connection timings варират; state order остава проверим. Notifications може да имат gaps, затова sequence counter е observation tool. Fake tests дават deterministic interleavings; реалният опит проверява interoperability и radio limitations. След stop всички active resource counts трябва да са 0.

## 13. Проверка на самостоятелната задача

- [ ] Има student-authored transition table.
- [ ] Deadlines покриват stuck connect/discover/read/write.
- [ ] Manual stop и lifecycle cancel предотвратяват retry.
- [ ] Old callbacks се игнорират.
- [ ] Missing characteristic/permission имат terminal policy.
- [ ] History е bounded и stable keys предотвратяват duplicates.

## 14. Оценяване

| Област | Точки |
|---|---:|
| Воден workflow | 25 |
| Самостоятелна state machine/history | 35 |
| Архитектура/code quality | 15 |
| Robustness/tests | 15 |
| Анализ/наблюдения | 5 |
| Устна защита | 5 |
| **Общо** | **100** |

Липса на физически peripheral не намалява оценката при пълен fake contract/test coverage. Radio throughput не е критерий за качество сам по себе си.

## 15. Въпроси за устна защита

1. Каква е разликата Connected/Ready? — Transport link срещу открити/валидирани services и активна subscription.
2. Защо CCCD? — Remote peripheral трябва да бъде конфигуриран да изпраща notifications; local flag не е достатъчен.
3. Защо един operation in flight? — Callback correlation и platform/peripheral sequencing трябва да са еднозначни.
4. Как generation помага? — Отхвърля late events от приключил owner/session.
5. Кога не retry-ваме? — Manual stop, hidden screen, denied/disabled/unsupported и изчерпан budget.
6. Защо read+notify може да дублират measurement? — Същата sequence value може да се достави по двата пътя; storage key го разграничава.

## 16. Връзка със следващото упражнение

Lab 7 измерва scan duty time, notification processing, DB writes и UI rate заедно със sensors/location. Fake transport позволява еднакъв workload преди и след optimization.
