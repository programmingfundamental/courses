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

Дефиниране на клас enum `Level` (`src/main/java/bg/tu_varna/sit/ps/lab7/task1/models/Level.java`):

```java
package bg.tu_varna.sit.ps.lab7.task1.models;

public enum Level {
    JUNIOR("Junior"),
    MID("Mid"),
    SENIOR("Senior"),
    LEAD("Lead");

    private final String displayName;

    Level(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
```

Дефиниране на клас enum `Technology` (`src/main/java/bg/tu_varna/sit/ps/lab7/task1/models/Technology.java`):

```java
package bg.tu_varna.sit.ps.lab7.task1.models;

public enum Technology {
    JAVA("Java"),
    JAVAFX("JavaFX"),
    SPRING_BOOT("Spring Boot"),
    SQL("SQL"),
    GIT("Git"),
    JAVASCRIPT("JavaScript"),
    DOCKER("Docker");

    private final String displayName;

    Technology(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
```

Дефиниране на клас `Developer` (`src/main/java/bg/tu_varna/sit/ps/lab7/task1/models/Developer.java`):

```java
package bg.tu_varna.sit.ps.lab7.task1.models;

import java.util.ArrayList;
import java.util.List;

public class Developer {
    private final String name;
    private final Level level;
    private final List<Technology> skills;

    public Developer(String name, Level level, List<Technology> skills) {
        if (name.trim().isEmpty() || skills.isEmpty() || level == null) {
            throw new IllegalArgumentException("Моля, въведете име и изберете поне едно умение!");
        }
        this.name = name;
        this.level = level;
        this.skills = new ArrayList<>(skills);
    }

    // Наличието на Get методи е задължително за работата на PropertyValueFactory
    public String getName() {
        return name;
    }

    public Level getLevel() {
        return level;
    }

    public List<Technology> getSkills() {
        return skills;
    }
}
```

### 2. Дефиниране на контролер клас `DeveloperController.java` (`src/main/java/bg/tu_varna/sit/ps/lab7/task1/controllers/DeveloperController.java`)

Основната функция на контролера е осъществяването на връзка между изгледа и данните. В него се извършва инициализацията на падащия списък и списъка с технологии, конфигурирането на колоните на таблицата и имплементацията на логиката за обработка на събития от бутоните.

```java
package bg.tu_varna.sit.ps.lab7.task1.controllers;

import bg.tu_varna.sit.ps.lab7.task1.models.Developer;
import bg.tu_varna.sit.ps.lab7.task1.models.Level;
import bg.tu_varna.sit.ps.lab7.task1.models.Technology;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class DeveloperController {

    // --- Елементи от формата за въвеждане ---
    @FXML
    private TextField nameField;
    @FXML
    public Label errorStatusLabel;
    @FXML
    public Label successStatusLabel;
    @FXML
    private Button addButton;

    @FXML
    private ComboBox<Level> levelCombo;
    @FXML
    private ListView<Technology> techListView;
    @FXML
    private TableView<Developer> developerTable;
    @FXML
    private TableColumn<Developer, String> colName;
    @FXML
    private TableColumn<Developer, Level> colLevel;
    @FXML
    private TableColumn<Developer, List<Technology>> colSkills;

    // Списък за визуализация в таблицата
    private final ObservableList<Developer> developersData;

    public DeveloperController() {
        developersData = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {

        initComboBox();

        initListView();

        initTableView();
    }

    //Конфигуриране на ComboBox
    private void initComboBox() {

        levelCombo.setItems(FXCollections.observableArrayList(Level.values()));
    }

    //Конфигуриране на ListView
    private void initListView() {

        techListView.setItems(FXCollections.observableArrayList(Technology.values()));
        // Разрешаване на множествен избор на технологии (чрез клавиш Ctrl/Cmd)
        techListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    // Конфигуриране на TableView (Свързване на колоните с класа Developer)
    private void initTableView() {

        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colLevel.setCellValueFactory(new PropertyValueFactory<>("level"));
        colSkills.setCellValueFactory(new PropertyValueFactory<>("skills"));

        developerTable.setItems(developersData);
    }

    @FXML
    protected void handleAddDeveloper() {
        // Извличане на данни от формата
        String name = nameField.getText();
        Level level = levelCombo.getValue();
        // Извличане на всички избрани елементи от ListView и конкатенация в общ String
        List<Technology> selectedTechs = techListView.getSelectionModel().getSelectedItems();

        boolean isCreated = createDeveloper(name, level, selectedTechs);

        if (isCreated) {
            clearForm();
            displayMessage("Успешно добавен!", false);
        } else {
            displayMessage("Моля, въведете име и изберете поне едно умение!", true);
        }
    }

    private boolean createDeveloper(String name, Level level, List<Technology> selectedTechs) {
        try {
            Developer newDev = new Developer(name, level, selectedTechs);
            return developersData.add(newDev);
        } catch (IllegalArgumentException e) {
            displayMessage(e.getMessage(), true);
        }
        return false;
    }

    private void clearForm() {
        nameField.clear();
        techListView.getSelectionModel().clearSelection();
        levelCombo.getSelectionModel().clearSelection();
    }

    private void displayMessage(String message, boolean isError) {
        errorStatusLabel.setText(message);
        errorStatusLabel.setDisable(!isError);
        successStatusLabel.setDisable(isError);
    }
}
```

