---
layout: default
title: Form Controller
parent: Лабораторно упражнение 5
grand_parent: Програмни системи
nav_order: 2
---

# Създаване на контролера `FormController.java`

Настоящото ръководство описва поетапно създаването на класа `FormController`, който управлява логиката на потребителския интерфейс, дефиниран във `form-view.fxml`. Изложението следва същия академичен и последователен стил, както при описанието на FXML файла, така че всяка стъпка да бъде ясно проследима и аргументирана.

Контролерът има за задача да:
- свърже визуалните компоненти от FXML файла с Java логика;
- следи въвеждането на данни в полетата;
- актуализира визуалния преглед на заявлението;
- валидира въведените стойности;
- управлява бутоните за потвърждение и изчистване;
- показва състояние и грешки чрез статусния етикет.

По този начин `FormController` изпълнява ролята на посредник между потребителския интерфейс и поведението на приложението.

---

## Стъпка 1. Създаване на файла `FormController.java`

Първата задача е да се създаде Java класът на контролера в пакет:

`bg.tu_varna.sit.ps.lab5.task1.controller`

### Очакван резултат

```text
bg/tu_varna/sit/ps/lab5/task1/controller/FormController.java
```

### Начален код

```java
package bg.tu_varna.sit.ps.lab5.task1.controller;

public class FormController {

}
```

Това е минималната основа на контролера. На този етап класът все още не съдържа нито полета, нито методи, но вече е подготвен да бъде използван от FXML файла чрез атрибута `fx:controller`.

---

## Стъпка 2. Добавяне на необходимите импорти и шаблонен текст

След създаването на класа трябва да се добавят необходимите импорти за компонентите, които ще бъдат използвани в контролера. Освен това се дефинира константа `template`, която ще служи като стойност по подразбиране в предварителния преглед, когато дадено поле е празно.

### Код

```java
package bg.tu_varna.sit.ps.lab5.task1.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class FormController {

    private final static String template = "..............................................";

}
```

Импортът `javafx.fxml.FXML` е необходим, за да може чрез анотацията `@FXML` да се осъществи връзката между елементите от FXML файла и полетата или методите в контролера.

Константата `template` има важна роля за визуализацията. Когато потребителят още не е попълнил дадено поле, в прегледа ще се показва този шаблон вместо празен текст.

---

## Стъпка 3. Дефиниране на полетата за връзка с FXML елементите

Следващата стъпка е да се добавят всички полета, чрез които контролерът ще получава достъп до визуалните компоненти. Те трябва да съответстват точно на `fx:id` идентификаторите, дефинирани във FXML файла.

### Код

```java
package bg.tu_varna.sit.ps.lab5.task1.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class FormController {

    private final static String template = "..............................................";

    @FXML
    private TextField facultyNumberField;
    @FXML
    private TextField studentNameField;
    @FXML
    private TextField specialtyField;

    @FXML
    private ToggleButton studyFormToggle;

    @FXML
    private TextArea requestTextArea;

    @FXML
    private CheckBox rectorCheckBox;
    @FXML
    private CheckBox deanCheckBox;
    @FXML
    private CheckBox headCheckBox;

    @FXML
    private RadioButton leaveRadio;
    @FXML
    private RadioButton hoursRadio;

    @FXML
    private Label facultyNumberPreviewLabel;
    @FXML
    private Label studentNamePreviewLabel;
    @FXML
    private Label specialtyPreviewLabel;
    @FXML
    private Label studyFormPreviewLabel;
    @FXML
    private Label receiverPreviewLabel;
    @FXML
    private Label requestTypePreviewLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label requestTextLabel;

    @FXML
    private Button confirmButton;
}
```

Всички полета, които са свързани с FXML файла, са означени с анотация `@FXML`. Това е задължително условие, когато полетата са `private`, защото по този начин JavaFX може да ги инжектира при зареждане на интерфейса.

Тук полетата могат да се разделят логически на няколко групи:
- входни компоненти – `TextField`, `TextArea`, `ToggleButton`, `CheckBox`, `RadioButton`;
- preview етикети – `Label`;
- бутон за потвърждение – `Button`.

---

## Стъпка 4. Добавяне на метода `initialize()`

След като всички компоненти са свързани, трябва да се дефинира началната инициализация на контролера. В JavaFX методът `initialize()` се извиква автоматично след зареждане на FXML файла и след инжектиране на всички `@FXML` елементи.

### Код

```java
@FXML
public void initialize() {
    initTextFields();
    initButtons();
    initValidators();
}
```

### Резултат от стъпка 4

```java
package bg.tu_varna.sit.ps.lab5.task1.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class FormController {

    private final static String template = "..............................................";

    @FXML
    private TextField facultyNumberField;
    @FXML
    private TextField studentNameField;
    @FXML
    private TextField specialtyField;

    @FXML
    private ToggleButton studyFormToggle;

    @FXML
    private TextArea requestTextArea;

    @FXML
    private CheckBox rectorCheckBox;
    @FXML
    private CheckBox deanCheckBox;
    @FXML
    private CheckBox headCheckBox;

    @FXML
    private RadioButton leaveRadio;
    @FXML
    private RadioButton hoursRadio;

    @FXML
    private Label facultyNumberPreviewLabel;
    @FXML
    private Label studentNamePreviewLabel;
    @FXML
    private Label specialtyPreviewLabel;
    @FXML
    private Label studyFormPreviewLabel;
    @FXML
    private Label receiverPreviewLabel;
    @FXML
    private Label requestTypePreviewLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label requestTextLabel;

    @FXML
    private Button confirmButton;

    @FXML
    public void initialize() {
        initTextFields();
    }
}
```

