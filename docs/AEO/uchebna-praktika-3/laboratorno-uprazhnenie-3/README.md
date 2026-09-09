---
layout: default
title: Laboratory exercise 3
parent: Training Practice 3
has_children: true
nav_order: 3
---

# Laboratory exercise 3

## Input Validation and Error Handling

### Objective

The objective of this exercise is to analyze and improve the REST API contract by appropriately separating:

- validation of input DTOs;
- conditional business rules;
- persistence constraints;
- error handling and mapping errors to HTTP responses.

The AI assistant will first be used to analyze the existing implementation and compare possible solutions. The code should be modified only after a specific approach has been selected.

### Initial State

Use the final working version from Laboratory Exercise 2. The application contains:

- ServiceRequest;
- CustomerInfo;
- request/response DTOs;
- repository;
- service;
- REST controllers;
- global error handling;
- basic Bean Validation.

### Review of the Current REST API

Before adding new functionality, analyze the existing solution. Provide the following prompt:

```
Review the current implementation of the ServiceRequest REST API.
Analyze:
•	the responsibilities of the controllers, service, and repository; 
•	the request and response DTO; 
•	the entity-to-DTO and DTO-to-entity mapping; 
•	the HTTP methods and response status codes; 
•	whether persistence entities are exposed through the API; 
•	whether any layer is bypassed; 
•	whether the current mapping approach is appropriate for the size of the project. 
Identify justified improvements and distinguish them from premature abstractions. Do not modify the code and do not add new functionality.

```

After the analysis, check:

- whether there is a clear separation between the layers;
- whether DTOs are used or entity objects are exposed directly through the API;
- where the conversion between DTOs and entities is performed;
- whether a separate mapper is justified at the current size of the project;
- whether the HTTP methods and status codes are correct;
- which of the proposed changes are actually necessary.

### Validation and Error-Handling Requirements

Consider the following rules.

For input data:

- the customer name is required and has a maximum length;
- the email address is required, must be valid, and has a maximum length;
- the description is required and has a minimum and maximum length;
- an address is required for ON_SITE;
- an address is not required for REMOTE;
- a reason for urgency is required for URGENT;
- INSTALLATION and REPAIR requests may be performed only ON_SITE;
- DIAGNOSTICS and CONSULTATION may be performed either ON_SITE or REMOTE;
- a new request always starts with status SUBMITTED;
- submittedAt is assigned by the system.

For errors:

- missing resource – 404;
- invalid input data – 400;
- malformed JSON, invalid enum value, or invalid type of a path/query parameter – 400;
- unexpected error – 500, without exposing the internal error message.

Prompt:

```
Analyze the following validation and error-handling requirements for the current ServiceRequest API.
Classify each rule as:
•	Bean Validation; 
•	domain/business validation; 
•	persistence constraint; 
•	API exception-mapping concern. 
Recommend where each rule should be enforced and explain whether the current GlobalExceptionHandler should be changed.
Do not modify the code yet.

```

*Review of the Proposed Classification*

Review the AI assistant's classification and evaluate whether each rule has been placed at the appropriate level. Pay particular attention to the distinction between:

- Bean Validation of an individual field, for example:

```java
@NotBlank
@Email
@Size(max = ...)
private String customerEmail;
```
 
- a rule that depends on a combination of several fields, for example:

```
executionMode == ON_SITE → address is required
priority == URGENT → urgencyReason is required
serviceType == INSTALLATION → executionMode must be ON_SITE
```

Should every rule related to a business requirement be treated as input validation?

*Experiment with an Incorrect Requirement*

Temporarily modify one of the error-handling requirements so that an obvious client error is described as returning status code 500. Ask the AI assistant to analyze the requirement without modifying the code.

Observe whether the assistant:

- challenges the requirement;
- asks for clarification;
- or accepts it and proposes an implementation.

*Selecting an Approach for the Conditional Rules*

Prompt:

```
Now, compare the two approaches for the conditional request rules:
•	service-level business validation 
•	a class-level custom Bean Validation constraint on ServiceRequestCreateRequest. 
The rules are listed in a previous prompt. Recommend the more appropriate approach for the current project and justify the choice in terms of responsibility, reuse, error reporting, complexity, and maintainability.
Do not modify the code yet.

```

Then evaluate the recommendation in terms of:

- responsibility;
- reusability;
- how validation errors are returned;
- complexity;
- possible future entry points into the system.

The selected solution should be appropriate for the current context and should not be treated as a universal rule that all business checks must be implemented through DTO validation.

*Implementing Only the Approved Changes*

Provide the following prompt:

```
Apply only the approved changes.
• Add the missing field-level Bean Validation constraints to ServiceRequestCreateRequest — customerName (required and maximum length), customerEmail (required, valid email, and maximum length), description (required, minimum length, and maximum length).
• Implement a class-level custom Bean Validation constraint on ServiceRequestCreateRequest that enforces ON_SITE requires a non-blank address, URGENT requires a non-blank urgencyReason, INSTALLATION and REPAIR are allowed only with ON_SITE, DIAGNOSTICS and CONSULTATION may be ON_SITE/REMOTE.
• Preserve the existing rule that status is set to SUBMITTED and submittedAt is assigned by the system.
• Update GlobalExceptionHandler so that: ResourceNotFoundException returns 404; Bean Validation errors return 400; malformed JSON and invalid enum values return 400; invalid path or query parameter types return 400; unexpected errors return 500 with a fixed safe message and do not expose the original error message.
• Keep the existing endpoints, DTO shape, manual mapping approach, persistence model, and package organization intact.
Do not add any other changes.
After the changes, build and run the application and report the actual result.

```

*Review of the Generated Changes*

Check:

- the added Bean Validation annotations;
- the implementation of the class-level constraint;
- the location of the constraint annotation and validator class;
- whether the controller uses the @Valid annotation;
- whether the conditional validation has been unnecessarily duplicated in multiple places;
- the changes in GlobalExceptionHandler;
- whether the 500 handler now returns a safe message;
- whether any changes were made outside the explicitly approved scope.

*Verification of API Behavior*

After starting the application, test the following scenarios:

- valid ON_SITE request;
- valid REMOTE request;
- ON_SITE request without an address;
- URGENT request without a reason for urgency;
- INSTALLATION with REMOTE;
- invalid email address;
- description that is too short or too long;
- invalid enum value;
- malformed JSON;
- request for a non-existent ID;
- invalid type of a path/query parameter.

Compare the expected HTTP status codes and error messages with the actual responses.

### Lab Result

By the end of Laboratory Exercise 3, the following should be available:

- a clear distinction between Bean Validation, cross-field validation, persistence constraints, and API exception mapping;
- a validated ServiceRequestCreateRequest;
- implemented conditional validation rules;
- dedicated mapping of client errors to 400 responses;
- 404 responses for missing resources;
- a safe 500 response for unexpected errors;
- preserved endpoints, DTO contract, and persistence model;
- verified functionality through REST requests.

