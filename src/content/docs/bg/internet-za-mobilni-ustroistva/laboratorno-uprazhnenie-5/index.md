---
title: "Упражнение 5 — API Gateway и единна входна точка"
sidebar:
  order: 5
  label: Упражнение 5
---

# Упражнение 5 — API Gateway и единна входна точка

## 1. Инженерен сценарий
Android build съдържа три service адреса. При backend deployment един от тях се променя и старите приложения спират да работят. Logs имат несвързани IDs, а mobile request към непознат path получава случайна HTML грешка.

## 2. Учебни цели
- Реализира routing през един public endpoint.
- Разграничава Gateway от business service/BFF.
- Проследява request ID и timing.
- Проверява path rewriting и query preservation.
- Проектира една cross-cutting error policy.
- Анализира authentication и rate-limit boundaries.

## 3. Предварителни знания
Lab 4 работещи services; HTTP path/header semantics. Envoy bootstrapping, clusters и Compose DNS са дадени.

## 4. Необходими инструменти
Docker Compose, Envoy, curl, Android client и gateway JSON logs. Не се пише нов proxy от socket primitives.

## 5. Архитектурен контекст
```text
Android → [Envoy Gateway]
              +→ User Service
              +→ Activity Service
              +→ BFF fixture
Internal service ports не са публикувани.
```
Gateway route table се редактира в infra/envoy.yaml. Mobile base URL остава един. Не се добавят joins или business DTO transformations в Gateway.

## 6. Кратка теория
Gateway е стабилен ingress с routing и cross-cutting policies: request metadata, timing, authentication enforcement, ограничаване на натоварване. Business aggregation за конкретен UI принадлежи на BFF. Request ID корелира операции, но не е authorization token и не е идентичен с trace ID.

Prefix matching изисква boundary: /api/users и /api/users/… са допустими, /api/users-evil не е User route. Query string и HTTP method трябва да се запазят. Не route-вайте произволен upstream host от input. Deadline на Gateway трябва да е съгласуван с downstream и mobile budget. [Envoy HTTP routing](https://www.envoyproxy.io/docs/envoy/latest/configuration/http/http_conn_man/route_matching) описва matching.

Starter генерира ingress request ID, презаписва недоверен външен ID и логва route/status/duration/bytes. Не логва Authorization или request bodies. Rate limit е admission policy; в Lab 5 го обсъждаме концептуално — той не замества domain quota. Routing само по себе си не е authentication; Lab 10 добавя проверките.
