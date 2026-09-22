---
title: "Упражнение 1 — Android Lifecycle, State и Jetpack Compose"
sidebar:
  order: 1
  label: Упражнение 1
---

# Упражнение 1 — Android Lifecycle, State и Jetpack Compose

## 1. Инженерен проблем

Изследовател стартира измерване, завърта телефона и вижда нулиран timer. След връщане от друг екран timer-ът понякога върви два пъти по-бързо. При възстановяване на process UI твърди „Running“, но задачата, която е управлявала експеримента, вече не съществува. Трябва да определим кой притежава state и какво действително се възстановява.

**Приемственост:** започваме Mobile Context Monitor като Experiment Tracker. ViewModel, user actions, clock abstraction и state taxonomy ще се използват в Lab 2; history още не е durable storage.

## 2. Учебни цели

- Анализира Activity и Compose lifecycle като различни lifetimes.
- Класифицира ephemeral UI, screen, shared business и persistent state.
- Реализира immutable UiState, state hoisting и unidirectional data flow.
- Използва remember, rememberSaveable, ViewModel и SavedStateHandle според lifetime.
- Диагностицира duplicate jobs/events при recomposition и navigation.
- Проверява state при rotation, Activity recreation и process recreation.
- Аргументира scope на втори функционален екран.

## 3. Предварителни знания

Kotlin, coroutines, basic Android Activity, Compose функции и navigation. Готов Compose проект от README, без business implementation.

## 4. Необходими инструменти

Android Studio, API 33/37 emulator, Logcat, adb, Compose testing. Физически телефон е по избор. Преди часа трябва да работят Gradle sync и Empty Activity.

## 5. Теоретична подготовка

Activity може да премине през create/start/resume/pause/stop/destroy. Composition има собствено влизане, recomposition и напускане; функцията може да се изпълни многократно, без да е нов screen. Не стартирайте business jobs в тялото на composable. Effect има identity/keys и cancellation, които трябва да са съзнателен избор. Вижте [Compose lifecycle](https://developer.android.com/develop/ui/compose/lifecycle).

| Данни | Owner/механизъм | Граница |
|---|---|---|
| expanded tooltip | remember | до напускане на composition |
| малък незавършен text draft | rememberSaveable или SavedStateHandle | restore чрез saved instance state |
| running status, counter, start instant | ViewModel | до изчистване на избрания VM scope |
| experiment ID и избран screen | saved state/navigation | малка reconstruction информация |
| история на измервания | Room от Lab 2 | durable local data |

ViewModel преживява configuration change, но не process death. SavedStateHandle и rememberSaveable възстановяват малки стойности при system recreation; не са база данни и не обещават restore след force-stop, clear data или премахване на task. Не поставяйте големи lists/bitmaps в Bundle. Вижте [Save UI states](https://developer.android.com/topic/libraries/architecture/saving-states).

```text
UI action -> ViewModel transition -> immutable StateFlow
    ^                                  |
    +----------- Compose state <-------+
```

State hoisting премества ownership до слоя, който решава промените. UI получава state и callbacks; не го дублира в независимо mutable копие. Събирайте StateFlow чрез `collectAsStateWithLifecycle()`. Спирането на UI collection не спира автоматично вече стартиран producer във viewModelScope. Вижте [State and Compose](https://developer.android.com/develop/ui/compose/state).

Timer не е `elapsed++` на всяка секунда. Изчислявайте elapsed от monotonic clock, например `SystemClock.elapsedRealtime()`, и accumulated duration. Tick job обновява представянето, а не определя истината. Rotation не създава нов start instant. След process recreation политиката в този lab е **Interrupted** с последния запазен snapshot, без автоматично възстановен job; durable recovery се добавя следващия час.
