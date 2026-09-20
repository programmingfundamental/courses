# Упражнение 3 — Бележки за преподавателя
Само за преподавателя. Този файл не се публикува в студентските страници.

## 1. Концептуална цел
Мобилният API е versioned договор с bounded resource use, не сериализация на database state.

## 2. Какво НЕ е основна цел
Не преподаваме ORM, SQL schema design или CRUD form UI.

## 3. Предварителна подготовка
Seed fixture 53 matching и 147 nonmatching records, valid/invalid JSON, DTO test skeleton. Стартирайте Activity Service в IDE или container; bootstrap schema е готов.

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
Как client отличава собствена грешка от transient outage? Какво струва изпращането на 10 000 entities през cellular?

## 6. Основни концепции
Boundary validation, error contract, identity, stable pagination, wire evolution и parsing cost.

## 7. Чести грешки
- Връща се ActivityStore record без DTO mapping.
- Приема се server status от request.
- Всички errors са 500.
- SQL се сглобява от sort/name text.
- size няма горна граница.
- Равни timestamps имат произволен ред.

## 8. Насочващи въпроси
Кой може да зададе version? Колко rows реално се четат? Какво получава стар client? Защо last page има по-малко items?

## 9. Очаквана архитектура
```text
Android → Gateway fixture (Lab 2)
Development curl → [Activity Service REST → ActivityStore → H2]
След Lab 5 mobile ще използва само Gateway.
```
Resource извършва DTO validation/mapping, store държи persistence. Независимите fields не изтичат към wire.

## 10. Ключови части от примерно решение
@Valid CreateActivity→validated domain input→store.insert→Response.created(...).entity(dto). Unknown ID се map-ва към 404; не връщайте null с 200.

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА**

Един допустим search ползва page*size със overflow guard, query limit=size+1, parameter binding и stable created_at/id order; hasNext идва от допълнителния row. status enum избира allowed branch. Приемайте cursor вариант, ако остава в 20 min и е tested.

## 11. Как да се предизвикат failure scenarios
POST blank title и malformed JSON; GET unknown ID; search size=10000. Между две offset pages добавете record с newest timestamp и покажете drift. Не превръщайте това в задача за snapshot database.

## 12. Очаквани наблюдения
По-малки pages ограничават bytes и parsing, но увеличават броя round trips. 404 и validation не са retryable 503.

## 13. Проверка на самостоятелната задача
- [ ] 53 matches дават 20/20/13.
- [ ] size/filter се валидират.
- [ ] Stable order е explicit.
- [ ] DTO не е entity.
- [ ] Concurrent-write limitation е описана.

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
1. Защо 201 и Location? — Клиентът получава identity/address на създадения resource.
2. Защо hasNext без total? — Може да избегне скъп count; trade-off се описва.
3. Какво е offset drift? — Dataset промяна измества page границите.
4. Къде се bind-ва input? — В parameterized query, със sort allowlist.
5. Какво е additive evolution? — Optional fields при tolerant client, без semantic break.

## 16. Връзка със следващото упражнение
Lab 4 отделя User ownership в remote service и прави failure propagation видима.