Методът `initialize()` не се извиква ръчно. Неговата задача е да стартира подготвящата логика на контролера. За по-добра структура кодът е разделен на три помощни метода:
- `initTextFields()` – инициализация на слушателите за текстовите полета;
- `initButtons()` – инициализация на бутоните;
- `initValidators()` – инициализация на валидиращите проверки.

Това решение подобрява четимостта и поддръжката на кода.

---

## Стъпка 5. Инициализация на текстовите полета и preview етикетите

Следващата стъпка е да се реализира обновяването на предварителния преглед при промяна на текстовите полета. Това се прави чрез слушатели на свойството `textProperty()`.

### Код

```java
private void initTextFields() {
    facultyNumberField.textProperty().addListener(
            (obs, oldVal, newVal) -> {
                facultyNumberPreviewLabel.setText(getTextOrDefault(newVal));
            });

    studentNameField.textProperty().addListener(
            (obs, oldVal, newVal) -> {
                studentNamePreviewLabel.setText(getTextOrDefault(newVal));
            });

    specialtyField.textProperty().addListener(
            (obs, oldVal, newVal) -> {
                specialtyPreviewLabel.setText(getTextOrDefault(newVal));
            });

    requestTextArea.textProperty().addListener(
            (obs, oldVal, newVal) -> {
                requestTextLabel.setText(getTextOrDefault(newVal));
            });
}

private String getTextOrDefault(String value) {
    return value.isBlank() ? FormController.template : value;
}
```

### Резултат от стъпка 5

```java
package bg.tu_varna.sit.ps.lab5.task1.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class FormController {

    private final static String template = "..............................................";

    @FXML
    private TextField facultyNumberField;
    @FXML
    private TextField studentNameField;
    @FXML
    private TextField specialtyField;

    @FXML
    private ToggleButton studyFormToggle;

    @FXML
    private TextArea requestTextArea;

    @FXML
    private CheckBox rectorCheckBox;
    @FXML
    private CheckBox deanCheckBox;
    @FXML
    private CheckBox headCheckBox;

    @FXML
    private RadioButton leaveRadio;
    @FXML
    private RadioButton hoursRadio;

    @FXML
    private Label facultyNumberPreviewLabel;
    @FXML
    private Label studentNamePreviewLabel;
    @FXML
    private Label specialtyPreviewLabel;
    @FXML
    private Label studyFormPreviewLabel;
    @FXML
    private Label receiverPreviewLabel;
    @FXML
    private Label requestTypePreviewLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label requestTextLabel;

    @FXML
    private Button confirmButton;

    @FXML
    public void initialize() {
        initTextFields();
    }

    private void initTextFields() {
        facultyNumberField.textProperty().addListener(
                (obs, oldVal, newVal) -> {
                    facultyNumberPreviewLabel.setText(getTextOrDefault(newVal));
                });

        studentNameField.textProperty().addListener(
                (obs, oldVal, newVal) -> {
                    studentNamePreviewLabel.setText(getTextOrDefault(newVal));
                });

        specialtyField.textProperty().addListener(
                (obs, oldVal, newVal) -> {
                    specialtyPreviewLabel.setText(getTextOrDefault(newVal));
                });

        requestTextArea.textProperty().addListener(
                (obs, oldVal, newVal) -> {
                    requestTextLabel.setText(getTextOrDefault(newVal));
                });
    }

    private String getTextOrDefault(String value) {
        return value.isBlank() ? FormController.template : value;
    }
}
```


Тази стъпка реализира динамично обновяване на визуалния преглед. Всеки път когато потребителят въведе или изтрие текст, съответният preview етикет се променя автоматично. Методът `getTextOrDefault()` централизира логиката за избиране между въведената стойност и шаблонния текст.

---

## Стъпка 6. Инициализация на бутона за форма на обучение

След като текстовите полета вече обновяват предварителния преглед, трябва да се добави логика и за бутона `studyFormToggle`. Неговото състояние определя дали формата на обучение ще бъде визуализирана като „Редовно обучение“ или „Задочно обучение“.

### Код

```java
private void initButtons() {
    studyFormToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
        studyFormPreviewLabel.setText(
                newVal ? "Задочно обучение" : "Редовно обучение"
        );
    });
}
```

### Резултат от стъпка 6

