---
title: Relationships Between Classes
sidebar:
  order: 2
---

# Relationships Between Classes

In object-oriented programming, classes are not only separate descriptions of objects. They can have relationships that model how objects interact, how one object uses another, and whether one object's lifecycle depends on another.

In Java, relationships between classes can be implemented through:

- a field of another class's type;
- a parameter of another class's type;
- a return value of another class's type;
- inheritance with `extends`;
- implementing an interface with `implements`;
- a nested class.

## “Is-a” Relationship

An “is-a” relationship is implemented through inheritance. A subclass is a specialized kind of its parent class.

```java
class Person {

    private String name;

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

class Student extends Person {

    private int facultyNumber;

    public Student(String name, int facultyNumber) {
        super(name);
        this.facultyNumber = facultyNumber;
    }
}
```

A `Student` is a `Person` because `Student` extends `Person`. Use inheritance only when a subclass can be treated as an object of its parent type.

## “Has-a” Relationship

A “has-a” relationship is implemented with a field that refers to an object of another class. One class contains or uses another class as part of its state.

```java
class Engine {

    private int power;

    public Engine(int power) {
        this.power = power;
    }

    public int getPower() {
        return power;
    }
}

class Car {

    private Engine engine;

    public Car(Engine engine) {
        this.engine = engine;
    }

    public int getEnginePower() {
        return engine.getPower();
    }
}
```

`Car` has an `Engine` field. A `Car` object uses an `Engine` object; a car is not an engine, so inheritance would be inappropriate.

## Association

An association is a general relationship in which an object of one class uses or knows about an object of another class. It describes interaction but does not necessarily define ownership or a dependent lifecycle.

```java
class Student {

    private String name;

    public Student(String name) {
        this.name = name;
    }

    public void enroll(Course course) {
        System.out.println(name + " enrolls in " + course.getName());
    }
}

class Course {

    private String name;

    public Course(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
```

The `enroll(Course course)` method accepts a `Course` object. This creates a relationship between `Student` and `Course`, because a `Student` object uses a `Course` object.

## Aggregation

Aggregation is a “part–whole” relationship in which the part can exist independently of the whole. The object holding the reference does not necessarily create the component object or fully control its lifecycle.

```java
class Teacher {

    private String name;

    public Teacher(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

class Course {

    private String name;
    private Teacher teacher;

    public Course(String name, Teacher teacher) {
        this.name = name;
        this.teacher = teacher;
    }

    public String getInformation() {
        return name + " - " + teacher.getName();
    }
}
```

The `Teacher` object is created outside `Course` and passed to its constructor. If a `Course` object is no longer used, the `Teacher` object can continue to exist and be used by another course.

```java
Teacher teacher = new Teacher("Ivan Petrov");

Course first = new Course("Programming", teacher);
Course second = new Course("Databases", teacher);
```

Here, the same teacher is associated with two courses. The teacher is therefore not owned by just one course.

## Composition

Composition is a “part–whole” relationship in which the part belongs to the whole and is created as an internal part of it. The part's lifecycle depends on the whole.

```java
class Address {

    private String town;
    private String street;

    public Address(String town, String street) {
        this.town = town;
        this.street = street;
    }

    public String getText() {
        return town + ", " + street;
    }
}

class Building {

    private Address address;

    public Building(String town, String street) {
        this.address = new Address(town, street);
    }

    public String getAddressText() {
        return address.getText();
    }
}
```

`Building` creates the `Address` object in its constructor. External code supplies values rather than an existing address, and the building constructs its internal object from those values. The address is part of the building's state.

## Aggregation and Composition

Both aggregation and composition model a “part–whole” relationship, but they differ in how strongly the objects depend on each other.

| Criterion | Aggregation | Composition |
| --------- | ----------- | ----------- |
| Creating the part | The part is passed in from outside. | The part is created inside the whole. |
| Lifecycle | The part can exist independently. | The part depends on the whole. |
| Sharing | An object can participate in several other objects. | A part usually belongs to one object. |
| Example | A course has a teacher. | A building has an address. |

Choose between aggregation and composition based on whether the component object should exist independently. If it is created and used independently, the relationship is aggregation. If it makes sense only as an internal part of another object, it is composition.

## Dependency Through a Parameter

A class depends on another type when it uses it for an operation without necessarily storing it in a field. For example, a printer uses a student only while printing the student's information:

```java
class CardPrinter {
    void print(Student student) {
        System.out.println(student.getName());
    }
}
```

Here, `CardPrinter` depends on `Student` and its `getName()` method. An association often stores a reference in a field for a longer-term relationship, while a dependency can be temporary use through a parameter.

Composition and aggregation describe ownership and lifecycle in the model. Java has no separate keywords for them and does not automatically destroy “parts” when the “whole” goes out of scope. The garbage collector reclaims memory based on object reachability.

## Nested Classes

A nested class is declared in the body of another class. The outer class provides a logical context, while the nested class describes a type closely related to it.

```java
class Bank {

    static class Account {

        private String iban;

        public Account(String iban) {
            this.iban = iban;
        }

        public String getIban() {
            return iban;
        }
    }
}
```

`Account` is declared in the body of `Bank`. Because it is declared `static`, it belongs to the `Bank` class, not to a particular `Bank` object.

Create an object of a static nested class using the outer class name:

```java
Bank.Account account = new Bank.Account("BG00BANK0000000000");
System.out.println(account.getIban());
```

A static nested class is useful when a type makes sense mainly as part of another type. It does not automatically access the outer class's non-static fields because it is not associated with a particular outer-class object.
