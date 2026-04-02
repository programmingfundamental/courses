---
layout: default
title: Context Menu
parent: Лабораторно упражнение 8
grand_parent: Програмни системи
nav_order: 4
---

## 4. Context Menu

```java
ContextMenu contextMenu = new ContextMenu();
MenuItem clearItem = new MenuItem("Изчисти");

contextMenu.getItems().add(clearItem);
```

```java
TextArea textArea = new TextArea();
textArea.setContextMenu(contextMenu);
```

```java
clearItem.setOnAction(e -> {
    textArea.clear();
});
```