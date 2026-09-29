---
title: Lab 6
sidebar:
  order: 6
---

# Lab 6

## The `Activity` lifecycle

An `Activity` is an Android component that provides a window for the user interface. For example, an application may use `LoginActivity`, `SettingsActivity`, or `MainActivity`. A single `Activity` can also present more than one screen through Compose.

The system manages each instance's lifecycle and calls callback methods when it is created, shown, stopped, and destroyed.

### Main callback methods

| Method | When it is called | Typical role |
| --- | --- | --- |
| `onCreate()` | When the instance is created, including after recreation | Initialization and setting content through `setContent`. |
| `onStart()` | When entering the visible state | Preparing work required while the `Activity` is visible. |
| `onResume()` | When entering the active state for interaction | Resuming work related to the active interface. |
| `onPause()` | When leaving the active state; the `Activity` may still be visible | Brief operations to pause work that requires an active interface. |
| `onStop()` | When the `Activity` is no longer visible | Stopping work required only while the interface is visible. |
| `onRestart()` | After stopping, when the same instance is shown again | Followed by `onStart()`. |
| `onDestroy()` | When the instance is destroyed, for example after `finish()` or certain configuration changes | Cleaning up work associated with this instance when the callback is invoked. |

`onDestroy()` is not guaranteed when the process is terminated and is not a reliable point for saving critical data. [Activity lifecycle](https://developer.android.com/guide/components/activities/activity-lifecycle).

### Typical sequences

```text
Creation:                       onCreate() → onStart() → onResume()
Temporary interruption:         onPause() → onResume()
Moving to the background:       onPause() → onStop()
Returning to the same instance: onRestart() → onStart() → onResume()
Finishing with finish():        onPause() → onStop() → onDestroy()
```

During recreation, the old instance is destroyed, and the new one goes through `onCreate()`, `onStart()`, and `onResume()`. These sequences describe common scenarios; process termination may interrupt the calls.

### Composition lifecycle

The `Activity` lifecycle and the composition lifecycle are different. A composable function enters the composition, may participate in recompositions, and later leaves the composition. Recomposition does not mean that `Activity.onCreate()` is called again.

`remember` retains a value at its corresponding position in the composition. `LaunchedEffect` is a composition-related side-effect API: it starts a coroutine on entry, restarts it when its keys change, and cancels it on exit. It is not a general `Activity` lifecycle callback and is not automatically cancelled just because the activity moves to the background.

## A reactive approach in Jetpack Compose

In the declarative approach, the interface is described based on the current data. Compose tracks reads of observable state and schedules recomposition of affected parts when it changes. Recomposition runs functions to update the composition; it is not synonymous with every operation that draws on the screen.

### Imperative example

With a View component, properties are changed explicitly:

```kotlin
import android.view.View
import android.widget.TextView

fun showGreeting(textView: TextView) {
    textView.text = "Hello"
    textView.visibility = View.VISIBLE
}
```

### Declarative example

```kotlin
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun NameGreeting() {
    var name by remember { mutableStateOf("Ivan") }
    Text(text = "Hello, $name!")
}
```

If `name` is changed by a handler function, Compose schedules recomposition of the code that reads that value. There is no manual call to a method that changes a View's text.

### State, `mutableStateOf()`, and `remember`

State represents data that can change, such as a counter or entered text. An ordinary local variable is not automatically Compose state.

- `mutableStateOf()` creates observable state. Changing its `value` can trigger recomposition.
- `remember` retains the created value across recompositions while its position remains in the composition.
- `remember` does not preserve values when the `Activity` is recreated. For suitable UI values, such as `String` and `Int`, you can use `rememberSaveable`, which participates in the UI state saving and restoration mechanism.
- The `by` syntax requires imports of `getValue` and `setValue` from `androidx.compose.runtime`.

### A reactive counter

```kotlin
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
            Text("Increment")
        }
    }
}
```

The counter starts at 0. Pressing the button increments `count`, and the value read is used during the next recomposition. This does not imply that every change immediately draws a separate frame.

### Comparing the approaches

| Characteristic | Imperative approach with Views | Declarative approach with Compose |
| --- | --- | --- |
| UI changes | Explicitly changing View properties. | Changing observable state and updating the composition. |
| Description | A sequence of actions on existing objects. | A description of the interface for given input data. |
| Updates | Depend on the View components used and the methods called. | Compose schedules the required recomposition and subsequent phases. |

Stopping an `Activity` does not automatically remove the composition or stop every operation that uses its state. Lifecycle-dependent work is managed explicitly according to the relevant API.

### State as an `Activity` property

State can also be stored as an instance property. Place the following snippet inside the `Activity` class, with imports of `mutableStateOf`, `getValue`, and `setValue`:

```kotlin
private var lifecycleState by mutableStateOf("")
```

Here, `remember` is unnecessary because the property belongs to the `Activity` rather than being local to a composable function. When a callback changes `lifecycleState`, the interface that reads it can update. The value is not automatically preserved in a new `Activity` instance.

## `Toast`

A `Toast` is a brief message that disappears automatically and requires no user action.

```kotlin
import android.content.Context
import android.widget.Toast

fun showGreetingToast(context: Context) {
    Toast.makeText(context, "Hello!", Toast.LENGTH_SHORT).show()
}
```

The first argument is the context, the second is the text, and `Toast.LENGTH_SHORT` or `Toast.LENGTH_LONG` determines the duration. `show()` displays the message.

You can use `this` in an `Activity` callback. With `import android.widget.Toast`, an example snippet is:

```kotlin
Toast.makeText(this, "State: onStart", Toast.LENGTH_SHORT).show()
```

Events that occur in quick succession and background execution restrictions may prevent all messages from being visible. Use Logcat to track the exact sequence.

## Logcat

Logcat displays diagnostic messages from Android and applications: system events, developer messages, warnings, and exceptions. In Android Studio, start the application through **Run**, open **Logcat**, and select the relevant device and process.

### Message levels

| Method | Level | Purpose |
| --- | --- | --- |
| `Log.v()` | VERBOSE | Detailed diagnostic information. |
| `Log.d()` | DEBUG | Debugging messages. |
| `Log.i()` | INFO | Information about normal execution. |
| `Log.w()` | WARN | A warning about a possible problem. |
| `Log.e()` | ERROR | Error information. |
| `Log.wtf()` | ASSERT | A serious violation of an expected condition; behavior depends on system configuration. |

Place the example calls inside an application method; `import android.util.Log` is required:

```kotlin
val tag = "LifecycleDemo"
Log.v(tag, "Detailed information")
Log.d(tag, "Debug message")
Log.i(tag, "Normal execution")
Log.w(tag, "Warning")
Log.e(tag, "Error")
Log.wtf(tag, "Critical condition violated")
```

### Filtering

Messages are filtered by package, level, and tag. For example, `tag:LifecycleDemo` restricts results to messages with that tag. This distinguishes the lab's events from other system messages.

## Observing the lifecycle

`LifecycleOwner` provides a `Lifecycle` object; `ComponentActivity` implements this interface. `LifecycleOwner` is not a replacement for a base class. `LifecycleObserver` is an observer interface, while `DefaultLifecycleObserver` provides callbacks such as `onStart(owner)` and `onStop(owner)`. Register an observer through `lifecycle.addObserver(...)`.

`DefaultLifecycleObserver` has no `onRestart()`. If this particular call must be logged, keep it as a callback in the `Activity`.
