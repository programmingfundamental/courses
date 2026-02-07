---
layout: default
title: Sample Task
parent: Laboratory Exercise 2
grand_parent: Software Systems
nav_order: 1
---

# Примерна задача: Създаване на "Dashboard" изглед с FXML и различни Контейнери за изглед

Ще създадем прост "Dashboard" изглед, който демонстрира как различни Контейнери за изглед могат да се комбинират за изграждане на сложен интерфейс. Всяка секция на дашборда ще използва различен Контейнер за изглед.

### 1. Създайте нов JavaFX проект в IntelliJ IDEA

### 2. Създайте нов пакет и класове

Създайте новия пакет `bg.tu_varna.sit.ps.sample_task`. В него създайте класовете `DashboardApplication`, `Dashboard` и `Launcher`.

- **`src/main/java/bg/tu_varna/sit/ps/sample_task/DashboardApplication.java`** (Основен клас):
  Този клас ще стартира приложението и ще зареди нашия FXML файл.

  ```java
  package bg.tu_varna.sit.ps.sample_task;`

  import javafx.application.Application;
  import javafx.fxml.FXMLLoader;
  import javafx.scene.Scene;
  import javafx.stage.Stage;

  import java.io.IOException;

  public class DashboardApplication extends Application {
      @Override
      public void start(Stage stage) throws IOException {
          // Зареждаме нашия FXML файл
          FXMLLoader fxmlLoader = new FXMLLoader(DashboardApplication.class.getResource("dashboard-view.fxml"));

          Parent root = fxmlLoader.load();
          new Dashboard(root);

          Scene scene = new Scene(root, 800, 600);
          stage.setTitle("Dashboard Application"); // Заглавие на прозореца
          stage.setScene(scene); // Задаваме графичната сцена на прозореца
          stage.show(); // Показваме прозореца
      }
  }
  ```

- **`src/main/java/bg/tu_varna/sit/ps/sample_task/Dashboard.java`**
  Този клас ще служи добавяне на интерактивност за нашия FXML файл. В момента няма да добавяме логика, но той е необходим за свързване на FXML с Java кода.

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

- **`src/main/java/bg/tu_varna/sit/ps/sample_task/Launcher.java`** (Клас за стартиране):
  Този клас съдържа основния метод за стартиране на приложението.

  ```java
  package bg.tu_varna.sit.ps.sample_task;

  import javafx.application.Application;

  public class Launcher {
      public static void main(String[] args) {
          Application.launch(DashboardApplication.class, args);
      }
  }
  ```

### 3. Създайте FXML файл за изгледа на дашборда

Създайте нова директория `resources/bg/tu_varna/sit/ps/sample_task/` и в нея създайте FXML файл с името `dashboard-view.fxml`.

- **`src/main/resources/bg/tu_varna/sit/ps/sample_task/dashboard-view.fxml`** (FXML файл):
  Това е мястото, където ще дефинираме нашия потребителски интерфейс, използвайки комбинация от Контейнери за изглед.

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>

  <?import javafx.scene.control.*?>
  <?import javafx.scene.layout.*?>
  <?import javafx.scene.text.Font?>

  <BorderPane maxHeight="-Infinity" maxWidth="-Infinity" minHeight="-Infinity" minWidth="-Infinity" prefHeight="600.0"
              prefWidth="800.0" xmlns="http://javafx.com/javafx/21" xmlns:fx="http://javafx.com/fxml/1">
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
              <Button maxWidth="1.7976931348623157E308" text="Section 3"/>
              <Region VBox.vgrow="ALWAYS"/> <!-- Разделител, който се разтяга -->
              <Button maxWidth="1.7976931348623157E308" text="Settings"/>
          </VBox>
      </left>
      <center>
          <StackPane BorderPane.alignment="CENTER">
              <!-- Пример за съдържание, което ще се наслагва -->
              <VBox alignment="CENTER" spacing="20.0">
                  <Label text="Welcome to your Dashboard!"/>
                  <Label text="Select a section from the left."/>
              </VBox>
          </StackPane>
      </center>
      <bottom>
          <HBox alignment="CENTER_LEFT" prefHeight="30.0" style="-fx-background-color: #F0F0F0; -fx-padding: 5;"
                BorderPane.alignment="CENTER">
              <Label text="Status: Ready"/>
              <Region HBox.hgrow="ALWAYS"/> <!-- Разделител, който се разтяга -->
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

**Обяснения на FXML файла:**

- **`BorderPane` (корен):** Използваме `BorderPane` като основен Контейнер за изглед, тъй като той е идеален за общата структура на едно приложение с хедър (горе), футър (долу), странични навигации (ляво/дясно) и централно съдържание.
  - Регионът `top` съдържа `HBox` за заглавието.
  - Регионът `left` съдържа `VBox` за навигационно меню.
  - Регионът `center` съдържа `StackPane` за основното съдържание (в момента само приветствие).
  - Регионът `bottom` съдържа друг `HBox` за статус бар.
  - Регионът `right` съдържа `FlowPane` за бързи връзки.
- **`HBox` (Хоризонтална кутия):** Използван в `top` и `bottom` за хоризонтално подреждане на елементи. Забележете `alignment="CENTER"` и `alignment="CENTER_LEFT"`. `Region HBox.hgrow="ALWAYS"` се използва като разделител, който заема цялото останало пространство, бутайки другите елементи към краищата.
- **`VBox` (Вертикална кутия):** Използван в `left` за вертикално подреждане на бутоните за навигация. `spacing="10.0"` добавя 10px разстояние между бутоните. `Region VBox.vgrow="ALWAYS"` за да бутне "Settings" бутона най-отдолу.
- **`StackPane` (Наслагващ контейнер):** Използван в `center`. В момента има само един `VBox` в него, но ако добавим още елементи, те ще се наслагват един върху друг в центъра.
- **`FlowPane` (Поточен контейнер):** Използван в `right` региона за "Quick Links". С `orientation="VERTICAL"`, бутоните се подреждат вертикално и "преливат" на нов ред, ако няма достатъчно място. `hgap` и `vgap` задават разстояния между елементите.

### 4. Добавяне на `GridPane` и `AnchorPane` към центъра на `StackPane`

За да демонстрираме и другите Контейнери за изглед, ще променим съдържанието на `StackPane` в централния регион. Представете си, че искаме да покажем две различни секции: една с форма (GridPane) и една с информация (AnchorPane), които биха могли да се сменят. За целите на това упражнение ще ги направим видими едновременно, но ще са в `StackPane`.

Можете да замените текущия `VBox` в `StackPane` със следния код:

```xml
<!-- Welcome VBox -->
<VBox alignment="CENTER" spacing="20.0" visible="true"> <!-- visible="true" по подразбиране -->
    <Label text="Welcome to your Dashboard!"/>
    <Label text="Select a section from the left."/>
