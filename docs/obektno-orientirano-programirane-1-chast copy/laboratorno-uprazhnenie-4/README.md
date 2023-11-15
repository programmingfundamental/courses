---
layout: default
title: Laboratory exercise 4
parent: Object-oriented Programming - 1 part
has_children: true
nav_order: 4
permalink: /docs/object-oriented-programming-1-part/laboratory-exercise-4
---

# Laboratory exercise 4

---
layout: default
title: Лабораторно упражнение 6
parent: Обектно-ориентирано програмиране - 1 част
has_children: true
nav_order: 6
permalink: /docs/obektno-orientirano-programirane-1-chast/laboratorno-uprazhnenie-6
---
# Лабораторно упражнение 6

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

**Task 1**: Define class Shape with abstract method *calculateSurface()*, *getName()* and *toString()* which returns name and area of a shape, and has properties *width* and *height*. Define classes Triangle and Rectangle as a children of Shape. The method *calculateSurface()* should return the rectangle's area as (height*_width) and triangle's area as (height*_width/2). Define class Circle where both properties are initialized with the same value (radius) and implement the abstract method. Define class with main function, create an array with shapes and display information about each shape.

**Task 2**: Define interface Movement with method move, that returns movement type as text. Define classes Dog, Bird and Fish and implement the interface. Define class with main function, create array with dogs, birds and fishes and display information for each animal.
