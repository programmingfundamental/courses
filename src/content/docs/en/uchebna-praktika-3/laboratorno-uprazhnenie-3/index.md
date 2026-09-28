---
title: Лабораторно упражнение 3
sidebar:
  order: 3
---

# Laboratory Exercise 3

## Constraints and Critical Evaluation in AI-Assisted Software Development

### Objective

The objective of this exercise is to examine how constraints and clearly defined task boundaries influence the analysis and modification of an existing software solution with the assistance of AI.

Validation and error-handling requirements will be used to examine different types of constraints, conflicting requirements, and alternative solutions involving different trade-offs.

The code is modified only after analyzing the existing implementation, critically evaluating the AI's proposals, and selecting a specific approach.

### Constraints When Formulating a Task for AI

In addition to describing the desired result, a task given to an AI can contain constraints that define the conditions and boundaries the solution must satisfy. In a software task, such constraints may include business rules, technological requirements, API contracts, or architectural decisions.

Some constraints can be formulated as negative constraints – actions or changes that must not be performed. For example, a requirement to preserve the existing REST API limits the permissible changes even though the internal implementation may be modified.

Requirements may be incomplete, ambiguous, or mutually contradictory. In such cases, accepting one interpretation without verification may lead to a solution that formally follows some of the instructions but is inconsistent with the remaining context. Therefore, AI proposals and interpretations must be critically evaluated.

When analyzing multiple requirements, a structured output can be specified in advance – a consistent response structure for each analyzed element. This makes it easier to identify omitted or incorrectly interpreted requirements.

In the context of this exercise, input validation, verification, and software validation should be distinguished. Input validation checks whether input data satisfies the specified rules. Verification checks whether the implemented solution conforms to the specified requirements and expected technical behavior. Software validation evaluates whether the developed solution satisfies the actual need and purpose for which it was created.
