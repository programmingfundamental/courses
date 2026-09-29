---
title: Tasks
sidebar:
  order: 100
  label: Tasks
taskPage: true
---
## Review exercise: asynchronous catalog loading

Apply what you learned about forms, state, lists, and coroutines in Labs 7, 9, and 10. Use a local loading simulation so that you can reproduce the same results.

## Independent tasks

### Task 1. Data source

Create a `data class CourseItem` with an identifier, a title, and a short description. Prepare six fictional courses.

Implement a `suspend` function that waits 1500 ms using `delay()` and returns the list. Add a scenario parameter: successful loading, an empty list, or a simulated error. Select the scenario through the UI before loading.

### Task 2. Screen states

Explicitly represent the "Initial", "Loading", "Data", "Empty result", and "Error" states, for example using a `sealed class`. When "Load" is pressed, start the work in a scope tied to the composition.

Show a loading indicator while waiting, a `LazyColumn` on success, an explanation for an empty result, and a message with "Try again" on error. Prevent restarting and changing the scenario while loading.

### Task 3. Cancellation and recovery

Add "Cancel" to cancel the current loading operation and return to the initial state. No late result should appear after cancellation. Cancellation must not be presented as an error.

Add a way to hide the entire catalog screen from the parent composition. Check that work is also cancelled when leaving the composition. If you handle exceptions broadly, rethrow `CancellationException`.

### Verification and submission

Submit the project and a table covering five scenarios: success, an empty result, an error followed by a successful retry, cancellation before 1500 ms, and hiding the screen during loading. Switch to the successful scenario for the retry. State which object or Compose mechanism manages the coroutine's lifetime.
