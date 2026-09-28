---
title: Лабораторно упражнение 5
sidebar:
  order: 5
---

# Laboratory Exercise 5

## Missing Context and Assumptions When Working with AI

### Objective

The objective of this exercise is to examine the behavior of an AI assistant when working with incomplete business requirements and to develop the ability to distinguish between information that has actually been provided and assumptions made by the model.

The exercise will analyze how missing context affects the proposed solution, which assumptions may be acceptable during preliminary analysis, and which information must be clarified before implementation.

After the necessary business context has been provided, the next part of the application will be developed – the offer associated with a service request and basic price calculation.

### Missing Context and Assumptions

The previous exercises distinguished between **missing context**, **assumption**, **unsupported claim**, and **hallucination**.
When working with incomplete context, an assumption is not necessarily incorrect. It may be a reasonable possible interpretation, but it should not be treated as a confirmed business requirement.

When missing information affects the domain model, business rules, public API, or another significant aspect of the solution, it must be clarified before implementation.

The analysis should distinguish between:

•	what is known from the provided context; 

•	what information is missing; 

•	what the AI assumes; 

•	which assumptions require clarification before implementation; 

•	which questions can be deferred. 
