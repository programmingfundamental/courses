---
layout: default
title: Laboratory exercise 11
parent: Training Practice 2
has_children: true
nav_order: 11
---

# Laboratory exercise 11

## Behavioral Design Patterns. Notification and Sequential Processing

#### Task 1

Develop an application for tracking the status of shipments in a courier system.

Each shipment is characterized by a tracking number, sender, recipient, and current location.

Different interested parties may be registered for a particular shipment – the sender, the recipient, and an employee of the courier company.

Whenever the location of the shipment changes, all registered interested parties must be notified.

Different notification recipients may react in different ways – for example, by displaying information in the console, recording the shipment's movement history, or updating their own information about the expected delivery.

It should be possible to register and remove interested parties while the program is running.

Create several shipments, register different notification recipients, and demonstrate several location changes.

#### Task 2

Develop an application for processing customer requests for technical support.

Each request contains:

- a number;
- a short description;
- a category – SOFTWARE, HARDWARE, NETWORK;
- a priority – NORMAL, URGENT;
- information indicating whether an on-site visit is required.

Requests are handled by different specialists.

Urgent requests are handled by an emergency support specialist regardless of their category.

A normal-priority request for a software issue that does not require an on-site visit is handled by a remote support specialist.

A normal-priority hardware request is handled by a hardware specialist.

A normal-priority network request is handled by a network specialist.

A request that cannot be handled by any of the specialized employees is handled by a general technical support employee.

Implement a solution that allows a new type of handler to be added without modifying the existing handlers.

Demonstrate the solution using requests with different combinations of category, priority, and the need for an on-site visit, showing which handler processes each request.

