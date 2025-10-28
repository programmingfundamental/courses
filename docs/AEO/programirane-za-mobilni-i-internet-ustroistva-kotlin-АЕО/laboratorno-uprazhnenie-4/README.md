---
layout: default
title: Lab 4
parent: Mobile and Internet Programming Kotlin
has_children: true
nav_order: 4
permalink: /docs/programirane-za-mobilni-i-internet-ustroistva-kotlin-аео/laboratorno-uprazhnenie-4
---

# Laboratory exercise 4

# Theory

## 1. Android Studio 

**Android Studio** is Google's official integration development environment (IDE) for Android applications creation.  
It is based upon **IntelliJ IDEA** and has available all necessary tools for development process - from coding and interface design to testing, debugging and app publishing in Google Play.

### Main features of Android Studio:

1. **Intelligent code editor (Code Editor)**  
   - Supports **Java**, **Kotlin** and **C++**.  
   - Provides **automatic completion**, **refactor**, **synthax check** and **real-time suggestions**.  
   - Has embedded quality code analysis system (Lint) that detects potential errors and ineffective code.  

2. **Layout Editor**  
   - Visual tool for user interface creation via **drag-and-drop**.  
   - Allows switch between **Design** and **Code** view.  
   - Supports **ConstraintLayout** allowing flexible element positioning and adaptation to different screen sizes.

3. **Gradle building system**  
   - Used for **compilation automation** and **dependency management**.  
   - Allows configuration of different **build variations** (debug, release) and **product flavors**.  
   - Provides easy integration of external libraries from Maven Central or Google Maven Repository.  

4. **Git and GitHub integration**  
   - Embedded support of **version control**.  
   - Allows clone, commit, push and pull request creation directly from IDE.  

5. **Embedded Android Emulator and Device Manager**  
   - Allows application running in simulated Android environment.  
   - Supports different devices, screens and Android versions.  
   - Provides fast switch between emulators and real devices connected via USB or Wi-Fi.  

![Фигура 1 – Начален екран на Android Studio с отворен проект](images/studio_main.png)  
*Figure 1. Base view of Android Studio with Layout Editor active*

---

## 2. Android SDK (Software Development Kit)

**Android SDK** (Software Development Kit) is set of tools, libraries and interfaces (API), which allow development, compilation and testing of Android applications.  

SDK is inseparable from Android Studio and is managed and updated by **SDK Manager**.

### Android SDK components:

1. **SDK Tools**  
   - Basic commands and utilities for work with the Android ecosystem.  
   - Includes `sdkmanager` (package installation and management), `avdmanager` (virtual device creation) and `adb` (Android Debug Bridge – a tool for device communication).  

2. **Platform Tools**  
   - Contains tools specific to a particular Android version.  
   - Examples: `adb`, `fastboot`, `logcat` etc., used in debugging and log analysis.  

3. **Build Tools**  
   - Used in compilation and application creation.  
   - Examples:  
     - `aapt` (Android Asset Packaging Tool) – packs resources into APK.  
     - `aidl` (Android Interface Definition Language) – generates code for process communication.  
     - `zipalign` – optimizes APK files for better performance.  

4. **Android Platform SDK**  
   - Includes libraries and API-s for given Android version.  
   - Allows developer to use functionalities introduced in a specific version of the operating system.  

5. **System Images**  
   - Necessary for virtual device creation in the emulator.  
   - They can be ARM, x86, or x86_64 based and include different versions of Android (e.g. Android 14, Android 15, etc.).  

![Фигура 2 – SDK Manager в Android Studio](images/sdk_manager.png)  
*Figure 2. „SDK Manager“ view in Android Studio, showing Android SDK versions available and installed*

---

## 3. Android Emulator

**Android Emulator** is a tool that provides a virtual environment for running Android applications.
It supports full testing and simulation of real devices, without physical hardware.

