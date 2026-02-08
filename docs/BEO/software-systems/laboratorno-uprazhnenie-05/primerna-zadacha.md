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

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import java.time.LocalDate;
import java.util.Optional;

public class LicenseController {
    @FXML private TextField userNameField;
    @FXML private ComboBox<String> licenseCombo;
    @FXML private CheckBox termsCheckBox;
    @FXML private DatePicker startDatePicker;
    @FXML private Slider usersSlider;
    @FXML private Label usersCountLabel;
    @FXML private ToggleGroup versionGroup;
    @FXML private ColorPicker themeColorPicker;
    @FXML private ProgressBar progressBar;
    @FXML private ProgressIndicator progressIndicator;

    @FXML
    public void initialize() {
        // Инициализация на ComboBox
        licenseCombo.getItems().addAll("Личен", "Бизнес", "Академичен");
        licenseCombo.getSelectionModel().selectFirst();

        // Настройка на DatePicker (текуща дата по подразбиране)
        startDatePicker.setValue(LocalDate.now());

        // Свързване на стойността на Slider-а с етикета за брой потребители
        usersSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                usersCountLabel.setText(String.valueOf(newVal.intValue())));

        // Подсказки
        Tooltip checkTooltip = new Tooltip("Трябва да се съгласите с условията");
        checkTooltip.setShowDelay(Duration.millis(500));
        termsCheckBox.setTooltip(checkTooltip);

        // Начално състояние на прогреса
        progressBar.setProgress(0);
        progressIndicator.setProgress(0);
    }

    @FXML
    protected void handleRegister() {
        if (!termsCheckBox.isSelected()) {
            showDialog(Alert.AlertType.WARNING, "Предупреждение", "Приемете условията!");
            return;
        }

        String user = userNameField.getText();
        if (user == null || user.trim().isEmpty()) {
            showDialog(Alert.AlertType.ERROR, "Грешка", "Въведете име!");
            return;
        }

        // Вземане на данни от новите контроли
        Alert confirm = getConfirm(user);

        ((Button) confirm.getDialogPane().lookupButton(ButtonType.OK)).setText("Да");
        ((Button) confirm.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("Отказ");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            simulateVerification();
        }
    }

    private Alert getConfirm(String user) {
        LocalDate date = startDatePicker.getValue();
        int users = (int) usersSlider.getValue();
        RadioButton selectedVersion = (RadioButton) versionGroup.getSelectedToggle();
        String version = selectedVersion.getText();
        Color themeColor = themeColorPicker.getValue();

        // Диалог за потвърждение с обобщена информация
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Потвърждение");
        confirm.setHeaderText("Регистрация за: " + user);
        confirm.setContentText(String.format(
                "Тип: %s\nНачало: %s\nПотребители: %d\nВерсия: %s\nЦвят: %s\n\nЖелаете ли да продължите?",
                licenseCombo.getValue(), date, users, version, themeColor
        ));

        return confirm;
    }

    private void simulateVerification() {
        // Симулация на процес на проверка чрез ProgressBar (използва се Task за паралелност)
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                for (int i = 0; i <= 10; i++) {
                    updateProgress(i, 10);
                    Thread.sleep(200); // Симулираме забавяне
                }
                return null;
            }
        };

        progressBar.progressProperty().bind(task.progressProperty());
        progressIndicator.progressProperty().bind(task.progressProperty());

        task.setOnSucceeded(e -> {
            progressBar.progressProperty().unbind();
            progressIndicator.progressProperty().unbind();
            showDialog(Alert.AlertType.INFORMATION, "Успех", "Лицензът е активиран!");
        });

        new Thread(task).start();
    }

    @FXML
    protected void handleClear() {
        userNameField.clear();
        termsCheckBox.setSelected(false);
        licenseCombo.getSelectionModel().selectFirst();
        startDatePicker.setValue(LocalDate.now());
        usersSlider.setValue(1);
        themeColorPicker.setValue(Color.WHITE);
        progressBar.setProgress(0);
        progressIndicator.setProgress(0);
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

    <Label text="Система за управление на лицензи"
           style="-fx-font-size: 18px; -fx-font-weight: bold;"
           GridPane.columnSpan="3" GridPane.halignment="CENTER"/>

    <!-- Име -->
    <Label text="Потребител:" GridPane.rowIndex="1" GridPane.columnIndex="0"/>
    <TextField fx:id="userNameField" GridPane.rowIndex="1" GridPane.columnIndex="1" GridPane.columnSpan="2"/>

    <!-- Тип лиценз -->
    <Label text="Тип:" GridPane.rowIndex="2" GridPane.columnIndex="0"/>
    <ComboBox fx:id="licenseCombo" GridPane.rowIndex="2" GridPane.columnIndex="1" prefWidth="150"/>

    <!-- Дата -->
    <Label text="Валиден от:" GridPane.rowIndex="3" GridPane.columnIndex="0"/>
    <DatePicker fx:id="startDatePicker" GridPane.rowIndex="3" GridPane.columnIndex="1"/>

    <!-- Брой потребители (Slider) -->
    <Label text="Потребители:" GridPane.rowIndex="4" GridPane.columnIndex="0"/>
    <Slider fx:id="usersSlider" min="1" max="50" value="1" GridPane.rowIndex="4" GridPane.columnIndex="1"/>
    <Label fx:id="usersCountLabel" text="1" GridPane.rowIndex="4" GridPane.columnIndex="2"/>

    <!-- Версия (RadioButton) -->
    <Label text="Версия:" GridPane.rowIndex="5" GridPane.columnIndex="0"/>
    <HBox spacing="10" GridPane.rowIndex="5" GridPane.columnIndex="1">
        <fx:define>
            <ToggleGroup fx:id="versionGroup"/>
        </fx:define>
        <RadioButton text="Стабилна" toggleGroup="$versionGroup" selected="true"/>
        <RadioButton text="Бета" toggleGroup="$versionGroup"/>
    </HBox>

    <!-- Цвят на темата -->
    <Label text="Цвят на тема:" GridPane.rowIndex="6" GridPane.columnIndex="0"/>
    <ColorPicker fx:id="themeColorPicker" GridPane.rowIndex="6" GridPane.columnIndex="1"/>

    <!-- Условия -->
    <CheckBox fx:id="termsCheckBox" text="Приемам условията на ползване"
              GridPane.rowIndex="7" GridPane.columnIndex="0" GridPane.columnSpan="2"/>

    <!-- Прогрес -->
    <ProgressBar fx:id="progressBar" prefWidth="200" GridPane.rowIndex="8" GridPane.columnIndex="1"/>
    <ProgressIndicator fx:id="progressIndicator" GridPane.rowIndex="8" GridPane.columnIndex="2"/>

    <!-- Бутони -->
    <HBox spacing="10" alignment="CENTER_RIGHT" GridPane.rowIndex="9" GridPane.columnIndex="1" GridPane.columnSpan="2">
        <Button text="Регистрирай" onAction="#handleRegister" />
        <Button text="Изчисти" onAction="#handleClear"/>
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
        Scene scene = new Scene(fxmlLoader.load(), 450, 550);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
}
```

### 5. Стартирайте приложението
