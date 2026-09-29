---
title: Tasks
sidebar:
  order: 100
  label: Tasks
taskPage: true
---
## Independent tasks

These tasks are for preparation and self-assessment for the test on Labs 4–7. Create a "Split the Bill" Compose application using arbitrary currency units.

### Task 1. Input form

Add fields for the total amount and number of participants, along with an "Add 10% service charge" `Switch`. Save the entered text and selection with `rememberSaveable`.

Specify in the label that the amount uses a period as the decimal separator. The form must remain usable while the keyboard is visible.

### Task 2. Calculation

Move the calculation into an ordinary Kotlin function. Accept only a finite positive amount and a positive integer number of participants. Check for invalid input before division.

When "Calculate" is pressed, display the total including the service charge and the amount per person to two decimal places. For an amount of 120 and four participants, expect 30.00 without the service charge and 33.00 with it.

### Task 3. State and user events

Add a "Reset" button. When input values change, hide the old result until "Calculate" is pressed again. If there is an error, display a message next to the corresponding field.

Separate the form and result display into different composable functions. Pass data and callback functions through parameters.

### Verification and submission

Submit the project, calculation function, and self-assessment results. Test empty input, letters, zero and negative amounts, zero and fractional participant counts, and `NaN` as the amount. Rotate the device and check that input data is restored.
