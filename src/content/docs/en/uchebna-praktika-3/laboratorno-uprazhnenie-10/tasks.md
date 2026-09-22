---
title: Tasks
taskPage: true
sidebar:
  label: Tasks
  order: 100
---
### Practical Task

A separate Spring Boot application called Notification Service should be added to the developed application.

The Notification Service should receive a notification when an offer is successfully created. For the purposes of the exercise, actual e-mail delivery or the use of an external service is not required. The received notification may be processed and written to the application log.

Before generating code, use the AI assistant to analyze where notification functionality naturally fits into the existing business process.

Prompt:
```
Analyze the existing Spring Boot application and identify where notification functionality would naturally fit into the current business workflows.

The goal is to introduce a separate Notification Service that will be responsible for receiving notification requests from the main application.

Identify:

which existing business events should trigger a notification;
what information the main application would need to send to the Notification Service;
which parts of the current application would need to be changed;
what responsibilities should remain in the main application and what should belong to the Notification Service;
suitable options for communication between the two applications;
what should be verified with an integration test.

Keep the proposal minimal and consistent with the current project. Do not introduce messaging infrastructure, service discovery, authentication, or other additional infrastructure unless it is clearly necessary.

Do not generate or modify code yet.
```

Review the resulting analysis. Pay attention to whether the AI assistant proposes functionality or infrastructure that is unnecessary for the task.

For the current exercise, the functionality should be limited to a single business scenario, such as successful offer creation.

#### Implementing the Notification Service

After the analysis, limit the functionality to one business scenario – sending a notification when an offer is successfully created.

The Notification Service should be implemented as a separate Spring Boot application. Communication between the main application and the Notification Service should use a synchronous HTTP request.

For the purposes of the exercise, actual e-mail delivery is not implemented. The received information is processed by the Notification Service and the message is written to the application log.

Prompt:

```
Based on the analysis, implement a minimal separate Notification Service and integrate it with the existing application.

For this exercise, support only one notification scenario: when an offer is successfully created for a service request.

Requirements:

create the Notification Service as a separate Spring Boot application;
use synchronous HTTP communication from the main application to the Notification Service;
keep the notification contract minimal;
the main application should send the notification only after successful offer creation;
the Notification Service should receive the request and compose/log the notification message;
do not add messaging infrastructure, service discovery, authentication, persistence, retry mechanisms, or other additional infrastructure;
do not modify unrelated production code.

Before modifying code, describe the files and components you plan to add or change.

Do not implement the integration test yet.
```

Before code generation, review the proposed plan. Verify that the suggested changes are limited to the required components and that unrelated business logic is not modified.

After the code has been generated, review:

- the location from which the notification is sent;
- the HTTP client and configuration of the Notification Service address;
- the structure of the data being sent;
- the Notification Service endpoint;
- the separation of responsibilities between the two applications.

Pay particular attention to the moment at which the notification is sent. A notification must not be sent if offer creation does not complete successfully.

#### Verifying the Communication

Run both applications simultaneously on different ports. Using Postman, execute the business sequence required to create an offer:

create service request
        ↓
transition to PROCESSING
        ↓
create offer
        ↓
HTTP request to Notification Service

Verify that when an offer is created successfully, the Notification Service receives the request and writes the corresponding message to the application log.

#### Creating an Integration Test

After the communication between the two applications has been verified manually, analyze what kind of integration test is appropriate for the implemented interaction.

At this stage, do not assume in advance that all components must necessarily be real or that they should be replaced with mock/stub objects. The integration boundary should be determined first.

Prompt:

```
Analyze the implemented offer-created notification integration and propose the integration test cases needed to verify the communication between the main application and the Notification Service.

Focus on the integration boundary, not on duplicating unit tests of OfferService or NotificationService.

Identify:

- what components should be real in the integration test;
- what, if anything, should be mocked;
- how the HTTP communication should be exercised;
- what should be asserted in the successful case;
- what should be verified when offer creation fails or the transaction does not complete successfully.

Keep the test scope minimal and suitable for the current project.

Do not generate or modify code yet.
```

Analyze the resulting proposal before generating the test. Determine what the proposed test would actually prove.

For this task, the integration test must verify real HTTP communication between the two Spring Boot applications.

*Task*

If necessary, refine the AI assistant's initial proposal so that the integration test includes the real web layers of both applications and real HTTP communication between them. Do not add complex external infrastructure solely for the purpose of the test.

Two scenarios are mandatory:

- successful offer creation → the Notification Service receives exactly one notification containing the expected data;
- failed offer creation → the Notification Service does not receive a notification.
