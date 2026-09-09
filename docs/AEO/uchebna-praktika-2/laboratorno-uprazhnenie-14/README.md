---
layout: default
title: Laboratory exercise 14
parent: Training Practice 2
has_children: true
nav_order: 14
---

# Laboratory exercise 14

## Comprehensive Application and Combination of Design Patterns

#### Task

Develop an application for organizing professional events.

The system organizes different types of events – seminars, practical training sessions, and conferences.

Each event is characterized by a name, date, venue, maximum number of participants, and participation fee.

Participants can register for an event. A registration contains a participant, the selected event, and its current state.

A registration may be requested, confirmed, or canceled.

A requested registration may be confirmed or canceled. A confirmed registration may be canceled up to a specified time before the start of the event. A canceled registration cannot be reactivated.

The participation fee may be calculated differently depending on the participant – standard pricing, student pricing, or group pricing.

Additional services such as lunch, printed materials, and a parking space may be added to the registration. Each additional service increases the final price, and several services may be added to the same registration.

When a registration is confirmed or canceled, interested parties must be notified of the change.

The organizer should be able to perform operations on registrations to confirm or cancel them, and the solution should allow new operations to be added easily.

Demonstrate the application by creating several events and participants, creating registrations with different pricing methods and different additional services, and changing the state of the registrations.
