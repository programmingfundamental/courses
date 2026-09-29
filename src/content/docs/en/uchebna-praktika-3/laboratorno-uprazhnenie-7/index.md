---
title: "Lab 7"
sidebar:
  order: 7
---

# Laboratory Exercise 7

## Context Selection and Verification of AI-Generated Tests

### Objective

The objective of this exercise is to examine how context selection influences test scenarios and unit tests generated with the assistance of AI, and how their correctness can be verified independently of the AI assistant's evaluation.

By comparing different contexts, the difference between tests derived from the current implementation and tests based on expected business behavior will be analyzed. The exercise will cover the selection of appropriate test scenarios, the use of real and mock objects, and the verification of generated assertions.

### Unit тестване

A unit test verifies a small, isolated part of the application's behavior – for example, the public behavior of an individual class or method. It should be fast, independent, and repeatable. Unit testing does not start the entire Spring application context.

Tests should verify observable behavior, not internal implementation details.

When selecting test cases, consider:

•	happy path – normal expected execution; 

•	negative path – a situation in which an operation should not be allowed, or the result should be negative; 

•	boundary/edge case – values or states around the boundary of a particular rule. 

### Context Selection When Generating Tests

When generating tests, the required context depends on the behavior to be verified. Providing only the current implementation may result in tests that reproduce the existing behavior without determining whether it corresponds to the business requirements.

A distinction should be made between:

•	the expected behavior according to the requirements; 

•	its current implementation; 

•	the behavior that the specific test should verify. 

From this exercise onward, the necessary **project context is selected independently**. Only files relevant to the current task are added as attachments. If missing context is identified during the work, it is supplemented with the necessary files.

### Verification of AI-Generated Tests

An AI-generated test should be verified in the same way as any other generated code. The fact that a test compiles and passes successfully does not prove that it verifies the correct behavior.

The evaluation should check whether:

•	the test scenario and expected result correspond to the business requirements; 

•	the test data correctly represent the scenario being tested; 

•	the assertions verify the specific expected behavior; 

•	significant negative and boundary cases are covered; 

•	the test does not unnecessarily depend on internal implementation details; 

•	the mock objects replace dependencies that actually need to be isolated. 

Successful execution of the tests shows that the current implementation satisfies the assertions that were written but does not prove that the correct test scenarios were selected or that all significant cases were tested. 

Therefore, both the test code and the expectations expressed in it must be independently verified.

### Structure and Assertions in a Unit Test

A unit test can be organized using the Given – When – Then structure:

•	Given – prepare the initial data, the object under test, and the required dependencies; 

•	When – execute the behavior being tested; 

•	Then – compare the obtained result with the expected behavior. 

JUnit 5 provides assertions such as *assertEquals()*, *assertTrue()*, *assertFalse()*, *assertNull()*, *assertNotNull()*, and *assertThrows()* for verifying results.

The selected assertion should prove the specific expected behavior. For example, checking only with *assertNotNull()* is not sufficient when the requirement specifies a particular value or state of the result.

### Real Objects and Mock Objects

In unit testing, the object whose behavior is being verified is created as a real object. Its dependencies may be replaced with mock objects when their actual execution is not the subject of the current test.

For example, when unit testing a service that uses a repository, a real service and a mock repository are typically used. In this way, the test does not depend on a database and verifies the behavior of the service under test.

**Mockito** allows mock dependencies to be created using @Mock and injected into a real instance of the class under test using @InjectMocks. With JUnit 5, Mockito can be enabled using @ExtendWith(MockitoExtension.class).

The behavior of a mock dependency can be specified using *when(...).thenReturn(...)*. When interaction with the dependency is part of the behavior being verified, it can be checked using verify().

Not every dependency should be replaced with a mock. Simple input and domain objects can usually be real objects. Excessive use of mock objects can make tests dependent on internal implementation rather than observable behavior.

### Unit Test and Integration Test

In a unit test, the class under test is instantiated directly, while its dependencies are replaced with mock objects when necessary. The Spring application context is not started.

An integration test verifies the interaction of several real components. When a Spring application context is required for the test, @SpringBootTest can be used.

For generated tests, it should be verified whether the proposed test type corresponds to the assigned task and whether additional infrastructure is being started unnecessarily.
