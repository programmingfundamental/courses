---
title: Tasks
taskPage: true
sidebar:
  label: Tasks
  order: 100
---

### Practical Task

A separate Spring Boot application, Notification Service, should be added to the developed application. It receives a notification when an offer has been created successfully.

Communication between the main application and Notification Service is performed through a synchronous HTTP request. No actual e-mail sending or external service should be implemented. The received notification is processed by Notification Service and written to the application log.

Do not add messaging infrastructure, service discovery, authentication, persistence, retry mechanisms, or other infrastructure that is unnecessary for the assigned task.

### Analysis of the Business Event and Required Context

Consider the following specific business scenario:

*when an offer has been created successfully, the main application must send a notification to a separate Notification Service.*

Before implementation, determine:

•	where successful offer creation is completed in the existing business process; 

•	what data are available at that point; 

•	which of those data are required for the notification; 

•	which existing components and artifacts contain the necessary information. 

Independently select the necessary project context. Use artifacts relevant to the current task instead of automatically providing the entire project.

With the assistance of the AI agent, analyze the existing business process and determine where notification sending should be introduced.

Verify the resulting proposal against the actual application artifacts. If missing information is identified, determine what additional context is required.

### Designing the Interaction Between the Applications

Based on the analysis, define the minimal interaction between the main application and Notification Service.

Determine:

•	which component in the main application initiates the sending; 

•	at what point in the business operation the communication takes place; 

•	what data should be transferred; 

•	which HTTP endpoint and method are provided by Notification Service; 

•	how responsibilities are divided between the two applications; 

•	what configuration information is required for communication. 

With the assistance of the AI agent, develop a proposal for a minimal contract between the applications.

Verify the proposal against the existing application and the task constraints. Determine whether the required data are actually available in the business process, whether the contract contains only the necessary information, and whether Notification Service takes over any business logic belonging to the main application.

Do not accept proposals for additional infrastructure or unrelated changes that are unnecessary for the specific interaction.

After verification, establish the contract between the applications that will be used during implementation.

### Implementing the Communication

Create Notification Service as a separate Spring Boot application. Implement communication from the main application to it through a synchronous HTTP request.

Before implementation, perform an impact analysis and define the scope of the change. Determine which components need to be created or modified in:

•	the main application; 

•	Notification Service; 

•	the configuration of communication between them. 

For the current stage, independently select the necessary context from both applications.

Ask the AI agent to implement the required changes according to the previously defined contract.

Before applying them, check:

•	whether they are limited to the necessary components; 

•	whether they preserve the defined contract; 

•	whether the notification is sent only after successful offer creation; 

•	whether the responsibilities of the two applications remain separated; 

•	whether unnecessary infrastructure or unrelated functionality is introduced. 

After approval, apply the necessary changes and verify the actual state of both applications.

### Verification of Actual HTTP Communication

Run the main application and Notification Service simultaneously on different ports.

Execute the following business sequence:

*create a request → transition to PROCESSING → create an offer → HTTP request to Notification Service*

Verify that when an offer is successfully created, Notification Service receives the expected data and writes the message to the application log.

Compare the observed behavior with the previously defined contract between the applications.

If a discrepancy is identified, apply root-cause analysis and make a minimal correction to the identified cause. An AI-proposed cause should be treated as a hypothesis until it has been confirmed through the code, configuration, diagnostic information, or observed behavior.

### Designing a Meaningful Integration Test

After verifying the actual communication, consider a new task:

How can it be automatically verified that when an offer is created successfully, the main application actually sends an HTTP request, and Notification Service actually receives it?

For the new task, determine the necessary project context again. Consider which artifacts from the main application and Notification Service are relevant and which information used during implementation is no longer necessary.

Define the integration test boundary:

•	which components must participate as real components; 

•	which dependencies may be replaced without removing the interaction that the test is intended to verify; 

•	how the HTTP communication should be exercised; 

•	what should be observed to determine that the notification has been received; 

•	how it should be established that no notification is sent when offer creation fails. 

For the current scenario, the objective is to verify actual HTTP communication between the two Spring Boot applications. Therefore, a proposal in which the HTTP communication itself is replaced with a mock or stub does not verify the same integration boundary.

With the assistance of the AI agent, propose a structure for the integration test and the necessary test scenarios.

At a minimum, consider:

•	successful offer creation → Notification Service receives exactly one notification with the expected data; 

•	failed offer creation → Notification Service receives no notification. 

For the proposed solution, determine:

•	whether it actually exercises the selected integration boundary; 

•	what successful execution of the test proves; 

•	what it does not prove; 

•	what additional test configuration or infrastructure would be required for its actual automated execution. 

Generation and implementation of the integration test are not required. The objective is to design a test that actually verifies the specified interaction and to understand why its implementation is more complex than unit and controller tests.

### Final Verification

After completing the exercise, verify that:

•	context relevant to the current task was selected for the different stages; 

•	the contract between the main application and Notification Service was defined before implementation and preserved during code generation; 

•	the scope and affected components were defined in advance, and the applied changes do not introduce unnecessary functionality or infrastructure; 

•	actual HTTP communication between the applications was verified against the defined contract; 

•	the contract between the components is distinguished from the integration test boundary;

•	the proposed integration test actually verifies the defined boundary, and it can be explained what the test proves and what it does not prove.
