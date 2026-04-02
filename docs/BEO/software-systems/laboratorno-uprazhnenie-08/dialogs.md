---
layout: default
title: Диалогови прозорци
parent: Лабораторно упражнение 8
grand_parent: Програмни системи
nav_order: 1
---

# JavaFX – Диалогови прозорци, изкачащи прозорци, меню и контекстно меню



### 1.5 FXML диалог

```java
FXMLLoader loader = new FXMLLoader(getClass().getResource("dialog.fxml"));
Parent root = loader.load();

Stage dialogStage = new Stage();
dialogStage.setScene(new Scene(root));
dialogStage.setTitle("FXML Диалог");
dialogStage.show();
```

---

### 1.6 Дъщерен прозорец

```java
Stage dialogStage = new Stage();
dialogStage.initOwner(primaryStage);
dialogStage.initModality(Modality.WINDOW_MODAL);

dialogStage.setScene(new Scene(root));
dialogStage.showAndWait();
```

---

### 1.7 Декорация

```java
Stage stage = new Stage();
stage.initStyle(StageStyle.UNDECORATED);
stage.setScene(new Scene(root));
stage.show();
```

---




