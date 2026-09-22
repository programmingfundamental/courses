---
title: "Упражнение 2 — Architecture и Local Persistence"
sidebar:
  order: 2
  label: Упражнение 2
---

# Упражнение 2 — Architecture и Local Persistence

## 1. Инженерен проблем

След рестартиране Experiment Tracker изгубва приключилите експерименти. Добавянето на database calls директно към бутоните решава един случай, но създава разминаване между показаното и записаното, duplicate inserts при повторно действие и блокиране на интерфейса при по-голяма история.

**Използваме от Lab 1:** actions, ViewModel и разграничението draft/committed state. **Предаваме към Lab 3:** repository, Room schema, stable operation IDs и reactive source of truth. Room е инфраструктура на проекта, а не отделен database курс.

## 2. Учебни цели

- Разделя UI, business state и durable local facts.
- Реализира repository abstraction с измерима отговорност.
- Моделира Experiment/Measurement с referential integrity.
- Наблюдава Room чрез Flow и StateFlow без main-thread I/O.
- Използва transaction за логически атомарни промени.
- Реализира persistent filter/sort настройки чрез DataStore.
- Проверява duplicates, parent deletion и bounded history query.

## 3. Предварителни знания

Lab 1 checkpoint, suspend functions, Flow, basic relational key/foreign key понятия. Не се изисква сложен SQL или dependency injection framework.

## 4. Необходими инструменти

Android Studio, emulator, Database Inspector, Room testing, coroutine test dispatcher. Физически device не е необходим. Room/KSP и DataStore dependencies трябва да са synchronized преди часа.

## 5. Теоретична подготовка

```text
Compose -> actions -> ViewModel -> Repository -> Room
Compose <- UiState <- StateFlow <- Flow <-------+
                           ^
                     DataStore settings
```

Room е local source of truth за записаните records. ViewModel не пази втора authoritative mutable list, която трябва ръчно да се синхронизира. Repository валидира domain операции, управлява transactions и отделя storage от UI; не е просто преименуван DAO без отговорност.

Room поддържа suspend queries за еднократна работа и Flow за наблюдение. Наблюдаван query може да се преизпълнява при table invalidation, дори резултатът да не се е променил. Подбирайте bounded projection и при нужда `distinctUntilChanged()`. Вижте [Asynchronous Room queries](https://developer.android.com/training/data-storage/room/async-queries).

| State | Хранилище | Пример |
|---|---|---|
| Непотвърден text draft | saved UI state | бележка от Plan |
| Committed experiment и measurements | Room | история и timestamps |
| Filter/sort preference | DataStore | последно избрана подредба |
| Loading/error/selection validity | ViewModel | избраният запис вече липсва |

DataStore е за малки настройки, не за high-frequency measurement history. Използвайте една instance на файл за process и suspend update/edit. Room transaction и DataStore update **не са обща атомарна transaction**; след startup reconcile-вайте selection/settings с реалните records. Вижте [DataStore](https://developer.android.com/topic/libraries/architecture/datastore).

Инварианти: ID се създава веднъж за logical operation; Measurement принадлежи на съществуващ Experiment; timestamps са epoch ms; `endedAt == null` означава неприключил record, но не доказва жив hardware collector; `endedAt >= startedAt`; value е finite. След process restart неприключил experiment се показва като interrupted и иска explicit recovery action. Исторически timestamps не са clock за текущ elapsed timer.
