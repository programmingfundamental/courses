---
layout: default
title: Tasks
parent: Laboratory excercise 10
grand_parent: Internet Technologies
nav_order: 2
---

#Tasks

1. Create a service layer for Report with CRUD functionalities
Implement a service class that handles the creation, editing, deletion, and retrieval of reports. Each method should validate the input and handle the association with a given task (Task) accordingly. Make sure that the created report is associated with a specific task by its identifier.

The complete logic of the ReportService should include:

- create a report for a given task;
- retrieve a report by ID;
- retrieve all reports for a given task;
- retrieve reports by working hours in a given interval (using FilterReportDto);
- retrieve reports with the maximum number of working hours for a given task;
- edit a report by ID;
- delete a report by ID.

2. Update the ReportController with the methods from the service layer (ReportService).
