---
layout: default
title: Laboratory exercise 2
parent: Training Practice 3
has_children: true
nav_order: 2
---

# Laboratory exercise 2

## Analysis and Development of the Domain Model

### Objective

The objective of this exercise is to analyze the initial application model created in the previous exercise and refine the main domain concepts, their responsibilities, and their relationships.

The AI assistant will first be used for analysis and modeling, without generating or modifying code. After the proposed changes have been evaluated, only the approved changes will be implemented.


### Initial State

Use the final working version of the project from Laboratory Exercise 1.

The project contains: ServiceRequest; the main enum types; DTOs; repository; service; basic REST endpoints.

### Extended Business Context

A customer submits a service request by providing:

- name;
- email address;
- description;
- service type;
- execution mode;
- priority.

For an on-site service, an address must be provided, while an urgent request requires a reason for urgency.

The system records the submission time and assigns an initial status.

A company employee can start processing a submitted request.

At a later stage of the process, the company may prepare an offer and, once the offer has been accepted, schedule the execution of the service. These parts of the process are not implemented in the current exercise.

### Analysis of the Business Requirements

Provide the following prompt:

```
Analyze the following business requirements together with the existing Spring Boot project.
Requirements:
•	request has client name, email, description, service type, execution type, address if the execution is on site, urgency reason if the request is urgent; 
•	request is in SUBMITTED status on creation; 
•	the customer cannot define initial status, price, deadline to receive offer and internal data; 
•	description should have minimal and maximal length; 
•	when execution is remote there is no need of address; 
•	INSTALLATION and REPAIR could be executed ON SITE; 
•	DIAGNOSTICS and CONSULTATION could be ON SITE/REMOTE; 
•	Priority URGENT requires a textual reason for urgency. 
Request processing requirements:
•	only request with SUBMITTED status could be processed; 
•	when processing starts, the request status switches to PROCESSING; 
•	the company should send offer or cancel the request up to 3 working days from the submitting moment; 
•	working days are defined as Monday to Friday; holidays are not considered; 
•	urgent requests could be submitted from Monday to Friday till 8PM; 
•	urgent request processing starts immediately; 
•	if there is no offer up to the deadline and such offer is not rejected, the status of the request switches to EXPIRED; 
•	only request with status PROCESSING could get an offer and be rejected; 
•	the cancellation requires a reason; 
•	if request is rejected, its status switches to REJECTED; 
•	rejected request could not be restored; the customer should place a new request. 
Identify the necessary domain concepts and explain their responsibilities. Distinguish between entities, value objects, and enums. Assess whether customer information requires a separate entity.
The later offer and service-execution stages may be identified as future domain concepts, but they are outside of the implementation scope at the moment.
Do not generate or modify code.

```

*Evaluation of the Proposed Domain Model*

Review the proposed concepts. For each of them, determine:

- whether it has its own identity;
- whether it has an independent lifecycle;
- whether it represents a value that belongs to another object;
- whether it represents a closed set of allowed values;
- whether it needs to be a separate class at the current size of the project.

Pay particular attention to:

- ServiceRequest;
- customer information;
- address;
- description;
- reason for urgency;
- ServiceType, ExecutionMode, Priority, RequestStatus.

*Analysis of Customer Information*

Determine whether the customer should be modeled as a separate entity. Consider the following:

- whether the customer has their own identifier;
- whether the customer has an independent lifecycle;
- whether the customer is managed independently of the request;
- whether multiple requests need to reference the same customer object;
- whether the request can store a snapshot of the customer's name and email address at the time of submission.

*Approval of Changes*

After the analysis, identify only the changes that are justified at the current stage of the project.

If the decision is made to represent customer information as a value object, the following prompt can be used:

```
Based on the analysis, apply only the following approved changes to the current project:
• Introduce an immutable CustomerInfo value object containing customerName and customerEmail. Persist it as part of ServiceRequest using JPA @Embeddable and @Embedded. CustomerInfo is not a separate entity and must not have its own repository.
• Keep address, description, and urgencyReason as String fields. Do not introduce separate value-object classes for them.
• Update the request and response DTO mappings to work with CustomerInfo while preserving the existing external API field names customerName and customerEmail.
• Preserve the existing REST endpoints and application behavior.
Do not add Offer, ServiceExecution, pricing, deadline calculation, new status transitions, domain-policy services, or any other future functionality.
Update all affected mappings, imports, constructors, and persistence annotations. Then build and run the application and report the actual result.

```

*Review of the Organization of Domain Types*

After the changes have been made, check:

- which package contains CustomerInfo;
- whether the package name corresponds to the role of the class;
- whether entities, value objects, and enums are mixed without a clear reason;
- whether too many technical packages have been created;
- whether any empty packages remain after moving classes.

If the package organization does not appear semantically consistent, ask the AI assistant to justify its choice without modifying the code.

*Refining the Package Organization*

If necessary, request a recommendation for a simple and semantically consistent structure without creating a separate package for every technical detail.

The following prompt can be used:

```
The goal is not to introduce a dedicated package for every technical concept but the structure to remain simple and semantically consistent. Which package organization would you recommend for this project if I want to distinguish between entities from other domain types without unnecessary technical packages?
Do not modify the code.

```

Evaluate the proposed structure and select an organization that:

- clearly distinguishes entities from the other domain types;
- does not introduce unnecessary package depth;
- remains appropriate for the current size of the project.

*Final Verification*

After implementing the approved changes:

- review all modified files;
- check the imports and package declarations;
- verify that no empty packages remain;
- compile the project;
- run the application.

Test the REST API using Postman.

Send requests to the implemented endpoints to:

- create a new service request;
- retrieve a request by its identifier;
- retrieve requests by customer email address;
- retrieve the requests available to office staff.

When creating a request, check:

- which fields are sent by the customer;
- which values are assigned by the system;
- what the resulting JSON response looks like;
- whether the customerName and customerEmail field names have been preserved in the external API after introducing CustomerInfo.

### Lab Result

By the end of the exercise, the following should be available:

- a well-reasoned conceptual model of the current domain;
- a clear distinction between entities, value objects, and enums;
- a decision about which concepts should be implemented now and which should be left for a later stage;
- a working ServiceRequest model with customer information organized according to the selected domain design;
- an unchanged external REST API;
- a semantically consistent package structure.
