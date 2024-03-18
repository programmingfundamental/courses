---
layout: default
title: Laboratory lesson 3
parent: Object-oriented Programming - 2 part AEO
has_children: true
nav_order: 3
permalink: /docs/obektno-orientirano-programirane-2-chast-aeo/laboratorno-uprazhnenie-3
---

# Laboratory lesson 3

# Singleton

Singleton is part of creational patterns. 
It solves two problems:

\-          Guarantees that there is just one instance of an object existing, which is important in case of shared resources;

\-          Supports global access to this instance, i.e. allows access from everywhere in the code and avoids its overriding. This ensures problem to be solved "at one place" and if the code depends on this problem, the solution is not scattered on many places.

In order to implement Singleton there must be two conditions followed: private default constructor and static method performing as constructor. The method calls the private constructor, creates object and stores it in a static field. All calls to this method return cached object.

Example:

```
public class SingletonClass {
    // static field for the object
    private static SingletonClass singletonInstance;
    // private default constructor
    private SingletonClass() {}
    // public static method
    public static SingletonClass getInstance() {
        if (singletonInstance == null) {
            singletonInstance = new SingletonClass();
        }
        return singletonInstance;
    }
    public void printClass() {
        System.out.println("Instance of SingletonClass");
    }
}

public class Main {
    public static void main(String[] args) {
        SingletonClass singletonClass = SingletonClass.getInstance();
        singletonClass.printClass();
    }
}
```

In order to be sure that all calls to the method after the first time return one and the same object, some modifications are made in getInstance():

```
public static SingletonClass getInstance() {
    if (singletonInstance == null) {
        singletonInstance = new SingletonClass();
        System.out.println("Creating instance");
    }
    return singletonInstance;
}

public class Main {
    public static void main(String[] args) {
        SingletonClass singletonClass = SingletonClass.getInstance();
        singletonClass.printClass();
        SingletonClass anotherSingletonClass = SingletonClass.getInstance();
        anotherSingletonClass.printClass();
        SingletonClass oneMoreSingletonClass = SingletonClass.getInstance();
        oneMoreSingletonClass.printClass();
    }
}
```

The pattern Singleton is used if the problem to be solved requires only one instance of a given object and when such instance should be accessible from all clients, for example when there database connection set.

The pattern is frequently used in combination with patterns like AbstractFactory, Factory, Builderm Facade, etc.

Another usage of Singleton includes logging, driver objects and thread pulls.


# Builder

Another member of creational patterns group is Builder.

It presents opportunity of complex object creation without using many different parameterized constructors.

Let's consider the following example:

```
public class Book {
    private String isbn;
    private String title;
    private String author;
    private int publishingYear;
    private String genre;
    private String annotation;
    private int pages;
    private double price;
    public Book(String isbn, String title, String author, int publishingYear, String genre, String annotation, int pages, double price) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publishingYear = publishingYear;
        this.genre = genre;
        this.annotation = annotation;
        this.pages = pages;
        this.price = price;
    }
    // getters
}
```

If the first three attributes are required (isbn, title and author), every time there will be attributes with null values passed when an object is created:

```
public class Main {
    public static void main(String[] args) {
        Book bookOne = new Book("123456", "East of Eden", "John Steinbeck", 1999,
                "classics", null, 512, 30.85);
        Book bookTwo = new Book("147147", "The Great Gatsby", "Francis Scott Fitzgerald", 0,
                "classics", null, 0, 28.75);
    }
}
```

Possible approach to avoid such null values is creation of various parameterized constructors:

```
public class Book {
    private String isbn;
    private String title;
    private String author;
    private int publishingYear;
    private String genre;
    private String annotation;
    private int pages;
    private double price;
    public Book(String isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }
    public Book(String isbn, String title, String author, int publishingYear) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publishingYear = publishingYear;
    }
    public Book(String isbn, String title, String author, String genre) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
    }
    public Book(String isbn, String title, String author, int publishingYear, String genre) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publishingYear = publishingYear;
        this.genre = genre;
    }
    public Book(String isbn, String title, String author, String genre, String annotation) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.annotation = annotation;
    }
    public Book(String isbn, String title, String author, int publishingYear, String annotation, int pages) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publishingYear = publishingYear;
        this.annotation = annotation;
        this.pages = pages;
    }
    public Book(String isbn, String title, String author, double price) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.price = price;
    }
    // getters
}
```

