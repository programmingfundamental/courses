---
title: Data Types
sidebar:
  order: 5
---

# Data Types

## Theory

A **data type** determines which values a variable can store, how much memory is needed to store it, and which operations can be performed on it.

This exercise covers the main primitive data types and the `String` type, which is used for text. `String` is not a primitive type, but it is needed in even the first Java programs.

The table summarizes the main types used in this exercise:

| Type      | Size  | Values |
| --------- | ----- | ------ |
| `byte`    | 8 bit  | -128 to 127 |
| `short`   | 16 bit | -32,768 to 32,767 |
| `int`     | 32 bit | -2,147,483,648 to 2,147,483,647 |
| `long`    | 64 bit | -2^63 to 2^63-1 |
| `float`   | 32 bit | Real number |
| `double`  | 64 bit | Real number |
| `char`    | 16 bit | Single character value |
| `boolean` | —     | `true` or `false` |
| `String`  | —     | Text |

## Examples of Data Types

### `byte`

Use `byte` for small whole numbers.

```java
byte age = 25;
byte minTemperature = -10;
```

### `short`

Use `short` for whole numbers outside the range of `byte` that do not require `int`.

```java
short year = 2026;
short depth = 1500;
```

### `int`

`int` is the most commonly used integer type.

```java
int studentsCount = 32;
int currentYear = 2026;
```

### `long`

Use `long` for very large whole numbers. Add the suffix `L` when writing a `long` literal directly.

```java
long population = 6500000000L;
long distanceInMeters = 123456789L;
```

### `float`

Use `float` for real numbers with lower precision. Add the suffix `f` when writing a `float` literal directly.

```java
float price = 10.25f;
float temperature = 36.6f;
```

### `double`

`double` is Java's standard type for real numbers.

```java
double area = 33.14;
double averageScore = 5.75;
```

### `char`

`char` stores a single character. Write the value in single quotation marks.

```java
char grade = 'A';
char symbol = '#';
```

### `boolean`

A `boolean` can have only two values: `true` or `false`.

```java
boolean isValid = true;
boolean hasAccess = false;
```

### `String`

Use `String` to store text. Write string values in double quotation marks.

```java
String firstName = "Ivan";
String lastName = "Petrov";
String city = "Varna";
```

It is important to distinguish between `char` and `String`:

```java
char grade = 'A';      // a single character in single quotes
String text = "A";     // text in double quotes
```

You can join strings with the `+` operator. This is called concatenation:

```java
String firstName = "Ivan";
String lastName = "Petrov";

String fullName = firstName + " " + lastName;
System.out.println(fullName);
```

When a number is added to a `String` with `+`, the result is text:

```java
int age = 20;
String message = "Age: " + age;

System.out.println(message);
```

In this exercise, `String` is introduced only as a type for storing and printing text. The built-in methods of the `String` class are not covered here.

## Key Points

- `int` is the most commonly used type for whole numbers.
- `double` is the standard type for real numbers.
- `char` stores exactly one character and uses single quotes.
- `String` stores text and uses double quotes.
- `boolean` can only be `true` or `false`.
- It is good practice to use the `L` suffix for `long` literals.
- `float` literals must use the `f` suffix.

## Variables

A variable is a named area of memory that stores a value of a particular type. In Java, every variable has a type, a name, and a value.

Java is statically typed. The type of each variable is determined at compile time and cannot be changed while the program runs.

## Declaring a Variable

A variable declaration specifies its type and name:

```java
int number;
double price;
```

## Initializing a Variable

Initialization assigns an initial value to a variable:

```java
int number = 15;
double price = 13.78;
```

## Literals

A literal is a fixed value written directly in source code. Literals can initialize variables, be passed as method arguments, or be part of an expression:

```java
10          // integer literal
3.14        // floating-point literal
'A'         // character literal
"Java"      // string literal
true        // boolean literal
null        // null literal
```

## Assigning a Value

After a variable is declared, you can assign a new value to it:

```java
int number;
number = 10;
number = 15;
```

## Variable Definition

In the context of Java, **defining a variable** usually means declaring it, with or without an initial value.

## `final` Variables

The `final` keyword means that the variable cannot be assigned a different value after its initial assignment:

```java
final int maxAttempts = 5;
```

After this declaration, you cannot assign a new value to `maxAttempts`.

## `var`

The `var` keyword lets the compiler infer the type of a local variable from its assigned value:

```java
var count = 10;    // int
var prefix = "T";  // String
var price = 1.33;  // double
```

Using `var` does not make Java dynamically typed. The type is still determined at compile time and cannot be changed later. Given the declarations above, this assignment is invalid:

```java
count = "text";
```

## Converting Primitive Types

Primitive type conversion uses a value of one primitive type as a value of another primitive type.

When conversion does not lose information, the compiler can perform it automatically:

```java
int count = 10;
long biggerCount = count;
double price = count;
```

Here, an `int` value is used as a `long` and as a `double`. This is allowed because `long` and `double` can represent a wider range of values.

When conversion might lose information, use an explicit cast. Write the target type in parentheses:

```java
double price = 12.75;
int wholePart = (int) price;
```

After the conversion, `wholePart` is 12; the fractional part is removed.

Use explicit casts carefully because they can change a value.
