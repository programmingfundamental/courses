---
layout: default
title: Laboratory exercise 3
parent: Training Practice 2
has_children: true
nav_order: 3
---

# Laboratory exercise 3

## SOLID Principles

#### Task 1

Develop an application for managing printing devices in an office.

The system may use different types of devices:

- a standard printer;
- a multifunctional device;
- a scanner.

Not all devices support the same operations. Depending on its type, a device may be able to:

- print;
- scan;
- copy;
- send faxes.

The system should allow different devices to be added and should perform only the operations that each particular device actually supports.

The interfaces should be designed so that a class is not forced to implement operations that it cannot perform.

In the main program, demonstrate the use of at least three different devices.

#### Task 2

Develop an application for calculating employee compensation.

The system works with different types of employees. Each employee has a name and base compensation.

Permanent employees receive a monthly bonus, hourly employees receive additional compensation based on the number of hours worked, while interns receive no additional compensation.

The system should allow the final compensation for each employee to be calculated and the total compensation for all employees to be displayed.

The solution should be designed so that adding a new type of employee does not require modifying the existing logic for processing all employees.

Checks such as:

```java
if (employeeType == ...)
```

or:

```java
instanceof
```

must not be used to determine how the compensation is calculated.

#### Task 3

Develop an application for processing orders in an online store.

For each order, the order number, customer, list of products, and total amount are stored. When an order is completed, the system should:

- calculate the final amount;
- save the order information;
- send a confirmation to the customer.

Initially, orders are stored in a file, while confirmations are displayed in the console.

The application should be designed so that:

- the order storage mechanism can be replaced;
- the customer notification mechanism can be replaced;
- adding a new storage or notification mechanism does not require modifying the order-processing logic.

Demonstrate the application using at least two different implementations of one of the replaceable components.

