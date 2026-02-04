---
layout: default
title: Примерна задача
parent: Лабораторно упражнение 3
grand_parent: Програмни системи
nav_order: 1
---

# Примерна задача

Да се създаде JavaFX приложение „Форма за регистрация“, което съдържа:

- поле за потребителско име (TextField);
- поле за парола (PasswordField);
- бутон „Регистрация“ (Button);
- текстово поле за статус (Label).

Приложението трябва:

- да валидира данните в реално време;
- да активира бутона само при валидни данни.

## 1. Реализация с imperative модел

### 1.1 Структора на приложението

Приложението се състои от:

- FXML файл – описва изгледа (UI);
- Controller клас – съдържа логиката;
- JavaFX Application клас – стартира приложението.

```java
src/
 ├─ main
 │  └─ resources/
 │  │    └─ registration.fxml
 │  └─ java
 │      └─ controller/
 │          └─ RegistrationController.java
 └─ Application.java
```

### 2. FXML файл – потребителски интерфейс

#### 2.1 registration.fxml

```java
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.control.*?>
<?import javafx.scene.layout.VBox?>

<VBox spacing="10" alignment="CENTER"
      xmlns:fx="http://javafx.com/fxml"
      fx:controller="controller.RegistrationController">

    <TextField fx:id="usernameField"
               promptText="Username"/>

    <PasswordField fx:id="passwordField"
                   promptText="Password"/>

    <Button fx:id="registerButton"
            text="Регистрация"/>

    <Label fx:id="statusLabel"
           text="Въведете данни"/>

</VBox>
```

- FXML описва само UI, без логика
- fx:id осигурява връзка с controller-a
- Това подпомага MVC / MVVM архитектура

### 3. Controller – Imperative модел

#### 3.1 Идея на imperative реализацията

При imperative подхода:
- логиката се изпълнява само при събитие;
- валидирането е ръчно;
- няма автоматична реакция при промяна на данни.

#### 3.2 RegistrationController – imperative версия

```java
package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegistrationController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button registerButton;

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {

        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.length() >= 4 && password.length() >= 6) {
                statusLabel.setText("Данните са валидни");
            } else {
                statusLabel.setText("Невалидни данни");
            }
        });
    }
}
```

- Валидирането става само при натискане на бутона
- UI не реагира при писане
- Липсва автоматична синхронизация
- Подходът е типичен за imperative модел


## 2. Реализация с Reactive модел

#### 4. Controller – Reactive модел (основна версия)
4.1 Основна идея

Reactive версията:

- използва JavaFX Properties;
- реагира в реално време;
- автоматично управлява UI чрез binding.

#### 4.2 RegistrationController – reactive версия

```java
package controller;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

public class RegistrationController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button registerButton;

    @FXML
    private Label statusLabel;

    // JavaFX Properties (reactive state)
    private final BooleanProperty validUsername =
            new SimpleBooleanProperty(false);

    private final BooleanProperty validPassword =
            new SimpleBooleanProperty(false);

    @FXML
    public void initialize() {

        /* ===== Observer (ChangeListener + addListener) ===== */
        usernameField.textProperty().addListener(
            (obs, oldVal, newVal) ->
                validUsername.set(newVal.length() >= 4)
        );

        passwordField.textProperty().addListener(
            (obs, oldVal, newVal) ->
                validPassword.set(newVal.length() >= 6)
        );

        /* ===== Binding ===== */
        BooleanBinding formInvalid =
                validUsername.not().or(validPassword.not());

        registerButton.disableProperty().bind(formInvalid);

        /* ===== Observer върху Binding ===== */
        formInvalid.addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                statusLabel.setText("Невалидни данни");
            } else {
                statusLabel.setText("Данните са валидни");
            }
        });

        /* ===== EventHandler ===== */
        registerButton.setOnAction(this::handleRegister);

        /* ===== addEventHandler ===== */
        registerButton.addEventHandler(
            ActionEvent.ACTION,
            e -> System.out.println("ActionEvent обработен")
        );
    }

    private void handleRegister(ActionEvent event) {
        statusLabel.setText("Регистрацията е успешна!");
    }
}

```

### 5. Main Application клас


```java
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(
            FXMLLoader.load(
                getClass().getResource("/view/registration.fxml")
            ),
            300, 200
        );

        stage.setTitle("JavaFX FXML Reactive Example");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

```

### 6. Сравнение на FXML imperative vs reactive

| Критерий              | Imperative | Reactive |
| --------------------- | ---------- | -------- |
| Реакция при въвеждане | Не         | Да       |
| Properties            | Не         | Да       |
| Binding               | Не         | Да       |
| Listener-и            | Ограничени | Активни  |
| Поддръжка             | По-трудна  | По-лесна |


### 7. Заключение

FXML ясно демонстрира предимствата на reactive модела в JavaFX:

- UI е отделен от логиката;
- промените се отразяват автоматично;
- кодът е по-четим и разширяем.

Imperative версията е полезна за разбиране на основите, но reactive моделът е препоръчителен за реални приложения.

