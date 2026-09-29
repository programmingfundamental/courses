---
title: Tasks
sidebar:
  order: 100
  label: Tasks
taskPage: true
---
## Task 1

Create a `GreetingText` composable function with the `@Composable` annotation and the parameters `message: String` and `modifier: Modifier = Modifier`. Display it in Compose Preview with the message "Happy Birthday Android!".

## Task 2

Set the `fontSize` parameter of `Text` to `100.sp`. Check the result.

## Task 3

Set the `lineHeight` parameter of `Text` to `116.sp`. Check the result.

## Task 4

Add a new text element showing who the greeting is from. Set its `fontSize` parameter to `36.sp`.

## Task 5

Arrange the text elements using `Row`, then `Column`. Compare the results and choose the layout that matches the desired appearance.

## Task 6

Set spacing using `Modifier.padding()`. For `Column`, use `verticalArrangement` and `horizontalAlignment`; for `Row`, use `horizontalArrangement` and `verticalAlignment`. Set text alignment within the `Text` area using `textAlign`. Check the difference between aligning an element and aligning the text inside it.

## Task 7

Additional resources for the task: [SharePoint link](https://tuvarnabg.sharepoint.com/:u:/s/msteams_230e9b/EXtfPyFQ_3tAnBwEYE7-4XgB3w6hd6boqpZEw_RJEj-sgg?e=HW2dfN).

## Independent tasks

Create an "Event Invitation" screen with Compose and Material 3. Use your own text and a local graphics resource.

### Task 1. A reusable invitation

Implement `EventCard(title: String, description: String, modifier: Modifier = Modifier)`. Display a title, a description, and an image. Split the content into at least two smaller composable functions.

Apply the `modifier` parameter to the card's root element. Read fixed labels from text resources.

### Task 2. Layout and readability

Create two Preview variants with a short and a long title. Use `Column`, `Row`, spacing, and suitable alignment so that elements do not overlap. Check the appearance at widths of 320 and 480 dp.

Change the order of `padding` and `background` in a separate example and describe the difference you observe.

### Task 3. Like counter

Add a "Like" button and text showing the number of likes, starting at 0. Each press should increment the count by 1 using `remember` and observable state. Add a "Reset" button.

Place two invitations on the screen with independent counters. Pressing a button in one must not change the other.

### Verification and submission

Submit the composable functions and screenshots of both Preview variants. Demonstrate three likes, a reset, and the independence of the two cards. Describe where each card's state is stored.
