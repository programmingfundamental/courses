---
title: Интернет за мобилни устройства
sidebar:
  order: 15
---

Практически курс за магистри по софтуерно инженерство с **10 лабораторни упражнения по 90 минути**. Общият проект **Mobile Activity Platform** свързва Android клиент с API Gateway, Mobile BFF и Quarkus услуги.

Изследваме мобилното приложение като Internet client при променлива свързаност, latency, metered networks, offline работа и distributed failures. Всяко занятие включва водена задача, **20 минути самостоятелна работа в часа**, failure scenarios и анализ.

## Лабораторни упражнения

| № | Тема | Инженерен фокус |
|---|---|---|
| 1 | [Mobile Connectivity](/courses/bg/internet-za-mobilni-ustroistva/laboratorno-uprazhnenie-1/) | Capabilities, validated Internet и network transitions |
| 2 | [Resilient Mobile API Client](/courses/bg/internet-za-mobilni-ustroistva/laboratorno-uprazhnenie-2/) | Failure model, cancellation и безопасен retry |
| 3 | [Quarkus Backend API](/courses/bg/internet-za-mobilni-ustroistva/laboratorno-uprazhnenie-3/) | DTO contracts, validation и bounded pagination |
| 4 | [Microservices Architecture](/courses/bg/internet-za-mobilni-ustroistva/laboratorno-uprazhnenie-4/) | Data ownership и remote dependencies |
| 5 | [API Gateway](/courses/bg/internet-za-mobilni-ustroistva/laboratorno-uprazhnenie-5/) | Единен вход, routing и cross-cutting policies |
| 6 | [Backend for Frontend](/courses/bg/internet-za-mobilni-ustroistva/laboratorno-uprazhnenie-6/) | Mobile round trips, payload и partial response |
| 7 | [Resilience и Fault Tolerance](/courses/bg/internet-za-mobilni-ustroistva/laboratorno-uprazhnenie-7/) | Time budgets, circuit breaker и retry amplification |
| 8 | [Offline-First и Synchronization](/courses/bg/internet-za-mobilni-ustroistva/laboratorno-uprazhnenie-8/) | Room queue, WorkManager и idempotency |
| 9 | [Realtime и Event-Driven Architecture](/courses/bg/internet-za-mobilni-ustroistva/laboratorno-uprazhnenie-9/) | Broker, WebSocket, duplicate/stale events |
| 10 | [Security и Observability](/courses/bg/internet-za-mobilni-ustroistva/laboratorno-uprazhnenie-10/) | JWT boundaries, tracing и end-to-end диагноза |

## Подготовка и общ проект

Необходими са Java/Kotlin, HTTP, concurrency и начален Android опит. Използват се Kotlin, Compose, ViewModel/Flow, Room, WorkManager, OkHttp, Quarkus, Docker Compose, Envoy, Redpanda и OpenTelemetry/Jaeger.

Emulator е достатъчен за занятията; физически телефон помага при cellular/VPN и реални network transitions. Starter проектите, software versions, ports и командите за стартиране са в `mobile-internet-course/README.md` в хранилището. Всички упражнения развиват един общ Android/Quarkus проект, с подготвена инфраструктура и отделни задачи за реализация.
