---
title: Tasks
sidebar:
  order: 100
  label: Tasks
taskPage: true
---

## Independent Study Exercises

Use the abstract classes and interfaces from the previous exercise. For each call, identify what is determined at compile time and what is determined at runtime.

## Task 1 — A Common Type for Employees

Define an `abstract class Employee` with private `name`, `employeeId`, and `baseSalary` fields; static `companyName` and `createdCount` fields; a constructor; and getter methods. Increment the counter whenever an employee is created.

Add an abstract `double calculateSalary()` method and a `toString()` method that returns the name, ID, and calculated salary. Use non-negative salaries. Explain why there is no single meaningful salary formula for the base class.

## Task 2 — Dynamic Polymorphism

Create a `Manager` class with a `department` field and a `Clerk` class with a `bonusPercent` field. Both extend `Employee` and override `calculateSalary()` with `@Override`. The manager receives the base salary; the clerk receives `baseSalary * (1 + bonusPercent / 100.0)`. Access the private parent field through a getter.

Create an `Employee[]` with two managers and four clerks. Iterate with the same `employee.calculateSalary()` call and calculate the total, without checking the concrete class. Verify that a clerk with a base salary of 1000 and a 10% bonus earns 1100. Print the company name and employee count through the class name.

## Task 3 — Static Polymorphism

In `Manager`, define these overloaded methods:

- `double calculateEarnings(int days, double dailyPay)`;
- `double calculateEarnings(double dailyPay)`, which uses 22 workdays.

The second method must call the first. Check that `calculateEarnings(18, 100.0)` returns 1800 and `calculateEarnings(100.0)` returns 2200. Explain why selection is static even though the methods are not declared with `static`.

## Task 4 — Overloading and Overriding Together

Create a `SalaryPrinter` class with overloaded `describe(Employee employee)` and `describe(Manager manager)` methods that return distinguishable messages.

Use `Employee employee = new Manager(...)`. Predict which overload is selected by `describe(employee)` and which implementation runs for `employee.calculateSalary()`. Compare this with a call through a variable of type `Manager`. Explain the declared type of the argument and the actual type of the receiver object.

## Task 5 — Polymorphism Through an Interface

Use `Movement`, `Dog`, `Bird`, and `Fish` from Exercise 4. Create a `Movement[]` and print `move()` for each element. Add a new class, such as `Robot`, that implements the same interface. The iteration code must work without changes.

Compare the common type `Movement` with the common type `Animal`. Explain why a robot can have the capability to move without being an animal.

## Task 6 — Equality and Safe Casting

Create a separate `final class EmployeeId` with one `String value` field. Implement `equals()` and `hashCode()` based on that value. Test the same object, two different objects with the same ID, a different ID, `null`, and an object of another type.

In the array from Task 2, use `instanceof Manager` before calling the specific `getDepartment()` method. Explain why this check is needed for the specific operation but not for the common `calculateSalary()` method. Use `getClass()` to display actual types, without trying to override this final method.
