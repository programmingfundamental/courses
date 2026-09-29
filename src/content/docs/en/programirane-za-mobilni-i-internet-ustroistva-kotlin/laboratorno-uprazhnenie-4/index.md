---
title: Lab 4
sidebar:
  order: 4
---

# Lab 4

## The Android Studio development environment

Android Studio is an integrated development environment for Android applications, based on IntelliJ IDEA. It provides a code editor and tools for building interfaces, compilation, testing, and debugging.

An Android interface can be built using XML-based View components or Jetpack Compose. These labs use Jetpack Compose.

### Main tools

| Tool | Purpose |
| --- | --- |
| Code Editor | Editing Kotlin, Java, and C++ code, code completion, refactoring, and Lint analysis. |
| Project | Viewing modules, source code, resources, and configuration files. The Android view groups files logically, while Project shows directories. |
| Layout Editor | A visual editor for XML layouts with View components, including `ConstraintLayout`. This is a separate approach from Compose. |
| Compose Preview | Previewing composable functions with `@Preview` without running the entire application. |
| Resource Manager | Viewing and adding resources, such as images. |
| Gradle | Automates compilation, resource processing, and dependency management. Supports build variants such as debug and release. |
| Device Manager | Creating and managing virtual devices (AVDs) and viewing connected devices. |
| Android Emulator | Running Android on a virtual device. |
| Logcat | Viewing and filtering system and application messages, including exceptions. |

Android Studio integrates with Git for tracking changes, cloning repositories, and working with remote repositories. Android project dependencies are usually obtained from Google Maven and Maven Central.

## Android SDK

The Android SDK (Software Development Kit) includes libraries and tools for compiling and testing applications. Packages are managed through **SDK Manager**.

| Component | Purpose |
| --- | --- |
| Command-line Tools | `sdkmanager` manages SDK packages, while `avdmanager` manages virtual devices. |
| Platform Tools | Include `adb` and `fastboot`. `adb` provides device connectivity, application installation, and access to Logcat. These tools are not limited to a single Android version. |
| Build Tools | Tools for building an application: for example, AAPT2 processes resources, `aidl` generates code for interprocess communication, and `zipalign` aligns data in an APK. |
| SDK Platform | The Android API library for a specific API level, used during compilation. |
| System Images | System images for virtual devices, matched to the Android version and the host machine's architecture. |

## Android Emulator and Device Manager

The emulator allows applications to be tested with different screen sizes, Android versions, and device configurations. It supports simulated location, sensors, cameras, calls, and SMS depending on the selected image. Hardware acceleration depends on the operating system and available virtualization. Testing on an emulator does not replace all testing on a physical device.

In **Device Manager**, create an Android Virtual Device (AVD) by selecting:

- a device type: phone, tablet, television, or watch;
- a system image and API level;
- a compatible architecture;
- hardware settings, such as memory and graphics acceleration.

In older versions, this tool is called **AVD Manager**. After starting the emulator, you can install the application from Android Studio or using `adb`. It also supports installing APKs by dragging them into the window, screenshots, and video recording.

## How Android Studio, the SDK, and the Emulator work together

| Component | Role |
| --- | --- |
| Android Studio | Organizes project work and launches build and verification tools. |
| Android SDK | Provides Android APIs and tools for building applications and communicating with devices. |
| Android Emulator | Runs the application in a virtual Android environment. |

Example workflow:

1. Create a project in Android Studio.
2. Gradle uses the Android SDK to build the application.
3. Install the APK on the selected emulator or physical device.
4. Run the application and check the result, including through Logcat.

## Structure of an Android project with Kotlin and Jetpack Compose

The project separates source code, resources, and configuration. When creating a project, select a Compose template, such as **Empty Activity**. Template names may vary between Android Studio versions.

```text
MyComposeApp/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/mycomposeapp/
│   │   │   │   └── MainActivity.kt
│   │   │   ├── res/
│   │   │   │   ├── drawable/
│   │   │   │   ├── values/
│   │   │   │   └── mipmap/
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                 # Local tests
│   │   └── androidTest/          # Tests on an Android device
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── local.properties
└── gradle/
    ├── libs.versions.toml        # If a version catalog is used
    └── wrapper/
        ├── gradle-wrapper.jar
        └── gradle-wrapper.properties
```

