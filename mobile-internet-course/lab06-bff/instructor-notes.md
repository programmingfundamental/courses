# Упражнение 6 — Бележки за преподавателя
Само за преподавателя. Този файл не се публикува в студентските страници.

## 1. Концептуална цел
Клиентската network цена и semantics на частичен резултат определят BFF design.

## 2. Какво НЕ е основна цел
Не е още едно CRUD API и не преместваме цялата domain logic в BFF.

## 3. Предварителна подготовка
Осигурете готовите contracts от Lab 3–5, две delayed client stubs и skeleton HomeResponse. Network delay е предварително настроен инструмент или deterministic client fixture.

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
Ако намалим mobile calls от три на един, намаляват ли backend calls? Какво вижда user при optional timeout?

## 6. Основни концепции
Round trips, payload projection, critical path, partial state, deadlines, fan-out.

## 7. Чести грешки
- Gateway получава aggregation code.
- Връщат се цели downstream DTOs.
- Всеки error става empty section.
- Parallel branches са unbounded.
- Няма общ deadline.
- Before и after използват различен cache/workload.

## 8. Насочващи въпроси
Кои fields screen реално показва? Коя section може да липсва? Как user разбира, че данните са stale? Колко remote calls са започнати?

## 9. Очаквана архитектура
```text
Android → Gateway → [Mobile BFF /mobile/home]
                              +→ User Service
                              +→ Activity Service
                              +→ optional Notification Service
```
BFF projection/orchestration използва service APIs; services запазват ownership. Mobile получава един contract.

## 10. Ключови части от примерно решение
Home събира bounded profile и recent activities. Critical error прекратява; optional failure маркира section unavailable. HTTP and decode errors се класифицират преди mapping.

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА**

Activity Overview може да съдържа ownerDisplayName, activity counts и notificationEligibility с separate availability metadata. Изберете Activity като critical, eligibility като optional; не приемайте unavailable eligibility за allowed. Една валидна алтернатива е whole-screen error при по-строги бизнес изисквания.

## 11. Как да се предизвикат failure scenarios
Delay-нете optional branch над нейния timeout; спрете critical service; подайте 1000 activities и проверете max10 projection. Повторете при еднаква simulated RTT.

## 12. Очаквани наблюдения
Mobile round trips намаляват; backend fan-out остава. По-малък DTO може да намали parsing, но aggregation добавя server work.

## 13. Проверка на самостоятелната задача
- [ ] DTO е за друг screen.
- [ ] Изборът critical/optional е обяснен.
- [ ] Два sources и bounded payload.
- [ ] Partial test е изпълнен.
- [ ] Budget е съгласуван.

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
1. Защо BFF е client-specific? — Оптимизира конкретна interaction/projection.
2. Каква е цената на aggregation? — Coupling, fan-out load и latency от dependencies.
3. Защо не Empty при outage? — Липса на данни и липса на знание са различни.
4. Как измервате bytes? — За целия screen workload при еднакви условия.
5. Кой пази Activity invariants? — Activity Service, не BFF.

## 16. Връзка със следващото упражнение
Lab 7 прави partial failure и downstream time budgets устойчиви при повтарящи се откази.
