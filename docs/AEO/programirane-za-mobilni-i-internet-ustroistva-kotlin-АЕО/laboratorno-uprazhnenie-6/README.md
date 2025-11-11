---
layout: default
title: Lab 6
parent: Mobile and Internet Programming Kotlin
has_children: true
nav_order: 6
permalink: /docs/programirane-za-mobilni-i-internet-ustroistva-kotlin-аео/laboratorno-uprazhnenie-6
---


# Lab 6

## Activity Lifecycle

Activity is the main Android component and is a particular user interface screen. For example:

Login screen (LoginActivity)

Settings screen (SettingsActivity)

Main screen (MainActivity)

When an application is started, the system creates activity and goes through various states (lifecycle).

The lifecycle defines what is happening with given activity from the moment of its creation till its destruction.
Android automatically calls methods on each state change.

### Lifecycle's main methods

| Method        | Call                                                            | Description                                 |
| ------------- | --------------------------------------------------------------- | ------------------------------------------- |
| `onCreate()`  | when an activity is created                                     | Variables, layout and data initialization   |
| `onStart()`   | when an activity becomes visible                                | Prepares screen to be displayed             |
| `onResume()`  | when an activity starts accepting user input                    | Screen is active and on focus               |
| `onPause()`   | when an activity loses focus (e.g., another screen opened)      | Stops animations, stores temp data          |
| `onStop()`    | when an activity is not visible anymore                         | Stops redundant resources                   |
| `onDestroy()` | when an activity is destroyed                                   | Frees memory and resources                  |
| `onRestart()` | after `onStop()`, when an activity is back at the screen        | Prepares given activity for re-display      |

### Relation between methods

onCreate() → onStart() → onResume()
   ↓            ↑
onPause() ← onRestart()
   ↓
onStop()
   ↓
onDestroy()

### Jetpack Compose and Lifecycle

In Jetpack Compose, the UI is built declaratively using functions.
However, the Activity Lifecycle remains the same — the ComponentActivity (or MainActivity) controls when the UI is created and destroyed.

Compose can "react" to changes in the lifecycle using remember, LaunchedEffect, and other mechanisms.

## Reactive approach of Jetpack Compose

Reactive programming means that the UI automatically reacts to changes in the data. In other words, there is no need to tell the system what to do, but how the interface should look when there is data changes.


🧱 Example of imperative approach (old):
textView.text = "Hello"
textView.visibility = View.VISIBLE


Here there is explicit description to the UI element what to do — this is an imperative (stepwise) style.
If the data changes, the screen should be manually refreshed (for example, via notifyDataSetChanged()).

🌿Example of reactive approach (Compose):
var name by remember { mutableStateOf("Иван") }

Text(text = "Hello, $name!")


Here, Compose will automatically redraw the text if name changes — no need to call invalidate() or notifyDataSetChanged().

The UI is declaratively described and always presents the current state of the data.

### Action

Jetpack Compose uses a reactive state management system.
When a value (e.g. mutableStateOf) changes:

- Compose detects the change.

- Determines which Composable functions use that value.

- Only calls those functions again.

- Redraws the UI with the new data.

### Main terms

State

Represents data that can change over time (e.g., counter, text, list, etc.).

var count by remember { mutableStateOf(0) }

🔸 remember

Compose forgets everything when redrawing, so remember preserves values ​​between calls to Composable functions.

var count by remember { mutableStateOf(0) }


→ count value won't be lost if the UI is redrawn.

🔸 mutableStateOf

Creates an observable value. When it changes, Compose automatically responds and refreshes the screen.

count++

→ immediately updates UI.

Example: reactive counter
@Composable
fun CounterScreen() {
    var count by remember { mutableStateOf(0) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Counter: $count", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { count++ }) {
            Text("Increase")
        }
    }
}


Execution steps:

- count starts from 0;

- pressing button leads to count increment;

- Compose automatically detects the change and redraws only Text, which uses changed variable count.

### Approach comparison

| Feature          | Imperative (old UI)                     | Reactive (Compose)                           |
| ---------------- | --------------------------------------- | -------------------------------------------- |
| UI change        | Manually via findViewById and setText   | Automatically on data change                 |
| Control          | Developer decides what to happen        | Developer describes how should look like     |
| Perfomance       | Frequent full UI update                 | Updates only necessary parts                 |
| Code             | Lot of code, hard to maintain           | Short and readble                            |


