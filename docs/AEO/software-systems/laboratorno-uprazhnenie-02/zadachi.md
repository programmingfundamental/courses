---
layout: default
title: Tasks
parent: Laboratory Exercise 2
grand_parent: Software Systems
nav_order: 2
---

# Independent Tasks

Using FXML and the learned Layout Containers, complete the following tasks.

### Task 1: Login Screen

Create a new FXML file (e.g., `login-view.fxml`). The design should look like this:

- In the center of the window, place a `GridPane` (Grid Container).
- The `GridPane` should contain:
  - A `Label` for "Username:" and a `TextField` for input.
  - A `Label` for "Password:" and a `PasswordField` for input.
  - Two `Button` elements: "Login" and "Cancel".
- The buttons should be arranged horizontally (this can be done in an `HBox` (Horizontal Box) inside the `GridPane` or by using `GridPane.columnSpan`).
- Use `padding` and `spacing` for a better appearance.
- Center the `GridPane` in the window (you can use a `StackPane` (Stacking Container) as the root of the scene or configure the `GridPane` `alignment` settings).

### Task 2: Toolbar Application with Multiple Sections

Create a new FXML file (e.g., `multi-section-view.fxml`). The design should include:

- `BorderPane` (Border Container) as the main Layout Container.
- **Top Region (`top`):** An `HBox` (Horizontal Box) containing several `Button` elements that mimic a Toolbar – for example, "New", "Open", "Save".
- **Left Region (`left`):** A `VBox` (Vertical Box) containing a `ListView` (or several `Button` elements) representing navigation items.
- **Central Region (`center`):** A `StackPane` (Stacking Container) containing at least three different graphical elements (these could be simple `Label` nodes inside a `VBox` or more complex Layout Containers like `GridPane`, `FlowPane`, or `AnchorPane` from the previous example), with each having `visible="false"` by default. The goal is to demonstrate that a `StackPane` can hold different views.
- **Bottom Region (`bottom`):** An `HBox` (Horizontal Box) for a status bar.
- **Right Region (`right`):** A `FlowPane` (Flow Container) with several `Label` or `TextField` controls showing "Properties" or "Details".

### Task 3: Product Gallery

Create an FXML file for a simple product gallery.

- Use a `TilePane` (another Layout Container not covered in detail, but it works similarly to `FlowPane` while arranging elements in uniform tiles) or a `FlowPane` (Flow Container).
- Each "tile" should contain a `VBox` (Vertical Box) with an `ImageView` (for the product) and two `Label` nodes (for name and price).
- Use placeholder images (you can use any `*.png` or `*.jpg` files from the internet).
- The goal is to demonstrate an adaptive arrangement of many similar elements.

### Task 4: Settings Form

Create a new FXML file `settings-view.fxml`. The goal is to practice selection controls.

- Root: `VBox` with centering and `spacing` of `15px`.

- Elements:
  - `Label` with the title "Notification Settings".

  - `CheckBox` for "Email Notifications".

  - `CheckBox` for "SMS Notifications" (selected by default).

  - Below them, add a `Separator` (horizontal line).

  - `Label` "Interface Theme:".

  - Three `RadioButton` elements: "Light", "Dark", "System".
    - **Important:** The three buttons must be in a single `ToggleGroup` so that only one can be selected at a time.

  - One `ToggleButton` control with the text "Mode: Online/Offline".

### Task 5: Feedback Form

Create a new FXML file `feedback-view.fxml`. This task focuses on working with long text and positioning.

- Root: `AnchorPane`.

- Elements:
  - At the top: `Label` "Your feedback is important to us".

  - In the middle: `TextArea` for free text input.
    - Use `promptText="Write your comment here..."`.

    - Set `wrapText="true"` so that the text wraps automatically.

    - Anchor it (`AnchorPane` anchors) at `50px` from all sides so it expands with the window.

  - At the bottom: An `HBox` containing a "Send" `Button` and a "Clear" `Button`, positioned in the bottom-right corner using anchoring.

### Task 6: Application Launcher

Create an FXML file `launcher-config-view.fxml` that combines everything learned.

- Root: `BorderPane`.

- Center (`center`): `GridPane` with the following fields:
  - Row 0: `Label` "Application Name:" and a `TextField`.

  - Row 1: `Label` "Description:" and a `TextArea` (with a height for 3 rows).

  - Row 2: `Label` "Launch Mode:" and an `HBox` containing two `RadioButton` nodes ("Windowed", "Full Screen").

  - Row 3: `Label` "Options:" and a `VBox` with two `CheckBox` nodes ("Auto-update", "Start with system").

- Bottom (`bottom`): An `HBox` with a "Save Changes" button, set as `defaultButton="true"` (to glow blue and respond to the Enter key).
