---
title: Tasks
taskPage: true
sidebar:
  label: Tasks
  order: 100
---

### Business Case – Complaint Management System

Develop a web application for managing complaints regarding purchased products.

The system contains information about completed purchases and the products included in them. A customer can submit a complaint about a product from an existing purchase by specifying a reason and a description of the identified problem.

A complaint can be submitted only within a specified period after the purchase date. A newly submitted complaint initially has status SUBMITTED.

After review, the complaint can be:

•	approved – APPROVED; 

•	rejected – REJECTED. 

When a complaint is approved, a method of resolving the complaint must be specified – replacement of the product or refund of the amount paid.

For the same purchase and product, a new complaint must not be allowed if an active complaint already exists for it.
A completed complaint cannot be modified.

The system must allow viewing completed purchases and submitted complaints.

### Analysis and Design

Before starting the implementation, analyze the given business case and define a strategy for working with the AI assistant.

Independently determine:

•	how the task will be divided into separate stages; 

•	the required project context for the individual tasks; 

•	the scope of individual changes and the affected components; 

•	how the AI's proposals will be evaluated and verified; 

•	how the implemented behavior will be independently verified. 

During the analysis and design process, clarify the domain model, business rules and validations, states and permitted transitions between them, the required REST operations, and the responsibilities of the main components.

The sequence in which these tasks are addressed, the required context, and the approach to interacting with the AI are not predefined.

Evaluate the resulting proposals against the given business case before including them in the project. Do not automatically add functionality, roles, states, dependencies, or infrastructure that do not follow from the requirements.

When the provided information is insufficient for an unambiguous decision, distinguish between information that actually follows from the requirements, missing information, and assumptions that have been made.

If a result is inappropriate or incomplete, the strategy for working with the AI may be changed according to the identified cause and the current state of the project.

### Documenting the Engineering Process

Create the following file in the root directory of the project: AI_WORKLOG.md

The file should be updated during development and should contain brief documentation of key interactions with the AI assistant and the engineering decisions made.

It is not necessary to describe every interaction with the AI, and a complete transcript of the conversations is not required. Document only cases that are significant to the development of the project, for example:

•	an important choice or change in the context used; 

•	an AI proposal that was accepted, corrected, or rejected; 

•	an identified assumption or missing information; 

•	an inappropriate or incorrect proposal; 

•	a problem that led to a change in the initial approach; 

•	a choice between possible solutions; 

•	a significant verification of a generated solution. 

At the beginning of the file, briefly describe the initially selected strategy:

```text
# AI Worklog

## Initial Strategy
Brief description of:
- how the task is divided into separate parts;
- how the necessary context is selected;
- how AI proposals are evaluated;
- how the implemented behavior is verified.
For selected key interactions, the following structure can be used:

## [Short Task Name]

### Objective
What problem needed to be solved?

### Provided Context
What information or which files were used and why?

### Main Prompt
The main prompt or its significant part.

### Result and Decision
What did the AI propose?
What was accepted, corrected, or rejected, and why?

### Verification
How was the accepted solution verified?
When a problem has led to a significant change in strategy, add the following section to the corresponding entry:

### Problem and Change in Approach
What problem was identified?
How was the cause determined?
What was changed in the way of working?
What was the result after the change?
This section should be used only when a significant change in the approach has actually occurred.
```

**AI_WORKLOG.md is not evaluated according to the number of recorded interactions or prompts used**. Its purpose is to show the key engineering decisions and the evolution of the strategy for working with AI.

### Implementation

Based on the completed analysis and design, develop a Spring Boot application implementing the main business process.
During development, independently determine which tasks should use the AI assistant, what project context is required, and how the resulting proposals and generated solutions should be verified.

The implemented behavior must be verified independently of the evaluation or explanation provided by the AI. When necessary, the working strategy should be changed according to the obtained results, and significant changes and their reasons should be recorded in AI_WORKLOG.md.

The application must implement at least:

•	a data model and persistence layer; 

•	creation and viewing of purchase information; 

•	submission and viewing of complaints for purchased products; 

•	verification of the complaint submission deadline; 

•	prevention of more than one active complaint for the same purchased product; 

•	approval and rejection of a complaint; 

•	specification of the resolution method for an approved complaint; 

•	control of permitted complaint state transitions; 

•	input data validation; 

•	appropriate handling of invalid business operations; 

•	a REST API for the implemented functionality. 

H2 may be used for this part of the task.

A user interface is not required as part of Laboratory Exercise 11.

### Verification of the Developed Solution

During development and after implementing the main business process, independently determine appropriate methods for verifying the developed solution.

The verification should make it possible to determine whether the implemented behavior corresponds to the given business case, including whether:

•	a complaint can be created only for a product from an existing purchase; 

•	the complaint submission deadline is applied correctly; 

•	no more than one active complaint can exist for the same purchased product; 

•	permitted state transitions are controlled; 

•	a resolution method is specified when a complaint is approved; 

•	a completed complaint cannot be modified; 

•	invalid input data and invalid business operations are handled appropriately; 

•	the REST API corresponds to the implemented model and business process. 

The method used to perform the verification is not predefined. Selecting appropriate verification methods is part of the independent development strategy.

Significant verifications and results that have influenced an engineering decision or the strategy for working with AI should be recorded in AI_WORKLOG.md.