Kotlin files can be in a `java` or `kotlin` directory, depending on the project. The `res/layout` folder is needed for XML layouts; Compose does not require an XML file for each screen, but both approaches can coexist in one application.

## `AndroidManifest.xml`

The manifest describes the application's components, permissions, and entry `Activity`. The following example assumes the package `com.example.mycomposeapp` and the generated resources `app_name`, `ic_launcher`, and `Theme.MyComposeApp`. Adjust the names to match your project.

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/Theme.MyComposeApp">
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

| Element | Description |
| --- | --- |
| `<manifest>` | The manifest's root element. In a modern Gradle project, `namespace` and `applicationId` are specified in the module configuration. |
| `<uses-permission>` | Declares a permission required by the functionality being used. |
| `<application>` | General settings: icon, name, theme, and other resources. |
| `<activity>` | Declares an `Activity` class. |
| `<intent-filter>` | Describes the intents (`Intent`) the component can handle; here, it defines the entry `Activity`. |

For applications targeting Android 12 or later, components with an `intent-filter` must explicitly set `android:exported`. For the entry `Activity`, the value is `true` so that the system launcher can start it. Other components should not automatically receive `true`.

## Gradle configuration (Kotlin DSL)

This repository does not contain a sample Android project or a version catalog from which to obtain a compatible set of versions. Therefore, keep the configuration generated by Android Studio's Compose template instead of copying a separate list of fixed versions.

`settings.gradle.kts` defines the name and modules. The following fragment goes after the generated plugin and repository settings:

```kotlin
rootProject.name = "MyComposeApp"
include(":app")
```

The project-level `build.gradle.kts` declares shared plugins, while `app/build.gradle.kts` applies those required by the application. With Kotlin 2.0 and later, use the Compose compiler plugin `org.jetbrains.kotlin.plugin.compose` with the same version as Kotlin. If the project uses a version catalog, take the version from its Kotlin entry. Do not add the old `composeOptions.kotlinCompilerExtensionVersion = "1.5.1"` to such a configuration. [Official Compose compiler plugin setup guide](https://developer.android.com/develop/ui/compose/setup-compose-dependencies-and-compiler).

A short fragment from `app/build.gradle.kts`, when the plugin version has already been defined at project level:

```kotlin
plugins {
    // Keep the remaining generated plugins.
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    buildFeatures {
        compose = true
    }
}
```

If the same plugin is already applied through `alias(...)`, do not add it a second time. Keep the generated dependencies for `activity-compose`, the Compose BOM, Material 3, `ui-tooling-preview`, and `ui-tooling`. The Compose BOM aligns Compose library versions; it does not determine the Kotlin or Compose compiler plugin version. After making changes, run **Sync Project with Gradle Files**.

## Your first Compose interface — `MainActivity.kt`

The `MainActivity` class extends `ComponentActivity`. Its `onCreate()` calls `setContent`, which sets the Compose content. The example uses Material 3 and the package `com.example.mycomposeapp`; adjust the package name to match your project.

```kotlin
package com.example.mycomposeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    GreetingScreen()
                }
            }
        }
    }
}

@Composable
fun GreetingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to MyComposeApp!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { /* The demonstration button has no action. */ }) {
            Text("Button")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingScreenPreview() {
    MaterialTheme {
        GreetingScreen()
    }
}
```

`@Composable` marks a composable function. `Text` displays text, while `Modifier` specifies properties such as size and spacing. `MaterialTheme` provides colors, typography, and shapes. In a real project, you can use the generated theme function that configures `MaterialTheme`. `@Preview` displays the interface in Android Studio.

## Resource files

| Directory or file | Contents |
| --- | --- |
| `res/drawable/` | Raster images and XML graphics resources, including vector drawables. SVG can be imported through Vector Asset when supported. |
| `res/mipmap/` | Application launcher icons. |
| `res/values/strings.xml` | Text resources. |
| `res/values/colors.xml` | Color resources, when needed. |
| `res/values/themes.xml` | The application's Android theme; the Compose theme is usually also configured through Kotlin code. |

The manifest describes components, Gradle manages the build and dependencies, `MainActivity.kt` defines the interface, and `res` contains resources. Android Studio brings together work on these files and starts the APK or Android App Bundle (AAB) build.
