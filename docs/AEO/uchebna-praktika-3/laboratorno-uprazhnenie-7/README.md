---
layout: default
title: Laboratory exercise 7
parent: Training Practice 3
has_children: true
nav_order: 7
---

# Laboratory exercise 7

## Unit Tests

### Objective

The objective of this exercise is to develop unit tests for selected parts of the application's business logic and to analyze the quality of tests proposed by an AI assistant.

The exercise distinguishes between:

- a real object and a mock;
- happy path, negative path, and boundary/edge cases;
- unit tests and Spring integration tests;
- tests of observable behavior and tests that depend on implementation details.

### Initial State

Use the reference state of the project after Laboratory Exercise 6.

The application functionality has been implemented, and some of the business logic has been separated into specialized services, including WorkingTimeService, LoyaltyService, and CancellationService.

### Brief Theory

*Unit test*

A unit test verifies a small, isolated part of the application's behavior – a method, class, or individual business responsibility.

A unit test should be:

- fast;
- independent;
- repeatable;
- focused on specific behavior.

A unit test does not start the entire Spring application unless this is required for the particular type of test.

*Given – When – Then*

Each test should be structured in three parts:

```java
@Test
public void shouldReturnFalseAtStartOfWorkingHours() {
    // given
    LocalDateTime time = LocalDateTime.of(2026, 8, 10, 9, 0);

    // when
    boolean result = workingTimeService.isOutsideWorkingHours(time);

    // then
    assertFalse(result);
}
```

*JUnit 5*

JUnit is a framework for automated testing of Java applications. In this exercise, JUnit 5 is used to create and run unit tests.

Test classes are usually placed in the src/test/java directory, with their package structure following that of the code under test. For example, for src/main/java/.../service/WorkingTimeService.java, the corresponding test may be placed in src/test/java/.../service/WorkingTimeServiceTest.java.

Test class names usually end with Test.

*@Test Annotation*

A method representing an individual test case is annotated with @Test.

```java
@Test
public void shouldRecognizeWorkingDay() {
    // given
    LocalDateTime dateTime = LocalDateTime.of(2026, 8, 10, 10, 0);

    // when
    boolean result = workingTimeService.isWorkingDay(dateTime);

    // then
    assertTrue(result);
}
```

Each test should verify specific behavior and have a clearly defined expected result. In these exercises, tests are organized according to the Given – When – Then structure:

- given – prepare the initial data and required objects;
- when – execute the behavior being tested;
- then – verify the resulting outcome.

*Assertions*

Assertions are used to compare the actual result with the expected result. Commonly used assertions include:
- assertEquals(expected, actual);
- assertTrue(condition);
- assertFalse(condition);
- assertNull(value);
- assertNotNull(value).

For example:

```java
@Test
public void shouldReturnNextWorkingDay() {
    // given
    LocalDateTime friday = LocalDateTime.of(2026, 8, 14, 10, 0);

    // when
    LocalDateTime result = workingTimeService.nextWorkingDay(friday);

    // then
    assertEquals(LocalDateTime.of(2026, 8, 17, 10, 0), result);
}
```

When writing tests, it is not sufficient to verify only that the result is not null. The assertion should demonstrate the specific expected behavior.

*Verifying That an Exception Is Thrown*

When the expected behavior is to throw an exception, use assertThrows():

```java
@Test
public void shouldRejectInvalidValue() {
    // given
    int invalidValue = -1;

    // when
    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.execute(invalidValue)
    );

    // then
    assertNotNull(exception);
}
```

A more meaningful assertion may also verify the type or content of the error when these form part of the method's contract.

*@BeforeEach*

When the same initial setup is required before every test, @BeforeEach may be used:

```java
private WorkingTimeService workingTimeService;

@BeforeEach
void setUp() {
    workingTimeService = new WorkingTimeService();
}
```

A method with this annotation is executed before every test method. This allows the tests to work with independent initial states.

Such organization is not required for a small stateless service.

*Parameterized Tests*

When the same behavior needs to be verified with different input values, a parameterized test can be used instead of multiple nearly identical tests. Such a test uses the @ParameterizedTest annotation. Test data can be supplied in several ways.

One option is to use @ValueSource:

```java
@ParameterizedTest
@ValueSource(ints = {1, 2, 3, 4, 5})
public void shouldRecognizeWorkingDays(int dayOfWeek) {
    // given
    // ...

    // when
    // ...

    // then
    // ...
}
```

When more than one parameter is required, @CsvSource can be used:

```java
@ParameterizedTest
@CsvSource({
        "8, 59, true",
        "9, 0, false",
        "17, 59, false",
        "18, 0, true"
})
public void shouldDetermineOutsideWorkingHours(int hour, int minute, boolean expected) {

    // given
    LocalDateTime dateTime = LocalDateTime.of(2026, 8, 10, hour, minute);

    // when
    boolean result = workingTimeService.isOutsideWorkingHours(dateTime);

    // then
    assertEquals(expected, result);
}
```

A parameterized test is appropriate when the same rule is being verified for different values. Unrelated scenarios should not be combined merely to reduce the number of test methods.

*Test Names*

The name of a test method should indicate the behavior being verified, for example:

- shouldRecognizeWeekendAsNonWorkingDay()
- shouldSkipWeekendWhenAddingWorkingDays()
- shouldRejectExecutionBeforeScheduledTime()

