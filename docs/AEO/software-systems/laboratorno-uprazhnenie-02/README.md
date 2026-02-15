---
layout: default
title: Laboratory Exercise 2
parent: Software Systems
has_children: true
nav_order: 2
#permalink: /docs/software-systems/laboratorno-uprazhnenie-2
---

# Exercise #2

In this exercise, we will focus on building the visual structure of JavaFX applications using **Layout Containers (Layout Controls)** and **FXML**.

## 1. FXML – Declarative UI Design

FXML is an XML-based language used to define the user interface in JavaFX. It allows for a clear separation between visual representation and program logic.

### 1.1. Structure of an FXML File

Every FXML file follows a strictly defined structure consisting of three main parts:

1. XML Header: The first line is always the standard XML definition line:

`<?xml version="1.0" encoding="UTF-8"?>`

2. Import Instructions (`<?import ...?>`):

In FXML, we cannot use graphical elements without importing them first. These correspond directly to import statements in Java.

Example: To use a `Button`, we must add `<?import javafx.scene.control.Button?>`. Wildcards can also be used: `<?import javafx.scene.layout.*?>`.

3. Root Element:

Every file must have exactly one root element (usually a layout container like `AnchorPane` or `VBox`). It defines the start of the hierarchy and contains the namespace definition (`xmlns:fx="http://javafx.com/fxml/1"`) and the link to the controller.

### 1.2. Hierarchy of Graphical Elements: Containers and Child Elements

In JavaFX, everything is organized in a tree structure (Scene Graph). Elements are divided into two main categories based on their role in the hierarchy:

- Container Elements (Parent/Containers):

  These are elements that can contain other elements within them. In JavaFX, these are descendants of the Parent class (most often from the `javafx.scene.layout` package).
  - Characteristics: They possess a `children` property (a list of children). In FXML, child elements are written directly between the opening and closing tags of the container.

  - Examples: `HBox`, `VBox`, `GridPane`, `StackPane`.

- Child/Leaf Elements (Children/Leaf Nodes):

  These are terminal elements that usually do not contain other objects. These are the basic controls of the interface.
  - Characteristics: They are placed inside containers, and their properties (position, size) are often managed by the logic of the parent container.

  - Examples: `Button`, `Label`, `TextField`.

Nesting Rule: Containers can contain both controls (`Button`) and other containers (`HBox` inside a `VBox`). This allows for the construction of complex and adaptive interfaces through nested structures.

### 1.3. Linking FXML and Java (fx:id)

To access a graphical element defined in FXML from Java code, we use the `fx:id` attribute.

Example in FXML: `<Button fx:id="myButton" />`

Example in Java (via lookup): `Button btn = (Button) root.lookup("#myButton");`

## 2. Layout Containers (Layout Controls):

Layout containers are special containers that manage the positioning and sizing of their children (other controls or layout containers). Using them is key to creating responsive and professional-looking user interfaces.

- **`HBox` (Horizontal Box):** Arranges its children side-by-side horizontally.
  - Properties: `spacing` (gap between children), `alignment` (alignment of children within the box), `HGrow` (horizontal expansion for an individual child).
  - Example:
    - Without attributes:

      ```xml
      <HBox> ... </HBox>
      ```

    - With attributes:
      ```xml
      <HBox spacing="15" alignment="CENTER" prefHeight="50.0"> ... </HBox>
      ```

- **`VBox` (Vertical Box):** Arranges its children one below the other vertically.
  - Properties: `spacing` (gap between children), `alignment` (alignment of children within the box), `VGrow` (vertical expansion for an individual child).
  - Example:
    - Without attributes:

      ```xml
      <VBox> ... </VBox>
      ```

    - With attributes:
      ```xml
      <VBox spacing="10" alignment="TOP_LEFT" prefWidth="200.0"> ... </VBox>
      ```

- **`BorderPane` (Border Container):** Arranges its children in five regions: `TOP`, `BOTTOM`, `LEFT`, `RIGHT`, and `CENTER`.
  - Very useful for basic application layouts (e.g., menu at the top, status bar at the bottom, navigation on the left/right, and main content in the center).
    - Example:
      - Without attributes:

        ```xml
        <BorderPane> ... </BorderPane>
        ```

      - With attributes:
        ```xml
        <BorderPane prefWidth="800" prefHeight="600">
            <top> <!-- Defines the element in the top zone -->
                <Label text="Header" BorderPane.alignment="CENTER" />
            </top>
        </BorderPane>
        ```

