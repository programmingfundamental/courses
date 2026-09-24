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

Стартирайте lab04. В resources има simulation с точно 6 wrong-password requests към localhost и един valid login. Не увеличавайте броя и не добавяйте wordlist. Наблюдавайте status/correlation ID, без да логвате пароли.

### Стъпка 2

В уязвимия режим валидният login след грешките е 204. Изпълнете WebSecurityTest#lab04_limitIsWiredIntoLogin → red. Разграничете test за LoginGuard самостоятелно от test, който доказва wiring в authentication flow.

### Стъпка 3

Прочетете counter state и transitions. Определете MAX_ATTEMPTS, LOCK_DURATION, RESET_AFTER_SUCCESS, началото на block interval и поведението точно на boundary time. Отхвърлете нулев/отрицателен policy.

### Стъпка 4

Свържете failure/success/block checks във provider. Запазете generic response за known/unknown account; blocked request не удължава безкрайно срока. Audit записва outcome/correlation без credentials.

### Стъпка 5

Пуснете LoginGuardTest с mutable Clock вместо реално изчакване. Добавете integration test за success преди прага, който reset-ва counters, и за независим Bob account.

Разпределение: scenario/theory 15 мин, наблюдение 15, root cause 10, guided fix 25, tests 15, самостоятелна работа 25, защита 5. Общо 110 минути.

## 10. Анализ на root cause

Сървърът разглежда всеки login изолирано и не пази история на отказите. Сигурното password hashing увеличава цената, но не задава policy за броя online опити. Неправилно reset/expire logic може да направи limiter-а неефективен или да заключи account завинаги.

Предайте причинна верига: **недоверен вход → нарушено assumption → липсващ/грешен control → наблюдавано въздействие**. Оценете likelihood/impact по скалата в [threat model](/courses/bg/ueb-sigurnost/materiali/architecture/threat-model/); аргументирайте residual risk след fix.

## 11. Реализация на защита

Използвайте atomic update на per-account state, временна блокировка и expiry. Policy е configurable чрез MAX_ATTEMPTS, LOCK_DURATION и RESET_AFTER_SUCCESS. В предоставената реализация failed-attempt window се обновява при failure; блокираните requests се отказват преди нов failure. Map е bounded приблизително, но single-process counters не са защита за cluster. Анализирайте IP throttling като втори слой, доверен само на презаписания proxy address.

Reference branch в общия проект служи за guided comparison. За предаване променете уязвимия code path и запазете същия lab mode. Самостоятелната задача по-долу изисква собствено разширение и няма готово решение в този файл.

## 12. Security regression test

Изпълнете:

```powershell
mvn test '-Dlab.mode=lab04' '-Dtest=WebSecurityTest#lab04*'
```

Преди поправка очаквайте assertion failure, който показва дефекта. След поправката същата команда трябва да премине. Infrastructure error не е валиден red security test.

Примерен test fragment (пълният runnable class и imports са в `vulnerable-app/src/test/java/bg/tuvarna/lab`):

```java
for (int i = 0; i < 5; i++) {
    mvc.perform(post("/login").with(csrf()).param("username", "alice")
       .param("password", "wrong")).andExpect(status().isUnauthorized());
}
mvc.perform(post("/login").with(csrf()).param("username", "alice")
   .param("password", "Lab-alice-2026!")).andExpect(status().isUnauthorized());
```

Задължителна матрица:

- под прага → валиден login е приет;
- прагът е достигнат → следващ валиден login е отказан;
- blocked request → generic 401 и без изместване на срока;
- точно на expiry → разрешен нов опит;
- success reset true/false → съответните counters;
- concurrent failures → няма изгубени increments;

Добавете поне един нов автоматизиран test за edge case или самостоятелната задача. Повторете `mvn test` след fix и накрая `mvn verify -Psecurity-tests`. Проверявайте content/state/identity, когато са приложими, а не само HTTP status.

## 13. Самостоятелна задача

**Problem statement:** разширете configurable policy с тестове на две конфигурации и модел за abuse при NAT/cluster.

**Requirements:** MAX_ATTEMPTS=3, LOCK_DURATION=PT30S, RESET_AFTER_SUCCESS=false да работят през истинския authentication provider; добавете integration tests, не само unit tests на map.

**Constraints:** без sleep, без външен Redis, без доверяване на произволен X-Forwarded-For; не връщайте account existence.

**Acceptance criteria:** достигнат праг, expiration и success-no-reset са доказани с injected Clock; malformed configuration отказва startup; кратък анализ предлага втори слой срещу distributed guessing и описва lockout DoS.

Предайте собствен code diff, test report и кратка аргументация. Не включвайте реални secrets или сурови session/token стойности в evidence.

## 14. Edge cases

- Два едновременни failures около прага.
- Един IP представлява цяла зала зад NAT.
- Неограничен брой измислени usernames пълнят counters.
- Restart губи in-memory state; това е документирано ограничение.

Изберете поне един за нов regression test и обяснете кой security invariant защитава.

## 15. Въпроси за анализ

1. Защо account lockout създава DoS риск?
2. Защо IP-only не е достатъчно?
3. Защо не използваме sleep в тест?
4. Как се проверява wiring?
5. Какво се логва?
6. Какъв е cluster проблемът?

## 16. Очакван резултат

Работещ guided fix за **Brute-Force атаки и защита на Authentication**, контролиран before/after evidence, root-cause анализ, test matrix и реализирана самостоятелна задача според acceptance criteria. Нормалният разрешен flow остава работещ. Отчетът разграничава доказаното с автоматизиран test, провереното ръчно и оставащите ограничения.

## 17. Checklist

- [ ] Vulnerability reproduced само в lab (за lab01 — unsafe config fixture анализиран).
- [ ] Root cause и trust assumption идентифицирани.
- [ ] Correct mitigation implemented server-side.
- [ ] Regression test показва red → green.
- [ ] Положителната функционалност остава работеща.
- [ ] Edge case има автоматизирана проверка.
- [ ] Самостоятелната задача покрива acceptance criteria.
- [ ] Evidence не съдържа secrets; reset процедурата е проверена.