The name does not need to describe the entire implementation. It should clearly indicate the expected behavior.

*Test Independence*

Each unit test should be executable independently of the others. A test must not depend on another test having run before it, on data created by another test, or on a particular test execution order.

Each test prepares its own required initial state in the Given section.

*What Should Be Tested*

Unit tests should verify publicly observable behavior rather than the way that behavior is implemented internally.

Private methods do not need to be tested directly. They are verified indirectly through the behavior of the public methods that use them.

*Types of Test Scenarios*

When selecting test cases, at least three types of scenarios should be considered:

- Happy path verifies normal expected execution;
- Negative path verifies a situation in which an operation should not be allowed or the result should be negative;
- Boundary/edge case verifies values immediately around the boundary of a business rule.

Boundary tests are particularly important for business rules involving time, dates, maximum and minimum values, object counts, permitted deadlines, percentages, and pricing constraints.

*Running Unit Tests*

JUnit tests can be run directly from the development environment or through Maven.

A successful build by itself does not prove that the business logic is correct. It shows only that all tests that were executed passed successfully.

Therefore, the quality of the test suite depends not only on whether the tests pass, but also on whether the appropriate test scenarios have been selected.

*Mockito*

A unit test should verify the behavior of a specific class independently of the other application components. When the class under test has dependencies on other objects, executing their real implementations may make the test dependent on a database, an external system, or other business logic.

Mock objects can be used to isolate the class under test.

Mockito is a library for creating and managing mock objects in Java tests.

*Real Object and Mock Object*

The object whose behavior is being verified should be a real object.

For example, if a service depends on a repository:

```java
public class LoyaltyService {

    private final ServiceRequestRepository serviceRequestRepository;

    // ...
}
```

when unit testing LoyaltyService, the objective is to verify its logic rather than the behavior of the repository layer. Therefore, LoyaltyService is the real object under test, while ServiceRequestRepository is a mock object.

The mock object replaces the real dependency and allows its result for a particular invocation to be specified in advance.

*Creating Mock Objects*

Mockito can be used with the following annotations:

```java
@Mock
private ServiceRequestRepository serviceRequestRepository;

@InjectMocks
private LoyaltyService loyaltyService;
```

Here, @Mock creates a mock implementation of the dependency, while @InjectMocks creates a real instance of the class under test and injects the required mock dependencies into it.

To use Mockito annotations with JUnit 5, the test class can be annotated as follows:

```java
@ExtendWith(MockitoExtension.class)
public class LoyaltyServiceTest {

    @Mock
    private ServiceRequestRepository serviceRequestRepository;

    @InjectMocks
    private LoyaltyService loyaltyService;
}
```

The Spring context is not started here. Mockito creates the objects required for the particular unit test.

*Defining Mock Behavior*

The behavior of mock dependencies can be specified using when(...).thenReturn(...). For example:

```java
when(serviceRequestRepository.countCompletedRequests(...)).thenReturn(3L);
```

This means that when the corresponding repository method is invoked, the repository mock should return the predefined value.

The complete test again follows the established structure:
```java
@Test
public void shouldRecognizeLoyalCustomer() {
    // given
    String email = "customer@example.com";

    when(serviceRequestRepository.countCompletedRequests(...)).thenReturn(3L);

    // when
    boolean result = loyaltyService.isLoyal(...);

    // then
    assertTrue(result);
}
```

Configuring mock objects is part of the Given section because it represents preparation of the environment required for the test scenario.

*Verifying Interaction with a Mock Object*

Mockito also allows verification that a particular method of a mock object has been invoked:

```java
verify(serviceRequestRepository).countCompletedRequests(...);
```

When necessary, the number of invocations can also be verified:

```java
verify(serviceRequestRepository, times(1)).countCompletedRequests(...);
```

The verify() method should be used when the interaction itself is important to the behavior being verified.

Dependencies of the object under test are mocked, not the object whose behavior is being tested.

Not every dependency needs to be mocked. Simple objects representing input data or part of the domain model can usually be real objects:

```java
ServiceRequest request = new ServiceRequest(...);
Offer offer = new Offer(...);
```

Mocks are most appropriate for dependencies that need to be isolated from the unit test, for example:

- repositories;
- external services;
- clients for other systems;
- components whose actual execution is not the subject of the current test.

Excessive use of mock objects makes tests difficult to read and may couple them to the internal implementation of the class.

*@SpringBootTest and Unit Tests*

@SpringBootTest starts the Spring application context and is intended for tests that require the Spring infrastructure.

@SpringBootTest is used for integration tests when the behavior being verified requires the Spring application context to be started and real Spring components to work together.

For unit tests, the Spring context is not started. The class under test is instantiated directly, and its dependencies are replaced with mock objects when necessary.

| Behavior Being Tested                                | Approach          |
|------------------------------------------------------|-------------------|
| WorkingTimeService                                   | JUnit 5           |
| LoyaltyService with controlled repository behavior   | JUnit 5 + Mockito |
| CancellationService with mock repositories           | JUnit 5 + Mockito |
| Service + real repository + Spring context           | integration test  |
| Interaction of several Spring components             | integration test  |

The full Spring application context can be loaded for integration testing using @SpringBootTest. Integration tests will be covered separately.

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

