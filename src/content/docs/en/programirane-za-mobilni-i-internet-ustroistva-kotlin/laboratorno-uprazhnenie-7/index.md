---
title: Lab 7
sidebar:
  order: 7
---

# Lab 7

## Forms, data entry, and handling user events

Forms collect input values through Compose components. State determines the displayed data, while handler functions describe the actions performed during interaction.

### Input components

| Component | Purpose |
| --- | --- |
| `TextField` | A text input field; `singleLine = true` restricts it to one line. |
| `OutlinedTextField` | A text field with an outline. |
| `Checkbox` | A choice between checked and unchecked states. |
| `RadioButton` | Selecting one option from a group managed through shared state. |
| `Switch` | Toggling a setting. |
| `Button` | Performing an action through `onClick`. |

`PasswordField` is not a standard standalone Material Compose component. A password field can be implemented using `TextField` or `OutlinedTextField` with `PasswordVisualTransformation()`.

### State management

`mutableStateOf()` creates observable state, while `remember` retains the value across recompositions. Use `rememberSaveable` for suitable UI values such as a name, email, and checkbox state so that they can be restored after recreation. In these examples, passwords are stored with `remember` and must be entered again after recreation.

The following snippet belongs inside a composable function and uses the imports listed below:

```kotlin
var username by rememberSaveable { mutableStateOf("") }
```

### Callback functions

In Compose, interactions are usually handled through callback parameters: `onClick`, `onValueChange`, and `onCheckedChange`. The handler function can change state or perform an action, such as logging to Logcat.

```kotlin
@Composable
fun EventsExample() {
    var text by rememberSaveable { mutableStateOf("") }
    var checked by rememberSaveable { mutableStateOf(false) }
    Column {
        TextField(value = text, onValueChange = { newValue -> text = newValue })
        Checkbox(checked = checked, onCheckedChange = { checked = it })
        Button(onClick = { Log.d("BTN", "Button pressed") }) {
            Text("Submit")
        }
    }
}
```

### Data validation

Check various conditions before processing the form:

- `isBlank()` detects an empty value or one consisting only of whitespace.
- The email address is checked separately for a suitable format; an email keyboard makes entry easier but does not validate the value.
- The password and its confirmation must match.
- Display an error through `isError` and explanatory text (`supportingText`), or through a `Toast`.

Display a success message only after all applicable checks pass. Create the `Toast` in the handler function, rather than as an action performed on every recomposition.

## Component usage examples

The examples use Compose Material 3. Place the following imports at the beginning of the Kotlin file. Each function shown can be called from `setContent` or another composable function.

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
        Text("Name:")
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") }
        )
        Text("Hello, $name")
    }
}
```

### `OutlinedTextField`

```kotlin
@Composable
fun OutlinedTextFieldExample() {
    var email by rememberSaveable { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Email:")
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email address") },
            placeholder = { Text("example@mail.com") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )
    }
}
```

### Password field

```kotlin
@Composable
fun PasswordFieldExample() {
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Password:")
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
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
        Text(if (isChecked) "I agree" else "I do not agree")
    }
}
```

### `RadioButton`

```kotlin
@Composable
fun RadioButtonExample() {
    var selectedOption by rememberSaveable { mutableStateOf("Male") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Gender:")
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
        Text("Selected: $selectedOption")
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
        Text("Notifications:")
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
            Toast.makeText(context, "The button was pressed!", Toast.LENGTH_SHORT).show()
        },
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Show message")
    }
}
```

### Example login form

Create a screen with a username, password, and "Log in" button. If a field is empty, display an error `Toast`; if both fields are filled in, display a message with the username. The example demonstrates local form validation; the password is not shown in the message.

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
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(context, "All fields are required.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Details entered for: $username", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Log in")
        }
    }
}
```
