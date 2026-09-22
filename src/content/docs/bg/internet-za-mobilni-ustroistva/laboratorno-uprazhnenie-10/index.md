---
title: "Упражнение 10 — Security, Observability и End-to-End Analysis"
sidebar:
  order: 10
  label: Упражнение 10
---

# Упражнение 10 — Security, Observability и End-to-End Analysis

## 1. Инженерен сценарий
Home понякога се забавя или връща 503. Mobile log показва само total timeout; backend има няколко services и повторни опити. Междувременно недоверен потребител подменя userId в POST body. Трябва да защитим операцията и да диагностицираме къде се губи времето.

## 2. Учебни цели
- Разграничава authentication, authorization и ownership.
- Валидира JWT signature/issuer/audience/expiry.
- Проследява token само през trusted boundaries.
- Свързва request ID, trace/span и metrics.
- Диагностицира latency по critical path.
- Представя доказателствена end-to-end диагноза.

## 3. Предварителни знания
Checkpoints 1–9, BFF/REST Client chain и finite budgets. JWT verification dependencies, demo key tooling, OTel exporter и Collector/Jaeger са конфигурирани.

## 4. Необходими инструменти
Compose observability profile, Jaeger UI, structured logs, /q/metrics чрез internal development access, Node demo token tool и curl/Android. Не се настройва enterprise IdP в часа.

## 5. Архитектурен контекст
```text
Android + access token → [Gateway boundary]
 → [BFF auth + span] → [Activity auth + span] → [User auth + span]
                     OTel Collector → Jaeger
Structured logs + requestId/traceId; aggregate metrics
```
Реализирайте security върху REST create/read path. Gateway остава единственият public вход; service authorization не се заменя с routing. За основния 90-минутен вариант затворете fixture и WebSocket public routes; live stream се публикува отново само с authenticated per-user routing.

## 6. Кратка теория
OAuth2 е authorization framework; OIDC добавя identity; JWT е token format. Native mobile е public client без embedded secret: production login ползва system browser Authorization Code + PKCE. Тук token.mjs е само локален signed-credential fixture, не OIDC server. Не използвайте ID token вместо API access token.

API валидира signature, issuer, audience и expiry; role не доказва ownership. 401 означава липсваща/невалидна identity, а 403 — authenticated caller без разрешение. Activity owner се извлича от verified subject или се сверява с него, не се доверява на request userId. Propagation е само към фиксирани trusted services с правилна audience; production token exchange може да е нужен. [Quarkus JWT RBAC](https://quarkus.io/guides/security-jwt/) и [OAuth за native apps](https://www.rfc-editor.org/rfc/rfc8252) дават контекст.

Trace свързва spans; requestId подпомага logs; metrics дават distributions/rates без high-cardinality user/token labels. OTel REST Client propagation запазва parent context. Span duration не е само CPU; може да включва network/queue/retry. При parallel fan-out critical path, а не сборът от durations, обяснява wall time. [Quarkus OpenTelemetry](https://quarkus.io/guides/opentelemetry/) описва instrumentation.
