---
title: Tasks
taskPage: true
sidebar:
  label: Tasks
  order: 100
---

### New Business Context

**Offer Acceptance, Rejection, and Expiration**

•	one service request can have only one offer;

•	the customer can accept or reject only a valid offer; 

•	when the offer is accepted, the request transitions from OFFERED to ACCEPTED; 

•	when the offer is rejected, the request transitions from OFFERED to REJECTED; 

•	after validUntil, the offer can no longer be accepted or rejected, and the request transitions to EXPIRED. 

**Service Scheduling**

•	only a request in the ACCEPTED state can be scheduled; 

•	a remote service can be scheduled no earlier than 4 hours after the time of scheduling; 

•	an on-site service can be scheduled no earlier than the next working day; 

•	scheduling determines an expected start and end time; 

•	successful scheduling results in status SCHEDULED. 

**Service Execution**

•	execution is represented by a separate ServiceExecution object; 

•	only a request in the SCHEDULED state can start execution; 

•	a service worker starts execution through an explicit operation; 

•	when execution starts, the actual start time is recorded and the request transitions to IN_PROGRESS; 

•	completion is also performed through an explicit operation; 

•	upon completion, the result of the performed work and the actual end time are recorded, and the request transitions to COMPLETED. 

**Cancellation**

•	a request can be cancelled before execution starts; 

•	cancellation requires a reason; 

•	a scheduled ON_SITE service can be cancelled no later than 24 hours before the scheduled start time; 

•	a scheduled REMOTE service can be cancelled no later than 4 hours before the scheduled start time; 

•	successful cancellation results in status CANCELLED. 

**Final Pricing**

•	each service type has a base price;

•	a fixed visit fee is added for ON_SITE; 

•	a fixed surcharge of €40 is added for URGENT; 

•	a customer is considered loyal if they have at least three previous requests with status COMPLETED; 

•	a loyal customer receives a 10% discount; 

•	applicable surcharges are added before the discount is applied;

•	the price is represented by PriceBreakdown, which makes it possible to trace the individual components and the final price; 

•	the price and price breakdown are calculated by the system and are not provided by the customer. 

### Task Decomposition

The new business context contains several related changes. Before implementation, divide the task into smaller parts that can be implemented and verified sequentially.

Independently select the necessary project context and provide the relevant files.

Formulate a prompt asking the AI assistant to propose a decomposition of the task. Use the basic structure:

**Task / Context / Constraints / Expected output**

At this stage, do not generate or modify code.

For each proposed part, the AI assistant should identify the functionality and business rules it covers, dependencies on the other parts, affected components, required project context, and a way to verify it independently.

### Evaluation of the Proposed Decomposition

Do not automatically accept the proposed decomposition.

Check:

•	whether all business requirements are covered; 

•	whether the individual parts are logically separated and independently verifiable; 

•	whether dependencies between them have been considered; 

•	whether the proposed implementation order is appropriate; 

•	whether any part combines too many different responsibilities; 

•	whether the task has been divided into unnecessarily small steps; 

•	whether unnecessary architectural changes or abstractions have been proposed; 

•	whether the proposed project context is sufficient and relevant. 

If necessary, revise the decomposition and determine the final implementation sequence.

### Implementation of the Individual Parts

After approving the decomposition, implement the task sequentially.

For each part:

- define its scope;

- perform a brief impact analysis;

- select the necessary project context;

- independently formulate an implementation prompt;

- review the actions and changes proposed by the Agent before applying them;

- independently verify the implemented behavior.

Follow the minimal change principle – implement only what is necessary for the current part, without prematurely implementing subsequent parts or performing unrelated restructuring.

After each part, compile and run the project and perform an appropriate functional check. Proceed to the next part only after verifying the current implementation.

When a problem is identified, apply:

**observation → diagnostic information → cause hypothesis → hypothesis verification → minimal correction → re-verification**

After identifying the cause, formulate a specific follow-up prompt for the necessary correction instead of regenerating the entire solution.

### Critical Review of the Implemented Solution

After implementing the individual parts, perform an overall critique/review of the resulting solution with the assistance of the AI.

Independently determine the necessary project context and evaluation criteria and formulate a prompt for the review.

The review should examine the interaction between the implemented parts. The following aspects may be evaluated:

•	distribution of responsibilities; 

•	compliance with domain rules and lifecycle transitions; 

•	duplicated logic; 

•	dependencies between components; 

•	transaction boundaries; 

•	unnecessary dependencies and premature abstractions; 

•	preservation of previously implemented behavior;

•	correspondence between the changes made and the previously defined scope.

The resulting review is a proposed evaluation. Before making any change, each identified weakness must be verified in the actual code.

### Gap Analysis Against the Business Requirements

**The gap analysis is performed by the student with the assistance of the AI.**

The objective is to compare the complete business requirements with the current state of the implementation and identify any gaps.

Start a new Agent session for the gap analysis so that the analysis is not based on conversational context from the previous generation and correction of the solution.

In the new session:

- provide the complete business requirements as the required state;

- independently determine the necessary project context;

- provide the relevant files from the current implementation as the current state;

- formulate a prompt for systematically comparing each business rule with the provided implementation;

- at this stage, do not generate or modify code.
  
For each business rule, the AI assistant should propose one of the following results:

•	fully implemented – implementation of the entire rule has been identified; 

•	partially implemented – only part of the required behavior has been implemented; 

•	missing – no behavior implementing the rule has been identified in the provided implementation; 

•	insufficient context for verification – the provided files do not allow a reliable evaluation. 

For the proposed evaluation, the specific location in the code on which it is based must be identified. For a partially implemented or missing rule, the specific identified gap must be stated.

If the context is insufficient, the student should determine what additional information is required, add the corresponding files, and repeat the verification.

The result produced by the AI does not constitute the final gap analysis. The student must verify the stated evidence in the actual code and determine the final status of each rule.

The presence of a class, method, or endpoint with an appropriate name is not sufficient evidence that a rule has been implemented.

### Correcting the Identified Gaps

Based on the verified gap analysis, determine the rules that require correction.

For each identified gap, perform a brief impact analysis, select the necessary project context, and formulate a prompt only for the specific correction.

Do not change the existing architecture or already working functionality unless this is necessary to address the identified gap.

Follow the minimal change principle.

After the change, verify both the corrected behavior and the preservation of related functionality that was already working.

### Final Verification

After implementing the corrections, perform an independent verification of the overall behavior.

Compile and run the project and verify the REST operations using Postman.

Verify at least:

•	the main lifecycle
SUBMITTED → PROCESSING → OFFERED → ACCEPTED → SCHEDULED → IN_PROGRESS → COMPLETED; 

•	offer rejection and expiration; 

•	the scheduling constraints for REMOTE and ON_SITE; 

•	starting and completing service execution; 

•	permitted and prohibited cancellation and the corresponding time constraints; 

•	correct calculation of the base price, ON_SITE fee, URGENT surcharge, and loyalty discount; 

•	application of surcharges before the discount; 

•	appropriate HTTP status codes for invalid operations, invalid input data, and a missing resource. 

The final verification is performed independently of the AI assistant's evaluation. If a discrepancy is found, identify the violated business rule and the cause of the problem.

If necessary, perform root-cause analysis and make a minimal correction, then re-verify the affected and related behavior.
