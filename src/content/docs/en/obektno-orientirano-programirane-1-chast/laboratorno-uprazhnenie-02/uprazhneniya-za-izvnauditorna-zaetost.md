---
title: Tasks
sidebar:
  order: 100
  label: Tasks
taskPage: true
---

## Independent Study Exercises

The tasks are arranged in increasing order of difficulty. The first tasks specify class, field, and method names. In later tasks, you will choose some of the implementation details yourself.

## Task 1

Define a `Cat` class that describes a cat.

The class must have these fields:

- `name` of type `String`;
- `breed` of type `String`;
- `age` of type `int`;
- `weight` of type `double`.

Define getter and setter methods for every field:

- `getName()` and `setName(String name)`;
- `getBreed()` and `setBreed(String breed)`;
- `getAge()` and `setAge(int age)`;
- `getWeight()` and `setWeight(double weight)`.

Create an `Application` class with a `main` method. In it, create a `Cat` object, set its values using the setter methods, and print them using the getter methods.

Example:

```java
Cat cat = new Cat();

cat.setName("Maya");
cat.setBreed("Persian");
cat.setAge(3);
cat.setWeight(4.2);

System.out.println(cat.getName());
System.out.println(cat.getBreed());
System.out.println(cat.getAge());
System.out.println(cat.getWeight());
```

## Task 2

Define a `Room` class that describes a room.

The class must have these fields:

- `number` of type `String`;
- `length` of type `double`;
- `width` of type `double`;
- `height` of type `double`.

Initialize `Room` objects using this parameterized constructor:

```java
Room(String number, double length, double width, double height)
```

Define getter methods only:

- `getNumber()`;
- `getLength()`;
- `getWidth()`;
- `getHeight()`.

Do not define setter methods. Set the values when creating the object, then read them using methods.

Create two `Room` objects and print the information for each room.

Example:

```java
Room room = new Room("A101", 6.0, 4.0, 3.0);
```

## Task 3

Extend your solution to Task 2, or define a new `Room` class with the same fields and additional calculation methods.

Implement these methods:

- `calculateArea()`, which returns the floor area;
- `calculateVolume()`, which returns the room volume;
- `getDescription()`, which returns a text description of the room.

Calculate the floor area as `length * width` and the volume as `length * width * height`.

Expected usage:

```java
Room room = new Room("B202", 5.0, 4.0, 2.8);

System.out.println(room.calculateArea());
System.out.println(room.calculateVolume());
System.out.println(room.getDescription());
```

The `getDescription()` method must return text containing the room number, area, and volume.

## Task 4

Define a `House` class that describes a house.

The class must have at least these fields:

- `address` of type `String`;
- `floors` of type `int`;
- `area` of type `double`;
- `hasGarage` of type `boolean`.

Initialize objects with a parameterized constructor and define getter methods for the values.

Also implement:

- `getDescription()`, which returns a text description of the house;
- `isLargerThan(House other)`, which returns `true` if this house has a larger area than the supplied house;
- `hasMoreFloorsThan(House other)`, which returns `true` if this house has more floors than the supplied house.

Create at least two `House` objects. Print their descriptions and compare their areas and floor counts.

## Task 5

Define a `Student` class with:

- a non-static `name` field of type `String`;
- a non-static `facultyNumber` field of type `int`;
- a static `university` field of type `String`.

Define this parameterized constructor:

```java
Student(String name, int facultyNumber)
```

Define a `getInformation()` method that returns text containing the student's name, faculty number, and university.

Create at least three `Student` objects and print their information with `getInformation()`. Then change the static `university` field through the class name and print the information again.

Example:

```java
Student.university = "Technical University of Varna";
```

The goal is to show that a static field belongs to the class and is shared by all its objects.

## Task 6

Define a `record` named `Book` to store information about a book.

The record must have these components:

- `title` of type `String`;
- `author` of type `String`;
- `year` of type `int`;
- `price` of type `double`.

Create at least two `Book` objects and use the generated methods:

- `title()`;
- `author()`;
- `year()`;
- `price()`;
- `toString()`;
- `equals(Object other)`.

Check the result of `equals()` when two books have identical values and when at least one value differs.

Add a compact constructor that removes leading and trailing whitespace from the title. For this task, assume the title reference is non-null and the price is non-negative. Handling invalid data with exceptions is covered in Exercise 6.

Also define a regular `BookClass` with the same data, a constructor, and getter methods. Compare the amount of code, getter method names, and the results of `==`, `equals()`, and `toString()` for two distinct objects with identical data.

## Task 7

Define an `enum` named `OrderStatus` to represent an order's status.

It must contain at least these values:

- `NEW`;
- `PAID`;
- `SHIPPED`;
- `DELIVERED`;
- `CANCELLED`.

Define an `Order` class. It must have a field for the status, of type `OrderStatus`. Choose the other fields yourself based on the information an order needs.

The `Order` class must have:

- a parameterized constructor;
- getter methods for its fields;
- a `getDescription()` method that returns a text description of the order;
- a `changeStatus(OrderStatus status)` method that changes the order's current status.

Create several orders with different statuses. Use conditional statements to check whether an order has been delivered, cancelled, or is still being processed.

## Task 8

Define a class of your choice that describes a real object from an educational, commercial, or household system. Examples include `Course`, `Product`, `Vehicle`, `BankAccount`, or `HotelReservation`.

For your class, decide:

- its name;
- at least four fields with suitable types;
- which values should be set through a constructor;
- which getter methods are needed;
- whether setter methods are needed;
- at least two methods that calculate something, perform a check, or return a text description.

Use at least one of the `this` or `static` keywords meaningfully.

Create a sample program that instantiates at least three objects and demonstrates all the methods you defined.

## Task 9 — References and `this` as an Argument

Use the `Student` class from Task 5. Create two variables that refer to the same student and a third variable that refers to a separate student with the same data. Predict the results of `==`, then verify them. Change the name through the second reference and inspect the first reference with a debugger.

Add `StudentPrinter.print(Student student)` and a `printCard()` method to `Student` that calls `StudentPrinter.print(this)`. Identify the parameter and the argument. Implement separate methods for changing the name and assigning a new object to a parameter. Show why only the first method changes the state visible through the original student reference.
