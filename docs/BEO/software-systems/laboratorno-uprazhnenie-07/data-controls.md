---
layout: default
title: Контроли за данни
parent: Лабораторно упражнение 7
has_children: false
nav_order: 1
---

В това упражнение ще надградим знанията си за изграждане на потребителски интерфейс в JavaFX, като разгледаме сложни графични елементи (контроли), които служат за визуализация на колекции от данни.

За разлика от базовите контроли (като `Button` или `Label`), които показват единична стойност, контролите за данни са предназначени да визуализират и управляват списъци или таблици от обекти.

## 1. Основни концепции

- **Разделяне на изгледа от данните:** При тези контроли дефинираме визуалната структура в **FXML**, но самите данни се подават динамично чрез програмната логика (**Java Controller**).
- **ObservableList:** За да подадем списък с данни на тези компоненти, използваме специална колекция, наречена `ObservableList`. Нейното предимство е, че автоматично уведомява графичния интерфейс (сцената), когато в нея се добавят, променят или премахнат елементи, и изгледът се обновява веднага.
- **Генерици (Generics - `<T>`):** Тъй като тези контроли могат да показват всякакви данни (текст, числа или ваши собствени обекти като `Student` или `Car`), те са "типизирани". Когато ги дефинираме в Java кода, **задължително** трябва да укажем какъв тип данни ще съхраняват, използвайки триъгълни скоби (напр. `ComboBox<String>`).

## 2. `ComboBox` (Падащ списък)

Използва се, когато искаме да предоставим на потребителя възможност да избере една стойност от предварително дефиниран списък с опции, спестявайки място на екрана.

В Java кода `ComboBox` приема един тип `<T>` – типът на опциите в списъка. Най-често това е `<String>`.

- **Свойства (FXML Атрибути):**
  - `promptText` - текст, който се показва, преди да бъде направен избор (подсказка, напр. "Изберете град").
  - `visibleRowCount` - колко елемента да се виждат едновременно при отваряне на падащото меню (по подразбиране е 10). Ако списъкът е по-голям, се появява скролбар.
  - `editable` - ако е `true`, потребителят може да въвежда собствена стойност в полето, освен да избира от падащия списък.

- **Пример в FXML:**

  ```xml
  <ComboBox fx:id="cityComboBox" promptText="Изберете град" prefWidth="200.0" visibleRowCount="5" editable="false" />
  ```

- **Пример в Java (Controller):**

  ```java
  import javafx.collections.FXCollections;
  import javafx.collections.ObservableList;
  import javafx.fxml.FXML;
  import javafx.scene.control.ComboBox;

  public class MainController {

      // Указваме типа <String>, защото градовете са текст
      @FXML
      private ComboBox<String> cityComboBox;

      @FXML
      public void initialize() {
          // 1. Създаване на списък с данни от съответния тип
          ObservableList<String> cities = FXCollections.observableArrayList(
              "София", "Пловдив", "Варна", "Бургас", "Русе"
          );

          // 2. Подаване на данните към ComboBox-а
          cityComboBox.setItems(cities);
      }
  }
  ```

---

## 3. `ListView` (Списъчен изглед)

Показва списък от елементи в правоъгълна област с възможност за превъртане. Подходящ е за показване на голям набор от еднотипни данни, когато искаме няколко елемента да са видими постоянно.

Подобно на `ComboBox`, `ListView` също приема един параметър за тип `<T>` в Java кода.

- **Свойства (FXML Атрибути):**
  - `orientation` - определя дали списъкът да се превърта вертикално (по подразбиране) или хоризонтално. Възможни стойности: `VERTICAL`, `HORIZONTAL`.
- В контролера може да се настрои режим на селекция – дали потребителят може да избира само един ред (`SINGLE`) или няколко едновременно (`MULTIPLE`).

- **Пример в FXML:**

  ```xml
  <ListView fx:id="usersListView" prefHeight="200.0" prefWidth="250.0" orientation="VERTICAL" />
  ```

- **Пример в Java (Controller):**

  ```java
  import javafx.collections.FXCollections;
  import javafx.collections.ObservableList;
  import javafx.fxml.FXML;
  import javafx.scene.control.ListView;
  import javafx.scene.control.SelectionMode;

  public class MainController {

      @FXML
      private ListView<String> usersListView;

      @FXML
      public void initialize() {
          ObservableList<String> users = FXCollections.observableArrayList(
              "Иван Иванов", "Петър Петров", "Мария Георгиева"
          );

          usersListView.setItems(users);

          // Позволяваме на потребителя да избира няколко елемента едновременно
          usersListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
      }
  }
  ```

---

## 4. `TableView` (Таблица)

Изключително мощен елемент за показване на сложни обекти под формата на редове и колони. Всеки ред отговаря на един обект (например `Student`), а всяка колона показва конкретно негово свойство (например "Име" или "Оценка").

### 4.1. Типове на таблицата и колоните

