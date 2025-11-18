---
layout: default
title: Laboratory exercise 4
parent: Object-oriented Programming - 1 part
has_children: true
nav_order: 4
permalink: /docs/object-oriented-programming-1-part/laboratory-exercise-4
---

# Laboratory exercise 4

### Abstract class and interface

Abstraction is the process of showing only “relevant” data and “hiding” unnecessary details about an object from the user (the programmer using the abstraction).

Since an abstract class allows for concrete implementation of methods, it does not provide 100% abstraction. It provides partial abstraction.

Interfaces on the other hand are used for 100% abstraction.

| Abstract class                                                                                           | Interface                                                                               |
| --------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------- |
| An abstract class can only extend one class or one abstract class at a time               | A single interface can extend any number of interfaces at once.                      |
| An abstract class can extend another concrete (regular) class or abstract class                    | An interface can only extend another interface.                                      |
| An abstract class can have both abstract and concrete methods.                                   | An interface can only have abstract methods.                                          |
| In abstract class, keyword “abstract” is mandatory to declare a method as abstract           | In interface, keyword “abstract” is optional to declare a method as abstract |
| An abstract class can have protected and public abstract methods                                    | An interface can only have public abstract methods.                                 |
| An abstract class can have a static, final, or static final variable with any access modifier. | An interface can only have a public static final (constant) variable                |


# Tasks

### Task 1

Define a Shape class with abstract methods calculateSurface(), getName() and toString(), which returns the name and area of ​​the shape, the fields width and height. Define two new classes for a triangle (Triangle) and a rectangle (Rectangle), which extend Shape class. The calculateSurface method should return the area of ​​the rectangle (height\*_width) and the triangle (height\*_width/2). Define a class for a circle with a suitable constructor, where both fields (height and width) are initialized to the same value (radius) and implement the method for calculating the area. Create an array of different shapes and output text information about them.

### Task 2

Define an interface Movement with a method move - returns the type of the movement as text.

Define interfaces Pet and Wild.

Define an abstract class Animal that implements the interface Movement, which has basic characteristics for an animal (e.g. Name, age ..). Define an abstract method sound(). Create a method for text representation of the object.

Define classes that extend Animal - Dog, Bird and Fish. Implement the methods from the interface and the abstract class.

Create an array with dogs, birds and fishes, and display the way they move and the sound they make. Based on the interface they implement, display whether they are wild or domestic.

### Task 3

Define a Vehicle interface that has 3 methods:

* changeGear, which accepts a parameter for the number of gears;
* speedUp - how much to accelerate;
* applyBrakes - how much to decelerate.

Define two classes that implement the Vehicle interface: Wheel and Car.

* For the Wheel class, when calculating acceleration, take into account the weight of the driver.

* When calculating the acceleration of the car, take into account its power.

In the main method, define two Vehicle variables, assigning different objects to them (Wheel and Car). Use all the methods in the classes and display the results.
