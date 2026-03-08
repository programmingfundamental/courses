---
layout: default
title: Лабораторно упражнение 4
parent: Програмни системи
has_children: true
nav_order: 4
---

# Лабораторно упражнение 4

## Управление на потребителския интерфейс

### Контролер (Controller)

Controller е клас, който обработва действията на потребителя (кликове, въвеждане на данни и д.р.), чете и валидира данните от FXML файловете, обновява декора чрез JavaFX механизми (binding, setText, disable и др.)

В JavaFX Controller е Java клас, който се свързва с FXML файла и управлява поведението на потребителския интерфейс.

FXML файлът не съдържа логика, а само декларативно описание на интерфейса и връзките към контролера.

Във FXML файл контролерът се дефинира и използва чрез няколко ключови елемента.

fx:controller атрибута свързва FXML файла с конкретен Java Controller клас.

Създаването на обектите на потребителския интерфейс и обекта на Java Controller класа се осъществява от FXMLLoader, който преобразува fxml дефиницията към Java обект. Това се случва като FXMLLoader анализира файловете с изгледи и свързва съответните изгледи в полетата на контролера. Свързването се усъществява с анотацията @FXML, която маркира полета и методи, които се свързват с елементите, декларирани в fxml файла.


![alt text](image-1.png)

```xml
<GridPane fx:controller="bg.tu_varna.sit.ps.lab4.LoginController" xmlns:fx="http://javafx.com/fxml/1">
```

```java
public class LoginController implements Initializable {

    private final String name;

    @FXML
    private Label label;

    public LoginController(String name) {
        this.name = name;
    }

    @Override
    public void initialize(URL location, ResourceBundle res) {
        this.label.setText(this.name); 
    }
}
```

```java
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/login-view.fxml")
        );

        LoginController controller = new LoginController("Login Manager");

        loader.setController(controller);

        Parent root = loader.load();

        Scene scene = new Scene(root, 600, 400);

        stage.setTitle("Login Application");
        stage.setScene(scene);
        stage.show();
    }
}
```

След като полетата бъдат успешно инжектирани, можем безопасно да ги достъпим чрез метода за инициализация за операции като регистриране на обработвачи на събития и стилизиране. По същество инжекцията не се случва при initialize, а след като конструкторът бъде извикан. Така че, полетата не са достъпни за използване в конструктора:

![alt text](image.png)

По време на създаване на обектите с конструктор изгледа е практически невалидни. Ако опитаме да достъпим FXML изгледите в конструктор, програмата ще хвърли NullPointerException.

Затова initialize() предоставя безопасен начин за постобработка на FXML изгледи и настройването им в началото на изпълнението на програмата. След това декора се рендерира, когато изгледа в готов. Следователно можем да поставим логиката на инициализация на интерфейса в метода за инициализация.

```java
@FXML
public void initialize() {
    button.setOnAction(e -> {
        System.out.println("Бутонът е натиснат");
    });
}
```

| Конструктор | *initialize()* |
|---|---|
| Изпълнява се, когато е създаден контролер обект | Изпълнява се автоматично след въвеждане на FXML изгледи |
| Извикване от JVM по време на инстанциация на обекти | Извикан от JavaFX FXML Loader |
| FXML изгледите са недостъпни | FXML изгледи са достъпни |
| Използва се за настройване на състояние на обекта | Използва се за инициализация на потребителския интерфейс |
| Извиква се веднъж на контролер | Извиква се веднъж на контролер |
| Може да взема параметри | Изисква само два аргумента: *URL* и *ResourceBundle* |

#### Анотиране на полета с @FXML анотация

fx:id атрибута е уникален идентификатор на UI елемент във FXML, който позволява достъп до компонента от контролера. Името на променливата в контролера трябва да съвпада с fx:id

Пример: 

<table>
<tr>
<td><b>FXML</b></td>
<td><b>Java</b></td>
</tr>

<tr>
<td>

<pre><code class="language-xml">
&lt;TextField fx:id="usernameField"/&gt;
</code></pre>

</td>
<td>

<pre><code class="language-java">
@FXML
private TextField usernameField;
</code></pre>

</td>
</tr>
</table>

#### Анотиране на методи с @FXML анотация

Метод за действие - Event handlers 
- атрибутите onAction, onMouseClicked, и др. свързват FXML събитие с метод в Controller. Метода трябва да съществува в контролера, да е маркиран с @FXML, да е void

<table>
<tr>
<td><b>FXML</b></td>
<td><b>Java</b></td>
</tr>

<tr>
<td>

<pre><code class="language-xml">
&lt;Button text="Вход" onAction="#onLogin"/&gt;
</code></pre>

</td>

<td>

<pre><code class="language-java">
@FXML
private void onLogin() {
    System.out.println("Бутонът е натиснат");
}
</code></pre>

</td>

</tr>
</table>


## 2. JavaFX Properties

За реализацията на реактивния модел JavaFX използва Properties – наблюдаеми контейнери за данни. Те съхраняват стойност и уведомяват регистрираните наблюдатели при всяка промяна.

Често използвани типове:

- `StringProperty`
- `IntegerProperty`
- `BooleanProperty`

Properties са ключови за автоматичното обновяване на интерфейса.

## 3. Observer Pattern и ChangeListener

След като свойствата могат да се променят динамично, JavaFX използва Observer design pattern, при който:

- Property е наблюдаваният обект;
- ChangeListener е наблюдателят.
- `addListener()` методът служи за регистриране на слушател към дадено Property и позволява реакция при всяка промяна.
- Слушателите могат да бъдат:
    - `ChangeListener`
    - `InvalidationListener`

Пример:
Снимка с пример
```java
textField.textProperty().addListener(
    (obs, oldValue, newValue) -> { // Добави обяснение какво е obs, oldValue, newValue
        System.out.println(oldValue + " -> " + newValue); // Подобри примера
    }
);
```

- Binding в JavaFX
    
    Binding автоматизира observer механизма и премахва необходимостта от ръчно управление на listener-и. Реализира се със метода `bind`

Пример:

```java
button.disableProperty().bind(textField.textProperty().isEmpty());
```

В този случай бутонът автоматично се деактивира при празно поле.

## 4. Event Handling в JavaFX

Докато Property реагира на промени в състоянието, Event Handling обработва действия на потребителя.

Пример:

```java
button.setOnAction(e -> {
    System.out.println("Бутонът е натиснат");
});
```

## 5. addEventHandler в JavaFX

`addEventHandler()` предоставя разширен контрол и позволява добавянето на множество обработчици за едно събитие.

- Какво е ActionEvent

Пример:

```java
button.addEventHandler(ActionEvent.ACTION, e -> {
    System.out.println("Action обработен");
});
```

Imperative моделът поставя основата за разработка на UI, но при по-сложни приложения става труден за поддръжка. Reactive моделът в JavaFX решава тези проблеми чрез Properties, listener-и, binding и event handling, осигурявайки автоматична синхронизация между данните и интерфейса.

