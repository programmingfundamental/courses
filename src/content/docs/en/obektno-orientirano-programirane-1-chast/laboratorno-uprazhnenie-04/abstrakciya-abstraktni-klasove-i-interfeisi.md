---
title: Abstraction, Abstract Classes, and Interfaces
sidebar:
  order: 1
---

# Abstraction, Abstract Classes, and Interfaces

## Abstraction

Abstraction means describing the essential properties and actions of an object for a given task while leaving implementation details in the background. For a geometric shape, we care that it can calculate its area, but the formula depends on the shape. Abstraction defines **what** can be done; encapsulation controls access to state and implementation.

In Java, an abstract class can describe a common model with partial implementation, while an interface describes a behavior contract. We will study these constructs here and use their common types for polymorphic behavior in Exercise 5.

## Abstract Classes

Declare an abstract class with the `abstract` keyword.

```java
abstract class Shape {

    private String color;

    public Shape(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public abstract double calculateArea();
}
```

The `Shape` class contains a field, a constructor, a regular method, and an abstract method. The abstract `calculateArea()` method has no body. Every concrete subclass must provide an implementation.

Use an abstract class as a common base type when several classes share characteristics but not every operation can be implemented at that level. It can contain common fields, constructors, and methods, while leaving different behavior as abstract methods.

Every shape in this example has a color, so `Shape` defines the `color` field and `getColor()` method. Area calculation depends on the specific shape, so `calculateArea()` is abstract and must be implemented by subclasses.

## Extending an Abstract Class

```java
class Rectangle extends Shape {

    private double width;
    private double height;

    public Rectangle(String color, double width, double height) {
        super(color);
        this.width = width;
        this.height = height;
    }

    @Override
    public double calculateArea() {
        return width * height;
    }
}
```

`Rectangle` extends `Shape`, calls the parent constructor with `super(color)`, and implements the abstract `calculateArea()` method.

## Rules for Abstract Classes

You cannot create an object directly from an abstract class.

```java
// Shape shape = new Shape("red"); // does not compile
```

An abstract class can contain fields, constructors, regular methods, and abstract methods. If a class contains at least one abstract method, the class itself must be declared `abstract`.

An abstract class does not have to contain abstract methods. Its constructor runs when a concrete subclass is created. An abstract method has no body and cannot be `private`, `static`, or `final`, because a subclass must be able to implement it.

## Interfaces and Comparison with Abstract Classes

A Java interface describes a behavior contract. It specifies methods that implementing classes must provide. An interface does not describe a concrete object on its own; it describes a capability or role that different classes can share.

## Declaring an Interface

Declare an interface with the `interface` keyword, followed by a name and a body in curly braces. The body describes the methods that implementing classes must provide.

```java
interface Printable {

    void print();
}
```

The `print()` method has no body. It describes an action that must exist in each concrete class implementing `Printable`.

## Implementing an Interface

A class implements an interface with the `implements` keyword.

```java
class Document implements Printable {

    @Override
    public void print() {
        System.out.println("Printing document");
    }
}
```

Interface methods are public as part of the contract. Their implementations in a class must use `public`, because an implementation cannot reduce the visibility of inherited behavior.

A concrete class that implements an interface must implement all of its abstract methods. Otherwise, the class itself must be declared `abstract`.

## An Interface as a Type

An interface can be used as the type of a variable, parameter, or array.

```java
Printable printable = new Document();
printable.print();
```

The variable's type is `Printable`, while the actual object is a `Document`. This lets you handle different classes in the same way when they implement the same interface.

## Multiple Interfaces

A class can implement more than one interface.

```java
interface Movable {

    void move();
}

interface Chargeable {

    void charge();
}

class ElectricCar implements Movable, Chargeable {

    @Override
    public void move() {
        System.out.println("Moving");
    }

    @Override
    public void charge() {
        System.out.println("Charging");
    }
}
```

This is possible because interfaces describe behavior rather than inherited state from multiple classes.

## Interface Members

An interface can contain:

- abstract methods;
- `default` methods;
- `static` methods;
- private helper methods with bodies;
- constants.

```java
interface Identifiable {

    int MIN_ID = 1;

    int getId();

    default boolean hasValidId() {
        return getId() >= MIN_ID;
    }

    static String getTypeName() {
        return "Identifiable";
    }
}
```

A field in an interface is a constant. It is treated as `public static final`, even if those modifiers are not written explicitly.

