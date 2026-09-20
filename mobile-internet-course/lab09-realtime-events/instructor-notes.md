# Упражнение 9 — Бележки за преподавателя
Само за преподавателя. Този файл не се публикува в студентските страници.

## 1. Концептуална цел
Ниска latency не премахва duplicate, ordering и recovery проблемите; durable state е отделно от connection.

## 2. Какво НЕ е основна цел
Не реализираме production outbox processor, FCM интеграция или multi-instance WebSocket fanout.

## 3. Предварителна подготовка
Пуснете broker предварително и проверете topic. Подгответе envelope fixtures с versions 1/3/3/2 и transport fake. Дайте готов HTTP status contract scaffold, без version acceptance/reconnect reducer.

Изтеглете dependencies/images и проверете starter build преди часа. Подгответе работещ checkpoint от предходното занятие или преподавателско копие само на вече преподаваната функционалност. Самостоятелното решение за текущия час остава извън student starter.

## 4. Разпределение на 90-те минути
| Дейност | Минути |
|---|---:|
| Инженерен проблем и контекст | 10 |
| Теория и мини експеримент | 15 |
| Водена задача и checkpoint | 35 |
| Самостоятелна задача | 20 |
| Failure checks, измервания и анализ | 10 |
| **Общо** | **90** |

На 60-тата минута започва самостоятелната част. При изоставане използвайте подготвения guided checkpoint; не отнемайте нейното време за setup или UI оформление.

## 5. Начален въпрос / сценарий
Получихме DONE, после закъсняло ACTIVE. Кое е истината? Какво сме пропуснали докато socket липсва?

## 6. Основни концепции
Command/event, per-aggregate version, at-least-once effects, replay gap, foreground lifetime, heartbeat.

## 7. Чести грешки
- Timestamp се приема за глобален order.
- Всеки event увеличава count.
- Дедуп set расте безкрайно.
- Reconnect job продължава след STOP.
- Snapshot overwrite-ва по-нов event.
- DB commit и publish се наричат атомарни.
- WebSocket се държи безкрайно в Worker.

## 8. Насочващи въпроси
Кой version е durable? Кога snapshot е по-стар от event? Кой cancel-ва retry? Какво доказва broker ACK?

## 9. Очаквана архитектура
```text
[Activity status command → commit → EventPublisher]
 → Redpanda → Notification EventConsumer → BFF internal relay
 → Gateway WebSocket → Android RealtimeSource → Room → UI
```
Pipeline relay е инфраструктура; domain acceptance е отделен pure policy и Room transaction. Socket generation и lifecycle имат един owner.

## 10. Ключови части от примерно решение
След validated status commit се публикува envelope. Consumer failure се propagation-ва; mobile разделя control frames от business events и не прави DB I/O в callback.

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА**

За този state projection приемете само version>storedVersion в transaction; duplicate equal/stale lower не се прилагат. Snapshot се подчинява на същия version guard. При gap се заявява authoritative snapshot; generation token отхвърля old socket callbacks. Reconnect budget се reset-ва след стабилна session, не след всеки кратък Open.

## 11. Как да се предизвикат failure scenarios
Повторете един broker JSON два пъти; изпратете v2 след v3; спрете bff/socket, променете status и възстановете. Fake доставя late message от стара session и бавен snapshot след нов event. Прекъснете broker след DB commit и отчетете gap.

## 12. Очаквани наблюдения
Броят transport messages може да надвишава applied state changes. След gap reconciliation е нужна дори reconnect да е successful. Exact energy цена изисква physical measurement.

## 13. Проверка на самостоятелната задача
- [ ] 1/3/3/2 завършва 3.
- [ ] Snapshot race е защитена.
- [ ] Retries<=3 и STOP ги отменя.
- [ ] State/version преживява restart.
- [ ] Няма unbounded dedup memory.

## 14. Оценяване
| Област | Точки |
|---|---:|
| Водена практическа задача | 30 |
| Самостоятелна задача | 30 |
| Архитектура и code quality | 15 |
| Failure handling и tests | 10 |
| Анализ и измервания | 10 |
| Устна проверка | 5 |
| **Общо** | **100** |

Работещ happy path без failure semantics не получава пълните точки. Оценявайте аргументацията и доказателствата, а не конкретен хардуер или абсолютна latency.

## 15. Въпроси за устна защита
1. Какво е event fact? — Описание на commit-ната domain промяна.
2. Защо equal version се пази? — Duplicate delivery не трябва да променя projection втори път.
3. Защо snapshot? — Socket delivery gap няма implicit replay.
4. Какво решава outbox? — Атомарен запис на intent за publish заедно с business commit.
5. Защо push не е source of truth? — Тя е delivery/wake-up сигнал, който може да се повтори или липсва.

## 16. Връзка със следващото упражнение
Lab 10 добавя auth boundaries и проследява mobile request през цялата distributed система.
