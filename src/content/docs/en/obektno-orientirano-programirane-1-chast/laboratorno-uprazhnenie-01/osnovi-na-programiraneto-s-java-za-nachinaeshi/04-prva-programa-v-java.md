---
title: Your First Java Program
sidebar:
  order: 4
---

# Your First Java Program

## What Is a Project Template?

A project template is a prepared starting structure that may include directories, settings, and sample code. IntelliJ IDEA uses the selected project type and the settings in **New Project** to create this foundation. You can then add your own classes and logic.

For example, selecting **Java** and **IntelliJ** as the build system creates a Java project with a source-code directory. The **Add sample code** option also adds an example program. Maven and Gradle use different structures and configuration files. The JDK determines which Java version compiles the program; it is not a project template.

## Creating a Project in IntelliJ IDEA

1. Select **New Project** on the welcome screen, or choose **File → New → Project**.
2. Select **Java**, enter the name `JavaBasics`, and choose a project location.
3. Select **IntelliJ** as the **Build system** and JDK 17 or 21 for the examples in this course.
4. Select **Add sample code** if you want the IDE to create a starter example. If you leave it unchecked, you will create the class yourself.
5. Select **Create**, inspect `src`, and run the sample `main` method if one was added.

A **project** template prepares the entire project structure. A **file** template, used with **New → Java Class**, prepares a single `.java` file. Both save you from writing the same initial code repeatedly. See JetBrains' guides to [creating a new project](https://www.jetbrains.com/help/idea/new-project-wizard.html) and [file templates](https://www.jetbrains.com/help/idea/using-file-and-code-templates.html).

After starting IntelliJ IDEA, select **New Project** to open the project configuration window:

<img width="709" height="734" alt="New project configuration in IntelliJ IDEA" src="https://github.com/user-attachments/assets/7314ceab-1f2d-4341-9b4c-0f3fdd114534" />

Set the following options:

- **Name** — the project name; the example in the figure uses *JavaBsics*.
- **Location** — the directory where the project will be stored; the example uses *Desktop*.
- **Build System** — IntelliJ.
- **JDK** — the Java version used by the project; the example uses *OpenJDK-17*.

## Project Structure

After creating the project, IntelliJ IDEA generates its initial structure.

<img width="586" height="379" alt="Initial project structure" src="https://github.com/user-attachments/assets/6a0f6c36-02c4-4912-a1e8-5e9306eca9a8" />

The main elements are:

- **Project** — the root directory;
- **src** — the directory containing source code;
- **out** — the directory containing compiled `.class` files;
- **External Libraries** — the libraries used by the project.

*Note:* The **out** directory is created automatically after the project is compiled successfully for the first time.

## Organizing Code with Packages

Before creating classes, it is good practice to create a package.

Packages are used to:

- group classes logically;
- avoid class-name conflicts;
- organize the project structure.

To create a package, open the context menu on the **src** directory and select:

## New -> Package

This course uses the following structure for the main package:

```java
bg.tu_varna.sit.<group>.<faculty_number>.task<task_number>
```

<img width="495" height="364" alt="Creating a package in IntelliJ IDEA" src="https://github.com/user-attachments/assets/c2bfacd2-8176-4c79-bd1b-c41a01bbad06" />

*Note:* Consistent naming and project organization allow tools to analyze, compile, test, and grade code automatically. For this reason, the course uses a common package structure.

## Using `import`

Use the `import` keyword when a file needs to use a class from another package. This avoids writing the class's fully qualified name every time.

```java
import java.time.LocalDate;
```

After this declaration, you can refer to the class by its short name:

```java
LocalDate today = LocalDate.now();
```

Write `import` declarations after the `package` declaration and before the class declaration.

```java
package bg.tu_varna.example;

import java.time.LocalDate;

public class Application {

}
```

## Creating Your First Class

After creating the package, create your first Java class by opening the package's context menu and selecting **New -> Java Class**.

Choose a meaningful class name that describes its purpose. In this course, the class containing the `main` method will be named **Application**.

<img width="976" height="276" alt="Creating a Java class" src="https://github.com/user-attachments/assets/1e0b4cbd-a47d-4f3b-8719-7f2ff933f150" />

## The `main` Method

Every Java application starts execution in the `main` method:

<img width="604" height="310" alt="The main method" src="https://github.com/user-attachments/assets/f4dc6382-25e3-4e66-9447-3d3b8a2fa2c7" />

This method is the application's entry point. When the program starts, the JVM begins execution here. In this course, the method will always be defined in the **Application** class. This follows a common Java practice of naming the class that represents an application's entry point this way.

There are several ways to run the `main` method. The figure below shows some of them:

<img width="571" height="229" alt="Ways to run the main method" src="https://github.com/user-attachments/assets/ab060380-1467-492c-ace8-b0b42f064dac" />

Selecting any of these options runs the program. This example prints the following message to the console (usually shown in a panel at the bottom):

```text
This is an example
```

After learning the basic constructs and methods, continue to [Debugging](/courses/en/obektno-orientirano-programirane-1-chast/laboratorno-uprazhnenie-01/otkrivane-i-otstranyavane-na-greshki/).
