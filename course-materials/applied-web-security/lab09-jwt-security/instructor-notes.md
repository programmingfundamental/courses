# Преподавателски бележки — упражнение 9: JWT Security и Token Manipulation

**Само за преподавателя. НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА.**
Всички демонстрации са само в предоставената локална учебна среда.

## 1. Концептуална цел

Приложението третира decoding като доказателство за authenticity. Base64url е обратимо без secret; attacker контролира payload bytes. Дори валидният подпис не означава, че token е предназначен за този API, не е изтекъл или има необходимите permissions.

Студентът трябва да аргументира invariant и boundary, не просто да получи green status.

## 2. Какво НЕ е основната цел

Запаметяване на payload, механично копиране на configuration или annotation. За тази тема основният инженерeн въпрос е: как **Bearer token → JwtDecoder → claim validators → authorities** създава и проверява доверие? Оценявайте причинното обяснение и evidence.

## 3. Подготовка преди часа

- От корена на курса изпълнете init-secrets и проверете Docker/Compose; ports 8080/8081 трябва да са свободни.
- Подгответе users alice/bob/admin от README; използвайте само synthetic данни.
- Задайте `LAB_MODE=lab09`; направете reset според README. При lab02 е нужен чист volume за {noop} seed.
- Стартирайте `docker compose up -d --build`; изчакайте /health=200.
- Изпълнете `mvn test '-Dlab.mode=lab09' '-Dtest=JwtSecurityTest'` от vulnerable-app; очаквайте security assertion failures преди fix.
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

API endpoint приема identity от декодиран JWT payload, без да проверява подписа. Локална промяна на sub от alice към admin се приема като нова identity, въпреки че signature вече не съответства.

Първо поискайте прогноза за резултата, след това покажете наблюдението. Разделете факта от предположението.

## 6. Основни точки за обяснение

Демонстрацията сменя sub, без да издава реални admin permissions извън лабораторния marker endpoint. Не показвайте independent answers преди работа. Очаквайте explicit presence check за exp/sub и audience membership, timestamp validator с Clock, authority mapping и реални signed token HTTP tests. За липсващ exp test разширете test signing helper, не production decoder.

Проследете allowed и denied flow през реалния code anchor, не само през диаграма.

## 7. Чести грешки

- Base64 decode се нарича validation.
- Trust към token alg/key URL.
- Само expiry check без signature.
- JWT payload се смята за encrypted.
- 401 за валиден token без permissions без обяснен договор.
- Само mocked jwt() tests.

## 8. Насочващи въпроси

- Коя стойност е под контрола на client?
- Къде точно тя преминава trust boundary в този flow?
- Кой allowed request трябва да остане работещ?
- Как ще различите липсващ control от test setup failure?
- Как ще докажете, че fix не е само промяна на видимия status?

Не давайте готовия independent code. При блокиране посочете boundary или test precondition.

## 9. Root cause

Приложението третира decoding като доказателство за authenticity. Base64url е обратимо без secret; attacker контролира payload bytes. Дори валидният подпис не означава, че token е предназначен за този API, не е изтекъл или има необходимите permissions.

Техническият locus е **Bearer token → JwtDecoder → claim validators → authorities**. Изисквайте студентът да посочи конкретния conditional/query/filter/sink и да обясни защо предишните слоеве не осигуряват тази гаранция. Risk scoring е условен; различна оценка се приема при верни assumptions и evidence.

## 10. Примерна защита

Използвайте стандартния resource server и Nimbus verifier с доверен public key, фиксиран algorithm и fail-closed behavior. Разделете session chain от stateless Bearer chain. Guided целта е криптографското отхвърляне на modified bytes; application policy за issuer/audience/time/scope се защитава отделно. Не log-вайте raw Authorization header.

Guided reference е secure code path в общия проект. След студентски fix лабораторният path трябва да има същия invariant; смяна на mode не се приема.

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА:**

Демонстрацията сменя sub, без да издава реални admin permissions извън лабораторния marker endpoint. Не показвайте independent answers преди работа. Очаквайте explicit presence check за exp/sub и audience membership, timestamp validator с Clock, authority mapping и реални signed token HTTP tests. За липсващ exp test разширете test signing helper, не production decoder.

При review търсете server-side control, устойчивост на alternative entry points и минимум един положителен тест. Не изисквайте еднакъв source code, ако security contract е изпълнен.

## 11. Очаквани security tests

- valid signed token → 200 и правилен subject
- expired token → 401
- modified payload / чужда signature → 401
- wrong issuer / audience → 401
- missing required scope → 403
- malformed / missing token → 401

Основен selector: `JwtSecurityTest`. Изисквайте нов test по independent acceptance criteria. Проверете, че тестът се проваля при временно връщане на дефекта, а не защото липсват credentials/CSRF/Docker.

## 12. Как да се reset-не лабораторията

От корена на курса: `docker compose restart app` чисти limiter counters, sessions и временните JWT signing keys; browser cookies се изчистват и login се повтаря. AES secret file остава и persistent encrypted data трябва да се decrypt-ва със същия key.

За чисти DB fixtures: `docker compose down -v`, след проверка че project е само учебният `applied-web-security`, после `docker compose up -d --build`. Това изтрива учебните comments/profile changes и seed-ва трите accounts/documents. Не използвайте global prune. Source fixes не се отменят от DB reset; използвайте собствен version-control checkpoint, без да overwrite-вате студентска работа.

## 13. Проверка на самостоятелната задача

- [ ] Problem statement е реализиран, не само описан.
- [ ] Всички requirements и constraints от lab09.md са покрити.
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

1. **Защо decode не е verify?**
   Очакван отговор: Base64url parsing не доказва кой е създал bytes.

2. **Какво гарантира signature?**
   Очакван отговор: Integrity и origin само при доверен key/algorithm, не confidentiality или permissions.

3. **Защо aud се проверява?**
   Очакван отговор: Предотвратява приемане на token, издаден за друг ресурс.

4. **Защо @WithMockJwt не стига?**
   Очакван отговор: Заобикаля real decoder и signature проверката.

5. **Защо missing scope е 403?**
   Очакван отговор: Identity е валидна, но required authority липсва.

6. **Може ли signed token да се replay-не?**
   Очакван отговор: Да; Bearer possession е достатъчно до expiry/revocation.

## 16. Връзка със следващото упражнение

Последният lab комбинира controls и проверява дали regression suite улавя повторната поява на дефектите.
