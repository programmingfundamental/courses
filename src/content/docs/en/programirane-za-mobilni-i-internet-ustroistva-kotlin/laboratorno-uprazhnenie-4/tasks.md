---
title: Tasks
taskPage: true
sidebar:
  label: Tasks
  order: 100
---
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
