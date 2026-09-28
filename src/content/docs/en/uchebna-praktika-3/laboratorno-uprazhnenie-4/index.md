---
title: Лабораторно упражнение 4
sidebar:
  order: 4
---

# Laboratory Exercise 4

## Iterative Refinement of a Solution When Working with AI

### Objective

The objective of this exercise is to examine the iterative refinement of a software solution when working with an AI assistant.

Through a sequence of prompts within the same Agent session, the exercise will examine how an initial proposal can be analyzed, its scope restricted, and the solution progressively refined based on accumulated context and the current state of the project.

The exercise distinguishes between conversational context and context provided by project files. At each iteration, it will be verified whether requirements that remain valid have been preserved, changed decisions have been updated, and no unsolicited changes have been introduced.


### Iterative Refinement

When developing software with AI, the initial prompt does not need to contain the final description of the entire solution. The generated result can be analyzed, and the task can be progressively refined based on identified problems, missing information, or changes in scope.

Instead of reformulating the entire task from the beginning for every refinement, a follow-up prompt can be used within the same conversation. It uses the accumulated context and adds a clarification, constraint, correction, or next step.

In successive interactions, a distinction should be made between:

•	conversational context – previous instructions, analyses, results, and decisions available within the current session; 

•	project context – relevant information from the existing project. 

The presence of information in the conversational context does not guarantee that the AI will interpret or apply it correctly. At each iteration, it should be verified whether:

•	the new refinement has been applied to the correct part of the solution; 

•	previous requirements that remain valid have been preserved; 

•	cancelled or changed decisions are no longer being used; 

•	no functionality outside the current scope has been added. 

A follow-up prompt should not mechanically repeat the entire previous prompt. It should clearly specify what is being changed, clarified, or corrected relative to the already established context.
