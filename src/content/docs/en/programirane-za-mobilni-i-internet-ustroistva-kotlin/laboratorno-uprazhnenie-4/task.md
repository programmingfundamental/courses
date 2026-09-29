---
title: Tasks
sidebar:
  order: 100
  label: Tasks
taskPage: true
---
Create an Android application with Kotlin and Jetpack Compose that displays a title and an image loaded from the internet.

## 1. Compose project

Create or open a project with Compose and Material 3. Keep the generated Gradle configuration described on the [main page](/courses/en/programirane-za-mobilni-i-internet-ustroistva-kotlin/laboratorno-uprazhnenie-4/).

## 2. Image dependencies

In the `dependencies` block of `app/build.gradle.kts`, add the dependencies for Coil Compose and network loading. The repository does not specify a Coil version. The example uses `3.6.2`, listed in the [official Coil setup guide](https://coil-kt.github.io/coil/getting_started/) when checked on 17 September 2026. Both artifacts must use the same version; if using a version catalog, specify it there.

```kotlin
dependencies {
    implementation("io.coil-kt.coil3:coil-compose:3.6.2")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.6.2")
}
```

## 3. Gradle sync

Run **Sync Project with Gradle Files**. Before proceeding, check that the dependencies have loaded successfully.

## 4. Internet permission

In `app/src/main/AndroidManifest.xml`, add the following element directly inside `<manifest>`, outside and before `<application>`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## 5. URL value

Store the URL as a `String` in the composable function:

```kotlin
val imageUrl = "https://i.imgur.com/DvpvklR.png"
```

## 6. Composable function

Create an `InternetImageScreen()` function with `@Composable` and `Column`. Call it inside `setContent` in `MainActivity`.

## 7. Title

Add a `Text` element to the `Column` with the title "Image from the internet".

## 8. Image

After the title, add an `AsyncImage` with the URL value, a description, an appropriate size, and `ContentScale.Crop`. The complete `MainActivity.kt` example is shown below. Adjust the `package` line to match your project's package.

```kotlin
package com.example.mycomposeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                InternetImageScreen()
            }
        }
    }
}

@Composable
fun InternetImageScreen(modifier: Modifier = Modifier) {
    val imageUrl = "https://i.imgur.com/DvpvklR.png"
    Column(
        modifier = modifier.fillMaxSize().safeDrawingPadding().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Image from the internet",
            style = MaterialTheme.typography.headlineSmall
        )
        AsyncImage(
            model = imageUrl,
            contentDescription = "Image loaded from the internet",
            modifier = Modifier.fillMaxWidth().height(300.dp),
            contentScale = ContentScale.Crop
        )
    }
}
```

## 9. Running the application

Run the application on an emulator or physical device with internet access.

## 10. Verification

Check that the title and image are displayed and that the image occupies the specified area. If loading fails, check the URL, internet connection, permission, and Logcat messages.

Loading a network resource is not a reliable test in Compose Preview, where network access is restricted. Test actual loading on an emulator or device. [Coil Preview documentation](https://coil-kt.github.io/coil/compose/#previews).

## Independent tasks

Work in your own Android project with Kotlin and Jetpack Compose. Use the generated project configuration.

### Task 1. Your first application

Create a "My Student Card" application. Move the interface into a `StudentCardScreen()` composable function and call it from `setContent`. Display a name, degree program, and year of study using sample data.

Run the application on an emulator or physical device and check that all labels are visible.

### Task 2. Resources and Preview

Store the application name and card labels in `strings.xml`. Add your own local image to `res/drawable` and display it with a description. The image can be a PNG icon you created.

Create a Preview of the card. Change one text resource and check the result both in Preview and in the running application.

### Task 3. Navigating the project

Create a table containing the paths to the manifest, the Kotlin file with the interface, the text resources, and the `app` module's Gradle file. State the purpose of each file.

Temporarily remove a closing XML tag from one of your own text resources. Start the build, record how the message identifies the error, restore the tag, and build the project successfully.

### Verification and submission

Submit the project, the table, and a screenshot of the running application. Include the XML error message and the result after the fix. The card must also be displayed without an internet connection.
