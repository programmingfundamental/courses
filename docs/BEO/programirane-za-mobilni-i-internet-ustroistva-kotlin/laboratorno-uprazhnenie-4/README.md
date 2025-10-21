---
layout: default
title: Лабораторно упражнение 4
parent: Програмиране за мобилни и Интернет устройства Kotlin
has_children: true
nav_order: 4
permalink: /docs/programirane-za-mobilni-i-internet-ustroistva-kotlin/laboratorno-uprazhnenie-4
---

# Лабораторно упражнение 4

# Теоретична част

## 1. Среда за разработване Android Studio

**Android Studio** е официалната интегрирана среда за разработка (IDE) на Google за създаване на Android приложения.  
Тя е базирана на **IntelliJ IDEA** и предлага всички необходими инструменти за цялостния процес на разработка – от писане на код и дизайн на интерфейси до тестване, дебъгване и публикуване на приложения в Google Play.

### Основни характеристики на Android Studio:

1. **Интелигентен редактор на код (Code Editor)**  
   - Поддържа **Java**, **Kotlin** и **C++**.  
   - Осигурява **автоматично довършване**, **рефакторинг**, **синтактична проверка** и **предложения в реално време**.  
   - Има вградена система за анализ на качеството на кода (Lint), която открива потенциални грешки и неефективен код.  

2. **Layout Editor**  
   - Визуален инструмент за изграждане на потребителски интерфейси чрез **drag-and-drop**.  
   - Позволява превключване между **Design** и **Code** изглед.  
   - Поддържа **ConstraintLayout**, който осигурява гъвкаво позициониране на елементите и адаптивност към различни размери на екрана.

3. **Gradle система за билдване**  
   - Използва се за **автоматизация на компилацията** и **управление на зависимости**.  
   - Позволява конфигурация на различни **build вариации** (debug, release) и **product flavors**.  
   - Дава възможност за лесна интеграция на външни библиотеки от Maven Central или Google Maven Repository.  

4. **Интеграция с Git и GitHub**  
   - Вградена поддръжка за **версионен контрол**.  
   - Може да клонира, комитва, пушва и създава pull request-и директно от IDE-то.  

5. **Вграден Android Emulator и Device Manager**  
   - Позволява стартиране на приложения в симулирана Android среда.  
   - Поддържа различни устройства, екрани и версии на Android.  
   - Осигурява бързо превключване между емулатори и реални устройства, свързани чрез USB или Wi-Fi.  

![Фигура 1 – Начален екран на Android Studio с отворен проект](images/studio_main.png)  
*Фигура 1. Основен изглед на Android Studio с активен Layout Editor.*

---

## 2. Android SDK (Software Development Kit)

**Android SDK** (Software Development Kit) представлява съвкупност от инструменти, библиотеки и интерфейси (API), които позволяват на разработчиците да създават, компилират и тестват Android приложения.  

SDK е неразделна част от Android Studio и може да се актуализира и управлява чрез **SDK Manager**.

### Основни компоненти на Android SDK:

1. **SDK Tools**  
   - Основни команди и помощни програми за работа с Android екосистемата.  
   - Включва `sdkmanager` (инсталация и управление на пакети), `avdmanager` (създаване на виртуални устройства) и `adb` (Android Debug Bridge – инструмент за комуникация с устройства).  

2. **Platform Tools**  
   - Съдържат инструменти, специфични за дадена версия на Android.  
   - Например: `adb`, `fastboot`, `logcat` и други, използвани при отстраняване на грешки и анализ на логове.  

3. **Build Tools**  
   - Използват се при процеса на компилация и изграждане на приложенията.  
   - Примери:  
     - `aapt` (Android Asset Packaging Tool) – пакетира ресурси в APK.  
     - `aidl` (Android Interface Definition Language) – генерира код за комуникация между процеси.  
     - `zipalign` – оптимизира APK файловете за по-добра производителност.  

4. **Android Platform SDK**  
   - Включва библиотеки и API-та за конкретна версия на Android.  
   - Позволява на разработчика да използва функционалности, въведени в определена версия на операционната система.  

5. **System Images**  
   - Необходими за създаване на виртуални устройства в емулатора.  
   - Могат да бъдат ARM, x86 или x86_64 базирани и включват различни версии на Android (например Android 14, Android 15 и т.н.).  

