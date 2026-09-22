# Преподавателски бележки — упражнение 1: Лабораторна среда и Threat Modeling

**Само за преподавателя. НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА.**
Всички демонстрации са само в предоставената локална учебна среда.

## 1. Концептуална цел

Нарушеното assumption е „вътрешният компонент е недостъпен по дефиниция“. Публикуван host port създава отделен entry point извън приложната authentication/authorization. Наличието на Nginx не променя правата върху директна DB връзка.

Студентът трябва да аргументира invariant и boundary, не просто да получи green status.

## 2. Какво НЕ е основната цел

Запаметяване на payload, механично копиране на configuration или annotation. За тази тема основният инженерeн въпрос е: как **Compose, Nginx, endpoint inventory и trust boundaries** създава и проверява доверие? Оценявайте причинното обяснение и evidence.

## 3. Подготовка преди часа

- От корена на курса изпълнете init-secrets и проверете Docker/Compose; ports 8080/8081 трябва да са свободни.
- Подгответе users alice/bob/admin от README; използвайте само synthetic данни.
- Задайте `LAB_MODE=lab01`; направете reset според README. При lab02 е нужен чист volume за {noop} seed.
- Стартирайте `docker compose up -d --build`; изчакайте /health=200.
- Изпълнете `mvn test '-Dtest=WebSecurityTest#lab01*'` от vulnerable-app; очаквайте green baseline.
- Прочетете resources и пригответе separate browser sessions; не раздавайте instructor-notes.
- За SQL/crypto вижте persistence; за browser topics проверете origin и cookies; за JWT tokens се издават наново след restart.

## 4. Препоръчително разпределение на времето

| Дейност | Минути |
|---|---:|
| Scenario и теория | 15 |
| Наблюдение/reproduction | 15 |
| Root cause | 10 |
| Guided mitigation | 25 |
| Regression tests | 15 |
| Самостоятелна задача | 25 |
| Устна защита | 5 |
| **Общо** | **110** |

При 90 минути подгответе предварително startup и evidence harness; при 120 използвайте допълнителните 10 за test mutation и обсъждане на ограничения.

## 5. Как да се въведе проблемът

Екипът добавя учебен портал и твърди, че „щом е зад Nginx, е защитен“. Преди да оцените конкретен exploit, трябва да установите кой може да достигне всеки компонент и какви данни преминават през него.

Първо поискайте прогноза за резултата, след това покажете наблюдението. Разделете факта от предположението.

## 6. Основни точки за обяснение

Започнете с въпроса „кой socket приема заявката?“ и поискайте учениците да проследят реалните services. Не приемайте рисунка без конкретни protocols и assets. В самостоятелния registration модул очаквайте password storage, role injection, enumeration, uncontrolled registration и logging/PII risks; точният списък може да е различен, ако е аргументиран.

Проследете allowed и denied flow през реалния code anchor, не само през диаграма.

## 7. Чести грешки

- Приравняване на threat с payload.
- Всички рискове са high без обосновка.
- Игнориране на authenticated attacker.
- Обявяване на Nginx за authentication boundary.
- Списък с ports без host binding.
- DFD без direction и sensitive data.

## 8. Насочващи въпроси

- Коя стойност е под контрола на client?
- Къде точно тя преминава trust boundary в този flow?
- Кой allowed request трябва да остане работещ?
- Как ще различите липсващ control от test setup failure?
- Как ще докажете, че fix не е само промяна на видимия status?

Не давайте готовия independent code. При блокиране посочете boundary или test precondition.

## 9. Root cause

Нарушеното assumption е „вътрешният компонент е недостъпен по дефиниция“. Публикуван host port създава отделен entry point извън приложната authentication/authorization. Наличието на Nginx не променя правата върху директна DB връзка.

Техническият locus е **Compose, Nginx, endpoint inventory и trust boundaries**. Изисквайте студентът да посочи конкретния conditional/query/filter/sink и да обясни защо предишните слоеве не осигуряват тази гаранция. Risk scoring е условен; различна оценка се приема при верни assumptions и evidence.

