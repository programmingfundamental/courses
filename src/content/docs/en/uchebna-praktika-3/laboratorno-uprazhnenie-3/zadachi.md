---
title: Tasks
taskPage: true
sidebar:
  label: Tasks
  order: 100
---

### Review of the Current Solution

Before working on the new requirements, review the current implementation and determine:

•	where validation of input DTOs is performed; 

•	which Bean Validation constraints already exist; 

•	whether the controller uses @Valid; 

•	whether there are business checks in the service layer; 

•	how exceptions are handled; 

•	which HTTP status codes and response bodies are returned when an error occurs. 

Bean Validation provides a declarative way to specify constraints on input data through annotations such as @NotBlank, @Email, and @Size. In Spring Boot, validation of an input DTO can usually be triggered using @Valid.
Identify and provide only the parts of the project necessary for analyzing input data, request processing, business checks, and existing error handling.

### Validation and Error-Handling Requirements

Consider the following requirements:

•	customerEmail is required and must be a valid email address; 

•	description is required and has a minimum and maximum length; 

•	ON_SITE requires an address; 

•	URGENT requires a reason for urgency; 

•	a new request starts with status SUBMITTED; 

•	submittedAt is assigned by the system; 

•	a missing resource returns HTTP 404; 

•	invalid input data returns HTTP 400; 

•	malformed JSON and an invalid enum value return HTTP 400; 

•	an unexpected error returns HTTP 500 without exposing the internal exception message. 

Submit:

```
### Task
Analyse the following validation and error-handling requirements for the current ServiceRequest API.
For each requirement provide:
- classification;
- recommended enforcement location;
- reasoning.
Possible classifications include:
- Bean Validation;
- domain/business validation;
- API exception-mapping concern;
- system-managed value.
If a requirement could reasonably belong to more than one category, identify the ambiguity.
Do not modify the project.

### Context
Requirements:
- customerEmail is required and must be a valid email address;
- description is required and has minimum and maximum length;
- ON_SITE execution requires a non-blank address;
- URGENT priority requires a non-blank urgencyReason;
- a new request starts with SUBMITTED status;
- submittedAt is assigned by the system;
- a missing resource returns HTTP 404;
- invalid input data returns HTTP 400;
- malformed JSON and invalid enum values return HTTP 400;
- an unexpected error returns HTTP 500 without exposing
  the internal exception message.

### Constraints
Do not introduce requirements that are not present in the context.
Do not generate or modify code.
```

### Reviewing the Proposed Analysis

Evaluate the generated result according to the criteria introduced in the previous exercises.

Additionally, check:

•	whether all specified requirements have been addressed; 

•	whether any rules not present in the context have been added; 

•	whether constraints on an individual field have been distinguished from rules that depend on a combination of data; 

•	whether the proposed enforcement location for each rule is appropriate; 

•	whether there are any assumptions or unsupported claims. 

Compare, for example:

- field Bean Validation:

```java
@NotBlank
@Email
@Size(max = ...)
private String customerEmail;
```
 
- and the conditional rule:

```
executionMode == ON_SITE → address is required
priority == URGENT → urgencyReason is required
serviceType == INSTALLATION → executionMode must be ON_SITE
```

Discuss why the fact that a rule originates from a business requirement does not automatically determine the technical mechanism through which it should be implemented.

### Experiment with a Conflicting Requirement

Submit the following additional requirement:

```
### Additional requirement
Invalid customer input should return HTTP 500 Internal Server Error.

### Task
Analyse this requirement in the context of the previously provided validation and error-handling requirements.
Identify whether it is consistent with the existing requirements and explain the consequences of implementing it.

### Constraint
Do not modify the project.
```

Check whether the AI assistant:

•	recognizes the conflict with the specified requirement that invalid input data should return HTTP 400; 

•	explains the consequences of the proposed change; 

•	accepts the latest instruction without critical evaluation; 

•	proposes a code change even though only analysis was requested. 

The conflicting requirement is not included in the subsequent implementation.

### Trade-Off Analysis

