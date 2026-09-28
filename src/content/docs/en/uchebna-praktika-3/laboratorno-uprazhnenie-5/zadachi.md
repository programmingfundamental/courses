---
title: Tasks
taskPage: true
sidebar:
  label: Tasks
  order: 100
---

### New Business Context

An offer associated with a service request must be added to the existing application.

It is known that:

•	one service request can have only one offer; 

•	the offer contains a description of the proposed work; 

•	the offer contains its creation date and time; 

•	the offer has a validity period; 

•	the offer contains a final price and a price breakdown; 

•	the final price must be positive; 

•	an offer can be created only for a request with status PROCESSING; 

•	after successful creation of the offer, the request transitions to status OFFERED; 

•	the price depends on the service type, execution mode, and priority. 

At this stage, the specific prices, surcharges, and the rule for determining the offer validity period have not been provided.

This information is intentionally omitted during the initial analysis.

### Context Selection

Before the first prompt, determine what information from the current project is necessary for analyzing the new business requirements.

Determine which files describe:

•	the current request model; 

•	the characteristics involved in pricing; 

•	the lifecycle behavior of the request; 

•	the existing service operations. 

Provide only the files identified as relevant.

Depending on the current implementation, these may include ServiceRequest, ServiceType, ExecutionMode, Priority, RequestStatus, and the corresponding service class. The list should not automatically be treated as mandatory – the selection should reflect the actual state of the project.

The following steps are performed in the **same Agent session** so that the additional missing information can be compared with the initial analysis.

### Analysis of Incomplete Context

Formulate a prompt asking the AI to analyze the new business requirements without implementation.

The prompt should explicitly require a distinction between:

•	confirmed information; 

•	missing information; 

•	possible assumptions; 

•	questions that must be clarified before implementation. 

The following can be used:

```
### Task
Analyse the following new business requirements in the context of the existing application.
For each significant implementation decision distinguish:
- confirmed information;
- missing information;
- possible assumptions;
- whether clarification is required before implementation.
Determine which missing information prevents a reliable implementation decision and which questions can be deferred.

### Context
- one service request can have only one offer;
- an offer contains a description of the proposed work;
- an offer contains its creation date and time;
- an offer has a validity period;
- an offer contains a final price and a price breakdown;
- the final price must be positive;
- an offer can be created only for a PROCESSING request;
- after successful offer creation the request becomes OFFERED;
- the price depends on service type, execution mode and priority.
Concrete prices, surcharges and the offer-validity rule have not been provided yet.

### Constraints
Do not generate or modify code.
Do not invent prices, surcharges, validity periods or other business values that are not explicitly provided.
Do not treat an assumption as a confirmed requirement.
If the project context is insufficient for a conclusion, identify the missing context instead of assuming its content.
```

### Analysis of the Result

Evaluate whether the AI clearly distinguishes the provided information from its own assumptions.

Pay particular attention to whether the AI:

•	invents specific prices or surcharges; 

•	assumes a particular validity period without a provided rule; 

•	proposes a pricing formula that does not follow from the requirements; 

•	makes unsupported claims about the current project; 

•	presents a possible design decision as an already established requirement; 

•	recognizes when the available project context is insufficient. 

If the AI generates specific nonexistent information and presents it as fact, determine whether the problem represents an assumption, unsupported claim, or hallucination according to the concepts introduced in the previous exercises.

The objective is not to classify every different interpretation as an error, but to determine the actual status of the information.

### Providing the Missing Business Context

After the initial analysis, provide the following confirmed information.

The following base prices are defined for ServiceType:

INSTALLATION – 120

REPAIR – 100

CONSULTATION – 50

For ExecutionMode:

ON_SITE – additional price 30

REMOTE – additional price 0

For Priority:

STANDARD – surcharge 0

URGENT – surcharge 40

The final price is calculated as:

*base price + execution mode additional price + priority surcharge*

The offer is valid for 3 calendar days from the time of creation.

In the same Agent session, provide the new information through a follow-up prompt. The student should formulate the prompt so that it requests:

•	an update of the previous analysis; 

•	identification of which missing information has now been clarified;

•	determination of which previous assumptions have been confirmed or rejected; 

•	identification of information that is still missing and required for the current implementation. 

At this stage, **do not generate code**.

### Evaluation After Providing the Additional Context

Compare the initial analysis with the updated result.

Determine:

•	which missing information has now been clarified; 

•	which assumptions have been confirmed; 

•	which assumptions have turned out to be incorrect; 

•	whether the AI has corrected its previous assumptions; 

•	whether it continues to use values or rules that contradict the newly confirmed context; 

