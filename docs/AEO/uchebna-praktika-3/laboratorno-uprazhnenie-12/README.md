---
layout: default
title: Laboratory exercise 12
parent: Training Practice 3
has_children: true
nav_order: 12
---

# Laboratory exercise 12

## Independent Development with an AI Assistant – Part II

### Objective

The objective of this exercise is to complete the development of the application from Laboratory Exercise 11 by using an external relational database, adding a user interface, and introducing automated verification of the core functionality.

During development, the AI assistant should be used independently to analyze the required changes, generate and review code, identify problems, and propose corrections.

The work continues from the state reached in Laboratory Exercise 11. The order of the individual tasks may be adapted to the current state of the project.

### Practical Task

#### Migration to PostgreSQL

The developed application should be configured to use PostgreSQL instead of the H2 database used so far.

Before making changes, analyze the existing project and determine the modifications required for migration to PostgreSQL.

Pay attention to:

- the required dependencies;
- database connection configuration;
- how the database tables are created and used;
- compatibility of the existing entity classes and repository components;
- whether changes are required in other parts of the application.

Business logic should not be modified solely because the database technology is being changed, unless a specific technical reason for such a change is identified.

After configuration, verify that the application's core operations work correctly with PostgreSQL and that the data is persisted in the database.

#### User Interface

Develop a simple React user interface for the application using the existing REST API.

The interface should support the core business process, including:

- viewing purchase information;
- viewing submitted complaints;
- submitting a new complaint for a purchased product;
- approving or rejecting a complaint;
- specifying the resolution method for an approved complaint.

A sophisticated user interface design is not required. The primary objective is to provide working interaction between the frontend and backend and to allow the main system operations to be performed.

When an operation fails, the user should receive appropriate information about the problem that occurred.

#### Automated Testing

Automated tests should be developed for the application's significant business logic.

Selected successful and unsuccessful scenarios should be verified, for example:

- successful complaint submission;
- an attempt to submit a complaint after the allowed deadline has expired;
- an attempt to submit a second active complaint for the same purchased product;
- allowed and disallowed state transitions;
- successful specification of a resolution method for an approved complaint.

It is not necessary to cover every possible scenario with a test. The selected tests should verify significant business rules rather than only trivial operations.

At least one integration test should also be implemented to verify a core business scenario through the application's REST API.

#### Review of the Final Solution

After the functionality has been completed, review the project with the help of the AI assistant.

The review should look for:

- unnecessary or duplicated components;
- violations of separation of responsibilities;
- business logic placed in an inappropriate layer;
- unused code;
- insufficient error handling;
- AI-proposed changes that increase complexity without a clear need.

Proposed changes should not be accepted automatically. They should be applied only when they improve the solution without changing the specified business requirements.

For the discussion in Laboratory Exercise 13, retain at least one example of an AI-generated solution or proposal that was accepted, corrected, or rejected, together with the reason for the decision.

### Result

By the end of the independent work in Laboratory Exercises 11 and 12, a complete web application for complaint management should have been developed.

The application should:

- implement the specified business process and business rules;
- use PostgreSQL for data persistence;
- provide a REST API and a React user interface for the main operations;
- handle invalid business operations;
- include automated tests for significant business rules and at least one integration test.

It should be possible to demonstrate one complete successful business scenario, as well as at least one scenario in which an operation is rejected because a business rule has been violated.

The resulting solutions and the experience gained from using the AI assistant will be reviewed and compared in Laboratory Exercise 13.

