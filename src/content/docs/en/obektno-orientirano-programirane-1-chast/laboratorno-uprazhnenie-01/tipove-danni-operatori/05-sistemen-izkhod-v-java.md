---
title: System Output in Java
sidebar:
  order: 6
---

# System Output in Java

In Java, information is most often printed to the console using the `System.out` object.

For example:

```java
System.out.println("This is an example");
```

The expression **System.out.println()** has three parts:

- `System` — a class in the Java standard library that provides access to system resources.
- `out` — a pre-created object representing the standard output stream (the console).
- `println()` — a method that prints the supplied value and moves to a new line.

## The `print()` Method

The `print()` method prints information to the console without moving to a new line.

```java
System.out.print("This is ");
System.out.print("an example!");
```

The output is:

```text
This is an example!
```

## The `println()` Method

The `println()` method prints information to the console and then moves to a new line.

```java
System.out.println("This is ");
System.out.println("an example!");
```

The output is:

```text
This is
an example!
```

## The `printf()` Method

The `printf()` method prints formatted information using format specifiers.

```java
String name = "Ivan";
int age = 20;

System.out.printf("Name: %s, age: %d%n", name, age);
```

The output is:

```text
Name: Ivan, age: 20
```

## The `String.format()` Method

The `String.format()` method uses the same format specifiers as `printf`, but it does not print the result directly to the console. It creates a new string from the specified format and values.

```java
String name = "Ivan";
int age = 20;

String message = String.format("Name: %s, age: %d", name, age);
System.out.println(message);
```

Use `printf` when the result should be printed immediately. Use `String.format()` when the formatted text should be stored in a variable or passed to another method.

## Format Specifiers

| Specifier | Purpose |
| --------- | ------- |
| %d        | Integer |
| %f        | Floating-point number |
| %c        | Character |
| %s        | String |
| %b        | Boolean value |
| %n        | New line |

*Note:* The `printf()` method is especially useful for printing tables, numeric results, and information that must follow a specific format.
