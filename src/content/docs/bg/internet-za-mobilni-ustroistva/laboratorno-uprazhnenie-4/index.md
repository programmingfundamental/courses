---
title: "Упражнение 4 — Microservices Architecture и Service-to-Service Communication"
sidebar:
  order: 4
  label: Упражнение 4
---

# Упражнение 4 — Microservices Architecture и Service-to-Service Communication

## 1. Инженерен сценарий
Activity Service трябва да потвърди owner, който вече се поддържа от отделен User Service. Локален method call се превръща в remote dependency. Когато User Service е бавен, create заявката блокира mobile UI или грешно връща „потребителят не съществува“.

## 2. Учебни цели
- Аргументира service boundary и ownership.
- Реализира Quarkus REST Client с timeouts.
- Разграничава missing user от unavailable dependency.
- Проследява request ID през remote call.
- Проектира трета услуга с ограничен contract.
- Анализира distributed coupling и partial effects.

## 3. Предварителни знания
Lab 3 DTO/status contract, Java CDI, basic synchronous HTTP. Една Activity операция остава в Activity Service.

## 4. Необходими инструменти
Четирите готови Quarkus modules, Maven/IDE, Docker Compose, curl и REST client test doubles. Android използва същата Activity functionality.

## 5. Архитектурен контекст
```text
Mobile request → [Activity Service → REST Client → User Service]
                      |
                     H2
Самостоятелно: Notification Service → избран owner API
```
User Service притежава profile; Activity Service — activity lifecycle. Модулите са независими processes и не четат чужди tables. Shared build configuration не отменя service boundaries.

## 6. Кратка теория
Разделянето има цена: serialization, network latency, independent failure, contract compatibility и deployment. Cohesion и ownership са аргументи; броят repositories не е цел. Synchronous call дава по-проста консистентна проверка, но добавя temporal coupling.

REST Client interface дефинира downstream URL чрез configKey, не hardcoded container IP. Connect/read timeouts са задължителни. Downstream 404 може да значи unknown user; timeout/503 не доказват липса. Не записвайте Activity преди необходимата user validation, освен ако explicit workflow не допуска pending validation. [Quarkus REST Client](https://quarkus.io/guides/rest-client/) описва registration, response mapping и deadlines.

Correlation ID се предава към фиксирани internal services. Няма arbitrary URL от client input и няма shared database join между owners. Read replica/cache може да намали coupling, но въвежда stale data и invalidation; това се обсъжда, без да се строи нова платформа в часа.
