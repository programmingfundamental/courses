---
title: "Упражнение 4 — Sensors и обработка на физически данни"
sidebar:
  order: 4
  label: Упражнение 4
---

# Упражнение 4 — Sensors и обработка на физически данни

## 1. Инженерен проблем

Телефонът стои неподвижно, но accelerometer стойностите се променят. При увеличаване на sampling UI започва да се обновява непрекъснато, а след напускане на екрана listener продължава да получава данни. Трябва да превърнем шумен stream в полезна информация, без да задържаме hardware и памет без необходимост.

**Използваме:** lifecycle/state от Lab 1, repository и Room от Lab 2, cancellation подхода от Lab 3. **Предаваме нататък:** SensorSource/fake replay, filters, bounded pipeline и resource counters за Lab 5 и Lab 7.

## 2. Учебни цели

- Реализира lifecycle-owned accelerometer subscription.
- Разграничава requested и observed sampling frequency.
- Преобразува SensorEvent в immutable sample с правилни units/time.
- Реализира moving average или time-aware low-pass filter.
- Отделя acquisition rate от presentation rate.
- Проектира shake detector с обоснован false-positive control.
- Измерва samples, drops, active listeners и detection quality.

## 3. Предварителни знания

Предходните checkpoints, callbackFlow/cancellation, vector magnitude и основи на sampling. Филтърът трябва да може да се тества без Android runtime.

## 4. Необходими инструменти

Android Studio, Logcat, emulator virtual sensors и deterministic fake source. **Физически Android device е силно препоръчителен** за noise/rate наблюдение, но не е задължителен за оценяването. Accelerometer до 100 Hz в този курс не изисква runtime sensor permission; availability се проверява изрично. Не добавяйте high-sampling permission за тази задача.

## 5. Теоретична подготовка

Accelerometer измерва ускорение в device axes, включително гравитационния принос, в m/s². Нормата `sqrt(x²+y²+z²)` при покой обикновено е близо до земното ускорение, не до нула. Rotation променя axis components; raw coordinates не са автоматично world coordinates.

Sampling period е заявка, не договор за точна честота. Измервайте реалните intervals от SensorEvent.timestamp, който е monotonic nanosecond timestamp. Callback съдържанието може да бъде reused от framework; копирайте x/y/z и timestamp веднага. Проверявайте `getDefaultSensor(TYPE_ACCELEROMETER)` за null и освобождавайте listener при край на collection. Вижте [Sensors overview](https://developer.android.com/develop/sensors-and-location/sensors/sensors_overview).

```text
SensorManager -> SensorEventListener -> bounded samples -> filter/detector
                                                               |
                                  Room motion events <---------+
                                  StateFlow (<=10 Hz) <---------+
                                          |
                                      ViewModel -> Compose
```

Moving average с прозорец N изглажда, но въвежда latency и изисква bounded window. Low-pass filter може да използва `alpha = dt/(tau+dt)` и `filtered += alpha*(raw-filtered)`, където dt и tau са в секунди. Инициализирайте от първия валиден sample; при невалиден/отрицателен dt reject-нете sample. Filtered magnitude и magnitude на filtered axes не са непременно едно и също — дефинирайте избраното.

Raw collection, event detection, persistence и presentation имат различни rates. StateFlow представлява последно състояние и conflation е допустима за UI; не го използвайте като lossless event journal. Motion event първо се регистрира в domain/persistence path, а UI показва count/last event.
