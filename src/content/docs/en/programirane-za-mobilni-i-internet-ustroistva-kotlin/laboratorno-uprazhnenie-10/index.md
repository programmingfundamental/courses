---
title: Lab 10
sidebar:
  order: 10
---

# Lab 10

## Coroutines in Kotlin

A coroutine is a computation that can be suspended and resumed later. Coroutines make asynchronous work easier while allowing the code to remain sequential and readable.

Kotlin provides language support through the `suspend` modifier. Practical high-level APIs such as `launch`, `async`, `CoroutineScope`, and `Dispatchers` come from the `kotlinx.coroutines` library, rather than the standard library. To use them, the project must include the appropriate dependencies, with versions aligned with the project configuration.

## Coroutines and threads

A coroutine is not a thread. It runs on a thread determined by its context and dispatcher, and many coroutines can share a pool of threads. When a coroutine suspends through a nonblocking operation, the thread can perform other work. Resumption does not necessarily occur on the same thread: this depends on the context.

Coroutines do not automatically eliminate the need for synchronization when mutable data is shared.

## The `suspend` modifier

A function marked `suspend` can use operations that suspend execution and resume it later. It is called from another `suspend` function or from the body of a coroutine.

The modifier itself does not move work to a background thread or make blocking code nonblocking. If the function calls a blocking operation, it may block the current thread.

```kotlin
import kotlinx.coroutines.delay

suspend fun fetchData(): String {
    delay(1000L) // Simulates waiting without a network operation.
    return "Data"
}
```

## `delay()` and blocking waits

`delay(1000L)` suspends the coroutine for at least the specified time in milliseconds without blocking the thread, and supports cancellation. `Thread.sleep(1000L)` blocks the current thread. The task's UI loop uses `delay()` to keep the interface responsive.

## `Dispatchers`

| Dispatcher | Purpose |
| --- | --- |
| `Dispatchers.Main` | Work on the main UI thread; on Android, support is provided by `kotlinx-coroutines-android`. |
| `Dispatchers.IO` | Blocking input/output operations, such as reading from a file. |
| `Dispatchers.Default` | CPU-intensive work. |

`Dispatchers.Unconfined` has special resumption behavior and is usually unnecessary for the standard Android tasks in this lab.

## Coroutine scope and structured concurrency

`CoroutineScope` defines the lifetime of coroutines launched within it through its context (`CoroutineContext`) and `Job`. The context contains a dispatcher, a `Job`, and other elements. The scope itself does not define an execution schedule.

With structured concurrency, coroutines are associated with a parent job. Cancelling the parent `Job` propagates cancellation to child coroutines so that unnecessary work can stop. Cancellation is cooperative: the code must reach operations that check for it, such as `delay()`, or check `isActive`.

`GlobalScope` does not tie work to the lifecycle of a particular screen and is unsuitable as the default choice for these Android tasks. The dice task uses `LaunchedEffect`, which ties the work to the composition.

## Launching coroutines and obtaining results

- `launch` starts a coroutine and returns a `Job`, which can be used to wait for or cancel the work. It does not return a computed result.
- `async` starts a coroutine and returns `Deferred<T>`. Obtain the result through `await()`.
- `suspend` functions allow suspending operations to be written sequentially, without expressing each step through a separate callback function.

The example uses `fetchData()` from the previous section and a structured scope:

```kotlin
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

suspend fun loadMessage(): String = coroutineScope {
    val result = async { fetchData() }
    result.await()
}
```

For a single operation, a direct call to `fetchData()` is sufficient; here, `async` demonstrates how to obtain a result.

## Benefits and limitations

Coroutines enable readable asynchronous code and efficient waiting when operations are nonblocking. Correct behavior requires an appropriate dispatcher, a managed lifetime, and proper cancellation. Errors can be handled with `try`/`catch` at the appropriate point, but the cancellation signal must not be swallowed as an ordinary error.
