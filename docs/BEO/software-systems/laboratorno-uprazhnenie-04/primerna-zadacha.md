---
layout: default
title: Примерна задача
parent: Лабораторно упражнение 4
grand_parent: Програмни системи
nav_order: 5
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

<table>
    <tr>
        <td><b>1. Реализация с imperative модел</b></td>
        <td><b>2. Реализация с Reactive модел</b></td>
    </tr>

    <tr>
        <td><b>1.1 Структура на приложението</b></td>
        <td></td>
    </tr>

    <tr>
        <td width="50%">
            <pre><code>
ps-project/
└─ src/
   └─ main/
      ├─ java/
      │  └─ bg/
      │     └─ tu_varna/
      │        └─ sit/
      │           └─ ps/
      │              └─ lab4/
      │                 └─ task1/
      │                    ├─ RegistrationApplication.java
      │                    ├─ Launcher.java
      │                    └─ controller/
      │                       └─ RegistrationController.java
      └─ resources/
         └─ bg/
            └─ tu_varna/
               └─ sit/
                  └─ ps/
                     └─ lab4/
                        └─ task1/
                           └─ registration-view.fxml
            </code></pre>
        </td>

        <td width="50%">
            <pre><code>
ps-project/
└─ src/
   └─ main/
      ├─ java/
      │  └─ bg/
      │     └─ tu_varna/
      │        └─ sit/
      │           └─ ps/
      │              └─ lab4/
      │                 └─ task2/
      │                    ├─ RegistrationApplication.java
      │                    ├─ Launcher.java
      │                    └─ controller/
      │                       └─ RegistrationController.java
      └─ resources/
         └─ bg/
            └─ tu_varna/
               └─ sit/
                  └─ ps/
                     └─ lab4/
                        └─ task2/
                           └─ registration-view.fxml
            </code></pre>
        </td>
    </tr>

    <tr>
        <td colspan="2"><b>3. Controller</b></td>
    </tr>

    <tr>
        <td>
            При imperative подхода:
            <ul>
                <li>логиката се изпълнява само при събитие;</li>
                <li>валидирането е ръчно;</li>
                <li>няма автоматична реакция при промяна на данни.</li>
            </ul>
        </td>
        <td>
            При Reactive подхода:
            <ul>
                <li>използва JavaFX Properties;</li>
                <li>реагира в реално време;</li>
                <li>автоматично управлява UI чрез binding.</li>
            </ul>
        </td>
    </tr>

    <tr>
        <td>
            <pre><code class="language-java">package controller;

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

        registerButton.setOnAction(e -&gt; {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.length() &gt;= 4 &amp;&amp; password.length() &gt;= 6) {
                statusLabel.setText("Данните са валидни");
            } else {
                statusLabel.setText("Невалидни данни");
            }
        });
    }
}</code></pre>
        </td>

        <td>
            <pre><code class="language-java">package controller;

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
            (obs, oldVal, newVal) -&gt;
                validUsername.set(newVal.length() &gt;= 4)
        );

        passwordField.textProperty().addListener(
            (obs, oldVal, newVal) -&gt;
                validPassword.set(newVal.length() &gt;= 6)
        );

        /* ===== Binding ===== */
        BooleanBinding formInvalid =
                validUsername.not().or(validPassword.not());

        registerButton.disableProperty().bind(formInvalid);

        /* ===== Observer върху Binding ===== */
        formInvalid.addListener((obs, oldVal, newVal) -&gt; {
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
            e -&gt; System.out.println("ActionEvent обработен")
        );
    }

    private void handleRegister(ActionEvent event) {
        statusLabel.setText("Регистрацията е успешна!");
    }
}</code></pre>
        </td>
    </tr>

    <tr>
        <td>
            <ul>
                <li>Валидирането става само при натискане на бутона</li>
                <li>UI не реагира при писане</li>
                <li>Липсва автоматична синхронизация</li>
                <li>Подходът е типичен за imperative модел</li>
            </ul>
        </td>
        <td>
            <ul>
                <li>Валидирането става в реално време</li>
                <li>UI реагира при всяка промяна</li>
                <li>Има автоматична синхронизация чрез binding</li>
                <li>Подходът е типичен за reactive модел</li>
            </ul>
        </td>
    </tr>

    <tr>
        <td colspan="2"><b>4. FXML файл – потребителски интерфейс – registration-view.fxml</b></td>
    </tr>

    <tr>
        <td>
            <pre><code class="language-xml">&lt;?xml version="1.0" encoding="UTF-8"?&gt;

