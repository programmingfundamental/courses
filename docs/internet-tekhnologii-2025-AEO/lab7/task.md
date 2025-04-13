---
layout: default
title: Task
parent: Laboratory excercise 7
grand_parent: Internet Technologies
nav_order: 6
---


# Task 1

Develop a Spring Boot application for managing tasks and their associated reports (will be enhanced in future exercises). The application should provide a REST API for creating, retrieving, updating, and deleting tasks and reports. Each task can have multiple reports associated with it.

The following information is stored for each task:    
- task id (id);
- title (summary);
- description (description);
- deadline (deadline);
- list of reports related to this task (reports).

Each report should store:
- report id (id);
- report content (content);
- time worked in hours (workTime);
- creation date (dateCreated);
- update date (dateUpdated);
- the task to which the report belongs (task).

Create functionalities:    

For a task:
- view all added tasks;
- add a task;
- view a given task by a given number;
- update a task by a given number and current data;
- delete a task by a given number.

For a report:
- add a report to a given task;
- view all reports to a given task;
- view a report to a given task;
- update a report to a given task;
- delete a report to a given task.
- display reports on a given task with the number of hours worked in a given interval;
- display the report on a given task with the largest number of hours worked;
- calculate the hours worked to complete a task by the given number.

Use DTO objects (TaskRequestDto, TaskResponseDto, ReportRequestDto, ReportResponseDto) to transfer data between clients and the server. The RequestDto classes should be used to receive input data from the client, and the ResponseDto classes should be used to return responses with all relevant fields, including the associated tasks and reports.
