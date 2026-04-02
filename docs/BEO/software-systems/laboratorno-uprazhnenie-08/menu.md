---
layout: default
title: Menu
parent: Лабораторно упражнение 8
grand_parent: Програмни системи
nav_order: 1
---

## 3. Menu

```java
MenuBar menuBar = new MenuBar();
Menu fileMenu = new Menu("Файл");
MenuItem exitItem = new MenuItem("Изход");

fileMenu.getItems().add(exitItem);
menuBar.getMenus().add(fileMenu);
```

```java
exitItem.setOnAction(e -> {
    System.out.println("Exit");
});
```

---