- **`GridPane` (Grid Container):** Organizes its children in a flexible grid of rows and columns.
  - Useful for forms, tables, or other structured layouts. Children are added to a specific column and row.
  - Example:
    - Without attributes:

      ```xml
      <GridPane> ... </GridPane>
      ```

    - With attributes:
      ```xml
      <GridPane hgap="10" vgap="10" alignment="CENTER">
          <!-- Defines distance between columns (hgap) and rows (vgap) -->
      </GridPane>
      ```

- **`FlowPane` (Flow Container):** Arranges its children sequentially, similar to a flow of text. When no space remains in the current row/column, it moves to the next.
  - Can be horizontal or vertical.
  - Example:
    - Without attributes:

      ```xml
      <FlowPane> ... </FlowPane>
      ```

    - With attributes:
      ```xml
      <FlowPane orientation="VERTICAL" hgap="5" vgap="5" prefWrapLength="200"> ... </FlowPane>
      ```

- **`StackPane` (Stacking Container):** Layers its children one on top of the other in the center, similar to a stack of cards.
  - Useful for showing alternative views or overlaying elements (e.g., a loading indicator over other content).
  - Example:
    - Without attributes:

      ```xml
      <StackPane> ... </StackPane>
      ```

    - With attributes:
      ```xml
      <StackPane prefWidth="400" prefHeight="300"> ... </StackPane>
      ```

- **`AnchorPane` (Anchoring Container):** Allows anchoring of its children to one or more sides of the container.
  - Provides very precise control over positioning but can be harder to maintain when resizing. Positions are set via the properties `topAnchor`, `bottomAnchor`, `leftAnchor`, and `rightAnchor`.
  - Example:
    - Without attributes:

      ```xml
      <AnchorPane> ... </AnchorPane>
      ```

    - With attributes:

      ```xml
      <AnchorPane>
          <Button text="Stay in corner" AnchorPane.topAnchor="10.0" AnchorPane.rightAnchor="10.0" />

      </AnchorPane>
      ```

## 3. Basic UI Controls:

### 3.1 Text Controls

These are used for visualizing or inputting text information.

- **`Label`:** Non-interactive text used to describe other elements.
  - _Properties:_ `text` (content), `graphic` (can contain an icon/image), `wrapText` (multi-line wrap), `alignment`.

  - _Usage:_ Titles, instructions for input fields.

- **`TextField`:** A single-line field for entering free text.
  - _Properties:_ `promptText` (hint text inside the field), `text` (the entered string), `editable` (whether it can be edited).

  - _Usage:_ Username, email, search queries.

- **`PasswordField`:** A specialized `TextField` that masks the entered characters (usually with dots).
  - _Usage:_ Passwords and sensitive information.

- **`TextArea`:** A multi-line field for entering long texts.
  - _Properties:_ `prefRowCount`, `prefColumnCount`, `wrapText`.

  - _Usage:_ Comments, descriptions, logs.

### 3.2 Buttons and Selection Controls:

These elements trigger logic or allow the user to make a choice.

- **`Button`:** A standard clickable button.
  - _Properties:_ `defaultButton` (activated by pressing `Enter`), `cancelButton` (activated by `Escape`).

  - _Usage:_ Form confirmation, opening new windows.

- **`CheckBox`:** A two-state (or three-state) selection.
  - _Properties:_ `selected` (`boolean`), `allowIndeterminate` (allows a third "undefined" state).

  - _Usage:_ Accepting terms and conditions, selecting multiple options.

- **`RadioButton`:** Selection of only one option from a given group.
  - _Properties:_ `toggleGroup` (all buttons in a logical group must share one instance of a `ToggleGroup`).

  - _Usage:_ Selecting gender, payment method, difficulty level.

- **`ToggleButton`:** A button that remains in a "selected" state until it is pressed again.
  - _Usage:_ Toggling modes on/off (e.g., `Bold`/`Italic` in a text editor).
