---
layout: default
title: Laboratory exercise 8
parent: Training Practice 2
has_children: true
nav_order: 8
---

# Laboratory exercise 8

## Combining Design Patterns

#### Task 1

Develop an application for creating business documents.

Each document must contain a title, author, and main text content. Additionally, a document number, date, organization, and access level may be specified when the document is created.

After creation, the following features may be added to the document independently:

- timestamp;
- watermark;
- version information;
- confidentiality marking.

Several of these features may be applied to the same document at the same time.

Creating documents with different combinations of optional initial data should not require multiple constructors. Adding a new type of additional processing should not require changes to the existing classes.

Demonstrate the application using several documents with different initial data and different combinations of additional features.

#### Task 2

Develop an application for managing devices in a smart home.

The system works with lighting, a climate control system, and an electronic lock. The devices are provided by two different manufacturers, with each manufacturer providing its own implementation of all three device types.

The client code should not depend on the concrete device classes provided by a particular manufacturer.

The devices can be controlled through:

- a wall-mounted control panel;
- a mobile application;
- a voice assistant.

The control mechanism should be able to vary independently of the specific type of device and its manufacturer.

The system should allow a new manufacturer, a new device type, or a new control mechanism to be added with minimal changes to the existing code.

Demonstrate the use of devices from both manufacturers with different control mechanisms.

