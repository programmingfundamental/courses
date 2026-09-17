---
layout: default
title: Лабораторно упражнение 4
parent: Програмиране за мобилни и Интернет устройства Kotlin
has_children: true
nav_order: 4
---

# Лабораторно упражнение 4

## Среда за разработване Android Studio

Android Studio е интегрирана среда за разработване на Android приложения, базирана на IntelliJ IDEA. Тя предоставя редактор на код, инструменти за изграждане на интерфейс, компилация, тестване и отстраняване на грешки.

Android интерфейс може да бъде изграден чрез XML базирани View компоненти или чрез Jetpack Compose. В настоящите упражнения се използва Jetpack Compose.

### Основни инструменти

| Инструмент | Предназначение |
| --- | --- |
| Code Editor | Редактиране на Kotlin, Java и C++ код, автоматично допълване, рефакториране и анализ с Lint. |
| Project | Преглед на модулите, изходния код, ресурсите и конфигурационните файлове. Android изгледът групира файловете логически, а Project показва директориите. |
| Layout Editor | Визуален редактор за XML оформления с View компоненти, включително `ConstraintLayout`. Това е отделен подход от Compose. |
| Compose Preview | Предварителен преглед на композируеми функции чрез `@Preview`, без стартиране на цялото приложение. |
| Resource Manager | Преглед и добавяне на ресурси, например изображения. |
| Gradle | Автоматизира компилацията, обработката на ресурсите и управлението на зависимостите. Поддържа варианти за изграждане, например debug и release. |
| Device Manager | Създаване и управление на виртуални устройства (AVD) и преглед на свързаните устройства. |
| Android Emulator | Изпълнение на Android на виртуално устройство. |
| Logcat | Преглед и филтриране на системни съобщения и съобщения от приложението, включително изключения. |

Android Studio има интеграция с Git за проследяване на промени, клониране на хранилища и работа с отдалечени хранилища. Зависимостите на Android проект обикновено се получават чрез Google Maven и Maven Central.

## Android SDK

Android SDK (Software Development Kit) включва библиотеки и инструменти за компилиране и тестване на приложения. Пакетите се управляват чрез **SDK Manager**.

| Компонент | Предназначение |
| --- | --- |
| Command-line Tools | `sdkmanager` управлява SDK пакети, а `avdmanager` — виртуални устройства. |
| Platform Tools | Включват `adb` и `fastboot`. `adb` осигурява връзка с устройство, инсталиране на приложения и достъп до Logcat. Тези инструменти не са ограничени до една Android версия. |
| Build Tools | Инструменти за изграждане на приложението: например AAPT2 обработва ресурси, `aidl` генерира код за комуникация между процеси, а `zipalign` подравнява данните в APK. |
| SDK Platform | Android API библиотеката за конкретно API ниво, използвана при компилация. |
| System Images | Системни образи за виртуалните устройства, съобразени с Android версията и архитектурата на машината. |

## Android Emulator и Device Manager

Емулаторът позволява проверка на приложения при различни размери на екрана, версии на Android и конфигурации на устройството. Поддържа симулиране на местоположение, сензори, камера, повиквания и SMS според избрания образ. Хардуерното ускорение зависи от операционната система и наличната виртуализация. Проверката на емулатор не замества всички проверки на физическо устройство.

В **Device Manager** се създава Android Virtual Device (AVD), като се избират:

- тип устройство — телефон, таблет, телевизор или часовник;
- системен образ и API ниво;
- съвместима архитектура;
- хардуерни настройки, например памет и графично ускорение.

В по-стари версии този инструмент се среща като **AVD Manager**. След стартиране на емулатора приложението може да се инсталира от Android Studio или чрез `adb`. Поддържат се също инсталиране на APK чрез плъзгане във прозореца, снимки на екрана и видеозапис.

## Връзка между Android Studio, SDK и Emulator

| Компонент | Роля |
| --- | --- |
| Android Studio | Организира работата по проекта и стартира инструментите за изграждане и проверка. |
| Android SDK | Предоставя Android API и инструментите за изграждане и комуникация с устройства. |
| Android Emulator | Изпълнява приложението във виртуална Android среда. |

Примерен работен поток:

1. В Android Studio се създава проект.
2. Gradle използва Android SDK, за да изгради приложението.
3. APK се инсталира на избрания емулатор или физическо устройство.
4. Приложението се стартира и резултатът се проверява, включително чрез Logcat.

## Структура на Android проект с Kotlin и Jetpack Compose

Проектът разделя изходния код, ресурсите и конфигурацията. При създаване на проект се избира шаблон за Compose, например **Empty Activity**. Имената на шаблоните може да се различават между версиите на Android Studio.

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
│   │   ├── test/                 # Локални тестове
│   │   └── androidTest/          # Тестове на Android устройство
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── local.properties
└── gradle/
    ├── libs.versions.toml        # Ако се използва version catalog
    └── wrapper/
        ├── gradle-wrapper.jar
        └── gradle-wrapper.properties
