---
layout: default
title: Context Menu
parent: Лабораторно упражнение 8
grand_parent: Програмни системи
nav_order: 2
---

## 1. Диалогови прозорци (Dialogs)

### 1.1 Същност

Диалоговият прозорец представлява помощен прозорец, използван за комуникация с потребителя. Той може да показва информация, предупреждения или да изисква потвърждение за дадено действие.

В JavaFX диалоговите прозорци могат да бъдат реализирани по два основни начина:

- чрез готовия клас `Alert`;
- чрез създаване на отделен прозорец (`Stage`), често описан с FXML.

---

### 1.2 Видове диалогови прозорци

- **INFORMATION** – показва информационно съобщение;
- **WARNING** – сигнализира за потенциален проблем;
- **ERROR** – показва грешка;
- **CONFIRMATION** – изисква избор от потребителя.

---

### 1.3 Основен пример и структура

```java
Alert alert = new Alert(Alert.AlertType.INFORMATION); 
// определя типа на диалога (INFORMATION → бутон OK)

alert.setTitle("Информация"); 
// задава заглавието на прозореца

alert.setHeaderText("Заглавие"); 
// задава заглавен текст

alert.setContentText("Това е информационно съобщение."); 
// задава съдържание

alert.showAndWait(); 
// показва и изчаква
```

---

### 1.4 Бутони

| Тип | Бутони |
|-----|--------|
| INFORMATION | OK |
| WARNING | OK |
| ERROR | OK |
| CONFIRMATION | OK, Cancel |

```java
Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
alert.setContentText("Сигурни ли сте?");

Optional<ButtonType> result = alert.showAndWait();

if (result.isPresent() && result.get() == ButtonType.OK) {
    System.out.println("OK");
} else {
    System.out.println("Cancel");
}
```

---