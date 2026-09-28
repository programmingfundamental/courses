---
title: "Lab 10"
sidebar:
  order: 10
---

# Laboratory Exercise 10

## Context Management When Working with Multiple Software Components

### Objective

The objective of this exercise is to examine context management when working with an AI assistant on a software task that affects multiple interacting components.

By adding a separate Notification Service to the existing application, the exercise will examine how the required context changes across different stages of the task and how artifacts from different components can be combined without providing unnecessary information.

The contract between the applications will be defined, and actual HTTP communication between them will be implemented and verified. After the implementation, the exercise will examine how the required context changes when moving to a new task – designing an automated integration test that actually verifies the implemented interaction.

### Context Management Across Multiple Components

In a software task that affects several components or applications, the required information may be distributed across different parts of the system. One component may define the business event, another the interface and structure of the exchanged data, while configuration or test artifacts may define how the interaction is performed and verified.

Providing all available artifacts does not guarantee a better result. Context management involves determining the information required for the current task, selecting the relevant artifacts, and changing the context when moving to the next stage.

*Context management does not mean accumulating the maximum amount of information but maintaining sufficient and relevant context for the current task*.

### Boundaries and Contracts Between Components

When two components interact, it is necessary to define what information passes between them, through which interface, and which component is responsible for the individual parts of the behavior.

For HTTP communication, the contract between the applications may include:

•	endpoint and HTTP method; 

•	request and response structure; 

•	permitted values; 

•	expected error behavior. 

When modifying or generating one of the components, this contract must be preserved. Context from one component should not lead to unjustified changes in the other solely to make the generated solution easier to implement.

Different stages of the same task may require different context. For example, determining when a notification should be sent requires context about the business process. Implementing HTTP communication requires context about the interface and exchanged data, while designing an integration test requires information about the interaction and how it should be verified.

### Integration Test Boundary

The contract between components and the integration test boundary serve different purposes.

The contract defines how the components interact during system execution. The integration test boundary defines which parts of that interaction must participate as real components in the specific verification and which dependencies may be replaced.

An integration test should be designed according to the behavior it needs to prove. If the objective is to verify actual HTTP communication between two applications, that communication must actually participate in the test.

Replacing the very interaction that is supposed to be verified with a mock, or stub changes the test boundary. The resulting test may be useful for another purpose, but it does not prove the same interaction.
