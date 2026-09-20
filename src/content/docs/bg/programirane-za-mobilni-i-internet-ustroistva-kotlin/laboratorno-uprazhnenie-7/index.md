---
title: Лабораторно упражнение 7
sidebar:
  order: 7
---

# Лабораторно упражнение 7

## Форми, въвеждане на данни и обработване на потребителски събития

Формите събират въведени стойности чрез Compose компоненти. Състоянието определя показаните данни, а обработващите функции описват действията при взаимодействие.

### Входни компоненти

| Компонент | Предназначение |
| --- | --- |
| `TextField` | Поле за текст; `singleLine = true` го ограничава до един ред. |
| `OutlinedTextField` | Текстово поле с контур. |
| `Checkbox` | Избор между отметнато и неотметнато състояние. |
| `RadioButton` | Избор на една възможност от група, управлявана чрез общо състояние. |
| `Switch` | Превключване на настройка. |
| `Button` | Изпълнение на действие чрез `onClick`. |

`PasswordField` не е стандартен самостоятелен Material Compose компонент. Поле за парола може да се реализира чрез `TextField` или `OutlinedTextField` с `PasswordVisualTransformation()`.

### Управление на състоянието

`mutableStateOf()` създава наблюдавано състояние, а `remember` запазва стойността между рекомпозиции. За подходящи UI стойности като име, имейл и отметка се използва `rememberSaveable`, за да могат да бъдат възстановени при пресъздаване. В примерите паролите се пазят с `remember` и при пресъздаване се въвеждат отново.

Следният фрагмент е част от тялото на композируема функция и използва импортите, дадени по-долу:

```kotlin
var username by rememberSaveable { mutableStateOf("") }
```

### Callback функции

В Compose взаимодействията обикновено се обработват чрез callback параметри: `onClick`, `onValueChange` и `onCheckedChange`. Обработващата функция може да промени състоянието или да изпълни действие, например запис в Logcat.

```kotlin
@Composable
fun EventsExample() {
    var text by rememberSaveable { mutableStateOf("") }
    var checked by rememberSaveable { mutableStateOf(false) }
    Column {
        TextField(value = text, onValueChange = { newValue -> text = newValue })
        Checkbox(checked = checked, onCheckedChange = { checked = it })
        Button(onClick = { Log.d("BTN", "Натиснат бутон") }) {
            Text("Изпращане")
        }
    }
}
```

### Валидация на данните

Преди обработване на формата се проверяват различни условия:

- `isBlank()` установява празна стойност или стойност само от празни знаци.
- Имейлът се проверява отделно за подходящ формат; клавиатурата за имейл улеснява въвеждането, но не валидира стойността.
- Паролата и нейното потвърждение трябва да съвпадат.
- Грешката се показва чрез `isError` и поясняващ текст (`supportingText`) или чрез `Toast`.

Съобщение за успех се показва само след успешното преминаване на всички приложими проверки. `Toast` се създава в обработващата функция, а не като действие при всяка рекомпозиция.

## Примери за използване на компонентите

Примерите използват Compose Material 3. Следните импорти се поставят в началото на Kotlin файла. Всяка показана функция може да се извика от `setContent` или от друга композируема функция.

```kotlin
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
```

### `TextField`

```kotlin
@Composable
fun SimpleTextFieldExample() {
    var name by rememberSaveable { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Име:")
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Име") }
        )
        Text("Здравей, $name")
    }
}
```

### `OutlinedTextField`

```kotlin
@Composable
fun OutlinedTextFieldExample() {
    var email by rememberSaveable { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Имейл:")
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Имейл адрес") },
            placeholder = { Text("example@mail.com") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )
    }
}
```

### Поле за парола

```kotlin
@Composable
fun PasswordFieldExample() {
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Парола:")
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Парола") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )
    }
}
```

### `Checkbox`

```kotlin
@Composable
fun CheckboxExample() {
    var isChecked by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { isChecked = it }
        )
        Text(if (isChecked) "Съгласен съм" else "Не съм съгласен")
    }
}
```

### `RadioButton`

```kotlin
@Composable
fun RadioButtonExample() {
    var selectedOption by rememberSaveable { mutableStateOf("Мъж") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Пол:")
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selectedOption == "Мъж",
                onClick = { selectedOption = "Мъж" }
            )
            Text("Мъж")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selectedOption == "Жена",
                onClick = { selectedOption = "Жена" }
            )
            Text("Жена")
        }
        Text("Избран: $selectedOption")
    }
}
```

### `Switch`

```kotlin
@Composable
fun SwitchExample() {
    var notificationsEnabled by rememberSaveable { mutableStateOf(true) }

    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Известия:")
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = notificationsEnabled,
            onCheckedChange = { notificationsEnabled = it }
        )
    }
}
```

### `Button`

```kotlin
@Composable
fun ButtonExample() {
    val context = LocalContext.current

    Button(
        onClick = {
            Toast.makeText(context, "Бутонът е натиснат!", Toast.LENGTH_SHORT).show()
        },
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Показване на съобщение")
    }
}
```

### Примерна форма за вход

Да се създаде екран с потребителско име, парола и бутон „Вход“. При празно поле да се покаже `Toast` за грешка, а при попълнени полета — съобщение с потребителското име. Примерът демонстрира локална проверка на формата; паролата не се показва в съобщението.

```kotlin
@Composable
fun LoginScreen() {
    var username by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Форма за вход", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Потребителско име") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Парола") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(context, "Всички полета са задължителни.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Попълнени данни за: $username", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Вход")
        }
    }
}
```

## Задачи за самостоятелна работа

Създайте форма „Записване за работилница“. Данните се обработват локално в приложението.

### Задача 1. Полета и състояние

Добавете име, имейл, брой места, избор „Присъствено“ или „Онлайн“ чрез `RadioButton` и `Checkbox` за съгласие с условията.

Използвайте етикети, подходяща клавиатура и `rememberSaveable` за въведените текстове и избори. Състоянието на полето за брой места да е текст, за да може да представя и временно празен вход.

### Задача 2. Валидация при изпращане

При „Записване“ проверете за непразно име, подходящ формат на имейла, цяло число места от 1 до 5 и поставена отметка. Използвайте безопасно преобразуване на числото. Поле с грешка да има `isError` и конкретен `supportingText`.

Покажете всички установени грешки при опита за изпращане. Само при валидни данни покажете обобщение с името, формата на участие и броя места.

### Задача 3. Редактиране и изчистване

Добавете бутон „Изчистване“, който връща полетата и съобщенията в начално състояние. При редакция след успешна проверка скрийте предишното обобщение, докато данните не бъдат проверени отново.

Изнесете проверката на броя места в обикновена Kotlin функция, която може да се извика независимо от Compose.

### Проверка и предаване

Предайте кода и таблица с резултати за празна форма, име само с интервали, невалиден имейл и брой места `""`, `"abc"`, `"0"`, `"1"`, `"5"`, `"6"`. Проверете липсващо съгласие, успешен запис, редакция и изчистване. При пресъздаване въведените стойности и избори трябва да се възстановят.
