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

## 6. Мини експеримент / демонстрация — 10 минути

Поставете временни counters в plain local variable, remember и rememberSaveable. Предизвикайте unrelated state change, rotation и navigation away/back. Логвайте Activity instance ID, VM instance ID, composition enter/exit и active ticker count. Избягвайте log на всяка recomposition в окончателната версия.

Попълнете таблица: действие → кой instance се сменя → кои стойности се пазят. `ActivityScenario.recreate()` проверява Activity recreation, а не process death. Отбележете тази разлика преди реализацията.

## 7. Водена практическа задача — 35 минути

### Стъпка 1 — state и transitions

Experiment Tracker има name, counter, elapsed time, status, Start и Stop. Имената са 1..60 непразни символа след trim; counter е неотрицателен. Status е Idle, Running, Stopped или Interrupted.

```kotlin
enum class RunStatus { Idle, Running, Stopped, Interrupted }
data class TrackerUiState(
    val name: String = "",
    val counter: Int = 0,
    val elapsedMs: Long = 0,
    val status: RunStatus = RunStatus.Idle,
    val error: String? = null
)
interface MonotonicClock { fun nowMs(): Long }
```

Създайте ViewModel с private MutableStateFlow и публичен StateFlow. Actions са nameChanged, start, increment и stop. Повторен Start в Running е no-op; Stop в Stopped не променя вече финализирания elapsed. Transition и guard се извършват в един owner context. UI disabling е удобство, а не единствената защита от duplicate actions.

Start от Idle, Stopped или Interrupted започва нов run с counter=0 и elapsed=0, като запазва валидното име. Increment е разрешен само в Running. Така нов опит не се смесва с приключил или прекъснат експеримент.

### Стъпка 2 — clock и job ownership

Използвайте един Job за presentation ticker, обновяван най-много 5 пъти/s. Start създава най-много един job. Stop го cancel-ва и изчислява final duration от clock. В тест подменете clock, вместо да чакате wall-clock секунди. Не пазете Activity или View в ViewModel.

### Стъпка 3 — два екрана

Направете Tracker и read-only Summary, който показва същия experiment. VM е scoped до общ navigation graph; route предава identity, а не целия mutable state. Двата екрана виждат една истина. Pure screen composables приемат state/actions като parameters.

Фрагментът за lifecycle-aware binding се поставя в route composable с предоставен ViewModel:

```kotlin
val uiState by viewModel.uiState.collectAsStateWithLifecycle()
```

Използвайте remember за tooltip и rememberSaveable за малък локален draft само когато той не дублира business field във ViewModel. Опишете къде се commit-ва draft към business state.

### Стъпка 4 — recreation policy

Запазете минимален snapshot: name, counter, elapsed checkpoint и предишен status. При възстановен Running snapshot новият VM започва като Interrupted. При rotation запазеният VM продължава нормално. Не реконструирайте стар coroutine Job от Boolean.

Snapshot през saved state е best-effort UI restoration, не гарантиран запис на последния tick. Проверете process recreation след background/save: `adb shell am kill <package>` и връщане от Recents. Ако OS не убие този process, използвайте emulator test setup и запишете ограничението. `force-stop` не е еквивалентен test за system restore.

## 8. Checkpoint — 5 минути

- Tracker и Summary показват еднакви name/counter/status.
- 20 бързи Start натискания оставят activeTicker=1; Stop води до 0.
- Rotation запазва elapsed и counter; Summary navigation не създава втори VM.
- System restore не показва Running без реален owner на операцията.

## 9. Самостоятелна задача — 35 минути, в часа

Превърнете Summary във втори функционален екран **Experiment Plan**: target count, кратка бележка и режим за сравнение на current/target. Потребителят редактира draft, избира Apply или Discard и се връща към Tracker.

**Функционални изисквания:** target е 1..1000; note е до 200 символа; невалиден draft не променя активния план; Apply изпълнява една business промяна; навигация назад има explicit policy за непотвърден draft. Пазете draft при rotation и system restore.

**Технически ограничения:** без Room на този етап, без Activity reference, без глобална mutable singleton. Изберете screen-scoped ViewModel със saved draft или shared graph state със separate draft ownership и обосновете защо. Водената част не определя това решение.

**Приемане:** след rotation draft е същият; Discard не променя плана; double Apply не дублира ефект; back behavior е предвидим; възстановен несъвместим/невалиден draft се показва като editable error state. Предайте state ownership таблица и два tests за решението, без да копирате Tracker reducer механично.

## 10. Edge cases

| Случай | Очаквано поведение |
|---|---|
| Бързи Start/Stop/Start | един актуален ticker и валидни transitions |
| Rotation в Running | същият experiment и monotonic elapsed |
| Back след редакция | документиран Apply/Discard policy |
| Process recreation | Interrupted, без fictitious active job |
| Дублирано Apply | един business effect |
| Target=0 или празно име | validation state, без crash |
| Смяна на system clock | elapsed не скача |

## 11. Тестване — част от 15-минутния failure блок

Unit tests с fake clock: Start→advance 1500 ms→Stop; duplicate Start; Stop без Start; invalid name; draft validation. Compose test проверява state hoisting и disabled/available actions. Instrumented lifecycle test използва ActivityScenario.recreate и navigation back; process-kill сценарият е отделен ръчен/instrumented experiment. След Stop и излизане от VM scope проверете active jobs=0 и липса на retained Activity reference.

## 12. Наблюдение и измерване — 10 минути

Съберете counts за VM creations, ticker starts/stops и UI updates. Запишете expected/actual state за rotation, navigation и process restore. Не приравнявайте броя recompositions на timer updates или frame rate. Наблюдавайте 30 s Running и 10 s Stopped; ticker count след Stop трябва да е 0. Резултатите са в `results/lab01/observations.md`.

## 13. Въпроси за анализ

1. Защо recomposition не е lifecycle restart на business operation?
2. Кога rememberSaveable е недостатъчно за experiment history?
3. Как VM scope влияе на navigation away/back?
4. Защо clock-derived elapsed е по-надежден от броене на ticks?
5. Какво различава rotation от process death?
6. Кой е owner на непотвърдения Plan draft и защо?
7. Защо lifecycle-aware collection не доказва, че producer е спрял?

## 14. Очакван резултат

Работещ Experiment Tracker с два функционални Compose екрана след самостоятелната част, explicit state ownership, clock tests и описана recreation policy. Историята ще стане durable в Lab 2.

## 15. Критерии за приемане

- [ ] Има name, elapsed, counter, status и Start/Stop.
- [ ] State е immutable и се подава през ViewModel/StateFlow.
- [ ] Collection е lifecycle-aware; jobs имат един owner.
- [ ] Rotation/navigation не дублират operation.
- [ ] Process restore се различава от configuration change.
- [ ] Самостоятелният Plan има валидиране, Apply/Discard и restore.
- [ ] Има happy path, invalid state, lifecycle и resource tests.
- [ ] Ownership таблицата и наблюденията са предадени.
