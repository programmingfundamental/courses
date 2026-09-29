---
title: Задачи
taskPage: true
sidebar:
  label: Задачи
  order: 100
---

## Самостоятелни задачи


### Задача 1 — Оценка на регистрация и промяна на задача

Предайте минимум три наблюдения за /auth/register и PATCH /tasks/{id}/update с Finding/Status, Risk, Evidence, Root Cause, Mitigation и Regression Test. Разграничете потвърден дефект, защитен случай и непроверена хипотеза. Добавете REGISTRATION_RESULT и TASK_UPDATE_RESULT audit events и нов тест, че credentials/tokens/private note не влизат в логовете. Поне един regression test трябва да е извън началния TaskManagerBaselineTest.

### Задача 2 — Проверка на обхвата

Разгледайте грешен profile/base URL, пропуснат PostgreSQL тест, debug logging и промяна на cookie behavior зад HTTPS proxy. Автоматизирайте поне две проверки и запишете коя част изисква реален браузър. Отчетът трябва да позволява друг човек да повтори същите команди.