### Reactivity and Lifecycle

Compose works in sync with Lifecycle — when an Activity or Fragment is stopped, Compose automatically stops monitoring state. This avoids memory leaks and unnecessary redraws.

| Term                 | Meaning                                                                 |
| -------------------- | ----------------------------------------------------------------------- |
| **Reactivity**       | UI automatically reacts to data changes                                 |
| **`mutableStateOf`** | Observable value that triggers an update                                |
| **`remember`**       | Preserves value between redraws                                         |
| **`Composable`**     | A function that describes how the UI should look in a given state       |


### mutableStateOf

mutableStateOf is part of Jetpack Compose and represents a reactive variable (observable state).
When its value changes, Compose automatically updates the UI that uses it.

Example:

var count by mutableStateOf(0)


Variable count could:

- be changed (mutable);

- when changed triggers screen redraw.

In the above Lifecycle example:

private var lifecycleState by mutableStateOf("")


— when the value of lifecycleState changes (for example from onStart to onPause), Compose automatically updates the text displayed on the screen.

🔸 This is part of Jetpack Compose's reactive approach - the UI always reflects the current state of the data..

## Toast

A toast is a short message that Android displays on the screen for a few seconds.
It does not interrupt the user and does not require interaction (it disappears automatically)..

Example:

Toast.makeText(context, "Hello!", Toast.LENGTH_SHORT).show()


- context – тthe current context (in Activity this can be used);

- the second parameter is the text to be displayed;

- Toast.LENGTH_SHORT or Toast.LENGTH_LONG – display duration;

- .show() – displays the Toast on the screen.

In this lab, Toast is used to indicate which lifecycle method was called, for example:

Toast.makeText(this, "Състояние: onStart", Toast.LENGTH_SHORT).show()

## Logcat

Logcat (short for Log + Concatenate) is a system for collecting and displaying logs (messages) from the Android operating system and developer's applications.

Shows in real time:

- system event (e.g., activity start),

- developers messages (Log.d, Log.e, etc.),

- errors, warnings and exceptions,

- other processes messages (if allowed).

In other words, Logcat is Android's "console" where it can be seen everything that's happening behind the scenes.

In Android Studio:

- Start an application (Run ▶️)

- Open tab Logcat

- There will be real-time logs as app is running.

### Log levels

| Method      | Level        | Meaning                      | When to be used                                     |
| ----------- | ------------ | ---------------------------- | --------------------------------------------------- |
| `Log.v()`   | VERBOSE      | Most detailed info           | For detailed debugging                              |
| `Log.d()`   | DEBUG        | Diagnostic info              | In time of development                              |
| `Log.i()`   | INFO         | Common info                  | To assert successful execution of a function        |
| `Log.w()`   | WARN         | Warning                      | Potential problem yet not an error                  |
| `Log.e()`   | ERROR        | Error                        | Error                                               |
| `Log.wtf()` | ASSERT / WTF | „What a Terrible Failure“ 😅 | Critical error that should not be happening         |

### Example for all levels:

val TAG = "LifecycleDemo"

Log.v(TAG, "Verbose: detailed info")
Log.d(TAG, "Debug: debug messages")
Log.i(TAG, "Info: standard info")
Log.w(TAG, "Warning: warning")
Log.e(TAG, "Error: error!")
Log.wtf(TAG, "WTF: fatal error!")

### Filtering in Logcat

Logcat displays all messages from the Android system and all applications.
To avoid being overwhelmed by information, logs could be filterd by:

App name / package name – e.g. com.example.lifecycledemo

Log level – only Debug, Error, etc.

Tag – e.g. TAG = "LifecycleDemo"

In Android Studio, filter could be selected from the Log Level drop-down menu or manually to type tag:LifecycleDemo.

## LifecycleObserver

LifecycleObserver is an interface from the Android Jetpack library (androidx.lifecycle) that allows a class to observe Lifecycle events (e.g. onStart, onStop, onResume, onPause) of LifecycleOwner objects — these are usually Activities.

