---
layout: default
title: Laboratory exercise 4
parent: Training Practice 3
has_children: true
nav_order: 4
---

# Laboratory exercise 4

## Request Processing and Lifecycle

### Objective

The objective of this exercise is to extend the application with the first operations in the request lifecycle and to distinguish between:

- invalid input data;
- a missing resource;
- a business operation that is not allowed in the current state.

The exercise also examines where lifecycle rules should be placed and how the entity should be protected from direct modification of its state.

### Initial State

Use the final working version of the project from Laboratory Exercise 3. The project already contains:

- ServiceRequest;
- DTOs and Bean Validation;
- REST endpoints for creating and retrieving requests;
- service and repository layers;
- a consistent error response format;
- handling for 400, 404, and 500 responses.

### New Business Context

Add the following rules to the existing application:

- an employee may start processing only a request with status SUBMITTED;
- when processing starts, the status becomes PROCESSING;
- a request may be rejected only while it is being processed;
- a rejection reason must be provided;
- when a request is rejected, its status becomes REJECTED;
- an operation that is not allowed for the current status must return 409.

The company must provide an offer within three working days after the request is submitted. The offer itself will be implemented at a later stage.

### Lifecycle Analysis

Provide the following prompt:

```
Analyze the following business requirements together with the current codebase.
Requirements:
•	Employee could start request processing if its status is SUBMITTED 
•	When processing starts, the status changes to PROCESSING 
•	Company have to prepare offer up to 3 business days after request submission 
•	Request could be rejected during processing with rejection reason cited 
•	Offer is created for processed and approved requests 
•	After offer is created, the request status changes to OFFERED 
•	Forbidden operation depending on request status returns 409 Conflict. 
Propose:
•	The required changes to the ServiceRequest lifecycle 
•	The necessary domain operations 
•	Whether Offer should be a separate entity, value object, or part of ServiceRequest 
•	The relationship between ServiceRequest and Offer 
•	The responsibilities of the entity, service, and controllers 
•	The REST operations required for the office workflow 
•	How invalid lifecycle operations should be represented. 
Distinguish current requirements from functionality that should remain outside the scope at this moment.
Do not generate or modify code.

```

Then evaluate whether the proposed solution:

- introduces future functionality too early;
- places lifecycle checks at the appropriate level;
- mixes input validation with business conflicts;
- proposes unnecessary CRUD operations for Offer.

*Restricting the Scope*

After the analysis, decide not to implement Offer in the current exercise. Provide the following prompt:

```
Please reevaluate the current scope without introducing Offer yet.
The purpose of current modification is to implement the first request-processing operations and lifecycle rules:
•	start processing only from SUBMITTED 
•	reject only from PROCESSING 
•	store a rejection reason 
•	return 409 Conflict for invalid lifecycle operations 
•	keep lifecycle checks inside ServiceRequest domain methods 
•	let the service load, invoke domain behavior, and persist transactionally. 
The offer itself will be introduced in a later stage.
Propose the required domain methods, service methods, REST endpoints, exceptions, and transaction boundaries.
Do not generate or modify the code yet.

```

*Distinguishing Between 400, 404, and 409*

Consider the following cases:

- an empty rejection reason;
- a non-existent requestId;
- an attempt to call startProcessing() on a request whose status is not SUBMITTED;
- an attempt to call reject() on a request whose status is not PROCESSING.

Determine the appropriate HTTP status for each case.

*Implementing the Lifecycle Operations*

Provide the following prompt:

```
Apply only the approved request-processing lifecycle changes:
• Extend ServiceRequest with a nullable rejectionReason field, startProcessing() only for SUBMITTED, and reject(String reason) only from PROCESSING
• Keep lifecycle transition checks inside ServiceRequest; do not duplicate them in controllers or service methods
• Add service operations startProcessing(Long id) and reject(Long id, String reason)
• Add office endpoints POST /api/office/requests/{id}/start-processing and POST /api/office/requests/{id}/reject
• Introduce a request DTO for rejection with a required non-blank reason
• Introduce RequestLifecycleConflictException and map it to 409 Conflict
• Keep missing request results in 404, invalid rejection payload in 400, and invalid lifecycle transition in 409
• Make modifying service operations transactional. Rely on JPA dirty checking where appropriate rather than adding unnecessary explicit save calls.
Preserve all existing endpoints, DTOs, validation, package organization, and error response format.
Do not add any other changes.
After modifications, build and run the application and report the actual results.

```

*Review of ServiceRequest Encapsulation*

After adding startProcessing() and reject(), check whether the entity still allows its lifecycle rules to be bypassed. Pay attention to:

- class-level @Setter;
- @Builder;
- @AllArgsConstructor;
- direct modification of status;
- direct modification of submittedAt;
- direct modification of rejectionReason.

Provide the following prompt:

```
Review the encapsulation of ServiceRequest.
The entity currently uses class-level @Setter, @Builder, and @AllArgsConstructors, while lifecycle rules are implemented through startProcessing() and reject(...). The create method in the service also assigns status and submittedAt through the builder.
Propose refactoring that:
•	prevents external modification of id 
•	prevents direct modification of status, submittedAt, and rejectionReason 
•	guarantees that every newly created request starts as SUBMITTED and receives submittedAt from the system 
•	preserves JPA compatibility 
•	keeps DTO mapping and persistence orchestration in the service 
•	avoids unnecessary setters and unrestricted constructors. 
Do not modify the code yet.

```

*Applying the Encapsulation Refactoring*

After approval, provide:

```
Apply the approved encapsulation refactoring to ServiceRequest:
• Remove class-level @Setter, @Builder, and @AllArgsConstructor
• Keep @Getter
• Use a protected JPA no-arg constructor
• Add a controlled static factory method submit(...)
• The factory must always initialize status to SUBMITTED, submittedAt from value supplied by the service, and rejectionReason to null
• Do not expose public setters for id, status, submittedAt, or rejectionReason
• Keep startProcessing() and reject(String reason) as only lifecycle mutation methods
• Keep DTO mapping and persistence orchestration in the service
• Inject Clock into the service and use LocalDateTime.now(clock) when calling the factory
• Preserve everything else and do not add any other changes.
After refactoring, build and run the application and report the actual results.

```

The modified files and imports must be reviewed.

*Final Verification*

Use REST requests to verify the following scenarios:

- SUBMITTED → PROCESSING;
- a second attempt to start processing – 409;
- PROCESSING → REJECTED with a valid reason;
- rejection with an empty reason – 400;
- rejection of a SUBMITTED request – 409;
- an operation on a non-existent ID – 404.

Verify that:

- the status cannot be set by the client;
- submittedAt cannot be set by the client;
- the rejection reason cannot be modified directly;
- lifecycle state changes are performed only through domain methods.

### Lab Result

By the end of Laboratory Exercise 4, the following should be available:

- a ServiceRequest with a controlled lifecycle;
- startProcessing() and reject(...) operations;
- a rejection reason;
- service methods and office endpoints;
- RequestLifecycleConflictException;
- 409 responses for invalid operations;
- transactional modification operations;
- an encapsulated entity without unauthorized mutators or a builder;
- controlled creation through a factory method;
- a preserved and working REST API.