•	whether any missing information remains that actually blocks the current implementation. 

Information generated by the AI in a previous step does not become a requirement merely because it remains in the conversational context.

### Defining the Domain Model

After providing the necessary context, ask the AI to analyze the required domain model.

Consider at least:

•	Offer; 

•	PriceBreakdown; 

•	the relationship between Offer and ServiceRequest; 

•	responsibility for calculating the price; 

•	representation of the fixed pricing components; 

•	determination of createdAt and validUntil; 

•	the PROCESSING → OFFERED transition. 

The student should evaluate the proposal and decide:

•	whether Offer has its own identity and lifecycle; 

•	whether PriceBreakdown represents a value object; 

•	whether the proposed new classes and dependencies are justified; 

•	whether introducing additional pricing abstractions is necessary;

•	whether the solution is appropriate for the current size and requirements of the project. 

Pay attention to whether the AI proposes, for example, pricing strategies, factories, rule engines, or other abstractions that do not solve a necessary problem in the current context.

Such a proposal is not automatically incorrect. It should be evaluated in terms of what problem it solves, what complexity it adds, and whether that complexity is justified by the current requirements.

Also discuss what future change could make the selected simpler solution no longer appropriate – for example, if prices need to be configurable independently of application deployment.

The objective is to select a solution justified by the current requirements rather than the most complex or flexible solution possible.

### Approving the Solution

Before implementation, the student defines the final model for the current iteration.

For the purposes of the exercise, the solution must satisfy the following requirements:

•	Offer represents the offer for a specific service request; 

•	one service request can have at most one offer; 

•	PriceBreakdown represents the individual price components; 

•	the price is calculated using only the confirmed pricing components; 

•	createdAt is determined by the system; 

•	validUntil is 3 calendar days after createdAt; 

•	an offer can be created only for a PROCESSING request; 

•	after successful creation of an offer, the request transitions to OFFERED. 

The specific internal implementation should not automatically be adopted from the AI's proposal. The student should be able to justify the selected classes, responsibilities, and dependencies.

### Impact Analysis and Updating the Context

Before implementation, perform an impact analysis.

The student determines:

•	which existing files need to be modified; 

•	which new files are required; 

•	what additional project context should be provided; 

•	which parts of the existing application should remain unchanged. 

Add only the necessary additional files.

If implementation requires information from a file that is not available in the context, the file should be provided rather than its contents being assumed.

### Implementing the Approved Model

In the same Agent session, the student formulates a prompt for implementing the approved model.

The prompt should clearly include:

•	the approved domain concepts and their responsibilities; 

•	the confirmed formula and pricing components; 

•	the validity rule; 

•	the constraints on creating an offer; 

•	the required lifecycle transition; 

•	the parts of the project that must not be changed. 

The following should not be implemented:

•	accepting or rejecting an offer; 

•	editing an offer; 

•	automatic processing of expired offers; 

•	scheduling or execution of the service; 

•	unsolicited pricing abstractions; 

•	other future functionality. 

The Agent proposes actions and changes, but the student reviews them before approval.

### Reviewing the Proposed Changes

Before applying the changes, check:

•	whether the new classes correspond to the approved model; 

•	whether only the necessary files have been modified; 

•	whether exactly the confirmed pricing components have been used; 

•	whether PriceBreakdown is constructed correctly; 

•	whether createdAt and validUntil are determined correctly; 

•	whether it is guaranteed that a service request has at most one offer; 

•	whether an offer is created only for a PROCESSING request; 

•	whether the request transitions to OFFERED only after the offer has been created successfully; 

•	whether the AI has added unsolicited abstractions or functionality; 

•	whether unrelated changes have been made. 

If there is a discrepancy, determine whether the cause is missing context, an incorrect assumption, a violated constraint, an incorrect interpretation, or a problem in the generated solution.

The next follow-up prompt should request only the necessary correction.

### Behavior Verification

After applying the approved changes, compile and run the application.

Using the REST API, verify at least the following scenarios:

•	creating an offer for a PROCESSING request → the offer is created and the request becomes OFFERED; 

•	attempting to create a second offer for the same request → the operation is rejected; 

•	creating an offer for a request that is not PROCESSING → HTTP 409 Conflict; 

•	PriceBreakdown contains the correct pricing components, and the final price corresponds to their sum; 
•	validUntil is 3 calendar days after createdAt; 

•	the request status changes only after the offer has been created successfully. 

The verification checks the implemented solution against the confirmed business requirements.

If a discrepancy is found, apply the previously introduced root-cause analysis process and, after identifying the cause, formulate a follow-up prompt only for the necessary minimal correction.
