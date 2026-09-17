---
layout: default
title: Laboratory exercise 12
parent: Training Practice 2
has_children: true
nav_order: 12
---

# Laboratory exercise 12

## Combining Behavioral and Structural Design Patterns

#### Task 1

Develop an application for managing the lighting in an office building.

The building contains individual lighting fixtures. The lighting fixtures are organized into rooms, and several rooms may form a zone.

The operations for turning the lights on and off should be executable on an individual lighting fixture, a room, or an entire zone.

The system has a control panel through which the user can specify an operation to be executed without the panel itself depending on the specific object on which the operation will be performed.

The same operation should be executable on an individual lighting fixture, a room, or a zone.

Demonstrate turning individual lighting fixtures and entire groups on and off through the control panel.

#### Task 2

Develop an application for managing a user subscription to an online service.

Each subscription is characterized by a number, user, and base monthly price.

A subscription may be active, suspended, or terminated.

When a subscription is active, the service may be used, and the subscription may be suspended or terminated. A suspended subscription does not allow the service to be used, but it may be reactivated or terminated. A terminated subscription cannot be used or reactivated.

Additional services may be added to an active subscription:

- additional cloud storage;
- premium support;
- access for an additional user.

Several services may be added to the same subscription at the same time, with each service increasing its monthly price.

The system should allow the final monthly price to be calculated and the permitted operations to be performed depending on the current state of the subscription.

Demonstrate the application using a subscription with several additional services and changes to its state.


