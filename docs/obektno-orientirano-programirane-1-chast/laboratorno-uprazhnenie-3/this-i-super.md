---
layout: default
title: Ключовите думи this и super
parent: Лабораторно упражнение 3
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 2
---
# Ключовите думи this и super

**this** и **super** не могат да се използват в статичен контекст.

### Запазена дума – this

Запазената дума „**this**“ сочи към текущия обект в метод или конструктор. Най-често „this”  се използва, за да предотврати объркването между атрибутите (на класа) и параметрите (на метода), които имат еднакви имена.

`„this“` може също да се използва, за да :

·         извика конструктора на текущия клас;

·         извика метод от текущия клас;

·         се връщане обект от текущия клас;

·         се предаде като аргумент на метод;

·         се предаде като аргумент на конструктор


```java
public class Subject {
    String name;
    double finalGrade;
    int hours;

    public Subject() {
    }

    public Subject(String name, int hours) {
        this.name = name;
        this.hours = hours;
    }

    public Subject(String name, int hours, double finalGrade) {
        this(name, hours); //invokes constructor with only two parameters
        this.finalGrade = finalGrade;
    }

    public String getName(){
        return this.name;
    }

    public Double getFinalGrade(){
        return this.finalGrade;
    }

    public int getHours()
    {
        return this.hours;
    }

    public void printSubjectNameAndHours(){
        System.out.println("Subject: " + this.getName() + " : " + this.getHours()); //method invoke
    }

}
```

### &#x20;Запазена дума – super

&#x20;Запазената дума супер сочи към супер класа (родителския клас) на обекта. Използва се, за да се извикват атрибути,методи и конструктор наследени от родителския клас.


```java
class Animal { // Superclass (parent)
  public void animalSound() {
    System.out.println("The animal makes a sound");
  }
}
```

```java
class Dog extends Animal { // Subclass (child)
  public void animalSound() {
    super.animalSound(); // Call the superclass method
    System.out.println("The dog says: bow wow");
  }
}
```

```java
public class Main {
  public static void main(String args[]) {
    Animal myDog = new Dog(); // Create a Dog object
    myDog.animalSound(); // Call the method on the Dog object
  }
}
```
