---
layout: default
title: Контроли за данни
parent: Лабораторно упражнение 7
grand_parent: Програмни системи
nav_order: 1
---

Настоящото лабораторно упражнение надгражда знанията за изграждане на потребителски интерфейс в JavaFX чрез разглеждане на сложни графични елементи (контроли), предназначени за визуализация на колекции от данни.

За разлика от базовите контроли (като `Button` или `Label`), визуализиращи единична стойност, контролите за данни служат за показване и управление на списъци или таблици от обекти.

## 1. Основни концепции

- **Разделяне на изгледа от данните:** При тези контроли визуалната структура се дефинира в **FXML**, докато самите данни се подават динамично чрез програмната логика (**Java Controller**).
- **ObservableList:** За подаване на списък с данни към тези компоненти се използва специализирана колекция, наречена `ObservableList`. Нейното основно предимство е автоматичното уведомяване на графичния интерфейс (сцената) при добавяне, промяна или премахване на елементи, което води до незабавно обновяване на изгледа.
- **Генерици (Generics - `<T>`):** Поради способността на тези контроли да визуализират различни типове данни (както стандартни типове като `String`, така и потребителски дефинирани класове като `Developer`), те са "типизирани". При дефинирането им в Java кода е **задължително** указването на типа данни, които ще съхраняват, чрез използване на триъгълни скоби (напр. `ComboBox<Developer>`).

Всички примери по-долу са базирани на модела `Developer` от примерната задача за система за управление на ИТ екип.

## 2. `ComboBox` (Падащ списък)

Използва се за предоставяне на възможност за избор на една стойност от предварително дефиниран списък с опции.

В Java кода в контролера, `ComboBox` приема един тип `<T>`. Макар често да се използват стандартни типове (напр. `<String>` за нива на умения), контролата е изключително полезна за работа с потребителски дефинирани обекти (напр. `<Developer>`).

- **Свойства (FXML Атрибути):**
  - `promptText` - текст, визуализиращ се преди осъществяването на избор (подсказка).
  - `visibleRowCount` - брой едновременно видими елементи при отваряне на падащото меню.

- **Примерна дефиниция в FXML:**

  ```xml
  <ComboBox fx:id="leadDeveloperComboBox" promptText="Изберете ръководител" prefWidth="250.0" />
  ```

- **Примерна реализация в Java (Controller):**

  ```java
  import javafx.collections.FXCollections;
  import javafx.collections.ObservableList;
  import javafx.fxml.FXML;
  import javafx.scene.control.ComboBox;

  public class TeamController {

      // Указване на потребителски дефиниран тип <Developer>
      @FXML
      private ComboBox<Developer> leadDeveloperComboBox;

      @FXML
      public void initialize() {
          ObservableList<Developer> developers = FXCollections.observableArrayList(
              new Developer("Иван Иванов", "Lead", "Java, Spring"),
              new Developer("Мария Георгиева", "Senior", "React, Node.js")
          );

          leadDeveloperComboBox.setItems(developers);
      }
  }
  ```

  _Забележка: За да визуализира коректно текста в падащия списък, `ComboBox` извиква метода `toString()` на подадения обект. Препоръчително е класът `Developer` да има предефиниран `toString()` метод._

---

## 3. `ListView` (Списъчен изглед)

Визуализира списък от елементи в правоъгълна област с възможност за превъртане.

Когато се работи с потребителски класове, често се изисква показването на повече от една данна (например едновременно името и нивото на разработчика) чрез вложени елементи. Това се реализира чрез дефиниране на потребителски `CellFactory`.

- **Примерна дефиниция в FXML:**

  ```xml
  <ListView fx:id="developersListView" prefHeight="200.0" prefWidth="300.0" />
  ```

