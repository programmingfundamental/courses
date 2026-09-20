# Упражнение 8 — Бележки за преподавателя
Само за преподавателя. Този файл не се публикува в студентските страници.

## 1. Концептуална цел
Durable intent и idempotent effect са различни части на end-to-end reliability; lost ACK е нормален distributed failure.

## 2. Какво НЕ е основна цел
Не реализираме общ sync engine, пълен conflict resolution UI или multi-region database.

## 3. Предварителна подготовка
Подгответе готовите client/BFF contracts, Room wiring и transaction helper. Проверете proxy с known successful POST. Scope на independent е една create операция и дадена SQL schema; това прави 20min реалистични.

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
Timeout означава ли, че server не е записал? Как mobile ще разбере след рестарт?

## 6. Основни концепции
Local source of truth, atomic outbox, stable identity, unique scheduling срещу effects, crash recovery, retention.

## 7. Чести грешки
- UI показва optimistic success без durable local insert.
- Key се генерира за всеки attempt.
- Ledger е in-memory map.
- Insert и ledger са отделни commits.
- Sending остава завинаги след crash.
- KEEP изпуска enqueue в края на Worker.
- Cancellation се catch-ва като retry.

## 8. Насочващи въпроси
Кой запис остава след process death? Може ли два callers да минат една проверка? Какъв payload е свързан с key? Кой събужда опашката след последния read?

## 9. Очаквана архитектура
```text
[Compose ← Room local source of truth]
             |
        PendingOp → WorkManager → Gateway → BFF → Activity DB + ledger
```
Room queue и server ledger са независими stores, свързани чрез stable operation identity. BFF не измисля нов key.

## 10. Ключови части от примерно решение
В една Room transaction local create+enqueue. Worker актуализира state след observable outcomes, а cancellation не унищожава intent. Constraints са hints; network error остава възможен.

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА**

В server transaction: claim unique principal/key, проверка на hash, create Activity и запис на serialized replay result. Conflict race се rollback-ва и чете commit-натият победител в нова transaction. Ако hash се различава→409; същият hash→същият ID/result. Не е достатъчна unique Activity title. Ledger retention трябва да покрива retry horizon; след изтичане политиката е explicit.

## 11. Как да се предизвикат failure scenarios
Proxy прекъсва само първия успешен POST response; изпратете повторно същия key. Два parallel requests с latch проверяват race. Kill/restart след Sending и след server commit. Добавете нова op точно преди worker exit.

## 12. Очаквани наблюдения
HTTP attempts могат да са повече от server effects. Recovery е eventual, не незабавна. Клиентът може временно да е Pending при вече създаден server record.

## 13. Проверка на самостоятелната задача
- [ ] Един effect при lost response.
- [ ] Един effect при concurrency.
- [ ] Different payload е 409.
- [ ] Restart пази key/result.
- [ ] Sending и wake-up policy са описани.

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
1. Какво е idempotency key? — Identity на logical command, запазена между attempts.
2. Защо server ledger? — Само server може да свърже atomically identity с durable effect.
3. Защо не check-then-insert? — Concurrent callers могат да минат check едновременно.
4. Какво е eventual consistency тук? — Local и remote state се сближават след sync/reconciliation.
5. Какво значи unknown outcome? — Липсва ACK, но effect може вече да съществува.

## 16. Връзка със следващото упражнение
Lab9 добавя asynchronous updates, които също могат да бъдат дублирани или пропуснати.
