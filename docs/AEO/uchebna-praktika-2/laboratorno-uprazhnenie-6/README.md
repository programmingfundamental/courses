---
layout: default
title: Laboratory exercise 6
parent: Training Practice 2
has_children: true
nav_order: 6
---

# Laboratory exercise 6

## Applying SOLID Principles and Creational Design Patterns

#### Task 1

Develop an application for organizing events.

For each event, the name, date, organizer, and venue are required. Additionally, a description, maximum number of participants, participation fee, and participant requirements may be specified.

The system supports both in-person and online events. For an in-person event, an address and a room are specified, while for an online event, a platform and an access link are provided.

Users can register for an event. If a maximum number of participants has been specified, registrations beyond this limit must not be allowed.

The application should allow:

- creating events;
- registering a participant;
- canceling a registration;
- displaying information about an event and its registered participants.

The classes should be organized appropriately so that differences between event types do not lead to duplication of common logic.

#### Task 2


Develop an application for registering and processing technical support requests.

Each request contains a number, problem description, customer name, and priority.

Requests may be of two types – software issues and hardware issues.

The system should allow:

- registering a new request;
- displaying all requests;
- finding a request by number;
- completing a request;
- displaying all unresolved requests.

The system is used by several different organizations.

Each organization handles requests differently. When a request is created, in addition to the request itself, the following must be determined:

- the employee responsible for handling the request;
- the method used to notify the customer;
- the rules for determining the expected processing time.

One organization uses internal employees and email notifications, while another uses external specialists and SMS notifications.

The application should allow a new organization with its own set of rules to be added without modifying the processing logic for existing organizations.

