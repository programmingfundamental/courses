---
layout: default
title: Sample Task
parent: Laboratory Exercise 5
grand_parent: Software Systems
nav_order: 1
---

# Sample Task: Software License Registration System

We will create a form that collects data from the user, uses various types of controls, and displays a confirmation dialog box.

### 1. Create a new JavaFX project in IntelliJ IDEA

### 2. Create the controller class `ProjectController` (`src/main/java/bg/tu_varna/sit/ps/lab5/ProjectController.java`)

```java
package bg.tu_varna.sit.ps.lab5;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.Optional;

public class ProjectController {
    @FXML private ComboBox<String> priorityCombo;
    @FXML private ListView<String> modulesListView;
    @FXML private DatePicker deadlinePicker;
    @FXML private ColorPicker projectColorPicker;

    @FXML
    public void initialize() {
        // 1. Working with ComboBox
        priorityCombo.getItems().addAll("Low", "Medium", "High", "Critical");
        priorityCombo.getSelectionModel().select(1);

        // 2. Working with ListView and selection
        ObservableList<String> availableModules = FXCollections.observableArrayList(
                "Database", "User Interface", "Security", "API Integration", "Logging");
        modulesListView.setItems(availableModules);
        modulesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // 3. Adding a ContextMenu to the ListView
        ContextMenu contextMenu = new ContextMenu();
        MenuItem infoItem = new MenuItem("View module info");
        infoItem.setOnAction(e -> {
            String selected = modulesListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showDialog(Alert.AlertType.INFORMATION, "Info", "Details for: " + selected);
            }
        });
        contextMenu.getItems().add(infoItem);
        modulesListView.setContextMenu(contextMenu);

        deadlinePicker.setValue(LocalDate.now().plusMonths(1));
    }

    @FXML
    protected void handleSaveProject() {
        ObservableList<String> selectedModules = modulesListView.getSelectionModel().getSelectedItems();

        if (selectedModules.isEmpty()) {
            showDialog(Alert.AlertType.WARNING, "Warning", "Please select at least one module!");
            return;
        }

        // Using a CONFIRMATION dialog
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Save Project");
        confirm.setContentText(String.format("Selected modules: %d\nPriority: %s\nDeadline: %s\nDo you wish to continue?",
                selectedModules.size(), priorityCombo.getValue(), deadlinePicker.getValue()));

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            showDialog(Alert.AlertType.INFORMATION, "Success", "Project saved successfully!");
        }
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

### 3. Create the FXML file `project-view.fxml` (`src/main/resources/bg/tu_varna/sit/ps/lab5/project-view.fxml` )

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?import javafx.geometry.Insets?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<VBox spacing="10" xmlns:fx="http://javafx.com/fxml"
      fx:controller="bg.tu_varna.sit.ps.lab5.ProjectController" prefWidth="400">
    <padding>
        <Insets top="20" right="20" bottom="20" left="20"/>
    </padding>

    <Label text="New Project Configuration" style="-fx-font-size: 16px; -fx-font-weight: bold;"/>

    <Label text="Project Priority:"/>
    <ComboBox fx:id="priorityCombo" maxWidth="Infinity"/>

    <Label text="Select modules (hold Ctrl for multiple selection):"/>
    <ListView fx:id="modulesListView" prefHeight="120">
        <tooltip>
            <Tooltip text="Right click for additional options"/>
        </tooltip>
    </ListView>

    <HBox spacing="10">
        <VBox HBox.hgrow="ALWAYS">
            <Label text="Deadline:"/>
            <DatePicker fx:id="deadlinePicker" maxWidth="Infinity"/>
        </VBox>
        <VBox>
            <Label text="Project Color:"/>
            <ColorPicker fx:id="projectColorPicker" maxWidth="Infinity"/>
        </VBox>
    </HBox>

    <Separator/>

    <Button text="Save Configuration" onAction="#handleSaveProject"
            maxWidth="Infinity" style="-fx-padding: 10;"/>
</VBox>
```

### 4. Change the loaded view in the main application class `HelloApplication` (`src/main/java/bg/tu_varna/sit/ps/lab5/HelloApplication.java`)

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
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("project-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 450, 550);
        stage.setTitle("Project Management!");
        stage.setScene(scene);
        stage.show();
    }
}
```

### 5. Start the application
