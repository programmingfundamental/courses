---
layout: default
title: Диалогови прозорци
parent: Лабораторно упражнение 8
grand_parent: Програмни системи
nav_order: 3
---

# 3. Диалогови прозорци (Dialogs) в JavaFX

Диалоговият прозорец представлява вторичен (помощен) прозорец, използван за взаимодействие с потребителя. Той служи за:

- показване на информация;
- извеждане на предупреждения;
- изискване на потвърждение;
- въвеждане на данни.

Диалоговите прозорци са важен елемент от графичния потребителски интерфейс, тъй като реализират контролирана комуникация между приложението и потребителя.

В JavaFX диалоговите прозорци могат да бъдат реализирани по два основни начина:

чрез готови класове (напр. Alert);
чрез създаване на самостоятелен прозорец (Stage), който често се описва чрез FXML

1. Основни понятия

1.1. Stage (прозорец)

Stage представлява самостоятелен прозорец в JavaFX. Всеки диалогов прозорец всъщност е нов Stage.

Характеристики:

- съдържа сцена (Scene);
- може да бъде главен или дъщерен;
- поддържа различни стилове и модалност.

1.2. Scene (декор)

Scene представлява контейнер за визуалните компоненти, които се показват в прозореца.

2. Реализация на диалогов прозорец

Диалогов прозорец може да бъде реализиран чрез:

- създаване на FXML файл (layout);
- зареждане на файла чрез FXMLLoader;
- създаване на нов Stage;
- асоцииране на сцена;
- визуализация.

2.1. Извикване диалог

```java
FXMLLoader loader = new FXMLLoader(getClass().getResource("dialog.fxml"));
Parent root = loader.load();

Stage dialogStage = new Stage();
dialogStage.setScene(new Scene(root));
dialogStage.setTitle("FXML Диалог");
dialogStage.show();
```

2.1. FXML файл за диалог

```java
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.control.Button?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.layout.VBox?>

<VBox xmlns:fx="http://javafx.com/fxml"
      fx:controller="com.example.DialogController"
      spacing="10" alignment="CENTER">
        <Label text="Това е диалогов прозорец"/>
        <Button text="Затвори" onAction="#handleClose"/>
</VBox>
```

2.1. Контроллер

```java
package bg.tu_varna.sit.ps.lab8.task1.example;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class DialogController {

    @FXML
    private Button closeButton;

    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
```

Всеки елемент на изгледа в свързан с Декора, в който е разположен. На свой ред всеки декор е свързан с прозореца, в който се намира.

    - Метода getScene извлича обекта надекора в който се намира графичния елемент
    - Метода getWindow извлича прозореца, в който се намира декора


4. Дъщерен прозорец (Child Window)

Дъщерният прозорец е диалог, който е логически свързан с главния прозорец.

Тази връзка се реализира чрез:

4.1. Owner

```java
dialogStage.initOwner(primaryStage);
```

Определя кой е родителският прозорец.

4.2. Modality

```java
dialogStage.initModality(Modality.WINDOW_MODAL);
```

Модалността определя дали потребителят може да взаимодейства с други прозорци.

Видове:

- NONE – няма ограничения;
- WINDOW_MODAL – блокира само родителския прозорец;
- APPLICATION_MODAL – блокира цялото приложение.

```java
Stage dialogStage = new Stage();
dialogStage.initOwner(primaryStage);
dialogStage.initModality(Modality.WINDOW_MODAL);

dialogStage.setScene(new Scene(root));
dialogStage.showAndWait();
```

4.3 Метода showAndWait()
- спира изпълнението на текущия поток;
- изчаква затварянето на прозореца;
- използва се при диалози, изискващи резултат.