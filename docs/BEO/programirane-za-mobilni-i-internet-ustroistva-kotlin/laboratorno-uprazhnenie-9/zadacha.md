---
layout: default
title: Задача
parent: Лабораторно упражнение 9
grand_parent: Програмиране за мобилни и Интернет устройства Kotlin
nav_order: 2
---

Задание:

Да се създаде приложение, което извежда в изображение и текст като използвате различни List и Grid

1.	Създайте стрингови ресурси, които ще се използвате за текст към изображението – файл lab9_strings

2.	Добавете изображенията от lab9_images.zip към вашия проект – res/drawable

3.	Създайте клас Place с атрибути:
-	 @StringRes val stringResourceId: Int,
-	 @DrawableRes val drawableResourceId: Int

4.	Създайте клас и метод, който връща списък с Places.

5.	Създайте функция @Composable fun PlaceApp()

6.	Създайте функция @Composable fun PlaceCard(place: Place, modifier: Modifier = Modifier), която извежда в Card изображение(Image) и текст (Text)

7.	Създайте функция @Composable fun PlaceColumn(places: List<Place>, modifier: Modifier = Modifier), която вика PlaceCard() в LazyColumn на зелен фон.

8.	Създайте функция  fun PlaceRow(places: List<Place>, modifier: Modifier = Modifier) , която вика PlaceCard() в LazyRow на син фон

9.	Създайте функция @Composable fun PlaceVerticalGrid(places: List<Place>, modifier: Modifier = Modifier) , която вика PlaceCard() в LazyVerticalGrid на лилав фон

10.	Създайте функция @Composable fun PlaceHorizontalGrid(places: List<Place>, modifier: Modifier = Modifier) , която вика PlaceCard() в LazyHorizontalGrid на лилав фон
