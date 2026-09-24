---
title: Tasks
sidebar:
  order: 100
  label: Tasks
taskPage: true
---

## Independent Study Exercises

The tasks follow this order: inheritance and `super`, relationships between classes, encapsulation, and access modifiers. The first tasks specify class and method names; later tasks leave the structure up to you.

## Task 1

Define a parent class `Person` and a subclass `Student`.

The `Person` class must contain:

- `private String name`;
- `private int age`;
- a `Person(String name, int age)` constructor;
- a `getName()` method;
- a `getAge()` method;
- a `getInformation()` method that returns text containing the name and age.

The `Student` class must extend `Person` and contain:

- `private int facultyNumber`;
- a `Student(String name, int age, int facultyNumber)` constructor;
- a `getFacultyNumber()` method;
- a `getStudentInformation()` method.

The `Student` constructor must use `super(name, age)`. The `getStudentInformation()` method must use `super.getInformation()` and add the faculty number to the result.

Create `Person` and `Student` objects. Call `getInformation()` on the `Person` object and `getStudentInformation()` on the `Student` object.

## Task 2

Define a class hierarchy for animals.

The parent class `Animal` must contain:

- `private String name`;
- `private int age`;
- `protected double weight`;
- an `Animal(String name, int age, double weight)` constructor;
- getter methods for its fields;
- a `getDescription()` method.

The `Dog` class must extend `Animal` and add:

- `private String breed`;
- `private int learnedCommands`;
- a `Dog(String name, int age, double weight, String breed, int learnedCommands)` constructor;
- a `getDogDescription()` method that uses `super.getDescription()`.

The `Cat` class must extend `Animal` and add:

- `private String furColor`;
- `private boolean indoor`;
- a `Cat(String name, int age, double weight, String furColor, boolean indoor)` constructor;
- a `getCatDescription()` method that uses `super.getDescription()`.

Create at least two `Dog` objects and two `Cat` objects. Print their descriptions.

## Task 3

Extend the hierarchy from Task 2.

Add this method to `Animal`:

```java
double calculateBaseDailyFood()
```

The method must return a sample daily food allowance based on the animal's weight. Choose the formula yourself and explain it in a short code comment.

Add a `calculateDogDailyFood()` method to `Dog`. It must use `super.calculateBaseDailyFood()` and adjust the result based on characteristics specific to dogs.

Add a `calculateCatDailyFood()` method to `Cat`. It must use `super.calculateBaseDailyFood()` and adjust the result based on characteristics specific to cats.

Create separate `Dog` and `Cat` objects. Call the corresponding daily food calculation methods and calculate the total food needed for one day.

## Task 4

Model the relationships between `University`, `Student`, `StudentCard`, and `CardPrinter`. Use an array of students supplied from outside to model aggregation in the university. The student should create and manage a student card as part of its state, while the printer should receive a student only as a parameter to its print operation.

Draw the “has-a”, association, aggregation, composition, and dependency relationships. Explain object ownership and lifecycles. Separately add `Student extends Person` and explain why this is an “is-a” rather than a “has-a” relationship. For composition, note that Java does not automatically destroy objects when their owner goes out of scope: this is a rule of the model, while memory is managed by the garbage collector.

## Task 5

Define an encapsulated `BankAccount` class.

The class must contain these private fields:

- `ownerName` of type `String`;
- `balance` of type `double`.

Define this parameterized constructor:

```java
BankAccount(String ownerName, double balance)
```

The constructor must set `ownerName`. Set `balance` only if its value is greater than or equal to `0`.

Define these methods:

- `getOwnerName()`;
- `getBalance()`;
- `deposit(double amount)`;
- `withdraw(double amount)`.

`deposit(double amount)` must increase the balance only when `amount` is positive. `withdraw(double amount)` must decrease the balance only when `amount` is positive and sufficient funds are available.

Create an `Application` class with a `main` method. Create a `BankAccount` object there and demonstrate all its methods.

## Task 6

Define a `Student` class that uses different access modifiers.

The class must contain:

- `private String name`;
- `private int facultyNumber`;
- `protected String specialty`;
- `public static String university`.

Define this constructor:

```java
Student(String name, int facultyNumber, String specialty)
```

Define getter methods for all non-static fields. Do not define a method that directly changes `facultyNumber`.

Create an `Application` class and create two `Student` objects in it. Demonstrate access to the public static field through the class name.

Example:

```java
Student.university = "Technical University of Varna";
```

Try to access a private field directly from `Application` and leave that line commented out because it does not compile.

Also check access from a subclass in another package. For the `private` field, show that the inherited getter works but direct access does not compile. Explain the difference between state present in an object and a member inherited by a class.

## Task 7

Design a class hierarchy of your choice. The topic can be an educational system, shop, transport, library, or another real-world area.

The hierarchy must contain:

- one parent class;
- at least two subclasses;
- private fields in the parent class;
- private fields in the subclasses;
- at least one `protected` field or method;
- constructors that use `super(...)`;
- at least one subclass method that calls `super.methodName()`.

Create a sample program that instantiates all subclasses and demonstrates access through public methods. Do not access private fields directly.

## Task 8 — Read-Only Object and `record`

Implement a `final class StudentCard` with `private final` fields for the card number and owner, a constructor, and getters only. Represent the same data with a `record StudentCardRecord`. Compare object creation, accessors, `equals()`, and `toString()`.

Also define a separate record with an `int[] grades` component. Show how changing the supplied array or the array returned by the accessor changes the visible contents. Add defensive copies on input and output, then repeat the check. Explain why a `final` reference alone is not enough.
