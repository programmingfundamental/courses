---
title: Tasks
taskPage: true
sidebar:
  label: Tasks
  order: 100
---
### Task

Create an application that randomly displays an image of a dice with a value at a time specified by the user.

- Download the dice images from: https://tuvarnabg.sharepoint.com/:u:/s/msteams_230e9b/EXtfPyFQ_3tAnBwEYE7-4XgB3w6hd6boqpZEw_RJEj-sgg?e=HW2dfN

- Add the images to your project – res/drawable

- Add string resources for each dice

- Create a Dice class with attributes:
    @StringRes val stringResourceId: Int,
    @DrawableRes val drawableResourceId: Int

- Add a time input field

- Add a button to start an asynchronous process that selects a random object from the dice list at a specified time from the input field and visualizes it.
