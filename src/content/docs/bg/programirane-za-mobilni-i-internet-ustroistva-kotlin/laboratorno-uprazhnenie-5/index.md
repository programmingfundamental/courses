---
title: Лабораторно упражнение 5
sidebar:
  order: 5
---

# Лабораторно упражнение 5

## Построяване на потребителски интерфейс

Потребителският интерфейс (UI) включва текстове, изображения, бутони, полета за въвеждане и тяхното разположение на екрана. Чрез него приложението представя информация и приема действия от потребителя.

![Варианти на активни бутони](/courses/docs/BEO/programirane-za-mobilni-i-internet-ustroistva-kotlin/laboratorno-uprazhnenie-5/image.png)

![Подсказка с описание и действие](/courses/docs/BEO/programirane-za-mobilni-i-internet-ustroistva-kotlin/laboratorno-uprazhnenie-5/image-1.png)

![Полета за въвеждане с плътен фон и с контур](/courses/docs/BEO/programirane-za-mobilni-i-internet-ustroistva-kotlin/laboratorno-uprazhnenie-5/image-2.png)

Елементите могат да бъдат интерактивни, например бутон и поле за въвеждане, или да представят информация, например текст и изображение.

## Jetpack Compose

Jetpack Compose е инструментариум за декларативно изграждане на Android интерфейси с Kotlin. Интерфейсът се описва чрез композируеми функции (`@Composable`), които използват входните данни и текущото състояние.

## Композируеми функции

Анотацията `@Composable` позволява на функцията да участва в композицията. Функциите, които описват UI, обикновено връщат `Unit` и извикват други композируеми функции, например `Text`, `Image` и `Button`. Не всяка функция с `@Composable` непременно създава видим елемент.

Композицията е описанието на интерфейса, което Compose изгражда при изпълнение на тези функции. Рекомпозицията е повторно изпълнение на засегнати части, когато входните данни или наблюдаваното състояние се променят.

## Мерни единици

Независимите от плътността пиксели (`dp`) се използват за размери и отстояния. Мащабируемите пиксели (`sp`) се използват за размер на текста и отчитат настройката на потребителя за шрифта. В Kotlin стойностите се записват например като `16.dp` и `20.sp` чрез съответните импорти от `androidx.compose.ui.unit`.

AndroidX е набор от библиотеки за Android. Compose API се използва чрез пакети като `androidx.compose.foundation`, `androidx.compose.material3` и `androidx.compose.ui`.

## Йерархия на потребителския интерфейс

Оформлението се изгражда чрез вложени извиквания. Родителското оформление съдържа дъщерни елементи, които също могат да съдържат други елементи.

- `Column` подрежда елементите вертикално.
- `Row` ги подрежда хоризонтално.
- `Box` позволява наслагване и позициониране на елементи в обща област.

![Вертикално подреждане с Column и хоризонтално подреждане с Row](/courses/docs/BEO/programirane-za-mobilni-i-internet-ustroistva-kotlin/laboratorno-uprazhnenie-5/image-3.png)

Следните три фрагмента се поставят поотделно в тялото на композируема функция. За тях са необходими `import androidx.compose.foundation.layout.Row` и `import androidx.compose.material3.Text`.

```kotlin
Row {
    Text("First Column")
    Text("Second Column")
}
```

![Два текстови елемента, подредени хоризонтално в Row](/courses/docs/BEO/programirane-za-mobilni-i-internet-ustroistva-kotlin/laboratorno-uprazhnenie-5/image-4.png)

Функции като `Row`, `Column` и `Box` приемат съдържание като ламбда израз. Когато последният аргумент е ламбда израз, той може да се запише след кръглите скоби във фигурни скоби `{ ... }` (trailing lambda). При липса на други аргументи кръглите скоби могат да се пропуснат.

Запис с именуван параметър `content`:

```kotlin
Row(
    content = {
        Text("Some text")
        Text("Some more text")
        Text("Last text")
    }
)
```

Равностоен запис с ламбда израз след скобите:

```kotlin
Row {
    Text("Some text")
    Text("Some more text")
    Text("Last text")
}
```

## Оформление и `Modifier`

`Modifier` задава характеристики като размер, вътрешни отстояния (`padding`), фон, позициониране и взаимодействие. Модификаторите се свързват във верига, чийто ред може да промени резултата. Някои са достъпни само в определен контекст, например `Modifier.align` в `Box`.

Не всички свойства се задават чрез `Modifier`. `fontSize`, `lineHeight` и `textAlign` са параметри на `Text`. При `Column` подреждането се управлява чрез `verticalArrangement` и `horizontalAlignment`, а при `Row` — чрез `horizontalArrangement` и `verticalAlignment`.

## Обработка на потребителски взаимодействия

Compose компонентите могат да показват визуална обратна връзка при взаимодействие. `Button` предоставя параметър `onClick`, в който се задава обработващата функция. Не е необходимо към бутона допълнително да се добавя `Modifier.clickable`.

`Modifier.clickable` се използва, когато друг подходящ елемент трябва да реагира на щракване. Така действието се описва чрез callback функция, без ръчно обработване на всяко докосване или натискане на клавиш.

## Ресурси на приложението

Ресурсите се съхраняват в `app/src/main/res`. **Resource Manager** служи за преглед и добавяне на ресурси и се отваря чрез **View > Tool Windows > Resource Manager**. При изграждане на проекта се генерират идентификатори в класа `R`, например `R.string.app_name`.

В Compose текстов ресурс се извлича чрез `stringResource()`, а подходящ графичен ресурс — чрез `painterResource()`. Съответните функции са в `androidx.compose.ui.res`. Графичният ресурс може да се подаде на `Image`.

## Динамичен потребителски интерфейс

Обикновена локална променлива не става автоматично наблюдавано Compose състояние (`state`). `mutableStateOf()` създава наблюдавано състояние. Промяна на стойността му може да предизвика рекомпозиция на частите, които я прочитат.

`remember` запазва стойността между рекомпозиции, докато съответната част е в композицията. Ламбда изразът, подаден на `remember`, създава първоначалната стойност; той не е обработчик, извикван при всяка промяна. Примерът показва брояч:

```kotlin
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun CounterButton() {
    var count by remember { mutableStateOf(0) }
    Button(onClick = { count++ }) {
        Text("Брой натискания: $count")
    }
}
```

`remember` сам по себе си не запазва стойността при пресъздаване на `Activity`.
