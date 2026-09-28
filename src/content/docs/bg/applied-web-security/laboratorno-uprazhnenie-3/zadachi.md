---
title: Задачи
taskPage: true
sidebar:
  label: Задачи
  order: 100
---

## Самостоятелни задачи

### Задача 1

**Условие:** добавете `PATCH /api/documents/{id}/title` за редактиране на заглавие.

**Изисквания:** USER редактира само свой документ; ADMIN — всеки; anonymous няма достъп. Валидното заглавие е 1–200 chars.

**Ограничения:** не допускайте прехвърляне на owner чрез body; използвайте CSRF за session API; запазете единна policy за read/write.

**Критерии за приемане:** матрица anonymous/owner/other/admin/missing и доказателство от DB, че отказаният PATCH не е променил нищо. Тестовете трябва да подават валиден CSRF при проверка на authorization, за да не се скрие дефект зад CSRF 403.

Предайте собствен code diff, test report и кратка аргументация. Не включвайте реални secrets или сурови session/token стойности в evidence.

### Задача 2 — Гранични случаи

- Отрицателен, прекалено голям или nonnumeric ID.
- Чужд и липсващ документ имат еднакъв видим отказ.
- Self-invocation може да заобиколи method-security proxy.
- Owner се променя между проверката и update.

Изберете поне един за нов regression test и обяснете кой security invariant защитава.
