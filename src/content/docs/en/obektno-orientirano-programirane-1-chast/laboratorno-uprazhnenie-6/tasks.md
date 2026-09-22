---
title: Tasks
taskPage: true
sidebar:
  label: Tasks
  order: 100
---
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
