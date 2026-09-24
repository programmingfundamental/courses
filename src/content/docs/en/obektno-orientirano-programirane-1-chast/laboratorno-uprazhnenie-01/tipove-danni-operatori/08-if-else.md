---
title: Conditional Statements
sidebar:
  order: 8
---

# Conditional Statements

Conditional statements let a program execute different sections of code depending on the result of a boolean expression. Java's main conditional constructs are:

- `if`
- `if-else`
- `if-else-if`
- `switch`

## The `if` Statement

An `if` statement executes a block of code only when its condition is `true`:

```java
if (condition) {
    // statements
}
```

```java
int number = 70;

if (number < 100) {
    System.out.println("The number is less than 100");
}
```

The example checks whether `number` is less than 100. Because the condition is true, the statement in the body of the `if` is executed and a message is printed.

## The `if-else` Statement

Use `if-else` when you need to choose between two alternatives.

Syntax:

```java
if (condition) {
    // statements when true
} else {
    // statements when false
}
```

Example:

```java
int number = 120;

if (number < 100) {
    System.out.println("The number is less than 100");
} else {
    System.out.println("The number is greater than or equal to 100");
}
```

The `if-else` statement selects one of two instruction sequences. If the condition is true, the `if` block runs; otherwise, the `else` block runs. In this example, the condition is false, so the `else` block is executed.

## The `if-else-if` Statement

Use `if-else-if` to check several conditions in sequence.

Syntax:

```java
if (condition1) {

} else if (condition2) {

} else if (condition3) {

} else {

}
```

Conditions are checked in order. After the first condition evaluates to true, the remaining conditions are not checked.

Example:

```java
int number = 1234;

if (number < 100) {
    System.out.println("Two-digit number");
} else if (number < 1000) {
    System.out.println("Three-digit number");
} else if (number < 10000) {
    System.out.println("Four-digit number");
} else {
    System.out.println("The number has more than four digits.");
}
```

The `if-else-if` construct checks several conditions in sequence until one is true. The statements in its block then run, and the other conditions are skipped. If no condition is true, the `else` block runs.

In this example, the program determines the number of digits in `number`. Since its value is 1234, the first two conditions are false and the third condition (`number < 10000`) is true. The console displays “Four-digit number”.

## Nested Conditional Statements

You can place one `if` statement inside another. This is called a **nested conditional statement**.

Example:

```java
int number = 70;

if (number < 100) {
    System.out.println("The number is less than 100.");

    if (number > 50) {
        System.out.println("The number is greater than 50.");
    }
}
```

A nested conditional lets one check depend on another. In this example, the inner `if` runs only if the outer condition is true. Both conditions are true, so both messages are printed.

## The `switch` Statement

Use `switch` to choose between multiple alternatives based on the value of one expression.

Syntax:

```java
switch (expression) {

    case value1:
        statements;

    case value2:
        statements;

    ...

    default:
        statements;
}
```

Example:

```java
int number = 2;

switch (number) {

    case 1:
        System.out.println("Case 1");

    case 2:
        System.out.println("Case 2");

    case 3:
        System.out.println("Case 3");

    default:
        System.out.println("Default");
}
```

Output:

```text
Case 2
Case 3
Default
```

The `switch` statement selects a block based on the value of an expression. Execution starts at the `case` that matches that value.

Here, `number` is 2, so execution starts at `case 2`. Because the cases do not contain `break` statements, execution continues through the following blocks. The console therefore displays “Case 2”, “Case 3”, and “Default”.

Since Java 14, `switch` can also use the newer arrow (`->`) syntax. It does not require `break`: after a case runs, control automatically exits the `switch` construct.

In the following example, `number` is 2, so only `case 2` runs and the console displays “Case 2”:

```java
int number = 2;

switch (number) {
    case 1 -> System.out.println("Case 1");
    case 2 -> System.out.println("Case 2");
    case 3 -> System.out.println("Case 3");
    default -> System.out.println("Default");
}
```
