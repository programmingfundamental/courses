---
layout: default
title: Laboratory exercise 13
parent: Training Practice 2
has_children: true
nav_order: 13
---

# Laboratory exercise 13

## Combining Behavioral Design Patterns

#### Task 1

Develop an application for managing company assets.

The system supports different types of assets – computers, printers, and company vehicles.

Each asset has common information such as an inventory number, acquisition date, and initial value, as well as specific characteristics depending on its type.

Different operations should be applicable to the assets:

- calculating the current value;
- generating maintenance information;
- determining the insurance value.

The asset classes should not need to be modified when a new operation is added.

The method used to calculate the current value may vary – straight-line depreciation, accelerated depreciation, or no depreciation. The selected method should be changeable independently of the asset type.

Create different assets and demonstrate different operations and methods for calculating their current value.

#### Task 2

Develop an application for managing shared resources in an office.

The system supports:

- meeting rooms;
- projectors;
- company vehicles.

Employees can submit requests to use these resources for a specified period.

Individual resources and employees should not communicate directly with each other. All requests are coordinated by a central object that checks availability and approves or rejects the use of a resource.

When the status of a request changes – approved, rejected, or canceled – interested employees must be notified.

The system should allow a new resource type and a new type of notification recipient to be added without modifying the existing communication logic.

Create several resources, employees, and requests, and demonstrate the coordination and notification mechanisms.
Да се създадат няколко ресурса, служители и заявки и да се демонстрират координацията и уведомяването.


