---
layout: default
title: Примерна задача
parent: Лабораторно упражнение 8
grand_parent: Програмни системи
nav_order: 5
---

Примерна задача

Да се разшири приложението за управление на данни за софтуерни разработчици, така че да поддържа работа с ComboBox, ListView, TableView, MenuBar, ContextMenu, FXML диалогови прозорци и Alert.

Приложението трябва да реализира следните функционалности:

- Главно меню (MenuBar) със следните команди:
    - Нов – изчиства формата;
    - Изтрий избран – изтрива избрания разработчик от таблицата;
    - Изход – затваря приложението след потвърждение;
- За програмата – отваря FXML диалогов прозорец с надпис: "Система за управление на ИТ екип" и "Автор Име Фалимия".
- Контекстно меню (ContextMenu) към TableView, което съдържа:
    - Изтриване – премахва избрания разработчик след потвърждение.
- Използване на стандартни диалогови прозорци (Alert) за:
    - грешки;
    - потвърждение на действия.

Стъпка 1: Съобщенията за успех и грешка да се извеждат в Alert.

В контролера DeveloperController трябва да се добави метод  `showAlert`, който ще бъде помощен за създаване на Alert.

```java
    private Optional<ButtonType> showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert.showAndWait();
    }
```

На всички места в DeveloperControllerкъдето се ползва метода `displayMessage` , трябва да се смени с използването на метода `showAlert` 

В метода `displayMessage` при създаване или при грешка

```java
       @FXML
    protected void handleAddDeveloper() {
        // Извличане на данни от формата
        String name = nameField.getText();
        Level level = levelCombo.getValue();
        // Извличане на всички избрани елементи от ListView и конкатенация в общ String
        List<Technology> selectedTechs = techListView.getSelectionModel().getSelectedItems();

        try {
            createDeveloper(name, level, selectedTechs);
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Успешно действие", null, "Успешно добавен!");
        } catch (IllegalArgumentException exception) {
            showAlert(Alert.AlertType.ERROR, "Грешка","Грешка по време на създаване", exception.getMessage());
        }
    }
```

В метода `createDeveloper` при възникнало изключение

```java
    private boolean createDeveloper(String name, Level level, List<Technology> selectedTechs) {
        try {
            Developer newDev = new Developer(name, level, selectedTechs);
            return developersData.add(newDev);
        } catch (IllegalArgumentException exception) {
            throw exception;
        }
    }
```

Стъпка 2: Добавяне на новите графични елементи

В `developer-view.fxml` трябва да се промени основния изглвд от `HBox` на `BorderPane`, за да може да се позиционира основното меню в прозореца.

```xml
<BorderPane xmlns:fx="http://javafx.com/fxml"
            fx:controller="bg.tu_varna.sit.ps.lab8.task1.controllers.DeveloperController"
            stylesheets="@css/styles.css"
            prefWidth="950.0"
            prefHeight="600.0">

</BorderPane>
```

В `center` секцията преместваме изгледа от 7 упражнение:

```xml
<BorderPane xmlns:fx="http://javafx.com/fxml"
            fx:controller="bg.tu_varna.sit.ps.lab8.task1.controllers.DeveloperController"
            stylesheets="@css/styles.css"
            prefWidth="950.0"
            prefHeight="600.0">

    <center>
        <HBox spacing="20" stylesheets="@css/styles.css" styleClass="main-container">

            <!-- Ляв панел: Форма за добавяне (асоциирана с CSS клас form-pane) -->
            <VBox spacing="10" prefWidth="250" styleClass="form-pane">
                <Label text="Нов служител" id="form-title"/>

                <Label text="Име и фамилия:"/>
                <TextField fx:id="nameField" promptText="Напр. Иван Иванов"/>

                <Label text="Ниво (Позиция):"/>
                <ComboBox fx:id="levelCombo" maxWidth="150"/>

                <Label text="Умения (задръжте Ctrl):"/>
                <ListView fx:id="techListView" prefHeight="150"/>

                <Button fx:id="addButton" text="Добави в екипа" onAction="#handleAddDeveloper"
                        maxWidth="150" id="add-btn"/>
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
    </center>

</BorderPane>
```

Към `top` секцията домавяме основното меню:

