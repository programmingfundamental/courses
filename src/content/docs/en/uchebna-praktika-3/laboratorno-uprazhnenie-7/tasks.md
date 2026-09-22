---
title: Tasks
taskPage: true
sidebar:
  label: Tasks
  order: 100
---
### Initial State

Use the reference state of the project after Laboratory Exercise 6.

The application functionality has been implemented, and some of the business logic has been separated into specialized services, including WorkingTimeService, LoyaltyService, and CancellationService.

### Practical Work

The first object to be tested is WorkingTimeService. The class contains business logic that does not depend on other application components and therefore allows the basic principles of unit testing to be examined without using mock objects.

Before generating test code, analyze the required test scenarios.

Prompt:

```
Analyze the existing WorkingTimeService and identify the unit test cases needed to verify its behavior.
Consider normal cases, boundary cases and invalid or exceptional cases where applicable.
For each proposed test, briefly state what behavior it verifies and the expected result.
Do not generate or modify code yet.
```

Analyze the resulting proposal against the actual implementation and business rules. Determine:

- whether all public operations are covered;
- whether different types of test scenarios are considered;
- whether any boundary values are missing;
- whether there are unnecessary or duplicate scenarios;
- whether testing is proposed for behavior that is not part of the requirements;
- whether accidental behavior of the current implementation is being treated as a business rule.

Evaluate whether the test scenarios proposed by the AI follow the business requirements or merely the current implementation.

*Generating Unit Tests*

Formulate a prompt for the AI assistant to create unit tests for the approved test scenarios.

*Analysis of the Generated Tests*

Check whether:

- the tests actually verify business behavior;
- assertions verify a specific expected result rather than only notNull;
- Given – When – Then is used meaningfully;
- business logic is not repeated in the test;
- mock objects are not used unnecessarily;
- @SpringBootTest is not used for an ordinary unit test;
- there is no attempt to test private methods directly;
- parameterized tests combine genuinely similar scenarios;
- all important boundary cases are covered.

If problems are identified, correct the tests. If necessary, formulate an additional prompt for the AI assistant.

*Unit Testing a Service with Dependencies*

Analyze the dependencies of LoyaltyService and determine which objects should be real and which can be replaced with mock objects. Identify test scenarios that verify the loyal-customer business rule.

Consider both normal and boundary cases.

Formulate a prompt for the AI assistant to generate unit tests for the selected scenarios.

*Extending the Test Suite*

Select at least two other business rules from the application and create appropriate tests for them. Possible subjects for testing include:

- allowed and disallowed cancellation;
- valid and invalid state transitions;
- offer expiration;
- price calculation;
- a missing request;
- boundary values for deadlines and scheduling.

*Final Analysis*

Use the AI assistant to review the created test suite. Formulate a prompt requesting an analysis of the quality and completeness of the tests without modifying the existing code.

Analyze the AI assistant's recommendations and determine which of them are justified. Pay particular attention to:

- missing scenarios;
- missing boundary cases;
- duplicate tests;
- excessive use of mock objects;
- tests that depend on implementation details;
- assertions that do not demonstrate the expected behavior.

After the analysis, make only the justified changes.

### Lab Result

A small and meaningful test suite for key parts of the application's business logic, including tests for a class without dependencies and for a service with mock dependencies. The tests use JUnit 5, the Given – When – Then structure, and parameterized tests where appropriate.
