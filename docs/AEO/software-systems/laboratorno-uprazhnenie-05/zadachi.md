---
layout: default
title: Tasks
parent: Laboratory Exercise 5
grand_parent: Software Systems
nav_order: 2
---

# Independent Tasks

### 1. Task 1: Specialized Course Selection

Create an application that allows a student to select a discipline from a `ListView`.

- Upon selecting a discipline from the list, a `ChoiceDialog` should open, asking the user for the "Enrollment Type" (e.g., Full-time, Part-time, Distance).

- The result of both selections should be displayed in an `Alert` message.

- Add a `Tooltip` to the list explaining how to make a selection.

### 2. Task 2: Text Editor with Context Menu

Develop a window with one large `TextArea`.

- Add a `ContextMenu` to the `TextArea`.

- The menu should contain the options: "Clear All" (clears the text), "Uppercase" (converts the entire text to uppercase), and "Change Color" (opens a `ColorPicker` in a dialog window to change the font color).

- Add a `Tooltip` to the `TextArea` with the label "Type your notes here".

### 3. Task 3: User Login Dialog

Create an application that, upon startup, does not show the main window immediately but instead first opens a `TextInputDialog`.

- The user must enter their name in the dialog.

- Once the user enters their name, a `ChoiceDialog` for selecting a role (Administrator, Moderator, User) should open immediately.

- If the user presses "OK", the main window opens and displays a label (`Label`) with the text: "Welcome, [Name]! You logged in as [Role].".

- If the user presses "Cancel" or closes the dialog, the application must close (`Platform.exit()`).
