---
layout: default
title: Context Menu
parent: Лабораторно упражнение 8
grand_parent: Програмни системи
nav_order: 4
---

## 4. Диалогови прозорци (Dialogs)

### 4.1 Същност

Диалоговият прозорец представлява помощен прозорец, използван за комуникация с потребителя. Той може да показва информация, предупреждения или да изисква потвърждение за дадено действие.

В JavaFX диалоговите прозорци могат да бъдат реализирани по два основни начина:

- чрез готовия клас `Alert`;
- чрез създаване на отделен прозорец (`Stage`), често описан с FXML.

---

### 4.2 Видове диалогови прозорци

- **INFORMATION** – показва информационно съобщение;
- **WARNING** – сигнализира за потенциален проблем;
- **ERROR** – показва грешка;
- **CONFIRMATION** – изисква избор от потребителя.

---

### 4.3 Основен пример и структура

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

### 4.4 Бутони

| Тип | Бутони |
|-----|--------|
| INFORMATION | OK |
| WARNING | OK |
| ERROR | OK |
| CONFIRMATION | OK, Cancel |

```java
    private Optional<ButtonType> showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert.showAndWait();
    }
```

```java
    Optional<ButtonType> result = showAlert(Alert.AlertType.INFORMATION, "Информация", "Редактиране", "Успешно изпълнена корекция");
    if (result.isPresent() && result.get() == ButtonType.OK) {
        handleCancel(actionEvent);
    }
```

---