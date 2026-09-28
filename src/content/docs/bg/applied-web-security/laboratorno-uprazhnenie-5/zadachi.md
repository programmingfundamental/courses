---
title: Задачи
taskPage: true
sidebar:
  label: Задачи
  order: 100
---

## Самостоятелни задачи

### Задача 1

**Условие:** намерете втория unsafe query в `vulnerable-app/src/main/resources/exercises/UnsafeLookup.java.txt`; той е starter за нов exact-title lookup module.

**Изисквания:** интегрирайте lookup като `GET /api/lookup?title=` и поправете query construction; резултатите трябва да са само за текущия user.

**Ограничения:** snippet-ът не е активен endpoint преди задачата; не копирайте готово решение от search; exact match не е LIKE; без destructive payloads.

**Критерии за приемане:** normal title, apostrophe, empty title, oversized value и boolean structured input са автоматизирано покрити. Един и същ title при Alice/Bob не нарушава isolation. Покажете red test върху unsafe starter и green след fix.

Предайте собствен code diff, test report и кратка аргументация. Не включвайте реални secrets или сурови session/token стойности в evidence.

### Задача 2 — Гранични случаи

- `%`/`_` са allowed wildcard data, но не сменят SQL structure.
- Apostrophe и кирилица в едно заглавие.
- Dynamic sort identifier не може да се bind-не като value.
- H2 compatibility mode не гарантира PostgreSQL semantics.

Изберете поне един за нов regression test и обяснете кой security invariant защитава.
