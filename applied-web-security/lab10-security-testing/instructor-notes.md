# Преподавателски бележки — упражнение 10: Security Testing и интегрирана защита

**Само за преподавателя. НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА.**
Всички демонстрации са само в предоставената локална учебна среда.

## 1. Концептуална цел

Комбинираните failures често имат различни trust assumptions: identity вместо ownership, text вместо SQL/HTML structure или конфигурация, която изключва control. Един успешен scanner pass или green mocked test не доказва, че всички тези boundaries са защитени.

Студентът трябва да аргументира invariant и boundary, не просто да получи green status.

## 2. Какво НЕ е основната цел

Запаметяване на payload, механично копиране на configuration или annotation. За тази тема основният инженерeн въпрос е: как **Цялата архитектура → evidence → fixes → regression suite** създава и проверява доверие? Оценявайте причинното обяснение и evidence.

## 3. Подготовка преди часа

- От корена на курса изпълнете init-secrets и проверете Docker/Compose; ports 8080/8081 трябва да са свободни.
- Подгответе users alice/bob/admin от README; използвайте само synthetic данни.
- Задайте `LAB_MODE=lab10`; направете reset според README. При lab02 е нужен чист volume за {noop} seed.
- Стартирайте `docker compose up -d --build`; изчакайте /health=200.
- Изпълнете `mvn test '-Dlab.mode=lab10' '-Dtest=WebSecurityTest,JwtSecurityTest,LoginGuardTest,VaultTest,AuditTest'` от vulnerable-app; очаквайте security assertion failures преди fix.
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

Преди учебен release е предоставен build с няколко върнати дефекта. Вашата задача е да направите bounded security review, да защитите системата и да докажете, че findings са отстранени без счупена нормална функционалност.

Първо поискайте прогноза за резултата, след това покажете наблюдението. Разделете факта от предположението.

## 6. Основни точки за обяснение

Lab10 активира 3,5,6: IDOR, SQL Injection и XSS. Не разкривайте пълната карта преди първоначалния review. Очаквайте distinct evidence и tests за всеки. Проверете дали student не е изключил LabMode. За independent module приемайте логически/конфигурационни observations с измеримо evidence; не изисквайте измислени vulnerabilities, ако control е правилен.

Проследете allowed и denied flow през реалния code anchor, не само през диаграма.

## 7. Чести грешки

- Само scanner screenshot.
- Risk без asset/precondition.
- Смяна на profile вместо code fix.
- Tests, които проверяват само helper.
- Предаване на secrets в evidence.
- Всяка dependency finding се маркира exploitable без проверка.

## 8. Насочващи въпроси

- Коя стойност е под контрола на client?
- Къде точно тя преминава trust boundary в този flow?
- Кой allowed request трябва да остане работещ?
- Как ще различите липсващ control от test setup failure?
- Как ще докажете, че fix не е само промяна на видимия status?

Не давайте готовия independent code. При блокиране посочете boundary или test precondition.

## 9. Root cause

Комбинираните failures често имат различни trust assumptions: identity вместо ownership, text вместо SQL/HTML structure или конфигурация, която изключва control. Един успешен scanner pass или green mocked test не доказва, че всички тези boundaries са защитени.

Техническият locus е **Цялата архитектура → evidence → fixes → regression suite**. Изисквайте студентът да посочи конкретния conditional/query/filter/sink и да обясни защо предишните слоеве не осигуряват тази гаранция. Risk scoring е условен; различна оценка се приема при верни assumptions и evidence.

## 10. Примерна защита

Използвайте server-side object policy, parameterized SQL и context-aware output control на съответните места. За конфигурацията запазете deny-by-default и отделните chains. Logging използва server-generated correlation ID и allowlisted fields; добавете event type без request body/header dump. Dependency update се приема след repeatable build и tests, а не само промяна на version string.

Guided reference е secure code path в общия проект. След студентски fix лабораторният path трябва да има същия invariant; смяна на mode не се приема.

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА:**

Lab10 активира 3,5,6: IDOR, SQL Injection и XSS. Не разкривайте пълната карта преди първоначалния review. Очаквайте distinct evidence и tests за всеки. Проверете дали student не е изключил LabMode. За independent module приемайте логически/конфигурационни observations с измеримо evidence; не изисквайте измислени vulnerabilities, ако control е правилен.

При review търсете server-side control, устойчивост на alternative entry points и минимум един положителен тест. Не изисквайте еднакъв source code, ако security contract е изпълнен.

## 11. Очаквани security tests

- authentication и session lifecycle
- owner/admin/non-owner policy
- brute-force threshold/expiration
- SQL input остава data; XSS input остава text
- CSRF mutation отказ без state change
- crypto roundtrip/tamper/logging
- JWT signature/claims/scope
- config, headers, real PostgreSQL и cookie flags

Основен selector: `WebSecurityTest,JwtSecurityTest,LoginGuardTest,VaultTest,AuditTest`. Изисквайте нов test по independent acceptance criteria. Проверете, че тестът се проваля при временно връщане на дефекта, а не защото липсват credentials/CSRF/Docker.

## 12. Как да се reset-не лабораторията

От корена на курса: `docker compose restart app` чисти limiter counters, sessions и временните JWT signing keys; browser cookies се изчистват и login се повтаря. AES secret file остава и persistent encrypted data трябва да се decrypt-ва със същия key.

За чисти DB fixtures: `docker compose down -v`, след проверка че project е само учебният `applied-web-security`, после `docker compose up -d --build`. Това изтрива учебните comments/profile changes и seed-ва трите accounts/documents. Не използвайте global prune. Source fixes не се отменят от DB reset; използвайте собствен version-control checkpoint, без да overwrite-вате студентска работа.

## 13. Проверка на самостоятелната задача

- [ ] Problem statement е реализиран, не само описан.
- [ ] Всички requirements и constraints от lab10.md са покрити.
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

1. **Какво е доказателство за поправка?**
   Очакван отговор: Red test на дефекта, green на fix и positive control за нормален flow.

2. **Защо severity не е равна на risk?**
   Очакван отговор: Risk зависи от exposure, reachability, assets и controls в конкретната система.

3. **Какво прави finding actionable?**
   Очакван отговор: Ясни preconditions, evidence, code cause, mitigation и проверим acceptance test.

4. **Защо correlation ID е server-generated?**
   Очакван отговор: Не позволява произволно log съдържание и объркване чрез клиентски identifier.

5. **Кога scanner pass е недостатъчен?**
   Очакван отговор: При business authorization, пропуснат authenticated coverage или disabled checks.

6. **Кога review е приключил?**
   Очакван отговор: Findings имат disposition, checks са изпълнени, limitations са явно записани.

## 16. Връзка със следващото упражнение

Курсът завършва с повторно изпълнима regression suite и защита на residual-risk решенията.
