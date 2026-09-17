---
layout: default
title: Laboratory exercise 5
parent: Training Practice 2
has_children: true
nav_order: 5
---

# Laboratory exercise 5

## Creational Design Patterns. Creating Families of Related Objects

#### Task 1

Develop an application for creating a user interface that can operate in two modes: desktop and mobile.

Each mode uses its own family of graphical components:

- button;
- input field;
- dialog box.

Components belonging to the same mode should have a consistent appearance and behavior.

The application should allow the interface mode to be selected at startup, after which all components used by the application should be created from the corresponding family.

Adding a new interface mode should not require changes to the code that uses buttons, input fields, and dialog boxes.

Demonstrate the application in both modes.

#### Task 2

Develop an application for a travel planning system.

The system offers two types of trips – city and mountain trips.

For each type, a compatible set of the following should be created:

- transportation;
- accommodation;
- main activity.

For example, a city trip may use public transportation, a hotel, and a cultural tour, while a mountain trip may use an off-road vehicle, a mountain lodge, and a hiking trip.

The application should work with common abstractions for transportation, accommodation, and activities without depending directly on their concrete implementations.

Adding a new type of trip should be possible by adding a new family of related objects.

Demonstrate the application by creating and displaying information for at least two different types of trips.