&lt;?import javafx.scene.control.*?&gt;
&lt;?import javafx.scene.layout.VBox?&gt;

&lt;VBox spacing="10" alignment="CENTER"
      xmlns:fx="http://javafx.com/fxml"
      fx:controller="bg.tu_varna.sit.ps.lab4.task1.controller.RegistrationController"&gt;

    &lt;TextField fx:id="usernameField"
               promptText="Username"/&gt;

    &lt;PasswordField fx:id="passwordField"
                   promptText="Password"/&gt;

    &lt;Button fx:id="registerButton"
            text="Регистрация"/&gt;

    &lt;Label fx:id="statusLabel"
           text="Въведете данни"/&gt;

&lt;/VBox&gt;</code></pre>
        </td>
        <td>
            <pre><code class="language-xml">&lt;?xml version="1.0" encoding="UTF-8"?&gt;

&lt;?import javafx.scene.control.*?&gt;
&lt;?import javafx.scene.layout.VBox?&gt;

&lt;VBox spacing="10" alignment="CENTER"
      xmlns:fx="http://javafx.com/fxml"
      fx:controller="bg.tu_varna.sit.ps.lab4.task2.controller.RegistrationController"&gt;

    &lt;TextField fx:id="usernameField"
               promptText="Username"/&gt;

    &lt;PasswordField fx:id="passwordField"
                   promptText="Password"/&gt;

    &lt;Button fx:id="registerButton"
            text="Регистрация"/&gt;

    &lt;Label fx:id="statusLabel"
           text="Въведете данни"/&gt;

&lt;/VBox&gt;</code></pre>
        </td>
    </tr>

    <tr>
        <td colspan="2">
            <ul>
                <li>FXML описва само UI, без логика</li>
                <li>fx:id осигурява връзка с controller-a</li>
            </ul>
        </td>
    </tr>

    <tr>
        <td colspan="2"><b>5. Application клас</b></td>
    </tr>

    <tr>
        <td>
            <pre><code class="language-java">import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RegistrationApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        
        FXMLLoader loader = new FXMLLoader(
            RegistrationApplication.class
            .getResource("registration-view.fxml")
        );

        Parent root = loader.load();

        Scene scene = new Scene(root, 300, 200);

        stage.setTitle("JavaFX FXML imperative Example");
        stage.setScene(scene);
        stage.show();
    }
}</code></pre>
        </td>
        <td colspan="2">
            <pre><code class="language-java">import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RegistrationApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        
        FXMLLoader loader = new FXMLLoader(
            RegistrationApplication.class
            .getResource("registration-view.fxml")
        );

        Parent root = loader.load();

        Scene scene = new Scene(root, 300, 200);

        stage.setTitle("JavaFX FXML Reactive Example");
        stage.setScene(scene);
        stage.show();
    }
}</code></pre>
        </td>
    </tr>

    <tr>
        <td colspan="2"><b>6. Main / Launcher клас</b></td>
    </tr>
    <tr>
<td>
<pre>
<code class="language-java">
  import javafx.application.Application;

  public class Launcher {
      public static void main(String[] args) {
          Application.launch(RegistrationApplication.class, args);
      }
  }
</code></pre>
        </td>
    </tr>
</table>


### 6. Сравнение на FXML imperative vs reactive

| Критерий              | Imperative | Reactive |
| --------------------- | ---------- | -------- |
| Реакция при въвеждане | Не         | Да       |
| Properties            | Не         | Да       |
| Binding               | Не         | Да       |
| Listener-и            | Ограничени | Активни  |
| Поддръжка             | По-трудна  | По-лесна |


