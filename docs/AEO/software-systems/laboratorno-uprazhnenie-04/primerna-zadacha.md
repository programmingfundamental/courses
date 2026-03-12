---
layout: default
title: Sample Task
parent: Laboratory Exercise 4
grand_parent: Software Systems
nav_order: 5
---

# Example Task

Create a JavaFX application called “Registration Form” that contains:

- a username field (TextField);
- a password field (PasswordField);
- a “Register” button (Button);
- a status text field (Label).

The application should:

- validate the data in real time;
- enable the button only when the data is valid.

<table>
    <tr>
        <td><b>1. Implementation Using the Imperative Model</b></td>
        <td><b>2. Implementation Using the Reactive Model</b></td>
    </tr>

    <tr>
        <td><b>1.1 Application Structure</b></td>
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
            In the imperative approach:
            <ul>
                <li>the logic is executed only when an event occurs;</li>
                <li>the validation is performed manually;</li>
                <li>there is no automatic reaction when the data changes.</li>
            </ul>
        </td>
        <td>
            In the reactive approach:
            <ul>
                <li>JavaFX Properties are used;</li>
                <li>reacts in real time;</li>
                <li>automatically manages the UI through binding.</li>
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
                statusLabel.setText("The data is valid.");
            } else {
                statusLabel.setText("Invalid data");
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
                statusLabel.setText("Invalid data");
            } else {
                statusLabel.setText("The data is valid");
            }
        });

        /* ===== EventHandler ===== */
        registerButton.setOnAction(this::handleRegister);

        /* ===== addEventHandler ===== */
        registerButton.addEventHandler(
            ActionEvent.ACTION,
            e -&gt; System.out.println("Processed ActionEvent")
        );
    }

    private void handleRegister(ActionEvent event) {
        statusLabel.setText("Registration successful!");
    }
}</code></pre>
        </td>
    </tr>

    <tr>
        <td>
            <ul>
                <li>Validation occurs only when the button is pressed</li>
                <li>The UI does not react while typing</li>
                <li>Automatic synchronization is missing</li>
                <li>This approach is typical of the imperative model</li>
            </ul>
        </td>
        <td>
            <ul>
                <li>Validation occurs in real time</li>
                <li>The UI reacts to every change</li>
                <li>There is automatic synchronization through binding</li>
                <li>This approach is typical of the reactive model</li>
            </ul>
        </td>
    </tr>

    <tr>
        <td colspan="2"><b>4. FXML file – user interface – registration-view.fxml</b></td>
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
            text="Registration"/&gt;

    &lt;Label fx:id="statusLabel"
           text="Enter data"/&gt;

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
            text="Registration"/&gt;

    &lt;Label fx:id="statusLabel"
           text="Enter data"/&gt;

&lt;/VBox&gt;</code></pre>
        </td>
    </tr>

    <tr>
        <td colspan="2">
            <ul>
                <li>FXML describes only the UI, without logic.</li>
                <li>fx:id provides a connection to the controller</li>
            </ul>
        </td>
    </tr>

    <tr>
        <td colspan="2"><b>5. Application class</b></td>
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
        <td colspan="2"><b>6. Launcher class</b></td>
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


### 6. Comparison of FXML: Imperative vs Reactive

| Criterion             | Imperative     | Reactive |
| --------------------- | -------------- | -------- |
| Reaction during input | No             | Yes      |
| Properties            | No             | Yes      |
| Binding               | No             | Yes      |
| Listeners             | Limited        | Active   |
| Maintenance           | More difficult | Easier   |



