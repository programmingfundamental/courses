---
title: Tasks
sidebar:
  order: 100
  label: Tasks
taskPage: true
---
Create an application that selects a random die face at a user-defined interval and displays the corresponding image.

## Resources and model

1. Use the dice images from the following resource: [images for the task](https://tuvarnabg.sharepoint.com/:u:/s/msteams_230e9b/EXtfPyFQ_3tAnBwEYE7-4XgB3w6hd6boqpZEw_RJEj-sgg?e=HW2dfN).
2. Add the images to `app/src/main/res/drawable`.
3. In `app/src/main/res/values/strings.xml`, add a text resource for the value of each die face.
4. Create a `Dice` data class with `@StringRes val stringResourceId: Int` and `@DrawableRes val drawableResourceId: Int`. Prepare a nonempty `List<Dice>` linking the actual resources. Do not infer file names from the resource URL.

## Interval and controls

5. Add a field for the interval in **positive whole seconds**. Check for an empty value, a nonnumeric value, and a value less than or equal to zero. Also reject values that cannot be safely converted to milliseconds.
6. Add "Start" and "Stop" buttons. "Start" should enable the repeating process only after successful validation. Disable the field and "Start" while the process is running. "Stop" should cancel the work.
7. Use a single `LaunchedEffect` controlled by the `running` state. After each `delay()`, select a random object from the list and display its image and text. Pressing "Start" again must not create additional loops.

## Control example

Add this example to a Kotlin file in the Compose project. Call `DiceScreen(dice)` inside `setContent`, within the application theme, using the prepared resource list. It uses Material 3. The code does not assume specific image names.

```kotlin
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

data class Dice(
    @StringRes val stringResourceId: Int,
    @DrawableRes val drawableResourceId: Int
)

fun intervalMillisOrNull(input: String): Long? {
    val seconds = input.trim().toLongOrNull() ?: return null
    if (seconds <= 0 || seconds > Long.MAX_VALUE / 1000L) return null
    return seconds * 1000L
}

@Composable
fun DiceScreen(dice: List<Dice>, modifier: Modifier = Modifier) {
    var intervalText by rememberSaveable { mutableStateOf("1") }
    var intervalMillis by remember { mutableStateOf(1000L) }
    var running by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var currentDice by remember(dice) { mutableStateOf(dice.firstOrNull()) }

    LaunchedEffect(running, intervalMillis, dice) {
        if (running && dice.isNotEmpty()) {
            while (isActive) {
                delay(intervalMillis)
                currentDice = dice.random()
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize().safeDrawingPadding().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = intervalText,
            onValueChange = { intervalText = it; error = null },
            label = { Text("Interval in seconds") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            enabled = !running,
            isError = error != null,
            supportingText = { error?.let { Text(it) } }
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                enabled = !running && dice.isNotEmpty(),
                onClick = {
                    val parsed = intervalMillisOrNull(intervalText)
                    if (parsed == null) {
                        error = "A valid positive number of seconds is required."
                    } else {
                        error = null
                        intervalMillis = parsed
                        running = true
                    }
                }
            ) { Text("Start") }
            Button(enabled = running, onClick = { running = false }) {
                Text("Stop")
            }
        }
        currentDice?.let { selected ->
            val description = stringResource(selected.stringResourceId)
            Image(
                painter = painterResource(selected.drawableResourceId),
                contentDescription = description,
                modifier = Modifier.size(180.dp),
                contentScale = ContentScale.Fit
            )
            Text(description)
        }
        if (dice.isEmpty()) {
            Text("Dice resources are missing.")
        }
    }
}
```

When the `running` key changes, the previous `LaunchedEffect` coroutine is cancelled. When it is `false`, no new loop starts. Leaving the composition also cancels the work. Moving the `Activity` to the background does not necessarily mean leaving the composition.

Initially, the first die face in the list is displayed. After "Start", a random selection occurs after each specified interval. The interval is approximate and depends on execution scheduling; `delay()` is not a precise clock.

## Verification

Test empty input, text instead of a number, zero, a negative value, and an excessively large number. The loop must not start when there is an error. After a valid start, check that the image changes, the "Start" button is disabled, "Stop" stops the process, and it can subsequently restart with a different interval. Random selection may repeat the previous die face.

## Independent tasks

Implement a Compose countdown timer with a coroutine. No images or network connection are required.

### Task 1. Input and display

Add a field for an initial duration in whole seconds from 1 to 300, text showing the remaining time, and "Start", "Stop", and "Reset" buttons.

For an invalid value, show an error and do not start the timer. Initially, the remaining time should be 0 until a valid duration is accepted.

### Task 2. A single running loop

Manage the countdown with `LaunchedEffect` and `delay()`. After each 1000 ms wait, decrease the value by 1 until it reaches 0. At 0, stop the loop and display "Done".

Disable the field and "Start" while the timer is running. A repeated action must not create a second loop. This is an educational countdown; clock-level accuracy is not required.

### Task 3. Cancellation and restarting

"Stop" should cancel the countdown and leave the current value visible. The next "Start" should begin from the entered duration. "Reset" should cancel the work, set the remaining time to 0, and hide "Done".

Add a button outside the timer that removes its composable function from the composition. When shown again, the timer should be stopped. The current execution state should be local to that function and should not be restored as running after `Activity` recreation.

### Verification and submission

Submit the code and observations for completing a 3-second countdown, stopping midway, restarting, resetting, and removing the timer from the composition. Test `0`, `301`, empty input, and nonnumeric text. Explain why `Thread.sleep()` is unsuitable for this UI loop.
