---
title: Задачи
taskPage: true
sidebar:
  label: Задачи
  order: 100
---

## Самостоятелни задачи

### Задача 1

**Условие:** подгответе crypto decision record за password, API secret, personal identifier, session identifier и database credential.

**Изисквания:** за всеки посочете нужда от възстановяване, избран механизъм, entropy/key source, storage boundary и rotation/revocation. Добавете test specification за всеки, а за два механизма — изпълними автоматизирани tests.

**Ограничения:** стандартни Java/Spring APIs; без custom algorithms, hardcoded production secrets или истински лични данни. API secret трябва да разгледа два случая: само verify и outbound use.

**Критерии за приемане:** решенията следват употребата на данните; посочени са backup/key loss и log risks. Не е достатъчна таблица, в която всичко се encrypt-ва.

Предайте собствен code diff, test report и кратка аргументация. Не включвайте реални secrets или сурови session/token стойности в evidence.

### Задача 2 — Гранични случаи

- Повторен nonce със същия key.
- Коректен envelope се копира към друг owner — AAD трябва да откаже.
- Restart със сменен key и запазена DB.
- Unknown envelope version, truncated tag, Unicode input.

Изберете поне един за нов regression test и обяснете кой security invariant защитава.