### Advantages:
- Allows running applications on **different versions of Android** (from API 21 to the latest).  
- Simulates different **screen sizes**, **resolutions**, **RAM** and **hardware specifications**.  
- Supports **GPS coordinates**, **sensors**, **camera**, **incoming calls** and **SMS**.  
- Supports **hardware virtualization** (Intel HAXM or Hypervisor Framework) for better performance.  
- Integrates directly with Android Studio for **debugging and profiling** of applications.  

### AVD Manager:
AVD Manager (Android Virtual Device Manager) is a tool for creating and managing emulators.
It allows you to select:
- **Device type** (phone, tablet, TV, watch).  
- **Android version (API level)**.  
- **System Image** and **architecture**.  
- **Hardware parameters** – RAM, GPU, orientation, external storage, etc.

![Фигура 3 – Прозорецът на AVD Manager с налични емулатори](images/avd_manager.png)  
*Figure 3. AVD Manager view with configured Android emulators*

Once the emulator is started, it behaves like a real device:
- Can be unlocked, launched apps, taken screenshots and recorded videos.  
- Supports drag-and-drop installation of APK files.  
- Allows use of **Android Debug Bridge (adb)** for installation and log analysis.  

![Фигура 4 – Стартиран емулатор с Android устройство](images/emulator_running.png)  
*Figure 4. Example of running Android Emulator with application in debug mode*

---

## 4. Dependency between Android Studio, SDK and Emulator

Android Studio, Android SDK, and Emulator work as interconnected components in a common development ecosystem.
Their roles can be described as follows:

| Component | Role in development process |
|------------|-------------------------------|
| **Android Studio** | Provides graphical and programming environment for application development. Manages the entire lifecycle – from project creation to APK/AAB file generation. |
| **Android SDK** | Contains the tools and libraries that compile code and provide access to the Android API. |
| **Android Emulator** | Simulates a real device for testing and debugging applications under development. |

### Sample workflow:
1. Developer creates new project in **Android Studio**.  
2. IDE uses **Gradle** and **Android SDK** for project compilation.  
3. The generated APK is automatically installed in **Emulator** or real device via `adb`.  
4. The application is launched, tested and debugged directly from the IDE.  

![Фигура 5 – Взаимодействие между Android Studio, SDK и Emulator](images/android_components_diagram.png)  
*Figure 5. Communication between Android Studio, SDK and Emulator in development process*

---

Android project
1. Android project structure with Kotlin and Jetpack Compose

Every Android project created with Android Studio has a clearly defined structure that separates source code, resources, and configuration files.
When using Kotlin, Jetpack Compose, and the Gradle Kotlin DSL, the project is built according to modern standards, with a declarative interface and a modular build process..

2. Basic structure of an Android project

After creating a new project with the Empty Compose Activity template, the structure usually looks like this:

```xml
MyComposeApp/
│
├── app/                                 # Main application module
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/                    # Kotlin source code
│   │   │   │   └── com/example/mycomposeapp/
│   │   │   │       └── MainActivity.kt
│   │   │   ├── res/                     # Resources (UI, icons, texts)
│   │   │   │   ├── drawable/            # Images and forms
│   │   │   │   ├── layout/              # XML layouts (not used in Compose)
│   │   │   │   ├── values/              # Colors, strings, themes
│   │   │   │   └── mipmap/              # Application icons
│   │   │   └── AndroidManifest.xml      # Application manifest file
│   │   └── test/                        # Unit and instrumented tests
│   ├── build.gradle.kts                 # Module configuration (Gradle Kotlin DSL)
│   └── proguard-rules.pro               # Obfuscation rules for release build
│
├── build.gradle.kts                     # Main Gradle file for the project
├── settings.gradle.kts                  # Module registration
├── gradle.properties                    # Gradle Global Settings
├── local.properties                     # Locale settings (path to SDK)
└── gradle/
    └── wrapper/
        ├── gradle-wrapper.jar
        └── gradle-wrapper.properties
```

3. AndroidManifest.xml

The AndroidManifest.xml file is the main configuration document of every Android application.
It defines:

application structure;

which activities, services, receivers and permissions are used;

which activity starts first (Launcher Activity);

what resources and libraries are associated with the application.

