---
title: Tasks
sidebar:
  order: 100
  label: Tasks
taskPage: true
---
## Independent tasks

The following tasks are for preparation and self-assessment for the test on Labs 1–2. Implement a "Student Report" console application.

### Task 1. Model and input data

Create a `data class Student` with a name, a student ID as a `String`, and a list of integer grades. Valid grades range from 2 to 6 inclusive.

Prepare the students "Anna" with grades `[6, 5, 6]`, "Boris" with `[2, 3]`, and "Vera" with an empty list. Use different fictional student IDs.

### Task 2. Calculations and reporting

Write a function that calculates the average grade and returns `null` when there are no grades. Put the check for invalid grades in a separate function. If an invalid grade is present, report the error and do not calculate an average for that student.

Print the name, student ID, and average grade to two decimal places, or "No grades". The expected averages are 5.67 for Anna and 2.50 for Boris.

### Task 3. Filtering and searching

Print the students with an average grade of at least 5.00, sorted by name. Add a search by student ID that returns `Student?`. For an ID that does not exist, display "Student not found".

With the initial data, only Anna should appear in the high-achievers list. Students with no grades or invalid grades are excluded from filtering.

### Verification and submission

Submit the code and sample output. Test an empty student list, a missing ID, and grades `[1, 7]`. Explain why the student ID is text and how you handle a missing average.
