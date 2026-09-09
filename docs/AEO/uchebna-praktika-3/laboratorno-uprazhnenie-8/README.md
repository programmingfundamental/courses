---
layout: default
title: Laboratory exercise 8
parent: Training Practice 3
has_children: true
nav_order: 8
---

# Laboratory exercise 8

## Testing REST Controllers

### Objective

The objective of this exercise is to develop automated tests for REST controllers and to analyze whether the tests verify the correct behavior of the web layer.

The exercise distinguishes between unit, controller, and integration tests and focuses on selecting the appropriate testing level according to the behavior that needs to be verified.

### Initial State

Use the final version of the application from Laboratory Exercise 7, containing the implemented business functionality and a set of unit tests for selected parts of the service layer.

The application contains REST endpoints for working with requests, offers, and service executions, Bean Validation for input DTOs, specialized exceptions, and a consistent error response format.

### Brief Theory

*Different Levels of Testing*

Different types of tests verify different aspects of application behavior.

Unit tests verify an individual class in isolation from its dependencies.

A controller test verifies the behavior of the application's web layer.

It can verify:

- HTTP methods and URLs;
- request body;
- JSON-to-DTO conversion;
- Bean Validation;
- HTTP status codes;
- response body;
- response DTO-to-JSON conversion;
- exception handling;
- the consistent error response format.

The business logic of the service layer is not the subject of a controller test. The controller's dependencies on the corresponding service objects can be replaced with mock objects.

An integration test verifies the interaction of several real application components.

When the entire Spring application context needs to be loaded, the @SpringBootTest annotation can be used.

*@WebMvcTest*

The annotation is used for tests focused on the Spring MVC layer. For example:

```java
@WebMvcTest(ServiceRequestController.class)
class ServiceRequestControllerTest {
    // ...
}
```

With this type of test, the entire application does not need to be started. Only the components required for testing the web layer are loaded.

This makes it possible to verify the behavior of the controller and the Spring MVC infrastructure without involving the actual execution of all service and repository components.

*MockMvc*

MockMvc allows HTTP requests to be sent to the Spring MVC application in a test environment without starting a real web server. For example, it can simulate a POST /api/requests request with a JSON request body and verify the resulting HTTP response.

The basic structure is:

```java
mockMvc.perform(...)
        .andExpect(...);
```

For example:

```java
mockMvc.perform(
        get("/api/requests/{id}", requestId)
    )
    .andExpect(status().isOk());
```

The Given – When – Then structure continues to be used in controller tests:

```java
@Test
public void shouldReturnServiceRequest() throws Exception {
    // given
    // prepare test data and service dependency behavior

    // when
    ResultActions result = mockMvc.perform(get("/api/requests/{id}", requestId));

    // then
    result.andExpect(status().isOk());
}
```

*Verifying the HTTP Response*

A controller test should not verify only that some response was received. It should verify behavior that forms part of the REST API contract.

For example, the HTTP status:

- .andExpect(status().isOk())
- .andExpect(status().isCreated())
- .andExpect(status().isBadRequest())
- .andExpect(status().isNotFound())
- .andExpect(status().isConflict())

The expected status must correspond to the specific API contract.

*Response body*

For a JSON response, specific fields can be verified:
- .andExpect(jsonPath("$.id").value(1))
- .andExpect(jsonPath("$.status").value("SUBMITTED"));

It is not sufficient for the test to demonstrate only that the request completed successfully. When an endpoint returns data, the test should verify that the returned data is correct.

*JSON request body*

For POST and PUT operations, a Java DTO object often needs to be converted to JSON.

ObjectMapper can be used for this purpose.

Conceptually:

```java
String json = objectMapper.writeValueAsString(request);

//and:

mockMvc.perform(
        post("/api/requests")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
);
```

This allows the test to send the request in a format equivalent to the one used by a REST client.

*Testing Bean Validation*

Controller tests make it possible to verify whether constraints on input DTOs actually participate in HTTP request processing.

For example, when a required field is missing, the expected result may be:

```java
.andExpect(status().isBadRequest());
```

When appropriate, the content of the error response should also be verified.

This is different from directly invoking a Java method:

```java
controller.create(invalidRequest);
```

With a direct invocation, the entire Spring MVC process for HTTP request deserialization and validation is not automatically executed.

*Testing Error Handling*

In the application, business errors are represented by specialized exceptions and mapped to HTTP responses by the common exception handler.

In a controller test, the service dependency can be configured to throw the corresponding exception.

For example, conceptually:

```java
when(service.someOperation(...))
        .thenThrow(new RequestNotFoundException(...));
```

After sending the HTTP request, verify whether the web layer maps the error to the correct:

- HTTP status;
- error response;
- JSON response format.

In this way, the controller test does not verify why the service determined that the request does not exist. That belongs to a unit test of the service layer.

#### What a Controller Test Should Not Verify

A controller test should not repeat the unit tests of the business layer.

For example, it should not prove again:

- how the price is calculated;
- how a loyal customer is determined;
- how working days are calculated;
- why a particular cancellation is allowed;
- how a particular status transition is performed.

These rules should be verified through unit tests of the corresponding classes.

The controller test verifies the HTTP behavior through which this functionality is exposed.

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

### Comparing Testing Levels

For one selected business scenario, determine what would be verified at the different testing levels.

For example, for a disallowed operation:

- service unit test – whether the implemented logic correctly identifies the disallowed operation and throws the appropriate exception;
- controller test – whether the REST controller maps this exception to a 409 response with the correct response body;
- integration test – whether the entire scenario works correctly when the real components execute together.

The same scenario does not always need to be implemented at every testing level. The objective is to select the level that verifies the behavior of interest with the smallest unnecessary scope.

*Final Analysis with AI*

Use the AI assistant to analyze the created set of controller tests.

Look for:

- uncovered REST operations;
- missing negative scenarios;
- missing response-body assertions;
- incorrect HTTP expectations;
- duplication with unit tests;
- unnecessary use of @SpringBootTest;
- excessive use of mock objects;
- tests that depend on a specific implementation;
- tests that pass without actually demonstrating the API contract.

Evaluate the resulting suggestions and apply only the justified changes.

### Lab Result

A set of automated tests for selected REST operations that verifies:

- successful HTTP requests;
- Bean Validation;
- HTTP status codes;
- JSON responses;
- business error handling;
- the consistent error response format.

The tests use an appropriate level of isolation and do not unnecessarily start the entire Spring application context.


