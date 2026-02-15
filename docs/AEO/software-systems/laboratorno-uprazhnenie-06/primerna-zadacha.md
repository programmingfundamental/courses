---
layout: default
title: Sample Task
parent: Laboratory Exercise 6
grand_parent: Software Systems
nav_order: 2
---

# Sample Task: Styling a Profile Card

We will create a visually attractive user profile card using an external CSS file for shadows, rounded corners, and interactive buttons.

### 1. Create a new JavaFX project in IntelliJ IDEA

### 2. Create the controller class `ProfileController` (`src/main/java/bg/tu_varna/sit/ps/lab6/ProfileController.java`)

```java
package bg.tu_varna.sit.ps.lab6;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class ProfileController {
    @FXML
    private Circle profileCircle;

    @FXML
    public void initialize() {
        String imageUrl = "https://randomuser.me/api/portraits/men/21.jpg";

        Image image = new Image(imageUrl, true);

        image.progressProperty().addListener((_, _, newProgress) -> {
            if (newProgress.doubleValue() == 1.0 && !image.isError()) {
                Platform.runLater(() -> {
                    profileCircle.setFill(new ImagePattern(image));
                    System.out.println("Image loaded successfully!");
                });
            }
        });

        image.exceptionProperty().addListener((_, _, newException) -> {
            if (newException != null) {
                System.err.println("Loading error: " + newException.getMessage());
            }
        });
    }
}
```

### 3. Create the FXML file `profile-view.fxml` (`src/main/resources/bg/tu_varna/sit/ps/lab6/profile-view.fxml`)

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import javafx.scene.shape.Circle?>

<VBox alignment="CENTER" spacing="15.0" styleClass="card"
      xmlns:fx="http://javafx.com/fxml"
      fx:controller="bg.tu_varna.sit.ps.lab6.ProfileController">

    <Circle fx:id="profileCircle" radius="50.0" styleClass="profile-image"/>

    <Label text="Ivan Ivanov" styleClass="name-label"/>
    <Label text="Software Engineer" styleClass="title-label"/>

    <HBox alignment="CENTER" spacing="10.0">
        <Button text="Message" styleClass="primary-button"/>
        <Button text="Profile" styleClass="secondary-button"/>
    </HBox>
</VBox>
```

### 4. Create the CSS file `style.css` (`src/main/resources/bg/tu_varna/sit/ps/lab6/style.css`)

```css
/* Main Layout Container (Card) */
.card {
  -fx-background-color: white;
  -fx-background-radius: 15;
  -fx-border-radius: 15;
  -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 10, 0, 0, 5);
  -fx-padding: 30;
  -fx-pref-width: 300;
}

/* Labels */
.name-label {
  -fx-font-size: 20px;
  -fx-font-weight: bold;
  -fx-text-fill: #2c3e50;
}

.title-label {
  -fx-font-size: 14px;
  -fx-text-fill: #7f8c8d;
}

/* Buttons */
.primary-button {
  -fx-background-color: #3498db;
  -fx-text-fill: white;
  -fx-background-radius: 20;
  -fx-padding: 8 20;
}

.primary-button:hover {
  -fx-background-color: #2980b9;
  -fx-cursor: hand;
}

.secondary-button {
  -fx-background-color: transparent;
  -fx-border-color: #3498db;
  -fx-border-width: 2;
  -fx-border-radius: 20;
  -fx-text-fill: #3498db;
  -fx-padding: 8 20;
}

.secondary-button:hover {
  -fx-background-color: #ecf0f1;
}

.profile-image {
  -fx-fill: #ecf0f1;
  -fx-stroke: #3498db;
  -fx-stroke-width: 3;
  -fx-stroke-type: outside;
  -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 5, 0, 0, 2);
}
```

### 5. Create a new main class `ProfileApplication` (`src/main/java/bg/tu_varna/sit/ps/lab6/ProfileApplication.java`)

```java
package bg.tu_varna.sit.ps.lab6;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ProfileApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ProfileApplication.class.getResource("profile-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 380, 420);

        String css = Objects.requireNonNull(ProfileApplication.class.getResource("style.css")).toExternalForm();
        scene.getStylesheets().add(css);

        stage.setTitle("Profile Card");
        stage.setScene(scene);
        stage.show();
    }
}
```

### 6. Change the main application class to `ProfileApplication` in the `Launcher` class and run the application
