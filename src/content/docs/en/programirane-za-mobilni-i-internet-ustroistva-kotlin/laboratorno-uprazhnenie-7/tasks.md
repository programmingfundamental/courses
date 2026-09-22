---
title: Tasks
taskPage: true
sidebar:
  label: Tasks
  order: 100
---
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