</VBox>

<!-- Пример за GridPane: Форма за потребителски данни -->
<GridPane alignment="CENTER" hgap="10.0" vgap="10.0" style="-fx-background-color: #FFFFFF; -fx-padding: 20;"
          StackPane.alignment="CENTER"
          visible="false"> <!-- Променете на visible="true", за да го видите -->
    <columnConstraints>
        <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
        <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
    </columnConstraints>
    <rowConstraints>
        <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES"/>
        <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES"/>
        <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES"/>
        <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES"/>
    </rowConstraints>
    <Label text="First Name:" GridPane.columnIndex="0" GridPane.rowIndex="0"/>
    <TextField GridPane.columnIndex="1" GridPane.rowIndex="0"/>
    <Label text="Last Name:" GridPane.columnIndex="0" GridPane.rowIndex="1"/>
    <TextField GridPane.columnIndex="1" GridPane.rowIndex="1"/>
    <Label text="Email:" GridPane.columnIndex="0" GridPane.rowIndex="2"/>
    <TextField GridPane.columnIndex="1" GridPane.rowIndex="2"/>
    <Button maxWidth="1.7976931348623157E308" text="Submit" GridPane.columnSpan="2"
            GridPane.halignment="CENTER" GridPane.rowIndex="3"/>
</GridPane>

<!-- Пример за AnchorPane: Детайлна информация -->
<AnchorPane style="-fx-background-color: #F8F8F8; -fx-padding: 20;" StackPane.alignment="CENTER"
            visible="false"> <!-- Променете на visible="true", за да го видите -->
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

**Пояснения за добавените графични елементи:**

- **`GridPane` (Мрежов контейнер):**
  - Дефинирани са `columnConstraints` (ограничения за колони) и `rowConstraints` (ограничения за редове), за да управляваме ширината на колоните и височината на редовете.
  - Всяка контрола е позиционирана с `GridPane.columnIndex` и `GridPane.rowIndex`.
  - `GridPane.columnSpan` позволява на бутона "Submit" да заема две колони.
- **`AnchorPane` (Закотвящ контейнер):**
  - Използва `AnchorPane.topAnchor`, `AnchorPane.bottomAnchor`, `AnchorPane.leftAnchor`, `AnchorPane.rightAnchor` за закачане на контролите към страните на контейнера. Това осигурява адаптивно позициониране спрямо размера на `AnchorPane`.
  - `Label` е закачен горе вляво, `TextArea` е закачен от всички страни (с отстояние), а `Button` е закачен долу вдясно.
- **`visible="false"`:** В примера е зададено `visible="false"` на `GridPane` и `AnchorPane`, така че само приветствието да се вижда първоначално. За да ги видите, променете `visible="true"` за съответната контрола. В реално приложение, това би било управлявано от Java код въз основа на избор от навигацията.

### 5. Стартиране на приложението

Стартирайте `Launcher.java` (десен бутон -> `Run 'Launcher.main()'`). Трябва да видите прозореца на приложението с всички дефинирани Контейнери за изглед. Експериментирайте с размера на прозореца, за да видите как Контейнерите за изглед реагират на промяната.
