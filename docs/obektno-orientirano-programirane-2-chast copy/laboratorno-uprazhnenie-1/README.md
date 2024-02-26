---
layout: default
title: Laboratory lesson 1
parent: Object-oriented Programming - 2 part AEO
has_children: true
nav_order: 1
permalink: /docs/obektno-orientirano-programirane-2-chast-aeo/laboratorno-uprazhnenie-1
---

# Laboratory lesson 1

### SOLID principles

The SOLID principles were introduced by Robert C. Martin in his work from 2000, "Design Principles and Design Patterns". This concept was later refined by Michael Feathers, who introduced the acronym SOLID. Over the past 20 years, these five principles have revolutionized the world of object-oriented programming, changing the way software is written.

What is SOLID, and how does it help in writing better code?

Put simply, Martin's and Feather's design principles encourage the creation of software that is easier to maintain, understand, and flexible. Therefore, despite the inevitability of applications growing in size, their complexity decreases!

The SOLID principles indicate how functions and data structures should be arranged in classes and how these classes should be interconnected.

The use of the term "class" does not mean that these principles only apply to object-oriented programming. A class is simply a related grouping of functions and data. Every software system has such groupings, whether they are called classes or not. The SOLID principles apply to these groups of functions and data, and even to a lower level such as a function or data structure, which we will refer to as elements of the software.

The following five concepts constitute the SOLID principles: 

**S** Single Responsibility principle

**O** Open-Closed principle

**L** Liskov Substitution principle

**I** Interface Segregation principle

**D** Dependency Inversion principle


# SRP - Single Responsibility Principle


This principle states that **an element should have only one responsibility**. Furthermore, it should have only one reason to change, which is a change in the requirement for that element to fulfill.

Applying this principle allows:

*Easier Testing* - A class with a single responsibility will have far fewer test cases.

*Fewer Connections* - Less functionality in one class will result in fewer dependencies.

*Better Organization* - Smaller, well-organized classes are easier to understand than monolithic ones.

The principle that "a function should do one and only one thing" is used when refactoring large functions into smaller ones; it is used at the lowest levels.

```java
public static void calculateSum() {
     System.out.println("Enter the Two numbers for addtion: ");  
     Scanner readInput = new Scanner(System.in);  
     
     int a = readInput.nextInt();  
     int b = readInput.nextInt();
     int sum = a + b;
     
     System.out.println("The sum of numbers is: "+sum);     
}
```

Written in this way, such function cannot accept arguments from different sources, e.g. file, database, another function, sensors, etc. 


The SOLID principle of Single Responsibility (SRP) is similar to the principle mentioned above, but it also has its differences. While the principle "One function, one responsibility" applies at a low level, the higher you go (towards higher levels of abstraction), the more challenging it becomes to recognize responsibilities.

Every class may contain more than one method or field/attribute. In such situation, the approach cannot be applied directly as with functions. Instead, the context must be considered, where this combination of methods and variables will find application.

This principle can be understood and realized by considering various ways in which it can be violated.

Example: 

Let's consider salary application, where we have classс Employee with three methods: calculatePay(), reportHours() и save().

```java
public class Employee {
     public void calculatePay() {
          //method body
     }
     public void reportHours() {
          //method body
     }
     public void save() {
          //method body
     }
}
```

Method calculatePay() calculates salary for accounting department employee.

Method reportHours() is for human resources department.

Method save() is for database admin.

Let's assume functions calculatePay() and reportHours() has common calculating algorithm for working hours with no overtime.

How the code should be organized?

It will be logical to put this common algorithm in a function *regularHours()*, avoiding repeating code.

Combining these 3 methods into one class can lead to interdependencies between different departments. This can cause the actions of one department to affect those of another, which is not always necessary.

Example:


```java
public class Book {

    private String name;
    private String author;
    private String text;

    //constructor, getters and setters

    // methods that directly relate to the book properties
    public String replaceWordInText(String word){
        return text.replaceAll(word, text);
    }

    public boolean isWordInText(String word){
        return text.contains(word);
    }
}
```

In this code fragment there are properties and two methods declared in a class called _Book_. The code works properly and could store as many books in application as necessary.

Later appears requirement to display book information, which leads to some refactoring:

```java
public class Book {
    //...

    void printTextToConsole(){
        // our code for formatting and printing the text
    }
}
```

This method will have access to the system output resources that will introduce new dependency - the class will store information about a book and will present this information.

This will break single responsibility principle since the class will have more than one functionality.

In order to follow SRP a new class should be introduced with the only resposibility of presenting book information:

```java
public class BookPrinter {

    // methods for outputting text
    void printTextToConsole(String text){
        //our code for formatting and printing the text
    }

    void printTextToAnotherMedium(String text){
        // code for writing to any other location..
    }
}
```

Since there is single class for printing information, the printing could be done in different places and sources.

**In order to follow SRP in a program is necessary to know the responsibility of each class.**

Following the SRP principle, classes will adhere to a single functionality. The purpose of methods and data will be clear. This means that the code will exhibit high cohesion, as well as resistance to changes, which together reduce errors.

Although the name of the principle is indicative, its application is not always easy. When developing a project, it is necessary to correctly delineate the responsibilities of each class and pay attention to cohesion.

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

