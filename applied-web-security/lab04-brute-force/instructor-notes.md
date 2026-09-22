# Преподавателски бележки — упражнение 4: Brute-Force атаки и защита на Authentication

**Само за преподавателя. НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА.**
Всички демонстрации са само в предоставената локална учебна среда.

## 1. Концептуална цел

Сървърът разглежда всеки login изолирано и не пази история на отказите. Сигурното password hashing увеличава цената, но не задава policy за броя online опити. Неправилно reset/expire logic може да направи limiter-а неефективен или да заключи account завинаги.

Студентът трябва да аргументира invariant и boundary, не просто да получи green status.

## 2. Какво НЕ е основната цел

Запаметяване на payload, механично копиране на configuration или annotation. За тази тема основният инженерeн въпрос е: как **AuthenticationProvider → LoginGuard → clock/counters** създава и проверява доверие? Оценявайте причинното обяснение и evidence.

## 3. Подготовка преди часа

- От корена на курса изпълнете init-secrets и проверете Docker/Compose; ports 8080/8081 трябва да са свободни.
- Подгответе users alice/bob/admin от README; използвайте само synthetic данни.
- Задайте `LAB_MODE=lab04`; направете reset според README. При lab02 е нужен чист volume за {noop} seed.
- Стартирайте `docker compose up -d --build`; изчакайте /health=200.
- Изпълнете `mvn test '-Dlab.mode=lab04' '-Dtest=WebSecurityTest#lab04*'` от vulnerable-app; очаквайте security assertion failures преди fix.
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

Няколко последователни грешни пароли не предизвикват никакво ограничение. Трябва да ограничите guessing срещу учебен account, като не позволите permanent lockout да се превърне в лесен denial of service.

Първо поискайте прогноза за резултата, след това покажете наблюдението. Разделете факта от предположението.

## 6. Основни точки за обяснение

Simulation е ограничена до шест грешки и един валиден опит. Демонстрирайте разликата между green unit tests и red HTTP integration test при bypass на guard. За самостоятелната задача очаквайте test configuration с @Primary mutable Clock и properties; не приемайте промяна на production sleep или tests, които чакат 30 секунди.

Проследете allowed и denied flow през реалния code anchor, не само през диаграма.

## 7. Чести грешки

- Permanent lockout без expiry.
- Reset при всеки request вместо след success.
- Trust към клиентски X-Forwarded-For.
- Отделно get/put без атомарност.
- Различни съобщения за missing/blocked users.
- In-memory решение обявено за cluster-ready.

## 8. Насочващи въпроси

- Коя стойност е под контрола на client?
- Къде точно тя преминава trust boundary в този flow?
- Кой allowed request трябва да остане работещ?
- Как ще различите липсващ control от test setup failure?
- Как ще докажете, че fix не е само промяна на видимия status?

Не давайте готовия independent code. При блокиране посочете boundary или test precondition.

## 9. Root cause

Сървърът разглежда всеки login изолирано и не пази история на отказите. Сигурното password hashing увеличава цената, но не задава policy за броя online опити. Неправилно reset/expire logic може да направи limiter-а неефективен или да заключи account завинаги.

Техническият locus е **AuthenticationProvider → LoginGuard → clock/counters**. Изисквайте студентът да посочи конкретния conditional/query/filter/sink и да обясни защо предишните слоеве не осигуряват тази гаранция. Risk scoring е условен; различна оценка се приема при верни assumptions и evidence.

## 10. Примерна защита

Използвайте atomic update на per-account state, временна блокировка и expiry. Policy е configurable чрез MAX_ATTEMPTS, LOCK_DURATION и RESET_AFTER_SUCCESS. В предоставената реализация failed-attempt window се обновява при failure; блокираните requests се отказват преди нов failure. Map е bounded приблизително, но single-process counters не са защита за cluster. Анализирайте IP throttling като втори слой, доверен само на презаписания proxy address.

Guided reference е secure code path в общия проект. След студентски fix лабораторният path трябва да има същия invariant; смяна на mode не се приема.

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА:**

Simulation е ограничена до шест грешки и един валиден опит. Демонстрирайте разликата между green unit tests и red HTTP integration test при bypass на guard. За самостоятелната задача очаквайте test configuration с @Primary mutable Clock и properties; не приемайте промяна на production sleep или tests, които чакат 30 секунди.

При review търсете server-side control, устойчивост на alternative entry points и минимум един положителен тест. Не изисквайте еднакъв source code, ако security contract е изпълнен.

## 11. Очаквани security tests

- под прага → валиден login е приет
- прагът е достигнат → следващ валиден login е отказан
- blocked request → generic 401 и без изместване на срока
- точно на expiry → разрешен нов опит
- success reset true/false → съответните counters
- concurrent failures → няма изгубени increments

Основен selector: `WebSecurityTest#lab04*`. Изисквайте нов test по independent acceptance criteria. Проверете, че тестът се проваля при временно връщане на дефекта, а не защото липсват credentials/CSRF/Docker.

## 12. Как да се reset-не лабораторията

От корена на курса: `docker compose restart app` чисти limiter counters, sessions и временните JWT signing keys; browser cookies се изчистват и login се повтаря. AES secret file остава и persistent encrypted data трябва да се decrypt-ва със същия key.

За чисти DB fixtures: `docker compose down -v`, след проверка че project е само учебният `applied-web-security`, после `docker compose up -d --build`. Това изтрива учебните comments/profile changes и seed-ва трите accounts/documents. Не използвайте global prune. Source fixes не се отменят от DB reset; използвайте собствен version-control checkpoint, без да overwrite-вате студентска работа.

## 13. Проверка на самостоятелната задача

- [ ] Problem statement е реализиран, не само описан.
- [ ] Всички requirements и constraints от lab04.md са покрити.
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

1. **Защо account lockout създава DoS риск?**
   Очакван отговор: Друг може умишлено да изчерпи attempts за жертвата.

2. **Защо IP-only не е достатъчно?**
   Очакван отговор: NAT смесва users, а distributed sources заобикалят лимита.

3. **Защо не използваме sleep в тест?**
   Очакван отговор: Прави го бавен/нестабилен; Clock позволява точен boundary.

4. **Как се проверява wiring?**
   Очакван отговор: Real login requests след failures, не само извикване на map.

5. **Какво се логва?**
   Очакван отговор: Outcome, correlation и безопасен actor identifier при обоснована нужда, без парола.

6. **Какъв е cluster проблемът?**
   Очакван отговор: Разделени counters позволяват повече опити и несъгласуван lockout.

## 16. Връзка със следващото упражнение

След ограничаване на login abuse преминаваме към нарушаване на границата data/code в SQL.