```java
package bg.tu_varna.sit.ps.lab5.task1.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class FormController {

    private final static String template = "..............................................";

    @FXML
    private TextField facultyNumberField;
    @FXML
    private TextField studentNameField;
    @FXML
    private TextField specialtyField;

    @FXML
    private ToggleButton studyFormToggle;

    @FXML
    private TextArea requestTextArea;

    @FXML
    private CheckBox rectorCheckBox;
    @FXML
    private CheckBox deanCheckBox;
    @FXML
    private CheckBox headCheckBox;

    @FXML
    private RadioButton leaveRadio;
    @FXML
    private RadioButton hoursRadio;

    @FXML
    private Label facultyNumberPreviewLabel;
    @FXML
    private Label studentNamePreviewLabel;
    @FXML
    private Label specialtyPreviewLabel;
    @FXML
    private Label studyFormPreviewLabel;
    @FXML
    private Label receiverPreviewLabel;
    @FXML
    private Label requestTypePreviewLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label requestTextLabel;

    @FXML
    private Button confirmButton;

    @FXML
    public void initialize() {
        initTextFields();
        initButtons();
    }

    private void initButtons() {
        studyFormToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            studyFormPreviewLabel.setText(
                    newVal ? "Задочно обучение" : "Редовно обучение"
            );
        });
    }

    private void initTextFields() {
        facultyNumberField.textProperty().addListener(
                (obs, oldVal, newVal) -> {
                    facultyNumberPreviewLabel.setText(getTextOrDefault(newVal));
                });

        studentNameField.textProperty().addListener(
                (obs, oldVal, newVal) -> {
                    studentNamePreviewLabel.setText(getTextOrDefault(newVal));
                });

        specialtyField.textProperty().addListener(
                (obs, oldVal, newVal) -> {
                    specialtyPreviewLabel.setText(getTextOrDefault(newVal));
                });

        requestTextArea.textProperty().addListener(
                (obs, oldVal, newVal) -> {
                    requestTextLabel.setText(getTextOrDefault(newVal));
                });
    }

    private String getTextOrDefault(String value) {
        return value.isBlank() ? FormController.template : value;
    }
}
```

Тук се използва слушател върху свойството `selectedProperty()`. Когато бутонът бъде активиран, стойността `newVal` става `true`, а когато е изключен – `false`. Благодарение на тернарния оператор се задава компактно и четимо решение за избор между двата текста.

---

## Стъпка 7. Добавяне на валидиращи слушатели

Следващата важна стъпка е да се добавят слушатели, които ще извикват методи за валидация при всяка промяна в потребителския интерфейс. Така приложението ще реагира веднага при невалидни стойности.

### Код

```java
private void initValidators() {
    studentNameField.textProperty().addListener((obs, o, n) -> validateName());
    facultyNumberField.textProperty().addListener((obs, o, n) -> validateFaculty());
    specialtyField.textProperty().addListener((obs, o, n) -> validateSpecialty());

    requestTextArea.textProperty().addListener((obs, o, n) -> validateRequestText());

    rectorCheckBox.selectedProperty().addListener((obs, o, n) -> validateReceiver());
    deanCheckBox.selectedProperty().addListener((obs, o, n) -> validateReceiver());
    headCheckBox.selectedProperty().addListener((obs, o, n) -> validateReceiver());

    leaveRadio.selectedProperty().addListener((obs, o, n) -> validateRequest());
    hoursRadio.selectedProperty().addListener((obs, o, n) -> validateRequest());
}
```

### Резултат от стъпка 7

```java
package bg.tu_varna.sit.ps.lab5.task1.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class FormController {

    private final static String template = "..............................................";

    @FXML
    private TextField facultyNumberField;
    @FXML
    private TextField studentNameField;
    @FXML
    private TextField specialtyField;

    @FXML
    private ToggleButton studyFormToggle;

    @FXML
    private TextArea requestTextArea;

    @FXML
    private CheckBox rectorCheckBox;
    @FXML
    private CheckBox deanCheckBox;
    @FXML
    private CheckBox headCheckBox;

    @FXML
    private RadioButton leaveRadio;
    @FXML
    private RadioButton hoursRadio;

    @FXML
    private Label facultyNumberPreviewLabel;
    @FXML
    private Label studentNamePreviewLabel;
    @FXML
    private Label specialtyPreviewLabel;
    @FXML
    private Label studyFormPreviewLabel;
    @FXML
    private Label receiverPreviewLabel;
    @FXML
    private Label requestTypePreviewLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label requestTextLabel;

    @FXML
    private Button confirmButton;

    @FXML
    public void initialize() {
        initTextFields();
        initButtons();
        initValidators();
    }

    private void initButtons() {
        studyFormToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            studyFormPreviewLabel.setText(
                    newVal ? "Задочно обучение" : "Редовно обучение"
            );
        });
    }

    private void initValidators() {
        studentNameField.textProperty().addListener((obs, o, n) -> validateName());
        facultyNumberField.textProperty().addListener((obs, o, n) -> validateFaculty());
        specialtyField.textProperty().addListener((obs, o, n) -> validateSpecialty());

        requestTextArea.textProperty().addListener((obs, o, n) -> validateRequestText());

        rectorCheckBox.selectedProperty().addListener((obs, o, n) -> validateReceiver());
        deanCheckBox.selectedProperty().addListener((obs, o, n) -> validateReceiver());
        headCheckBox.selectedProperty().addListener((obs, o, n) -> validateReceiver());

        leaveRadio.selectedProperty().addListener((obs, o, n) -> validateRequest());
        hoursRadio.selectedProperty().addListener((obs, o, n) -> validateRequest());
    }

    private void initTextFields() {
        facultyNumberField.textProperty().addListener(
                (obs, oldVal, newVal) -> {
                    facultyNumberPreviewLabel.setText(getTextOrDefault(newVal));
                });

        studentNameField.textProperty().addListener(
                (obs, oldVal, newVal) -> {
                    studentNamePreviewLabel.setText(getTextOrDefault(newVal));
                });

        specialtyField.textProperty().addListener(
                (obs, oldVal, newVal) -> {
                    specialtyPreviewLabel.setText(getTextOrDefault(newVal));
                });

        requestTextArea.textProperty().addListener(
                (obs, oldVal, newVal) -> {
                    requestTextLabel.setText(getTextOrDefault(newVal));
                });
    }

    private String getTextOrDefault(String value) {
        return value.isBlank() ? FormController.template : value;
    }
}
```

