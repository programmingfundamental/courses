---
title: Задачи
taskPage: true
sidebar:
  label: Задачи
  order: 100
---

## Самостоятелни задачи

### Задача 1

**Условие:** добавете `GET /api/account-summary`, който връща username и брой собствени документи.

**Изисквания:** anonymous се отказва; user identity идва само от SecurityContext; count е за текущия user.

**Ограничения:** не приемайте username от query parameter; не връщайте password/hash/roles от DB entity; не използвайте Basic auth вместо зададения session flow.

**Критерии за приемане:** автоматизирани anonymous, invalid-login, valid-login и cross-user isolation tests, включително Alice count=2 и Bob count=1; обяснение защо login и policy са различни отговорности.

Предайте собствен code diff, test report и кратка аргументация. Не включвайте реални secrets или сурови session/token стойности в evidence.

### Задача 2 — Гранични случаи

- Парола с Unicode, която надхвърля BCrypt byte limit при допустим брой chars.
- Unknown user срещу known user: еднакъв текст, но възможни timing различия.
- Session/CSRF token преди login се сменят; клиентът трябва да вземе нов CSRF token.
- Смяна на mode без DB reset оставя legacy {noop} записи.

Изберете поне един за нов regression test и обяснете кой security invariant защитава.