AndroidManifest.xml example

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.mycomposeapp">

    <!-- Declaring permissions (permissions) -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.MyComposeApp">

        <!-- Main (launcher) activity -->
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="@string/app_name">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

    </application>

</manifest>
```
Elements in Manifest file:

| Tag                 | Description                                                                                            |
| ------------------- | ------------------------------------------------------------------------------------------------------ |
| `<manifest>`        | A main container that defines the package and content of the application.                              |
| `<uses-permission>` | Declares permissions required by the application (e.g. internet access, location, camera, etc.).       |
| `<application>`     | Describes global settings for the application — icon, theme, resources, whether it supports RTL, etc.  |
| `<activity>`        | Defines the Activity classes (screens) of the application.                                             |
| `<intent-filter>`   | Defines how an Activity can be launched – e.g. as a Launcher or via another Intent.                    |


Important: On Android 12+, all Activities, Services, and Broadcast Receivers that can be launched from outside must have android:exported="true".

4. Gradle configuration (Kotlin DSL)
4.1. settings.gradle.kts

Specifies the project name and included modules.

rootProject.name = "MyComposeApp"
include(":app")

4.2. build.gradle.kts (project level)

This file contains plugins and global dependencies applicable to all modules..

plugins {
    id("com.android.application") version "8.5.0" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}

4.3. app/build.gradle.kts (module level)

The main Gradle file of the application written in Kotlin DSL.

```
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.mycomposeapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mycomposeapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.5")
    implementation("androidx.activity:activity-compose:1.9.1")

    // Jetpack Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    testImplementation("junit:junit:4.13.2")
}

```

Note: Gradle Kotlin DSL provides stronger typing and smart hints in Android Studio compared to the classic Groovy syntax.

5. MainActivity.kt

The MainActivity.kt file represents the main entry point of the application.
It inherits the ComponentActivity class and uses Jetpack Compose to declaratively build the user interface..

```
package com.example.mycomposeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.mycomposeapp.ui.theme.MyComposeAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyComposeAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
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
        Text(text = "Добре дошли в MyComposeApp!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { /* TODO: добави действие */ }) {
            Text("Натисни ме")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingScreenPreview() {
    MyComposeAppTheme {
        GreetingScreen()
    }
}

```

Main points:

setContent {} – defines the visual content of the screen with Compose.

@Composable – annotation that marks functions that build UI components.

Preview – allows interface visualization directly in Android Studio.

MaterialTheme – sets color scheme, fonts, and shapes defined in the theme.

6. Resource files (res/)

Directory res/ contains all static resources for an application:

| Subdirectory             | Description                                                 |
| ------------------------ | ----------------------------------------------------------- |
| `drawable/`              | Images, SVG shapes, and XML resources for graphics.         |
| `mipmap/`                | Icons with different resolutions (launcher icons).          |
| `values/strings.xml`     | Text strings used in the application.                       |
| `values/colors.xml`      | Color values ​​used in the theme.                             |
| `values/themes.xml`      | Defines the visual theme (dark/light, colors, fonts).       |

7. Interconnection between components

AndroidManifest.xml – describes the structure and permissions of the application.

Gradle Kotlin DSL – controls the compilation process, dependencies, and Compose configurations.

MainActivity.kt – defines the logic and interface of the application.

res/ – contains visual and textual resources.

Android Studio – coordinates everything via Gradle and generates a ready-made .apk or .aab file.







**Task**

Create a simple Android app with Kotlin and Jetpack Compose that: renders a title on the screen; loads and displays an image from the web.

*Required dependencies*

In the app/build.gradle.kts file, add the following dependencies:

implementation("io.coil-kt:coil-compose:2.6.0")

*Permissions in AndroidManifest.xml*

Add the Internet access permission so that the app can load images from the web:

Main image loading function:

val url = https://i.imgur.com/DvpvklR.png

Image( painter = rememberAsyncImagePainter(model = url), contentDescription = "Интернет изображение", modifier = Modifier .size(300.dp) .padding(8.dp), contentScale = ContentScale.Crop )

Фигура 1. Диаграма на взаимодействието между Manifest, Gradle, Compose и ресурсите.