Валидацията е организирана така, че да се изпълнява автоматично при всяка промяна в съответния компонент. По този начин контролерът реагира в реално време и потребителят получава незабавна обратна връзка дали въведените данни са коректни.

---

## Стъпка 8. Добавяне на методи за избор на получател и тип заявление

Следва реализирането на методите, които се извикват чрез `onAction` от FXML файла. Те обновяват съответните етикети в областта за предварителен преглед.

### Код

```java
@FXML
private void selectedReceivers() {
    StringBuilder stringBuilder = new StringBuilder();

    if (rectorCheckBox.isSelected()) {
        stringBuilder.append("Ректор, ");
    }
    if (deanCheckBox.isSelected()) {
        stringBuilder.append("Декан, ");
    }
    if (headCheckBox.isSelected()) {
        stringBuilder.append("Ръководител катедра, ");
    }

    if (stringBuilder.isEmpty()) {
        receiverPreviewLabel.setText(template);
        return;
    }

    receiverPreviewLabel.setText(
            stringBuilder.substring(0, stringBuilder.length() - 2)
    );
}

@FXML
private void selectedRequestType() {
    String result = template;

    if (leaveRadio.isSelected()) {
        result = "Академичен отпуск";
    }
    if (hoursRadio.isSelected()) {
        result = "Отработване на часове";
    }

    requestTypePreviewLabel.setText(result);
}
```

### Резултат от стъпка 8

```java
package bg.tu_varna.sit.ps.lab5.task1.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class FormController {

    private final static String template = "..............................................";

    @FXML
    private TextField facultyNumberField;
    @FXML
    private TextField studentNameField;
    @FXML
    private TextField specialtyField;

    @FXML
    private ToggleButton studyFormToggle;

    @FXML
    private TextArea requestTextArea;

    @FXML
    private CheckBox rectorCheckBox;
    @FXML
    private CheckBox deanCheckBox;
    @FXML
    private CheckBox headCheckBox;

    @FXML
    private RadioButton leaveRadio;
    @FXML
    private RadioButton hoursRadio;

    @FXML
    private Label facultyNumberPreviewLabel;
    @FXML
    private Label studentNamePreviewLabel;
    @FXML
    private Label specialtyPreviewLabel;
    @FXML
    private Label studyFormPreviewLabel;
    @FXML
    private Label receiverPreviewLabel;
    @FXML
    private Label requestTypePreviewLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label requestTextLabel;

    @FXML
    private Button confirmButton;

    @FXML
    public void initialize() {
        initTextFields();
        initButtons();
        initValidators();
    }

    private void initButtons() {
        studyFormToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            studyFormPreviewLabel.setText(
                    newVal ? "Задочно обучение" : "Редовно обучение"
            );
        });
    }

    private void initValidators() {
        studentNameField.textProperty().addListener((obs, o, n) -> validateName());
        facultyNumberField.textProperty().addListener((obs, o, n) -> validateFaculty());
        specialtyField.textProperty().addListener((obs, o, n) -> validateSpecialty());

        requestTextArea.textProperty().addListener((obs, o, n) -> validateRequestText());

        rectorCheckBox.selectedProperty().addListener((obs, o, n) -> validateReceiver());
        deanCheckBox.selectedProperty().addListener((obs, o, n) -> validateReceiver());
        headCheckBox.selectedProperty().addListener((obs, o, n) -> validateReceiver());

        leaveRadio.selectedProperty().addListener((obs, o, n) -> validateRequest());
        hoursRadio.selectedProperty().addListener((obs, o, n) -> validateRequest());
    }

    private void initTextFields() {
        facultyNumberField.textProperty().addListener(
                (obs, oldVal, newVal) -> {
                    facultyNumberPreviewLabel.setText(getTextOrDefault(newVal));
                });

        studentNameField.textProperty().addListener(
                (obs, oldVal, newVal) -> {
                    studentNamePreviewLabel.setText(getTextOrDefault(newVal));
                });

        specialtyField.textProperty().addListener(
                (obs, oldVal, newVal) -> {
                    specialtyPreviewLabel.setText(getTextOrDefault(newVal));
                });

        requestTextArea.textProperty().addListener(
                (obs, oldVal, newVal) -> {
                    requestTextLabel.setText(getTextOrDefault(newVal));
                });
    }

    private String getTextOrDefault(String value) {
        return value.isBlank() ? FormController.template : value;
    }

    @FXML
    private void selectedReceivers() {
        StringBuilder stringBuilder = new StringBuilder();

        if (rectorCheckBox.isSelected()) {
            stringBuilder.append("Ректор, ");
        }
        if (deanCheckBox.isSelected()) {
            stringBuilder.append("Декан, ");
        }
        if (headCheckBox.isSelected()) {
            stringBuilder.append("Ръководител катедра, ");
        }

        if (stringBuilder.isEmpty()) {
            receiverPreviewLabel.setText(template);
            return;
        }

        receiverPreviewLabel.setText(
                stringBuilder.substring(0, stringBuilder.length() - 2)
        );
    }

    @FXML
    private void selectedRequestType() {
        String result = template;

        if (leaveRadio.isSelected()) {
            result = "Академичен отпуск";
        }
        if (hoursRadio.isSelected()) {
            result = "Отработване на часове";
        }

        requestTypePreviewLabel.setText(result);
    }
}
```

