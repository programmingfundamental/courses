---
title: Tasks
taskPage: true
sidebar:
  label: Tasks
  order: 100
---

### Selecting the First Object to Test

Review the current implementation of the application and identify where the logic related to working days and working hours is implemented.

For the first experiment, independently select a class that implements such logic and can be tested in isolation without using a repository, other services, or external components. The name and specific structure of the class may vary depending on the project implementation.

If the current project does not contain a suitable standalone class, use the provided WorkingTimeService. It is used as an educational object for the first part of the exercise and does not need to be integrated into the existing business logic of the application.

*Reference WorkingTimeService*

```java
package com.example.servicerequestmanagementsystem.service;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Service
public class WorkingTimeService {

    public LocalDateTime addWorkingDays(LocalDateTime dateTime, int workingDays) {
        LocalDateTime result = dateTime;
        int added = 0;

        while (added < workingDays) {
            result = result.plusDays(1);

            if (isWorkingDay(result)) {
                added++;
            }
        }

        return result;
    }

    public LocalDateTime nextWorkingDay(LocalDateTime dateTime) {
        LocalDateTime result = dateTime;

        do {
            result = result.plusDays(1);
        } while (!isWorkingDay(result));

        return result;
    }

    public boolean isWorkingDay(LocalDateTime dateTime) {
        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();

        return dayOfWeek != DayOfWeek.SATURDAY
                && dayOfWeek != DayOfWeek.SUNDAY;
    }

    public boolean isOutsideWorkingHours(LocalDateTime dateTime) {
        int hour = dateTime.getHour();

        return hour < 9 || hour >= 18;
    }
}
```

### Influence of Selected Context on Test Scenarios

For the selected class, use the AI assistant to identify appropriate unit test scenarios. At this stage, do not generate test code.

For both experiments, formulate the prompt independently using the introduced **Task / Context / Constraints / Expected output** structure, adapted to the specific task.

### Experiment 1 – Current Implementation Only

Initially, provide only the implementation of the selected class as context.

Restricting the context to the implementation alone is a condition of the experiment, not a recommended approach to generating tests.

Formulate a prompt requesting appropriate happy path, negative path, and boundary/edge case scenarios, with the expected result specified for each scenario.

Save the resulting proposal for subsequent comparison.

### Experiment 2 – Implementation and Business Context

In a new session, assign the same task, but in addition to the class implementation, provide the relevant business context necessary to determine the expected behavior.

Select the business context independently. Do not provide all business requirements of the application, but only the rules related to the behavior of the selected class.

Keep the remaining conditions of the experiment as close as possible to the first case so that the main difference is the presence of business context.

### Comparison and Evaluation of the Proposed Test Scenarios

Compare the proposals from the two experiments and check:

•	which scenarios are proposed in both cases; 

•	which scenarios appear after providing the business context; 

•	which expected results can be justified by the business requirements; 

•	whether the AI has treated behavior from the current implementation as a business requirement; 

•	whether scenarios have been proposed for behavior that is not defined by the provided context; 

•	whether significant boundary cases are covered. 

When the implementation defines specific behavior, but the business requirements do not specify what that behavior should be, the current behavior of the code should not automatically be treated as expected business behavior.

Distinguish between cases in which the AI assumes about missing behavior, presents the assumption as an established requirement, or makes an unsupported claim that cannot be justified by the provided context.

Based on the comparison, determine the set of test scenarios that should be implemented as unit tests.

### Verification of Test Dependencies

Before generating the unit tests, review pom.xml and determine whether the project contains the required dependencies for JUnit 5 and Mockito.

Consider that the required libraries may already be included through an existing Spring Boot test dependency and should not be added again.

If a required dependency is missing, make only the necessary change and reload the Maven project.

AI proposals for changes to pom.xml should not be accepted automatically but should be checked against the actual project configuration.

### Generation and Verification of Unit Tests

Based on the approved set of test scenarios, use the AI assistant to generate unit tests for the selected class.
Independently formulate a prompt and determine the necessary project context. Attach the files required to create executable tests for the current implementation, together with the approved test scenarios and applicable technological constraints.

Before accepting the generated tests, check:

•	whether each test corresponds to an approved test scenario; 

•	whether the input data and expected results are correct; 

•	whether the assertions verify the specific expected behavior; 

•	whether the defined happy path, negative path, and boundary/edge case scenarios are covered; 

•	whether observable behavior rather than internal implementation details is being tested; 

•	whether the AI has introduced additional assumptions about undefined behavior; 

•	whether the testing tools already available in the project are being used; 

•	whether the Spring application context or other infrastructure is being started unnecessarily. 

After reviewing them, add the approved tests to the project and execute them.

### Analysis of a Failing Test

When a test fails, do not automatically assume that the cause is a defect in the application.

Apply the introduced root-cause analysis process:

**observation → diagnostic information → cause hypothesis → hypothesis verification → minimal correction → re-verification**

Determine whether the discrepancy is caused by:

•	an incorrect expected result or inappropriate test data; 

•	an incorrectly implemented test; 

•	a problem in the test configuration; 

•	a defect in the implementation under test; 

•	a discrepancy between the implementation and the business requirement. 

Correct the identified cause of the problem rather than modifying the test solely to make it pass.
If additional project context is required to identify the cause, add only the necessary files.


### Unit Testing a Service with Dependencies

Select a service from the current project implementation that contains business logic and has at least one dependency on another component.

Determine the behavior to be tested, the required business and project context, and which dependencies should be replaced with mock objects.

Independently organize the work with the AI assistant to identify appropriate happy path, negative path, and boundary/edge case scenarios and generate the necessary unit tests.

Verify the resulting tests against the business requirements and the actual implementation. Pay particular attention to the test data used, the behavior of mock dependencies, the expected results, and the assertions.

Execute the tests and analyze the results. If a discrepancy is identified, determine its cause and make only the necessary correction.

### Final Verification

After completing the work, verify that:

•	the selected test scenarios and expected results can be justified by the business requirements; 

•	the generated unit tests verify the expected behavior rather than only the current implementation; 

•	mock objects are used only for dependencies that need to be isolated; 

•	the tests execute without unnecessarily starting the Spring application context; 

•	identified discrepancies have been analyzed and the actual cause of the problem has been corrected;

•	the project context used is relevant to the specific task, and the AI's proposals have been verified against the requirements, code, and execution results. 

