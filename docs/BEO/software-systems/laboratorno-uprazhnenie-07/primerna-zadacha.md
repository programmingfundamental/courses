---
layout: default
title: Примерна задача
parent: Лабораторно упражнение 7
grand_parent: Програмни системи
nav_order: 3
---

# Примерна задача: Система за управление на ИТ екип

Настоящата задача обхваща реализацията на приложение за въвеждане и визуализация на данни за софтуерни разработчици. Проектът демонстрира практическото използване на `TableView`, `ComboBox`, `ListView` (с настроен множествен избор), както и външно стилизиране чрез CSS.

### 1. Дефиниране на модел на данните (Model)

Началният етап изисква дефинирането на клас, описващ същността на един програмист. Задължително условие за този клас е наличието на `public` Get-методи, осигуряващи достъп до данните от страна на компонента `TableView`.

Дефиниране на клас `Developer` (`src/main/java/bg/tu_varna/sit/ps/lab7/sample_task/models/Developer.java`):

```java
package bg.tu_varna.sit.ps.lab7.sample_task.models;

public class Developer {
    private String name;
    private String level;
    private String skills;

    public Developer(String name, String level, String skills) {
        this.name = name;
        this.level = level;
        this.skills = skills;
    }

    // Наличието на Get методи е задължително за работата на PropertyValueFactory
    public String getName() {
        return name;
    }

    public String getLevel() {
        return level;
    }

    public String getSkills() {
        return skills;
    }
}
```

### 2. Дефиниране на контролер клас `DeveloperController.java` (`src/main/java/bg/tu_varna/sit/ps/lab7/sample_task/controllers/DeveloperController.java`)

Основната функция на контролера е осъществяването на връзка между изгледа и данните. В него се извършва инициализацията на падащия списък и списъка с технологии, конфигурирането на колоните на таблицата и имплементацията на логиката за обработка на събития от бутоните.

```java
package bg.tu_varna.sit.ps.lab7.sample_task.controllers;

import bg.tu_varna.sit.ps.lab7.sample_task.models.Developer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class DeveloperController {

    // --- Елементи от формата за въвеждане ---
    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<String> levelCombo;
    @FXML
    private ListView<String> techListView;
    @FXML
    private Button addButton;
    @FXML
    private Label statusLabel;

    // --- Елементи на таблицата ---
    @FXML
    private TableView<Developer> developerTable;
    @FXML
    private TableColumn<Developer, String> colName;
    @FXML
    private TableColumn<Developer, String> colLevel;
    @FXML
    private TableColumn<Developer, String> colSkills;

    // Списък за визуализация в таблицата
    private ObservableList<Developer> developersData;

    @FXML
    public void initialize() {
        // 1. Конфигуриране на ComboBox
        levelCombo.setItems(FXCollections.observableArrayList(
                "Junior", "Mid", "Senior", "Lead"
        ));

        // 2. Конфигуриране на ListView
        techListView.setItems(FXCollections.observableArrayList(
                "Java", "JavaFX", "Spring Boot", "SQL", "Git", "JavaScript", "Docker"
        ));
        // Разрешаване на множествен избор на технологии (чрез клавиш Ctrl/Cmd)
        techListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // 3. Конфигуриране на TableView (Свързване на колоните с класа Developer)
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colLevel.setCellValueFactory(new PropertyValueFactory<>("level"));
        colSkills.setCellValueFactory(new PropertyValueFactory<>("skills"));

        // Инициализиране на празен списък към таблицата
        developersData = FXCollections.observableArrayList();
        developerTable.setItems(developersData);
    }

    @FXML
    protected void handleAddDeveloper() {
        // Извличане на данни от формата
        String name = nameField.getText();
        String level = levelCombo.getValue();

        // Извличане на всички избрани елементи от ListView и конкатенация в общ String
        ObservableList<String> selectedTechs = techListView.getSelectionModel().getSelectedItems();
        String skills = String.join(", ", selectedTechs);

        // Валидация на задължителни полета
        if (name.trim().isEmpty() || skills.isEmpty() || level == null) {
            statusLabel.setText("Моля, въведете име и изберете поне едно умение!");
            // Динамично прилагане на inline стил за грешка
            statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            return;
        }

        // Създаване на обект и добавяне към колекцията на таблицата
        Developer newDev = new Developer(name, level, skills);
        developersData.add(newDev);

        // Изчистване на формата за последващо въвеждане
        nameField.clear();
        techListView.getSelectionModel().clearSelection();
        levelCombo.getSelectionModel().clearSelection();

        statusLabel.setText("Успешно добавен!");
        // Динамично прилагане на inline стил за успех
        statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
    }
}
```

### 3. Дефиниране на изглед чрез FXML (`team-view.fxml`)

В този файл се дефинира визуалната структура на приложението. Зареждането на CSS файла се осъществява чрез атрибута `stylesheets` в кореновия `HBox` елемент.

