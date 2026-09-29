---
title: Задачи
taskPage: true
sidebar:
  label: Задачи
  order: 100
---

## Самостоятелни задачи


### Задача 1 — Rotation на ключове

Разширете envelope до v1:keyId:Base64(nonce+ciphertext+tag) и поддържайте key ring от конфигурирани файлове. Новите записи използват currentKeyId; старите се четат с техния ID. Реализирайте service операция за миграция на една бележка, без публичен административен endpoint. Докажете old-read/new-write, повторно изпълнение и отказ при непознат key ID.

### Задача 2 — Решения за secrets

Направете таблица за password, access signing key, refresh token, private note и DB credential: нужно ли е възстановяване, механизъм, съхранение, rotation/revocation и тест. Реализирайте поне два теста. Включете restart със същия/различен ключ, променен owner/AAD и кирилица.
