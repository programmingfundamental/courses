---
title: "Lab 8"
sidebar:
  order: 8
---

# Laboratory Exercise 8

## Context Selection According to the Task and Test Level

### Objective

The objective of this exercise is to examine how the task and the selected level of testing determine the context required when working with an AI assistant to generate automated tests.

By moving from unit testing of selected business functionality to testing the corresponding REST behavior, the exercise will analyze which information is relevant to different test levels and which information is unnecessary for the specific verification.

The practical work focuses on moving from unit testing to controller testing and verifying the generated tests against the actual API contract, the current implementation, and the results of their execution.

### Different Levels of Testing

Different levels of testing verify different scopes of application behavior.

A **unit** test verifies the behavior of an individual class in isolation. The class under test is instantiated directly, while its dependencies are replaced with mock objects when necessary.

A **controller** test verifies the behavior of the web layer. It can verify the HTTP method and URL, request body processing, Bean Validation, HTTP status, response body, and the mapping of errors to the corresponding HTTP response. The business logic of the service layer is not the subject of a controller test.

An **integration** test verifies the interaction of several real application components and may include the Spring application context when necessary.

The choice of test level is determined by the behavior that needs to be verified. A broader scope does not automatically mean a better test.

### Context Selection According to the Task and Test Level

The context required by the AI assistant to generate tests depends not only on the functionality being tested but also on the level at which it should be verified.

For a unit test of a service, relevant context may include the code of the class under test, its dependencies, and the business rules that determine the expected behavior.

For a controller test of related functionality, the focus changes. Relevant context may include the REST endpoint, HTTP method, request and response DTOs, validation rules, HTTP status codes, error format, and the behavior that the controller expects from the service layer. The internal implementation of the business logic is usually unnecessary when it is not the subject of the current test.

For an integration test, broader context is required because the interaction between real components is being verified.

Therefore, relevant context is not a fixed set of files or information. It is determined by the question that the specific test is intended to answer. Providing more context is not an objective in itself – unnecessary information may broaden the task and direct the AI toward behavior that does not belong to the selected test level.

### Controller Tests with Spring MVC

For tests focused on the Spring MVC layer, @WebMvcTest can be used. Such a test loads the components required to verify the web layer without the need to start the entire application.

MockMvc allows HTTP requests to be sent to the Spring MVC application in a test environment without starting a real web server. It can be used to verify:

•	HTTP status; 

•	JSON response content; 

•	request body processing; 

•	Bean Validation; 

•	mapping of exceptions to the corresponding HTTP response. 

Controller dependencies on the service layer can be replaced with mock objects. In this way, the controller test verifies how the web layer responds to a particular result or error from the service layer without testing the business logic itself again.

ObjectMapper can be used when working with a JSON request body, while the contents of a JSON response can be verified using jsonPath().

### Verification of AI-Generated Controller Tests

An AI-generated controller test should be evaluated against the task, the actual API contract, and the selected test level.

Check whether:

•	the HTTP method and URL correspond to the actual API; 

•	the request data, validation scenarios, HTTP status, and response body correspond to the API contract; 

•	the behavior of the mocked service is appropriate for the scenario being tested; 

•	the test verifies the web layer without duplicating business logic from the unit tests; 

•	@SpringBootTest or other infrastructure is not used unnecessarily; 

•	the AI has not added endpoints, fields, status codes, or other API behavior that does not exist in the application. 

Successful execution of a controller test does not prove the correctness of the business logic behind the mocked service and does not replace tests at other levels.


•	ИИ не е добавил endpoints, полета, status кодове или друго API поведение, което не съществува в приложението.

Успешното изпълнение на controller тест не доказва коректността на бизнес логиката зад mock-натия service и не замества тестовете на останалите нива.
