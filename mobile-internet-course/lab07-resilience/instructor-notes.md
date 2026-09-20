# Упражнение 7 — Бележки за преподавателя
Само за преподавателя. Този файл не се публикува в студентските страници.

## 1. Концептуална цел
Resilience ограничава отказа и цената; не обещава, че всеки request непременно ще успее.

## 2. Какво НЕ е основна цел
Не настройваме production SLA от 10 calls и не добавяме всички annotations заради наличността им.

## 3. Предварителна подготовка
Проверете fixture pattern и injectable client counters. Осигурете harness за точно 10 callers; готовият Home checkpoint е prerequisite. За 422 case използвайте scripted client failure, защото основният mixed fixture връща 503.

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
Retry помага на една заявка. Какво прави на service, който вече е претоварен?

## 6. Основни концепции
Budget arithmetic, classified exceptions, breaker state, concurrency cap, partial semantics.

## 7. Чести грешки
- @Retry обхваща всички Exceptions.
- Retries са на няколко layers.
- Fallback връща празен live success.
- Timeout няма връзка с HTTP deadline.
- Методът се извиква извън CDI interception.
- Breaker статистика се приема за global cluster state.

## 8. Насочващи въпроси
Колко attempts са възможни общо? Кой error може да се промени след 200 ms? Кой section е optional? Има ли останала работа след timeout?

## 9. Очаквана архитектура
```text
Android → Gateway → [BFF policy]
                      +→ Fast Activity Service
                      +→ Notification /unstable
```
Отделен policy bean пази fault semantics; resource/BFF DTO mapping не извършва скрити retries.

## 10. Ключови части от примерно решение
500 ms branch attempt, maxRetries=1 и bounded delay оставят място в Home budget. Retry-on само за избран transient exception. Breaker failure ratio/window се измерват, не се копират без обяснение.

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА**

422 е terminal и не трябва да храни transient retry loop. За capacity timeout са допустими един retry с jitter или отказ без retry плюс bulkhead. Пример: maxConcurrent=4, queue=4, excess rejected към unavailable section; общ branch deadline<=1.5 s. Не изисквайте точно тези числа — изисквайте доказателство.

## 11. Как да се предизвикат failure scenarios
Fixture fail/slow; после ok. Scripted 422; паралелен harness10. Изключете mobile retry, за да измерите BFF policy самостоятелно, после обсъдете multiplication.

## 12. Очаквани наблюдения
Breaker може да намали calls и да увеличи частичните отговори. Ниска latency не доказва пълни данни; remote work може да надживее client timeout.

## 13. Проверка на самостоятелната задача
- [ ] Permanent/transient се различават.
- [ ] Budget е finite.
- [ ] Има измерени attempts.
- [ ] Concurrency policy е explicit.
- [ ] Trade-off е аргументиран.

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
1. Какво предпазва breaker? — Caller/dependency от повтарящи се вероятно безполезни calls.
2. Защо retry само GET тук? — Read няма mutation side effect; POST още няма idempotency.
3. Какво е amplification? — Downstream attempts / logical requests.
4. Какво означава unavailable fallback? — Системата признава липса на данни.
5. Защо HTTP timeout остава? — FT timeout не замества transport cancellation/deadline.

## 16. Връзка със следващото упражнение
Lab 8 добавя durable idempotency, за да може повторение на offline command да е безопасно.