## Abstract Methods in an Interface

A method without a body in an interface is an abstract method. It describes an action the class must implement.

```java
interface Printable {

    void print();
}
```

The `print()` method has no body. Every concrete class implementing `Printable` must provide an implementation.

## `default` Methods

A `default` method is an interface method with a body. It provides a standard implementation that classes can use directly or override.

```java
interface Printable {

    void print();

    default void printHeader() {
        System.out.println("Document");
    }
}
```

`default` methods make it possible to add behavior to an interface without necessarily requiring every existing class to change.

## Static Methods in Interfaces

An interface can contain `static` methods. They belong to the interface itself and are called through its name.

```java
interface TextUtils {

    static boolean isEmpty(String text) {
        return text == null || text.isEmpty();
    }
}
```

Call it like this:

```java
boolean result = TextUtils.isEmpty("");
```

A static interface method is not called through an object of a class that implements the interface.

## Extending Interfaces

An interface can extend another interface with the `extends` keyword.

```java
interface Readable {

    void read();
}

interface Editable extends Readable {

    void edit();
}
```

A class implementing `Editable` must implement both `edit()` and the inherited `read()` method.

```java
class Document implements Editable {

    @Override
    public void read() {
        System.out.println("Reading");
    }

    @Override
    public void edit() {
        System.out.println("Editing");
    }
}
```

## A Regular Interface

A regular interface describes actions that can be implemented by different classes. It can contain one or more abstract methods.

```java
interface Drawable {

    void draw();
}

class Circle implements Drawable {

    @Override
    public void draw() {
        System.out.println("Drawing circle");
    }
}

class Rectangle implements Drawable {

    @Override
    public void draw() {
        System.out.println("Drawing rectangle");
    }
}
```

Both classes implement `Drawable`, so they can be handled through the common `Drawable` type.

```java
Drawable shape = new Circle();
shape.draw();
```

## Functional Interfaces

A functional interface has exactly one abstract method. It describes a single operation that must be implemented by a class or another mechanism that provides behavior.

```java
@FunctionalInterface
interface Operation {

    int apply(int first, int second);
}
```

The `@FunctionalInterface` annotation is optional, but it lets the compiler verify that the interface remains functional.

## Marker Interfaces

A marker interface has no methods. It marks a class as having a particular property or as eligible for special handling.

```java
interface Auditable {

}

class Payment implements Auditable {

}
```

Here, `Payment` is marked as a type that can participate in auditing logic.

## Working with Interfaces

You cannot create an object directly from an interface.

```java
// Printable printable = new Printable(); // does not compile
```

An interface can be used as a type, but the actual object must be an instance of a class that implements it.

```java
Printable printable = new Document();
```

A class can implement multiple interfaces, and an interface can extend another interface. Fields declared in an interface are constants treated as `public static final`. A concrete class must implement the interface's abstract methods.

## Abstract Classes and Interfaces

Use an abstract class when subclasses share a common base, common state, or a partial implementation. Use an interface when you need to describe behavior that unrelated classes can implement.

```java
abstract class Animal {

    private String name;

    public Animal(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract String sound();
}

interface Trainable {

    void train();
}

class Dog extends Animal implements Trainable {

    public Dog(String name) {
        super(name);
    }

    @Override
    public String sound() {
        return "Bark";
    }

    @Override
    public void train() {
        System.out.println("Training dog");
    }
}
```

A `Dog` object includes the state initialized in `Animal`, inherits its accessible methods, and implements the `Trainable` capability. The private `name` field remains a member of `Animal`; `Dog` does not inherit it as a member.

## Choosing Between an Abstract Class and an Interface

Use an abstract class when there is an “is-a” hierarchy. Use an interface when different classes need to support the same action without necessarily sharing a parent class.

For example, a `Dog` is an `Animal`, so extending an abstract class is appropriate. A `Document`, `Image`, and `Report` can all be `Printable` without needing to extend the same parent class.

| Abstract class | Interface |
| --------------- | --------- |
| Used when classes share state and part of their behavior | Used when different classes must provide the same behavior |
| Can contain state fields | Declared fields are constants |
| A class can extend only one abstract class | A class can implement multiple interfaces |
| Can contain constructors, abstract methods, and regular methods | Can contain constants, abstract methods, `default` methods, and `static` methods |
| Suitable for a shared partial implementation | Suitable for describing a role or capability |
