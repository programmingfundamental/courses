---
title: Laboratory exercise 10
sidebar:
  order: 10
---

# Laboratory exercise 10

## Application Integration and Integration Testing

### Objective

The objective of this exercise is to extend the developed application with a separate Notification Service that communicates with the main application over HTTP.

When an offer is created, the main application should send information to the new Notification Service. After the communication has been implemented, an integration test should be created to verify the interaction between the two applications.

During the exercise, the AI assistant is used to analyze the existing code, propose a solution, generate the required components, and create the integration test. The proposed solutions must be reviewed and evaluated before they are accepted.

### Brief Theory

#### Integration Testing

Integration tests verify whether separate components or applications work correctly together.

In unit tests, a single class is usually isolated and its dependencies may be replaced with mock objects. In an integration test, real components participate and the interaction between them is verified.

| Unit Test                        | Integration Test                                                              |
| -------------------------------- | ----------------------------------------------------------------------------- |
| Verifies an individual component | Verifies interaction between components                                       |
| Dependencies are often mocked    | Real dependencies are used when they are part of the integration being tested |
| Runs quickly and in isolation    | Usually requires more infrastructure                                          |
| Detects errors in specific logic | Detects problems in component interaction                                     |


In Spring Boot, an integration test may start a real application context, web server, database, and other required components.

When two applications communicate over HTTP, an integration test may verify:

- whether the request is actually sent to the other application;
- whether the endpoint and HTTP method are correct;
- whether the sent data matches the expected contract;
- whether the second application can receive the request;
- what happens when the business operation in the first application fails.

An important part of integration testing is defining the integration boundary. For example, the HTTP server may be replaced with a stub, but in that case the test verifies only the outgoing communication of the first application. If the objective is to verify the actual interaction between two applications, both applications must participate in the test.
