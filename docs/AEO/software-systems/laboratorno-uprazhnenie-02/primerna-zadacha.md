---
layout: default
title: Sample Task
parent: Laboratory Exercise 2
grand_parent: Software Systems
nav_order: 1
---

# Sample Task: Creating a "Dashboard" View with FXML and Various Layout Containers

We will create a simple "Dashboard" view that demonstrates how different Layout Containers can be combined to build a complex interface. Each section of the dashboard will use a different Layout Container.

### 1. Create a new JavaFX project in IntelliJ IDEA

### 2. Create a new package and classes

Create a new package `bg.tu_varna.sit.ps.sample_task`. Inside it, create the classes `DashboardApplication`, `Dashboard`, and `Launcher`.

- **`src/main/java/bg/tu_varna/sit/ps/sample_task/DashboardApplication.java`** (Main Class):
  This class will start the application and load our FXML file.

  ```java
  package bg.tu_varna.sit.ps.sample_task;

  import javafx.application.Application;
  import javafx.fxml.FXMLLoader;
  import javafx.scene.Parent;
  import javafx.scene.Scene;
  import javafx.stage.Stage;

  import java.io.IOException;

  public class DashboardApplication extends Application {
      @Override
      public void start(Stage stage) throws IOException {
          // Load our FXML file
          FXMLLoader fxmlLoader = new FXMLLoader(DashboardApplication.class.getResource("dashboard-view.fxml"));

          Parent root = fxmlLoader.load();
          new Dashboard(root);

          Scene scene = new Scene(root, 900, 650);
          stage.setTitle("Dashboard Application"); // Window title
          stage.setScene(scene); // Set the scene graph for the window
          stage.show(); // Show the window
      }
  }
  ```

- **`src/main/java/bg/tu_varna/sit/ps/sample_task/Dashboard.java`**
  This class will serve to add interactivity to our FXML file. At the moment, we won't add logic, but it is necessary for linking the FXML with the Java code.

  ```java
  package bg.tu_varna.sit.ps.sample_task;

  import javafx.scene.Parent;
  import javafx.scene.control.Label;

  public class Dashboard {
      private final Label headerLabel;
      private final Label navigationLabel;

      public Dashboard(Parent root) {
          headerLabel = (Label) root.lookup("#headerLabel");
          navigationLabel = (Label) root.lookup("#navigationLabel");

          init();
      }

      private void init() {
          headerLabel.setText("Dashboard");
          navigationLabel.setText("Home > Dashboard");
      }
  }
  ```

- **`src/main/java/bg/tu_varna/sit/ps/sample_task/Launcher.java`** (Startup Class):
  This class contains the main method to start the application.

  ```java
  package bg.tu_varna.sit.ps.sample_task;

  import javafx.application.Application;

  public class Launcher {
      public static void main(String[] args) {
          Application.launch(DashboardApplication.class, args);
      }
  }
  ```

### 3. Create the FXML file for the Dashboard view

Create a new directory `resources/bg/tu_varna/sit/ps/sample_task/` and inside it create an FXML file named `dashboard-view.fxml`.

