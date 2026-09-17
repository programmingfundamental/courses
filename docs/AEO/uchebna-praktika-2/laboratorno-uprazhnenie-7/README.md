---
layout: default
title: Laboratory exercise 7
parent: Training Practice 2
has_children: true
nav_order: 7
---

# Laboratory exercise 7

## Structural Design Patterns. Adapting Interfaces

#### Task 1

Develop an application for a transportation reservation system.

The system uses a common interface for searching for available travel options. The interface accepts a departure point, destination, and date and returns information about the available travel options.

Initially, the application works with its own component for bus transportation.

Two existing external systems must be integrated:

a railway transportation system that uses different method names and returns its own result object;
a flight system that accepts a separate object containing the search criteria.

The application code should not work directly with the interfaces of the external systems.

Implement a solution that allows bus, railway, and flight options to be searched through the same interface.

#### Task 2

Develop an application for a task management system.

The system uses its own Task model, which contains a title, description, deadline, and priority.

The organization already uses two external task management systems. The first provides tasks as objects containing summary, details, dueDate, and a numeric importance value. The second uses name, text, a string representation of the deadline, and the textual values LOW, NORMAL, and HIGH for priority.

The existing external classes cannot be modified.

The application should allow tasks from both systems to be used as objects of the internal model without making the rest of the application dependent on their concrete classes.

Implement functionality for:

- displaying all tasks;
- filtering tasks by priority;
- finding tasks with approaching deadlines;
- working simultaneously with tasks from the internal model and both external systems.
