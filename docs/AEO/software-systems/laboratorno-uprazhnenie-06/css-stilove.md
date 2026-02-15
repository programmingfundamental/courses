---
layout: default
title: List of Styles (CSS) in JavaFX
parent: Laboratory Exercise 6
grand_parent: Software Systems
nav_order: 1
permalink: /docs/software-systems/laboratorno-uprazhnenie-6/css-stilove
---

### 1. Backgrounds & Fills

These properties determine the appearance of the "body" of a graphical element.

- `-fx-background-color`: Sets the background color. It can accept color names (`red`), Hex codes (`#FFFFFF`), RGB (`rgb(255,0,0)`), or gradients (`linear-gradient`).

  ```css
  .my-button {
    -fx-background-color: #ff0000;
  }
  ```

  ```css
  .my-pane {
    -fx-background-color: linear-gradient(to bottom, #ff0000, #0000ff);
  }
  ```

  ```css
  .my-label {
    -fx-background-color: derive(green, 20%);
  }
  ```

- `-fx-background-radius`: Determines the rounding of background corners. Accepts 1 value (for all corners) or 4 values (Top-Left, Top-Right, Bottom-Right, Bottom-Left).

  ```css
  .my-button {
    -fx-background-radius: 10;
  }
  ```

  ```css
  .my-pane {
    -fx-background-radius: 10 0 0 10;
  }
  ```

- `-fx-background-insets`: Determines the offset of the background from the element's boundaries. Often used to create "nested" colors or a border effect.

  ```css
  .my-button {
    -fx-background-color: red, white;
    -fx-background-insets: 0, 2;
    -fx-background-radius: 10, 8;
  }
  ```

- `-fx-fill`: Specifically used for shapes (`Circle`, `Rectangle`) or for the color of a `Text` object.

  ```css
  .my-circle {
    -fx-fill: blue;
  }
  ```

### 2. Borders

These control the outer line around the container or control.

- `-fx-border-color`: Sets the color of the border.

  ```css
  .my-pane {
    -fx-border-color: black;
  }
  ```

- `-fx-border-width`: Sets the thickness of the border. Can be set individually: `-fx-border-width: 1 0 1 1;` (Top, Right, Bottom, Left).

  ```css
  .my-pane {
    -fx-border-width: 2;
  }
  ```

  ```css
  .my-pane {
    -fx-border-width: 2 0 2 2;
  }
  ```

- `-fx-border-radius`: Determines the rounding of the border itself. Usually, this should match `-fx-background-radius`.

  ```css
  .my-button {
    -fx-border-radius: 10;
  }
  ```

  ```css
  .my-pane {
    -fx-border-radius: 10 0 0 10;
  }
  ```

- `-fx-border-style`: Determines the line style – `solid`, `dashed`, or `dotted`.

  ```css
  .my-pane {
    -fx-border-style: dashed;
  }
  ```

- `-fx-stroke`: Used instead of `-fx-border-color` for shapes such as `Circle` or `Line`.

  ```css
  .my-circle {
    -fx-stroke: red;
  }
  ```

- `-fx-stroke-width`: Line thickness for shapes.

  ```css
  .my-circle {
    -fx-stroke-width: 3;
  }
  ```

### 3. Text & Fonts

Applied to controls containing text (`Label`, `Button`, `TextField`).

- `-fx-text-fill`: Sets the color of the text within controls (equivalent to `color` in web development).

  ```css
  .my-label {
    -fx-text-fill: white;
  }
  ```

- `-fx-font-family`: Font choice (e.g., `"Arial"`, `"System"`, `"Verdana"`).

  ```css
  .my-label {
    -fx-font-family: 'Arial';
  }
  ```

- `-fx-font-size`: Font size (e.g., `14px`, `1.2em`).

  ```css
  .my-label {
    -fx-font-size: 16px;
  }
  ```

- `-fx-font-weight`: Font weight (`bold`, `normal`, `100` to `900`).

  ```css
  .my-label {
    -fx-font-weight: bold;
  }
  ```

- `-fx-font-style`: Font style (`italic`, `normal`).

  ```css
  .my-label {
    -fx-font-style: italic;
  }
  ```

- `-fx-alignment`: Alignment of content within the element (`center`, `center-left`, `top-right`).

  ```css
  .my-button {
    -fx-alignment: center;
  }
  ```

### 4. Sizing & Spacing

These manage the placement of the element and the space within it.

- `-fx-padding`: Internal spacing between the content and the border of the element.

  ```css
  .my-button {
    -fx-padding: 10;
  }
  ```

  ```css
  .my-button {
    -fx-padding: 10 20; /* Top/Bottom, Left/Right */
  }
  ```

  ```css
  .my-button {
    -fx-padding: 10 20 5 15; /* Top, Right, Bottom, Left */
  }
  ```

- `-fx-pref-width` / `-fx-pref-height`: Preferred width and height.

  ```css
  .my-button {
    -fx-pref-width: 100;
    -fx-pref-height: 40;
  }
  ```

- `-fx-min-width` / `-fx-max-width`: Constraints for minimum and maximum width.

  ```css
  .my-button {
    -fx-min-width: 80;
    -fx-max-width: 150;
  }
  ```

- `-fx-opacity`: Transparency of the entire element (from `0.0` – fully transparent, to `1.0` – solid).

  ```css
  .my-pane {
    -fx-opacity: 0.5;
  }
  ```

### 5. Effects & Interactivity

Visual enhancements and behavior.

- `-fx-effect`: Application of graphical effects. Most common:
  - `dropshadow(type, color, radius, spread, x-offset, y-offset)` – outer shadow.

    ```css
    .my-button {
      -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 10, 0, 5, 5);
    }
    ```

  - `inner-shadow(...)` – inner shadow.

    ```css
    .my-button {
      -fx-effect: inner-shadow(gaussian, rgba(0, 0, 0, 0.5), 10, 0, 5, 5);
    }
    ```

- `-fx-cursor`: Changes the cursor type when hovering (`hand`, `default` - arrow, `wait` - loading).

  ```css
  .my-button {
    -fx-cursor: hand;
  }
  ```

- `-fx-visibility`: Determines if the element is visible (`visible`, `hidden`).

  ```css
  .my-pane {
    -fx-visibility: hidden;
  }
  ```

### 6. Pseudo-classes (States)

Used for dynamic style changes based on user action.

- `:hover`: Style when the mouse is over the element.

  ```css
  .my-button:hover {
    -fx-background-color: #00ff00;
  }
  ```

- `:pressed`: Style while the element is being pressed.

  ```css
  .my-button:pressed {
    -fx-background-color: #0000ff;
  }
  ```

- `:focused`: Style when the element has focus (e.g., a selected text field).

  ```css
  .my-textfield:focused {
    -fx-border-color: blue;
  }
  ```

- `:disabled`: Style when the control is inactive (`setDisable(true)`).

  ```css
  .my-button:disabled {
    -fx-background-color: #cccccc;
    -fx-text-fill: #666666;
  }
  ```

### 7. Colors

JavaFX supports various ways to define colors:

- Color names: `red`, `blue`, `green`, `black`, `white`, etc.
- Hex codes: `#RRGGBB` (e.g., `#ff0000` for red).
- RGBA: `rgba(255, 0, 0, 0.5)` for semi-transparent red.
- HSL: `hsl(120, 100%, 50%)` for bright green.
- `derive(color, percentage)`: Allows creating a lighter or darker shade of a given color (e.g., `derive(red, 20%)` for a lighter red).