```xml
<BorderPane xmlns:fx="http://javafx.com/fxml"
            fx:controller="bg.tu_varna.sit.ps.lab8.task1.controllers.DeveloperController"
            stylesheets="@css/styles.css"
            prefWidth="950.0"
            prefHeight="600.0">

    <top>
        <MenuBar>
            <Menu text="Файл">
                <MenuItem text="Нов" onAction="#handleNew"/>
                <MenuItem text="Изтрий избран" onAction="#handleDeleteSelected"/>
                <MenuItem text="Изход" onAction="#handleExit"/>
            </Menu>
            <Menu text="Помощ">
                <MenuItem text="За програмата" onAction="#handleAboutDialog"/>
            </Menu>
        </MenuBar>
    </top>

        <center>
        <HBox spacing="20" stylesheets="@css/styles.css" styleClass="main-container">

            <!-- Ляв панел: Форма за добавяне (асоциирана с CSS клас form-pane) -->
            <VBox spacing="10" prefWidth="250" styleClass="form-pane">
                <Label text="Нов служител" id="form-title"/>

                <Label text="Име и фамилия:"/>
                <TextField fx:id="nameField" promptText="Напр. Иван Иванов"/>

                <Label text="Ниво (Позиция):"/>
                <ComboBox fx:id="levelCombo" maxWidth="150"/>

                <Label text="Умения (задръжте Ctrl):"/>
                <ListView fx:id="techListView" prefHeight="150"/>

                <Button fx:id="addButton" text="Добави в екипа" onAction="#handleAddDeveloper"
                        maxWidth="150" id="add-btn"/>
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
    </center>

</BorderPane>
```

Контекстното меню трябва да е вградено в `TableView` това става по следняи начин:

```xml
<BorderPane xmlns:fx="http://javafx.com/fxml"
            fx:controller="bg.tu_varna.sit.ps.lab8.task1.controllers.DeveloperController"
            stylesheets="@css/styles.css"
            prefWidth="950.0"
            prefHeight="600.0">

    <top>
        <MenuBar>
            <Menu text="Файл">
                <MenuItem text="Нов" onAction="#handleNew"/>
                <MenuItem text="Изтрий избран" onAction="#handleDeleteSelected"/>
                <SeparatorMenuItem/>
                <MenuItem text="Изход" onAction="#handleExit"/>
            </Menu>
            <Menu text="Помощ">
                <MenuItem text="За програмата" onAction="#handleAboutDialog"/>
            </Menu>
        </MenuBar>
    </top>

    <center>
        <HBox spacing="20" stylesheets="@css/styles.css" styleClass="main-container">

            <!-- Ляв панел: Форма за добавяне (асоциирана с CSS клас form-pane) -->
            <VBox spacing="10" prefWidth="250" styleClass="form-pane">
                <Label text="Нов служител" id="form-title"/>

                <Label text="Име и фамилия:"/>
                <TextField fx:id="nameField" promptText="Напр. Иван Иванов"/>

                <Label text="Ниво (Позиция):"/>
                <ComboBox fx:id="levelCombo" maxWidth="150"/>

                <Label text="Умения (задръжте Ctrl):"/>
                <ListView fx:id="techListView" prefHeight="150"/>

                <Button fx:id="addButton" text="Добави в екипа" onAction="#handleAddDeveloper"
                        maxWidth="150" id="add-btn"/>
            </VBox>

            <!-- Десен панел: Таблица -->
            <VBox HBox.hgrow="ALWAYS" spacing="10">
                <Label text="Списък на екипа"/>

                <TableView fx:id="developerTable" VBox.vgrow="ALWAYS">
                    <contextMenu>
                        <ContextMenu>
                            <MenuItem text="Изтрий" onAction="#handleDeleteSelected"/>
                        </ContextMenu>
                    </contextMenu>
                    <columns>
                        <TableColumn fx:id="colName" text="Служител" prefWidth="150.0"/>
                        <TableColumn fx:id="colLevel" text="Ниво" prefWidth="100.0"/>
                        <TableColumn fx:id="colSkills" text="Технологии" prefWidth="240.0"/>
                    </columns>
                </TableView>
            </VBox>
        </HBox>
    </center>

</BorderPane>
```

Стъпка 3: Към контролера трябва да се добавят новите методи за управление на потребителския интерфейс и полета за новите елементи:

