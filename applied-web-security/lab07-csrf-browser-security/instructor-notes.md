# Преподавателски бележки — упражнение 7: CSRF, Cookies и Browser Security

**Само за преподавателя. НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА.**
Всички демонстрации са само в предоставената локална учебна среда.

## 1. Концептуална цел

Автоматично прикрепената session cookie доказва кой browser притежава session, но не доказва произход/намерение на state-changing action. CORS/SOP често спират четенето на резултата, след като нежеланата промяна вече е изпълнена.

Студентът трябва да аргументира invariant и boundary, не просто да получи green status.

## 2. Какво НЕ е основната цел

Запаметяване на payload, механично копиране на configuration или annotation. За тази тема основният инженерeн въпрос е: как **Browser cookie policy → CsrfFilter → state-changing Controller** създава и проверява доверие? Оценявайте причинното обяснение и evidence.

## 3. Подготовка преди часа

- От корена на курса изпълнете init-secrets и проверете Docker/Compose; ports 8080/8081 трябва да са свободни.
- Подгответе users alice/bob/admin от README; използвайте само synthetic данни.
- Задайте `LAB_MODE=lab07`; направете reset според README. При lab02 е нужен чист volume за {noop} seed.
- Стартирайте `docker compose up -d --build`; изчакайте /health=200.
- Изпълнете `mvn test '-Dlab.mode=lab07' '-Dtest=WebSecurityTest#lab07*'` от vulnerable-app; очаквайте security assertion failures преди fix.
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

Alice е влязла в портала. Друга локална страница съдържа форма, която променя display name през нейната session. Приложението проверява кой е user, но не проверява дали request идва от легитимния flow.

Първо поискайте прогноза за резултата, след това покажете наблюдението. Разделете факта от предположението.

## 6. Основни точки за обяснение

Използвайте точно localhost и двата ports; смесването с 127.0.0.1 сменя site/host и може да скрие CSRF. Ако validation ports са други, коригирайте action на локалния HTML fixture преди демонстрация. Самостоятелният test трябва да ползва две MockHttpSession instances и token от едната в другата, след което да провери DB count.

Проследете allowed и denied flow през реалния code anchor, не само през диаграма.

## 7. Чести грешки

- CORS като CSRF fix.
- Всички GET requests се смятат за safe въпреки mutation.
- CSRF disabled за целия app заради JWT.
- Смесване на localhost/127.0.0.1.
- Secure=true се обявява за работещ HTTP login.
- Status-only test без проверка на state.

## 8. Насочващи въпроси

- Коя стойност е под контрола на client?
- Къде точно тя преминава trust boundary в този flow?
- Кой allowed request трябва да остане работещ?
- Как ще различите липсващ control от test setup failure?
- Как ще докажете, че fix не е само промяна на видимия status?

Не давайте готовия independent code. При блокиране посочете boundary или test precondition.

## 9. Root cause

Автоматично прикрепената session cookie доказва кой browser притежава session, но не доказва произход/намерение на state-changing action. CORS/SOP често спират четенето на резултата, след като нежеланата промяна вече е изпълнена.

Техническият locus е **Browser cookie policy → CsrfFilter → state-changing Controller**. Изисквайте студентът да посочи конкретния conditional/query/filter/sink и да обясни защо предишните слоеве не осигуряват тази гаранция. Risk scoring е условен; различна оценка се приема при верни assumptions и evidence.

## 10. Примерна защита

В session SecurityFilterChain запазете default CSRF. Използвайте server-provided token в form parameter `_csrf` или върнатия headerName. GET не трябва да променя бизнес state. Session cookie: HttpOnly, SameSite=Lax; Secure=true в реален TLS profile, false само за описаната HTTP лаборатория. Не изключвайте CSRF глобално само защото приложението има и JWT endpoints.

Guided reference е secure code path в общия проект. След студентски fix лабораторният path трябва да има същия invariant; смяна на mode не се приема.

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА:**

Използвайте точно localhost и двата ports; смесването с 127.0.0.1 сменя site/host и може да скрие CSRF. Ако validation ports са други, коригирайте action на локалния HTML fixture преди демонстрация. Самостоятелният test трябва да ползва две MockHttpSession instances и token от едната в другата, след което да провери DB count.

При review търсете server-side control, устойчивост на alternative entry points и минимум един положителен тест. Не изисквайте еднакъв source code, ако security contract е изпълнен.

## 11. Очаквани security tests

- валиден token и authenticated session → 200 и update
- missing token → 403 и без update
- invalid token → 403 и без update
- token от друга session → 403
- real Set-Cookie → HttpOnly, SameSite=Lax; Secure=true в CookieIT

Основен selector: `WebSecurityTest#lab07*`. Изисквайте нов test по independent acceptance criteria. Проверете, че тестът се проваля при временно връщане на дефекта, а не защото липсват credentials/CSRF/Docker.

## 12. Как да се reset-не лабораторията

От корена на курса: `docker compose restart app` чисти limiter counters, sessions и временните JWT signing keys; browser cookies се изчистват и login се повтаря. AES secret file остава и persistent encrypted data трябва да се decrypt-ва със същия key.

За чисти DB fixtures: `docker compose down -v`, след проверка че project е само учебният `applied-web-security`, после `docker compose up -d --build`. Това изтрива учебните comments/profile changes и seed-ва трите accounts/documents. Не използвайте global prune. Source fixes не се отменят от DB reset; използвайте собствен version-control checkpoint, без да overwrite-вате студентска работа.

## 13. Проверка на самостоятелната задача

- [ ] Problem statement е реализиран, не само описан.
- [ ] Всички requirements и constraints от lab07.md са покрити.
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

1. **Защо SOP не спира формата?**
   Очакван отговор: Ограничава четене на cross-origin response, но form POST може да бъде изпратен.

2. **Защо SameSite=Lax не спря локалния пример?**
   Очакван отговор: Двата ports са различни origins, но same-site.

3. **Защо CORS не е CSRF control?**
   Очакван отговор: Не удостоверява намерението и не спира всички state-changing requests.

4. **Защо token се взема след login?**
   Очакван отговор: Authentication променя session/CSRF state; старият token може да е невалиден.

5. **Кога може да се изключи CSRF за API?**
   Очакван отговор: При изолиран stateless Bearer-only flow без автоматични browser credentials.

6. **Какво доказва CookieIT?**
   Очакван отговор: Emitted cookie flags от servlet container, не browser TLS/cookie enforcement.

## 16. Връзка със следващото упражнение

След защита на request flow защитаваме данните, които вече са попаднали в storage.
