---
layout: default
title: Лабораторно упражнение 7
parent: Програмиране за мобилни и Интернет устройства Kotlin
has_children: true
nav_order: 7
permalink: /docs/programirane-za-mobilni-i-internet-ustroistva-kotlin/laboratorno-uprazhnenie-7
---

# Лабораторно упражнение 7

## Тема: Форми и елементи за въвеждане на данни и слушатели на събития  
**Дисциплина:** Програмиране за мобилни и Интернет устройства  
**Език:** Kotlin  
**Среда:** Android Studio (Jetpack Compose)

---

## 📘 1. Теоретична част

### 1.1. Форми и елементи за въвеждане на данни
В мобилните приложения често се налага потребителят да въвежда данни чрез различни елементи на интерфейса.  
В Jetpack Compose се използват декларативни компоненти за изграждане на форми:

| Елемент | Описание | Пример |
|----------|-----------|--------|
| **TextField** | Едноредово поле за въвеждане на текст. | `TextField(value, onValueChange = { ... })` |
| **OutlinedTextField** | С текстово поле с очертание (граница). | `OutlinedTextField(value, onValueChange = { ... })` |
| **PasswordField** | Поле за парола (с маскиране на текста). | `OutlinedTextField(visualTransformation = PasswordVisualTransformation())` |
| **Checkbox** | Поле за избор (да/не). | `Checkbox(checked, onCheckedChange = { ... })` |
| **RadioButton** | Позволява избор от няколко опции. | `RadioButton(selected, onClick = { ... })` |
| **Switch** | Превключвател (включено/изключено). | `Switch(checked, onCheckedChange = { ... })` |
| **Button** | Изпълнява действие при натискане. | `Button(onClick = { ... })` |

---

### 1.2. Управление на състоянието (State)
В Jetpack Compose **състоянието** определя какво се визуализира в потребителския интерфейс.  
За промяна на съдържанието се използва `remember` и `mutableStateOf()`.

```kotlin
var username by remember { mutableStateOf("") }

При промяна на стойността, интерфейсът автоматично се обновява.

1.3. Слушатели на събития (Event Listeners)

Слушателите на събития (event listeners) са функции, които реагират на действия на потребителя — натискане на бутон, промяна на текст, избор от списък и др.

Примери:

// Натискане на бутон
Button(onClick = { Log.d("BTN", "Натиснат бутон!") }) { Text("Изпрати") }

// Промяна на текст
TextField(
    value = text,
    onValueChange = { newValue -> text = newValue }
)


1.4. Валидация на данни

Преди да се изпратят въведените данни, често се извършва проверка (валидация):

if (username.isBlank()) {
    Toast.makeText(context, "Моля, въведете име!", Toast.LENGTH_SHORT).show()
}

## 💡 1.2. Примери за използване на елементите

### 🟦 TextField
```kotlin
@Composable
fun SimpleTextFieldExample() {
    var name by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Въведете име:")
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Име") }
        )
        Text("Здравей, $name")
    }
}

OutlinedTextField

@Composable
fun OutlinedTextFieldExample() {
    var email by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Въведете имейл:")
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Имейл адрес") },
            placeholder = { Text("example@mail.com") }
        )
    }
}

PasswordField

@Composable
fun PasswordFieldExample() {
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Въведете парола:")
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Парола") },
            visualTransformation = PasswordVisualTransformation()
        )
    }
}

Checkbox

@Composable
fun CheckboxExample() {
    var isChecked by remember { mutableStateOf(false) }

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

RadioButton

@Composable
fun RadioButtonExample() {
    var selectedOption by remember { mutableStateOf("Мъж") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Изберете пол:")
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

Switch

@Composable
fun SwitchExample() {
    var notificationsEnabled by remember { mutableStateOf(true) }

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

Button

@Composable
fun ButtonExample() {
    val context = LocalContext.current

    Button(
        onClick = {
            Toast.makeText(context, "Бутонът е натиснат!", Toast.LENGTH_SHORT).show()
        },
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Натисни ме")
    }
}


Пример:

Създайте екран за вход в мобилно приложение с две полета:

Потребителско име

Парола

и бутон "Вход", който показва Toast съобщение с въведените данни.

@Composable
fun LoginScreen() {
    var username by remember { mutableStateOf("") }
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
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(context, "Моля, попълнете всички полета!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Вход: $username / $password", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Вход")
        }
    }
}


