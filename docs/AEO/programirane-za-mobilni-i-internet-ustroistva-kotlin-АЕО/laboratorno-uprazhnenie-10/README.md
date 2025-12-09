---
layout: default
title: Lab 10
parent: Mobile and Internet Programming Kotlin
has_children: true
nav_order: 10
permalink: /docs/programirane-za-mobilni-i-internet-ustroistva-kotlin-аео/laboratorno-uprazhnenie-10
---

# Lab 10

Kotlin Coroutines are a tool for asynchronous programming that makes it easy to manage tasks running in different threads without the complexity of classic mechanisms like threads and callbacks. Coroutines are integrated into Kotlin as part of the standard library and provide a convenient way to work with asynchronous tasks.

Main features:

1. Coroutines do not create new threads. Instead, they run in existing threads and can be "suspended" and "resumed" without blocking the current thread. This makes them much more efficient than classic threads.

2. Coroutines use a special keyword method – suspend. This keyword indicates that the function can be temporarily “suspended” and resumed later, thus freeing up the thread in which it is executing. This eliminates blocking behavior.

3. Dispatchers control which thread a Coroutine will run on:

- Dispatchers.Main – for operations that require access to the main (UI) thread.
- Dispatchers.IO – for input/output tasks, such as file operations or network requests.
- Dispatchers.Default – for heavy computing tasks.
- Dispatchers.Unconfined – allows a Coroutine to run on any thread.

4. Scope is the context in which a Coroutine lives. Scope determines when a Coroutine starts and ends.
- GlobalScope: for coroutines with global scope.
- CoroutineScope: for local coroutines limited to a specific lifecycle.

5. Action

    - Execution:
        When a Coroutine is started, it does not create a new thread, but registers with a scheduler that determines when and where to execute.
        For example, if Dispatchers.IO is used, the Coroutine will be placed in a pool of threads that are optimized for I/O operations.

    - Suspend and resume:
        The suspend functions allow the Coroutine to "pause" at a given point, freeing up the current thread. Once the task is finished, the                  Coroutine resumes from the same point, without losing its state.

    - Thread management mechanism:
        When launch or async are used, the corresponding Coroutine registers with a specific Dispatcher and Scope. This ensures that tasks will be            properly managed and synchronized.

6. Key components
    - Builders (Constructors):

       launch: Starts a new Coroutine to perform a task without a result (fire-and-forget).
       async: Starts a new Coroutine that returns a result via Deferred (similar to Future in Java).
       Suspend functions: They are the foundation of Coroutines. The Suspend function can only be called from another Coroutine or another suspend             function.

        ```kotlin
        suspend fun fetchData(): String {
            delay(1000) // long operation simulation
            return "Data"
        }
        ```

        Coroutine Scope: Defines the Coroutine lifecycle. When a Scope is terminated, all Coroutines within it are also terminated.

        Context: A context is an environment that includes Dispatcher, Job, and other settings. Each Coroutine runs in a specific context.

7. Advantages of Kotlin Coroutines
    - More readable and maintainable code.
    - Easy handling of asynchrony without the complexity of callbacks.
    - More efficient use of resources (no thread blocking).
    - Easy error handling through built-in mechanisms like try-catch.
    
8. Disadvantages
    - Requires a good understanding of the concepts (especially for beginners).
    - Possible errors due to incorrect management of contexts and Scope.
  


### Task

Create an application that randomly displays an image of a dice with a value at a time specified by the user.

- Download the dice images from: https://tuvarnabg.sharepoint.com/:u:/s/msteams_230e9b/EXtfPyFQ_3tAnBwEYE7-4XgB3w6hd6boqpZEw_RJEj-sgg?e=HW2dfN

- Add the images to your project – res/drawable

- Add string resources for each dice

- Create a Dice class with attributes:
    @StringRes val stringResourceId: Int,
    @DrawableRes val drawableResourceId: Int

- Add a time input field

- Add a button to start an asynchronous process that selects a random object from the dice list at a specified time from the input field and visualizes it.