![Фигура 2 – SDK Manager в Android Studio](images/sdk_manager.png)  
*Фигура 2. Прозорецът „SDK Manager“ в Android Studio, показващ наличните и инсталираните версии на Android SDK.*

---

## 3. Android Emulator

**Android Emulator** е инструмент, който предоставя виртуална среда за изпълнение на Android приложения.  
Той позволява пълно тестване и симулация на реални устройства, без физически хардуер.

### Предимства и възможности:
- Позволява стартиране на приложения върху **различни версии на Android** (от API 21 до последните).  
- Симулира различни **размери на екрана**, **резолюции**, **RAM** и **хардуерни характеристики**.  
- Поддържа **GPS координати**, **сензори**, **камера**, **входящи повиквания** и **SMS**.  
- Поддържа **хардверна виртуализация** (Intel HAXM или Hypervisor Framework) за по-бърза работа.  
- Интегрира се директно с Android Studio за **дебъгване и профилиране** на приложения.  

### Управление чрез AVD Manager:
AVD Manager (Android Virtual Device Manager) е инструмент за създаване и управление на емулатори.  
Той позволява избор на:
- **Тип устройство** (телефон, таблет, телевизор, часовник).  
- **Версия на Android (API level)**.  
- **System Image** и **архитектура**.  
- **Хардуерни параметри** – RAM, GPU, ориентация, външна памет и др.

![Фигура 3 – Прозорецът на AVD Manager с налични емулатори](images/avd_manager.png)  
*Фигура 3. Прозорецът на AVD Manager с конфигурирани Android емулатори.*

След като емулаторът е стартиран, той се държи като реално устройство:
- Може да се отключва, стартира приложения, правят се снимки на екрана и видеозаписи.  
- Поддържа drag-and-drop инсталация на APK файлове.  
- Позволява използване на **Android Debug Bridge (adb)** за инсталация и лог анализ.  

![Фигура 4 – Стартиран емулатор с Android устройство](images/emulator_running.png)  
*Фигура 4. Пример за стартиран Android Emulator с приложение в режим на дебъгване.*

---

## 4. Връзка между Android Studio, SDK и Emulator

Android Studio, Android SDK и Emulator работят като взаимосвързани компоненти в обща екосистема за разработка.  
Техните роли могат да бъдат описани така:

| Компонент | Роля в процеса на разработка |
|------------|-------------------------------|
| **Android Studio** | Осигурява графична и програмна среда за разработка на приложения. Управлява целия жизнен цикъл – от създаването на проект до генериране на APK/AAB файл. |
| **Android SDK** | Съдържа инструментите и библиотеките, чрез които се компилира кодът и се осигурява достъп до Android API. |
| **Android Emulator** | Симулира реално устройство за тестване и отстраняване на грешки в разработваните приложения. |

### Примерен работен поток:
1. Разработчикът създава нов проект в **Android Studio**.  
2. IDE-то използва **Gradle** и **Android SDK** за компилация на проекта.  
3. Генерираният APK се инсталира автоматично в **Emulator** или реално устройство чрез `adb`.  
4. Приложението се стартира, тества и дебъгва директно от IDE-то.  

![Фигура 5 – Взаимодействие между Android Studio, SDK и Emulator](images/android_components_diagram.png)  
*Фигура 5. Взаимодействие между Android Studio, SDK и Emulator в процеса на разработка.*

---

Теоретична част
1. Структура на Android проект с Kotlin и Jetpack Compose

Всеки Android проект, създаден с Android Studio, има ясно дефинирана структура, която разделя сорс кода, ресурсите и конфигурационните файлове.
Когато използваме Kotlin, Jetpack Compose и Gradle Kotlin DSL, проектът се изгражда по съвременни стандарти, с декларативен интерфейс и модулен билд процес.

2. Основна структура на Android проект

След създаване на нов проект с шаблон Empty Compose Activity, структурата обикновено изглежда така:

