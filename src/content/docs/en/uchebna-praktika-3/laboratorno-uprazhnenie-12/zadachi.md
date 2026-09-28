---
title: Tasks
taskPage: true
sidebar:
  label: Tasks
  order: 100
---

### Practical Task

Development of the application from Laboratory Exercise 11 continues using the existing project. The sequence of the individual tasks and the interaction with the AI assistant are determined independently according to the state of the project.

When a problem occurs, the necessary project and diagnostic information should be used to identify its cause before making a correction. Significant changes to the initially selected approach should be recorded in AI_WORKLOG.md.

### User Interface

Develop a simple React user interface for the application using the existing REST API.

The interface should support the main business process, including:

•	viewing purchase information; 

•	viewing submitted complaints; 

•	submitting a new complaint for a purchased product; 

•	approving or rejecting a complaint; 

•	specifying the resolution method for an approved complaint. 

A complex user interface design is not required. The main objective is to provide working interaction between the frontend and backend and to allow the main system operations to be performed.

When an operation fails, the user should receive appropriate information about the problem that occurred.
Independently determine the required project context for developing the client. The generated frontend code should be verified against the existing API contract and the actual behavior of the backend application.

The backend application should not be changed solely to make the generated frontend code work. When a change appears to be necessary, first determine which component the problem belongs to and what correction is required.

Significant decisions or changes in the approach used should be documented in AI_WORKLOG.md.

### Automated Testing

Develop automated tests for the significant business logic of the application.

Select successful and unsuccessful scenarios that verify the main business rules. Appropriate scenarios may include:

•	successful submission of a complaint; 

•	an attempt to submit a complaint after the permitted deadline has expired; 

•	an attempt to submit a second active complaint for the same purchased product; 

•	permitted and prohibited state transitions; 

•	successful specification of a resolution method for an approved complaint. 

It is not necessary to cover every possible scenario with a test. Tests should be selected to verify significant business rules rather than only trivial operations.

Implement **at least one integration test** that verifies a main business scenario through the application's REST API.

The type and scope of the tests, as well as the required project context, should be determined according to the behavior that needs to be verified.

When AI is used to generate tests, verify whether the expected results follow from the business requirements, whether the test data allow the corresponding scenario to be executed, and whether the assertions verify significant behavior.

The mocks used should not replace the behavior that the test itself is intended to verify.

A failing test should not automatically be treated as evidence of a defect in the application. Its cause should be identified by examining the test, the expected business behavior, the current implementation, and the available diagnostic information.

Significant cases that lead to a change in the implementation or in the strategy for working with AI should be documented in AI_WORKLOG.md.

### Final Verification

After completing the development, perform a final verification of the solution.

Determine whether:

•	the implemented solution satisfies the given business requirements; 

•	the main business process can be performed through the React client; 

•	the frontend application uses the existing REST API contract; 

•	invalid input data and prohibited business operations are handled as expected; 

•	the automated tests verify selected significant business rules; 

•	at least one integration test verifies a main business scenario through the REST API; 

•	the implemented behavior has been verified independently of the evaluations and explanations provided by the AI. 

The final verification should establish both the compliance of the solution with the given requirements and whether the intended main business process can be successfully performed.

### Preparation for the Presentation in Laboratory Exercise 13

Before completing Laboratory Exercise 12, review AI_WORKLOG.md and, if necessary, supplement the explanations of the key decisions that have already been documented.

No separate report should be created, and a complete record of interactions with the AI is not required.

For the discussion in Laboratory Exercise 13, it should be possible to select specific examples from AI_WORKLOG.md that demonstrate:

•	a case in which the context used was changed or supplemented; 

•	a problem that led to adaptation of the initial approach; 

•	an AI proposal that was accepted, corrected, or rejected, and the reason for the decision; 

•	a way in which an AI proposal or generated solution was independently verified. 

AI_WORKLOG.md is used as the basis for discussing the engineering process during the final presentation.

### Additional Task – Migrating to PostgreSQL

After completing the main tasks, the application may be configured to use PostgreSQL instead of the H2 database.

Independently determine the necessary changes, affected components, required project context, and method for verifying the result.

Before applying them, evaluate the AI's proposals against the actual state of the project and the scope of the task. Changing the database being used should not, by itself, result in changes to the business logic.

After configuration, verify that the main application operations work correctly with PostgreSQL and that data is stored and retrieved successfully.

If a problem occurs, identify its cause before making a correction, adding the necessary diagnostic and project information to the context.

If the task leads to a significant problem, a change in the initial approach, or the correction or rejection of an AI proposal, document the case in AI_WORKLOG.md.
