---
layout: default
title: Tasks
parent: Laboratory excercise 9
grand_parent: Internet Technologies
nav_order: 5
---

# Tasks

In the application developed so far, replace the storage in a collection with a PostgreSQL database.

1\. Create entity classes with the following fields for:

Task:
- unique task identifier;
- summary;
- description;
- deadline for completion;
- collection of reports for the task.

Report:
- unique report identifier;
- content;
- hours worked;
- date and time of report creation;
- date and time of last report update;
- the task to which the report belongs.

2\. Define Model Mapper.

3\. Create repository layers for Task and Report.

4\. Create a service layer with CRUD operations for Task.

5\. Update controllers.
