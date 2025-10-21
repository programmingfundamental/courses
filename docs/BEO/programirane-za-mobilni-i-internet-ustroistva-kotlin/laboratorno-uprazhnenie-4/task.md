---
layout: default
title: Задача
parent: Лабораторно упражнение 4
grand_parent: Програмиране за мобилни и Интернет устройства Kotlin
nav_order: 2
---

# Задачата

Да се създаде просто Android приложение с Kotlin и Jetpack Compose, което:
визуализира заглавие на екрана;
зарежда и показва изображение от интернет.

2. Необходими зависимости

Във файла app/build.gradle.kts добавете следните зависимости:

implementation("io.coil-kt:coil-compose:2.6.0")

3. Разрешения в AndroidManifest.xml

Добавете разрешение за достъп до интернет, за да може приложението да зарежда изображения от уеб:

 <uses-permission android:name="android.permission.INTERNET" />

 4. Главен функция за зареждане на изображение:

 val url = https://i.imgur.com/DvpvklR.png

 Image(
        painter = rememberAsyncImagePainter(model = url),
        contentDescription = "Интернет изображение",
        modifier = Modifier
            .size(300.dp)
            .padding(8.dp),
        contentScale = ContentScale.Crop
    )

