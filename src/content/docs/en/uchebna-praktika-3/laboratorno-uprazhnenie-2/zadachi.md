---
title: Tasks
taskPage: true
sidebar:
  label: Tasks
  order: 100
---
### Extended Business Context

The customer submits a service request by specifying:

•	name; 

•	email; 

•	description; 

•	service type; 

•	execution mode; 

•	priority. 

For an on-site service, an address is provided, while an urgent request requires a reason for urgency.

The system records the submission time and assigns the initial status SUBMITTED. The customer does not provide system-managed values.

A company employee can start processing a request with status SUBMITTED. When processing starts, its status changes to PROCESSING.

At a later stage, the company may prepare an offer, and after its acceptance, execution of the service may be scheduled. These parts of the process are outside the scope of the current exercise.

Before working with the AI, determine:

•	which information represents functional and domain context; 

•	which parts of the existing project are relevant to the current task; 

•	which known information about the future evolution of the system is not necessary for the current analysis. 

### Entity, Value Object, and Enum

An **Entity** is a domain object with its own identity, allowing it to be tracked independently as its properties change.

A **Value Object** is an object without its own identity or independent lifecycle. It represents a value or a set of related values and belongs to another object in the domain model.

An **Enum** is appropriate for a closed set of predefined allowed values.

### Analysis of the Business Requirements

Before submitting the request, determine which existing files are necessary for analyzing the current domain model.

Provide:

•	ServiceRequest; 

•	the existing request and response DTOs for the service request; 

•	ServiceType; 

•	ExecutionMode; 

•	Priority; 

•	RequestStatus. 

If the DTOs generated in the previous exercise have different names, use the actual files from the project.

Do not provide the remaining files merely because they are available in the project.

Submit the following prompt:

```
### Task
Analyse the following business requirements in the context of the existing application.
Identify the domain concepts relevant to the current task and explain their responsibilities.
Distinguish between entities, value objects and enums.
Assess whether customer information should remain directly in ServiceRequest or should be represented by a separate domain concept.
Do not modify the project.

### Context
A service request contains:
- customer name and email;
- description;
- service type;
- execution mode;
- priority;
- address when execution is ON_SITE;
- urgency reason when priority is URGENT.
A new request has status SUBMITTED. System-managed values are not provided by the customer.
A request in SUBMITTED status can start processing. When processing starts, its status becomes PROCESSING.
Offer creation and service execution are future stages and are outside the scope of the current task.

### Constraints
Do not redesign unrelated parts of the application.
Do not analyse or implement the internal structure of future Offer or ServiceExecution concepts.
Do not generate or modify code.
First provide only the domain analysis.

### Expected output
For the concepts relevant to the current task:
- identify their responsibilities;
- classify them as entities, value objects or enums where appropriate;
- explain whether customer information requires a separate domain concept;
- justify the proposed domain model.
```

### Evaluation of the Proposed Domain Model

Evaluate the generated result according to the criteria for analyzing AI-generated results introduced in the previous exercise.

For the proposed domain concepts, additionally assess:

•	whether the object has its own identity and independent lifecycle; 

•	whether it represents a value belonging to another domain object; 

•	whether it represents a closed set of allowed values; 

•	whether separating it into an independent class is necessary within the current scope; 

•	whether the proposal follows from the business requirements or represents an additional decision made by the AI. 

Pay particular attention to customer information. Determine whether the customer is managed independently of the request and has its own identity and lifecycle, or whether the name and email represent information belonging to the particular request.

Do not automatically accept the classification proposed by the AI. The decision must be justified with respect to the business requirements, the existing project, and the defined scope.

### Approving the Change

After the analysis, determine which proposals are justified within the current scope.

For the purposes of the next part of the exercise, adopt the following design decision:

•	customer information is represented by an immutable value object CustomerInfo containing customerName and customerEmail; 

•	CustomerInfo belongs to ServiceRequest and is not a separate entity; 

•	address, description, and urgencyReason remain fields of type String; 

•	the external REST API retains the customerName and customerEmail fields; 

•	the future Offer and ServiceExecution concepts are not implemented. 

Compare this decision with the AI's proposal and determine which of its proposals are accepted and which are rejected.

### Impact Analysis and Updating the Context

Before modifying the code, perform an impact analysis.

Determine:

•	which existing classes will be affected by introducing CustomerInfo; 

•	how the change affects the persistence layer; 

•	which DTO mappings need to be adapted; 

•	which of the previously provided files remain relevant; 

•	which additional files are necessary to implement the change safely. 

Add only the files identified as relevant.

The context required for analyzing the domain model is not necessarily sufficient for its implementation. It should be updated according to the specific task and the expected impact of the change.

### Implementing the Approved Change

After updating the context, submit:

```text
### Task
Implement the approved change in the existing project.

### Approved change
Introduce an immutable CustomerInfo value object containing customerName and customerEmail.
Persist CustomerInfo as part of ServiceRequest using JPA @Embeddable and @Embedded.
CustomerInfo is not a separate entity and must not have its own repository.
Update the affected DTO mappings while preserving the existing external API fields customerName and customerEmail.
Keep address, description and urgencyReason as String fields.

### Constraints
Preserve the existing REST endpoints and current application behavior.
Do not add Offer, ServiceExecution or other future functionality.
Do not redesign unrelated parts of the application.
Modify only the files affected by the approved change.
Keep affected mappings, imports and persistence annotations consistent.
```

The Agent may propose actions and changes to the provided parts of the project. These must not be accepted automatically.

### Reviewing the Proposed Changes

Before approving the changes proposed by the Agent, check:

•	which files will be created or modified; 

•	whether they correspond to the performed impact analysis; 

•	whether the changes are limited to the defined scope; 

•	whether exactly the approved design decision has been implemented; 

•	whether unsolicited classes, dependencies, or functionality have been added; 

•	whether the external REST API has been preserved; 

•	whether unrelated parts of the application have been changed. 

If there is a discrepancy, do not accept the proposal merely because it was generated by the Agent. Identify the reason and formulate a specific clarification or correction.

After verification, approve only the necessary changes.
