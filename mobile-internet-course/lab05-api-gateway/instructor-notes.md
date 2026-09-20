# Упражнение 5 — Бележки за преподавателя
Само за преподавателя. Този файл не се публикува в студентските страници.

## 1. Концептуална цел
Ingress topology independence и cross-cutting consistency без превръщане на Gateway в business monolith.

## 2. Какво НЕ е основна цел
Не пишем reverse proxy library или production distributed rate limiter.

## 3. Предварителна подготовка
Проверете Envoy image/config, готовите User/Activity checkpoints и logs. Дайте local_reply_config reference и празен response template, без попълнена policy.

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
Трябва ли нов backend deployment да изисква Android release? Къде ще открием failed request?

## 6. Основни концепции
Path routing, metadata trust boundary, timing, local replies, Gateway/BFF separation.

## 7. Чести грешки
- Prefix /api/users match-ва сходни чужди paths.
- Премахва се твърде голяма част от path.
- POST body/method се променя.
- Логва се Authorization.
- Internal /q route се публикува wildcard.
- Gateway събира User+Activity business данни.

## 8. Насочващи въпроси
Какво става с query string? Кой е избрал request ID? Error идва от upstream или от proxy? Има ли business knowledge в filter-а?

## 9. Очаквана архитектура
```text
Android → [Envoy Gateway]
              +→ User Service
              +→ Activity Service
              +→ BFF fixture
Internal service ports не са публикувани.
```
Envoy routes към services; backend contracts остават independent. Android няма списък internal URLs.

## 10. Ключови части от примерно решение
Route към cluster с finite timeout и prefix rewrite; exact/path-segment boundaries; generate request ID и structured access log от starter.

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА**

Local reply mappers могат да използват original status за разграничаване на 404/503/504 и bounded JSON error template с request ID. Direct response за непознат path е алтернатива, ако не прихваща business 404. Не давайте еднакъв 200 error wrapper.

## 11. Как да се предизвикат failure scenarios
curl /api/users-evil; спрете само activities; изпратете X-Request-ID с невалидни символи; върнете service и повторете. Envoy validate преди restart избягва загуба на часа по YAML typo.

## 12. Очаквани наблюдения
Gateway може да генерира грешка преди service span. Единният URL запазва mobile configuration, но Gateway става critical component.

## 13. Проверка на самостоятелната задача
- [ ] 404 и upstream outage са различими.
- [ ] JSON content type е правилен.
- [ ] Request ID е проследим.
- [ ] Success body не е променен.
- [ ] Business aggregation липсва.

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
1. Защо не aggregation в Gateway? — Тя е client/business-specific и усложнява общата ingress policy.
2. Какво е local reply? — Response, създаден от proxy без нормален upstream result.
3. Защо exact boundary? — За да не публикуваме непредвиден route.
4. Какво още трябва за security? — Authentication/authorization и TLS, не само routing.
5. Какво измерва duration? — Proxy request lifetime, не само business execution.

## 16. Връзка със следващото упражнение
Lab 6 добавя Mobile BFF зад същия ingress и оптимизира network round trips.
