# Упражнение 4 — Бележки за преподавателя
Само за преподавателя. Този файл не се публикува в студентските страници.

## 1. Концептуална цел
Service boundary добавя network failure domain; добрата decomposition запазва ownership и прави отказите explicit.

## 2. Какво НЕ е основна цел
Не преследвайте максимален брой services и не въвеждайте Kubernetes/service mesh.

## 3. Предварителна подготовка
Пуснете User/Activity и празен Notification module. Подгответе 404/503/slow User stub и count query. Mobile използва checkpoint от Lab 2–3.

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
Как се променя create, когато User lookup вече е remote? Кой притежава данните след разделянето?

## 6. Основни концепции
Cohesion/coupling, independent availability, timeout propagation, contracts, ownership.

## 7. Чести грешки
- Двете услуги четат една таблица.
- 404 и timeout стават false.
- Няма client deadline.
- Activity се записва преди задължителната проверка.
- Internal IP попада в Android.
- Третата услуга е само proxy без обяснена отговорност.

## 8. Насочващи въпроси
Кой може да промени тези данни? Нужно ли е синхронно чакане? Какво казвате на mobile при outage? Има ли нежелан record?

## 9. Очаквана архитектура
```text
Mobile request → [Activity Service → REST Client → User Service]
                      |
                     H2
Самостоятелно: Notification Service → избран owner API
```
CDI service orchestration стои между resource и store. Notification упражнението е малко, със собствен DTO и един dependency.

## 10. Ключови части от примерно решение
Validate request→remote user lookup→classify result→local insert. Exception mapping пази missing и unavailable различни; shared ID свързва logs.

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА**

Приемлива трета capability: GET notification eligibility чете User Service, комбинира го със собствена локална preference и връща allowed/reason. При outage връща unavailable, без да измисля consent. Preference ownership е Notification; User identity остава User. Други boundaries са допустими при същата яснота.

## 11. Как да се предизвикат failure scenarios
docker compose stop users; повторете POST и проверете count; start users. Stub delay над read timeout. Върнете 404 само за u-missing. Не спирайте Docker daemon или чужди projects.

## 12. Очаквани наблюдения
Разделеният call може да се провали независимо от Activity process. Correlation ID позволява проследяване; не е transaction ID.

## 13. Проверка на самостоятелната задача
- [ ] Третата boundary има собствена отговорност.
- [ ] Има един реален remote call.
- [ ] Timeout/error policy е видима.
- [ ] Няма shared DB/mobile topology leak.
- [ ] Failure test е изпълнен.

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
1. Защо cohesion е важна? — Свързаните business invariants остават при един owner.
2. Какво е temporal coupling? — Caller зависи downstream да е наличен сега.
3. Защо не обща DB? — Заобикаля contract и независим ownership.
4. Какво прави correlation ID? — Свързва observations, не гарантира atomicity.
5. Кога да не разделяме? — Когато ползата не оправдава network/operational complexity.

## 16. Връзка със следващото упражнение
Lab 5 скрива backend topology зад един Gateway address.
