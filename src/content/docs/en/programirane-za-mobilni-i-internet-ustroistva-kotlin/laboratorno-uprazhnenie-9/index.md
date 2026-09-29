---
title: Lab 9
sidebar:
  order: 9
---

# Lab 9

## Lists and grids in Jetpack Compose

A collection of elements can be displayed using `Column` or `Row` when the number of elements is small and lazy creation is unnecessary. `Column` creates the supplied content, including elements outside the visible area.

The following message examples use a shared model and composable function:

```kotlin
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

data class Message(val id: Long, val text: String)

@Composable
fun MessageRow(message: Message) {
    Text(message.text)
}

@Composable
fun MessageList(messages: List<Message>) {
    Column {
        messages.forEach { message ->
            MessageRow(message)
        }
    }
}
```

To enable vertical scrolling, add `Modifier.verticalScroll(rememberScrollState())` to a `Column`, with imports of `verticalScroll` and `rememberScrollState` from `androidx.compose.foundation` and `Modifier` from `androidx.compose.ui`. This does not turn `Column` into a lazy component: all content is still created.

## Lists with lazy item creation (`LazyColumn` and `LazyRow`)

`LazyColumn` and `LazyRow` compose and lay out the required items based on the visible area and scroll position. They are suitable for large or dynamic numbers of items. Nearby items may be prepared in advance, so do not assume that only visible pixels are created.

`LazyColumn` arranges and scrolls items vertically, while `LazyRow` does so horizontally. Grids use `LazyVerticalGrid` and `LazyHorizontalGrid`, respectively.

### `LazyListScope`

The lazy list block provides a DSL: a set of functions for describing content. In `LazyListScope`, `item` adds one item, `items` adds multiple items, and `itemsIndexed` also provides the index.

```kotlin
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun NumberedList() {
    LazyColumn {
        item { Text("First item") }
        items(5) { index -> Text("Item: $index") }
        item { Text("Last item") }
    }
}
```

Use the `items` extension to iterate over a collection. This example uses `Message` and `MessageRow` from the beginning of the page:

```kotlin
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable

@Composable
fun LazyMessageList(messages: List<Message>) {
    LazyColumn {
        items(messages) { message ->
            MessageRow(message)
        }
    }
}
```

`itemsIndexed` is imported from `androidx.compose.foundation.lazy.itemsIndexed` and passes two arguments to the lambda expression: the index and the item.

### Stable keys

The `key` parameter helps Compose track an item's identity when items are added, removed, or reordered. The key must be unique within the list and stable for the same item. To save state on Android, use a type supported by `Bundle`, such as `Long` or `String`.

The following snippet replaces the `items` block in `LazyMessageList`. It assumes that each message has a unique `id`:

```kotlin
items(messages, key = { it.id }) { message ->
    MessageRow(message)
}
```

## Grids with lazy item creation

`LazyVerticalGrid` arranges items in columns and scrolls vertically. `LazyHorizontalGrid` arranges items in rows and scrolls horizontally. The `LazyGridScope` block provides `item` and `items` functions similar to those for lists.

The number and size of cells are set through `columns` in `LazyVerticalGrid` and `rows` in `LazyHorizontalGrid`:

- `GridCells.Adaptive(minSize = 128.dp)` determines the number of columns or rows based on the available space and desired minimum cell size. If the area is too small, one cell uses the available size.
- `GridCells.Fixed(2)` specifies exactly two columns or two rows.

The image example uses resource identifiers passed through `photos`:

```kotlin
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

data class Photo(@DrawableRes val drawableResourceId: Int)

@Composable
fun PhotoItem(photo: Photo) {
    Image(
        painter = painterResource(photo.drawableResourceId),
        contentDescription = "Photo",
        modifier = Modifier.fillMaxWidth().height(128.dp),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun PhotoGrid(photos: List<Photo>) {
    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 128.dp)) {
        items(photos) { photo -> PhotoItem(photo) }
    }
}
```

With adaptive sizing, the remaining space is distributed among the columns. For a fixed number of columns, replace `GridCells.Adaptive(...)` with `GridCells.Fixed(2)`.

### An item spanning a full row

The `span` parameter determines how many cells an item occupies. `GridItemSpan(maxLineSpan)` is suitable for a heading spanning all columns, even when their number is adaptive.

```kotlin
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun CategoryCard(title: String) {
    Text(title)
}

@Composable
fun CategoryGrid() {
    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 30.dp)) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            CategoryCard("Fruits")
        }
        items(6) { index -> Text("Item $index") }
    }
}
```
