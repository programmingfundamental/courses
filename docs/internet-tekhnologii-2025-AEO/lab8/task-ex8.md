---
layout: default
title: Tasks
parent: Laboratory excercise 8
grand_parent: Internet Technologies
nav_order: 4
---

# Tasks

1\.     Edit the application by adding a DTO to filter input data for reports. The class should provide validation for the following fields:       
- Time must not be null and must start counting after midnight (LocalTime time);
- Start date must be in the future and not null (LocalDateTime from);
- End date must be in the future and after the start date and not null (LocalDateTime to).

2\.      Create a resource not found exception class ResourceNotFoundException.java that extends Exception. Implement the exception throwing in the controller layer, simulating a non-existent resource being retrieved. Provide a GlobalExceptionHandler to describe the response to the client when this exception occurs.

3\.      Add validation to the following fields when adding or updating:  

 Task:      
- Title cannot be empty and must be between 10 and 255 characters;
- Description must contain at least 10 characters and cannot be longer than 2500 characters;
- Deadline cannot be null and must be in the future.

Report:
- Content cannot be empty and must contain between 10 and 2500 characters;
- Working hours cannot be null and must start after midnight (00:00).

In the GlobalExceptionHandler, provide an appropriate response to the client when data is submitted incorrectly, causing a MethodArgumentNotValid exception.