Дефиниране на `src/main/resources/bg/tu_varna/sit/ps/lab7/sample_task/team-view.fxml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<!-- Зареждане на CSS файл. Символът @ указва наличие на ресурс в съответната директория -->
<HBox spacing="20" stylesheets="@css/styles.css" styleClass="main-container" xmlns:fx="http://javafx.com/fxml"
      fx:controller="bg.tu_varna.sit.ps.lab7.sample_task.controllers.DeveloperController" prefWidth="800"
      prefHeight="550">

    <!-- Ляв панел: Форма за добавяне (асоциирана с CSS клас form-pane) -->
    <VBox spacing="10" prefWidth="250" styleClass="form-pane">
        <Label text="Нов служител" id="form-title"/>

        <Label text="Име и фамилия:"/>
        <TextField fx:id="nameField" promptText="Напр. Иван Иванов"/>

        <Label text="Ниво (Позиция):"/>
        <ComboBox fx:id="levelCombo" maxWidth="Infinity"/>

        <Label text="Умения (задръжте Ctrl):"/>
        <ListView fx:id="techListView" prefHeight="150"/>

        <Button fx:id="addButton" text="Добави в екипа" onAction="#handleAddDeveloper"
                maxWidth="Infinity" id="add-btn"/>

        <Label fx:id="statusLabel" wrapText="true"/>
    </VBox>

    <!-- Десен панел: Таблица -->
    <VBox HBox.hgrow="ALWAYS" spacing="10">
        <Label text="Списък на екипа" style="-fx-font-size: 16px; -fx-font-weight: bold;"/>

        <TableView fx:id="developerTable" VBox.vgrow="ALWAYS">
            <columns>
                <TableColumn fx:id="colName" text="Служител" prefWidth="150.0"/>
                <TableColumn fx:id="colLevel" text="Ниво" prefWidth="100.0"/>
                <TableColumn fx:id="colSkills" text="Технологии" prefWidth="240.0"/>
            </columns>
        </TableView>
    </VBox>
</HBox>
```

### 4. Дефиниране на CSS файл за стилизиране (`styles.css`)

Дефиниране на `src/main/resources/bg/tu_varna/sit/ps/lab7/sample_task/css/styles.css`:

```css
/* Типов селектор: дефиниране на глобален шрифт за приложението */
.root {
  -fx-font-family: 'Segoe UI', Arial, sans-serif;
  -fx-background-color: #f4f6f8; /* Светлосив фон */
}

/* Основен контейнер: външно отстояние */
.main-container {
  -fx-padding: 20;
}

/* Селектор за клас: стилизиране на контейнера на формата */
.form-pane {
  -fx-background-color: white;
  -fx-padding: 15;
  -fx-border-color: #e0e0e0;
  -fx-border-width: 1;
  -fx-border-radius: 8;
}

/* ID селектор: Заглавие на формата */
#form-title {
  -fx-font-size: 18px;
  -fx-font-weight: bold;
  -fx-text-fill: #2c3e50;
  -fx-padding: 0 0 10 0;
}

/* Типов селектор за всички TextField компоненти */
.text-field {
  -fx-border-color: #cccccc;
  -fx-border-width: 1;
  -fx-border-radius: 4;
  -fx-padding: 5;
}

.text-field:focused {
  -fx-border-color: #3498db;
  -fx-border-width: 2;
}

/* ID селектор: Стилизиране на основния бутон */
#add-btn {
  -fx-background-color: #3498db;
  -fx-text-fill: white;
  -fx-font-weight: bold;
  -fx-padding: 8;
  -fx-border-radius: 4;
}

/* Псевдо-клас: ефект при позициониране на курсора (hover) */
#add-btn:hover {
  -fx-background-color: #2980b9;
}

/* Псевдо-клас: ефект при активиране (pressed) */
#add-btn:pressed {
  -fx-background-color: #1f618d;
}

/* Специфично стилизиране на заглавната част на таблицата */
.table-view .column-header {
  -fx-background-color: #ecf0f1;
  -fx-font-weight: bold;
}
```

### 5. Дефиниране на основен Application клас `ITApplication.java` (`src/main/java/bg/tu_varna/sit/ps/lab7/sample_task/ITApplication.java`)

Класът отговаря за зареждането на FXML файла и инициализацията на основния JavaFX прозорец.

```java
package bg.tu_varna.sit.ps.lab7.sample_task;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ITApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                ITApplication.class.getResource("team-view.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Система за управление на ИТ екип");
        stage.setScene(scene);
        stage.show();
    }
}
```

### 6. Дефиниране на стартов клас Launcher (`src/main/java/bg/tu_varna/sit/ps/lab7/sample_task/Launcher.java`)

```java
package bg.tu_varna.sit.ps.lab7.sample_task;

import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        Application.launch(ITApplication.class, args);
    }
}
```
