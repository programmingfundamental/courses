---
title: "Lab 6"
sidebar:
  order: 6
---

# Laboratory Exercise 6

## Decomposition and Critical Evaluation of a Complex Software Problem

### Objective

The objective of this exercise is to examine the use of task decomposition when working with an AI assistant and the critical evaluation of the results obtained through the successive implementation of individual parts.

A large software task will be divided into smaller, verifiable parts with clearly defined dependencies and scope. After implementation, critique/review and gap analysis will be used to evaluate the resulting solution and identify partially implemented or missing business rules.

The practical task is to extend the service request lifecycle with offer acceptance and rejection, scheduling, execution, cancellation, and additional pricing rules.

### Decomposition and Critical Evaluation of a Complex Problem

For a larger software task, a single prompt requesting the analysis and implementation of all requirements at once makes both solution generation and subsequent verification more difficult.

Task decomposition divides the task into smaller, logically related, and verifiable parts. When defining them, dependencies, the overall context, and an appropriate implementation order should be considered. Each part should have a clear scope and a verifiable result.

After implementation, two different analysis approaches can be applied:

•	critique/review evaluates the quality of the resulting solution according to selected criteria – for example, distribution of responsibilities, dependencies, duplicated logic, domain rules, and unnecessary complexity; 

•	gap analysis compares the business requirements with the current implementation and identifies which rules are fully implemented, partially implemented, or missing. 

In both cases, the AI assistant's result represents a proposed analysis that must be verified by the developer.

The exercise follows this process:

**decomposition → implementation and verification of individual parts → review → gap analysis → targeted corrections → verification**

### Independent Selection of Project Context

From this exercise onward, the necessary project context is selected independently according to the current task.

Before each prompt, determine what information from the existing project is necessary for the specific analysis, implementation, or verification task and add the relevant files.

It is not necessary to automatically provide the entire project. Context selection should consider the scope of the current step, the affected components, and dependencies on already implemented functionality.

If the provided context proves insufficient, first determine what information is missing and then add the necessary files.