The problem here is that it is impossible to create more than one parameterized constructor with the same type and number of parameters. In the example above, there cannot be constructor by isbn, title, author and publishing year, and at the same time constructor by isbn, title, author and pages.

### _Builder_

Let's modify the example with Builder pattern:

```
// Some code
public class Book {
    public static class Builder {
        private String isbn;
        private String title;
        private String author;
        private int publishingYear;
        private String genre;
        private String annotation;
        private int pages;
        private double price;
        public Builder (String isbn, String title, String author) {
            this.isbn = isbn;
            this.title = title;
            this.author = author;
        }
        public Builder withPublishingYear(int publishingYear) {
            this.publishingYear = publishingYear;
            return this;
        }
        public Builder withGenre(String genre) {
            this.genre = genre;
            return this;
        }
        public Builder withAnnotation(String annotation) {
            this.annotation = annotation;
            return this;
        }
        public Builder withPages(int pages) {
            this.pages = pages;
            return this;
        }
        public Builder withPrice(double price) {
            this.price = price;
            return this;
        }
        public Book build() {
            return new Book();
        }
    }
    private Book() {}
    // getters, setters, toString, etc.
}
```

Important part of implementation is the private constructor of class Book which leads to Builder usage in order to create an object.

Creating objects using Builder looks like:

```
// Some code
public class Main {
    public static void main(String[] args) {
        Book bookOne = new Book.Builder("123456", "East of Eden", "John Steinbeck")
                .withGenre("classics")
                .withPublishingYear(1999)
                .withPages(512)
                .withPrice(30.85)
                .build();
        Book bookTwo = new Book.Builder("147147", "The Great Gatsby", "Francis Scott Fitzgerald")
                .withPrice(28.75)
                .build();
    }
}
```

### _Pros and cons of Builder usage_

Implementation of Builder pattern leads to more code lines written, but the code is more readble and more flexible.

It reduces number of parameterized constructors and object creation is just a chaining of parameter methods. In this way the null values are avoided.

Object creation is managed from one place when using Builder, which is a good security practice.




# PRACTICE

### Task 1

Create program with the Book example (for Builder).

* Expand class Book with the necessary fields and constructors
* Complete method build in order to create Book object
* Validate fileds' values.

### Task 2

Create program for cars creation:

* Interface Мotion with method void go(double mileage)
* Classes for car components:
  * Engine - volume, mileage, started/stopped
    * implements Motion and increases mileage if the car is in movement, elsewhere an exception is thrown;
  * GPSNaviator - destination
  * Enumeration Transmission - SINGLE_SPEED, MANUAL, AUTOMATIC, SEMI_AUTATIC
  * TripComputer - returns information for the fuel, engine condition (started/stopped) and mileage.
* Car classes
  * Enum CarType - CITY\_CAR, SPORTS\_CAR, SUV
  * Abstract class Vehicle
    * fields
      * carType
      * seats
      * engine
      * transmission
      * tripComputer
      * gpsNavigator
  * Class Car:
    * extends Vehicle
    * methods - getters
  * Class Manual
    * extends Vehicle
    * method toString returns car information and information for "Trip Computer" and "GPS Navigator"
* Creational classes:
  * Interface Builder with component setters and method build
  * Class CarBuilder
  * Class CarManualBuilder
* Class Director
  * creates city car
  * creates sport car
  * creates SUV
* Main function where three types cars are build.



BONUS TASK

Implement Singleton and Builder to create a real estate agents program.

Each agent has their own name, contact phone, and uses a shared list of real estate properties. They can add and remove properties from this list.

Each property has characteristics such as type (house, apartment, or office), area, price, number of rooms, furnished or not, availability of garage/parking space, and garden.
