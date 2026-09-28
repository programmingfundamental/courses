---
title: Лабораторно упражнение 2
sidebar:
  order: 2
---

# Laboratory Exercise 2

## Analysis and Evolution of the Domain Model

### Objective

The objective of this exercise is to examine how the selection of relevant context and the definition of task scope influence the analysis and evolution of an existing software solution with the assistance of AI.

Through analysis before action, the main domain concepts, their responsibilities, and relationships will first be analyzed without making changes to the code. After evaluating the proposed solution, only the approved changes will be implemented.

The exercise applies the general engineering process for working with AI introduced previously, with an emphasis on selecting relevant context, defining the scope, performing impact analysis, and maintaining control over the changes proposed by the Agent.

### Context When Working with an Existing Software Project

When working on an existing project, the AI assistant can use different type of context. Depending on the task, this may include:

•	functional context – requirements concerning system behavior; 

•	domain context – concepts, rules, and relationships from the problem domain; 

•	technological context – languages, frameworks, libraries, and other technologies being used; 

•	architectural context – application structure, layers, dependencies, and architectural decisions; 

•	context from existing artifacts – code, configuration, APIs, tests, or other parts of the current project. 

Not all available context is equally useful for every task. Relevant context is the information necessary for correctly understanding and performing the specific task. Adding irrelevant information increases the amount of context without necessarily improving the result.

It is also important to define the scope of the task – what should be analyzed or changed and what remains outside the current task.

When a design decision must be made before implementation, the analysis before action strategy can be used: the AI is first asked to provide analysis and a proposal without modifying the code. After evaluation by the developer, only the approved changes are implemented.

Before implementation, an impact analysis is performed to determine which parts of the existing solution may be affected by the planned change. Based on this analysis, the necessary context for implementation is selected.

The minimal change principle is applied – only the changes required for the current task are made, without unrelated restructuring or additional functionality.
