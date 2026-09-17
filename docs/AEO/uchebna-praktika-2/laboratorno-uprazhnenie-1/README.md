---
layout: default
title: Laboratory exercise 1
parent: Training Practice 2
has_children: true
nav_order: 1
---

# Laboratory exercise 1

## Input/Output Operations. File and Collection Processing

### Task 1

Develop a program for processing a system log file.

Each line in the file has the following format:

```
timestamp;user;action
```

For example:

```
2026-03-15T10:15:20;ivan;LOGIN
2026-03-15T10:17:03;ivan;VIEW
2026-03-15T10:18:45;maria;LOGIN
2026-03-15T10:25:12;ivan;LOGOUT
```

The program should:

- determine the number of actions performed by each user;
- determine the user with the highest number of actions;
- display all distinct action types;
- display the users who have logged into the system at least once but have not logged out;
- write the results to an output file.

If an invalid line is encountered, it should be skipped without terminating the processing of the file.

### Task 2

Develop a program for processing courier delivery records.

The input file contains one delivery record per line in the following format:

```
id;courier;city;weight;price;status
```

he status may be DELIVERED, FAILED, or RETURNED.

Create an appropriate class to represent a single delivery.

The program should:

- load all valid records from the file;
- group the deliveries by courier;
- calculate the total revenue from successfully delivered shipments;
- determine the courier with the highest number of successfully completed deliveries;
- display the number of deliveries for each city;
- determine the cities with at least one failed delivery;
- sort successfully delivered shipments by price in descending order;
- write summary statistics to an output file.

If a record contains invalid numeric values or an unknown status, it should be skipped without terminating the program.

