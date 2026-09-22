---
title: Tasks
taskPage: true
sidebar:
  label: Tasks
  order: 100
---
#### Task 1:

Create a class Employee that has the following attributes:

· Company name (static)

· Number of employees (static) (incremented when creating a new employee)

· First name (visible to the successor)

· Last name (visible to the successor)

· Personal identification number (private)

· City (visible to the successor)

· Salary (private)

And methods:

· accessor methods, depending on the access modifier;

· constructors;

· method for text representation of the object;

· salary, which returns 0;

· static method that returns the total number of employees, accepting an array of employees as a parameter.


#### Task 2:

Create a class Manager that inherits the Employee class and has the following attributes:

· Sector (private)

Methods:

· Accessor methods, depending on the access specifier;

· Constructors;

· method for text representation of the object;

· Method "salary" that overrides the salary method of the parent class, returning the value of the salary attribute.

· Method "earnings", which accepts two parameters - number of working days and earnings for one working day;

· Method "earnings", which accepts one parameter - earnings for one working day and returns the multiplication of the earnings for one working day by 22 working days;


#### Task 3:

Create a class Clerk that extends Employee with the following attributes:

· Additional percentage;

· Sector (private);

The following methods:

· Access methods, depending on the access specifier;

· Constructors;

· method for text representation of the object;

· Method “salary” that overrides the salary method of the parent class, returning the value of the salary attribute with the additional percentages added to the salary.

· Method for total work experience without a parameter, which returns the work experience in years (calculated based on the year of birth of the clerk, assuming that he started working at 23 years old);

· Method for total work experience with one parameter of type String, which indicates how many years the clerk has not worked (these years must be subtracted from the total work experience);

· Method for total work experience with one parameter of type double , which indicates how many years the clerk has not worked (these years must be subtracted from the total work experience);


#### Task 4:

Create a class that contains a main method:

\- Create 2 managers and display the text representation of the objects;

\- Calculate the manager's salary;

\- Calculate his earnings for 18 working days and a full working month

\- Create 4 clerks and display the text representation of the objects;

\- Calculate the salary of each of the clerks

\- Display the length of service of each employee, with two of them having missed 2 and 3 years respectively (use the three different methods)

\- Display how many employees there are in the entire company

\- Display the name of the company where all the employees work.