Методът `selectedReceivers()` събира избраните стойности в `StringBuilder`. Това е удобен начин за конструиране на текст, когато има повече от една възможна стойност. Ако няма избран получател, в preview етикета се задава шаблонният текст.

Методът `selectedRequestType()` е по-прост, защото използва `RadioButton` елементи и в даден момент може да има само един избран тип заявление.

---

## Стъпка 9. Добавяне на помощни и валидиращи методи

След като контролерът вече обновява прегледа, остава да се реализира логиката за валидиране на въведените данни и за управление на състоянието на бутона за потвърждение.

### Код

```java
private void setStatusLabel(String successMessage, String errorMessage, boolean disableButton) {
    statusLabel.setText(disableButton ? errorMessage : successMessage);
    confirmButton.setDisable(disableButton);
}

private boolean isDigit(String faculty) {
    for (char c : faculty.toCharArray()) {
        if (!Character.isDigit(c)) {
            return false;
        }
    }
    return true;
}

private boolean validateName() {
    String name = studentNameField.getText().trim();
    String successMessage = "Състояние: Въведено име";
    String errorMessage = "Грешка: Името трябва да е повече от 3 символа";
    boolean isValid = name.length() >= 3;
    setStatusLabel(successMessage, errorMessage, !isValid);
    return isValid;
}

private boolean validateFaculty() {
    String faculty = facultyNumberField.getText().trim();
    String successMessage = "Състояние: Въведен факултетен номер";
    String errorMessage = "Грешка: Факултетният номер трябва да е 8 цифри";
    boolean isValid = faculty.length() == 8 && isDigit(faculty);
    setStatusLabel(successMessage, errorMessage, !isValid);
    return isValid;
}

private boolean validateSpecialty() {
    String specialty = specialtyField.getText().trim();
    String successMessage = "Състояние: Въведена специалност";
    String errorMessage = "Грешка: Специалността трябва да е повече от 2 букви";
    boolean isValid = specialty.length() >= 2;
    setStatusLabel(successMessage, errorMessage, !isValid);
    return isValid;
}

private boolean validateReceiver() {
    String successMessage = "Състояние: Избран получател";
    String errorMessage = "Грешка: Изберете получател";
    boolean isValid = rectorCheckBox.isSelected() || deanCheckBox.isSelected() || headCheckBox.isSelected();
    setStatusLabel(successMessage, errorMessage, !isValid);
    return isValid;
}

private boolean validateRequest() {
    String successMessage = "Състояние: Избран вид заявление";
    String errorMessage = "Грешка: Изберете вид заявление";
    boolean isValid = leaveRadio.isSelected() || hoursRadio.isSelected();
    setStatusLabel(successMessage, errorMessage, !isValid);
    return isValid;
}

private boolean validateRequestText() {
    String text = requestTextArea.getText().trim();
    String successMessage = "Състояние: Попълнен текст на заявлението";
    String errorMessage = "Грешка: Текстът на заявлението трябва да е поне 10 символа";
    boolean isValid = text.length() >= 10;
    setStatusLabel(successMessage, errorMessage, !isValid);
    return isValid;
}
```

### Резултат от стъпка 9

