---
title: Methods
sidebar:
  order: 12
---

# Methods

A method is a named block of instructions that performs a specific operation. It can be called from another part of the program.

Methods:

- reduce code repetition;
- make programs easier to read;
- divide a program into smaller parts;
- make a program easier to maintain and extend.

## Methods and Functions

A method is similar to a function in other programming languages: it has a name, accepts parameters, runs instructions, and may return a result. For example, a function that adds two numbers can be represented in Java by the method `sum(int a, int b)`.

In Java, a method belongs to a class or interface. A static method is called through its class; a non-static method is called on a particular object and can use that object's state. Unlike a mathematical function, a method can change data or print text, and a method with a `void` return type does not return a value. Not every executable block is a method: constructors and initialization blocks also exist.

## Declaring a Method

A method declaration specifies its name, parameters, return type, and body.

```java
public static void printGreeting() {
    System.out.println("Hello!");
}
```

The `printGreeting()` method accepts no input and returns no result. Each time it is called, it runs the same instruction.

## Calling a Method

A method runs only when it is called.

```java
public class Main {

    public static void main(String[] args) {
        printGreeting();
        printGreeting();
    }

    public static void printGreeting() {
        System.out.println("Hello!");
    }
}
```

The `main()` method calls `printGreeting()` twice, so the text is printed twice.

## A Method with a Parameter

A parameter lets you pass a value to a method.

```java
public static void printName(String name) {
    System.out.println("Hello, " + name);
}
```

The `name` parameter has type `String`. You can pass a different value each time you call the method.

```java
printName("Anna");
printName("Ivan");
```

## A Method with Multiple Parameters

A method can have more than one parameter.

```java
public static void printSum(int firstNumber, int secondNumber) {
    System.out.println(firstNumber + secondNumber);
}
```

When calling the method, provide a value for every parameter.

```java
printSum(5, 7);
```

## A Method That Returns a Value

A method can return a result to the code that called it. The return type is written before the method name.

```java
public static int calculateSum(int firstNumber, int secondNumber) {
    return firstNumber + secondNumber;
}
```

Here, `calculateSum()` returns an `int`.

```java
int result = calculateSum(5, 7);
System.out.println(result);
```

## The `return` Statement

The `return` statement ends the current method and returns control to the code that called it.

If a method has a return type, `return` must provide a value of the corresponding type.

```java
public static int square(int number) {
    return number * number;
}
```

The `square()` method accepts an integer parameter and returns its square. The value after `return` is passed to the calling code, and the method ends.

In a `void` method, `return` can be used only to end execution early.

```java
public static void printNumber(int number) {

    if (number < 0) {
        return;
    }

    System.out.println(number);
}
```

If the supplied number is negative, the method ends immediately and does not print the value.

## Variable Number of Arguments

A method can accept a variable number of arguments of the same type. Write `...` after the parameter type.

```java
public static int sum(int... numbers) {
    int result = 0;

    for (int number : numbers) {
        result = result + number;
    }

    return result;
}
```

The `numbers` parameter is used as an array inside the method. A call can pass different numbers of arguments:

```java
int first = sum(1, 2);
int second = sum(1, 2, 3, 4);
```

A method can have only one variable-argument parameter. It must be the last parameter in the list.

## The `main` Method

The `main` method is the entry point of a Java program. Program execution starts here.

```java
public static void main(String[] args) {
    printGreeting();
    printName("Anna");
    printSum(5, 7);

    int result = calculateSum(5, 7);
    System.out.println(result);
}
```

In this example, `main` calls several other methods. This keeps the main sequence of the program short and readable.
