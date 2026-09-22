---
title: "Упражнение 6 — Backend for Frontend и mobile-specific aggregation"
sidebar:
  order: 6
  label: Упражнение 6
---

# Упражнение 6 — Backend for Frontend и mobile-specific aggregation

## 1. Инженерен сценарий
Home екранът прави последователно profile, activities и suggestions заявки през връзка със 150 ms RTT. UI чака множество round trips и получава излишни fields. Едно огромно агрегиращо API обаче може да забави всичко, когато optional service е бавен.

## 2. Учебни цели
- Проектира BFF screen-specific DTO.
- Разграничава aggregation от domain ownership.
- Измерва requests, bytes и latency.
- Реализира orchestration с ограничен deadline.
- Проектира partial response semantics.
- Сравнява serial и parallel fan-out trade-offs.

## 3. Предварителни знания
Lab 5 Gateway, REST Clients и common errors. Готовият BFF module и clients са настроени.

## 4. Необходими инструменти
Quarkus BFF, Android client/curl, controllable delay fixture, logs и response-byte counter. Не е необходим нов screen design.

## 5. Архитектурен контекст
```text
Android → Gateway → [Mobile BFF /mobile/home]
                              +→ User Service
                              +→ Activity Service
                              +→ optional Notification Service
```
Gateway само route-ва /api/mobile/ към BFF. BFF притежава Home projection; Activity правила остават в Activity Service.

## 6. Кратка теория
BFF събира нужните данни за определен client screen. Намалява mobile round trips, но не премахва backend calls и може да стане bottleneck. DTO трябва да има минимални нужни fields и explicit section status, а не копие на всички downstream payloads.

Critical и optional sections имат различна политика. Partial response се обозначава като partial/stale/unavailable; празен items не прикрива отказ. Parallel fan-out може да намали critical path, но увеличава concurrency и downstream load; serial calls натрупват latency. Всяка branch има timeout и общ budget; не чакайте optional резултат след deadline. [REST Client](https://quarkus.io/guides/rest-client/) и [Mutiny combining items](https://smallrye.io/smallrye-mutiny/latest/guides/combining-items/) са инструментите, а не целта.

Сравнявайте bytes за целия mobile screen workload, включително всички заявки. RTT simulation трябва да е еднаква before/after; локален тест без network delay не представя cellular автоматично. P95 от малък брой runs е rough observation, не SLA.
