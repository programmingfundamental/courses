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

### 2. Създайте контролер клас `ProjectController` (`src/main/java/bg/tu_varna/sit/ps/lab5/ProjectController.java`)

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
        // 1. Работа с ComboBox
        priorityCombo.getItems().addAll("Нисък", "Среден", "Висок", "Критичен");
        priorityCombo.getSelectionModel().select(1);

        // 2. Работа с ListView и селекция
        ObservableList<String> availableModules = FXCollections.observableArrayList(
                "База данни", "Потребителски интерфейс", "Сигурност", "API интеграция", "Логиране");
        modulesListView.setItems(availableModules);
        modulesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // 3. Добавяне на ContextMenu към ListView
        ContextMenu contextMenu = new ContextMenu();
        MenuItem infoItem = new MenuItem("Виж информация за модула");
        infoItem.setOnAction(e -> {
            String selected = modulesListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showDialog(Alert.AlertType.INFORMATION, "Инфо", "Детайли за: " + selected);
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
            showDialog(Alert.AlertType.WARNING, "Внимание", "Моля, изберете поне един модул!");
            return;
        }

        // Използване на CONFIRMATION диалог
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Потвърждение");
        confirm.setHeaderText("Запис на проект");
        confirm.setContentText(String.format("Избрани модули: %d\nПриоритет: %s\nКраен срок: %s\nЖелаете ли да продължите?",
                selectedModules.size(), priorityCombo.getValue(), deadlinePicker.getValue()));

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            showDialog(Alert.AlertType.INFORMATION, "Успех", "Проектът е запазен успешно!");
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

### 3. Създайте FXML файл `project-view.fxml` (`src/main/resources/bg/tu_varna/sit/ps/lab5/project-view.fxml` )

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

    <Label text="Конфигурация на нов проект" style="-fx-font-size: 16px; -fx-font-weight: bold;"/>

    <Label text="Приоритет на проекта:"/>
    <ComboBox fx:id="priorityCombo" maxWidth="Infinity"/>

    <Label text="Избор на модули (задръжте Ctrl за многократен избор):"/>
    <ListView fx:id="modulesListView" prefHeight="120">
        <tooltip>
            <Tooltip text="Десен бутон за допълнителни опции"/>
        </tooltip>
    </ListView>

    <HBox spacing="10">
        <VBox HBox.hgrow="ALWAYS">
            <Label text="Краен срок:"/>
            <DatePicker fx:id="deadlinePicker" maxWidth="Infinity"/>
        </VBox>
        <VBox>
            <Label text="Цвят на проекта:"/>
            <ColorPicker fx:id="projectColorPicker" maxWidth="Infinity"/>
        </VBox>
    </HBox>

    <Separator/>

    <Button text="Запази конфигурацията" onAction="#handleSaveProject"
            maxWidth="Infinity" style="-fx-padding: 10;"/>
</VBox>
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
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("project-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 450, 550);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
}
```

### 5. Стартирайте приложението
