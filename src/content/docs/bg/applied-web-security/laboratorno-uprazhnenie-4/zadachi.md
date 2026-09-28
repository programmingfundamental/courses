---
title: Задачи
taskPage: true
sidebar:
  label: Задачи
  order: 100
---

## Самостоятелни задачи

### Задача 1

**Условие:** разширете configurable policy с тестове на две конфигурации и модел за abuse при NAT/cluster.

**Изисквания:** MAX_ATTEMPTS=3, LOCK_DURATION=PT30S, RESET_AFTER_SUCCESS=false да работят през истинския authentication provider; добавете integration tests, не само unit tests на map.

**Ограничения:** без sleep, без външен Redis, без доверяване на произволен X-Forwarded-For; не връщайте account existence.

**Критерии за приемане:** достигнат праг, expiration и success-no-reset са доказани с injected Clock; malformed configuration отказва startup; кратък анализ предлага втори слой срещу distributed guessing и описва lockout DoS.

Предайте собствен code diff, test report и кратка аргументация. Не включвайте реални secrets или сурови session/token стойности в evidence.

### Задача 2 — Гранични случаи

- Два едновременни failures около прага.
- Един IP представлява цяла зала зад NAT.
- Неограничен брой измислени usernames пълнят counters.
- Restart губи in-memory state; това е документирано ограничение.

Изберете поне един за нов regression test и обяснете кой security invariant защитава.
