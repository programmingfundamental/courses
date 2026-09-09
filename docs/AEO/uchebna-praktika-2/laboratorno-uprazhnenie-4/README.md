---
layout: default
title: Laboratory exercise 4
parent: Training Practice 2
has_children: true
nav_order: 4
---

# Laboratory exercise 4

## Creational Design Patterns

#### Task 1

Develop an application that uses a shared configuration.

The configuration contains:

- application name;
- default language;
- maximum number of concurrent users.

Only one object containing the configuration should exist within the application. It should be accessible from different parts of the program.

Demonstrate that retrieving the configuration from different parts of the application returns the same instance.

#### Task 2

Develop an application for creating user profiles on an online platform.

A user profile must contain:

username;
email;
password.

The following information may also be provided:

- phone number;
- address;
- short description;
- profile picture;
- preferred language;
- notification settings.

It should not be possible to create a profile without the required information. The email must be validated and the password must meet a minimum length requirement.

The solution should allow profiles to be created with different combinations of optional data without defining multiple constructors.

Demonstrate the solution using at least three profiles with different combinations of data.

#### Task 3

Develop an application for creating courier delivery requests.

Each request must contain:

- sender;
- recipient;
- shipment type;
- weight;
- delivery method – to an office or to an address.

The following information may also be provided:

- delivery address;
- declared value of the shipment contents;
- cash on delivery amount;
- insurance;
- priority delivery;
- comment for the courier.

When creating a request, the following conditions must be satisfied:

- the weight must be positive;
- for delivery to an address, a delivery address must be provided;
- for delivery to an office, a delivery address is not required;
- the cash on delivery amount, if specified, must be positive;
- the declared value cannot be negative;
- insurance may be requested only for a shipment with a specified declared value.

It should not be possible to create an object that violates any of these conditions.

Demonstrate both successful request creation and attempts to create requests with invalid combinations of data.
