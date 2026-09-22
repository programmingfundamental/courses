---
title: Tasks
taskPage: true
sidebar:
  label: Tasks
  order: 100
---
Create an application that outputs an image and text using different List and Grid

1. Create string resources that will be used for text to the image 

2. Add the images from lab9_images.zip to your project – res/drawable

3. Create a Place class with attributes:
- @StringRes val stringResourceId: Int,
- @DrawableRes val drawableResourceId: Int

4. Create a class and method that returns a list of Places.

5. Create a function @Composable fun PlaceApp()

6. Create a function @Composable fun PlaceCard(place: Place, modifier: Modifier = Modifier), which outputs an image (Image) and text (Text) to a Card

7. Create a function @Composable fun PlaceColumn(places: List<Place>, modifier: Modifier = Modifier), which calls PlaceCard() in a LazyColumn on a green background.

8. Create a function fun PlaceRow(places: List<Place>, modifier: Modifier = Modifier) ​​that calls PlaceCard() in a LazyRow on a blue background

9. Create a function @Composable fun PlaceVerticalGrid(places: List<Place>, modifier: Modifier = Modifier) ​​that calls PlaceCard() in a LazyVerticalGrid on a purple background

10. Create a function @Composable fun PlaceHorizontalGrid(places: List<Place>, modifier: Modifier = Modifier) ​​that calls PlaceCard() in a LazyHorizontalGrid on a purple background
