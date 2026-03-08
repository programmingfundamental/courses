---
layout: default
title: API за свързване
parent: Лабораторно упражнение 4
grand_parent: Програмни системи
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

- Какво е ActionEvent

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

Imperative моделът поставя основата за разработка на UI, но при по-сложни приложения става труден за поддръжка. Reactive моделът в JavaFX решава тези проблеми чрез Properties, listener-и, binding и event handling, осигурявайки автоматична синхронизация между данните и интерфейса.

