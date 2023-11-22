---
layout: default
title: Laboratory exercise 5
parent: Object-oriented Programming - 1 part
has_children: true
nav_order: 5
permalink: /docs/object-oriented-programming-1-part/laboratory-exercise-5
---

# Laboratory exercise 5


# Practice

### Task 1

Create program for company employees.

Define:

* class Employee with properties for first, middle and last name; access methods for all fields and method for textual description (returns full name of employee)
* клас Фирма, който да описва фирмата с нейните служители и имена. В конструктор който приема броя на служителите във фирмата, инициализирайте масива. Създайте два метода&#x20;
* class Company which describes all its employees. Define constructor with number of employees and inside initialize an array of employees. Class Company has following methods:
  * getInfo() - returns information for all employees, uses concatenation
  * getInformation() - returns information for all employees, uses StringBuffer
  * getEmployees(String name) - returns list of employees with the name present.

Check the behavior with different number of employees and put System.out.println(java.time.LocalDateTime.now()) at the beginning and at the end of method calls to see the difference.

### Task 2

Create program for parking lot:

Define:

* Class Car with number, width and length
* Class Truck with number, width, length and load
* Class Bus with number, width, length and number of seats

Decide if the classes could have common parent class.

* Class ParkingLot with 1000 vehicles
* Constructor with string parameter
  * Car:В4747КК,4,6;Truck:В4747КК,4,6,3;Bus:В4747КК,4,6,59
  * Divide the input string and for each part define an object into the array
* Method for textual description with concatenation or StringBuilder by choice.