```java
package bg.tu_varna.sit.ps.lab5.task1.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class FormController {

    private final static String template = "..............................................";

    @FXML
    private TextField facultyNumberField;
    @FXML
    private TextField studentNameField;
    @FXML
    private TextField specialtyField;

    @FXML
    private ToggleButton studyFormToggle;

    @FXML
    private TextArea requestTextArea;

    @FXML
    private CheckBox rectorCheckBox;
    @FXML
    private CheckBox deanCheckBox;
    @FXML
    private CheckBox headCheckBox;

    @FXML
    private RadioButton leaveRadio;
    @FXML
    private RadioButton hoursRadio;

    @FXML
    private Label facultyNumberPreviewLabel;
    @FXML
    private Label studentNamePreviewLabel;
    @FXML
    private Label specialtyPreviewLabel;
    @FXML
    private Label studyFormPreviewLabel;
    @FXML
    private Label receiverPreviewLabel;
    @FXML
    private Label requestTypePreviewLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label requestTextLabel;

    @FXML
    private Button confirmButton;

    @FXML
    public void initialize() {
        initTextFields();
        initButtons();
        initValidators();
    }

    private void initButtons() {
        studyFormToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            studyFormPreviewLabel.setText(
                    newVal ? "Задочно обучение" : "Редовно обучение"
            );
        });
    }

    private void initValidators() {
        studentNameField.textProperty().addListener((obs, o, n) -> validateName());
        facultyNumberField.textProperty().addListener((obs, o, n) -> validateFaculty());
        specialtyField.textProperty().addListener((obs, o, n) -> validateSpecialty());

        requestTextArea.textProperty().addListener((obs, o, n) -> validateRequestText());

        rectorCheckBox.selectedProperty().addListener((obs, o, n) -> validateReceiver());
        deanCheckBox.selectedProperty().addListener((obs, o, n) -> validateReceiver());
        headCheckBox.selectedProperty().addListener((obs, o, n) -> validateReceiver());

        leaveRadio.selectedProperty().addListener((obs, o, n) -> validateRequest());
        hoursRadio.selectedProperty().addListener((obs, o, n) -> validateRequest());
    }

    private void initTextFields() {
        facultyNumberField.textProperty().addListener(
                (obs, oldVal, newVal) -> {
                    facultyNumberPreviewLabel.setText(getTextOrDefault(newVal));
                });

        studentNameField.textProperty().addListener(
                (obs, oldVal, newVal) -> {
                    studentNamePreviewLabel.setText(getTextOrDefault(newVal));
                });

        specialtyField.textProperty().addListener(
                (obs, oldVal, newVal) -> {
                    specialtyPreviewLabel.setText(getTextOrDefault(newVal));
                });

        requestTextArea.textProperty().addListener(
                (obs, oldVal, newVal) -> {
                    requestTextLabel.setText(getTextOrDefault(newVal));
                });
    }

    private String getTextOrDefault(String value) {
        return value.isBlank() ? FormController.template : value;
    }

    @FXML
    private void selectedReceivers() {
        StringBuilder stringBuilder = new StringBuilder();

        if (rectorCheckBox.isSelected()) {
            stringBuilder.append("Ректор, ");
        }
        if (deanCheckBox.isSelected()) {
            stringBuilder.append("Декан, ");
        }
        if (headCheckBox.isSelected()) {
            stringBuilder.append("Ръководител катедра, ");
        }

        if (stringBuilder.isEmpty()) {
            receiverPreviewLabel.setText(template);
            return;
        }

        receiverPreviewLabel.setText(
                stringBuilder.substring(0, stringBuilder.length() - 2)
        );
    }

    @FXML
    private void selectedRequestType() {
        String result = template;

        if (leaveRadio.isSelected()) {
            result = "Академичен отпуск";
        }
        if (hoursRadio.isSelected()) {
            result = "Отработване на часове";
        }

        requestTypePreviewLabel.setText(result);
    }

    private void setStatusLabel(String successMessage, String errorMessage, boolean disableButton) {
        statusLabel.setText(disableButton ? errorMessage : successMessage);
        confirmButton.setDisable(disableButton);
    }

    private boolean isDigit(String faculty) {
        for (char c : faculty.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    private boolean validateName() {
        String name = studentNameField.getText().trim();
        String successMessage = "Състояние: Въведено име";
        String errorMessage = "Грешка: Името трябва да е повече от 3 символа";
        boolean isValid = name.length() >= 3;
        setStatusLabel(successMessage, errorMessage, !isValid);
        return isValid;
    }

    private boolean validateFaculty() {
        String faculty = facultyNumberField.getText().trim();
        String successMessage = "Състояние: Въведен факултетен номер";
        String errorMessage = "Грешка: Факултетният номер трябва да е 8 цифри";
        boolean isValid = faculty.length() == 8 && isDigit(faculty);
        setStatusLabel(successMessage, errorMessage, !isValid);
        return isValid;
    }

    private boolean validateSpecialty() {
        String specialty = specialtyField.getText().trim();
        String successMessage = "Състояние: Въведена специалност";
        String errorMessage = "Грешка: Специалността трябва да е повече от 2 букви";
        boolean isValid = specialty.length() >= 2;
        setStatusLabel(successMessage, errorMessage, !isValid);
        return isValid;
    }

    private boolean validateReceiver() {
        String successMessage = "Състояние: Избран получател";
        String errorMessage = "Грешка: Изберете получател";
        boolean isValid = rectorCheckBox.isSelected() || deanCheckBox.isSelected() || headCheckBox.isSelected();
        setStatusLabel(successMessage, errorMessage, !isValid);
        return isValid;
    }

    private boolean validateRequest() {
        String successMessage = "Състояние: Избран вид заявление";
        String errorMessage = "Грешка: Изберете вид заявление";
        boolean isValid = leaveRadio.isSelected() || hoursRadio.isSelected();
        setStatusLabel(successMessage, errorMessage, !isValid);
        return isValid;
    }

    private boolean validateRequestText() {
        String text = requestTextArea.getText().trim();
        String successMessage = "Състояние: Попълнен текст на заявлението";
        String errorMessage = "Грешка: Текстът на заявлението трябва да е поне 10 символа";
        boolean isValid = text.length() >= 10;
        setStatusLabel(successMessage, errorMessage, !isValid);
        return isValid;
    }
}
```