- **`src/main/resources/bg/tu_varna/sit/ps/sample_task/dashboard-view.fxml`** (FXML file):
  This is where we define our user interface using a combination of Layout Containers.

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>

  <?import javafx.scene.control.*?>
  <?import javafx.scene.layout.*?>
  <?import javafx.scene.text.Font?>

  <BorderPane maxHeight="-Infinity" maxWidth="-Infinity" minHeight="-Infinity" minWidth="-Infinity" prefHeight="600.0"
              prefWidth="850.0" xmlns="http://javafx.com/javafx/21" xmlns:fx="http://javafx.com/fxml/1">
      <top>
          <HBox alignment="CENTER" prefHeight="50.0" BorderPane.alignment="CENTER">
              <Label text="Dashboard Header" fx:id="headerLabel">
                  <font>
                      <Font name="System Bold" size="24.0"/>
                  </font>
              </Label>
          </HBox>
      </top>
      <left>
          <VBox prefWidth="150.0" spacing="10.0" style="-fx-background-color: #E0E0E0; -fx-padding: 10;"
              BorderPane.alignment="CENTER">
              <Label text="Navigation" fx:id="navigationLabel"/>
              <Button maxWidth="1.7976931348623157E308" text="Section 1"/>
              <Button maxWidth="1.7976931348623157E308" text="Section 2"/>
              <ToggleButton fx:id="statusToggleButton" maxWidth="1.7976931348623157E308" text="Status"/>
              <Region VBox.vgrow="ALWAYS"/>
              <Button maxWidth="1.7976931348623157E308" text="Settings"/>
          </VBox>
      </left>
      <center>
          <StackPane BorderPane.alignment="CENTER" style="-fx-padding: 10;">
              <!-- Welcome VBox -->
              <VBox alignment="CENTER" spacing="20.0" visible="true"> <!-- visible="true" by default -->
                  <Label text="Welcome to your Dashboard!"/>
                  <Label text="Select a section from the left."/>
              </VBox>
          </StackPane>
      </center>
      <bottom>
          <HBox alignment="CENTER_LEFT" prefHeight="30.0" style="-fx-background-color: #F0F0F0; -fx-padding: 5;"
              BorderPane.alignment="CENTER">
              <Label text="Status: Ready"/>
              <Region HBox.hgrow="ALWAYS"/>
              <Label text="Version 1.0"/>
          </HBox>
      </bottom>
      <right>
          <FlowPane hgap="5.0" vgap="5.0" prefWidth="150.0" orientation="VERTICAL"
                  style="-fx-background-color: #D0D0D0; -fx-padding: 10;" BorderPane.alignment="CENTER">
              <Label text="Quick Links"/>
              <Button text="Link A"/>
              <Button text="Link B"/>
              <Button text="Link C"/>
              <Button text="Link D"/>
              <Button text="Link E"/>
          </FlowPane>
      </right>
  </BorderPane>
  ```

**Explanations of the FXML file:**

- **`BorderPane` (root):** We use `BorderPane` as the main Layout Container, as it is ideal for the general structure of an application with a header (top), footer (bottom), side navigation (left/right), and central content.
  - The `top` region contains an `HBox` for the title.
  - The `left` region contains a `VBox` for the navigation menu.
  - The `center` region contains a `StackPane` for the main content (currently just a greeting).
  - The `bottom` region contains another `HBox` for the status bar.
  - The `right` region contains a `FlowPane` for quick links.
- **`HBox` (Horizontal Box):** Used in `top` and `bottom` for horizontal arrangement of elements. Note `alignment="CENTER"` and `alignment="CENTER_LEFT"`. `Region HBox.hgrow="ALWAYS"` is used as a separator that occupies all remaining space, pushing other elements to the edges.
- **`VBox` (Vertical Box):** Used in `left` for vertical arrangement of navigation buttons. `spacing="10.0"` adds a 10px distance between buttons. `Region VBox.vgrow="ALWAYS"` is used to push the "Settings" button to the very bottom.
- **`StackPane` (Stacking Container):** Used in `center`. Currently it has only one `VBox` in it, but if we add more elements, they will stack on top of each other in the center.
- **`FlowPane` (Flow Container):** Used in the `right` region for "Quick Links". With `orientation="VERTICAL"`, the buttons are arranged vertically and "overflow" to a new column if there isn't enough space. `hgap` and `vgap` set the distances between elements.

### 4. Adding `GridPane` and `AnchorPane` to the center of the `StackPane`

To demonstrate the other Layout Containers, we will change the content of the `StackPane` in the central region. Imagine we want to show two different sections: one with a form (GridPane) and one with information (AnchorPane), which could be swapped. For the purposes of this exercise, we will make them visible simultaneously, but they will reside within the `StackPane`.

You can replace the current `VBox` in the `StackPane` with the following code:

```xml
<!-- Welcome VBox -->
<VBox alignment="CENTER" spacing="20.0" visible="true"> <!-- visible="true" by default -->
    <Label text="Welcome to your Dashboard!"/>
    <Label text="Select a section from the left."/>
</VBox>

