---
layout: default
title: Laboratory exercise 4
parent: Object-oriented Programming - 1 part
has_children: true
nav_order: 4
permalink: /docs/object-oriented-programming-1-part/laboratory-exercise-4
---

# Laboratory exercise 4


### Abstract classes and interfaces

Abstraction is a process of hiding from user the unnecessary object data, leaving visible only suitable details.

Since the abstract class allows concrete implementation of some methods, there is no full abstraction achieved. An abstract class offers partial abstraction.
On the other hand, the interfaces offer complete abstraction since there is no implementation present.

Abstract class:

- could extend only one class/abstract class at the moment;
- could have both abstract and concrete methods;
- the keyword "abstract" is required;
- could have protected and private properties;
- could have *static*, *final* or *static final* variables preceeded with every access modifier.

Interface:

- could extend as many interfaces as wanted;
- could extend only interface, but not a class;
- could have only abstract methods;
- the keyword "abstract" is redundant;
- all methods are by default abstract and public;
- could have only *public static final* properties (constants).


**Practice**

**Task 1**: Define class Shape with abstract method *calculateSurface()*, *getName()* and *toString()* which returns name and area of a shape, and has properties *width* and *height*. Define classes Triangle and Rectangle as a children of Shape. The method *calculateSurface()* should return the rectangle's area as (height x width) and triangle's area as ((height x width)/2). Define class Circle where both properties are initialized with the same value (radius) and implement the abstract method. Define class with main function, create an array with shapes and display information about each shape.

**Task 2**: Define interface Movement with method *move()*, that returns movement type as text. Define classes Dog, Bird and Fish and implement the interface. Define class with main function, create array with dogs, birds and fishes and display information for each animal.

**Task 3**: 
Define interface Movement with method *move()*, that returns movement type as text.
Define interfaces Pet and Wild.
Define abstract class Animal that implements interface Movement, has attributes for name and age, abstract method *sound()* and method for textual representation.
Define children of Animal - Dog, Bird and Fish. Implement interface and abstract class' methods.
Define class with main function, create an array with animals and display their movement. Display information about pets and wild animals present.

**Task 4**:

Create program for account interest.

Define interface InterestCalculator with method that calculates and returns the interest of given account, *calculateInterest()*.

Define abstract class Acount that implements the interface and has properties for accountId and balance, parameterized constructor, get methods and method for textual description.

Define class DepositAccount which inherits the abstract class with property for currency ("EUR" or "USD") and has method for textual description. The interest is 2% for USD and 3% for EUR, 0 for other currencies.

Define class SavingsAccount which inherits the abstract class with property for saving period in months and has method for textual description. The interest is 5% for period up to half year, 7% for perid up to year and 10% in other cases.

Define class with main function, declare array with accounts and test defined methods. Calculate and display the average interest.
