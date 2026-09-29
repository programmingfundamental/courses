---
title: Tasks
sidebar:
  order: 100
  label: Tasks
taskPage: true
---
Create a registration form with the following fields:

- name;
- email;
- password;
- password confirmation;
- a `Checkbox` for accepting the terms of use.

## Building the form

1. Use `OutlinedTextField` for text fields, with labels and `singleLine = true`.
2. Set `KeyboardOptions(keyboardType = KeyboardType.Email)` for the email field.
3. Use `PasswordVisualTransformation()` and `KeyboardOptions(keyboardType = KeyboardType.Password)` for both password fields.
4. Use `rememberSaveable { mutableStateOf(...) }` for the name, email, and checkbox. These values are suitable for saving UI state during recreation. Store passwords with `remember` and require them to be entered again after recreation.
5. Arrange the form using `Column`, `Spacer`, and `Modifier.padding()`.
6. Add a "Register" button whose `onClick` validates the data.

## Registration checks

Before displaying a success message, check that:

1. All text fields contain a value other than an empty string or whitespace only.
2. The email has a suitable format. For local validation, you can use `android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()`. This check does not prove that the address exists.
3. The password and confirmation match. Compare passwords exactly as entered, without removing any characters.
4. The acceptance checkbox is checked.

For invalid data, show a `Toast` with a specific error message. The corresponding text field can also be marked using `isError` and `supportingText`.

Only when all checks pass should you display "Registration successful!". Do not execute the success branch if an error occurs. This task demonstrates form data validation.

## Verifying the result

Try an empty form, an invalid email, mismatched passwords, an unchecked checkbox, and fully valid data. Check that every invalid case displays an error and that only the valid case displays success.

## Independent tasks

Create a "Workshop Registration" form. Process the data locally in the application.

### Task 1. Fields and state

Add a name, email, number of places, a choice between "In person" and "Online" using `RadioButton`, and a `Checkbox` for accepting the terms.

Use labels, an appropriate keyboard, and `rememberSaveable` for the entered text and selections. Store the number-of-places field as text so that it can also represent temporarily empty input.

### Task 2. Validation on submission

When "Register" is pressed, check for a nonblank name, a suitable email format, an integer number of places from 1 to 5, and a checked checkbox. Use safe numeric conversion. A field with an error should have `isError` and specific `supportingText`.

Display all detected errors on the submission attempt. Only with valid data should you show a summary containing the name, attendance format, and number of places.

### Task 3. Editing and clearing

Add a "Clear" button that restores fields and messages to their initial state. When editing after successful validation, hide the previous summary until the data is validated again.

Move validation of the number of places into an ordinary Kotlin function that can be called independently of Compose.

### Verification and submission

Submit the code and a results table for an empty form, a name containing only spaces, an invalid email, and the following numbers of places: `""`, `"abc"`, `"0"`, `"1"`, `"5"`, `"6"`. Test missing acceptance, successful registration, editing, and clearing. Entered values and selections should be restored after recreation.
