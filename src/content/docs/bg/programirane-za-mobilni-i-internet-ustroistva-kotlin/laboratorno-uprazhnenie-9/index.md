---
title: Лабораторно упражнение 9
sidebar:
  order: 9
---

# Лабораторно упражнение 9

## Списъци и решетки в Jetpack Compose

Колекция от елементи може да се представи чрез `Column` или `Row`, когато броят е малък и не е необходимо отложено създаване. `Column` създава подаденото съдържание, включително елементите извън видимата област.

Следните примери за съобщения използват общ модел и композируема функция:

```kotlin
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

data class Message(val id: Long, val text: String)

@Composable
fun MessageRow(message: Message) {
    Text(message.text)
}

@Composable
fun MessageList(messages: List<Message>) {
    Column {
        messages.forEach { message ->
            MessageRow(message)
        }
    }
}
```

За вертикално превъртане към `Column` може да се добави `Modifier.verticalScroll(rememberScrollState())`, с импорти на `verticalScroll` и `rememberScrollState` от `androidx.compose.foundation` и `Modifier` от `androidx.compose.ui`. Това не превръща `Column` в Lazy компонент — съдържанието пак се създава изцяло.

## Списъци с отложено създаване на елементите (`LazyColumn` и `LazyRow`)

`LazyColumn` и `LazyRow` композират и подреждат необходимите елементи според видимата област и позицията на превъртане. Те са подходящи за голям или динамичен брой елементи. Възможно е предварително подготвяне на близки елементи, затова не се предполага, че се създават единствено видимите пиксели.

`LazyColumn` подрежда и превърта вертикално, а `LazyRow` — хоризонтално. Решетките използват съответно `LazyVerticalGrid` и `LazyHorizontalGrid`.

### `LazyListScope`

Блокът на Lazy списъка предоставя DSL — набор от функции за описание на съдържанието. В `LazyListScope` функцията `item` добавя един елемент, `items` — няколко елемента, а `itemsIndexed` предоставя и индекса.

```kotlin
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun NumberedList() {
    LazyColumn {
        item { Text("First item") }
        items(5) { index -> Text("Item: $index") }
        item { Text("Last item") }
    }
}
```

За обхождане на колекция се използва разширението `items`. Примерът използва `Message` и `MessageRow` от началото на страницата:

```kotlin
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable

@Composable
fun LazyMessageList(messages: List<Message>) {
    LazyColumn {
        items(messages) { message ->
            MessageRow(message)
        }
    }
}
```

`itemsIndexed` се импортира от `androidx.compose.foundation.lazy.itemsIndexed` и подава два аргумента на ламбда израза — индекс и елемент.

### Стабилни ключове

Параметърът `key` помага на Compose да проследява идентичността на елемент при добавяне, премахване или пренареждане. Ключът трябва да е уникален в списъка и стабилен за същия елемент. За запазване на състояние в Android се използва поддържан от `Bundle` тип, например `Long` или `String`.

Следният фрагмент заменя блока `items` в `LazyMessageList`. Приема се, че всяко съобщение има уникално `id`:

```kotlin
items(messages, key = { it.id }) { message ->
    MessageRow(message)
}
```

## Решетки с отложено създаване на елементите

`LazyVerticalGrid` разполага елементи в колони и превърта вертикално. `LazyHorizontalGrid` разполага елементи в редове и превърта хоризонтално. Блокът `LazyGridScope` предоставя функции `item` и `items`, подобни на тези за списъците.

Броят и размерът на клетките се задават чрез `columns` при `LazyVerticalGrid` и `rows` при `LazyHorizontalGrid`:

- `GridCells.Adaptive(minSize = 128.dp)` определя броя колони или редове според наличното място и желания минимален размер на клетката. При твърде малка област остава една клетка в наличния размер.
- `GridCells.Fixed(2)` задава точно две колони или два реда.

Примерът с изображения използва ресурсни идентификатори, подадени чрез `photos`:

```kotlin
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

data class Photo(@DrawableRes val drawableResourceId: Int)

@Composable
fun PhotoItem(photo: Photo) {
    Image(
        painter = painterResource(photo.drawableResourceId),
        contentDescription = "Снимка",
        modifier = Modifier.fillMaxWidth().height(128.dp),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun PhotoGrid(photos: List<Photo>) {
    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 128.dp)) {
        items(photos) { photo -> PhotoItem(photo) }
    }
}
```

При адаптивно оразмеряване оставащото място се разпределя между колоните. За фиксиран брой колони `GridCells.Adaptive(...)` може да се замени с `GridCells.Fixed(2)`.

### Елемент, който заема цял ред

Параметърът `span` определя колко клетки заема елементът. `GridItemSpan(maxLineSpan)` е подходящ за заглавие, което обхваща всички колони, дори когато броят им е адаптивен.

```kotlin
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun CategoryCard(title: String) {
    Text(title)
}

@Composable
fun CategoryGrid() {
    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 30.dp)) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            CategoryCard("Fruits")
        }
        items(6) { index -> Text("Item $index") }
    }
}
```

## Задачи за самостоятелна работа

Създайте каталог на места с поне 12 записа. Използвайте собствени текстове и локални изображения; един и същ графичен ресурс може да се използва за няколко записа.

### Задача 1. Данни и списък

Създайте модел с уникален `id`, име, категория и идентификатор на графичен ресурс. Подгответе записи в поне три категории, например „Парк“, „Музей“ и „Плаж“.

Покажете ги чрез `LazyColumn` и отделна композируема функция за карта. Използвайте `id` като стабилен ключ; графичният ресурс не е идентификатор на записа.

### Задача 2. Търсене и филтриране

Добавете търсене по част от името без значение на регистъра и избор на категория, включително „Всички“. Двата филтъра да действат едновременно.

Покажете броя намерени места и съобщение при празен резултат. Изчистването на търсенето и изборът „Всички“ да възстановяват пълния списък.

### Задача 3. Решетка и любими места

Добавете превключване между `LazyColumn` и `LazyVerticalGrid` и действие „Любимо“ за всеки запис. Пазете избраните идентификатори в общо за екрана наблюдавано състояние.

Сортирайте местата по име и проверете дали отбелязаните любими остават свързани с правилните записи при сортиране, филтриране и смяна на оформлението.

### Проверка и предаване

Предайте модела, примерните данни и UI кода. Проверете празно търсене, липсващо съвпадение и комбинация от двата филтъра. Отбележете място, скрийте го с филтър и го покажете отново — отметката трябва да се запази в текущия екран.
