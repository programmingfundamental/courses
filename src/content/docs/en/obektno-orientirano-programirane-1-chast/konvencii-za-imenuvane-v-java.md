---
title: "Java Naming Conventions"
sidebar:
  order: 1
---

# Java Naming Conventions

Java naming conventions are established rules for choosing names for packages, classes, interfaces, methods, variables, and constants. The compiler does not enforce these rules in every case, but following them makes code more readable, more predictable, and easier to maintain.

The main naming styles used in Java are:

- `PascalCase` - each word begins with a capital letter: `Person`, `ImageEditor`;
- `camelCase` - the first word begins with a lowercase letter, and each subsequent word begins with a capital letter: `basePrice`, `calculateAverageScore`;
- `UPPER_SNAKE_CASE` - all letters are uppercase, and words are separated by underscores: `MIN_AGE`, `MAX_USERS_COUNT`;
- `kebab-case` - all letters are lowercase, and words are separated by hyphens: `student-management`, `oop-lab-01`. This style is used for project names, but not for Java identifiers.

| Identifier Type | Convention | Rules | Examples |
| --------------- | ---------- | ----- | -------- |
| IntelliJ IDEA project | `kebab-case` | When creating a project, use a short, descriptive name written with Latin letters. Avoid spaces, Cyrillic letters, and uninformative names. For laboratory exercises, the name may include the exercise number. | `student-management` <br> `oop-lab-01` |
| Package | lowercase | Package names use lowercase letters only. They usually begin with the organization's or project's domain name in reverse order. Avoid uppercase letters, spaces, and special characters. | `com.example.school` <br> `bg.tu_varna.oop` |
| Class | `PascalCase` | A class name is usually a noun or noun phrase that describes what the class represents. It should be specific and informative. | `class Person` <br> `class ImageEditor` |
| Interface | `PascalCase` | Interfaces use the same naming style as classes. The name should describe a behavior, capability, or role. | `interface Calculator` <br> `interface Printable` |
| Method | `camelCase` | A method name is usually a verb or verb phrase because a method performs an action. | `run()` <br> `getRate()` <br> `addAverageScore()` |
| Variable | `camelCase` | A variable name should clearly describe the value it stores. Avoid single-letter names, except for counters in short loops. | `int age;` <br> `double basePrice;` |
| Constant | `UPPER_SNAKE_CASE` | Constants are declared with `static final` and written in uppercase letters. If the name contains more than one word, separate the words with an underscore `_`. | `static final int MIN_AGE = 18;` <br> `static final double VAT_RATE = 0.20;` |

Well-chosen names should explain what an element represents or what action it performs. For example, `calculateTotalPrice()` is a clearer method name than `calc()` or `doWork()`.

In this course, the class containing the `main` method will be named `Application`. Other classes should have names that reflect their purpose.
