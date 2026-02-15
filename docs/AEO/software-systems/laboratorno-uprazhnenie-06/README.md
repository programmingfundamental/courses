---
layout: default
title: Laboratory Exercise 6
parent: Software Systems
has_children: true
nav_order: 6
#permalink: /docs/software-systems/laboratorno-uprazhnenie-6
---

# Exercise #6

JavaFX uses CSS (Cascading Style Sheets) to separate visual design from application logic, similar to web development. JavaFX CSS is based on the CSS 2.1 specification with some elements from CSS 3.

**1. JavaFX CSS Specifics:**

- **Prefix -fx-:** All JavaFX-specific properties start with the -fx- prefix (e.g., `-fx-background-color` instead of `background-color`).

- **Files:** External files with the `.css` extension are typically used.

- **Default Style:** By default, JavaFX applications use the Modena theme.

- [List of Styles](https://programmingfundamental.github.io/courses/docs/AEO/software-systems/laboratorno-uprazhnenie-06/css-stilove) - A detailed overview of CSS styles in JavaFX.

**1.2. Selectors:**

- **Type Selector:** Applied to all elements of a given type (e.g., `.button` for all buttons).

- **ID Selector:** Applied to a specific graphical element via its ID (defined with `setId()` in Java or `fx:id` in FXML). In CSS, the `#` symbol is used (e.g., `#login-button`).

- **Style Class:** Allows applying a style to a group of elements. In CSS, the `.` symbol is used (e.g., `.main-label`).

**3. Style Application Levels (by priority):**

1. **Inline styles (directly in the code):** The method `setStyle("-fx-property: value;")` is used. (Highest priority).

2. **External CSS (external file):** Added to the scene graph or a specific container: `scene.getStylesheets().add("style.css");`.

3. **User Agent Stylesheet:** The system theme (Modena).

**4. Pseudo-classes:**
JavaFX supports states such as `:hover` (when hovering with the mouse), `:pressed` (when clicked), and `:focused` (when the element has focus).
