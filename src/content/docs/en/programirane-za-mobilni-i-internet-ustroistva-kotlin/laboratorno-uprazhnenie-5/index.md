---
title: Lab 5
sidebar:
  order: 5
---

# Lab 5

## Building a user interface

The user interface (UI) includes text, images, buttons, input fields, and their arrangement on the screen. It allows the application to present information and receive user actions.

![Enabled button variants](/courses/docs/BEO/programirane-za-mobilni-i-internet-ustroistva-kotlin/laboratorno-uprazhnenie-5/image.png)

![Tooltip with a description and an action](/courses/docs/BEO/programirane-za-mobilni-i-internet-ustroistva-kotlin/laboratorno-uprazhnenie-5/image-1.png)

![Filled and outlined input fields](/courses/docs/BEO/programirane-za-mobilni-i-internet-ustroistva-kotlin/laboratorno-uprazhnenie-5/image-2.png)

Elements can be interactive, such as buttons and input fields, or present information, such as text and images.

## Jetpack Compose

Jetpack Compose is a toolkit for declaratively building Android interfaces with Kotlin. The interface is described through composable functions (`@Composable`) that use input data and the current state.

## Composable functions

The `@Composable` annotation allows a function to participate in composition. Functions that describe UI usually return `Unit` and call other composable functions, such as `Text`, `Image`, and `Button`. Not every function marked `@Composable` necessarily creates a visible element.

The composition is the description of the interface that Compose builds when these functions run. Recomposition reruns affected parts when input data or observed state changes.

## Units of measurement

Density-independent pixels (`dp`) are used for sizes and spacing. Scalable pixels (`sp`) are used for text size and account for the user's font setting. In Kotlin, values are written as, for example, `16.dp` and `20.sp`, using the corresponding imports from `androidx.compose.ui.unit`.

AndroidX is a set of Android libraries. Compose APIs are used through packages such as `androidx.compose.foundation`, `androidx.compose.material3`, and `androidx.compose.ui`.

## User interface hierarchy

A layout is built through nested calls. A parent layout contains child elements, which can also contain other elements.

- `Column` arranges elements vertically.
- `Row` arranges them horizontally.
- `Box` allows elements to overlap and be positioned within a shared area.

![Vertical arrangement with Column and horizontal arrangement with Row](/courses/docs/BEO/programirane-za-mobilni-i-internet-ustroistva-kotlin/laboratorno-uprazhnenie-5/image-3.png)

Place each of the following three snippets separately inside a composable function. They require `import androidx.compose.foundation.layout.Row` and `import androidx.compose.material3.Text`.

```kotlin
Row {
    Text("First Column")
    Text("Second Column")
}
```

![Two text elements arranged horizontally in a Row](/courses/docs/BEO/programirane-za-mobilni-i-internet-ustroistva-kotlin/laboratorno-uprazhnenie-5/image-4.png)

Functions such as `Row`, `Column`, and `Box` accept content as a lambda expression. When the last argument is a lambda expression, it can be written after the parentheses in braces `{ ... }` (a trailing lambda). If there are no other arguments, the parentheses can be omitted.

Using the named `content` parameter:

```kotlin
Row(
    content = {
        Text("Some text")
        Text("Some more text")
        Text("Last text")
    }
)
```

Equivalent syntax with a trailing lambda:

```kotlin
Row {
    Text("Some text")
    Text("Some more text")
    Text("Last text")
}
```

## Layout and `Modifier`

`Modifier` specifies characteristics such as size, padding, background, positioning, and interaction. Modifiers are chained, and their order can change the result. Some are available only in a particular scope, such as `Modifier.align` inside a `Box`.

Not all properties are set through `Modifier`. `fontSize`, `lineHeight`, and `textAlign` are parameters of `Text`. In a `Column`, arrangement is controlled through `verticalArrangement` and `horizontalAlignment`; in a `Row`, through `horizontalArrangement` and `verticalAlignment`.

## Handling user interactions

Compose components can provide visual feedback during interaction. `Button` provides an `onClick` parameter for specifying a handler function. There is no need to add `Modifier.clickable` to the button as well.

Use `Modifier.clickable` when another suitable element should respond to a click. This describes the action through a callback function without manually handling every touch or key press.

## Application resources

Resources are stored in `app/src/main/res`. **Resource Manager** is used to view and add resources and is opened through **View > Tool Windows > Resource Manager**. Building the project generates identifiers in the `R` class, such as `R.string.app_name`.

In Compose, use `stringResource()` to retrieve a text resource and `painterResource()` to retrieve a suitable graphics resource. These functions are in `androidx.compose.ui.res`. The graphics resource can be passed to `Image`.

## Dynamic user interfaces

An ordinary local variable does not automatically become observable Compose state. `mutableStateOf()` creates observable state. Changing its value can trigger recomposition of the parts that read it.

`remember` retains a value across recompositions while the corresponding part remains in the composition. The lambda passed to `remember` creates the initial value; it is not a handler called on every change. This example shows a counter:

```kotlin
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun CounterButton() {
    var count by remember { mutableStateOf(0) }
    Button(onClick = { count++ }) {
        Text("Click count: $count")
    }
}
```

`remember` on its own does not retain the value when the `Activity` is recreated.
