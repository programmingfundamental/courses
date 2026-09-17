---
layout: default
title: Laboratory exercise 2
parent: Training Practice 2
has_children: true
nav_order: 2
---

# Laboratory exercise 2

## Relationships Between Classes. Association, Aggregation, and Composition

#### Task 1

Develop an application for managing a car rental company.

The company owns cars. For each car, the registration number, make, model, and current mileage are stored.

Customers can rent cars. A customer may have multiple rentals during different periods, and a car may be rented by different customers sequentially over time.

For each rental, the start date, end date, and price are stored.

A service history is maintained for each car. It contains records of performed maintenance, with each record storing the date, description, and the car's mileage at the time of servicing.

Maintenance is performed by service centers that exist independently of the car rental company. A service center may service cars belonging to different companies.

Implement the required classes and the relationships between them. The application should allow:

- adding cars and customers;
- creating and completing a rental;
- adding a record to a car's service history;
- displaying the rental history of a selected customer;
- displaying the service history of a selected car.

During the implementation, determine which objects can exist independently and which represent a specific relationship or form part of another object.

#### Task 2

Develop an application for managing the operations of a company.

The company is organized into departments. Each department has a name and employees. Each employee has an employee ID, name, and job title.

An employee may be transferred from one department to another. When a department is removed, its employees are not removed from the system and may subsequently be assigned to another department.

The company carries out projects. Each project has a name, description, and deadline. Employees from different departments may participate in the same project, and an employee may participate in several projects at the same time.

For an employee's participation in a project, the project role and the number of planned hours are stored. One of the participating employees must be designated as the project manager.

Implement the required classes and the relationships between them. The application should allow:

- creating and removing departments;
- assigning and transferring employees;
- creating projects;
- adding and removing project participants;
- designating a project manager;
- displaying employees by department;
- displaying participants by project.

The implementation must preserve employee information independently of changes to the company's organizational structure.

#### Task 3

Develop an application for organizing a conference.

A conference has a name, start date, and end date, and includes thematic sessions. Each session has a title, start time, and duration.

One or more presentations are held within a session. Each presentation has a title and a short description and may be delivered by one or more speakers.

A speaker may participate in different presentations and in different conferences.

Each session takes place in a room. Rooms exist independently of the conference program and may be used by different sessions at different times.

Participants register for the conference. Each participant may select the sessions they wish to attend. For each session registration, the registration time is stored.

Implement the required classes and the relationships between them. The application should allow:

- adding sessions to a conference;
- adding presentations to a session;
- assigning speakers and a room;
- registering participants;
- registering a participant for a session;
- displaying the conference program;
- displaying the sessions selected by a particular participant.

During the implementation, determine which elements belong to the structure of a particular conference and which objects should be able to exist and be reused independently of it.


