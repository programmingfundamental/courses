---
title: Tasks
taskPage: true
sidebar:
  label: Tasks
  order: 100
---

### New Business Context

The following requirements are added to the existing application:

•	an employee can start processing only a request with status SUBMITTED; 

•	when processing starts, the status changes to PROCESSING; 

•	only a request with status PROCESSING can receive an offer or be rejected; 

•	a rejection reason must be provided when a request is rejected; 

•	when a request is rejected, its status changes to REJECTED; 

•	a rejected request cannot be restored; 

•	an operation that is not allowed for the current status results in HTTP 409 Conflict; 

•	when an offer is created, the request transitions from PROCESSING to OFFERED; 

•	if a request is not processed within the specified deadline, it may transition to EXPIRED. 

Some of the requirements describe future lifecycle stages. **It is not necessary to implement all identified requirements in a single iteration**.

### Lifecycle Analysis

Before submitting the prompt, determine which context from the current project is necessary to analyze:

•	the current state of ServiceRequest; 

•	statuses and lifecycle rules; 

•	service operations; 

•	the REST API; 

•	existing error handling. 

Provide only the relevant files. Depending on the current implementation, these may include ServiceRequest, RequestStatus, the corresponding service and controller classes, and the classes involved in error handling.
Do not automatically add the entire project.

From this step until the end of the exercise, use the same Agent session so that subsequent prompts can use the accumulated conversational context.

Submit:

```
### Task
Analyse the following new business requirements in the context of the existing application.
Identify:
- the lifecycle transitions implied by the requirements;
- the domain, service and REST operations required;
- the appropriate distribution of responsibilities;
- the required error handling.
Distinguish between functionality needed for the next implementation step and functionality that can be deferred.

### Context
- only a SUBMITTED request can start processing;
- starting processing changes the status to PROCESSING;
- only a PROCESSING request can receive an offer or be rejected;
- rejection requires a reason;
- rejection changes the status to REJECTED;
- a REJECTED request cannot be restored;
- an operation not allowed for the current status returns HTTP 409 Conflict;
- creating an offer changes PROCESSING to OFFERED;
- a request that is not processed within the applicable deadline may become EXPIRED.

### Constraints
Do not generate or modify code.
Do not redesign unrelated parts of the application.
Do not assume that all identified lifecycle functionality must be implemented in the same iteration.
If the requirements or provided project context are insufficient for a concrete decision, identify the missing information instead of making an unsupported assumption.
```

If the Agent proposes creating or modifying files despite the Do not generate or modify code constraint, do not approve the actions and take this behavior into account when evaluating the result.

### Evaluation of the Initial Proposal

Evaluate the generated analysis according to the criteria introduced in the previous exercises.

Additionally, check:

•	whether the lifecycle transitions have been identified correctly; 

•	whether an invalid lifecycle operation has been distinguished from invalid input data and a missing resource; 

•	whether responsibilities have been distributed appropriately between the domain object, service, and controller; 

•	whether the AI proposes implementing Offer or EXPIRED in the immediate iteration; 

•	whether it proposes other classes, endpoints, or operations that are not necessary; 

•	whether it identifies missing information instead of making unsupported assumptions; 

•	what it proposes implementing now and what it proposes deferring. 

The AI's proposal does not automatically determine the implementation scope. The developer decides which of the identified changes are necessary for the current iteration.

### Iterative Scope Refinement

For the current iteration, the decision is made not to implement Offer, deadline calculation, or the transition to EXPIRED.

Only the following will be implemented:

•	SUBMITTED → PROCESSING; 

•	PROCESSING → REJECTED; 

•	a mandatory rejection reason; 

•	HTTP 409 Conflict for an invalid lifecycle operation. 

This represents **scope refinement** – the initially analyzed problem is restricted to the functionality required for the current iteration.

In the **same Agent session**, submit the following follow-up prompt:

```
## Task
Refine the proposed solution for the current implementation step based on the previous analysis.
Do not generate or modify code yet.

## Scope
For this iteration, implement only the following lifecycle operations:
- start processing only from SUBMITTED;
- reject a request only from PROCESSING;
- require a rejection reason;
- return 409 Conflict for invalid lifecycle operations.

Do not introduce:
- Offer;
- deadline calculation;
- expiration handling.

## Design Requirements
- Lifecycle transition rules should be enforced by ServiceRequest domain behavior.
- The service should load the request, invoke the corresponding domain operation, and persist the change transactionally.

## Expected Output
Revise the proposed solution for this reduced scope and describe:
- domain operations;
- service operations;
- REST endpoints;
- exceptions and their mapping to HTTP responses;
- transaction boundaries.

Keep the proposal limited to the current implementation step.
```

