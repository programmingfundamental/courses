---
layout: default
title: lab 7
parent: Mobile and Internet Programming Kotlin
has_children: true
nav_order: 7
permalink: /docs/programirane-za-mobilni-i-internet-ustroistva-kotlin-аео/laboratorno-uprazhnenie-7
---

## Lab 7

## Forms and data entry elements and event listeners  


---

## 📘 1. Theory

### 1.1. Forms and data entry elements
In mobile applications, the user often needs to enter data through various interface elements.
Jetpack Compose uses declarative components to build forms:

| Element | Description | Example |
|----------|-----------|--------|
| **TextField** | Single-line text input field | `TextField(value, onValueChange = { ... })` |
| **OutlinedTextField** | Text box with outline (border) | `OutlinedTextField(value, onValueChange = { ... })` |
| **PasswordField** |Password field (with text obfuscation) | `OutlinedTextField(visualTransformation = PasswordVisualTransformation())` |
| **Checkbox** | Checkbox (yes/no) | `Checkbox(checked, onCheckedChange = { ... })` |
| **RadioButton** |Radio button | `RadioButton(selected, onClick = { ... })` |
| **Switch** | Switch (on/off). | `Switch(checked, onCheckedChange = { ... })` |
| **Button** | Performs action on button press | `Button(onClick = { ... })` |

---

### 1.2. State management
In Jetpack Compose, **state** determines what is rendered in the user interface.
To change the content, `remember` and `mutableStateOf()` are used.

```kotlin
var username by remember { mutableStateOf("") }
```

The user interface is automatically updated when there is a value change.

### 1.3. Event Listeners

Event listeners are functions which react to user action - button click, text change, choice made, etc.

Example:

```kotlin
// Button click
Button(onClick = { Log.d("BTN", "Pressed button!") }) { Text("Send") }

// Text change
TextField(
    value = text,
    onValueChange = { newValue -> text = newValue }
)
```

### 1.4.Data validation

Before the entered data is sent, a check (validation) is often performed.:

```kotlin
if (username.isBlank()) {
    Toast.makeText(context, "Please, enter name!", Toast.LENGTH_SHORT).show()
}
```


## 💡Examples of elements usage

### TextField
```kotlin
@Composable
fun SimpleTextFieldExample() {
    var name by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Enter name:")
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") }
        )
        Text("Hello, $name")
    }
}
```

### OutlinedTextField

```kotlin
@Composable
fun OutlinedTextFieldExample() {
    var email by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Enter email:")
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email address") },
            placeholder = { Text("example@mail.com") }
        )
    }
}
```

### PasswordField

```kotlin
@Composable
fun PasswordFieldExample() {
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Enter password:")
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
    }
}
```

### Checkbox

```kotlin
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
        Text(if (isChecked) "Agree" else "Do not agree")
    }
}
```

### RadioButton

```kotlin
@Composable
fun RadioButtonExample() {
    var selectedOption by remember { mutableStateOf("Мъж") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Choose gender:")
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selectedOption == "Male",
                onClick = { selectedOption = "Male" }
            )
            Text("Male")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selectedOption == "Female",
                onClick = { selectedOption = "Female" }
            )
            Text("Female")
        }
        Text("Избран: $selectedOption")
    }
}
```

### Switch

```kotlin
@Composable
fun SwitchExample() {
    var notificationsEnabled by remember { mutableStateOf(true) }

    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Notifications:")
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = notificationsEnabled,
            onCheckedChange = { notificationsEnabled = it }
        )
    }
}
```

### Button

```kotlin
@Composable
fun ButtonExample() {
    val context = LocalContext.current

    Button(
        onClick = {
            Toast.makeText(context, "Button is pressed!", Toast.LENGTH_SHORT).show()
        },
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Press")
    }
}
```


## Example:

Create a mobile app login screen with two fields:
- Username
- Password
and a "Login" button that displays a Toast message with the entered data.

```kotlin
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
        Text("Login form", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(context, "Please, fill the required fields!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Login: $username / $password", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Login")
        }
    }
}
```

### Task
Create a registration form that includes the following fields:
- Name
- Email
- Password
- Confirm password
- A checkbox for agreeing to the terms of use.

Add a "Register" button that:
- Checks if all fields are filled in;
- Checks if the passwords match;
- If there is an error — displays a Toast with an error message;
- If everything is OK — displays a message "Registration successful!".

Tips:

Use OutlinedTextField for the fields.

Use Checkbox for the consent.

Use remember { mutableStateOf(...) } to manage the values.

Add a visual structure with Column, Spacer, and Modifier.padding().
