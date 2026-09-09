---
layout: default
title: Laboratory exercise 9
parent: Training Practice 2
has_children: true
nav_order: 9
---

# Laboratory exercise 9

## Structural Design Patterns – Composite, Adapter, and Flyweight

#### Task 1

Develop an application for managing work tasks in an organization.

Work within the organization is represented by individual tasks, projects, and subprojects.

Each individual task is characterized by a name, description, and estimated number of hours required for completion. A project may contain individual tasks and subprojects.

For each element in the structure, it should be possible to:

- display information;
- calculate the total number of hours required for completion;
- determine the number of individual tasks it contains.

The system should also incorporate tasks received from an existing external system. The external system represents a task using an ExternalTask class, which provides a description and a duration in minutes through an interface different from the one used by the application. The ExternalTask class cannot be modified.

External tasks should be able to participate in projects in the same way as the other tasks.

Demonstrate the application using a structure containing individual tasks, external tasks, projects, and subprojects.

#### Task 2

Develop an application for a theater seat reservation system.

The theater organizes multiple performances in different halls. Each seat for a particular performance has individual information:

- row number;
- seat number;
- status – available or reserved;
- customer name when the seat is reserved.

However, a large number of seats share the same characteristics depending on their category. For each seat category, the following information is stored:

- category name;
- base price;
- seat type;
- description of the view of the stage;
- additional features.

A single hall may contain thousands of seats, while the number of categories is small – for example, standard, balcony, and VIP.

The system should:

- allow a large number of seats to be created;
- avoid storing the same category information multiple times;
- allow a seat to be reserved and released;
- calculate the price of a particular seat;
- display information about the available seats for a given performance.

Categories should be created and provided centrally so that the same shared object is used for a particular category.

Demonstrate that multiple different seats share the same category information.

