---
title: Tasks
sidebar:
  order: 100
  label: Tasks
taskPage: true
---
Create an application that displays the name of the last `Activity` callback invoked, logs lifecycle events to Logcat, and updates the text when the state changes.

## Task 1

Move the interface description from `onCreate()` into a separate composable function. Call `setContent` once in `onCreate()`. Create an `update(state: String)` method that changes the observable state; use it to update the text without calling `setContent` again.

## Task 2

Create a function with `@Composable` and a `state: String` parameter that contains:

- a `Text` element with the label "Last Activity callback:";
- a `Text` element that displays the value of `state`.

The callback name describes the last event, rather than a value of the `Lifecycle.State` enum.

## Task 3

Define the following properties in the `Activity` class. Replace the demonstration value of `TAG` with your student ID. Imports of `mutableStateOf`, `getValue`, and `setValue` from `androidx.compose.runtime` are required.

```kotlin
private val TAG = "123456"
private var lifecycleState by mutableStateOf("")
```

Pass the `lifecycleState` property to the composable function inside `setContent`.

## Task 4

Implement the `onCreate()`, `onStart()`, `onResume()`, `onPause()`, `onStop()`, `onRestart()`, and `onDestroy()` callbacks, retaining the required calls to `super`. For each event:

- use `update()` to set `lifecycleState` to the method name;
- log a debug message to Logcat using `Log.d(TAG, ...)`;
- display a short `Toast` message.

The shared state-update and messaging operations can be combined in `update()`. Verify event history in Logcat because the UI and `Toast` may not display every intermediate value.

## Task 5

Move the shared implementation of the callbacks and `update()` into a base class extending `ComponentActivity`. Have `MainActivity` extend it and set the Compose interface. Check that events are still logged exactly once.

## Task 6

As an alternative, move shared lifecycle event handling into a class implementing `DefaultLifecycleObserver` and register it through `lifecycle.addObserver(...)` in the `Activity`. Have the observer invoke a supplied handler function to update and log the state. The `Activity` is a `LifecycleOwner` and provides the observed lifecycle.

Avoid duplicate messages from the base class and the observer running simultaneously. Keep `onRestart()` in the `Activity` because `DefaultLifecycleObserver` has no corresponding method.

## Testing

1. Start the application. Track `onCreate()`, `onStart()`, and `onResume()` in Logcat. The screen will usually show the last event.
2. Move the application to the background using Home. Check `onPause()` and `onStop()`.
3. Bring the application back to the foreground. If the same instance has been retained, track `onRestart()`, `onStart()`, and `onResume()`.
4. Finish the current `Activity` with an explicit call to `finish()`, for example from a temporary button. Track `onPause()`, `onStop()`, and `onDestroy()`.

When the process is terminated, `onDestroy()` is not guaranteed to be called. Depending on the Android version, pressing Back in the entry `Activity` may move the task to the background; use `finish()` to test destruction.

## Independent tasks

Extend the lifecycle monitoring application with a small UI state experiment.

### Task 1. Two counters

Add two counters with their own increment buttons: one should use `remember`, and the other `rememberSaveable`. Display the names of the mechanisms used next to their values.

Increment both counters to 3 and trigger another UI change, such as showing and hiding help text, without removing the counters from the composition. Check whether the values are retained.

### Task 2. Recreating the Activity

Log the callbacks and an identifier for the `Activity` instance to Logcat. Compare moving to the background and returning with recreation when the device is rotated.

For the rotation experiment, confirm in Logcat that a new instance was actually created. Record the values of both counters before and after each scenario. On recreation, expect the `remember` counter to start at 0 and the `rememberSaveable` counter to restore its value.

### Task 3. Separating state from the view

Implement `CounterPanel(value: Int, onIncrement: () -> Unit, onReset: () -> Unit)`. The function should display the value and send actions through its callback parameters. The calling function should manage the state.

Use the panel for both counters. Add a Preview with a fixed value and empty callback functions.

### Verification and submission

Submit the code, a short Logcat excerpt, and a table with "scenario / old or new instance / values before and after". Explain the difference between recomposition and `Activity` recreation. Do not use `Toast` as the sole evidence of event order.
