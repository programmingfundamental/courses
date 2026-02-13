---
layout: default
title: First Application with JavaFX
parent: Laboratory Exercise 1
grand_parent: Software Systems
nav_order: 4
#permalink: /docs/AEO/software-systems/laboratory-exercise-01/first-javafx-application
---

# Creating the First JavaFX Application – Calculator

After configuring the project and creating the main classes, the first JavaFX application can be implemented. As an example, we will create a calculator that adds two numbers and displays the result in a graphical interface.

## 1. Extending the Application Class

The `CalculateApplication` class must extend the JavaFX `Application` class. This is mandatory because `Application` manages the lifecycle of a JavaFX application and allows the creation of a graphical window. Without this inheritance, the application cannot be launched as a JavaFX application.

```java
package bg.tu_varna.sit.ps.lab1;

import javafx.application.Application;
import javafx.stage.Stage;

public class CalculateApplication extends Application {

    @Override
    public void start(Stage stage) {
        // Build the graphical interface here
    }
}
```

## 2. Creating the Graphical Components

Inside the `start(Stage stage)` method, create the user interface components. Use text fields for number input, a button to trigger the calculation, and a label to display the result. These components provide basic interaction between the user and the application.

```java
TextField number1Field = new TextField();
number1Field.setPromptText("First number");

TextField number2Field = new TextField();
number2Field.setPromptText("Second number");

Button calculateButton = new Button("Calculate");

Label resultLabel = new Label("Result:");
```

*When importing, use controls from the `javafx.scene.control` packages.*

## 3. Event Handling

Add an event handler to the button, which executes when the button is clicked. This code reads the values from the text fields, converts them to numbers, performs the arithmetic operation, and displays the result. This demonstrates how to define application behavior in response to user interface interactions.

```java
calculateButton.setOnAction(event -> {
    double num1 = Double.parseDouble(number1Field.getText());
    double num2 = Double.parseDouble(number2Field.getText());
    double result = num1 + num2;

    resultLabel.setText("Result: " + result);
});
```

## 4. Arranging Components (Layout)

Use a `VBox` layout container to arrange the components. Layout containers are necessary because JavaFX does not allow direct positioning of elements without a container. `VBox` arranges components vertically, which is convenient for a simple interface like the calculator.

```java
VBox layout = new VBox(12);
layout.setAlignment(Pos.CENTER);
layout.setPadding(new Insets(20));
layout.getChildren().addAll(
        number1Field,
        number2Field,
        calculateButton,
        resultLabel
);
```

The `VBox` container arranges all components one below another in the order they are added. The value `12` sets the spacing between elements. `setAlignment(Pos.CENTER)` centers all components in the window, improving the visual layout. `setPadding(new Insets(20))` adds padding between the components and the edges of the window to prevent them from touching the borders.

## 5. Creating the Scene and Showing the Window

After building the interface, place it in a `Scene` object. Associate the scene with the main window (`Stage`), set a title, and call `show()` to display the application.

```java
Scene scene = new Scene(layout, 320, 240);

stage.setTitle("Addition Calculator");
stage.setScene(scene);
stage.show();
```

## 6. Launching the Application via the Launcher Class

To safely start a JavaFX application, use a separate `Launcher` class containing the `main()` method. It calls the static `launch()` method, which starts the JavaFX environment and invokes the `start()` method of the `CalculateApplication` class.

```java
package bg.tu_varna.sit.ps.lab1;

import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        Application.launch(CalculateApplication.class, args);
    }
}
```

When launched successfully, the following window should appear: <br> <img width="402" height="339" alt="Screenshot 2026-02-03 151410" src="https://github.com/user-attachments/assets/41735391-83c5-4049-9247-73e193b2e7dd" />
