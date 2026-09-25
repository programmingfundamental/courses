# Упражнение 10 — Бележки за преподавателя
Само за преподавателя. Този файл не се публикува в студентските страници.

## 1. Концептуална цел
Security и observability са end-to-end свойства; правилният отказ и обоснованата диагноза са част от correctness.

## 2. Какво НЕ е основна цел
Не инсталираме enterprise IdP и не реализираме пълен mobile login или production SIEM за 90 минути.

## 3. Предварителна подготовка
Подгответе предходните checkpoints и security policy skeletons. Генерирайте keys преди Docker up; member/viewer tokens са краткотрайни. Стартирайте Collector/Jaeger и подгответе slow-user overlay. За основния scope изключете WS route; authenticated per-user stream е следваща проектна стъпка.

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
Кой може да създаде Activity от името на u1? Кой span обяснява бавния Home и как го доказваме?

## 6. Основни концепции
Identity/authorization/ownership, token propagation към trusted services, critical path на trace, sanitized logs и metrics с ограничена cardinality.

## 7. Чести грешки
- Base64 decoding се приема за JWT validation.
- Role заменя ownership check.
- Token се слага в URL/query/logs.
- Internal service няма проверки, защото има Gateway.
- Сумират се паралелни spans като wall-clock duration.
- Променят се timeouts без доказателства.
- Липсващ trace се описва като нулева latency.

## 8. Насочващи въпроси
Кой е verified subject? За коя audience е token? Кой child span е по critical path? Колко attempts показват logs? Какво не знаем от тези 20 samples?

## 9. Очаквана архитектура
```text
Android + access token → [Gateway boundary]
 → [BFF auth + span] → [Activity auth + span] → [User auth + span]
                     OTel Collector → Jaeger
Structured logs + requestId/traceId; aggregate metrics
```
Gateway routing/ingress span, BFF/service authorization и REST Client context propagation. Management telemetry е internal; demo issuer е отделен тестов инструмент.

## 10. Ключови части от примерно решение
JWT extension проверява подписа и claims; policy/roles и ownership са отделни проверки. Trace context преминава през instrumented REST Clients. Tokens не влизат в MDC. Security tests проверяват effects, не само HTTP status.

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА**

При подготвения slow-user scenario всеки трети User lookup получава 900 ms delay. Диагнозата трябва да посочи User span и корелация с общата latency/timeout. Ако BFF retry умножава calls, това е вторична amplification. При правилен budget може да има бърз 503 вместо бавен 200; студентът трябва да обясни разликата. Предложение за bounded cache или промяна на dependency budget трябва да включва consistency trade-off.

## 11. Как да се предизвикат failure scenarios
Стартирайте slow-user overlay и сравнете baseline. Използвайте expired/viewer и token с wrong audience; променете първия знак на signature за invalid-signature test. Спрете Collector за една проба и го възстановете. Не извеждайте tokens в общите logs.

## 12. Очаквани наблюдения
Unauthorized requests спират преди business write. Slow span може да доминира общото време дори BFF CPU да е нисък. Telemetry outage трябва да се различава от API outage.

## 13. Проверка на самостоятелната задача
- [ ] Има trace, log и metric evidence.
- [ ] Компонентът и call са назовани.
- [ ] Baseline и failure runs са съпоставими.
- [ ] Причината и симптомът са разграничени.
- [ ] Предложената промяна има описан trade-off.
- [ ] Report не съдържа secrets.

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
1. Какво валидира JWT? — Signature, issuer, audience и expiry; после се проверява authorization.
2. Защо ownership е отделно? — Role не дава право върху всеки resource.
3. Какво е traceparent? — Propagation context, а не идентичност или permission.
4. Как се открива retry amplification? — Повече child calls/attempts спрямо logical requests.
5. Защо няма OIDC setup в часа? — Използваме готови тестови credentials; учебната цел са API boundaries и diagnosis.

## 16. Връзка със следващото упражнение
Курсът завършва с един проект. Следващите изменения се избират според измерванията: outbox, production OIDC/TLS, multi-instance fanout или по-добра offline conflict policy.
