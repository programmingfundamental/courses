---
title: Loop Statements
sidebar:
  order: 9
---

# Loop Statements

Loop statements execute a block of instructions repeatedly while a condition is satisfied.

Java has three main loop constructs:

- `for`
- `while`
- `do-while`

Choose a loop based on the task and how repetition is controlled.

## The `for` Loop

Use a `for` loop when the number of repetitions is known in advance or can be determined before the loop starts.

Syntax:

```java
for (initialization; condition; update) {
    // statements
}
```

Where:

- initialization runs once before the loop starts;
- the condition is checked before each iteration;
- the update runs after each iteration.

Example:

```java
int[] numbers = {5, 10, 15, 20};

for (int i = 0; i < numbers.length; i++) {
    System.out.println(numbers[i]);
}
```

The example prints each array element in sequence using its index.

## Enhanced `for` Loop

Use the enhanced `for` loop (also called `for-each`) to visit every element in an array or collection when you do not need the element's index.

Syntax:

```java
for (type variable : collection) {
    // statements
}
```

Example:

```java
int[] numbers = {5, 10, 15, 20};

for (int number : numbers) {
    System.out.println(number);
}
```

This has the same effect as the previous example but uses different syntax. The enhanced loop is shorter and easier to read when you only need to visit every element.

## The `while` Loop

Use a `while` loop when the number of repetitions is not known in advance and execution should continue as long as a condition is true.

The condition is checked **before** each iteration.

Syntax:

```java
while (condition) {
    // statements
}
```

Example:

```java
int counter = 0;

while (counter < 5) {
    System.out.println(counter);
    counter++;
}
```

The variable `counter` is initialized to 0.

Before each iteration, the loop checks whether `counter < 5`. While this is true, the loop body prints the current value and then increases it by one with the `++` operator.

When `counter` becomes 5, the condition is false and the loop ends.

If the condition is false at the first check, the loop body does not run.

## The `do-while` Loop

A `do-while` loop is similar to a `while` loop, but it checks the condition **after** executing the loop body.

This guarantees that the statements run **at least once**.

Syntax:

```java
do {
    // statements
} while (condition);
```

Example:

```java
int counter = 0;

do {
    System.out.println(counter);
    counter++;
} while (counter < 5);
```

This behaves like the earlier `while` example, but whatever the initial value of `counter`, it is printed at least once.

## Comparing Loop Statements

| Loop | Characteristics |
| ---- | --------------- |
| `for` | Use when the number of repetitions is known in advance. |
| `while` | Use when the number of repetitions is unknown. The condition is checked before each iteration. |
| `do-while` | The condition is checked after the loop body, so the body runs at least once. |
| Enhanced `for` | Use to visit array or collection elements without an index. |
