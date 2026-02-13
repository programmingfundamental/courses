---

layout: default
title: Installing JDK
parent: Environment Setup
grand_parent: Laboratory Exercise 1
ancestor: Software Systems
nav_order: 1
#permalink: /docs/AEO/software-systems/laboratory-exercise-1/ide-installation/jdk
---

# Installing JDK

Before you start writing Java programs, you need to install the JDK. You can download it from:

[https://www.oracle.com/java/technologies/downloads/](https://www.oracle.com/java/technologies/downloads/)

Run the installer and follow the steps. It is recommended to install in the default directories. After installation, you need to configure the environment variables.

Environment variables can be set either via the command line or by following this path in the Windows GUI:

My Computer / Right-click -> Properties -> Advanced System Settings -> Environment Variables

Or search in Windows: Edit the system environment variables

<img width="346" height="372" alt="Screenshot 2026-01-22 140654" src="https://github.com/user-attachments/assets/6a9b1ecf-7ea8-497b-a0a5-2747515dcb5e" /><br>

Click on Environment Variables ->

* Edit the Path variable and add the path to the `bin` folder inside the JDK installation directory.
* Add a new system variable `JAVA_HOME` pointing to the JDK installation directory.

<img width="746" height="217" alt="Screenshot 2026-01-22 141833" src="https://github.com/user-attachments/assets/d5b11211-c9f1-4dfc-8b4f-1f059405d9a5" /><br>

The paths to the respective folders can be easily copied in File Explorer (right-click on the folder path).
To verify that Java is installed successfully, restart your computer, open Command Prompt, and enter the command `java -version`.
