---
layout: default
title: Laboratory exercise 10
parent: Training Practice 2
has_children: true
nav_order: 10
---

# Laboratory exercise 10

## Applying and Combining Structural Design Patterns

#### Task 1

Develop an application for creating and managing drones designed for different purposes.

The system supports drones for aerial photography, delivery, and terrain surveying.

Each drone must have a model, maximum flight altitude, and battery capacity. Depending on its purpose and specific configuration, a drone may additionally be equipped with:

- a camera;
- GPS;
- an obstacle avoidance system;
- additional sensors;
- a cargo module.

When creating a drone, different combinations of optional components should be configurable without requiring multiple constructors.

Drones can be controlled in different ways:

- by remote control;
- through a mobile application;
- through an autonomous control module.

The control mechanism should be able to vary independently of the drone's purpose and configuration. Adding a new type of drone should not require changes to the existing control mechanisms, and adding a new control mechanism should not require changes to the existing drone types.

Create drones for different purposes and with different configurations, and demonstrate controlling them in different ways.

#### Task 2

Develop an application for managing hotel reservations.

The hotel has rooms of different categories – standard, family, and luxury.

Each individual room is characterized by a room number, floor, and category.

Rooms belonging to the same category share common characteristics:

- maximum number of guests;
- base price per day;
- bed type;
- description;
- standard amenities.

The hotel has a large number of rooms, while the number of room categories is limited.

Each reservation contains:

- customer;
- room;
- start date;
- end date.

The following additional services may be added to a reservation:

- breakfast;
- parking space;
- extra bed;
- access to the spa.

Each additional service has a fixed price per day. One or more services may be added to a reservation in any combination.

The final price of a reservation is determined by the duration of the stay, the base daily price of the room category, and the prices of the selected additional services.

The system should allow:

- creating rooms of different categories;
- reusing the shared information for a particular category across multiple rooms;
- creating a reservation;
- adding different additional services to a reservation;
- calculating the final price;
- displaying information about the reservation and the selected services.

Demonstrate the application by creating several rooms, with at least two of them belonging to the same category. Create reservations with different duration and different combinations of additional services.


