---
title: "Упражнение 9 — Realtime Communication и Event-Driven Architecture"
sidebar:
  order: 9
  label: Упражнение 9
---

# Упражнение 9 — Realtime Communication и Event-Driven Architecture

## 1. Инженерен сценарий
Activity status се променя, докато mobile screen е отворен. Polling на всяка секунда харчи заявки и все пак показва стар state. WebSocket подобрява latency, но след reconnect пристигат duplicate и out-of-order events; потребителят вижда DONE→ACTIVE rollback.

## 2. Учебни цели
- Разграничава command от event.
- Реализира broker→notification→WebSocket pipeline.
- Управлява foreground connection lifecycle.
- Проектира bounded reconnect и reconciliation.
- Обработва duplicate/stale events по aggregate.
- Сравнява polling, WebSocket и push trade-offs.

## 3. Предварителни знания
Lab8 durable Activity/version и Room; Lab2 cancellation; basic JSON. Broker/consumer/relay boilerplate и Android socket adapter са готови.

## 4. Необходими инструменти
Compose events profile с Redpanda, Quarkus Messaging и WebSockets Next; OkHttp WebSocket; event fixture JSON; Logcat и broker logs. Physical device не е необходим.

## 5. Архитектурен контекст
```text
[Activity status command → commit → EventPublisher]
 → Redpanda → Notification EventConsumer → BFF internal relay
 → Gateway WebSocket → Android RealtimeSource → Room → UI
```
Activity Service е source of truth. WebSocket е нисколатентен transport, а не durable truth; snapshot API остава нужен след gap.

## 6. Кратка теория
Command заявява промяна, event описва факт след commit. Envelope има eventId, activityId, userId, version, status, occurredAt. Version се сравнява за един Activity; timestamp сам по себе си не дава total order. Broker delivery е поне потенциално duplicate; Kafka transport не прави business processing exactly-once.

Activity commit и broker publish са различни effects. В лабораторията показваме gap; production решение обикновено е transactional outbox. Notification→BFF relay също може да се повтори. WebSocket няма replay guarantee след disconnect; snapshot reconciliation възстановява current state. [Quarkus Kafka](https://quarkus.io/guides/kafka-getting-started/) и [WebSockets Next](https://quarkus.io/guides/websockets-next-reference/) са използваните APIs.

Foreground-only socket се отваря при STARTED screen и user intent. Heartbeat/ping има deadline; backoff с jitter и maximum attempts предпазва battery/backend. STOP отменя socket/retry. WorkManager не е host за постоянен socket. Push notification е подходяща background wake-up подсказка, не гаранция за точно еднократно прилагане на state. Polling остава възможна fallback стратегия с различна цена.
