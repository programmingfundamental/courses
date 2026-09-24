---
title: Задачи
taskPage: true
sidebar:
  label: Задачи
  order: 100
---

## 9. Водена практическа задача

Преди работа следвайте [startup/reset](/courses/bg/ueb-sigurnost/podgotovka/). Командите за Maven се изпълняват от `vulnerable-app`, а Compose командите — от корена на курса.

### Стъпка 1

Стартирайте lab05 и влезте като Alice. GET /api/search?q=notes → само нейния документ. За сравнение empty q връща нейните два документа.

### Стъпка 2

Използвайте локалната команда от resources с q=`' OR '1'='1' -- `, без destructive statements. В уязвимия режим резултатът включва Bob. Запишете request parameter и owner списъка като evidence.

### Стъпка 3

Възстановете получения SQL на хартия. Посочете затворения literal, OR condition и коментара. Проверете O'Reilly: валиден текст не трябва да причинява query error.

### Стъпка 4

Заменете конкатенацията със `owner=? AND title LIKE ?`; bind-нете principal и `%`+q+`%` като values. Ограничете размера и NUL input; не правете blacklist на SQL keywords.

### Стъпка 5

Изпълнете lab05 regression и PostgresIT. Добавете test за `%` и `_`, като ясно документирате, че wildcard search е позволен, но owner isolation остава. Проверете, че error response няма SQL.

Разпределение: scenario/theory 15 мин, наблюдение 15, root cause 10, guided fix 25, tests 15, самостоятелна работа 25, защита 5. Общо 110 минути.

## 10. Анализ на root cause

q преминава от данни към синтаксис преди заявката да достигне DB parser. Authenticated identity не прави произволния search input доверен. Ownership условие в конкатениран query може да бъде заобиколено от променената boolean структура.

Предайте причинна верига: **недоверен вход → нарушено assumption → липсващ/грешен control → наблюдавано въздействие**. Оценете likelihood/impact по скалата в [threat model](/courses/bg/ueb-sigurnost/materiali/architecture/threat-model/); аргументирайте residual risk след fix.

## 11. Реализация на защита

Guided pattern: `db.query("SELECT ... WHERE owner=? AND title LIKE ?", mapper, principal, "%" + q + "%")`. SQL структурата остава константна. За sorting използвайте map от публично enum към фиксирани SQL identifiers; не поставяйте untrusted column name в placeholder. DB runtime user няма CREATE/DDL; обработвайте malformed inputs с 400 без SQL текст.

Reference branch в общия проект служи за guided comparison. За предаване променете уязвимия code path и запазете същия lab mode. Самостоятелната задача по-долу изисква собствено разширение и няма готово решение в този файл.

## 12. Security regression test

Изпълнете:

```powershell
mvn test '-Dlab.mode=lab05' '-Dtest=WebSecurityTest#lab05*'
```

Преди поправка очаквайте assertion failure, който показва дефекта. След поправката същата команда трябва да премине. Infrastructure error не е валиден red security test.

Примерен test fragment (пълният runnable class и imports са в `vulnerable-app/src/test/java/bg/tuvarna/lab`):

```java
mvc.perform(get("/api/search").with(user("alice"))
    .param("q", "' OR '1'='1' -- "))
    .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
mvc.perform(get("/api/search").with(user("alice")).param("q", "O'Reilly"))
    .andExpect(jsonPath("$.length()").value(1));
```

Задължителна матрица:

- normal notes → един result;
- O'Reilly → валиден result;
- structured malicious input → нула results;
- empty input → само собствени docs;
- oversized/NUL input → 400;
- wildcards → allowed semantics, без чужди owners;

Добавете поне един нов автоматизиран test за edge case или самостоятелната задача. Повторете `mvn test` след fix и накрая `mvn verify -Psecurity-tests`. Проверявайте content/state/identity, когато са приложими, а не само HTTP status.

## 13. Самостоятелна задача

**Problem statement:** намерете втория unsafe query в `vulnerable-app/src/main/resources/exercises/UnsafeLookup.java.txt`; той е starter за нов exact-title lookup module.

**Requirements:** интегрирайте lookup като `GET /api/lookup?title=` и поправете query construction; резултатите трябва да са само за текущия user.

**Constraints:** snippet-ът не е активен endpoint преди задачата; не копирайте готово решение от search; exact match не е LIKE; без destructive payloads.

**Acceptance criteria:** normal title, apostrophe, empty title, oversized value и boolean structured input са автоматизирано покрити. Един и същ title при Alice/Bob не нарушава isolation. Покажете red test върху unsafe starter и green след fix.

Предайте собствен code diff, test report и кратка аргументация. Не включвайте реални secrets или сурови session/token стойности в evidence.

## 14. Edge cases

- `%`/`_` са allowed wildcard data, но не сменят SQL structure.
- Apostrophe и кирилица в едно заглавие.
- Dynamic sort identifier не може да се bind-не като value.
- H2 compatibility mode не гарантира PostgreSQL semantics.

Изберете поне един за нов regression test и обяснете кой security invariant защитава.

## 15. Въпроси за анализ

1. Защо validation не заменя binding?
2. Какво е wrong при concatenated JPA native query?
3. Защо injection може да е опасен без DELETE?
4. Как се поддържа dynamic ORDER BY?
5. Защо O'Reilly е важен тест?
6. Защо PostgreSQL test е отделен?

## 16. Очакван резултат

Работещ guided fix за **SQL Injection**, контролиран before/after evidence, root-cause анализ, test matrix и реализирана самостоятелна задача според acceptance criteria. Нормалният разрешен flow остава работещ. Отчетът разграничава доказаното с автоматизиран test, провереното ръчно и оставащите ограничения.

## 17. Checklist

- [ ] Vulnerability reproduced само в lab (за lab01 — unsafe config fixture анализиран).
- [ ] Root cause и trust assumption идентифицирани.
- [ ] Correct mitigation implemented server-side.
- [ ] Regression test показва red → green.
- [ ] Положителната функционалност остава работеща.
- [ ] Edge case има автоматизирана проверка.
- [ ] Самостоятелната задача покрива acceptance criteria.
- [ ] Evidence не съдържа secrets; reset процедурата е проверена.
