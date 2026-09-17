---
layout: default
title: Laboratory exercise 11
parent: Training Practice 3
has_children: true
nav_order: 11
---

# Laboratory exercise 11

## Independent Development with an AI Assistant – Part I

### Objective

The objective of this exercise is to independently apply the approaches used so far for software development with the assistance of an AI assistant.

Based on the given business case, the requirements should be analyzed, an appropriate solution should be designed, and the core functionality of a web application should be implemented.

The AI assistant may be used for analysis, design, code generation, and code review. The resulting suggestions should be evaluated before they are incorporated into the project.

### Practical Task

#### Business Case – Complaint Management System

Develop a web application for managing complaints concerning purchased products.

The system contains information about completed purchases and the products included in those purchases. A customer may submit a complaint about a product from an existing purchase, specifying a reason and a description of the identified problem.

A complaint may be submitted only within a specified period after the purchase date. A newly submitted complaint initially has the status SUBMITTED.

After review, the complaint may be:

- approved – APPROVED;
- rejected – REJECTED.

When a complaint is approved, a resolution method must be specified – replacement of the product or refund of the amount paid.

A new complaint for the same purchase and product must not be allowed if an active complaint already exists for that product.

A completed complaint cannot be modified.

The system should allow users to view completed purchases and submitted complaints.

#### Analysis and Design

Before generating code, use the AI assistant to analyze the given business case.

The analysis should identify:

- the main objects and the relationships between them;
- the required business rules and validations;
- the states and allowed state transitions;
- the required REST operations;
- the main application layers and their responsibilities.

The AI assistant's proposal should be reviewed before proceeding with the implementation. Functionality, roles, states, or infrastructure that do not follow from the requirements must not be added automatically.

#### Implementation

Create a Spring Boot application implementing the core business process.

For the first part of the independent work, at least the following should be implemented:

- the data model and persistence layer;
- creation and retrieval of purchase information;
- submission and retrieval of complaints for purchased products;
- verification of the complaint submission deadline;
- prevention of more than one active complaint for the same purchased product;
- approval and rejection of a complaint;
- specification of the resolution method for an approved complaint;
- control of the allowed complaint state transitions;
- input validation;
- appropriate handling of invalid business operations;
- a REST API for the implemented functionality.

H2 may be used for this part of the task.

A user interface is not required as part of Laboratory Exercise 11.

### Lab Result

By the end of the exercise, a working backend application for complaint management should be developed.

Through its REST API, the application should allow the core business process to be demonstrated – from registering purchase information and submitting a complaint to approving or rejecting it and specifying a resolution method when it is approved.

It should also be possible to demonstrate cases in which the system rejects an operation because a business rule has been violated, for example an expired complaint submission deadline, an existing active complaint for the same purchased product, or an invalid state transition.

The implemented application will be used as the initial version for Laboratory Exercise 12, where development will continue with a user interface, automated testing, and migration to PostgreSQL.


