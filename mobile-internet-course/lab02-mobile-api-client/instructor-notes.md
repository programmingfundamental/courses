# Упражнение 2 — Бележки за преподавателя
Само за преподавателя. Този файл не се публикува в студентските страници.

## 1. Концептуална цел
Failure model и budget са част от public client API; retry е business решение, не универсален catch.

## 2. Какво НЕ е основна цел
Не пишете REST backend, TLS stack или general-purpose HTTP library.

## 3. Предварителна подготовка
Пуснете fixture и тествайте петте modes. Подгответе Call→suspend transport skeleton/MockWebServer equivalent без classification/retry policy, за да остане време за решенията.

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
Server е записал POST, но отговорът изчезва. Какво означава „Опитай отново“?

## 6. Основни концепции
Transport/HTTP/decode boundaries, cancellation propagation, timeout budget, idempotency и attempts.

## 7. Чести грешки
- Всеки Exception става Offline.
- Response body не се затваря.
- Call не се cancel-ва с coroutine.
- Всеки 4xx се retry-ва.
- Backoff е извън total budget.
- POST timeout автоматично повтаря mutation.

## 8. Насочващи въпроси
Получен ли е HTTP status? Може ли сървърът вече да е записал? Колко време остава? Кой държи Call след Back?

## 9. Очаквана архитектура
```text
[Android ApiClient → Repository → ViewModel]
       |
Gateway /api/fixture/activities → готов fixture BFF
```
Repository не връща raw OkHttp Response в UI; VM притежава request job; decoder и retry policy се тестват отделно.

## 10. Ключови части от примерно решение
Response status се проверява преди DTO decoding. Body use/finally, cancellation hook и snapshot на request generation предотвратяват resource/stale-state errors.

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА**

Възможна policy: само GET, transient connect/read timeout или 502/503/504, максимум 3 attempts с 200/400 ms jittered backoff при достатъчен deadline. 429 може да бъде отложен според Retry-After. 401/403/422, decode и TLS validation са terminal. POST остава UnknownOutcome.

## 11. Как да се предизвикат failure scenarios
mode=slow задържа 3 s; намалете test read timeout до 500 ms. mode=malformed връща счупен JSON с 200. Изключете мрежата, натиснете Cancel в backoff и пребройте реалните calls.

## 12. Очаквани наблюдения
Validated network може да има failed call. Timeout не казва дали mutation е commit-ната. Retry увеличава bytes/latency дори да повиши success rate.

## 13. Проверка на самостоятелната задача
- [ ] Policy table е аргументирана.
- [ ] GET attempts <=3 и deadline<=5 s.
- [ ] Cancel спира backoff.
- [ ] Permanent errors не се повтарят.
- [ ] POST uncertain outcome е distinct.

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
1. Защо malformed 200 не е success? — Transport/status success не валидира payload contract.
2. Какво отменя coroutine? — Call.cancel трябва да е свързан изрично.
3. Какво е total budget? — Calls, waiting и backoff в един deadline.
4. Кога POST може да се retry-ва? — При server-supported idempotency contract.
5. Как тествате jitter? — Injected deterministic random/clock.

## 16. Връзка със следващото упражнение
Lab 3 проектира server contract, който mobile failure model може да интерпретира предвидимо.
