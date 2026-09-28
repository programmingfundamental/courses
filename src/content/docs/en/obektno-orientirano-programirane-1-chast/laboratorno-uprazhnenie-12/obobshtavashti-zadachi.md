---
title: Tasks
sidebar:
  order: 100
  label: Tasks
taskPage: true
---

# Review Exercises

This lab reviews classes and references, inheritance, relationships and encapsulation, abstraction, abstract classes, interfaces, static and dynamic polymorphism, exceptions, strings, `record`, generic classes, collections, `Map.Entry`, and method references.

The tasks require you to design class hierarchies independently. Choose access modifiers, constructors, methods, interfaces, and collections to match the required behavior.

## Topics

1. [Independent Study Exercises](/courses/en/obektno-orientirano-programirane-1-chast/laboratorno-uprazhnenie-12/obobshtavashti-zadachi/)

## Independent Study Exercises

## Task 1

Implement an animal system using interfaces, an abstract class, and polymorphism.

Define a `Movement` interface with `String move()`. The method should return a textual description of how the animal moves.

Define `Pet` and `Wild` interfaces. Each should have a method that returns a textual description of the animal's type. Choose clear method names that describe the action.

Define an abstract `Animal` class implementing `Movement`. Give it protected fields `name` of type `String`, `age` of type `int`, and `weight` of type `double`. Add a parameterized constructor and accessors.

Declare abstract methods `String sound()` and `String eats(int count)`. `sound()` returns the animal's sound; `eats(int count)` returns text describing it eating the specified amount. Add `getDescription()` to return a textual description of the animal's common data.

Define `Dog`, `Bird`, and `Fish` as subclasses of `Animal`. Each class must call the `Animal` constructor with `super(...)`, implement `move()`, `sound()`, and `eats(int count)`, implement either `Pet` or `Wild` according to its behavior, and add at least one field of its own.

Create an `Animal[]` containing dogs, birds, and fish. Traverse it and print each animal's description, movement, sound, feeding result, and whether it is a pet or wild animal according to its implemented interface.

## Task 2

Implement an employee system for an organization with three employee types: workers, experts, and managers.

Define an `ExecuteWork` interface with `boolean execute(String taskName)`. The method accepts a task name and returns whether the task was completed successfully.

Define an abstract `Employee` class implementing `ExecuteWork`. It must have private `name` (`String`), `totalTasks` (`int`), and `successfulTasks` (`int`) fields. Add a constructor, accessors, protected methods for incrementing assigned and successful task counts, and `double successRate()`. The success rate must be in `[0, 1]`; return `0` when there are no assigned tasks.

## Class `Worker`

Define `Worker` extending `Employee` with a private `failedTaskNumber` field of type `int`.

The worker must fail every `failedTaskNumber`-th task. For example, if it is `3`, the first two tasks succeed, the third fails, and the cycle repeats.

Implement `execute(String taskName)`. It must increment the number of assigned tasks and increment successful tasks only when execution succeeds. Add a private helper method that determines whether the current task will succeed.

## Class `Expert`

Define `Expert` extending `Employee`. An expert always completes an assigned task successfully. Its `execute(String taskName)` method must increment both assigned and successful task counts and return `true`.

## Class `Manager`

Define `Manager` extending `Employee` with a collection of subordinates:

```java
private List<Employee> employees;
```

When assigned a task, a manager must delegate it to the subordinate with the fewest assigned tasks. The manager succeeds if the selected subordinate completes the task successfully.

Implement a constructor, `addEmployee(Employee employee)`, `execute(String taskName)`, and `printStatus()`. `printStatus()` should display the names and current success rates of all subordinates.

## Demonstration

Create a sample program that creates two workers with different `failedTaskNumber` values and one expert; assigns several tasks directly to them; creates a manager whose subordinates are those employees; assigns tasks to the manager; prints subordinate status; and displays each employee's success rate.

Then create a second manager with a new employee and the first manager as subordinates. Demonstrate that tasks can be delegated to another manager as well.

## Extension — Applying the topics covered

Extend the employee system as follows:

1. Create a snapshot record `EmployeeReport(String name, int totalTasks, int successfulTasks)`. Show that executing another task changes the employee but not an existing report. Compare this with a regular read-only class.
2. Use a custom `record KeyValue<K, V>(K key, V value)` for an “employee ID — report” result. State the types of both components and distinguish the pair from a complete `Map`.
3. Store reports in `Map<Integer, EmployeeReport>`. Traverse with `Map.Entry<Integer, EmployeeReport>` and build a text report using `StringBuilder`. Use unique IDs as keys because names may repeat.
4. Sort a copy of the employee list by success rate using `Comparator.comparingDouble(Employee::successRate)`, then add a secondary name criterion. Explain the method reference using its equivalent lambda expression.
5. Add an overloaded `execute(String taskName, int repetitions)` method. Distinguish compile-time selection of the overload from dynamic execution of `execute(String)` on a specific employee.
6. Reject `failedTaskNumber <= 0` and delegating to a manager with no subordinates using appropriate exceptions. Use `finally` to report that the delegation attempt has finished. Check both successful and unsuccessful attempts.

Also check an empty list, equal success rates, duplicate names, and an employee with no assigned tasks. Set a breakpoint during delegation and trace how two references can reach the same employee. Draw inheritance and the “manager has subordinates” relationship separately.