Тук са събрани всички помощни методи за проверка на входа. По този начин основните обработчици остават кратки и четими, а логиката за валидиране е изведена в отделни, ясно именувани методи.

---

## Стъпка 10. Добавяне на обработчиците за потвърждение и изчистване и финален код

Последната стъпка е да се реализират действията при натискане на бутоните „Потвърди“ и „Почисти“. След това контролерът вече е завършен.

### Код

```java
@FXML
private void handleConfirm() {
    if(!validateName()) return;
    if(!validateFaculty()) return;
    if(!validateSpecialty()) return;
    if(!validateReceiver()) return;
    if(!validateRequest()) return;
    if(!validateRequestText()) return;

    setStatusLabel("Състояние: Данните са потвърдени", "", false);
}

@FXML
private void handleClear() {
    facultyNumberField.clear();
    studentNameField.clear();
    specialtyField.clear();
    requestTextArea.clear();

    studyFormToggle.setSelected(false);

    rectorCheckBox.setSelected(false);
    deanCheckBox.setSelected(false);
    headCheckBox.setSelected(false);

    leaveRadio.setSelected(false);
    hoursRadio.setSelected(false);

    requestTextArea.clear();

    facultyNumberPreviewLabel.setText(template);
    studentNamePreviewLabel.setText(template);
    specialtyPreviewLabel.setText(template);
    studyFormPreviewLabel.setText(template);
    receiverPreviewLabel.setText(template);
    requestTypePreviewLabel.setText(template);
    requestTextLabel.setText(template);

    statusLabel.setText("Състояние: Формата е изчистена");
    confirmButton.setDisable(false);
}
```

### Краен резултат

