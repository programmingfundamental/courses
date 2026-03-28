---
layout: default
title: Примерна задача
parent: Лабораторно упражнение 7
grand_parent: Програмни системи
nav_order: 3
---

# Примерна задача: Система за управление на ИТ екип

Ще създадем приложение, в което въвеждаме данни за софтуерни разработчици и ги визуализираме в таблица. Проектът ще демонстрира използването на `TableView`, `ComboBox`, `ListView` (с множествен избор), както и външно стилизиране със CSS.

### 1. Създайте модел на данните (Model)

Първо трябва да създадем клас, който описва един програмист. Този клас **задължително** трябва да има `public` Get-методи, за да може `TableView` да ги прочете.

Създайте клас `Developer` (`src/main/java/bg/tu_varna/sit/ps/lab7/sample_task/models/Developer.java`):

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

    // Изключително важно за PropertyValueFactory!
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

### 2. Създайте контролер класа `DeveloperController.java` (`src/main/java/bg/tu_varna/sit/ps/lab7/sample_task/controllers/DeveloperController.java`)

Контролерът ще свърже изгледа с данните. Ще инициализираме падащия списък и списъка с технологии, ще настроим колоните на таблицата и ще добавим логика за бутона.

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

    // Списъкът, който ще се показва в таблицата
    private ObservableList<Developer> developersData;

    @FXML
    public void initialize() {
        // 1. Настройка на ComboBox
        levelCombo.setItems(FXCollections.observableArrayList(
                "Junior", "Mid", "Senior", "Lead"
        ));

        // 2. Настройка на ListView
        techListView.setItems(FXCollections.observableArrayList(
                "Java", "JavaFX", "Spring Boot", "SQL", "Git", "JavaScript", "Docker"
        ));
        // Разрешаваме избор на повече от една технология (с натиснат Ctrl/Cmd)
        techListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // 3. Настройка на TableView (Свързване на колоните с класа Developer)
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colLevel.setCellValueFactory(new PropertyValueFactory<>("level"));
        colSkills.setCellValueFactory(new PropertyValueFactory<>("skills"));

        // Инициализиране на празен списък за таблицата
        developersData = FXCollections.observableArrayList();
        developerTable.setItems(developersData);
    }

    @FXML
    protected void handleAddDeveloper() {
        // Четене на данните от формата
        String name = nameField.getText();
        String level = levelCombo.getValue();

        // Вземане на всички избрани елементи от ListView и обединяването им в един String
        ObservableList<String> selectedTechs = techListView.getSelectionModel().getSelectedItems();
        String skills = String.join(", ", selectedTechs);

        // Валидация (задължителни полета)
        if (name.trim().isEmpty() || skills.isEmpty()) {
            statusLabel.setText("Моля, въведете име и изберете поне едно умение!");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Създаване на нов обект и добавяне към колекцията на таблицата
        Developer newDev = new Developer(name, level, skills);
        developersData.add(newDev);

        // Изчистване на формата за следващо въвеждане
        nameField.clear();
        techListView.getSelectionModel().clearSelection();
        levelCombo.getSelectionModel().clearSelection();

        statusLabel.setText("Успешно добавен!");
        statusLabel.setStyle("-fx-text-fill: green;");
    }
}
```

### 3. Създайте FXML файла (`team-view.fxml`)

Тук дефинираме визуалната структура. Забележете как зареждаме CSS файла чрез атрибута `stylesheets` в кореновия `HBox`.

Създайте `src/main/resources/bg/tu_varna/sit/ps/lab7/sample_task/team-view.fxml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?import javafx.geometry.Insets?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<!-- Зареждаме CSS файла. Символът @ указва, че това е ресурс в същата папка -->
<HBox spacing="20" stylesheets="@css/styles.css" xmlns:fx="http://javafx.com/fxml"
      fx:controller="bg.tu_varna.sit.ps.lab7.sample_task.controllers.DeveloperController" prefWidth="800"
      prefHeight="550">
    <padding>
        <Insets top="20" right="20" bottom="20" left="20"/>
    </padding>

    <!-- Лява част: Форма за добавяне (свързана с CSS клас form-pane) -->
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

    <!-- Дясна част: Таблица -->
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

### 4. Създайте CSS файла за стилизиране (`styles.css`)

Създайте `src/main/resources/bg/tu_varna/sit/ps/lab3/css/styles.css`:

```css
/* Типов селектор: задава глобален шрифт за цялото приложение */
.root {
  -fx-font-family: 'Segoe UI', Arial, sans-serif;
  -fx-background-color: #f4f6f8; /* Светло сив фон */
}

/* Селектор за клас: стилизираме контейнера на формата */
.form-pane {
  -fx-background-color: white;
  -fx-padding: 15;
  -fx-background-radius: 8;
  -fx-border-radius: 8;
  -fx-border-color: #e0e0e0;
  -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.05), 10, 0, 0, 2);
}

/* ID селектор: Заглавието на формата */
#form-title {
  -fx-font-size: 18px;
  -fx-font-weight: bold;
  -fx-text-fill: #2c3e50;
  -fx-padding: 0 0 10 0;
}

/* Типов селектор за всички TextField контроли */
.text-field {
  -fx-background-radius: 4;
  -fx-border-color: #cccccc;
  -fx-border-radius: 4;
  -fx-padding: 5;
}

.text-field:focused {
  -fx-border-color: #3498db;
}

/* ID селектор: Стилизиране на основния бутон */
#add-btn {
  -fx-background-color: #3498db;
  -fx-text-fill: white;
  -fx-font-weight: bold;
  -fx-background-radius: 4;
  -fx-padding: 8;
  -fx-cursor: hand;
}

/* Псевдо-клас: ефект при посочване (hover) */
#add-btn:hover {
  -fx-background-color: #2980b9;
}

/* Псевдо-клас: ефект при натискане (pressed) */
#add-btn:pressed {
  -fx-background-color: #1f618d;
}

/* Специфично стилизиране на заглавната част на таблицата */
.table-view .column-header {
  -fx-background-color: #ecf0f1;
  -fx-font-weight: bold;
}
```

### 5. Application клас `ITApplication.java` (`src/main/java/bg/tu_varna/sit/ps/lab7/sample_task/ITApplication.java`)

Основният клас, който зарежда FXML файла и стартира JavaFX прозореца.

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

### 6. Main / Launcher клас (`src/main/java/bg/tu_varna/sit/ps/lab7/sample_task/Launcher.java`)

```java
package bg.tu_varna.sit.ps.lab7.sample_task;

import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        Application.launch(ITApplication.class, args);
    }
}
```
