---
layout: default
title: OCP - Open-Closed Principle
parent: Лабораторно упражнение 1
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 2
---

# OCP - Open-Closed Principle

As the name suggests, this principle states that **software objects should be open for extension but closed for modification**. As a result, when business requirements change, the object can be extended but not modified.

Example:

```
public class Guitar {
    private String make;
    private String model;
    private int volume;

    //Constructors, getters & setters
}
```

In the beginning, this class describes classic guitar, but later decoration should be added.

One way to do this is simply adding *decoration* property to the existing class _Guitar_, but this will require additional modifications.

Instead, it's better to extend class _Guitar_:


```
public class SuperCoolGuitarWithFlames extends Guitar {

    private String flameColor;
 
    //constructor, getters + setters
}
```

Written like that, existing application won't be affected, there will be only additional functionality.

**One way to follow this principle is using interfaces.**

### **1.** Example not following OCP

**Addition and Subtraction Calculator.**


```
public interface CalculatorOperation {}
```

**Class **_**"Addition**_**", which sums two numbers and implements **_**CalculatorOperation**_**:**

```java
public class Addition implements CalculatorOperation {
    private double left;
    private double right;
    private double result = 0.0;

    public Addition(double left, double right) {
        this.left = left;
        this.right = right;
    }

    // getters and setters

}
```

We also need class _Subtraction_:

```java
public class Subtraction implements CalculatorOperation {
    private double left;
    private double right;
    private double result = 0.0;

    public Subtraction(double left, double right) {
        this.left = left;
        this.right = right;
    }

    // getters and setters
}
```

**Execution class:**

```java
public class Calculator {

    public void calculate(CalculatorOperation operation) {
        if (operation == null) {
            throw new InvalidParameterException("Can not perform operation");
        }

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

Although this code looks fine, this is not good example of OCP. Adding new operation will require refactoring of already existing method _calculate_, meaning the code breaks OCP.

### 2. Same example following OCP

In order to follow OCP, the method _calculate_ will be placed on a higher level of abstraction.

Possible solution is each operation to be defined in a class:

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

    // constructor, getters and setters

    @Override
    public void perform() {
        result = left + right;
    }
}
```

Adding new operation means simply creating new class and implementing the same interface:

```java
public class Division implements CalculatorOperation {
    private double left;
    private double right;
    private double result;

    // constructor, getters and setters
    @Override
    public void perform() {
        if (right != 0) {
            result = left / right;
        }
    }
}
```

**With such organization, there will be no need of method modification when new operation is added:**


```java
public class Calculator {

    public void calculate(CalculatorOperation operation) {
        if (operation == null) {
            throw new InvalidParameterException("Cannot perform operation");
        }
        operation.perform();
    }
}
```
In this way, the class is _closed_ for modification but _open_ for extension.


### 3. Conclusion

The principle of open for extension and closed for modification objects improves work on projects by reducing errors that may arise when modifying classes and facilitates testing by encapsulating existing logic and providing the opportunity to focus on the new logic of the application.
