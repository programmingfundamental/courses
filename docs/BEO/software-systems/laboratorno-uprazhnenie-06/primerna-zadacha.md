---
layout: default
title: Примерна задача
parent: Лабораторно упражнение 6
grand_parent: Програмни системи
nav_order: 2
---

# Примерна задача: Стилизиране на профилна карта (Profile Card)

Ще създадем визуално атрактивна карта на потребител, използвайки външен CSS файл за сенки, заоблени ъгли и интерактивни бутони.

### 1. Създайте нов JavaFX проект в IntelliJ IDEA

### 2. Създайте контролер клас `ProfileController` (`src/main/java/bg/tu_varna/sit/ps/lab5/ProfileController.java`)

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
                      System.out.println("Снимката е заредена успешно!");
                  });
              }
          });

          image.exceptionProperty().addListener((_, _, newException) -> {
              if (newException != null) {
                  System.err.println("Грешка при зареждане: " + newException.getMessage());
              }
          });
      }
  }
```

### 3. Създайте FXML файл `profile_view.fxml` (`src/main/resources/bg/tu_varna/sit/ps/lab6/profile_view.fxml` )

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import javafx.scene.shape.Circle?>

<VBox alignment="CENTER" spacing="15.0" styleClass="card"
      xmlns:fx="http://javafx.com/fxml"
      fx:controller="bg.tu_varna.sit.ps.lab6.ProfileController">

    <Circle fx:id="profileCircle" radius="50.0" styleClass="profile-image"/>

    <Label text="Иван Иванов" styleClass="name-label"/>
    <Label text="Софтуерен инженер" styleClass="title-label"/>

    <HBox alignment="CENTER" spacing="10.0">
        <Button text="Съобщение" styleClass="primary-button"/>
        <Button text="Профил" styleClass="secondary-button"/>
    </HBox>
</VBox>
```

### 4. Създайте CSS файл `style.css` (`src/main/resources/bg/tu_varna/sit/ps/lab6/style.css`)

```css
/* Основен контейнер за изглед (Карта) */
.card {
  -fx-background-color: white;
  -fx-background-radius: 15;
  -fx-border-radius: 15;
  -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 10, 0, 0, 5);
  -fx-padding: 30;
  -fx-pref-width: 300;
}

/* Етикети */
.name-label {
  -fx-font-size: 20px;
  -fx-font-weight: bold;
  -fx-text-fill: #2c3e50;
}

.title-label {
  -fx-font-size: 14px;
  -fx-text-fill: #7f8c8d;
}

/* Бутони */
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

### 5. Създайте нов основен клас `ProfileApplication` (`src/main/java/bg/tu_varna/sit/ps/lab6/ProfileApplication.java`)

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

          String css = ProfileApplication.class.getResource("style.css").toExternalForm();
          scene.getStylesheets().add(css);

          stage.setTitle("Profile Card");
          stage.setScene(scene);
          stage.show();
      }
  }
```

### 6. Променете в класа `Launcher` основния клас на приложението на `ProfileApplication` и стартирайте приложението
