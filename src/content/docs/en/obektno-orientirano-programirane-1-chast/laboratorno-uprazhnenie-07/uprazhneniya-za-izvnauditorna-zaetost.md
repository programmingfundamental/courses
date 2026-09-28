---
title: Tasks
sidebar:
  order: 100
  label: Tasks
taskPage: true
---

## Independent Study Exercises

## Task 1

Define an `Employee` class with these fields: `firstName`, `middleName`, and `lastName` of type `String`; `baseSalary` of type `double`; and `position` of type `String`. Add a constructor, accessor methods, and `getFullName()`, which returns all three names as one string.

## Task 2

Define a `Company` class with a `name` and an array of employees. Add `getInfo()`, which builds the company information through `String` concatenation; `getInformation()`, which returns the same information using `StringBuilder`; and `findEmployeesByName(String name)`, which returns text describing employees whose names contain the supplied value.

Create at least 1,000 employees and compare the execution time of `getInfo()` and `getInformation()`. Use `System.nanoTime()` to measure the time. Before comparing performance, use `equals()` to verify that both methods return identical text. Run several warm-up and measured iterations. Treat one measurement as an observation, not as general proof of performance.

## Task 3

Define classes for vehicles: `Car` with a registration number, width, and length; `Truck` with a registration number, width, length, and load capacity; and `Bus` with a registration number, width, length, and number of seats. Decide whether a common parent class `Vehicle` is appropriate. If you use one, place the common fields in it.

## Task 4

Define a `Parking` class that creates vehicles from a formatted string.

Example input:

```text
Car:B4747KK,4,6;Truck:B1111TT,4,8,3;Bus:B2222BB,4,10,59
```

Split the input on `;`, identify each vehicle type, and create the corresponding object. Use `StringBuilder` when building a textual description of the parking lot. Demonstrate the class with at least five vehicles.

## Task 5 — Immutable and mutable text

Create `String original = "Java"` and `String alias = original`. Append text using `original += " OOP"` and explain why `alias` remains `"Java"`. Repeat with two references to the same `StringBuilder` and `append(" OOP")`; check why both references see the change.

Repeat with a `final StringBuilder`. Show that its contents can change but assigning a different object to the reference does not compile. Leave the invalid reassignment as a comment. Explain why “static” in the lesson title does not mean the `static` modifier.
