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

Стартирайте `lab03`, login като Alice и GET /api/documents/1 → 200. Отбележете principal и owner, не само status.

### Стъпка 2

GET /api/documents/2 със същата session → 200 и Bob owner в уязвимия режим. Това е цялата контролирана reproduction; използвайте само IDs 1–3.

### Стъпка 3

Прочетете SecurityConfig и Documents.get. Посочете коя проверка доказва authentication и къде липсва relation principal–object. Изпълнете test матрицата: different user case трябва да е red.

### Стъпка 4

Реализирайте policy в service code path: ownership или ADMIN, иначе същия 404 като missing resource. Owner никога не се приема от клиентски parameter. Проверете отделно /admin/status с USER → 403.

### Стъпка 5

Добавете тест за директно service извикване през Spring proxy и за ID=-1. Запазете положителните owner/admin tests и повторете лабораторния режим без да изключвате флага.

Разпределение: scenario/theory 15 мин, наблюдение 15, root cause 10, guided fix 25, tests 15, самостоятелна работа 25, защита 5. Общо 110 минути.

## 10. Анализ на root cause

Приложението вярва, че щом потребителят знае ID и е authenticated, той е authorized. ID принадлежи на client-controlled входа. Role проверка само на /admin не покрива object-level достъп на нормалните users.

Предайте причинна верига: **недоверен вход → нарушено assumption → липсващ/грешен control → наблюдавано въздействие**. Оценете likelihood/impact по скалата в [threat model](/courses/bg/ueb-sigurnost/materiali/architecture/threat-model/); аргументирайте residual risk след fix.

## 11. Реализация на защита

Извлечете principal от Authentication, сравнете owner server-side и позволете изричен admin override. Използвайте service boundary, за да няма алтернативен controller без policy. За операции read-modify-write обсъдете transaction/TOCTOU; lookup-then-update може да има race, ако ownership се променя.

Reference branch в общия проект служи за guided comparison. За предаване променете уязвимия code path и запазете същия lab mode. Самостоятелната задача по-долу изисква собствено разширение и няма готово решение в този файл.

## 12. Security regression test

Изпълнете:

```powershell
mvn test '-Dlab.mode=lab03' '-Dtest=WebSecurityTest#lab03*'
```

Преди поправка очаквайте assertion failure, който показва дефекта. След поправката същата команда трябва да премине. Infrastructure error не е валиден red security test.

Примерен test fragment (пълният runnable class и imports са в `vulnerable-app/src/test/java/bg/tuvarna/lab`):

```java
mvc.perform(get("/api/documents/1").with(user("bob")))
   .andExpect(status().isNotFound());
mvc.perform(get("/api/documents/1").with(user("admin").roles("ADMIN")))
   .andExpect(status().isOk());
```

Задължителна матрица:

- anonymous → 401;
- owner → 200 с правилния object;
- other user → 404, без чуждо съдържание;
- ADMIN → 200;
- missing ID → 404; nonnumeric → 400; USER admin route → 403;

Добавете поне един нов автоматизиран test за edge case или самостоятелната задача. Повторете `mvn test` след fix и накрая `mvn verify -Psecurity-tests`. Проверявайте content/state/identity, когато са приложими, а не само HTTP status.

## 13. Самостоятелна задача

**Problem statement:** добавете `PATCH /api/documents/{id}/title` за редактиране на заглавие.

**Requirements:** USER редактира само свой документ; ADMIN — всеки; anonymous няма достъп. Валидното заглавие е 1–200 chars.

**Constraints:** не допускайте прехвърляне на owner чрез body; използвайте CSRF за session API; запазете единна policy за read/write.

**Acceptance criteria:** матрица anonymous/owner/other/admin/missing и доказателство от DB, че отказаният PATCH не е променил нищо. Тестовете трябва да подават валиден CSRF при проверка на authorization, за да не се скрие дефект зад CSRF 403.

Предайте собствен code diff, test report и кратка аргументация. Не включвайте реални secrets или сурови session/token стойности в evidence.

## 14. Edge cases

- Отрицателен, прекалено голям или nonnumeric ID.
- Чужд и липсващ документ имат еднакъв видим отказ.
- Self-invocation може да заобиколи method-security proxy.
- Owner се променя между проверката и update.

Изберете поне един за нов regression test и обяснете кой security invariant защитава.

## 15. Въпроси за анализ

1. Защо UUID не поправя IDOR?
2. Каква е разликата horizontal/vertical?
3. Защо service policy е полезна?
4. Защо 404 при чужд документ?
5. Защо да проверим DB след отказ?
6. Какъв е рискът от owner в request body?

## 16. Очакван резултат

Работещ guided fix за **Authorization и Broken Access Control**, контролиран before/after evidence, root-cause анализ, test matrix и реализирана самостоятелна задача според acceptance criteria. Нормалният разрешен flow остава работещ. Отчетът разграничава доказаното с автоматизиран test, провереното ръчно и оставащите ограничения.

## 17. Checklist

- [ ] Vulnerability reproduced само в lab (за lab01 — unsafe config fixture анализиран).
- [ ] Root cause и trust assumption идентифицирани.
- [ ] Correct mitigation implemented server-side.
- [ ] Regression test показва red → green.
- [ ] Положителната функционалност остава работеща.
- [ ] Edge case има автоматизирана проверка.
- [ ] Самостоятелната задача покрива acceptance criteria.
- [ ] Evidence не съдържа secrets; reset процедурата е проверена.