- **Примерна реализация с вложени графични елементи в Java:**

  ```java
  import javafx.collections.FXCollections;
  import javafx.collections.ObservableList;
  import javafx.fxml.FXML;
  import javafx.scene.control.ListCell;
  import javafx.scene.control.ListView;
  import javafx.scene.control.Label;
  import javafx.scene.layout.HBox;

  public class TeamController {

      @FXML
      private ListView<Developer> developersListView;

      @FXML
      public void initialize() {
          ObservableList<Developer> developers = FXCollections.observableArrayList(
              new Developer("Иван Иванов", "Junior", "Java"),
              new Developer("Петър Петров", "Mid", "SQL")
          );

          developersListView.setItems(developers);

          // Дефиниране на CellFactory за визуализация на множество данни и вложени елементи
          developersListView.setCellFactory(param -> new ListCell<>() {
              @Override
              protected void updateItem(Developer dev, boolean empty) {
                  super.updateItem(dev, empty);

                  if (empty || dev == null) {
                      setText(null);
                  } else {
                      setText(dev.getName() + " (" + dev.getLevel() + ")");
                      setStyle("-fx-font-weight: bold;");
                  }
              }
          });
      }
  }
  ```

---

## 4. `TableView` (Таблица)

Комплексен елемент за визуализация на сложни обекти под формата на редове и колони. Всеки ред съответства на отделен обект (`Developer`), а всяка колона изобразява конкретно негово свойство ("Име", "Ниво").

### 4.1. Типизация на таблицата и колоните

1. **`TableView<S>`:** Приема един тип `S` (Модел). Това е класът на обектите, представляващи редовете (напр. `TableView<Developer>`).
2. **`TableColumn<S, T>`:** Дефинирането на колоните изисква **два** типа:
   - `S` - типът на реда (напр. `Developer`).
   - `T` - типът на данните за визуализация в конкретната клетка (напр. `String` за име).

### 4.2. Етапи на реализация и `PropertyValueFactory`

Свързването на колоните с класа се осъществява чрез `PropertyValueFactory`. При подаване на низ (напр. `"name"`), таблицата вътрешно търси съответен getter метод (напр. `getName()`) в модела `Developer`.

```xml
<TableView fx:id="developerTable" prefHeight="300.0" prefWidth="500.0">
    <columns>
        <TableColumn fx:id="colName" text="Име" prefWidth="150.0" />
        <TableColumn fx:id="colLevel" text="Ниво" prefWidth="100.0" />
    </columns>
</TableView>
```

```java
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class TeamController {

    @FXML
    private TableView<Developer> developerTable;

    @FXML
    private TableColumn<Developer, String> colName;

    @FXML
    private TableColumn<Developer, String> colLevel;

    @FXML
    public void initialize() {
        // Свързване на колоните с Get методите на класа Developer
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colLevel.setCellValueFactory(new PropertyValueFactory<>("level"));

        ObservableList<Developer> list = FXCollections.observableArrayList(
            new Developer("Иван Иванов", "Senior", "Java, Docker")
        );

        developerTable.setItems(list);
    }
}
```

---

## 5. Интеракция с компонентите (Селектиране, Добавяне и Премахване)

Управлението на данните и селекцията е стандартизирано и се извършва по сходен начин при всички контроли за данни.

### 5.1. Управление на селекцията (SelectionModel)

Достъпът до избраните от потребителя елементи се осъществява чрез `SelectionModel`.

_Забележка: Настройката за режим на селекция (`SelectionMode`) и логиката за извличане на елементи е валидна за `ListView`, `TableView`, `TreeView` и други подобни контроли._

```java
// Настройване на режим за избор на множество елементи
developersListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

// Извличане на един избран елемент (SINGLE режим)
Developer selectedDev = developerTable.getSelectionModel().getSelectedItem();

// Извличане на списък с всички избрани елементи (MULTIPLE режим)
ObservableList<Developer> selectedDevs = developersListView.getSelectionModel().getSelectedItems();
```

### 5.2. Динамично добавяне и премахване

Тъй като контролите са обвързани с `ObservableList`, добавянето или премахването на елемент от колекцията автоматично и мигновено обновява графичния интерфейс.

**Пример за добавяне:**

```java
public void addNewDeveloper(String name, String level, String skills) {
    Developer newDev = new Developer(name, level, skills);

    // Вземаме колекцията, асоциирана с таблицата, и добавяме елемента
    developerTable.getItems().add(newDev);
}
```

**Пример за премахване на селектиран елемент:**

```java
public void removeSelectedDeveloper() {
    // 1. Установяване на селектирания елемент
    Developer selectedDev = developerTable.getSelectionModel().getSelectedItem();

    // 2. Премахване от колекцията (графичният интерфейс се обновява сам)
    if (selectedDev != null) {
        developerTable.getItems().remove(selectedDev);
    }
}
```
