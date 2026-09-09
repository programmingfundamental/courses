---
layout: default
title: Laboratory exercise 5
parent: Training Practice 3
has_children: true
nav_order: 5
---

# Laboratory exercise 5

## Extending the Business Logic

### Objective

The objective of this exercise is to extend the request-processing workflow by introducing an offer, a price breakdown, and new time-related and lifecycle rules.

The exercise also examines which new concepts require separate domain types and which architectural abstractions would still be premature.

### Initial State

Use the final working version from Laboratory Exercise 4. The application already supports:

- transition from SUBMITTED to PROCESSING;
- transition from PROCESSING to REJECTED;
- 409 responses for invalid lifecycle operations;
- an encapsulated ServiceRequest;
- transactional modification operations.

### New Business Context

After processing has started, an employee may create an offer for the request.

The offer:

- belongs to exactly one request;
- at most one offer may exist for a given request;
- may be created only for a request with status PROCESSING;
- contains a description of the proposed work;
- contains the creation time and validity deadline;
- contains an estimated duration;
- contains a price breakdown;
- has no status of its own;
- causes the request to transition to OFFERED after it is created.

Validity rules:

- normal request – 3 working days after the offer is created;
- urgent request – 30 minutes after the offer is created;
- an offer for an urgent request must be created within 1 hour after the request was submitted.

The price consists of:

- a base price according to the service type;
- a fee according to the execution mode;
- a surcharge according to the priority;
- an additional out-of-hours fee;
- a loyalty discount.

In the current version, both the out-of-hours fee and the loyalty discount have a value of 0. The subtotal, loyaltyDiscount, and finalPrice values are calculated by the system and are not provided externally.

*Initial Analysis Without Implementation*

Provide the following prompt:

```
The business process now continues with creating an offer for given request. 
Business requirements:
• An offer can only be created for a request in status PROCESSING
• Each request can have at most one offer
• An offer contains the total proposed price and a validity period
• The total price depends on service type, execution mode, and whether the request is urgent
• The pricing rules are expected to evolve in future iterations
• Customer acceptance, scheduling, execution and expiration will be implemented later.
Analyze the current project together with these new requirements.
Propose:
• The required domain model changes
• Whether Offer should be a separate entity, value object or something else
• The relationship between Offer and ServiceRequest
• The minimal fields required for Offer in the current iteration
• Where pricing logic should be located
• Whether the current architecture is still enough or whether any new abstractions are justified
• Which functionality belongs in these changes and which should remain out of scope.
Do not generate or modify code.

```

Then evaluate:

- whether it is justified to model Offer as a separate entity;
- whether a bidirectional relationship is necessary;
- whether a separate pricing component is justified at this stage;
- which future concepts the AI assistant attempts to introduce prematurely.

*Refining the Model*

Prompt: 

```
Refine the proposal based on the following decisions:
• Offer is a separate entity,
• Use a unidirectional relationship from Offer to ServiceRequest,
• ServiceRequest must not contain Offer field,
• The servce-request foreign key in Offer must be unique,
• Offer contains only id, serviceRequest, totalPrice, createdAt, and validUntil,
• The application uses a single currency so do not add currency or Money
• Creating the offer changes the request status to OFFERED
• An offer can be created only for request in PROCESSING state
• Offer creation and the request status change must be performed in one transaction
• Use a separate OfferService and do not add generic CRUD operations.

For this modification, do not introduce OfferPricingPolicy or OfferPriceCalculator, a rule engine, or another pricing abstraction.

First pricing version should be implemented directly and clearly, using the current fixed rules: base price according to ServiceType; fixed amount according to ExecutionMode; fixed surcharge when Priority is URGENT.

The purpose of this iteration is to obtain a simple working implementation whose growing conditional logic can be analyzed in the next iteration.
Considering this prompt, propose:
• the exact responsibilities of Offer and OfferService
• the required DTOs and endpoints
• where the fixed prices should be stored
• the transaction flow
• the required domain method on ServiceRequest
• the expected 400, 404 and 409 cases.
Do not generate or modify code.

```

Analyze the following aspects:

- the responsibilities of Offer;
- the responsibilities of the corresponding service;
- the endpoints;
- the transaction boundary;
- 400, 404, and 409 cases;
- why markOffered belongs to ServiceRequest.

*Refining the Final Model*

The next prompt should introduce the complete approved model:

- Offer;
- PriceBreakdown;
- enum types with properties;
- validity rules;
- price calculation;
- no status for Offer;
- no use of design patterns;
- no automation of offer acceptance, scheduling, or expiration.

*Analysis of Fixed Domain Characteristics*

Consider the proposal to store fixed characteristics in the enum types:

- in the service type – base price, standard duration, and whether remote execution is supported;
- in the execution mode – execution fee;
- in the priority – surcharge.

Evaluate when such values are reasonable as enum properties and when they should instead become configurable data.

In the current project, this approach is reasonable because the values represent fixed domain characteristics rather than, for example, administrator-configurable settings.

Experiment: examine what happens when no specific values are provided. Check which values were selected by the AI assistant and whether they follow from the business requirements or were invented.

*Implementation*

Provide a prompt containing the changes approved for implementation. The prompt must:

- describe only the approved changes;
- specify the constraints of the current iteration;
- preserve the existing API and project organization except where a change requires otherwise;
- prohibit the introduction of unrequested architectural abstractions;
- require the project to be built and run after the changes.

*Review of the Generated Code*

Check:

- whether the JPA relationship is correct;
- whether there is a unique constraint;
- whether PriceBreakdown actually calculates the internally derived values;
- whether subtotal and finalPrice can be provided externally;
- whether the offer-validity check is correct;
- whether an unnecessary offer status has been introduced;
- whether the DTOs contain only the information required for their purpose;
- whether the pricing logic remains direct and clear;
- whether abstractions outside the approved scope have been added;
- the quality of the naming, particularly method names.

*Final Verification*

Use REST requests to verify:

- successful creation of a valid offer;
- a second attempt to create an offer for the same request – 409;
- creation of an offer for a request in SUBMITTED state – 409;
- creation of an offer for a non-existent request – 404;
- an empty description of the required work – 400;
- an urgent offer created after the one-hour deadline – 409;
- the request status changes to OFFERED;
- the response contains PriceBreakdown;
- the internally calculated fields contain consistent values.

### Lab Result

By the end of Laboratory Exercise 5, the following should be available:

- a separate Offer entity;
- a unidirectional relationship to ServiceRequest;
- at most one offer per request;
- a PriceBreakdown field implemented as an @Embeddable value object;
- fixed pricing characteristics stored in the enum types;
- the first direct implementation of pricing;
- offer-validity rules;
- transition from PROCESSING to OFFERED;
- transactional offer creation;
- preserved 400, 404, and 409 handling;
- verified functionality without unnecessary pricing architecture.



