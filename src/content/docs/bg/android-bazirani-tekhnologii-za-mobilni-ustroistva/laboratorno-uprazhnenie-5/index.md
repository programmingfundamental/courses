---
title: "Упражнение 5 — Location и Context-Aware Applications"
sidebar:
  order: 5
  label: Упражнение 5
---

# Упражнение 5 — Location и Context-Aware Applications

## 1. Инженерен проблем

Телефонът е на бюрото, но location logger отчита „изминати“ стотици метри. След approximate permission приложението не показва нищо, а при background продължава да заявява updates. Получена координата не означава достатъчно точна, прясна или допустима за distance calculation позиция.

**Използваме от Lab 4:** source abstraction, fake traces, timestamps и lifecycle owner; Room/Flow идват от Lab 2. **Предаваме към Lab 6–7:** capability/permission state, filtering evidence и battery-conscious interval control.

## 2. Учебни цели

- Реализира foreground location logger с runtime permissions.
- Различава precise/approximate grant, denied и disabled provider.
- Валидира accuracy, age, order и duplicate samples.
- Изчислява distance върху обосновано филтриран route.
- Управлява updates според lifecycle и user intent.
- Аргументира privacy и battery trade-offs на interval/accuracy.
- Проверява degraded mode чрез emulator и fake traces.

## 3. Предварителни знания

Lab 2 и 4, Android permission workflow, distance между географски точки, monotonic timestamps. Не е необходим map SDK.

## 4. Необходими инструменти

Android Studio, emulator location controls/route playback, fake LocationSource и Logcat. **Физически device е силно препоръчителен** за реална accuracy/battery оценка. За задачата emulator/fake е достатъчен. Няма background location или foreground service; collection е само при видим STARTED screen.

## 5. Теоретична подготовка

Runtime permission е текуща възможност, не permanent Boolean. Request-вайте след user action, обработвайте denial без crash и проверявайте отново при връщане/преди protected API call. При нужда от precise поискайте FINE и COARSE заедно; потребителят може да даде само approximate. Grant на COARSE е валидно degraded състояние. Вижте [Location permissions](https://developer.android.com/develop/sensors-and-location/location/permissions).

Manifest declarations за това приложение:

```xml
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

Това е manifest, а UI остава Compose. Не добавяйте ACCESS_BACKGROUND_LOCATION. UI permission launcher е в presentation boundary; repository получава актуалната capability и превръща race-time SecurityException в PermissionRequired state.

Location има latitude/longitude, horizontal accuracy radius, wall timestamp и elapsed-realtime timestamp. Accuracy е оценка с несигурност, не гарантирана грешка. Age се изчислява от monotonic elapsed time в текущия boot; стар historical point не се проверява спрямо нов boot. `Location.distanceBetween`/`distanceTo` дават геодезично разстояние в метри; не изваждайте latitude/longitude като линейни координати. Вижте [Location API](https://developer.android.com/reference/android/location/Location).

```text
Permission + visible + user enabled
                  |
LocationManager -> LocationRepository -> bounded persistence -> Room
                            |
                         UiState -> Compose
```

Използваме platform LocationManager и `android.location.LocationRequest.Builder(intervalMs)`, с requestLocationUpdates(provider, request, executor, listener) и removeUpdates(listener). Изборът на provider проверява availability/enabled status и grant; не предполага, че GPS, network или fused provider винаги съществуват. Заявеният interval не е точен metronome. Вижте [LocationManager](https://developer.android.com/reference/android/location/LocationManager).

При approximate mode може да е неразумно да се изчислява пешеходно разстояние. Показвайте последна coarse позиция, accuracy и „недостатъчна точност за distance“, вместо 0 m като сигурна истина. Преминаване precise→approximate може да предизвика process restart; saved selection не замества permission recheck.
