---
layout: default
title:  Imperative & Reactive
parent: Laboratory Exercise 4
grand_parent: Software Systems
nav_order: 4
---

## Imperative and Reactive models in building user interface applications.

The development of graphical user interfaces requires continuous synchronization between the application’s data and the visual components. In early UI technologies, this synchronization was implemented through the so-called imperative model, where the developer manually controls every change in the interface.

As the complexity of applications increases, this approach leads to code that is difficult to maintain and has a higher probability of errors. In response to these issues, JavaFX introduces a reactive model, which automatically updates the interface in response to changes in data and user actions.

### 1. Imperative модел при разработка на UI

#### 1.1 Essence of the Imperative Model

The imperative model is an approach in which the developer explicitly defines the sequence of actions that must be executed whenever data changes or a user event occurs.

Main characteristics:

- The UI is updated manually;
- The logic and the interface are tightly coupled;
- Every change requires additional code.

#### 1.2 Example of the Imperative Approach

```java
button.setOnAction(e -> {
    String text = textField.getText();
    label.setText(text);
});
```

In this example:

the developer manually retrieves the value;

manually updates the UI component;

the code is executed only when a specific event occurs.

If the value of textField changes in another way, the label will not be updated.

#### 1.3 Disadvantages of the Imperative Model

- the need to repeat logic;
- risk of missed updates;
- difficult maintenance with complex dependencies;
- lower code readability.

These limitations motivate the introduction of a reactive approach.

### 2. Reactive Model in JavaFX

The reactive model in JavaFX represents an architectural approach in which the user interface automatically reacts to changes in the application’s state. The main idea is that data acts as the central element, while UI components observe it.

#### 2.2 Example of the Reactive Approach

```java
label.textProperty().bind(textField.textProperty());
```

After establishing this connection:

- the label updates automatically;
- no additional code is required;
- all changes are reflected in real time.

### 3. Comparison between the Imperative and Reactive Models

| Criterion   | Imperative  | Reactive (JavaFX) |
| ----------- | ----------- | ----------------- |
| UI Update   | Manual      | Automatic         |
| Coupling    | High        | Low               |
| Code        | More        | Less              |
| Errors      | More likely | Fewer             |
| Maintenance | Difficult   | Easy              |


The imperative model provides the foundation for UI development, but it becomes difficult to maintain in more complex applications. The reactive model in JavaFX addresses these issues through properties, listeners, binding, and event handling, ensuring automatic synchronization between the data and the user interface.