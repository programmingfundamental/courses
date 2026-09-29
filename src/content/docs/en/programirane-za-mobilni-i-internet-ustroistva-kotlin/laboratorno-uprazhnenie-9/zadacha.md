---
title: Tasks
sidebar:
  order: 100
  label: Tasks
taskPage: true
---
Create an application that displays images and text using different lists and grids in Jetpack Compose.

1. Add the text resources from the supplied `lab9_strings` file to `app/src/main/res/values/strings.xml`.
2. Extract the images from the supplied `lab9_images.zip` archive and add them to `app/src/main/res/drawable`.

The repository does not contain `lab9_strings` or `lab9_images.zip`, and no download link is provided. To complete the exercise with these specific teaching resources, obtain them from the instructor. Do not assume their names or contents.

## Model and data

3. Create a `Place` data class with the following properties:

```kotlin
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Place(
    @StringRes val stringResourceId: Int,
    @DrawableRes val drawableResourceId: Int
)
```

4. Create a class with a `loadPlaces(): List<Place>` method that returns a list of `Place` objects linking text resources to images. Use the actual identifiers from the generated `R` class.

## Composable functions

5. Create a `PlaceApp()` function with `@Composable` that obtains the list and displays the selected layout. Call the function from `setContent`.
6. Create `PlaceCard(place: Place, modifier: Modifier = Modifier)` with `@Composable`. Place an `Image` and a `Text` inside a `Card`, retrieving resources through `painterResource()` and `stringResource()`.
7. Implement `PlaceColumn(places: List<Place>, modifier: Modifier = Modifier)` with `@Composable`, calling `PlaceCard()` for each item in a `LazyColumn` on a **green background**.
8. Implement `PlaceRow(places: List<Place>, modifier: Modifier = Modifier)` with `@Composable`, calling `PlaceCard()` for each item in a `LazyRow` on a **blue background**.
9. Implement `PlaceVerticalGrid(places: List<Place>, modifier: Modifier = Modifier)` with `@Composable`, using a `LazyVerticalGrid` on a **purple background**. Set `columns` through `GridCells.Fixed` or `GridCells.Adaptive`.
10. Implement `PlaceHorizontalGrid(places: List<Place>, modifier: Modifier = Modifier)` with `@Composable`, using a `LazyHorizontalGrid` on a **purple background**. Set `rows` and a bounded height for the horizontal grid.

Apply the `modifier` parameter to the root element of the corresponding function. Test the four layouts separately. When using a resource identifier as a `key`, ensure that it is unique for each place in the list.

## Independent tasks

Create a catalog of places with at least 12 entries. Use your own text and local images; the same graphics resource can be used for multiple entries.

### Task 1. Data and a list

Create a model with a unique `id`, name, category, and graphics resource identifier. Prepare entries in at least three categories, such as "Park", "Museum", and "Beach".

Display them using `LazyColumn` and a separate card composable. Use `id` as a stable key; the graphics resource is not the entry's identifier.

### Task 2. Searching and filtering

Add a case-insensitive search by part of the name and category selection, including "All". Both filters should apply simultaneously.

Display the number of places found and a message when the result is empty. Clearing the search and selecting "All" should restore the full list.

### Task 3. Grid and favorite places

Add switching between `LazyColumn` and `LazyVerticalGrid`, along with a "Favorite" action for each entry. Store the selected identifiers in observable state shared across the screen.

Sort places by name and check that favorites remain associated with the correct entries when sorting, filtering, and switching layouts.

### Verification and submission

Submit the model, sample data, and UI code. Test an empty search, no matching results, and a combination of both filters. Mark a place as a favorite, hide it with a filter, and show it again: the selection must be retained on the current screen.