### 3. Дефиниране на изглед чрез FXML (`team-view.fxml`)

В този файл се дефинира визуалната структура на приложението. Зареждането на CSS файла се осъществява чрез атрибута `stylesheets` в кореновия `HBox` елемент.

Дефиниране на `src/main/resources/bg/tu_varna/sit/ps/lab7/task1/team-view.fxml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<!-- Зареждане на CSS файл. Символът @ указва наличие на ресурс в съответната директория -->
<HBox spacing="20" styleClass="main-container" xmlns:fx="http://javafx.com/fxml"
      fx:controller="bg.tu_varna.sit.ps.lab7.task1.controllers.DeveloperController" prefWidth="800"
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

        <Label fx:id="errorStatusLabel" styleClass="error" disable="false" wrapText="true"/>
        <Label fx:id="successStatusLabel" styleClass="success" disable="false" wrapText="true"/>
    </VBox>

    <!-- Десен панел: Таблица -->
    <VBox HBox.hgrow="ALWAYS" spacing="10">
        <Label text="Списък на екипа"/>

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

Дефиниране на `src/main/resources/bg/tu_varna/sit/ps/lab7/task1/css/styles.css`:

След като е създаден css файла трябва да се добави към root тага в fxml файла:

```xml
 <HBox  stylesheets="@css/styles.css" 
  
      styleClass="main-container" 
      spacing="20" xmlns:fx="http://javafx.com/fxml"
      fx:controller="bg.tu_varna.sit.ps.lab7.task1.controllers.DeveloperController" prefWidth="800"
      prefHeight="550">

```

```css
/* Типов селектор: дефиниране на глобален шрифт за приложението */
/* Типов селектор: дефиниране на глобален шрифт за приложението */
.root {
  -fx-font-family: 'Segoe UI', Arial, sans-serif;
  -fx-background-color: #f4f6f8; /* Светлосив фон */
}

/* Основен контейнер: външно отстояние */
.main-container {
  -fx-padding: 20;
}

/* Селектор за елемент: стилизиране на Label на формата */
Label {
    -fx-font-size: 16px;
    -fx-font-weight: bold;
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
TextField {
  -fx-border-color: #cccccc;
  -fx-border-width: 1;
  -fx-border-radius: 4;
  -fx-padding: 5;
}

TextField:focused {
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
TableView ColumnHeader {
  -fx-background-color: #ecf0f1;
  -fx-font-weight: bold;
}

.error {
    -fx-text-fill: red;
}

.success {
    -fx-text-fill: green;
}

```

### 5. Дефиниране на основен Application клас `DeveloperApplication.java` (`src/main/java/bg/tu_varna/sit/ps/lab7/task1/DeveloperApplication.java`)

Класът отговаря за зареждането на FXML файла и инициализацията на основния JavaFX прозорец.

```java
package bg.tu_varna.sit.ps.lab7.task1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DeveloperApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                DeveloperApplication.class.getResource("team-view.fxml")
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
package bg.tu_varna.sit.ps.lab7.task1;

import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        Application.launch(DeveloperApplication.class, args);
    }
}
```
