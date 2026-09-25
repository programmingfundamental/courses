# Преподавателски бележки — упражнение 6: Cross-Site Scripting (XSS)

**Само за преподавателя. НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА.**
Всички демонстрации са само в предоставената локална учебна среда.

## 1. Концептуална цел

Сървърът поставя untrusted string в HTML response без context-aware encoding. Browser parser не знае, че string-ът е „само comment“. Storage, authentication и input length checks не променят parsing context.

Студентът трябва да аргументира invariant и boundary, не просто да получи green status.

## 2. Какво НЕ е основната цел

Запаметяване на payload, механично копиране на configuration или annotation. За тази тема основният инженерeн въпрос е: как **Stored comment / reflected query → HTML rendering → browser** създава и проверява доверие? Оценявайте причинното обяснение и evidence.

## 3. Подготовка преди часа

- От корена на курса изпълнете init-secrets и проверете Docker/Compose; ports 8080/8081 трябва да са свободни.
- Подгответе users alice/bob/admin от README; използвайте само synthetic данни.
- Задайте `LAB_MODE=lab06`; направете reset според README. При lab02 е нужен чист volume за {noop} seed.
- Стартирайте `docker compose up -d --build`; изчакайте /health=200.
- Изпълнете `mvn test '-Dlab.mode=lab06' '-Dtest=WebSecurityTest#lab06*'` от vulnerable-app; очаквайте security assertion failures преди fix.
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

Alice записва comment, който при преглед от Bob променя заглавието на browser tab. Сървърът третира user content като HTML и го изпълнява в origin на портала.

Първо поискайте прогноза за резултата, след това покажете наблюдението. Разделете факта от предположението.

## 6. Основни точки за обяснение

Сменянето на document.title е достатъчно evidence. Проверете raw response независимо от CSP и не приемайте само screenshot. За homepage очаквайте URI parsing, scheme allowlist и отделно quoted-attribute encoding; case/whitespace normalization и invalid URI са част от анализа. Не показвайте готовия independent fix.

Проследете allowed и denied flow през реалния code anchor, не само през диаграма.

## 7. Чести грешки

- Encoding само на input.
- CSP като единствен fix.
- HtmlUtils за JavaScript string.
- innerHTML за plain text.
- HttpOnly е обявено за пълна XSS защита.
- Test само за exact <script>, без контекст.

## 8. Насочващи въпроси

- Коя стойност е под контрола на client?
- Къде точно тя преминава trust boundary в този flow?
- Кой allowed request трябва да остане работещ?
- Как ще различите липсващ control от test setup failure?
- Как ще докажете, че fix не е само промяна на видимия status?

Не давайте готовия independent code. При блокиране посочете boundary или test precondition.

## 9. Root cause

Сървърът поставя untrusted string в HTML response без context-aware encoding. Browser parser не знае, че string-ът е „само comment“. Storage, authentication и input length checks не променят parsing context.

Техническият locus е **Stored comment / reflected query → HTML rendering → browser**. Изисквайте студентът да посочи конкретния conditional/query/filter/sink и да обясни защо предишните слоеве не осигуряват тази гаранция. Risk scoring е условен; различна оценка се приема при верни assumptions и evidence.

## 10. Примерна защита

За зададения plain-text HTML sink използвайте HtmlUtils.htmlEscape. За DOM text използвайте textContent, а не innerHTML. Не интерполирайте user data в JavaScript source. Guided CSP: `default-src 'none'; form-action 'self'; frame-ancestors 'none'; base-uri 'none'`. При нови UI assets разрешавайте само необходимите sources; не добавяйте unsafe-inline за удобство.

Guided reference е secure code path в общия проект. След студентски fix лабораторният path трябва да има същия invariant; смяна на mode не се приема.

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА:**

Сменянето на document.title е достатъчно evidence. Проверете raw response независимо от CSP и не приемайте само screenshot. За homepage очаквайте URI parsing, scheme allowlist и отделно quoted-attribute encoding; case/whitespace normalization и invalid URI са част от анализа. Не показвайте готовия independent fix.

При review търсете server-side control, устойчивост на alternative entry points и минимум един положителен тест. Не изисквайте еднакъв source code, ако security contract е изпълнен.

## 11. Очаквани security tests

- ordinary HTML characters → encoded response
- script-like stored input → literal text, без raw script tag
- normal Bulgarian text → правилно показан
- CSP присъства след fix
- browser marker не се изпълнява; отделно от MockMvc assertions

Основен selector: `WebSecurityTest#lab06*`. Изисквайте нов test по independent acceptance criteria. Проверете, че тестът се проваля при временно връщане на дефекта, а не защото липсват credentials/CSRF/Docker.

## 12. Как да се reset-не лабораторията

От корена на курса: `docker compose restart app` чисти limiter counters, sessions и временните JWT signing keys; browser cookies се изчистват и login се повтаря. AES secret file остава и persistent encrypted data трябва да се decrypt-ва със същия key.

За чисти DB fixtures: `docker compose down -v`, след проверка че project е само учебният `applied-web-security`, после `docker compose up -d --build`. Това изтрива учебните comments/profile changes и seed-ва трите accounts/documents. Не използвайте global prune. Source fixes не се отменят от DB reset; използвайте собствен version-control checkpoint, без да overwrite-вате студентска работа.

## 13. Проверка на самостоятелната задача

- [ ] Problem statement е реализиран, не само описан.
- [ ] Всички requirements и constraints от lab06.md са покрити.
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

1. **Защо HTML encoding не е универсално?**
   Очакван отговор: Parser contexts имат различен синтаксис и разрешени стойности.

2. **Какво не доказва MockMvc?**
   Очакван отговор: Не изпълнява JavaScript и не прилага реален browser CSP.

3. **Защо пазим original comment в DB?**
   Очакван отговор: Encoding е output-context операция; storage encoding води до coupling/double encoding.

4. **Кога е нужен sanitizer?**
   Очакван отговор: При позволен rich HTML с explicit safe subset.

5. **Достатъчно ли е HttpOnly?**
   Очакван отговор: Не, XSS може да прави authenticated same-origin actions.

6. **Може ли CSP да скрие bug?**
   Очакван отговор: Да; missing execution не означава, че sink е правилно encoded.

## 16. Връзка със следващото упражнение

Browser може да изпрати authenticated request и без XSS; CSRF използва автоматично прикрепените cookies.
