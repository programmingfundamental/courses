---
layout: default
title: Tasks
parent: Laboratory Exercise 6
grand_parent: Software Systems
nav_order: 3
---

# Independent Tasks

### 1. Task 1: Creating a "Dark Mode"

- Create a copy of the previous CSS file under the name `dark-style.css`.

- Change the colors so that the card background is dark gray (`#2d3436`) and the text is light (`#dfe6e9`).

- Modify the shadow color (`dropshadow`) to make it look more discreet on a dark background.

- Apply the new file and observe the differences.

### 2. Task 2: Styling a Login Form by ID

- Create a login form (Login) with two `TextField` nodes and one `Button`.

- In the FXML file, set `id="login-button"` for the button.

- In the CSS file, use an ID selector (`#login-button`) to make the button bright orange only in this specific form, without changing the other buttons in the application.

- Use `-fx-background-insets` to add an "inner shadow" effect to the text fields.

### 3. Task 3: Working with Rounded Elements

- Create an `HBox` graphical element containing three buttons: "Home", "Gallery", "Contacts".

- Style them using CSS classes so that:
  - Only the left button has rounded corners on the left side.

  - Only the right button has rounded corners on the right side.

  - The middle button remains with square corners.

- This is achieved using the `-fx-background-radius` property (which accepts four values, one for each corner).
