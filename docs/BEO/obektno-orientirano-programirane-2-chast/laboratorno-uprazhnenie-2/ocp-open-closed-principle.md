---
layout: default
title: OCP - Open-Closed Principle
parent: Лабораторно упражнение 2
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 2
---

# OCP - Open-Closed Principle

## Принцип „отворен за разширение, затворен за промяна“

Принципът Open/Closed гласи, че софтуерните елементи трябва да бъдат отворени за разширение, но затворени за промяна. Това означава, че при добавяне на нова функционалност е желателно да се добавя нов код, без да се променя вече съществуващият и тестван код.

### Проблем

Нека имаме калкулатор, който изпълнява различни операции.

```java
public interface CalculatorOperation {
}
```
```java
public class Addition implements CalculatorOperation {

    private double left;
    private double right;
    private double result;

    public Addition(double left, double right) {
        this.left = left;
        this.right = right;
    }

    public double getLeft() {
        return left;
    }

    public double getRight() {
        return right;
    }

    public void setResult(double result) {
        this.result = result;
    }

    public double getResult() {
        return result;
    }
}
```
```java
public class Subtraction implements CalculatorOperation {

    private double left;
    private double right;
    private double result;

    public Subtraction(double left, double right) {
        this.left = left;
        this.right = right;
    }

    public double getLeft() {
        return left;
    }

    public double getRight() {
        return right;
    }

    public void setResult(double result) {
        this.result = result;
    }

    public double getResult() {
        return result;
    }
}
```
```java
public class Calculator {

    public void calculate(CalculatorOperation operation) {
        if (operation instanceof Addition) {
            Addition addition = (Addition) operation;
            addition.setResult(addition.getLeft() + addition.getRight());
        } else if (operation instanceof Subtraction) {
            Subtraction subtraction = (Subtraction) operation;
            subtraction.setResult(subtraction.getLeft() - subtraction.getRight());
        }
    }
}
```

Този код работи, но нарушава OCP. Ако бъде добавена нова операция, например умножение или деление, класът *Calculator* трябва да бъде променян.

### Решение

Възможна е реализация, при която операцията сама да знае как да се изпълни.

```java
public interface CalculatorOperation {

    void perform();
}
```
```java
public class Addition implements CalculatorOperation {

    private double left;
    private double right;
    private double result;

    public Addition(double left, double right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void perform() {
        result = left + right;
    }

    public double getResult() {
        return result;
    }
}
```
```java
public class Subtraction implements CalculatorOperation {

    private double left;
    private double right;
    private double result;

    public Subtraction(double left, double right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void perform() {
        result = left - right;
    }

    public double getResult() {
        return result;
    }
}
```
```java
public class Calculator {

    public void calculate(CalculatorOperation operation) {
        operation.perform();
    }
}
```
Добавянето на нова операция вече не изисква промяна в класа Calculator.

```java
public class Division implements CalculatorOperation {

    private double left;
    private double right;
    private double result;

    public Division(double left, double right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void perform() {
        if (right == 0) {
            throw new ArithmeticException("Division by zero.");
        }

        result = left / right;
    }

    public double getResult() {
        return result;
    }
}
```

### Предимства

Прилагането на OCP води до:
* по-лесно добавяне на нова функционалност;
* по-малък риск от грешки в съществуващ код;
* по-добра разширяемост;
* по-лесно тестване на отделните реализации.

### Недостатъци / трудности

OCP често изисква използване на абстракции, интерфейси и допълнителни класове. При малки задачи това може да направи кода по-сложен от необходимото.

### Приложение

OCP се използва при:
* добавяне на нови операции;
* работа с различни стратегии;
* плъгини и разширения;
* шаблони като Strategy, Factory Method, Abstract Factory, Decorator.



