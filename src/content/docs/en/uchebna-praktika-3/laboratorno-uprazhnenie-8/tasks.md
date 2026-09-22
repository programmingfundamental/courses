---
title: Tasks
taskPage: true
sidebar:
  label: Tasks
  order: 100
---
### Initial State

Use the final version of the application from Laboratory Exercise 7, containing the implemented business functionality and a set of unit tests for selected parts of the service layer.

The application contains REST endpoints for working with requests, offers, and service executions, Bean Validation for input DTOs, specialized exceptions, and a consistent error response format.

### Practical Tasks

*Analysis of ServiceRequestController*

Use ServiceRequestController as the first object to be tested. Before generating test code, analyze the controller test scenarios that are required.

Prompt:

```
Analyze the existing ServiceRequestController and identify the controller test cases needed to verify its HTTP behavior.
Consider successful requests, request validation, business errors, HTTP status codes and response bodies.
Distinguish what should be tested at the controller/web layer from behavior that belongs to service unit tests.
Do not generate or modify code yet.
```

Compare the resulting suggestions with the actual controller implementation and the existing API contract. Determine:

- whether all REST operations are covered;
- whether the proposed HTTP status codes are correct;
- whether the response body is verified;
- whether invalid requests are considered;
- whether business errors that the controller may receive from the service layer are considered;
- whether scenarios are proposed that actually belong to service unit tests;
- whether scenarios are proposed that do not exist in the current requirements.

*Generating Controller Tests*

After approving the test scenarios, formulate a prompt for the AI assistant to create the controller tests.

*Analysis of the Generated Tests*

Run and analyze the generated tests. Check whether the AI assistant:

- uses an appropriate type of test;
- uses @SpringBootTest unnecessarily;
- starts or creates real dependencies between services;
- mocks the controller itself;
- verifies only the HTTP status;
- verifies the response body when an endpoint returns data;
- uses the correct HTTP status codes;
- verifies Bean Validation;
- verifies the standardized error response;
- duplicates business logic from the unit tests;
- creates too many mock objects;
- adds tests for behavior that does not exist.

If problems are identified, make the necessary corrections.

*Extending the Controller Tests Set*

After analyzing the first generated tests, select REST operations from the remaining controllers.

Suitable scenarios may include:

- successful offer creation;
- an attempt to create an offer when the request is in an invalid state;
- acceptance of a valid offer;
- an operation on an expired offer;
- successful execution scheduling;
- an invalid scheduling date;
- starting an execution;
- an attempt to perform a disallowed cancellation;
- a request for a non-existent resource.

Select several scenarios that verify different aspects of HTTP behavior rather than multiple variants of the same operation.

For the selected scenarios:

- determine the expected HTTP behavior;
- formulate a prompt;
- analyze the generated code;
- run the tests;
- correct any identified problems.

### Lab Result

A set of automated tests for selected REST operations that verifies:

- successful HTTP requests;
- Bean Validation;
- HTTP status codes;
- JSON responses;
- business error handling;
- the consistent error response format.

The tests use an appropriate level of isolation and do not unnecessarily start the entire Spring application context.
