---
title: "Упражнение 7 — Performance, Energy и Robustness"
sidebar:
  order: 7
  label: Упражнение 7
---

# Упражнение 7 — Performance, Energy и Robustness

## 1. Инженерен проблем

Mobile Context Monitor показва правилни стойности, но след няколко минути има повече allocations, database writes и UI updates, отколкото потребителят може да използва. Location и BLE могат да останат активни без реална нужда. „Работи“ не означава, че приложението използва разумно ресурсите или че оптимизацията запазва качеството на измерванията.

**Използваме:** всички pipeline-и от Lab 1–6 и техните fake traces/counters. **Краен резултат:** baseline/optimized сравнение, поне три самостоятелни оптимизации и техническа аргументация за trade-offs. Не се създава нов отделен проект.

## 2. Учебни цели

- Формулира проверима performance/resource хипотеза.
- Профилира CPU, memory, allocations и main-thread activity.
- Измерва DB operations, UI updates и acquisition/processing rates отделно.
- Реализира поне три оптимизации със запазени domain invariants.
- Разграничава energy measurement от software proxy metrics.
- Проверява lifecycle, permissions и overload след оптимизация.
- Аргументира before/after резултат с repeatability и ограничения.

## 3. Предварителни знания

Работещи sensors/location/BLE adapters или техните fakes, Room, WorkManager, Flow cancellation и basics на profiling. Преди часа regression suite от Lab 1–6 трябва да е наличен.

## 4. Необходими инструменти

Android Studio CPU/Memory Profiler, System Trace/Perfetto, Layout Inspector за Compose observations, Database Inspector и fake replay. **Физически device е силно препоръчителен**; за изводи за реална radio/energy цена е необходим. Emulator позволява software comparison, но неговата battery simulation не измерва реален разход. Запазете същия build variant/device/tooling за before/after.

## 5. Теоретична подготовка

```text
acquisition rate -> processing rate -> persistence rate -> presentation rate
      |                    |                 |                  |
  sensor/radio       CPU + queue         DB/I/O            Compose/UI
```

Тези rates не трябва да са равни. Sampling намалява downstream events, filtering променя signal, batching намалява transactions, а bounded buffers ограничават memory за сметка на explicit overflow behavior. По-малък DB operations count може да означава batching или изгубени данни — измерете и row count/quality.

Recomposition count не е frame count, а StateFlow emission не обещава отделна recomposition за всяка стойност. Slow collector може да пропуска междинни states по conflation. Измервайте emitted, observed и committed UI updates отделно, без metrics counter сам да предизвиква feedback loop. Profiling добавя overhead; allocation recording и debug Inspector се използват за диагностика, а timing comparison — при еднакъв profileable/release-like setup. Вижте [Android profiling](https://developer.android.com/studio/profile) и [System tracing](https://developer.android.com/topic/performance/tracing).

Energy е интеграл на power по време, не синоним на CPU %. Power Profiler/ODPM на поддържани устройства показва device-level rails, включващи и други процеси. За неподдържани устройства запишете N/A за energy и използвайте обозначени proxies: active sensor time, location requests, BLE scan duty time, DB transactions и background work. Не превръщайте 1% battery change от кратък run в прецизен app energy result. Вижте [Power Profiler](https://developer.android.com/studio/profile/power-profiler).
