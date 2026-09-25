# Преподавателски бележки — упражнение 8: Криптография и защита на чувствителни данни

**Само за преподавателя. НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА.**
Всички демонстрации са само в предоставената локална учебна среда.

## 1. Концептуална цел

Storage access се смята за еквивалентен на permission за четене на чувствителните полета. Ако key и ciphertext са в един dump, encryption добавя малка защита; затова key boundary е отделен. Непроверен authentication tag или nonce reuse нарушават гаранциите на AEAD.

Студентът трябва да аргументира invariant и boundary, не просто да получи green status.

## 2. Какво НЕ е основната цел

Запаметяване на payload, механично копиране на configuration или annotation. За тази тема основният инженерeн въпрос е: как **Web.sensitive → Vault → encrypted field в PostgreSQL** създава и проверява доверие? Оценявайте причинното обяснение и evidence.

## 3. Подготовка преди часа

- От корена на курса изпълнете init-secrets и проверете Docker/Compose; ports 8080/8081 трябва да са свободни.
- Подгответе users alice/bob/admin от README; използвайте само synthetic данни.
- Задайте `LAB_MODE=lab08`; направете reset според README. При lab02 е нужен чист volume за {noop} seed.
- Стартирайте `docker compose up -d --build`; изчакайте /health=200.
- Изпълнете `mvn test '-Dlab.mode=lab08' '-Dtest=WebSecurityTest#lab08*'` от vulnerable-app; очаквайте security assertion failures преди fix.
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

Backup на учебния портал съдържа личен identifier в plaintext. Authorization върху endpoint не помага на offline reader на този backup. Трябва да защитите полето, без да създавате собствен crypto алгоритъм.

Първо поискайте прогноза за резултата, след това покажете наблюдението. Разделете факта от предположението.

## 6. Основни точки за обяснение

Не позволявайте демонстрация с истински личен identifier. Подробният reference Vault използва AES-GCM; оценявайте lifecycle, а не запаметяване на Cipher.init. За independent record: password → password hash; verify-only API token → high-entropy token + hash; outbound secret/DB credential → secret manager или protected key storage; personal identifier → AEAD според threat model; session identifier → SecureRandom, кратък lifecycle и revocation.

Проследете allowed и denied flow през реалния code anchor, не само през диаграма.

## 7. Чести грешки

- Еднакъв static IV за всеки запис.
- Hardcoded AES key в Git.
- Използване на Random вместо SecureRandom.
- Игнориране на bad tag exception.
- Криптиране на passwords за login.
- Key и dump се публикуват заедно като evidence.

## 8. Насочващи въпроси

- Коя стойност е под контрола на client?
- Къде точно тя преминава trust boundary в този flow?
- Кой allowed request трябва да остане работещ?
- Как ще различите липсващ control от test setup failure?
- Как ще докажете, че fix не е само промяна на видимия status?

Не давайте готовия independent code. При блокиране посочете boundary или test precondition.

## 9. Root cause

Storage access се смята за еквивалентен на permission за четене на чувствителните полета. Ако key и ciphertext са в един dump, encryption добавя малка защита; затова key boundary е отделен. Непроверен authentication tag или nonce reuse нарушават гаранциите на AEAD.

Техническият locus е **Web.sensitive → Vault → encrypted field в PostgreSQL**. Изисквайте студентът да посочи конкретния conditional/query/filter/sink и да обясни защо предишните слоеве не осигуряват тази гаранция. Risk scoring е условен; различна оценка се приема при верни assumptions и evidence.

## 10. Примерна защита

Използвайте стандартен AES-GCM чрез Vault, random nonce за всяка операция и owner като AAD. Envelope има version `v1`, nonce, ciphertext и tag; това не е custom crypto algorithm. Не логвайте crypto exceptions с input/key material. Persistent key е нужен при persistent DB; временно генериран key е допустим само с временната H2 база. За rotation проектирайте key ID и decrypt-old/encrypt-new стратегия.

Guided reference е secure code path в общия проект. След студентски fix лабораторният path трябва да има същия invariant; смяна на mode не се приема.

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА:**

Не позволявайте демонстрация с истински личен identifier. Подробният reference Vault използва AES-GCM; оценявайте lifecycle, а не запаметяване на Cipher.init. За independent record: password → password hash; verify-only API token → high-entropy token + hash; outbound secret/DB credential → secret manager или protected key storage; personal identifier → AEAD според threat model; session identifier → SecureRandom, кратък lifecycle и revocation.

При review търсете server-side control, устойчивост на alternative entry points и минимум един положителен тест. Не изисквайте еднакъв source code, ако security contract е изпълнен.

## 11. Очаквани security tests

- encrypt/decrypt → original value
- еднакъв input два пъти → различни envelopes
- wrong key/owner → generic error
- corrupted ciphertext/tag → rejected
- null/empty/malformed envelope → rejected
- logs не съдържат synthetic field или credentials

Основен selector: `WebSecurityTest#lab08*`. Изисквайте нов test по independent acceptance criteria. Проверете, че тестът се проваля при временно връщане на дефекта, а не защото липсват credentials/CSRF/Docker.

## 12. Как да се reset-не лабораторията

От корена на курса: `docker compose restart app` чисти limiter counters, sessions и временните JWT signing keys; browser cookies се изчистват и login се повтаря. AES secret file остава и persistent encrypted data трябва да се decrypt-ва със същия key.

За чисти DB fixtures: `docker compose down -v`, след проверка че project е само учебният `applied-web-security`, после `docker compose up -d --build`. Това изтрива учебните comments/profile changes и seed-ва трите accounts/documents. Не използвайте global prune. Source fixes не се отменят от DB reset; използвайте собствен version-control checkpoint, без да overwrite-вате студентска работа.

## 13. Проверка на самостоятелната задача

- [ ] Problem statement е реализиран, не само описан.
- [ ] Всички requirements и constraints от lab08.md са покрити.
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

1. **Защо Base64 не е encryption?**
   Очакван отговор: Декодирането не изисква secret key.

2. **Защо password не се encrypt-ва?**
   Очакван отговор: Нужна е само проверка; reversible storage разкрива всички passwords при key compromise.

3. **Защо GCM tag е нужен?**
   Очакван отговор: Проверява authenticity/integrity преди връщане на plaintext.

4. **Какво пази AAD owner?**
   Очакван отговор: Предотвратява незабелязано преместване на ciphertext между owners.

5. **Какъв риск остава при app compromise?**
   Очакван отговор: App има key и може да decrypt-ва.

6. **Как се прави rotation без загуба?**
   Очакван отговор: Version/key ID, наличен old key за controlled re-encryption, atomic migration и backup.

## 16. Връзка със следващото упражнение

Подписаните JWT също изискват правилен crypto mechanism и validation; payload остава четим.
