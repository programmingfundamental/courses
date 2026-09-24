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

Направете пълен reset с `LAB_MODE=lab02`, за да се seed-нат уязвимите users. Стартирайте WebSecurityTest#lab02_passwordStorage в същия режим; очаквайте failure за липсващ `{bcrypt}` prefix.

### Стъпка 2

През browser login формата влезте като Alice; DevTools показва POST /login → 204 и session cookie. GET /api/me → Alice; отделен private window → 401. Никога не прилагайте cookie стойността в отчета.

### Стъпка 3

Проследете login request през SecurityConfig/provider/Accounts. С DB query прочетете само учебния password prefix; покажете защо този запис излага password. Проверете unknown user и wrong password: еднакъв generic отказ.

### Стъпка 4

Променете Accounts.encode, така че и лабораторният code path да използва PasswordEncoder. За съществуващи users опишете reset/migration стратегия; само промяна на encoder не преобразува старите записи. За упражнението изчистете учебния volume.

### Стъпка 5

Изпълнете тестовете за истински form login и logout. Добавете registration test за BCrypt prefix и два hashes на еднаква парола, които се различават, но се match-ват. Удостоверете, че новият user не може да задава ADMIN.

Разпределение: scenario/theory 15 мин, наблюдение 15, root cause 10, guided fix 25, tests 15, самостоятелна работа 25, защита 5. Общо 110 минути.

## 10. Анализ на root cause

Login успехът не доказва безопасно съхранение. `{noop}` казва на DelegatingPasswordEncoder да сравни password без hash. Доверието е неправилно прехвърлено от login UX към защита на данните в DB. Ръчен controller, който само връща 200, също не създава автоматично persistent SecurityContext.

Предайте причинна верига: **недоверен вход → нарушено assumption → липсващ/грешен control → наблюдавано въздействие**. Оценете likelihood/impact по скалата в [threat model](/courses/bg/ueb-sigurnost/materiali/architecture/threat-model/); аргументирайте residual risk след fix.

## 11. Реализация на защита

Използвайте server-side PasswordEncoder при registration/seed и DaoAuthenticationProvider при login. Guided baseline е `PasswordEncoderFactories.createDelegatingPasswordEncoder()`. Не сравнявайте нови random hashes със string equality. Запазете CSRF върху login/logout, generic failures и default session fixation protection. Отделете legacy password migration от новото записване.

Reference branch в общия проект служи за guided comparison. За предаване променете уязвимия code path и запазете същия lab mode. Самостоятелната задача по-долу изисква собствено разширение и няма готово решение в този файл.

## 12. Security regression test

Изпълнете:

```powershell
mvn test '-Dlab.mode=lab02' '-Dtest=WebSecurityTest#lab02*'
```

Преди поправка очаквайте assertion failure, който показва дефекта. След поправката същата команда трябва да премине. Infrastructure error не е валиден red security test.

Примерен test fragment (пълният runnable class и imports са в `vulnerable-app/src/test/java/bg/tuvarna/lab`):

```java
var result = mvc.perform(post("/login").with(csrf())
    .param("username", "alice").param("password", "Lab-alice-2026!"))
    .andExpect(status().isNoContent()).andReturn();
var session = (MockHttpSession) result.getRequest().getSession(false);
mvc.perform(get("/api/me").session(session))
    .andExpect(jsonPath("$.username").value("alice"));
```

Задължителна матрица:

- valid credentials → 204 и /api/me със същата session → Alice;
- wrong password и unknown user → 401 generic;
- anonymous protected endpoint → 401;
- logout → 204 и session invalidation;
- stored password → BCrypt prefix и успешен matches;

Добавете поне един нов автоматизиран test за edge case или самостоятелната задача. Повторете `mvn test` след fix и накрая `mvn verify -Psecurity-tests`. Проверявайте content/state/identity, когато са приложими, а не само HTTP status.

## 13. Самостоятелна задача

**Problem statement:** добавете `GET /api/account-summary`, който връща username и брой собствени документи.

**Requirements:** anonymous се отказва; user identity идва само от SecurityContext; count е за текущия user.

**Constraints:** не приемайте username от query parameter; не връщайте password/hash/roles от DB entity; не използвайте Basic auth вместо зададения session flow.

**Acceptance criteria:** автоматизирани anonymous, invalid-login, valid-login и cross-user isolation tests, включително Alice count=2 и Bob count=1; обяснение защо login и policy са различни отговорности.

Предайте собствен code diff, test report и кратка аргументация. Не включвайте реални secrets или сурови session/token стойности в evidence.

## 14. Edge cases

- Парола с Unicode, която надхвърля BCrypt byte limit при допустим брой chars.
- Unknown user срещу known user: еднакъв текст, но възможни timing различия.
- Session/CSRF token преди login се сменят; клиентът трябва да вземе нов CSRF token.
- Смяна на mode без DB reset оставя legacy {noop} записи.

Изберете поне един за нов regression test и обяснете кой security invariant защитава.

## 15. Въпроси за анализ

1. Защо salt не трябва да се пази тайно?
2. Защо два BCrypt hashes не се сравняват директно?
3. Защо @WithMockUser не доказва login?
4. Какво означава 204 от login?
5. Защо logout е POST с CSRF?
6. Как се мигрира plaintext store?

## 16. Очакван резултат

Работещ guided fix за **Authentication със Spring Security**, контролиран before/after evidence, root-cause анализ, test matrix и реализирана самостоятелна задача според acceptance criteria. Нормалният разрешен flow остава работещ. Отчетът разграничава доказаното с автоматизиран test, провереното ръчно и оставащите ограничения.

## 17. Checklist

- [ ] Vulnerability reproduced само в lab (за lab01 — unsafe config fixture анализиран).
- [ ] Root cause и trust assumption идентифицирани.
- [ ] Correct mitigation implemented server-side.
- [ ] Regression test показва red → green.
- [ ] Положителната функционалност остава работеща.
- [ ] Edge case има автоматизирана проверка.
- [ ] Самостоятелната задача покрива acceptance criteria.
- [ ] Evidence не съдържа secrets; reset процедурата е проверена.
