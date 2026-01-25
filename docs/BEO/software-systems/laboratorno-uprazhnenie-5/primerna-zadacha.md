---
layout: default
title: Примерна задача
parent: Лабораторно упражнение 5
grand_parent: Програмни системи
nav_order: 1
---

# Примерна задача: Система за регистрация на софтуерен лиценз

Ще създадем форма, която събира данни от потребителя, използва различни видове контроли и показва диалогов прозорец за потвърждение.

### 1. Създайте нов JavaFX проект в IntelliJ IDEA

### 2. Създайте контролер клас `LicenseController` (`src/main/java/bg/tu_varna/sit/ps/lab5/LicenseController.java`)

```java
  package bg.tu_varna.sit.ps.lab5;

  import javafx.fxml.FXML;
  import javafx.scene.control.*;
  import javafx.util.Duration;

  import java.util.Optional;

  public class LicenseController {
      @FXML
      private TextField userNameField;
      @FXML
      private ComboBox<String> licenseCombo;
      @FXML
      private CheckBox termsCheckBox;

      @FXML
      public void initialize() {
          licenseCombo.getItems().addAll("Личен", "Бизнес", "Академичен");
          licenseCombo.getSelectionModel().selectFirst();

          Tooltip checkTooltip = new Tooltip("Трябва да се съгласите с условията, за да продължите");
          checkTooltip.setShowDelay(Duration.millis(500));

          termsCheckBox.setTooltip(checkTooltip);
      }

      @FXML
      protected void handleRegister() {
          if (!termsCheckBox.isSelected()) {
              showDialog(Alert.AlertType.WARNING, "Предупреждение", "Трябва да приемете условията, за да продължите!");
              return;
          }

          String user = userNameField.getText();
          String type = licenseCombo.getValue();

          if (user == null || user.trim().isEmpty()) {
              showDialog(Alert.AlertType.ERROR, "Грешка", "Полето 'Потребителско име' не може да бъде празно!");
          } else {
              Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
              confirm.setTitle("Потвърждение на регистрацията");
              confirm.setHeaderText("Ще регистрирате лиценз на името на: " + user);
              confirm.setContentText("Избран тип лиценз: " + type + ". Желаете ли да продължите?");

              ((Button) confirm.getDialogPane().lookupButton(ButtonType.OK)).setText("Да");
              ((Button) confirm.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("Отказ");

              Optional<ButtonType> result = confirm.showAndWait();
              if (result.isPresent() && result.get() == ButtonType.OK) {
                  showDialog(Alert.AlertType.INFORMATION, "Успех", "Лицензът беше регистриран успешно!");
              }
          }
      }

      @FXML
      protected void handleClear() {
          userNameField.clear();
          termsCheckBox.setSelected(false);
          licenseCombo.getSelectionModel().selectFirst();
      }

      private void showDialog(Alert.AlertType type, String title, String content) {
          Alert alert = new Alert(type);
          alert.setTitle(title);
          alert.setHeaderText(null);
          alert.setContentText(content);
          alert.showAndWait();
      }
  }

```

### 3. Създайте FXML файл `license_view.fxml` (`src/main/resources/bg/tu_varna/sit/ps/lab5/license_view.fxml` )

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.geometry.Insets?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<GridPane fx:controller="bg.tu_varna.sit.ps.lab5.LicenseController"
          xmlns:fx="http://javafx.com/fxml" hgap="10" vgap="10">
    <padding>
        <Insets top="20" right="20" bottom="20" left="20"/>
    </padding>

    <Label text="Регистрация на софтуерен лиценз"
           style="-fx-font-size: 18px; -fx-font-weight: bold;"
           GridPane.columnSpan="2" GridPane.halignment="CENTER"/>

    <Label text="Потребителско име:" GridPane.rowIndex="1" GridPane.columnIndex="0"/>
    <TextField fx:id="userNameField" GridPane.rowIndex="1" GridPane.columnIndex="1">
        <tooltip>
            <Tooltip text="Въведете вашето пълно име"/>
        </tooltip>
    </TextField>

    <Label text="Тип лиценз:" GridPane.rowIndex="2" GridPane.columnIndex="0"/>
    <ComboBox fx:id="licenseCombo" GridPane.rowIndex="2" GridPane.columnIndex="1" prefWidth="150">
        <tooltip>
            <Tooltip text="Изберете план, който отговаря на вашите нужди"/>
        </tooltip>
    </ComboBox>

    <Label text="Приемам условията:" GridPane.rowIndex="3" GridPane.columnIndex="0"/>
    <CheckBox fx:id="termsCheckBox" GridPane.rowIndex="3" GridPane.columnIndex="1">
        <tooltip>
            <Tooltip text="Трябва да се съгласите с лицензионното споразумение"/>
        </tooltip>
    </CheckBox>

    <HBox spacing="10" alignment="CENTER_RIGHT" GridPane.rowIndex="4" GridPane.columnIndex="1">
        <Button text="Регистрирай" onAction="#handleRegister">
            <tooltip>
                <Tooltip text="Изпращане на данните за регистрация"/>
            </tooltip>
        </Button>
        <Button text="Изчисти" onAction="#handleClear">
            <tooltip>
                <Tooltip text="Изчистване на всички полета във формата"/>
            </tooltip>
        </Button>
    </HBox>
</GridPane>
```

### 4. Променете зареждания изглед в основния клас на приложението `HelloApplication` (`src/main/java/bg/tu_varna/sit/ps/lab5/HelloApplication.java`)

```java
  package bg.tu_varna.sit.ps.lab5;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("license-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 345, 200);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
}
```

### 5. Стартирайте приложението