```java
package bg.tu_varna.sit.ps.lab5.task1.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class FormController {

    private final static String template = "..............................................";

    @FXML
    private TextField facultyNumberField;
    @FXML
    private TextField studentNameField;
    @FXML
    private TextField specialtyField;

    @FXML
    private ToggleButton studyFormToggle;

    @FXML
    private TextArea requestTextArea;

    @FXML
    private CheckBox rectorCheckBox;
    @FXML
    private CheckBox deanCheckBox;
    @FXML
    private CheckBox headCheckBox;

    @FXML
    private RadioButton leaveRadio;
    @FXML
    private RadioButton hoursRadio;

    @FXML
    private Label facultyNumberPreviewLabel;
    @FXML
    private Label studentNamePreviewLabel;
    @FXML
    private Label specialtyPreviewLabel;
    @FXML
    private Label studyFormPreviewLabel;
    @FXML
    private Label receiverPreviewLabel;
    @FXML
    private Label requestTypePreviewLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label requestTextLabel;

    @FXML
    private Button confirmButton;

    @FXML
    public void initialize() {
        initTextFields();
        initButtons();
        initValidators();
    }

    private void initButtons() {
        studyFormToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            studyFormPreviewLabel.setText(
                    newVal ? "Задочно обучение" : "Редовно обучение"
            );
        });
    }

    private void initValidators() {
        studentNameField.textProperty().addListener((obs,o,n) -> validateName());
        facultyNumberField.textProperty().addListener((obs,o,n) -> validateFaculty());
        specialtyField.textProperty().addListener((obs,o,n) -> validateSpecialty());

        requestTextArea.textProperty().addListener((obs,o,n) -> validateRequestText());

        rectorCheckBox.selectedProperty().addListener((obs,o,n) -> validateReceiver());
        deanCheckBox.selectedProperty().addListener((obs,o,n) -> validateReceiver());
        headCheckBox.selectedProperty().addListener((obs,o,n) -> validateReceiver());

        leaveRadio.selectedProperty().addListener((obs,o,n) -> validateRequest());
        hoursRadio.selectedProperty().addListener((obs,o,n) -> validateRequest());
    }

    private void initTextFields() {
        facultyNumberField.textProperty().addListener(
                (obs, oldVal, newVal) -> {
                    facultyNumberPreviewLabel.setText(getTextOrDefault(newVal));
                });

        studentNameField.textProperty().addListener(
                (obs, oldVal, newVal) -> {
                    studentNamePreviewLabel.setText(getTextOrDefault(newVal));
                });

        specialtyField.textProperty().addListener(
                (obs, oldVal, newVal) -> {
                    specialtyPreviewLabel.setText(getTextOrDefault(newVal));
                });

        requestTextArea.textProperty().addListener(
                (obs, oldVal, newVal) -> {
                    requestTextLabel.setText(getTextOrDefault(newVal));
                });
    }

    private String getTextOrDefault(String value) {
        return value.isBlank() ? FormController.template : value;
    }

    @FXML
    private void selectedReceivers() {
        StringBuilder stringBuilder = new StringBuilder();

        if (rectorCheckBox.isSelected()) {
            stringBuilder.append("Ректор, ");
        }
        if (deanCheckBox.isSelected()) {
            stringBuilder.append("Декан, ");
        }
        if (headCheckBox.isSelected()) {
            stringBuilder.append("Ръководител катедра, ");
        }

        if (stringBuilder.isEmpty()) {
            stringBuilder.append(template);
        }

        receiverPreviewLabel.setText(stringBuilder.substring(0, stringBuilder.length() - 2));
    }

    @FXML
    private void selectedRequestType() {
        String result = template;
        if (leaveRadio.isSelected()) {
            result = "Академичен отпуск";
        }
        if (hoursRadio.isSelected()) {
            result = "Отработване на часове";
        }

        requestTypePreviewLabel.setText(result);
    }

    private void setStatusLabel(String successMessage, String errorMessage, boolean disableButton) {
        statusLabel.setText(disableButton ? errorMessage : successMessage);
        confirmButton.setDisable(disableButton);
    }

    @FXML
    private void handleConfirm() {
        if(!validateName()) return;
        if(!validateFaculty()) return;
        if(!validateSpecialty()) return;
        if(!validateReceiver()) return;
        if(!validateRequest()) return;
        if(!validateRequestText()) return;

        setStatusLabel("Състояние: Данните са потвърдени", "", false);
    }

    @FXML
    private void handleClear() {
        facultyNumberField.clear();
        studentNameField.clear();
        specialtyField.clear();
        requestTextArea.clear();

        studyFormToggle.setSelected(false);

        rectorCheckBox.setSelected(false);
        deanCheckBox.setSelected(false);
        headCheckBox.setSelected(false);

        leaveRadio.setSelected(false);
        hoursRadio.setSelected(false);

        requestTextArea.clear();

        facultyNumberPreviewLabel.setText(template);
        studentNamePreviewLabel.setText(template);
        specialtyPreviewLabel.setText(template);
        studyFormPreviewLabel.setText(template);
        receiverPreviewLabel.setText(template);
        requestTypePreviewLabel.setText(template);
        requestTextLabel.setText(template);

        statusLabel.setText("Състояние: Формата е изчистена");
        confirmButton.setDisable(false);
    }

    private boolean isDigit(String faculty) {
        for (char c : faculty.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    private boolean validateName() {
        String name = studentNameField.getText().trim();
        String successMessage = "Състояние: Въведено име";
        String errorMessage = "Грешка: Името трябва да е повече от 3 символа";
        boolean isValid = name.length() >= 3;
        setStatusLabel(successMessage, errorMessage, !isValid);
        return isValid;
    }

    private boolean validateFaculty() {
        String faculty = facultyNumberField.getText().trim();
        String successMessage = "Състояние: Въведен факултетен номер";
        String errorMessage = "Грешка: Факултетният номер трябва да е 8 цифри";
        boolean isValid = faculty.length() == 8 && isDigit(faculty);
        setStatusLabel(successMessage, errorMessage, !isValid);
        return isValid;
    }

    private boolean validateSpecialty() {
        String specialty = specialtyField.getText().trim();
        String successMessage = "Състояние: Въведена специалност";
        String errorMessage = "Грешка: Специалността трябва да е повече от 2 букви";
        boolean isValid = specialty.length() >= 2;
        setStatusLabel(successMessage, errorMessage, !isValid);
        return isValid;
    }

    private boolean validateReceiver() {
        String successMessage = "Състояние: Избран получател";
        String errorMessage = "Грешка: Изберете получател";
        boolean isValid = (rectorCheckBox.isSelected() || deanCheckBox.isSelected() || headCheckBox.isSelected());
        setStatusLabel(successMessage, errorMessage, !isValid);
        return isValid;
    }

    private boolean validateRequest() {
        String successMessage = "Състояние: Избран вид заявление";
        String errorMessage = "Грешка: Изберете вид заявление";
        boolean isValid = (leaveRadio.isSelected() || hoursRadio.isSelected());
        setStatusLabel(successMessage, errorMessage, !isValid);
        return isValid;
    }

    private boolean validateRequestText() {
        String text = requestTextArea.getText().trim();
        String successMessage = "Състояние: Попълнен текст на заявлението";
        String errorMessage = "Грешка: Текстът на заявлението трябва да е поне 10 символа";
        boolean isValid = text.length() >= 10;
        setStatusLabel(successMessage, errorMessage, !isValid);
        return isValid;
    }
}
```

### Заключение

Чрез последователното изпълнение на описаните стъпки се създава завършен JavaFX контролер, който управлява поведението на формата, реализира динамична визуализация на данните и осигурява базова валидация на потребителския вход. Така контролерът изпълнява своята основна роля в архитектурата на приложението – да координира взаимодействието между потребителя и графичния интерфейс.
