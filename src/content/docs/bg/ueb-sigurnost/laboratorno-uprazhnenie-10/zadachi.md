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

Стартирайте чист lab10. Направете bounded inventory само на предоставените endpoints. Изпълнете `mvn test -Dlab.mode=lab10` и разграничете assertion failures от environment/build errors.

### Стъпка 2

Потвърдете findings с минимум една реална local request pair: разрешен flow и нарушаващ policy flow. Класифицирайте по root cause, а не само по HTTP status. Попълнете resources/finding-template.md.

### Стъпка 3

Поправете дефектите, без да променяте LAB_MODE и без да отслабвате tests. За всеки finding добавете поне един нов edge-case regression test; повтарящите се root causes могат да имат общ control с отделни coverage checks.

### Стъпка 4

Прегледайте Maven dependency tree, Compose bindings, DB role, secret file placement и audit format. Запишете конкретно коя версия/конфигурация сте проверили. OWASP ZAP е optional: passive-only срещу localhost, ограничен context, без външни URL и без active scan.

### Стъпка 5

Пуснете пълния `mvn verify -Psecurity-tests`, след това нормален login → own document → comment → logout flow през proxy. Извършете deliberate mutation на един fix във временен local working copy, покажете red test, възстановете и покажете green.

Разпределение: scenario/theory 15 мин, наблюдение 15, root cause 10, guided fix 25, tests 15, самостоятелна работа 25, защита 5. Общо 110 минути.

## 10. Анализ на root cause

Комбинираните failures често имат различни trust assumptions: identity вместо ownership, text вместо SQL/HTML structure или конфигурация, която изключва control. Един успешен scanner pass или green mocked test не доказва, че всички тези boundaries са защитени.

Предайте причинна верига: **недоверен вход → нарушено assumption → липсващ/грешен control → наблюдавано въздействие**. Оценете likelihood/impact по скалата в [threat model](/courses/bg/ueb-sigurnost/materiali/architecture/threat-model/); аргументирайте residual risk след fix.

## 11. Реализация на защита

Използвайте server-side object policy, parameterized SQL и context-aware output control на съответните места. За конфигурацията запазете deny-by-default и отделните chains. Logging използва server-generated correlation ID и allowlisted fields; добавете event type без request body/header dump. Dependency update се приема след repeatable build и tests, а не само промяна на version string.

Reference branch в общия проект служи за guided comparison. За предаване променете уязвимия code path и запазете същия lab mode. Самостоятелната задача по-долу изисква собствено разширение и няма готово решение в този файл.

## 12. Security regression test

Изпълнете:

```powershell
mvn test '-Dlab.mode=lab10' '-Dtest=WebSecurityTest,JwtSecurityTest,LoginGuardTest,VaultTest,AuditTest'
```

Преди поправка очаквайте assertion failure, който показва дефекта. След поправката същата команда трябва да премине. Infrastructure error не е валиден red security test.

Примерен test fragment (пълният runnable class и imports са в `vulnerable-app/src/test/java/bg/tuvarna/lab`):

```java
mvc.perform(get("/health"))
   .andExpect(header().exists("X-Correlation-ID"))
   .andExpect(header().string("X-Content-Type-Options", "nosniff"));
// Добавете assertions за body/owner/state към всеки finding,
// не само status().is4xxClientError().
```

Задължителна матрица:

- authentication и session lifecycle;
- owner/admin/non-owner policy;
- brute-force threshold/expiration;
- SQL input остава data; XSS input остава text;
- CSRF mutation отказ без state change;
- crypto roundtrip/tamper/logging;
- JWT signature/claims/scope;
- config, headers, real PostgreSQL и cookie flags;

Добавете поне един нов автоматизиран test за edge case или самостоятелната задача. Повторете `mvn test` след fix и накрая `mvn verify -Psecurity-tests`. Проверявайте content/state/identity, когато са приложими, а не само HTTP status.

## 13. Самостоятелна задача

**Problem statement:** направете mini security assessment на registration + profile модула и разширете audit events за него.

**Requirements:** предайте Finding, Risk, Evidence, Root Cause, Mitigation, Regression Test за минимум 3 обосновани observations; разграничете потвърден finding от limitation/hypothesis. Добавете безопасен event type и автоматизиран no-secret-in-logs test.

**Constraints:** само локални synthetic данни; без general request/body logging; без изтриване на existing tests или смяна на vulnerable mode като fix.

**Acceptance criteria:** поне един нов regression test извън готовата suite; positive functionality test; correlation между event и request без raw session/token; остатъчен риск и приоритет за всяко observation.

Предайте собствен code diff, test report и кратка аргументация. Не включвайте реални secrets или сурови session/token стойности в evidence.

## 14. Edge cases

- Green тест срещу грешния profile/base URL.
- Случайно skip-нат Docker integration test.
- Debug logging разкрива request parameters.
- Control работи директно към app, но proxy configuration го променя.

Изберете поне един за нов regression test и обяснете кой security invariant защитава.

## 15. Въпроси за анализ

1. Какво е доказателство за поправка?
2. Защо severity не е равна на risk?
3. Какво прави finding actionable?
4. Защо correlation ID е server-generated?
5. Кога scanner pass е недостатъчен?
6. Кога review е приключил?

## 16. Очакван резултат

Работещ guided fix за **Security Testing и интегрирана защита**, контролиран before/after evidence, root-cause анализ, test matrix и реализирана самостоятелна задача според acceptance criteria. Нормалният разрешен flow остава работещ. Отчетът разграничава доказаното с автоматизиран test, провереното ръчно и оставащите ограничения.

## 17. Checklist

- [ ] Vulnerability reproduced само в lab (за lab01 — unsafe config fixture анализиран).
- [ ] Root cause и trust assumption идентифицирани.
- [ ] Correct mitigation implemented server-side.
- [ ] Regression test показва red → green.
- [ ] Положителната функционалност остава работеща.
- [ ] Edge case има автоматизирана проверка.
- [ ] Самостоятелната задача покрива acceptance criteria.
- [ ] Evidence не съдържа secrets; reset процедурата е проверена.