This prompt does not repeat the entire initial context. It uses the accumulated conversational context and specifies only the changed scope.

### Evaluation of the Refined Proposal

Compare the new proposal with the initial analysis and check:

•	whether Offer, deadline calculation, and expiration handling have been removed from the current scope; 

•	whether requirements that remain valid have been preserved; 

•	whether the proposed changes are limited to the necessary lifecycle operations; 

•	whether responsibilities have been distributed appropriately; 

•	whether HTTP 400, 404, and 409 have been distinguished; 

•	whether the AI continues to use decisions or assumptions that are no longer applicable; 

•	whether unsolicited functionality has been added. 

The sequence

**initial proposal → evaluation → follow-up prompt → refined proposal**

represents iterative refinement of the solution.

### Distinguishing Between Errors

Before implementation, determine the expected behavior in the following situations:

•	empty rejection reason → HTTP 400 Bad Request; 

•	request with a nonexistent identifier → HTTP 404 Not Found; 

•	starting processing for a request that is not SUBMITTED → HTTP 409 Conflict; 

•	rejecting a request that is not PROCESSING → HTTP 409 Conflict. 

Explain the difference between:

•	invalid input data; 

•	a missing resource; 

•	a valid request for an operation that is not allowed given the current state of the resource. 

### Updating the Context

After approving the refined solution, perform an impact analysis.

Determine:

•	the files that need to be modified; 

•	the additional project context required for implementation; 

•	the parts of the application that should remain unchanged. 

Add only the additional files necessary for implementation.

The accumulated conversational context is preserved, but this does not eliminate the need to provide relevant information from the actual current state of the project.

### Implementing the Current Iteration

In the same Agent session, submit:

```
### Task
Implement the approved changes for the current lifecycle iteration.
Add support for:
- starting processing of a SUBMITTED request;
- rejecting a PROCESSING request;
- requiring a rejection reason;
- returning HTTP 409 Conflict for invalid lifecycle operations.
Use the responsibilities and REST operations approved in the previous analysis.

### Constraints
Keep lifecycle transition rules in ServiceRequest domain behavior.
The service should load the request, invoke the corresponding domain operation, and persist the change transactionally.
Preserve the existing behavior for HTTP 400 and HTTP 404.
Do not introduce Offer, deadline calculation, or expiration handling.
Do not add unrelated functionality.
Do not refactor unrelated code.
Modify only the files required for the approved changes.
```

The Agent may propose the necessary actions and changes to the project. Review them before approval.

Check:

•	whether the files actually affected correspond to the performed impact analysis; 

•	whether the changes are limited to the approved scope; 

•	whether exactly the agreed lifecycle operations have been implemented; 

•	whether valid decisions from previous iterations have been preserved; 

•	whether unsolicited functionality has been added; 

•	whether the Agent's summary corresponds to the changes actually proposed. 

If a problem occurs, do not regenerate the solution from the beginning. Formulate a short follow-up prompt describing the specific discrepancy and the necessary correction.

### Evaluation of the Domain Model After Implementation

After implementing the changes, review the current state of ServiceRequest and evaluate whether the lifecycle rules are protected against being bypassed.

Check:

•	how status can be modified; 

•	whether rejectionReason can be assigned outside the rejection operation; 

•	what determines the initial status and submittedAt; 

•	whether the lifecycle rules can be bypassed through setters, constructors, a builder, or other publicly accessible operations; 

•	whether state changes go through the defined domain operations. 

If a specific problem is identified, formulate a follow-up prompt in the same Agent session that describes the problem and requests the minimum necessary correction.

If no problem is identified, do not perform additional refactoring merely for the purpose of creating another iteration.

### Behavior Verification

After completing the changes, compile and run the application.

Using the REST API, verify:

•	starting processing of a SUBMITTED request → PROCESSING; 

•	attempting to start processing again → HTTP 409 Conflict; 

•	rejecting a PROCESSING request with a valid reason → REJECTED; 

•	rejecting with an empty reason → HTTP 400 Bad Request; 

•	rejecting a request that is not PROCESSING → HTTP 409 Conflict; 

•	performing an operation on a nonexistent request → HTTP 404 Not Found. 

This constitutes verification of the actual behavior against the approved scope of the current iteration.

If a discrepancy is found, apply the process used in the previous exercise:

**observation → diagnostic information → cause hypothesis → hypothesis verification → minimal correction → re-verification**

The next follow-up prompt to the AI should be formulated after analyzing the available diagnostic information. A cause proposed by the AI should not automatically be accepted as the actual cause.
