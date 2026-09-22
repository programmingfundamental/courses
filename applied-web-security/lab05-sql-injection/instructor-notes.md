# Преподавателски бележки — упражнение 5: SQL Injection

**Само за преподавателя. НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА.**
Всички демонстрации са само в предоставената локална учебна среда.

## 1. Концептуална цел

q преминава от данни към синтаксис преди заявката да достигне DB parser. Authenticated identity не прави произволния search input доверен. Ownership условие в конкатениран query може да бъде заобиколено от променената boolean структура.

Студентът трябва да аргументира invariant и boundary, не просто да получи green status.

## 2. Какво НЕ е основната цел

Запаметяване на payload, механично копиране на configuration или annotation. За тази тема основният инженерeн въпрос е: как **Documents.search → JDBC query → DB parser** създава и проверява доверие? Оценявайте причинното обяснение и evidence.

## 3. Подготовка преди часа

- От корена на курса изпълнете init-secrets и проверете Docker/Compose; ports 8080/8081 трябва да са свободни.
- Подгответе users alice/bob/admin от README; използвайте само synthetic данни.
- Задайте `LAB_MODE=lab05`; направете reset според README. При lab02 е нужен чист volume за {noop} seed.
- Стартирайте `docker compose up -d --build`; изчакайте /health=200.
- Изпълнете `mvn test '-Dlab.mode=lab05' '-Dtest=WebSecurityTest#lab05*'` от vulnerable-app; очаквайте security assertion failures преди fix.
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

Search трябва да показва само документите на текущия user. Един search string променя структурата на SQL условието и резултатите вече съдържат Bob документи при Alice session.

Първо поискайте прогноза за резултата, след това покажете наблюдението. Разделете факта от предположението.

## 6. Основни точки за обяснение

Не използвайте UNION към системни таблици, файлови операции или delay payloads. Трите seed docs са достатъчни за confidentiality evidence. За exact lookup очакваният fix bind-ва и owner, и title и има equality semantics; test-ът с duplicate title при два owners разграничава injection fix от ownership fix.

Проследете allowed и denied flow през реалния code anchor, не само през диаграма.

## 7. Чести грешки

- Blacklist на OR/SELECT.
- Ръчно replace на apostrophes.
- Bind на title, но concatenation на owner.
- Преминаване към ORM като единствен fix.
- Положителен test без non-owner assertions.
- SQL details в error response.

## 8. Насочващи въпроси

- Коя стойност е под контрола на client?
- Къде точно тя преминава trust boundary в този flow?
- Кой allowed request трябва да остане работещ?
- Как ще различите липсващ control от test setup failure?
- Как ще докажете, че fix не е само промяна на видимия status?

Не давайте готовия independent code. При блокиране посочете boundary или test precondition.

## 9. Root cause

q преминава от данни към синтаксис преди заявката да достигне DB parser. Authenticated identity не прави произволния search input доверен. Ownership условие в конкатениран query може да бъде заобиколено от променената boolean структура.

Техническият locus е **Documents.search → JDBC query → DB parser**. Изисквайте студентът да посочи конкретния conditional/query/filter/sink и да обясни защо предишните слоеве не осигуряват тази гаранция. Risk scoring е условен; различна оценка се приема при верни assumptions и evidence.

## 10. Примерна защита

Guided pattern: `db.query("SELECT ... WHERE owner=? AND title LIKE ?", mapper, principal, "%" + q + "%")`. SQL структурата остава константна. За sorting използвайте map от публично enum към фиксирани SQL identifiers; не поставяйте untrusted column name в placeholder. DB runtime user няма CREATE/DDL; обработвайте malformed inputs с 400 без SQL текст.

Guided reference е secure code path в общия проект. След студентски fix лабораторният path трябва да има същия invariant; смяна на mode не се приема.

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА:**

Не използвайте UNION към системни таблици, файлови операции или delay payloads. Трите seed docs са достатъчни за confidentiality evidence. За exact lookup очакваният fix bind-ва и owner, и title и има equality semantics; test-ът с duplicate title при два owners разграничава injection fix от ownership fix.

При review търсете server-side control, устойчивост на alternative entry points и минимум един положителен тест. Не изисквайте еднакъв source code, ако security contract е изпълнен.

## 11. Очаквани security tests

- normal notes → един result
- O'Reilly → валиден result
- structured malicious input → нула results
- empty input → само собствени docs
- oversized/NUL input → 400
- wildcards → allowed semantics, без чужди owners

Основен selector: `WebSecurityTest#lab05*`. Изисквайте нов test по independent acceptance criteria. Проверете, че тестът се проваля при временно връщане на дефекта, а не защото липсват credentials/CSRF/Docker.

## 12. Как да се reset-не лабораторията

От корена на курса: `docker compose restart app` чисти limiter counters, sessions и временните JWT signing keys; browser cookies се изчистват и login се повтаря. AES secret file остава и persistent encrypted data трябва да се decrypt-ва със същия key.

За чисти DB fixtures: `docker compose down -v`, след проверка че project е само учебният `applied-web-security`, после `docker compose up -d --build`. Това изтрива учебните comments/profile changes и seed-ва трите accounts/documents. Не използвайте global prune. Source fixes не се отменят от DB reset; използвайте собствен version-control checkpoint, без да overwrite-вате студентска работа.

## 13. Проверка на самостоятелната задача

- [ ] Problem statement е реализиран, не само описан.
- [ ] Всички requirements и constraints от lab05.md са покрити.
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

1. **Защо validation не заменя binding?**
   Очакван отговор: Допустимите текстови стойности съдържат special characters; parser separation е структурният control.

2. **Какво е wrong при concatenated JPA native query?**
   Очакван отговор: ORM не променя вече построения SQL string.

3. **Защо injection може да е опасен без DELETE?**
   Очакван отговор: SELECT може да разкрие чужди данни.

4. **Как се поддържа dynamic ORDER BY?**
   Очакван отговор: Server allowlist на фиксирани identifiers.

5. **Защо O'Reilly е важен тест?**
   Очакван отговор: Доказва нормална функционалност и че fix не е blacklist на apostrophe.

6. **Защо PostgreSQL test е отделен?**
   Очакван отговор: Реалният dialect и driver могат да се различават от H2.

## 16. Връзка със следващото упражнение

В browser rendering същата граница code/data се проявява като XSS, но защитата зависи от output context.