Заради по-сложната си структура, тук трябва да укажем типовете както за самата таблица, така и за всяка отделна колона:

1. **`TableView<S>`:** Приема един тип `S` (Моделът). Това е класът на обектите, които ще представляват редовете. (напр. `TableView<Student>`).
2. **`TableColumn<S, T>`:** Колоните изискват **ДВА** типа:
   - `S` - типът на реда (трябва да съвпада с този на таблицата, напр. `Student`).
   - `T` - типът на данните, които ще се визуализират в самата клетка на тази колона (напр. `Integer` за номер, `String` за име, `Double` за оценка). _Забележка: използват се обвиващите класове (`Integer`, `Double`), а не примитивни типове (`int`, `double`)._

### 4.2. Свойства (FXML Атрибути)

- **За `TableView`:** `prefHeight`, `prefWidth`.
- **За `TableColumn`:**
  - `text` - заглавието на колоната, което се показва в хедъра на таблицата.
  - `prefWidth` - препоръчителна ширина на колоната.

### 4.3. Примерна имплементация стъпка по стъпка

**Стъпка 1: Създаване на клас (Модел)**

Този клас описва данните за един ред.

_**Много важно**: Задължително е да имате публични "getter" методи (напр. `getName()`), защото таблицата ги използва скрито, за да прочете стойностите!_

```java
public class Student {
    private int id;
    private String name;
    private double grade;

    public Student(int id, String name, double grade) {
        this.id = id;
        this.name = name;
        this.grade = grade;
    }

    // Getters са задължителни за PropertyValueFactory!
    public int getId() { return id; }
    public String getName() { return name; }
    public double getGrade() { return grade; }
}
```

**Стъпка 2: Дефиниране на таблицата в FXML**

Всяка колона получава собствен `fx:id`, за да можем да я достъпим в контролера и да ѝ кажем какво да показва. `TableView` съдържа тага `<columns>`, в който се изброяват колоните.

```xml
<TableView fx:id="studentsTable" prefHeight="300.0" prefWidth="500.0">
    <columns>
        <!-- Колона за ID -->
        <TableColumn fx:id="idColumn" text="Номер" prefWidth="80.0" />
        <!-- Колона за Име -->
        <TableColumn fx:id="nameColumn" text="Име и Фамилия" prefWidth="250.0" />
        <!-- Колона за Оценка -->
        <TableColumn fx:id="gradeColumn" text="Среден успех" prefWidth="150.0" />
    </columns>
</TableView>
```

**Стъпка 3: Свързване и зареждане на данните в Java (Controller)**

За да разбере дадена колона коя стойност от обекта `Student` да покаже, използваме класа `PropertyValueFactory`. Името, което подаваме там (напр. `"name"`), кара таблицата да търси метод `getName()` в модела.

```java
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class MainController {

    // Таблицата съхранява обекти от тип Student
    @FXML
    private TableView<Student> studentsTable;

    // Колоната за номер взема обект Student и връща Integer
    @FXML
    private TableColumn<Student, Integer> idColumn;

    // Колоната за име взема обект Student и връща String
    @FXML
    private TableColumn<Student, String> nameColumn;

    // Колоната за оценка взема обект Student и връща Double
    @FXML
    private TableColumn<Student, Double> gradeColumn;

    @FXML
    public void initialize() {
        // 1. Свързване на колоните със свойствата на класа Student чрез PropertyValueFactory
        // Текстовете "id", "name", "grade" отговарят на методите getId(), getName(), getGrade()
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));

        // 2. Създаване на колекцията с данните
        ObservableList<Student> studentList = FXCollections.observableArrayList(
            new Student(101, "Георги Димитров", 5.50),
            new Student(102, "Елена Василева", 6.00),
            new Student(103, "Николай Костов", 4.20)
        );

        // 3. Подаване на списъка към таблицата за визуализация
        studentsTable.setItems(studentList);
    }
}
```

### 4.4. Как работи `PropertyValueFactory` и защо е важен?

Когато работим с `TableView`, всяка колона трябва да знае **как да извлече** конкретната стойност от обекта (в нашия случай `Student`), за да я покаже в съответната клетка. `PropertyValueFactory` е вграден клас в JavaFX, който автоматизира този процес, като създава връзката между колоната и свойството на класа.

#### Как се осъществява магията?

Той използва механизъм, наречен "Reflection" (Отражение в Java), за да търси метод по определено име. Когато подадете низ (String) в конструктора му, например `new PropertyValueFactory<>("name")`, JavaFX автоматично търси в класа (Модела) метод, който е конструиран по стандарта за Java Getters: думата `get` + името на свойството с главна буква.

Примери за съвпадения:

- `"id"` кара таблицата да извиква метода `getId()` за всеки ред.
- `"name"` кара таблицата да извиква метода `getName()`.
- `"firstName"` кара таблицата да извиква метода `getFirstName()`.
