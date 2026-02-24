---
layout: default
title: SRP - Single Responsibility Principle
parent: Laboratory lesson 1
grand_parent: Object-oriented Programming - 2 part AEO
nav_order: 1
---

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
