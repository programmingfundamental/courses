---
layout: default
title: Lab 9
parent: Mobile and Internet Programming Kotlin
has_children: true
nav_order: 9
permalink: /docs/programirane-za-mobilni-i-internet-ustroistva-kotlin-аео/laboratorno-uprazhnenie-9
---

# Lab 9

## Lists and grids

Many applications need to display collections of items. This lab is dedicated to the way to do this efficiently in Jetpack Compose.

If the use case that needs to be developed does not require scrolling, Column or Row could be used and emit the contents of each item by Iterating a List like this:

```koitlin
@Composable
fun MessageList(messages: List<Message>) {
    Column {
        messages.forEach { message ->
            MessageRow(message)
        }
    }
}
```

Scrolling is implemented via modifier verticalScroll()

## Lazy lists

If there is a need to display large number of items (or a list of unknown length), using the Column layout can cause performance issues, as all items will be composed and arranged, whether they are visible or not.

Compose provides a set of components that compose and arrange only items that are visible in the component's viewport. These components include LazyColumn, LazyRow, LazyVerticalGrid, LazyHorizontalGrid

As the name suggests, the difference between them is the orientation in which they arrange their items and scroll.

Lazy components are different from most layouts in Compose. Instead of taking a parameter to a block of UI elements, which allows applications to directly emit Composable elements, Lazy components provide a LazyListScope block. This LazyListScope block provides a DSL (domain-specific language) that allows applications to describe the contents of the element. The lazy component is then responsible for adding the contents of each item as required by the layout and scroll position.

The LazyListScope DSL provides a number of functions for describing items in the layout. In the most basic case, item() adds a single item, and items(Int) adds multiple items:

```kotlin
LazyColumn {
    // Add a single item
    item {
        Text(text = "First item")
    }

    // Add 5 items
    items(5) { index ->
        Text(text = "Item: $index")
    }

    // Add another single item
    item {
        Text(text = "Last item")
    }
}
```

There are also a number of extension functions that allow addition to collections of items, such as a list. These extensions make possible to easily migrate the column example above.:

```kotlin
LazyColumn {
    items(messages) { message ->
        MessageRow(message)
    }
}
```

There is also a variant of the items() extension function called itemsIndexed() that provides the index.

## Lazy grids

The LazyVerticalGrid and LazyHorizontalGrid components provide support for displaying elements in a grid. A lazy vertical grid will display its elements in a vertically scrolling container spread across multiple columns, while lazy horizontal grids will have the same behavior on the horizontal axis.

Grids have the same powerful API capabilities as lists and also use a very similar DSL - LazyGridScope.() to describe the content.

The columns parameter in LazyVerticalGrid and the rows parameter in LazyHorizontalGrid control how the cells are formed into columns or rows. The following example displays elements in a grid, using GridCells.Adaptive to set each column to be at least 128.dp wide:

```kotlin
LazyVerticalGrid(
    columns = GridCells.Adaptive(minSize = 128.dp)
) {
    items(photos) { photo ->
        PhotoItem(photo)
    }
}
```

LazyVerticalGrid make possible to specify a width for the elements, and then the grid will fit into as many columns as possible. Any remaining width is divided equally between the columns after the number of columns is calculated. This adaptive way of sizing is particularly useful for displaying sets of elements on different screen sizes.

If the exact number of columns to be used is known in advance, an instance of GridCells.Fixed could be used containing the number of columns needed.

If the design requires only certain elements to have non-standard sizes, grid support can be used to provide custom column ranges for elements. Specifying the column range is done with the help of range parameter of the element and elements methods of the LazyGridScope DSL. maxLineSpan, one of the range values, is particularly useful when using adaptive sizing, since the number of columns is not fixed. This example shows how to provide a full range of rows:

```kotlin
LazyVerticalGrid(
    columns = GridCells.Adaptive(minSize = 30.dp)
) {
    item(span = {
        // LazyGridItemSpanScope:
        // maxLineSpan
        GridItemSpan(maxLineSpan)
    }) {
        CategoryCard("Fruits")
    }
    // ...
}
```


# Task


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

