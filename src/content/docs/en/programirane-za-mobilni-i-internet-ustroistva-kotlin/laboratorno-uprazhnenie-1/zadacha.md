---
title: Tasks
sidebar:
  order: 100
  label: Tasks
taskPage: true
---
## Independent tasks

Use a Kotlin console project and the "Introduction to Kotlin" material. Give each task a separate function and call it from `main()`.

### Task 1. Battery status

Implement `batteryLabel(percent: Int?): String`, which returns:

- "No data" for `null`;
- "Invalid value" for values outside the 0–100 range;
- "Low charge" for 0–15, "Normal charge" for 16–79, and "Full charge" for 80–100.

Use conditional expressions and handle `null` without the `!!` operator.

### Task 2. Processing measurements

From a `List<Int?>`, select non-null measurements in the range −50 to 50 inclusive. Print their count, minimum, maximum, and average. If there are no valid measurements, print "No valid measurements" without calculating an average.

For input `[18, 21, -100, null, 25, 60, 19]`, expect 4 valid measurements, a minimum of 18, a maximum of 25, and an average of 20.75.

### Task 3. Device list

Process a list of names: trim leading and trailing whitespace, exclude empty names, remove case-insensitive duplicates, and sort the result. Convert names to lowercase for unambiguous output. Use collection operations and lambda expressions.

For `[" Phone ", "", "TABLET", "phone", " Watch "]`, expect `["phone", "tablet", "watch"]`.

### Verification and submission

Submit the Kotlin files and the verification output. For the battery, test `null`, −1, 0, 15, 16, 79, 80, 100, and 101; for both lists, add an empty input and an input containing only invalid or empty values.