<!-- Example GridPane: User Data Form -->
<GridPane alignment="CENTER" hgap="10.0" vgap="10.0" style="-fx-background-color: #FFFFFF; -fx-padding: 20;"
            StackPane.alignment="CENTER"
            visible="false"> <!-- Change to visible="true" to see it -->
    <columnConstraints>
        <ColumnConstraints minWidth="100.0" prefWidth="120.0"/>
        <ColumnConstraints minWidth="200.0" prefWidth="250.0"/>
    </columnConstraints>

    <Label text="User Registration" GridPane.columnSpan="2"
            style="-fx-font-weight: bold; -fx-font-size: 16px;"/>

    <Label text="Name:" GridPane.rowIndex="1"/>
    <TextField promptText="Enter name" GridPane.columnIndex="1" GridPane.rowIndex="1"/>

    <Label text="Password:" GridPane.rowIndex="2"/>
    <PasswordField promptText="Enter password" GridPane.columnIndex="1" GridPane.rowIndex="2"/>

    <Label text="Profile Type:" GridPane.rowIndex="3"/>
    <HBox spacing="10.0" alignment="CENTER_LEFT" GridPane.columnIndex="1" GridPane.rowIndex="3">
        <fx:define>
            <ToggleGroup fx:id="accountGroup"/>
        </fx:define>
        <RadioButton fx:id="personalRadio" text="Personal" toggleGroup="$accountGroup"/>
        <RadioButton text="Business" toggleGroup="$accountGroup"/>
    </HBox>

    <Label text="Subscription:" GridPane.rowIndex="4"/>
    <CheckBox fx:id="newsletterCheckBox" text="Subscribe to newsletter" GridPane.columnIndex="1"
                GridPane.rowIndex="4"/>

    <Label text="Notes:" GridPane.rowIndex="5" GridPane.valignment="TOP"/>
    <TextArea prefHeight="80.0" promptText="Enter additional info..." wrapText="true"
                GridPane.columnIndex="1" GridPane.rowIndex="5"/>

    <Button maxWidth="Infinity" text="Submit" GridPane.columnIndex="1" GridPane.rowIndex="6"
            defaultButton="true"/>
</GridPane>

<!-- Example AnchorPane: Detailed Information -->
<AnchorPane style="-fx-background-color: #F8F8F8; -fx-padding: 20;" StackPane.alignment="CENTER"
            visible="false"> <!-- Change to visible="true" to see it -->
    <Label layoutX="14.0" layoutY="14.0" text="Detailed Information" AnchorPane.leftAnchor="10.0"
            AnchorPane.topAnchor="10.0">
        <font>
            <Font name="System Bold" size="18.0"/>
        </font>
    </Label>
    <TextArea editable="false" layoutX="14.0" layoutY="40.0" prefHeight="150.0" prefWidth="300.0"
                text="This is a detailed information section. It can contain various types of content, images, or longer texts."
                wrapText="true" AnchorPane.bottomAnchor="50.0" AnchorPane.leftAnchor="10.0"
                AnchorPane.rightAnchor="10.0" AnchorPane.topAnchor="50.0"/>
    <Button layoutX="273.0" layoutY="210.0" mnemonicParsing="false" text="Close"
            AnchorPane.bottomAnchor="10.0" AnchorPane.rightAnchor="10.0"/>
</AnchorPane>
```

**Explanations for the added graphical elements:**

- **`GridPane` (Grid Container):**
  - `columnConstraints` (column constraints) and `rowConstraints` (row constraints) are defined to manage column width and row height.
  - Each control is positioned using `GridPane.columnIndex` and `GridPane.rowIndex`.
  - `GridPane.columnSpan` allows the "Submit" button to occupy two columns.
- **`AnchorPane` (Anchoring Container):**
  - Uses `AnchorPane.topAnchor`, `AnchorPane.bottomAnchor`, `AnchorPane.leftAnchor`, `AnchorPane.rightAnchor` to anchor controls to the sides of the container. This ensures adaptive positioning relative to the size of the `AnchorPane`.
  - The `Label` is anchored top-left, the `TextArea` is anchored on all sides (with an offset), and the `Button` is anchored bottom-right.
- **`visible="false"`:** In the example, `visible="false"` is set for the `GridPane` and `AnchorPane` so that only the greeting is visible initially. To see them, change to `visible="true"` for the respective control. In a real application, this would be managed by Java code based on navigation selection.

### 5. Running the Application

Run `Launcher.java` (Right click -> `Run 'Launcher.main()'`). You should see the application window with all defined Layout Containers. Experiment with the window size to see how the Layout Containers react to changes.
