---
layout: default
title: Bainding API
parent: Laboratory Exercise 4
grand_parent: Software Systems
nav_order: 3
---

# API за свързване

## 1. JavaFX Properties

For implementing the reactive model, JavaFX uses Properties – observable data containers. They store a value and notify registered observers whenever a change occurs.

Commonly used types:

- `StringProperty`
- `IntegerProperty`
- `BooleanProperty`

Properties are key to the automatic updating of the user interface.

## 2. Observer Pattern & ChangeListener

Since properties can change dynamically, JavaFX uses the Observer design pattern, in which:

- The Property is the observed object;
- The ChangeListener is the observer.
- `addListener()` The method is used to register a listener to a given Property and allows reacting to every change.
- Listeners can be::
    - `ChangeListener`
    - `InvalidationListener`

Example:

```java
usernameField.textProperty().addListener(new ChangeListener<String>() {
    @Override
    public void changed(ObservableValue<? extends String> obs, String oldVal, String newVal) {
        validUsername.set(newVal.length() >= 4);
    }
});
```

Where ``` obs ``` is the object whose value has changed, ``` oldVal ``` is the value before the change, and ``` newVal ``` is the value after the change.

Since the event handling interface has only one method, we can treat it as a functional interface and replace these lines with a single lambda expression to make the code easier to read:

```java
usernameField.textProperty().addListener(
    (obs, oldVal, newVal) ->
        validUsername.set(newVal.length() >= 4)
);
```

- JavaFX Binding
    
    Binding automates the observer mechanism and removes the need for manual listener management. It is implemented using the method `bind`

Example:

```java
BooleanBinding formInvalid =
        validUsername.not().or(validPassword.not());

registerButton.disableProperty().bind(formInvalid);
```

In this case, the button is automatically disabled when a field is empty.

## 3. JavaFX Event Handling

While Property reacts to changes in state, Event Handling processes user actions.

The EventHandler<T> interface defines the behavior for handling an event. In this interface, T is the type of event that the handler will receive. T must be a class that extends Event.

| T             | When it is used    |
| ------------- | ------------------ |
| `ActionEvent` | for buttons, menus |
| `MouseEvent`  | for mouse actions  |
| `KeyEvent`    | for key presses    |
| `ScrollEvent` | for scrolling      |
| `DragEvent`   | for drag & drop    |
| `TouchEvent`  | for touch events   |



```java
button.setOnAction(new EventHandler<ActionEvent>() {
    @Override
    public void handle(ActionEvent event) {
        System.out.println("The button is pressed.");
    }
});
```

Since the event handling interface has only one method, we can treat it as a functional interface and replace these lines with a single lambda expression to make the code easier to read:

```java
button.setOnAction(e -> {
    System.out.println("The button was pressed.");
});
```

## 4. JavaFX addEventHandler

`addEventHandler()` It provides extended control and allows adding multiple handlers for a single event.

Example:

```java
button.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
    @Override
    public void handle(ActionEvent e) {
        System.out.println("Action handled");
    }
});
```

```java
button.addEventHandler(ActionEvent.ACTION, e -> {
    System.out.println("Action handled");
});
```

The types of events are:

| Event class   | EventType                                  |
| ------------- | ------------------------------------------ |
| `ActionEvent` | `ACTION`                                   |
| `MouseEvent`  | `MOUSE_CLICKED`, `MOUSE_PRESSED`     |
| `KeyEvent`    | `KEY_PRESSED`, `KEY_RELEASED`, `KEY_TYPED` |
| `ScrollEvent` | `SCROLL`, `SCROLL_STARTED`                 |

