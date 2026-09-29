---
title: Задачи
taskPage: true
sidebar:
  label: Задачи
  order: 100
---

## Самостоятелни задачи


### Задача 1 — HTML форма за нова задача

Добавете GET /ui/tasks/new с полета summary, description, deadline и hidden CSRF. POST /ui/tasks приема form data и делегира към същия TaskService.create. Добавете setters към TaskRequestDto за @ModelAttribute binding или отделен form DTO със същата валидация. Не копирайте repository логика. Успех=201; липсващ/чужд token=403 без INSERT. Owner е текущият потребител.

### Задача 2 — Token и cookie граници

Проверете стар token след login, стар session cookie след logout, same-site/cross-origin form и Secure cookie през HTTPS. Изпълнете ръчни Postman заявки за token/session, а действителното изпращане на cookie проверете с браузър. Опишете защо 403 за POST без token не доказва правилна authentication policy.
