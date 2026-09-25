# Преподавателски бележки — упражнение 2: Authentication със Spring Security

**Само за преподавателя. НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА.**
Всички демонстрации са само в предоставената локална учебна среда.

## 1. Концептуална цел

Login успехът не доказва безопасно съхранение. `{noop}` казва на DelegatingPasswordEncoder да сравни password без hash. Доверието е неправилно прехвърлено от login UX към защита на данните в DB. Ръчен controller, който само връща 200, също не създава автоматично persistent SecurityContext.

Студентът трябва да аргументира invariant и boundary, не просто да получи green status.

## 2. Какво НЕ е основната цел

Запаметяване на payload, механично копиране на configuration или annotation. За тази тема основният инженерeн въпрос е: как **SecurityFilterChain → AuthenticationProvider → Accounts → SecurityContext** създава и проверява доверие? Оценявайте причинното обяснение и evidence.

## 3. Подготовка преди часа

- От корена на курса изпълнете init-secrets и проверете Docker/Compose; ports 8080/8081 трябва да са свободни.
- Подгответе users alice/bob/admin от README; използвайте само synthetic данни.
- Задайте `LAB_MODE=lab02`; направете reset според README. При lab02 е нужен чист volume за {noop} seed.
- Стартирайте `docker compose up -d --build`; изчакайте /health=200.
- Изпълнете `mvn test '-Dlab.mode=lab02' '-Dtest=WebSecurityTest#lab02*'` от vulnerable-app; очаквайте security assertion failures преди fix.
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

В DB на портала са открити password стойности, които могат директно да се прочетат. Login работи, но изтичане на backup би разкрило паролите. Трябва да възстановите сигурното съхранение и да докажете реален session flow.

Първо поискайте прогноза за резултата, след това покажете наблюдението. Разделете факта от предположението.

## 6. Основни точки за обяснение

Преди часа проверете пълен reset, иначе {noop} demonstration може да липсва. Reference fix е return encoder.encode(password), но оценявайте и migration/verification. За account-summary очаквайте извличане на principal и parameterized count с owner filter; не показвайте този критерий преди самостоятелната работа.

Проследете allowed и denied flow през реалния code anchor, не само през диаграма.

## 7. Чести грешки

- Използване на SHA-256 за password storage.
- Base64 като „криптиране“.
- Сравняване на hash strings вместо matches.
- Само mocked authentication tests.
- Връщане на 403 при wrong password без договор.
- Пропускане на session persistence и logout.

## 8. Насочващи въпроси

- Коя стойност е под контрола на client?
- Къде точно тя преминава trust boundary в този flow?
- Кой allowed request трябва да остане работещ?
- Как ще различите липсващ control от test setup failure?
- Как ще докажете, че fix не е само промяна на видимия status?

Не давайте готовия independent code. При блокиране посочете boundary или test precondition.

## 9. Root cause

Login успехът не доказва безопасно съхранение. `{noop}` казва на DelegatingPasswordEncoder да сравни password без hash. Доверието е неправилно прехвърлено от login UX към защита на данните в DB. Ръчен controller, който само връща 200, също не създава автоматично persistent SecurityContext.

Техническият locus е **SecurityFilterChain → AuthenticationProvider → Accounts → SecurityContext**. Изисквайте студентът да посочи конкретния conditional/query/filter/sink и да обясни защо предишните слоеве не осигуряват тази гаранция. Risk scoring е условен; различна оценка се приема при верни assumptions и evidence.

## 10. Примерна защита

Използвайте server-side PasswordEncoder при registration/seed и DaoAuthenticationProvider при login. Guided baseline е `PasswordEncoderFactories.createDelegatingPasswordEncoder()`. Не сравнявайте нови random hashes със string equality. Запазете CSRF върху login/logout, generic failures и default session fixation protection. Отделете legacy password migration от новото записване.

Guided reference е secure code path в общия проект. След студентски fix лабораторният path трябва да има същия invariant; смяна на mode не се приема.

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА:**

Преди часа проверете пълен reset, иначе {noop} demonstration може да липсва. Reference fix е return encoder.encode(password), но оценявайте и migration/verification. За account-summary очаквайте извличане на principal и parameterized count с owner filter; не показвайте този критерий преди самостоятелната работа.

При review търсете server-side control, устойчивост на alternative entry points и минимум един положителен тест. Не изисквайте еднакъв source code, ако security contract е изпълнен.

## 11. Очаквани security tests

- valid credentials → 204 и /api/me със същата session → Alice
- wrong password и unknown user → 401 generic
- anonymous protected endpoint → 401
- logout → 204 и session invalidation
- stored password → BCrypt prefix и успешен matches

Основен selector: `WebSecurityTest#lab02*`. Изисквайте нов test по independent acceptance criteria. Проверете, че тестът се проваля при временно връщане на дефекта, а не защото липсват credentials/CSRF/Docker.

## 12. Как да се reset-не лабораторията

От корена на курса: `docker compose restart app` чисти limiter counters, sessions и временните JWT signing keys; browser cookies се изчистват и login се повтаря. AES secret file остава и persistent encrypted data трябва да се decrypt-ва със същия key.

За чисти DB fixtures: `docker compose down -v`, след проверка че project е само учебният `applied-web-security`, после `docker compose up -d --build`. Това изтрива учебните comments/profile changes и seed-ва трите accounts/documents. Не използвайте global prune. Source fixes не се отменят от DB reset; използвайте собствен version-control checkpoint, без да overwrite-вате студентска работа.

## 13. Проверка на самостоятелната задача

- [ ] Problem statement е реализиран, не само описан.
- [ ] Всички requirements и constraints от lab02.md са покрити.
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

1. **Защо salt не трябва да се пази тайно?**
   Очакван отговор: Тя прави hashes уникални; устойчивостта разчита на трудното guessing, не на secret salt.

2. **Защо два BCrypt hashes не се сравняват директно?**
   Очакван отговор: Random salt ги прави различни; използва се matches.

3. **Защо @WithMockUser не доказва login?**
   Очакван отговор: Той инжектира principal и заобикаля проверката на password.

4. **Какво означава 204 от login?**
   Очакван отговор: Authentication е приета; persistence трябва да се докаже с последваща session request.

5. **Защо logout е POST с CSRF?**
   Очакван отговор: Променя session state и не трябва да се предизвиква от чужда страница без намерение.

6. **Как се мигрира plaintext store?**
   Очакван отговор: Контролирано преизчисляване/reset без логване; не просто смяна на конфигурацията.

## 16. Връзка със следващото упражнение

След установяване на identity трябва да се провери до кои чужди ресурси тя достига.