```java

public class DeveloperController {

    // --- Елементи от формата за въвеждане ---
    @FXML
    private TextField nameField;
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

        try {
            createDeveloper(name, level, selectedTechs);
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Успешно действие", null, "Успешно добавен!");
        } catch (IllegalArgumentException exception) {
            showAlert(Alert.AlertType.ERROR, "Грешка","Грешка по време на създаване", exception.getMessage());
        }
    }

    private boolean createDeveloper(String name, Level level, List<Technology> selectedTechs) {
        try {
            Developer newDev = new Developer(name, level, selectedTechs);
            return developersData.add(newDev);
        } catch (IllegalArgumentException exception) {
            throw exception;
        }
    }

    private void clearForm() {
        nameField.clear();
        techListView.getSelectionModel().clearSelection();
        levelCombo.getSelectionModel().clearSelection();
    }

    public void handleNew() {

    }

    public void handleExit() {

    }

    public void handleAboutDialog() {

    }

    private Optional<ButtonType> showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert.showAndWait();
    }
}

```

Стъпка 4: Метода `handleNew` трябва да имплементира почистване на полетата.
С метода `clearForm` се пчиства формата завъвеждане. за да се премахне селекцията на `TableView` се ползва метода `clearSelection`

```java
    public void handleNew(ActionEvent actionEvent) {
        clearForm();
        developerTable.getSelectionModel().clearSelection();
    }
```

Стъпка 5: Метода `handleExit` трябва да имплементира затваряне на приложението. Това се прави като се вземе декора от графичен елемент и се затвори прозореца свързан с този декор.
```java
            Stage stage = (Stage) developerTable.getScene().getWindow();
            stage.close();
```

Пълната реализация на метода:
Преди да се затори програмата ще се покаже `Alert`, с който да се потвърди затварянето. 
```java
    public void handleExit(ActionEvent actionEvent) {
        Optional<ButtonType> result = showAlert(Alert.AlertType.CONFIRMATION, "Изход", "Затваряне на приложението", "Сигурни ли сте, че искате да затворите приложението?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage stage = (Stage) developerTable.getScene().getWindow();
            stage.close();
        }
    }
```

Стъпка 6: Метода `handleDeleteSelected` трябва да имлементира изтриване на селектирания елемент от `TableView`
    - Първо трябва да се извлече селектирания елемнт с метода `getSelectedItem`
    - Следващата стъпка е са се провери дали е извлечен елемент
    - Ако няма селектиран елемент се извежда съобщение `Alert`
    - При наличен елемент се извежда съобщение за потвърждение с `Alert`
    - След потвърждаване се изтрива обекта от колекцията на `TableView`

```java
    public void handleDeleteSelected(ActionEvent actionEvent) {
        Developer selectedDeveloper = developerTable.getSelectionModel().getSelectedItem();
        if (selectedDeveloper == null) {
            showAlert(Alert.AlertType.WARNING, "Предупреждение",null, "Моля, изберете разработчик от таблицата.");
        } else {
            Optional<ButtonType> result =  showAlert(Alert.AlertType.WARNING, "Предупреждение", "Изтриване на разработчик", "Сигурни ли сте, че искате да изтриете избрания разработчик?");
            if (result.isPresent() && result.get() == ButtonType.OK) {
                developersData.remove(selectedDeveloper);
            }
        }
    }
```

Стъпка 7: Метода `handleAboutDialog` трябва да имплементира отварянето на диалог. 

За да се отвори нов прозорец трябва да се създаде fxml файл с оформление

`bg/tu_varna/sit/ps/lab8/task1/about-view.fxml`

```xml
<VBox alignment="CENTER"
      spacing="12"
      styleClass="main-container"
      stylesheets="@css/styles.css">

    <Label text="Система за управление на ИТ екип" styleClass="dialog-title"/>
    <Label text="Автор Име Фалимия" wrapText="true"/>

</VBox>
```

Като допълнение към стандартното отваряне на прозорец се използва `initModality` за поведението на приложението при стартиран дъщерен прозорец.

```java
    public void handleAboutDialog(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    DeveloperApplication.class.getResource("about-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("За програмата");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Грешка", "Неуспешно отваряне на прозореца.", e.getMessage());
        }
    }
```
