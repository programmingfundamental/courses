---
title: Installing the JDK
sidebar:
  order: 2
---

Java application development requires an appropriate development environment. This course uses the Java Development Kit (JDK) and the IntelliJ IDEA integrated development environment.

# Installing the JDK

Install the Java Development Kit (JDK) before developing Java applications.

We recommend using a current **LTS (Long-Term Support)** release of the JDK. It is available from Oracle's official website or from the alternative link below:

- [Oracle Java downloads](https://www.oracle.com/java/technologies/downloads/)
- https://tuvarnabg-my.sharepoint.com/:u:/g/personal/vkolesnichenko_tu-varna_bg/ET92nCILMK9MpO_krBzE8RkBYg90A97t1NTBC-aor3a26A?e=e6PcXa

Run the installer and follow the standard installation steps. We recommend installing the JDK in the default directory.

## Configuring Environment Variables

After installation, configure the system environment variables.

In Windows, open the environment variable settings through:

## This PC -> Properties -> Advanced system settings -> Environment Variables

or search for *"Edit the system environment variables"*:

<img width="554" height="570" alt="Edit the system environment variables in Windows" src="https://github.com/user-attachments/assets/0cc72f8b-114e-4189-b1ee-ce8d9b12145e" />

## The JAVA_HOME Variable

Create a system variable named **JAVA_HOME** and set it to the path of the directory where the JDK is installed:

<img width="834" height="365" alt="JAVA_HOME system variable" src="https://github.com/user-attachments/assets/8f4c39ae-4271-442d-9aaf-de6e73fce44b" />

## The Path Variable

Add the path to the **bin** directory inside the JDK installation directory to the system **Path** variable:

<img width="531" height="637" alt="Add the JDK bin directory to Path" src="https://github.com/user-attachments/assets/3a720c45-de22-4122-9167-b4ae5bdb74b1" />

## Verifying the Installation

To verify the installation, run the following commands in **Command Prompt**:

```text
java -version
javac -version
```

If the installation was successful, the output will show the versions of Java and the `javac` compiler:

<img width="925" height="185" alt="Java and javac version output" src="https://github.com/user-attachments/assets/969dab30-4ea5-4803-8ba2-00637d049427" />
