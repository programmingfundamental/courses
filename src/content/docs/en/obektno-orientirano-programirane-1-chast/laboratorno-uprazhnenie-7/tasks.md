---
title: Tasks
taskPage: true
sidebar:
  label: Tasks
  order: 100
---
### Task 1

Create a program for employees in a company.

To do this, you will need:

* Employee class, which will describe an employee and his three names, basic salary and position. Methods for accessing the class fields. Method for text representation. (The employee's full name)
* Company class, which will describe the company with its employees and names. In a constructor that accepts the number of employees in the company, initialize the array. Create two methods
* getInfo() - which will return as a result the information about the company with all employees of the company. Use string concatenation
* getInformation() - which will return as a result the information about the company with all employees of the company. Use a string buffer
* getEmployees(String name) - Returns a list of the names of employees in which the given name exists

Check the execution of the methods for different numbers of employees in the company with System.out.println(java.time.LocalDateTime.now()); at the beginning and end of the execution of the methods is there a difference between String and StringBuilder?

### Task 2

Write a program for car parking:

For this purpose you will need:

* class Car with number, width and length
* class Truck with number, width and length, load capacity
* class Bus with number, width and length, number of seats

Consider whether the classes should have a common parent class?

* class Parking, with a list of 1000 vehicles.
* constructor that accepts an input parameter, a formatted string
* Car:В4747КК,4,6;Truck:В4747КК,4,6,3;Bus:В4747КК,4,6,59
* Parse the string and for each type of vehicle create an object in the array
* Text representation of the parking lot, use string concatenation or string buffer, depending on who performed better in the first task
