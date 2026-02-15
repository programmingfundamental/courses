---
layout: default
title: Laboratory Exercise 5
parent: Software Systems
has_children: true
nav_order: 5
#permalink: /docs/software-systems/laboratorno-uprazhnenie-5
---

# Exercise #5

Controls are graphical elements with which the user interacts. All of them inherit the `Control` class and possess characteristics such as styling, focus, and states.

**1. List Controls and Selectors**

These are used for selecting values from a predefined set.

- **`ComboBox` (Drop-down list):** A compact element that expands when clicked.
  - _Properties:_ items (ObservableList with values), editable (allows the user to type their own value).

  - _Usage:_ Choosing a city, country, or category.

  - _Attributes:_
    - `promptText` (text shown in the field when no value is selected).
    - `value` (the current selection value).
    - `editable`: Boolean value (`true`/`false`). Allows the user to enter text that is not in the list.

  - _Example:_
    ```xml
    <ComboBox fx:id="priorityCombo" promptText="Priority" editable="false" />
    ```

- **`ListView` (List View):** Visualizes a list where several items are visible simultaneously.
  - _Properties:_ selectionMode (`SINGLE` or `MULTIPLE`).

  - _Usage:_ Message list, files in a folder.

  - _Attributes:_
    - `orientation`: Defines the direction of the list (`HORIZONTAL` or `VERTICAL`). Defaults to vertical.

    - `prefHeight` / `prefWidth`: Frequently used to limit the size of the visible area.

  - _Configuration in Java (Controller):_ Unlike others, the selection mode is often configured here:

    ```java
    listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    ```

  - _Example:_
    ```xml
    <ListView fx:id="fileList" prefHeight="200" prefWidth="300" />
    ```

- **`DatePicker` (Date Picker):** Combines a text field with a popup calendar.
  - _Properties:_ value (LocalDate type object).

  - _Usage:_ Date of birth, start/end date of an event.

  - _Attributes:_
    - `showWeekNumbers`: Boolean value for displaying week numbers.
    - `promptText`: The expected date format (e.g., `"dd.mm.yyyy"`).

  - _Example:_
    ```xml
    <DatePicker fx:id="deadlinePicker" promptText="Deadline" showWeekNumbers="true" />
    ```

- **`ColorPicker` (Color Picker):** A palette for choosing a color.
  - _Properties:_ value (Color type object).

  - _Usage:_ Setting the application theme, graphic editors.

  - _Attributes:_
    - `style`: Often used to set the base color via `-fx-color-label-visible`.

  - _Example:_
    ```xml
    <ColorPicker fx:id="colorPicker" value="#ff0000" />
    ```

**2. Event Handling:**
The most commonly used method for handling interaction is `setOnAction()`. In FXML, this is defined via the `onAction="#methodName"` attribute, and the method is implemented in the controller class with the `@FXML` annotation.

**3. Popup Controls:**
These are elements that appear over the main content:

- **`Tooltip`**: A brief hint when hovering the mouse over an element.

- **`ContextMenu`**: A menu that appears upon right-clicking.

**4. Dialogs:**
JavaFX provides the built-in `Alert` class for standard messages and the `Dialog` class for personalized windows.

- Alert Types: `INFORMATION`, `WARNING`, `ERROR`, `CONFIRMATION`.

- Interactive Dialogs: `TextInputDialog` (for string input) and `ChoiceDialog` (for list selection).
