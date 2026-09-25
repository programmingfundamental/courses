# Преподавателски бележки — упражнение 3: Authorization и Broken Access Control

**Само за преподавателя. НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА.**
Всички демонстрации са само в предоставената локална учебна среда.

## 1. Концептуална цел

Приложението вярва, че щом потребителят знае ID и е authenticated, той е authorized. ID принадлежи на client-controlled входа. Role проверка само на /admin не покрива object-level достъп на нормалните users.

Студентът трябва да аргументира invariant и boundary, не просто да получи green status.

## 2. Какво НЕ е основната цел

Запаметяване на payload, механично копиране на configuration или annotation. За тази тема основният инженерeн въпрос е: как **Controller → Documents.get → object ownership policy** създава и проверява доверие? Оценявайте причинното обяснение и evidence.

## 3. Подготовка преди часа

- От корена на курса изпълнете init-secrets и проверете Docker/Compose; ports 8080/8081 трябва да са свободни.
- Подгответе users alice/bob/admin от README; използвайте само synthetic данни.
- Задайте `LAB_MODE=lab03`; направете reset според README. При lab02 е нужен чист volume за {noop} seed.
- Стартирайте `docker compose up -d --build`; изчакайте /health=200.
- Изпълнете `mvn test '-Dlab.mode=lab03' '-Dtest=WebSecurityTest#lab03*'` от vulnerable-app; очаквайте security assertion failures преди fix.
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

Alice отваря собствен документ с ID=1. При промяна на URL към ID=2 вижда фактура на Bob. Login е правилен; дефектът е в решението дали конкретната identity има право върху конкретния object.

Първо поискайте прогноза за резултата, след това покажете наблюдението. Разделете факта от предположението.

## 6. Основни точки за обяснение

Демонстрирайте с двете реални sessions, след което използвайте mocked users за бързата policy матрица. Reference Documents.get проверява owner/admin след DB lookup. При PATCH проверявайте server-side owner и mutation в транзакция, включително no-write on denial. Приемайте 403 вместо 404 само ако договорът е променен последователно и student обоснове disclosure.

Проследете allowed и denied flow през реалния code anchor, не само през диаграма.

## 7. Чести грешки

- Само hasRole(USER) без ownership.
- Използване на owner от body.
- Скриване на link в UI като fix.
- Всички tests с ADMIN.
- 403 поради missing CSRF се брои за authorization доказателство.
- Policy само в един controller, без service reuse.

## 8. Насочващи въпроси

- Коя стойност е под контрола на client?
- Къде точно тя преминава trust boundary в този flow?
- Кой allowed request трябва да остане работещ?
- Как ще различите липсващ control от test setup failure?
- Как ще докажете, че fix не е само промяна на видимия status?

Не давайте готовия independent code. При блокиране посочете boundary или test precondition.

## 9. Root cause

Приложението вярва, че щом потребителят знае ID и е authenticated, той е authorized. ID принадлежи на client-controlled входа. Role проверка само на /admin не покрива object-level достъп на нормалните users.

Техническият locus е **Controller → Documents.get → object ownership policy**. Изисквайте студентът да посочи конкретния conditional/query/filter/sink и да обясни защо предишните слоеве не осигуряват тази гаранция. Risk scoring е условен; различна оценка се приема при верни assumptions и evidence.

## 10. Примерна защита

Извлечете principal от Authentication, сравнете owner server-side и позволете изричен admin override. Използвайте service boundary, за да няма алтернативен controller без policy. За операции read-modify-write обсъдете transaction/TOCTOU; lookup-then-update може да има race, ако ownership се променя.

Guided reference е secure code path в общия проект. След студентски fix лабораторният path трябва да има същия invariant; смяна на mode не се приема.

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА:**

Демонстрирайте с двете реални sessions, след което използвайте mocked users за бързата policy матрица. Reference Documents.get проверява owner/admin след DB lookup. При PATCH проверявайте server-side owner и mutation в транзакция, включително no-write on denial. Приемайте 403 вместо 404 само ако договорът е променен последователно и student обоснове disclosure.

При review търсете server-side control, устойчивост на alternative entry points и минимум един положителен тест. Не изисквайте еднакъв source code, ако security contract е изпълнен.

## 11. Очаквани security tests

- anonymous → 401
- owner → 200 с правилния object
- other user → 404, без чуждо съдържание
- ADMIN → 200
- missing ID → 404; nonnumeric → 400; USER admin route → 403

Основен selector: `WebSecurityTest#lab03*`. Изисквайте нов test по independent acceptance criteria. Проверете, че тестът се проваля при временно връщане на дефекта, а не защото липсват credentials/CSRF/Docker.

## 12. Как да се reset-не лабораторията

От корена на курса: `docker compose restart app` чисти limiter counters, sessions и временните JWT signing keys; browser cookies се изчистват и login се повтаря. AES secret file остава и persistent encrypted data трябва да се decrypt-ва със същия key.

За чисти DB fixtures: `docker compose down -v`, след проверка че project е само учебният `applied-web-security`, после `docker compose up -d --build`. Това изтрива учебните comments/profile changes и seed-ва трите accounts/documents. Не използвайте global prune. Source fixes не се отменят от DB reset; използвайте собствен version-control checkpoint, без да overwrite-вате студентска работа.

## 13. Проверка на самостоятелната задача

- [ ] Problem statement е реализиран, не само описан.
- [ ] Всички requirements и constraints от lab03.md са покрити.
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

1. **Защо UUID не поправя IDOR?**
   Очакван отговор: ID може да изтече или бъде получено легитимно; policy пак липсва.

2. **Каква е разликата horizontal/vertical?**
   Очакван отговор: Чужд user object срещу административни права.

3. **Защо service policy е полезна?**
   Очакван отговор: Контролът покрива повече от един controller entry point.

4. **Защо 404 при чужд документ?**
   Очакван отговор: Намалява information disclosure за existence; договорът трябва да е последователен.

5. **Защо да проверим DB след отказ?**
   Очакван отговор: Статусът може да е правилен, но mutation да е извършена преди отказа.

6. **Какъв е рискът от owner в request body?**
   Очакван отговор: Клиентът може да подмени identity relation.

## 16. Връзка със следващото упражнение

Коректната identity и policy не предпазват от системно guessing на passwords.
