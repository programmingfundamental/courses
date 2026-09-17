---
layout: default
title: Задача
parent: Лабораторно упражнение 9
grand_parent: Програмиране за мобилни и Интернет устройства Kotlin
nav_order: 2
---

# Задача

Да се създаде приложение, което показва изображения и текст чрез различни списъци и решетки в Jetpack Compose.

1. Да се добавят текстовите ресурси от предоставения файл `lab9_strings` в `app/src/main/res/values/strings.xml`.
2. Изображенията от предоставения архив `lab9_images.zip` да се разархивират и добавят в `app/src/main/res/drawable`.

В хранилището не са налични `lab9_strings` и `lab9_images.zip` и няма предоставена връзка за изтегляне. За изпълнение с конкретните учебни ресурси е необходимо те да бъдат предоставени от преподавателя. Имената и съдържанието им не се предполагат.

## Модел и данни

3. Да се създаде клас за данни `Place` със следните свойства:

```kotlin
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Place(
    @StringRes val stringResourceId: Int,
    @DrawableRes val drawableResourceId: Int
)
```

4. Да се създаде клас с метод `loadPlaces(): List<Place>`, който връща списък от обекти `Place`, свързващи текстовите ресурси с изображенията. Да се използват действителните идентификатори от генерирания клас `R`.

## Композируеми функции

5. Да се създаде функция `PlaceApp()` с `@Composable`, която получава списъка и показва избраното оформление. Функцията да се извика от `setContent`.
6. Да се създаде функция `PlaceCard(place: Place, modifier: Modifier = Modifier)` с `@Composable`. В `Card` да се разположат `Image` и `Text`, като ресурсите се извлекат чрез `painterResource()` и `stringResource()`.
7. Да се реализира `PlaceColumn(places: List<Place>, modifier: Modifier = Modifier)` с `@Composable`, която извиква `PlaceCard()` за всеки елемент в `LazyColumn` на **зелен фон**.
8. Да се реализира `PlaceRow(places: List<Place>, modifier: Modifier = Modifier)` с `@Composable`, която извиква `PlaceCard()` за всеки елемент в `LazyRow` на **син фон**.
9. Да се реализира `PlaceVerticalGrid(places: List<Place>, modifier: Modifier = Modifier)` с `@Composable`, която използва `LazyVerticalGrid` на **лилав фон**. Да се зададе `columns` чрез `GridCells.Fixed` или `GridCells.Adaptive`.
10. Да се реализира `PlaceHorizontalGrid(places: List<Place>, modifier: Modifier = Modifier)` с `@Composable`, която използва `LazyHorizontalGrid` на **лилав фон**. Да се зададе `rows` и ограничена височина за хоризонталната решетка.

Параметърът `modifier` да се приложи към кореновия елемент на съответната функция. Да се проверят четирите оформления поотделно. При използване на ресурсен идентификатор като `key` да се гарантира, че той е уникален за всяко място в списъка.