```xml
MyComposeApp/
│
├── app/                                 # Основен модул на приложението
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/                    # Kotlin сорс код
│   │   │   │   └── com/example/mycomposeapp/
│   │   │   │       └── MainActivity.kt
│   │   │   ├── res/                     # Ресурси (UI, икони, текстове)
│   │   │   │   ├── drawable/            # Изображения и форми
│   │   │   │   ├── layout/              # XML layout-и (не се използват при Compose)
│   │   │   │   ├── values/              # Цветове, низове, теми
│   │   │   │   └── mipmap/              # Икони на приложението
│   │   │   └── AndroidManifest.xml      # Манифест файлът на приложението
│   │   └── test/                        # Unit и instrumented тестове
│   ├── build.gradle.kts                 # Конфигурация на модула (Gradle Kotlin DSL)
│   └── proguard-rules.pro               # Правила за обфускация при release билд
│
├── build.gradle.kts                     # Главен Gradle файл за проекта
├── settings.gradle.kts                  # Регистрация на модулите
├── gradle.properties                    # Глобални настройки на Gradle
├── local.properties                     # Локални настройки (път до SDK)
└── gradle/
    └── wrapper/
        ├── gradle-wrapper.jar
        └── gradle-wrapper.properties
```

3. AndroidManifest.xml

Файлът AndroidManifest.xml е основният конфигурационен документ на всяко Android приложение.
Той определя:

структурата на приложението;

кои activities, services, receivers и permissions се използват;

коя активност стартира първа (Launcher Activity);

какви ресурси и библиотеки са свързани с приложението.

Примерен AndroidManifest.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.mycomposeapp">

    <!-- Деклариране на разрешения (permissions) -->
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

        <!-- Основна (launcher) активност -->
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
Основни елементи в Manifest файла:

| Таг                 | Описание                                                                                               |
| ------------------- | ------------------------------------------------------------------------------------------------------ |
| `<manifest>`        | Главен контейнер, който дефинира пакета и съдържанието на приложението.                                |
| `<uses-permission>` | Декларира разрешения, необходими на приложението (например достъп до интернет, локация, камера и др.). |
| `<application>`     | Описва глобални настройки за приложението — икона, тема, ресурси, дали поддържа RTL и др.              |
| `<activity>`        | Определя Activity класовете (екраните) на приложението.                                                |
| `<intent-filter>`   | Дефинира как дадена Activity може да бъде стартирана – напр. като Launcher или чрез друг Intent.       |


Важно: При Android 12+ всички Activities, Services и Broadcast Receivers, които могат да се стартират отвън, трябва да имат android:exported="true".

4. Gradle конфигурация (Kotlin DSL)
4.1. settings.gradle.kts

Определя името на проекта и включените модули.

rootProject.name = "MyComposeApp"
include(":app")

4.2. build.gradle.kts (проектно ниво)

Този файл съдържа плъгини и глобални зависимости, приложими за всички модули.

plugins {
    id("com.android.application") version "8.5.0" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}

4.3. app/build.gradle.kts (модулно ниво)

Основният Gradle файл на приложението, написан с Kotlin DSL.

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

Забележка: Gradle Kotlin DSL осигурява по-строга типизация и интелигентни подсказки в Android Studio в сравнение с класическия Groovy синтаксис.

5. MainActivity.kt

Файлът MainActivity.kt представлява основната входна точка на приложението.
Той наследява класа ComponentActivity и използва Jetpack Compose за декларативно изграждане на потребителския интерфейс.

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

Основни елементи:

setContent {} – дефинира визуалното съдържание на екрана с Compose.

@Composable – анотация, която маркира функции, изграждащи UI компоненти.

Preview – позволява визуализация на интерфейса директно в Android Studio.

MaterialTheme – задава цветова схема, шрифтове и форми, дефинирани в темата.

6. Ресурсни файлове (res/)

Папката res/ съдържа всички статични ресурси на приложението:

| Подпапка             | Описание                                                    |
| -------------------- | ----------------------------------------------------------- |
| `drawable/`          | Изображения, SVG форми и XML ресурси за графики.            |
| `mipmap/`            | Икони с различна резолюция (launcher icons).                |
| `values/strings.xml` | Текстови низове, използвани в приложението.                 |
| `values/colors.xml`  | Цветови стойности, използвани в темата.                     |
| `values/themes.xml`  | Дефинира визуалната тема (тъмна/светла, цветове, шрифтове). |

7. Взаимовръзка между компонентите

AndroidManifest.xml – описва структурата и разрешенията на приложението.

Gradle Kotlin DSL – контролира процеса на компилация, зависимости и Compose конфигурации.

MainActivity.kt – дефинира логиката и интерфейса на приложението.

res/ – съдържа визуални и текстови ресурси.

Android Studio – координира всичко чрез Gradle и генерира готов .apk или .aab файл.


Фигура 1. Диаграма на взаимодействието между Manifest, Gradle, Compose и ресурсите.

