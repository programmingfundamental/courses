---
title: Задачи
taskPage: true
sidebar:
  label: Задачи
  order: 100
---

## Самостоятелни задачи

### Задача 1

**Условие:** добавете пълен negative validation contract за expiration, issuer, audience и required scope, включително absent claims.

**Изисквания:** валидна signature, но wrong issuer/audience/expired/missing-exp/empty-sub → 401; валиден token без scope → 403. Тествайте expiry boundary с controlled Clock и разгледайте zero clock skew в baseline.

**Ограничения:** tests подписват локално с тестовия issuer; `.with(jwt())` не доказва signature validation; не ползвайте външен token service или чужди keys.

**Критерии за приемане:** всяка claim mutation има собствен test с ясен failure reason; положителният token работи; session cookie без Bearer не дава достъп. Аргументирайте replay policy и какво става след restart на издателя на токени.

Предайте собствен code diff, test report и кратка аргументация. Не включвайте реални secrets или сурови session/token стойности в evidence.

### Задача 2 — Гранични случаи

- Липсващ exp — не разчитайте само на timestamp validator.
- aud е списък, а не задължително един string.
- Token точно на expiry и различен clock skew.
- Valid signature, но missing scope; смяна на signing key при restart.

Изберете поне един за нов regression test и обяснете кой security invariant защитава.
