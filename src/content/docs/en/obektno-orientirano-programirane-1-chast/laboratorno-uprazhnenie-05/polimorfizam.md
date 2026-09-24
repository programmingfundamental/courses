---
title: Static and Dynamic Polymorphism
sidebar:
  order: 1
---

# Static and Dynamic Polymorphism

Polymorphism is an object-oriented programming principle in which the same operation can have different implementations depending on the object's specific type. In Java, this appears through method and constructor overloading and method overriding. Overriding is also sometimes called redefining a method.

This topic follows [abstraction, abstract classes, and interfaces](/courses/en/obektno-orientirano-programirane-1-chast/laboratorno-uprazhnenie-04/abstrakciya-abstraktni-klasove-i-interfeisi/).

## Types of Polymorphism

In Java, polymorphism can be classified by when the implementation of an operation is selected.

Here, **static** means “determined at compile time” and **dynamic** means “determined at runtime”. These terms describe when a method is selected; they do not indicate whether it is declared with the `static` keyword. Overloaded methods can be non-static. Dynamic polymorphism applies to overridden non-static methods.

Compile-time polymorphism occurs when the compiler selects which method or constructor to call based on the supplied arguments. This is associated with overloading.

Runtime polymorphism occurs when the choice of an overridden method depends on the object's actual type. This is associated with inheritance and method overriding.

| Type | Mechanism | When the implementation is selected |
| ---- | --------- | ----------------------------------- |
| Static polymorphism | Method and constructor overloading | At compile time, based on the declared argument types |
| Dynamic polymorphism | Overriding non-static methods | At runtime, based on the actual type of the receiver object |

## Two Steps in Method Selection

```java
class Animal {
    public String sound() { return "Animal"; }
}

class Dog extends Animal {
    @Override
    public String sound() { return "Bark"; }
}

class Reporter {
    public String describe(Animal animal) { return "Animal parameter"; }
    public String describe(Dog dog) { return "Dog parameter"; }
}

class DispatchExample {
    public static void main(String[] args) {
        Animal animal = new Dog();
        Reporter reporter = new Reporter();
        System.out.println(reporter.describe(animal)); // Animal parameter
        System.out.println(animal.sound());            // Bark
    }
}
```

The compiler selects `describe(Animal)` because the declared type of `animal` is `Animal`. For `animal.sound()`, the method signature is known at compile time, but the implementation is selected based on the actual `Dog` object. Overloading does not automatically search for the “most specific” type at runtime.

Fields and static methods do not participate in dynamic overriding. A static method with the same signature in a subclass **hides** the parent method. For clarity, call static methods through the class name. Private methods are not inherited, and final methods cannot be overridden.

## Method Overloading

Method overloading occurs when one class has several methods with the same name but different parameter lists. A parameter list includes the number, types, and order of parameters.

```java
class Calculator {

    int sum(int first, int second) {
        return first + second;
    }

    double sum(double first, double second) {
        return first + second;
    }

    int sum(int first, int second, int third) {
        return first + second + third;
    }
}
```

When an overloaded method is called, the compiler chooses a version based on the supplied arguments.

```java
Calculator calculator = new Calculator();

int firstResult = calculator.sum(2, 3);
double secondResult = calculator.sum(2.5, 3.5);
int thirdResult = calculator.sum(2, 3, 4);
```

Overloading does not give the operation a new name. It lets one operation use the same name when it can work with different parameters.

Overloading is a form of compile-time polymorphism because the specific method version is selected before the program runs.

## Constructor Overloading

Constructors can be overloaded in the same way as methods. A class can have several constructors with the same name, since a constructor always has the same name as its class. The number, types, and order of parameters distinguish them.

```java
class Product {

    private String name;
    private double price;

    public Product() {
        this("Unknown", 0.0);
    }

    public Product(String name) {
        this(name, 0.0);
    }

    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }
}
```

When an object is created, the compiler selects a constructor based on the arguments in the `new` expression.

```java
Product first = new Product();
Product second = new Product("Keyboard");
Product third = new Product("Monitor", 250.0);
```

This is compile-time polymorphism because the constructor is selected before the program starts.

## Method Overriding

Method overriding occurs when a subclass defines a method with the same signature as a method in its parent class. This is also called redefining a method.

```java
class Animal {

    public String sound() {
        return "Unknown sound";
    }
}

class Dog extends Animal {

    @Override
    public String sound() {
        return "Bark";
    }
}
```

The `sound()` method exists in `Animal`, but `Dog` provides its own implementation. The `@Override` annotation indicates that the method is intended to override an inherited method.

## Requirements for Overriding a Method

For a method to be overridden correctly:

