---
title: Installing IntelliJ
sidebar:
  order: 3
---

# Installing IntelliJ

This course uses the IntelliJ IDEA integrated development environment for Java application development.

Download the IDE from JetBrains' official website. Students can use the university-provided license to unlock the full Ultimate feature set.

Download IntelliJ IDEA:

[Direct download](https://www.jetbrains.com/idea/download/?section=windows)

Run the installer and follow the standard installation steps.

During installation, we recommend associating files with the **.java** extension with IntelliJ IDEA. Then double-clicking a Java file will open it in the IDE.

After installation, you should be able to:

- start IntelliJ IDEA;
- create a new Java project;
- select an installed JDK version;
- compile and run a Java application.

## Installing a JDK Through IntelliJ IDEA

IntelliJ IDEA can use an existing JDK or download one when you create a project. This lets you configure Java directly through the IDE without manually locating an installation directory.

When creating a project, select `New Project`. In the project setup window, specify:

- the project name;
- the project location;
- the `Java` language;
- the build system;
- the JDK to use for the project.

If no version is selected in the `JDK` field, use the option to add or download a JDK. Depending on your IntelliJ IDEA version, this may appear as `Add JDK`, `Download JDK`, or in the dropdown next to the `JDK` field.

When downloading a JDK through IntelliJ IDEA, select:

- the JDK version;
- the JDK vendor;
- the directory where the JDK will be installed.

After you confirm, IntelliJ IDEA downloads the selected version and adds it to the available SDK configurations. You can then select that version as the JDK for the current project.

## Selecting a JDK for a Project

Each Java project in IntelliJ IDEA uses a specific JDK version. This version determines which compiler is used and which language features are available.

For an existing project, check or change the JDK in the project settings:

```text
File -> Project Structure -> Project
```

Select the JDK version for the project in the `SDK` field. If the required version is missing, add it using `Add SDK`.

You can also set the `Language level` in the same window. This setting determines which Java syntax features are allowed in the project. Usually, the language level should match the selected JDK version.

## Managing Java Versions in IntelliJ IDEA

A computer can have several JDK versions installed. The operating system may use one version when running `java` and `javac` from the command line, while IntelliJ IDEA uses another version for a particular project.

Check the command-line version with:

```text
java -version
javac -version
```

Check the IntelliJ IDEA version in `File -> Project Structure -> Project`. If these versions do not match, a program might compile in the IDE but fail to compile from the command line, or vice versa.

Choose a JDK version suitable for the language features used in each project. An older JDK cannot compile code that uses features from a newer Java version.

## JDK and Running Your First Program

Before running your first Java program in IntelliJ IDEA, make sure that:

- a JDK version is selected for the project;
- the source file is in the `src` directory;
- the class to run contains a `main` method;
- the run configuration uses the same JDK as the project.

If IntelliJ IDEA reports that no JDK is selected, the project cannot be compiled. Select an installed JDK or download one through the project settings.
