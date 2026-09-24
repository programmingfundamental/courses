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

Стартирайте lab09. Вземете session и CSRF, POST /token, после GET /token-api/documents с Authorization: Bearer. Очаквайте subject=alice. Не качвайте token в онлайн decoder.

### Стъпка 2

В resources има локален PowerShell decoder/manipulator. Променете само sub към admin, запазете старата signature и подайте токена само към localhost. Уязвимият decoder приема променената identity.

### Стъпка 3

Прочетете Tokens и tokenChain. Отделете parse, signature verification, claim validation и authorization като четири решения. Изпълнете JwtSecurityTest#modifiedPayloadAndForeignSignature → red.

### Стъпка 4

Реализирайте verification с NimbusJwtDecoder и фиксиран trusted RSA key/RS256 и в lab09 branch. Не създавайте собствен signature алгоритъм и не приемайте jku/x5u от токена.

### Стъпка 5

За guided fix добавете modified-payload и чужд signing key tests. Самостоятелно завършете claim/scope policy и допълнете tests за липсващ exp и непразен sub; baseline validators са справка, а новите negative tests трябва да конструират реално подписани tokens.

Разпределение: scenario/theory 15 мин, наблюдение 15, root cause 10, guided fix 25, tests 15, самостоятелна работа 25, защита 5. Общо 110 минути.

## 10. Анализ на root cause

Приложението третира decoding като доказателство за authenticity. Base64url е обратимо без secret; attacker контролира payload bytes. Дори валидният подпис не означава, че token е предназначен за този API, не е изтекъл или има необходимите permissions.

Предайте причинна верига: **недоверен вход → нарушено assumption → липсващ/грешен control → наблюдавано въздействие**. Оценете likelihood/impact по скалата в [threat model](/courses/bg/ueb-sigurnost/materiali/architecture/threat-model/); аргументирайте residual risk след fix.

## 11. Реализация на защита

Използвайте стандартния resource server и Nimbus verifier с доверен public key, фиксиран algorithm и fail-closed behavior. Разделете session chain от stateless Bearer chain. Guided целта е криптографското отхвърляне на modified bytes; application policy за issuer/audience/time/scope се защитава отделно. Не log-вайте raw Authorization header.

Reference branch в общия проект служи за guided comparison. За предаване променете уязвимия code path и запазете същия lab mode. Самостоятелната задача по-долу изисква собствено разширение и няма готово решение в този файл.

## 12. Security regression test

Изпълнете:

```powershell
mvn test '-Dlab.mode=lab09' '-Dtest=JwtSecurityTest'
```

Преди поправка очаквайте assertion failure, който показва дефекта. След поправката същата команда трябва да премине. Infrastructure error не е валиден red security test.

Примерен test fragment (пълният runnable class и imports са в `vulnerable-app/src/test/java/bg/tuvarna/lab`):

```java
mvc.perform(get("/token-api/documents")
    .header("Authorization", "Bearer " + modifiedToken))
    .andExpect(status().isUnauthorized());
mvc.perform(get("/token-api/documents")
    .header("Authorization", "Bearer " + validToken))
    .andExpect(status().isOk());
```

Задължителна матрица:

- valid signed token → 200 и правилен subject;
- expired token → 401;
- modified payload / чужда signature → 401;
- wrong issuer / audience → 401;
- missing required scope → 403;
- malformed / missing token → 401;

Добавете поне един нов автоматизиран test за edge case или самостоятелната задача. Повторете `mvn test` след fix и накрая `mvn verify -Psecurity-tests`. Проверявайте content/state/identity, когато са приложими, а не само HTTP status.

## 13. Самостоятелна задача

**Problem statement:** добавете пълен negative validation contract за expiration, issuer, audience и required scope, включително absent claims.

**Requirements:** валидна signature, но wrong issuer/audience/expired/missing-exp/empty-sub → 401; валиден token без scope → 403. Тествайте expiry boundary с controlled Clock и разгледайте zero clock skew в baseline.

**Constraints:** tests подписват локално с тестовия issuer; `.with(jwt())` не доказва signature validation; не ползвайте външен token service или чужди keys.

**Acceptance criteria:** всяка claim mutation има собствен test с ясен failure reason; положителният token работи; session cookie без Bearer не дава достъп. Аргументирайте replay policy и какво става след restart на учебния issuer.

Предайте собствен code diff, test report и кратка аргументация. Не включвайте реални secrets или сурови session/token стойности в evidence.

## 14. Edge cases

- Липсващ exp — не разчитайте само на timestamp validator.
- aud е списък, а не задължително един string.
- Token точно на expiry и различен clock skew.
- Valid signature, но missing scope; смяна на signing key при restart.

Изберете поне един за нов regression test и обяснете кой security invariant защитава.

## 15. Въпроси за анализ

1. Защо decode не е verify?
2. Какво гарантира signature?
3. Защо aud се проверява?
4. Защо @WithMockJwt не стига?
5. Защо missing scope е 403?
6. Може ли signed token да се replay-не?

## 16. Очакван резултат

Работещ guided fix за **JWT Security и Token Manipulation**, контролиран before/after evidence, root-cause анализ, test matrix и реализирана самостоятелна задача според acceptance criteria. Нормалният разрешен flow остава работещ. Отчетът разграничава доказаното с автоматизиран test, провереното ръчно и оставащите ограничения.

## 17. Checklist

- [ ] Vulnerability reproduced само в lab (за lab01 — unsafe config fixture анализиран).
- [ ] Root cause и trust assumption идентифицирани.
- [ ] Correct mitigation implemented server-side.
- [ ] Regression test показва red → green.
- [ ] Положителната функционалност остава работеща.
- [ ] Edge case има автоматизирана проверка.
- [ ] Самостоятелната задача покрива acceptance criteria.
- [ ] Evidence не съдържа secrets; reset процедурата е проверена.