- the subclass method must have the same name;
- the parameter list must match;
- the return type must be the same or compatible;
- visibility must not be more restrictive than in the parent class;
- the parent method must not be declared `final`, `static`, or `private`.

```java
class Parent {

    public String getInfo() {
        return "Parent";
    }
}

class Child extends Parent {

    @Override
    public String getInfo() {
        return "Child";
    }
}
```

The `@Override` annotation is optional, but lets the compiler check that a method is actually overriding an inherited method.

## Methods Inherited from `Object`

Every Java class extends `Object`. Therefore, every object has the methods defined in `Object`.

Important methods inherited from `Object` include:

- `toString()` — returns a text representation of the object;
- `equals(Object other)` — compares the current object with another object;
- `hashCode()` — returns a number associated with the object's logical identity;
- `getClass()` — returns an object describing the current object's actual class.

You can override `toString()`, `equals()`, and `hashCode()` in your own class. `getClass()` is `final` and cannot be overridden.

```java
class Student {

    private String name;
    private int facultyNumber;

    public Student(String name, int facultyNumber) {
        this.name = name;
        this.facultyNumber = facultyNumber;
    }

    @Override
    public String toString() {
        return name + " " + facultyNumber;
    }
}
```

Use `toString()` when an object needs to be represented as text.

```java
Student student = new Student("Ivan", 12345);
System.out.println(student);
```

Printing the object calls the overridden `toString()` method.

## The `getClass()` Method

The `getClass()` method is inherited from `Object`. It returns information about an object's actual class at runtime.

```java
Animal animal = new Dog();

System.out.println(animal.getClass().getSimpleName());
```

The variable's type is `Animal`, but the actual object is a `Dog`. Therefore, `getClass().getSimpleName()` returns `Dog`.

`getClass()` does not replace polymorphism. Use it only when you need information about an object's actual class.

## The `instanceof` Operator

The `instanceof` operator checks whether a reference points to an object of a particular type.

```java
Animal animal = new Dog();

if (animal instanceof Dog) {
    System.out.println("The object is Dog.");
}
```

Use `instanceof` before an explicit cast when you are not sure of the object's actual type. It is also used when overriding `equals()`, because the method parameter has type `Object`.

## `equals()` and `hashCode()`

Use `equals()` to compare objects logically. If two distinct objects should be considered equal based on their field values, override `equals()`.

```java
class Student {

    private int facultyNumber;

    public Student(int facultyNumber) {
        this.facultyNumber = facultyNumber;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Student)) {
            return false;
        }

        Student student = (Student) other;
        return facultyNumber == student.facultyNumber;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(facultyNumber);
    }
}
```

When you override `equals()`, you must also override `hashCode()`. The methods must agree: if two objects are equal according to `equals()`, they must return the same `hashCode()` value.

## Polymorphic References

A variable of a parent type can refer to an object of a subclass.

```java
Animal animal = new Dog();

System.out.println(animal.sound());
```

The variable's type is `Animal`, but the actual object is a `Dog`. Calling `sound()` runs the implementation in `Dog`. The implementation is selected at runtime based on the object's actual type.

## Type Conversion with Inheritance

You can assign a subclass object to a parent-type variable. This conversion happens automatically.

```java
Dog dog = new Dog();
Animal animal = dog;
```

Converting from a parent type to a subclass type must be explicit.

```java
Animal animal = new Dog();
Dog dog = (Dog) animal;
```

The cast is valid only if the actual object has the corresponding type. Otherwise, a runtime error occurs (handling this is covered in Exercise 6).

## An Array of the Parent Type

Polymorphism lets you process different subclasses through one common parent type.

Add these classes to the previous `Animal` and `Dog` example:

```java
class Cat extends Animal {
    @Override
    public String sound() { return "Meow"; }
}

class Bird extends Animal {
    @Override
    public String sound() { return "Chirp"; }
}
```

```java
Animal[] animals = new Animal[3];

animals[0] = new Dog();
animals[1] = new Cat();
animals[2] = new Bird();

for (Animal animal : animals) {
    System.out.println(animal.sound());
}
```

Each array element has type `Animal`, but can contain a different concrete subclass. Iteration calls the appropriate implementation of `sound()`.

## Overriding and Overloading

Overriding and overloading are different. Overloading is selected at compile time based on the method or constructor parameters.

Overriding is related to inheritance. A subclass provides a new implementation of a method with the same signature as one in the parent class. The overridden method is selected at runtime based on the object's actual type.

## Key Concepts

| Concept | Purpose |
| ------- | ------- |
| Overloading | A method or constructor has several versions with different parameters. |
| Overriding | A subclass provides a new implementation of an inherited method. |
| Polymorphism | Different objects are handled through a common parent type. |
| Abstract class | A base class that cannot be instantiated and may contain abstract methods. |
