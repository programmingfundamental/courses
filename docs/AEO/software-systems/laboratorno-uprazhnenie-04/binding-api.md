---
layout: default
title: Bainding API
parent: Laboratory Exercise 4
grand_parent: Software Systems
nav_order: 3
---

# API за свързване

## 1. JavaFX Properties

За реализацията на реактивния модел JavaFX използва Properties – наблюдаеми контейнери за данни. Те съхраняват стойност и уведомяват регистрираните наблюдатели при всяка промяна.

Често използвани типове:

- `StringProperty`
- `IntegerProperty`
- `BooleanProperty`

Properties са ключови за автоматичното обновяване на интерфейса.

## 2. Observer Pattern и ChangeListener

След като свойствата могат да се променят динамично, JavaFX използва Observer design pattern, при който:

- Property е наблюдаваният обект;
- ChangeListener е наблюдателят.
- `addListener()` методът служи за регистриране на слушател към дадено Property и позволява реакция при всяка промяна.
- Слушателите могат да бъдат:
    - `ChangeListener`
    - `InvalidationListener`

Пример:

```java
usernameField.textProperty().addListener(new ChangeListener<String>() {
    @Override
    public void changed(ObservableValue<? extends String> obs, String oldVal, String newVal) {
        validUsername.set(newVal.length() >= 4);
    }
});
```

Където ``` obs ``` е обекта чиято стойност се е променила, ``` oldVal ``` старата стойност преди промяната и ``` newVal ``` новата стойност след промяната

Тъй като интерфейсът за обработка на събития има само един метод, можем да го третираме като функционален интерфейс и да заменим тези редове с един лямбда израз, за да направим кода си по-лесен за четене:

```java
usernameField.textProperty().addListener(
    (obs, oldVal, newVal) ->
        validUsername.set(newVal.length() >= 4)
);
```

- Binding в JavaFX
    
    Binding автоматизира observer механизма и премахва необходимостта от ръчно управление на listener-и. Реализира се със метода `bind`

Пример:

```java
BooleanBinding formInvalid =
        validUsername.not().or(validPassword.not());

registerButton.disableProperty().bind(formInvalid);
```

В този случай бутонът автоматично се деактивира при празно поле.

## 3. Event Handling в JavaFX

Докато Property реагира на промени в състоянието, Event Handling обработва действия на потребителя. 

Интерфейсът EventHandler<T> дефинира поведението за обработка на събитието. В интерфейса T е типът на събитието (event), което обработчикът ще получи. T трябва да е клас, който наследява Event.

| T             | Кога се използва         |
| ------------- | ------------------------ |
| `ActionEvent` | при бутони, менюта       |
| `MouseEvent`  | при действия с мишка     |
| `KeyEvent`    | при натискане на клавиши |
| `ScrollEvent` | при скрол                |
| `DragEvent`   | при drag & drop          |
| `TouchEvent`  | при touch събития        |


```java
button.setOnAction(new EventHandler<ActionEvent>() {
    @Override
    public void handle(ActionEvent event) {
        System.out.println("Бутонът е натиснат");
    }
});
```

Тъй като интерфейсът за обработка на събития има само един метод, можем да го третираме като функционален интерфейс и да заменим тези редове с един лямбда израз, за да направим кода си по-лесен за четене:

```java
button.setOnAction(e -> {
    System.out.println("Бутонът е натиснат");
});
```

## 4. addEventHandler в JavaFX

`addEventHandler()` предоставя разширен контрол и позволява добавянето на множество обработчици за едно събитие.

Пример:

```java
button.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
    @Override
    public void handle(ActionEvent e) {
        System.out.println("Action обработен");
    }
});
```

```java
button.addEventHandler(ActionEvent.ACTION, e -> {
    System.out.println("Action обработен");
});
```

Типовете на събития са:

| Event class   | EventType                                  |
| ------------- | ------------------------------------------ |
| `ActionEvent` | `ACTION`                                   |
| `MouseEvent`  | `MOUSE_CLICKED`, `MOUSE_PRESSED` и др.     |
| `KeyEvent`    | `KEY_PRESSED`, `KEY_RELEASED`, `KEY_TYPED` |
| `ScrollEvent` | `SCROLL`, `SCROLL_STARTED`                 |