```

Kotlin файловете могат да са в директория `java` или `kotlin` според проекта. Папката `res/layout` е нужна за XML оформления; Compose не изисква XML файл за всеки екран, но двата подхода могат да съществуват в едно приложение.

## `AndroidManifest.xml`

Манифестът описва компонентите на приложението, разрешенията и входната `Activity`. Следният пример предполага пакет `com.example.mycomposeapp` и генерирани ресурси `app_name`, `ic_launcher` и `Theme.MyComposeApp`. Имената се съобразяват с конкретния проект.

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

| Елемент | Описание |
| --- | --- |
| `<manifest>` | Коренов елемент на манифеста. В съвременния Gradle проект `namespace` и `applicationId` се задават в конфигурацията на модула. |
| `<uses-permission>` | Декларира разрешение, необходимо за използваната функционалност. |
| `<application>` | Общи настройки: икона, име, тема и други ресурси. |
| `<activity>` | Декларира клас `Activity`. |
| `<intent-filter>` | Описва какви намерения (`Intent`) може да обработва компонентът; тук определя входната `Activity`. |

При приложения, насочени към Android 12 или по-нова версия, компонентите с `intent-filter` трябва изрично да задават `android:exported`. За входната `Activity` стойността е `true`, за да може системният launcher да я стартира. Останалите компоненти не трябва автоматично да получават `true`.

## Gradle конфигурация (Kotlin DSL)

В това хранилище няма примерен Android проект или version catalog, от който да се вземе съгласуван набор версии. Затова се запазва конфигурацията, генерирана от Compose шаблона на Android Studio, вместо да се копира отделен списък с фиксирани версии.

`settings.gradle.kts` определя името и модулите. Следният фрагмент се намира след генерираните настройки за плъгини и хранилища:

```kotlin
rootProject.name = "MyComposeApp"
include(":app")
```

Проектният `build.gradle.kts` декларира общите плъгини, а `app/build.gradle.kts` прилага нужните за приложението. При Kotlin 2.0 и по-нова версия се използва Compose compiler plugin `org.jetbrains.kotlin.plugin.compose` със същата версия като Kotlin. Ако проектът използва version catalog, версията се взема от неговия запис за Kotlin. Не се добавя старият `composeOptions.kotlinCompilerExtensionVersion = "1.5.1"` към такава конфигурация. [Официални указания за Compose compiler plugin](https://developer.android.com/develop/ui/compose/setup-compose-dependencies-and-compiler).

Кратък фрагмент от `app/build.gradle.kts`, когато версията на плъгина вече е определена на проектно ниво:

```kotlin
plugins {
    // Останалите генерирани плъгини се запазват.
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    buildFeatures {
        compose = true
    }
}
```

Ако същият плъгин вече е приложен чрез `alias(...)`, не се добавя втори път. Запазват се генерираните зависимости за `activity-compose`, Compose BOM, Material 3, `ui-tooling-preview` и `ui-tooling`. Compose BOM съгласува версиите на Compose библиотеките; той не определя версията на Kotlin или на Compose compiler plugin. След промяна се изпълнява **Sync Project with Gradle Files**.

## Първи Compose интерфейс — `MainActivity.kt`

Класът `MainActivity` наследява `ComponentActivity`. В `onCreate()` се извиква `setContent`, който задава Compose съдържанието. Примерът използва Material 3 и пакет `com.example.mycomposeapp`; името на пакета се съобразява с проекта.

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
        Text("Добре дошли в MyComposeApp!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { /* Демонстрационният бутон няма действие. */ }) {
            Text("Бутон")
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

`@Composable` означава композируема функция. `Text` визуализира текст, а `Modifier` задава например размер и отстояния. `MaterialTheme` предоставя цветове, типография и форми. В реалния проект може да се използва генерираната функция за тема, която конфигурира `MaterialTheme`. `@Preview` показва интерфейса в Android Studio.

## Ресурсни файлове

| Директория или файл | Съдържание |
| --- | --- |
| `res/drawable/` | Растерни изображения и XML графични ресурси, включително vector drawable. SVG може да се импортира чрез Vector Asset, когато е поддържан. |
| `res/mipmap/` | Икони за стартиране на приложението. |
| `res/values/strings.xml` | Текстови ресурси. |
| `res/values/colors.xml` | Цветови ресурси, когато са необходими. |
| `res/values/themes.xml` | Android темата на приложението; Compose темата обикновено се конфигурира и чрез Kotlin код. |

Манифестът описва компонентите, Gradle управлява изграждането и зависимостите, `MainActivity.kt` задава интерфейса, а `res` съдържа ресурсите. Android Studio обединява работата с тези файлове и стартира изграждането на APK или Android App Bundle (AAB).
