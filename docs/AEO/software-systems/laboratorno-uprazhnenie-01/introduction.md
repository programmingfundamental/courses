---
layout: default
title: Introduction to JavaFX
parent: Laboratory Exercise 1
grand_parent: Software Systems
nav_order: 1
#permalink: /docs/AEO/software-systems/laboratory-exercise-1/introduction
---

# Introduction to JavaFX

JavaFX is a modern Java library for building graphical user interfaces (GUI) for desktop applications. It allows you to create windows, controls (buttons, text fields, etc.), graphics, animations, and multimedia elements. JavaFX is designed to run on devices that support Java, including Windows, macOS, and Linux.

## JavaFX Architecture

JavaFX uses an internal layered architecture that separates responsibilities among different subsystems and allows the creation of high-performance, cross-platform graphical applications. This architecture describes how the JavaFX API communicates with the graphics engine, windowing system, multimedia, and operating system.

<br>
<p align="center">
<img width="700" height="380" alt="image" src="https://github.com/user-attachments/assets/4c162252-786c-42a6-8e70-cf9b66a2fba5" />
</p>
<br>

* **JavaFX API**: The top layer providing classes and packages for animations, UI controls, CSS styling, scene graph, events, multimedia, and application lifecycle.
* **Scene Graph**: Core of the graphical interface; a hierarchical tree of nodes (root, branch, leaf) representing visual components.
* **Quantum Toolkit**: Connects Prism (graphics engine) and Glass (window toolkit) with the API.
* **Prism**: Hardware-accelerated 2D/3D graphics engine; falls back to software rendering if GPU is unavailable.
* **Glass Windowing Toolkit**: Platform-dependent layer that manages windows, events, and surfaces.
* **WebView**: Embeds web content (HTML5, CSS, JavaScript) and allows interaction between Java and JavaScript.
* **Media Engine**: High-performance multimedia pipeline (GStreamer) for audio and video playback.

## Logical Structure of a JavaFX Application

<br>
<br>
<p align="center">
<img width="582" height="411" alt="Structure-of-JavaFx-Application" src="https://github.com/user-attachments/assets/240eae24-8197-4e18-8860-79565f9aad14" />
</p>
<br>
<br>

1. **Main Components**

* **Stage / Display** – Main application window.
* **Scene / Decor** – The content (interface) visible in the window.
* **Scene Graph** – Hierarchical structure of elements (nodes) that form the interface. Each element (e.g., Button, Label, TextField) is a node.

2. **Node**
   Each node represents a single element of the scene and has its own state, including:

* **ID** – unique identifier.
* **Style** – CSS styles applied to the node.
* **Effects** – visual effects (lighting, shadows, transformations).
* **Event Handlers** – event handling (clicks, mouse movement, key presses).
* **State** – current state (visibility, enabled/disabled, etc.).

## JavaFX Application Lifecycle

To create a JavaFX application, your class must extend the abstract class *javafx.application.Application*. The lifecycle is managed through four main methods: `init()`, `start(Stage stage)`, `stop()`, and the static method `launch()`.

<br>
<p align="center">
<img width="600" height="320" alt="529819736-0916eab7-119b-431b-9199-71615ef78ca0" src="https://github.com/user-attachments/assets/5785a1c2-d1b9-4a84-ba38-d0ede722e49c" />
</p>
<br>

1. **launch() – Start the application**
   This static method is called first. It prepares the JavaFX environment (initializes threads, graphics pipeline, scene, etc.) and then creates an instance of the class extending `Application`.
   *Important:* `launch()` is never called from `start()`; it is the entry point.

2. **init() – Initialization**
   Called after the instance is created. At this point, JavaFX API is not ready, so UI elements or scenes should not be created. Suitable for:

* loading configuration files
* preparing data
* initializing business logic unrelated to the interface

*Important:* Called once per application lifecycle.

3. **start(Stage stage) – Launching the interface**
   The main method where the UI is created. `Stage` is the main window. In `start()`:

* Create a `Scene` and add layout containers, buttons, text fields, and other UI elements.
* Set window title, size, and element visibility.
* Attach event handlers.
* Call `stage.show()` to display the window.

*Important:* Called after `init()` on the JavaFX Application Thread; UI operations are thread-safe.

4. **stop() – Closing the application**
   Called when the application is closing (e.g., user clicks "X"). Useful for:

* releasing resources (files, DB connections)
* stopping background threads
* saving state or settings

*Ensures safe application termination without data loss.*