Conditional rules can be implemented in different ways. Choosing between alternative solutions should consider their trade-offs – the advantages, disadvantages, and consequences of each approach in the context of the specific project.

For the rules:

•	ON_SITE requires a non-blank address; 

•	URGENT requires a non-blank urgencyReason; 

compare:

•	validation in the service layer; 

•	a class-level custom Bean Validation constraint on the request DTO. 

Submit:

```
### Task
Compare two approaches for implementing the conditional request rules in the current ServiceRequest API:
1. service-level business validation;
2. a class-level custom Bean Validation constraint on the request DTO.
The rules are:
- ON_SITE requires a non-blank address;
- URGENT requires a non-blank urgencyReason.
Compare the approaches in terms of:
- responsibility;
- reuse;
- error reporting;
- implementation complexity;
- maintainability.

### Constraints
Do not select an approach.
Do not modify the project.
```

Critically evaluate the generated comparison. Check whether the stated advantages and disadvantages follow from the actual characteristics of the approaches and the current project rather than from unsupported AI assumptions.

Based on the analysis, select an approach for the current project and justify the choice.

The selected solution should not be treated as a universal rule for implementing conditional validation.

### Updating the Context

After selecting an approach, perform an impact analysis and determine which parts of the project will be affected by the implementation.

Assess:

•	which of the previously provided files remain relevant; 

•	which additional files are necessary for the selected approach; 

•	which parts of the project should remain unchanged. 

Add only the necessary additional context.

### Implementing the Approved Solution

Formulate an implementation prompt that includes the selected approach for the conditional rules.

The following structure can be used:

```
### Task
Apply the approved validation and error-handling changes to the existing ServiceRequest API.

### Required changes
Add the required field-level Bean Validation constraints:
- customerEmail — required and valid email;
- description — required and with minimum and maximum length.
Implement the approved approach for these conditional rules:
- ON_SITE requires a non-blank address;
- URGENT requires a non-blank urgencyReason.
Preserve the existing behavior in which:
- status is initialized to SUBMITTED;
- submittedAt is assigned by the system.
Ensure that:
- a missing resource returns HTTP 404;
- validation errors return HTTP 400;
- malformed JSON and invalid enum values return HTTP 400;
- unexpected errors return HTTP 500 without exposing the internal exception message.

### Constraints
Keep the existing endpoints unchanged.
Preserve the external request and response structure.
Do not modify the persistence model unless required by the approved validation approach.
Do not add unrelated functionality.
Do not refactor unrelated code.
Modify only the files required for the approved changes.
```

Before approving the actions and changes proposed by the Agent, check:

•	whether all approved changes have been implemented; 

•	whether the selected approach has been used for the conditional rules; 

•	whether the specified constraints have been observed; 

•	whether the existing API has been preserved; 

•	whether there are any unsolicited changes or functionality; 

•	whether the Agent's description corresponds to the changes actually proposed. 

If there is a discrepancy, determine whether the cause is a violated constraint, an unclear instruction, missing relevant context, or an incorrect AI decision.

Formulate a follow-up prompt only for the necessary correction instead of regenerating the entire solution.

### Verification of API Behavior

After approving the changes, compile and run the application.

Using Postman, test at least the following scenarios:

•	a valid request; 

•	an invalid email address; 

•	an ON_SITE request without an address; 

•	an URGENT request without a reason for urgency; 

•	an invalid enum value or malformed JSON; 

•	a request for a nonexistent ID. 

For each scenario, compare the expected and actual HTTP status and review the response body.

Verify that error responses contain sufficient information about the problem without exposing unnecessary internal technical information.

### Analysis of Discrepancies

When the actual result differs from the expected result, perform a root-cause analysis before asking the AI assistant for a correction.

Use the actual result, response body, exception, and available diagnostic information to formulate a hypothesis about the cause. A cause proposed by the AI should not be accepted automatically but should be checked against the actual code, configuration, and observed behavior.

After identifying the actual cause, make only the necessary correction and execute the corresponding scenario again. The objective is to eliminate the cause of the problem rather than only the observed symptom.
