---
layout: default
title: Laboratory exercise 6
parent: Training Practice 3
has_children: true
nav_order: 6
---

# Laboratory exercise 6

## Extending the Business Logic and Analyzing the Architectural Solution

### Objective

The objective of this exercise is to implement the remaining part of the request lifecycle by adding offer acceptance and rejection, scheduling, execution, cancellation, and final pricing.

The exercise also examines when growing business logic justifies separating new responsibilities and when introducing additional abstractions would still be premature.

### Initial State

Use the final working version from Laboratory Exercise 5. The project already contains ServiceRequest, Offer, PriceBreakdown, direct initial pricing logic, and a lifecycle implemented up to the OFFERED state.

### New Business Context

Offer Acceptance and Rejection
- only a valid offer may be accepted or rejected;
- after acceptance, the request transitions to ACCEPTED;
- after rejection, the request transitions to the corresponding final state;
- after validUntil expires, the request transitions to EXPIRED;
- an expired offer cannot be accepted, modified, or replaced;
- the offer has no status of its own – its state is determined by ServiceRequest.status and Offer.validUntil.

Scheduling
- only an accepted request may be scheduled;
- scheduling is performed by office staff;
- the service must be scheduled within 5 working days after acceptance;
- a REMOTE service may be scheduled for a time no earlier than 4 hours from the moment the scheduling operation is performed;
- if no execution date is scheduled within this period, the request transitions to EXPIRED;
- an ON_SITE request may be scheduled no earlier than the next working day;
- execution cannot be scheduled later than 30 calendar days after acceptance;
- a normal service is scheduled on working days between 9:00 and 18:00;
- rescheduling is not supported;
- successful scheduling changes the status to SCHEDULED.

Execution
- execution is represented by a separate ServiceExecution object;
- execution may start only from SCHEDULED;
- it may not start before the scheduled time;
- the actual start time is recorded;
- the status changes to IN_PROGRESS;
- completion is an explicit operation;
- the result is recorded;
- the actual end time is recorded;
- the status changes to COMPLETED;
- execution is not completed automatically when the expected duration has elapsed.

Cancellation
- cancellation is allowed in SUBMITTED, PROCESSING, ACCEPTED, and SCHEDULED;
- a cancellation reason is required;
- an ON_SITE request may be cancelled up to 24 hours before execution;
- a REMOTE request may be cancelled up to 4 hours before execution;
- cancellation is not allowed in IN_PROGRESS or COMPLETED;
- an OFFERED request is not cancelled – the offer is rejected instead;
- successful cancellation changes the status to CANCELLED.

Pricing
- base price;
- surcharge for on-site execution;
- surcharge for urgent requests;
- surcharge for execution outside working hours;
- 10% discount for a loyal customer;
- a loyal customer is one who has at least 3 successfully completed requests before the current request was submitted;
- customers are identified by normalized email address;
- surcharges are applied before the discount;
- the final price must be positive;
- PriceBreakdown is a snapshot;
- once an offer has been accepted, its price cannot be changed.

*First Prompt*

Provide:

```
New business requirements for the existing codebase: ...
Please analyze the new requirements and propose how the current model should be modified.
Assess what should be added to ServiceRequest; whether new entities or value objects are needed; what DTOs and REST operations are required; which checks belong in entity, service, or Bean Validation; and whether any design pattern is actually justified.
Do not generate or modify code.

```

*Evaluation of the Initial Analysis*

Check whether the AI assistant:

- invents a business process that was not specified;
- duplicates lifecycle state;
- introduces a scheduler/background job without justification;
- places too much data in ServiceRequest;
- proposes additional policy/helper classes too early;
- assigns the responsibilities of the individual operations appropriately.

*Independent Refinement of the Model*

Create a follow-up prompt that corrects proposals that do not correspond to the approved domain model.

The prompt must preserve ServiceRequest as the only holder of lifecycle state, must not introduce a status for Offer, must not add a scheduler job, and must assess whether scheduling and execution now justify introducing a separate entity.

*Defining Roles and Responsibilities*

Evaluate the following responsibilities:

- customer – accepts/rejects an offer;
- office staff – schedules execution;
- service employee – starts/completes execution;
- ServiceRequest – lifecycle transitions;
- ServiceExecution – operational timeline;
- service layer – rules dependent on time or repository data.

*Implementation*

Based on the approved model, formulate the prompts required for implementation.

Do not introduce new design patterns, helper layers, changes to the package organization, or additional entities unless they are required by a specific business rule.

After each major change, build and run the project.

*Architectural Review*

After the initial implementation, request a review focused on:

- separation of responsibilities;
- duplication;
- domain invariants;
- dependencies between services;
- organization of DTOs and repositories;
- premature abstractions.

*Gap Analysis Against the Business Rules*

Create a prompt that classifies each business rule as:

- fully implemented;
- partially implemented;
- missing.

For partially implemented and missing rules, require an explanation of exactly what is missing.

*Correcting Only the Identified Gaps*

Formulate a prompt for implementing only the missing or partially implemented business rules.

Architectural changes must be prohibited unless they are required for the rule itself.

Review the modified files afterwards.

### Manual Architectural Change

After the functional changes have been completed, select one part of the code where the accumulated logic has begun to reduce readability or blur responsibilities.

Perform a limited manual refactoring without assistance from the AI assistant.

The assistant should then be used only to review the change that was made.

### Lab Result

By the end of Laboratory Exercise 6, the following should be available:

- a completed lifecycle up to COMPLETED, CANCELLED, and EXPIRED;
- a separate ServiceExecution;
- correct boundaries according to user roles;
- a complete set of rules for scheduling, cancellation, and execution;
- calculation of the loyal-customer discount and the out-of-hours surcharge;
- an immutable accepted offer price;
- a gap analysis against the business requirements;
- at least one justified manual architectural change;
- an AI review of the manual change.



