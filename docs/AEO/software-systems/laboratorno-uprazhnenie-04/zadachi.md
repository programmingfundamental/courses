---
layout: default
title: Tasks
parent: Laboratory Exercise 4
grand_parent: Software Systems
nav_order: 6
---

# Tasks

Create a JavaFX application with an FXML interface for system login.

The application must contain:

- a username field
- a password field
- a “Login” button
- a text (Label) for messages to the user

Button logic

If any field is empty → display the message:

- "Please fill in all fields!"

If username == "admin" and password == "1234" → display the message:

- "Login successful!"

In any other case → display the message:

- "Invalid data!"

The message should be empty when the application initializes.

The “Login” button should be disabled while:

- the username or password field is empty.

Try to implement this using BooleanBinding.