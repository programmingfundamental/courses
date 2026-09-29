---
title: Tasks
sidebar:
  order: 100
  label: Tasks
taskPage: true
---
## Review exercise: personal study planner

Develop a small application that combines Kotlin models, Compose, forms, lists, and coroutines. Use the material from Labs 1–10; you do not need to have implemented the catalog from Lab 11. Data may be kept only in memory for the current session.

## Independent tasks

### Task 1. Model and adding tasks

Create a `data class StudyTask` with a unique identifier, title, course, and completion flag. Prepare three sample entries.

Add a form for a new entry. The title and course must not be empty or contain only spaces. After successful validation, add exactly one entry and clear the form. On error, retain the input and display a specific message.

### Task 2. List, filters, and actions

Display tasks using a `LazyColumn` with stable keys. Add actions to mark a task as completed and delete it, along with "All", "Active", and "Completed" filters.

Display the counts of active and completed tasks across the entire data set, regardless of the filter. If there are no visible tasks, display appropriate text. Use shared observable state so that the list and counters update together.

### Task 3. Study session and demonstration

Allow the user to select an active task and start a short 5-second study session using a coroutine. Display the remaining time and provide "Stop". Only one session may run at a time; its completion displays a message, while marking the task as completed remains a separate user action.

Cancel the session when the selected task is deleted or completed. Work must also be cancelled when the screen is removed from the composition. Describe in a README how to run the application and how the code is split into model, UI, and logic.

### Verification and submission

Submit the project and a short demonstration: adding a valid task, rejecting an empty title, filtering, completing and deleting a task, completing a session, and cancelling a session while it is running. Also test an empty list. State which data is lost when the application starts again; persistent storage is not a task requirement.
