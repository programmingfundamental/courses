---
title: Tasks
sidebar:
  order: 100
  label: Tasks
taskPage: true
---

## Independent Study Exercises

## Task 1

Describe the abstraction “geometric shape”: every shape has a name and can calculate its area, but the specific dimensions and formulas differ.

Define an `abstract class Shape` with a private `name` field, a constructor, an implemented `getName()` method, and an abstract `double calculateSurface()` method. Implement `toString()` to return the name and area.

Create a `Rectangle` with width and height, a `Triangle` with a base and corresponding height, and a `Circle` with a radius. Store dimensions in the concrete classes. Use the formulas `width * height`, `base * height / 2.0`, and `Math.PI * radius * radius`. Assume positive dimensions for this task.

Create each shape and verify the results: a 3 × 4 rectangle → 12, a triangle with base 3 and height 4 → 6, and a circle with radius 1 → π. Leave `new Shape(...)` as a comment and explain why it does not compile. Explain why an abstract class still has a constructor.

## Task 2

Define a `Movement` interface with a `move` method that returns the type of movement as text.

Define `Dog`, `Bird`, and `Fish` classes and implement the interface methods.

Create one object of each class and call `move()`. Explain why the method must be `public` and why `new Movement()` does not compile.

## Task 3

Define a `Movement` interface with a `move` method that returns the type of movement as text.

Define the `Pet` and `Wild` interfaces.

Define an abstract `Animal` class that implements `Movement` and stores private fields for name and age. Add a constructor, getter methods, an abstract `sound()` method, and a method that returns a text representation of the object. `Animal` can leave `move()` unimplemented because it is abstract.

Define subclasses `Dog`, `Bird`, and `Fish` that extend `Animal`. Implement the methods from the interface and abstract class.

Create concrete animals and print their movement and sounds. Choose which classes implement `Pet` and which implement `Wild`, and explain why these are marker interfaces. Identify the state shared by the hierarchy and the capability described by the interface. Processing a common array with dynamic behavior selection is covered in the next exercise.

## Task 4

Define a `Vehicle` interface with three methods:

- `changeGear`, which accepts the number of gears;
- `speedUp`, which specifies how much to accelerate;
- `applyBrakes`, which specifies how much to slow down.

Define two classes, `Bicycle` and `Car`, that implement the interface.

- When calculating bicycle acceleration, account for the rider's weight.
- When calculating car acceleration, account for the car's power.

In `main`, create a bicycle and a car, call all the methods, and print the results. Add a `default` description method and a `static` method that returns the category name to the interface. Explain how to call each kind of method.

## Task 5 — Functional Interface

Define an `@FunctionalInterface Operation` with an `int apply(int first, int second)` method, and an `Addition` class that implements it. Check the results for `(2, 3)`, `(0, 0)`, and `(-2, 3)`. Temporarily try adding a second abstract method and explain the compiler message, then remove it. Compare this interface with the marker interfaces `Pet` and `Wild`.

## Bonus

A bank offers several types of accounts: deposit accounts, credit accounts, and mortgage accounts. Its clients may be individuals or companies. Every account has a client, a balance, and a monthly interest rate.

Deposit accounts allow deposits and withdrawals. Credit and mortgage accounts allow deposits only. Every account can calculate its interest for a given number of months. The general formula is:

```text
number_of_months * monthly_interest_rate
```

Credit accounts charge no interest during the first three months for individual clients and the first two months for companies. Deposit accounts charge no interest when their balance is positive and less than 1,000. Mortgage accounts charge half the interest for the first 12 months for companies and no interest for the first six months for individuals.

Design an object-oriented model of the banking system using classes and interfaces. Model the classes, interfaces, base classes, and abstract operations, then implement the corresponding interest calculations.
