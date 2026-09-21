---
title: Задачи
sidebar:
  order: 2
---

# Задачи

Да се създаде Android приложение с Kotlin и Jetpack Compose, което визуализира заглавие и изображение, заредено от интернет.

## 1. Compose проект

Да се създаде или отвори проект с Compose и Material 3. Да се запази генерираната Gradle конфигурация, описана в [основната страница](/courses/bg/programirane-za-mobilni-i-internet-ustroistva-kotlin/laboratorno-uprazhnenie-4/).

## 2. Зависимости за изображението

В `app/build.gradle.kts`, в блока `dependencies`, да се добавят зависимостите за Coil Compose и за зареждане от мрежата. Хранилището не задава версия на Coil. Примерът използва `3.6.2`, посочена в [официалните указания на Coil](https://coil-kt.github.io/coil/getting_started/) при проверката на 17.09.2026 г. Двата артефакта трябва да са с една и съща версия; при използване на version catalog тя се задава там.

```kotlin
dependencies {
    implementation("io.coil-kt.coil3:coil-compose:3.6.2")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.6.2")
}
```

## 3. Gradle sync

Да се изпълни **Sync Project with Gradle Files**. Преди следващата стъпка да се провери дали зависимостите са заредени успешно.

## 4. Разрешение за интернет

В `app/src/main/AndroidManifest.xml` да се добави следният елемент непосредствено в `<manifest>`, извън и преди `<application>`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## 5. URL стойност

Адресът да се запише като `String` в композируемата функция:

```kotlin
val imageUrl = "https://i.imgur.com/DvpvklR.png"
```

## 6. Композируема функция

Да се създаде функция `InternetImageScreen()` с `@Composable` и `Column`. Тя да бъде извикана в `setContent` на `MainActivity`.

## 7. Заглавие

В `Column` да се добави `Text` със заглавие „Изображение от интернет“.

## 8. Изображение

След заглавието да се добави `AsyncImage` с URL стойността, описание, подходящ размер и `ContentScale.Crop`. Пълният пример за `MainActivity.kt` е даден по-долу. Редът `package` се съобразява с пакета на проекта.

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
            text = "Изображение от интернет",
            style = MaterialTheme.typography.headlineSmall
        )
        AsyncImage(
            model = imageUrl,
            contentDescription = "Изображение, заредено от интернет",
            modifier = Modifier.fillMaxWidth().height(300.dp),
            contentScale = ContentScale.Crop
        )
    }
}
```

## 9. Стартиране

Приложението да се стартира на емулатор или физическо устройство с достъп до интернет.

## 10. Проверка

Да се провери дали заглавието и изображението се визуализират и дали изображението заема зададената област. При неуспех да се проверят адресът, интернет връзката, разрешението и съобщенията в Logcat.

Зареждането на мрежов ресурс не е надежден тест в Compose Preview, където мрежовият достъп е ограничен. Реалното зареждане се проверява на емулатор или устройство. [Документация на Coil за Preview](https://coil-kt.github.io/coil/compose/#previews).

## Задачи за самостоятелна работа

Работете в собствен Android проект с Kotlin и Jetpack Compose. Използвайте генерираната конфигурация на проекта.

### Задача 1. Първо приложение

Създайте приложение „Моята студентска карта“. Изнесете интерфейса в композируема функция `StudentCardScreen()` и я извикайте от `setContent`. Покажете име, специалност и курс с примерни данни.

Стартирайте приложението на емулатор или физическо устройство и проверете дали се виждат всички надписи.

### Задача 2. Ресурси и Preview

Запишете името на приложението и етикетите на картата в `strings.xml`. Добавете собствено локално изображение в `res/drawable` и го покажете с описание. Изображението може да бъде създадена от вас PNG икона.

Създайте Preview на картата. Променете един текстов ресурс и проверете резултата както в Preview, така и в стартираното приложение.

### Задача 3. Ориентиране в проекта

Съставете таблица с пътищата до манифеста, Kotlin файла с интерфейса, текстовите ресурси и Gradle файла на модула `app`. За всеки файл посочете предназначението му.

В собствен текстов ресурс временно премахнете затварящия XML таг. Стартирайте изграждането, запишете как съобщението насочва към грешката, възстановете тага и изградете успешно проекта.

### Проверка и предаване

Предайте проекта, таблицата и снимка на работещото приложение. Приложете съобщението за XML грешката и резултата след поправката. Картата трябва да се показва и без интернет връзка.