## 10. Примерна защита

Публикувайте само Nginx на loopback, премахнете host ports на DB/app, използвайте internal network и отделен runtime DB role. За HTTP routes приложете explicit allowlist и deny-by-default. Докажете controls поотделно: network isolation не замества identity или object policy.

Guided reference е secure code path в общия проект. След студентски fix лабораторният path трябва да има същия invariant; смяна на mode не се приема.

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА:**

Започнете с въпроса „кой socket приема заявката?“ и поискайте учениците да проследят реалните services. Не приемайте рисунка без конкретни protocols и assets. В самостоятелния registration модул очаквайте password storage, role injection, enumeration, uncontrolled registration и logging/PII risks; точният списък може да е различен, ако е аргументиран.

При review търсете server-side control, устойчивост на alternative entry points и минимум един положителен тест. Не изисквайте еднакъв source code, ако security contract е изпълнен.

## 11. Очаквани security tests

- public health → 200
- anonymous /api/me → 401
- authenticated unknown route → 403
- Compose DB/app ports → няма
- unsafe config fixture → проверката трябва да го отхвърли

Основен selector: `WebSecurityTest#lab01*`. Изисквайте нов test по independent acceptance criteria. Проверете, че тестът се проваля при временно връщане на дефекта, а не защото липсват credentials/CSRF/Docker.

## 12. Как да се reset-не лабораторията

От корена на курса: `docker compose restart app` чисти limiter counters, sessions и временните JWT signing keys; browser cookies се изчистват и login се повтаря. AES secret file остава и persistent encrypted data трябва да се decrypt-ва със същия key.

За чисти DB fixtures: `docker compose down -v`, след проверка че project е само учебният `applied-web-security`, после `docker compose up -d --build`. Това изтрива учебните comments/profile changes и seed-ва трите accounts/documents. Не използвайте global prune. Source fixes не се отменят от DB reset; използвайте собствен version-control checkpoint, без да overwrite-вате студентска работа.

## 13. Проверка на самостоятелната задача

- [ ] Problem statement е реализиран, не само описан.
- [ ] Всички requirements и constraints от lab01.md са покрити.
- [ ] Acceptance cases включват denied и allowed outcomes.
- [ ] Има поне един нов автоматизиран regression test.
- [ ] Evidence сочи точния code path и лабораторен mode.
- [ ] Няма secrets в отчета и residual risk е аргументиран.

## 14. Критерии за оценяване

| Критерий | Точки |
|---|---:|
| Root cause, boundary и risk reasoning | 20 |
| Контролирано reproduction и evidence | 20 |
| Коректна mitigation и independent реализация | 25 |
| Автоматизирани regression/positive tests | 20 |
| Code quality, reproducibility и безопасно evidence | 10 |
| Устна защита | 5 |
| **Общо** | **100** |

При неработещ control не присъждайте точките за mitigation само за green тест с грешни preconditions. При lab01 reproduction означава доказано несъответствие на unsafe config fixture, без реално отваряне на DB.

## 15. Въпроси за устна защита

1. **Може ли internal network да замени authorization?**
   Очакван отговор: Не; authenticated клиентът достига приложението и може да злоупотреби с неговите операции.

2. **Каква е разликата между asset и threat?**
   Очакван отговор: Документът е asset; неразрешеният му прочит е threat.

3. **Къде е boundary при SQL?**
   Очакван отговор: Между приложния input/query construction и DB интерпретацията и правата.

4. **Защо vulnerability без Internet exposure остава vulnerability?**
   Очакван отговор: Reachability променя likelihood, но слабостта и potential impact остават.

5. **Как доказвате mitigation?**
   Очакван отговор: С конфигурационен/HTTP отрицателен тест и положителен разрешен flow.

6. **Какво пропуска Top 10?**
   Очакван отговор: Специфични business abuse flows и assumptions; нужен е собствен модел.

## 16. Връзка със следващото упражнение

Threat model определя identity assets; следващият lab проверява как се създава доверена identity.
