---
layout: default
title: OCP - Open-Closed Principle
parent: Лабораторно упражнение 1
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 2
---

# OCP - Open-Closed Principle

**Както подсказва името, този принцип гласи, че софтуерните обекти трябва да бъдат отворени за разширение, но затворени за модификация.** В резултат на това, когато бизнес изискванията се променят (условието на задачата се надгради), тогава обектът може да бъде разширен, но не и модифициран.

Пример:

```
public class Guitar {
    private String make;
    private String model;
    private int volume;

    //Constructors, getters & setters
}
```

В началото този клас описва класическа китара, но след време се налага добавянето за декорация.

Възможно е добавянето на поле за декорация в клас _"Китара_", но това би довело и до други промени.

Вместо това е възможно разширяването на съществуващия клас _**Китара**_:

```
public class SuperCoolGuitarWithFlames extends Guitar {

    private String flameColor;
 
    //constructor, getters + setters
}
```

По този начин е сигурно, че досегашното приложение няма да бъде засегнато, а само ще бъде разширена функционалността.

**Интерфейсите са един от начините за спазване на този принцип.**

### **1.** Несъответствие

**Калкулатор за събиране и изваждане.**


```
public interface CalculatorOperation {}
```

**Клас **_**"Добавяне**_**", който би събрал две числа и би имплементирал **_**CalculatorOperation**_**:**

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

Необходимо е декларирането и на клас _Изваждане_:

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

**Клас за изпълнение на операциите:**

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

**Въпреки че това може да изглежда добре, това не е добър пример за OCP.** Добавянето на нови операции би довело до промяна в съществуващия метод _изчисляване_ на клас _Калкулатор_.

**Това означава, че този код не е съвместим с OCP.**

### 2. Съвместими с OCP

Горният пример може да бъде рефакторирано с цел спазване на OCP. Необходимо е методът _изчисляване_ да бъде сложен на по-абстрактно ниво.

Едно възможно решение е всяка операция да бъде делегирана в съответен клас:

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

Добавянето на нова функционалност се свежда до създаването на нов клас, имплементиращ същия интерфейс:

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

**По този начин няма да е необходимо класа **_**Калкулатор**_** да бъде модифициран при добавяне на нова операция:**

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

Така класът е _затворен_ за модификация, но _отворен_ за разширение.

### 3. Заключение

Принципа на отворени за разширение и затворени за модификация обекти подобрява работата върху проектите, като намалява грешките, които могат да възникнат при модифициране на класове и улеснява тестването като капсулира вече съществуващата логика и предоставя възможност за концентрация върху новата логика на приложението.
