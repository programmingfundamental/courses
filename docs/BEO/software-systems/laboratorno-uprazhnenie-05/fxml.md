---
layout: default
title: Form fxml
parent: Лабораторно упражнение 5
grand_parent: Програмни системи
nav_order: 3
---

# Създаване на потребителския интерфейс

Настоящото ръководство описва поетапно създаването на FXML файл за графичен потребителски интерфейс на JavaFX приложение. Изложението следва академичен и последователен стил, така че всяка стъпка да бъде ясно обоснована както от гледна точка на структурата на кода, така и от гледна точка на предназначението на отделните визуални компоненти.

## Цел на упражнението

В рамките на това упражнение се изгражда форма за въвеждане и визуализиране на данни за студентско заявление. Интерфейсът трябва да позволява:
- въвеждане на основни данни за студента;
- избор на получател;
- избор на вид заявление;
- въвеждане на текст на заявлението;
- визуализиране на примерен преглед на документа;
- потвърждение и изчистване на формата.

За реализацията се използва контейнерът `BorderPane`, тъй като той позволява естествено разделяне на интерфейса на логически области: горна, лява, централна, дясна и долна част.

---

## Стъпка 1. Създаване на файла `form-view.fxml`

Първата задача е да се създаде FXML файлът, в който ще бъде описан графичният интерфейс. Файлът трябва да бъде разположен в ресурсната директория:

`bg/tu_varna/sit/ps/lab5/task1/form-view.fxml`

### Очакван резултат

```text
bg/tu_varna/sit/ps/lab5/task1/form-view.fxml
```

### Обяснение

FXML файлът служи за декларативно описание на потребителския интерфейс. Вместо визуалните компоненти да се създават програмно в Java кода, те се описват в XML структура, което подобрява четимостта, улеснява поддръжката и разделя логиката от представянето.

---

## Стъпка 2. Дефиниране на основния layout

Основният контейнер на приложението трябва да бъде `BorderPane`. Този контейнер е особено подходящ, когато интерфейсът е организиран в отделни зони с ясно разграничени функции.

Размерите на прозореца се задават чрез:
- `prefHeight="400.0"`
- `prefWidth="600.0"`

Контролерът се свързва с атрибута:

`fx:controller="bg.tu_varna.sit.ps.lab5.task1.controller.FormController"`

### Начален код

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import javafx.scene.text.Font?>
<?import javafx.geometry.Insets?>
<?import javafx.scene.paint.Color?>

<BorderPane xmlns="http://javafx.com/javafx"
            xmlns:fx="http://javafx.com/fxml"
            fx:controller="bg.tu_varna.sit.ps.lab5.task1.controller.FormController"
            prefHeight="400.0" prefWidth="600.0">

</BorderPane>
```

### Обяснение

В началото на файла се декларират необходимите импорти. Те позволяват използването на стандартни JavaFX елементи като:
- `Label`
- `TextField`
- `Button`
- `VBox`
- `HBox`
- `GridPane`
- `Insets`
- `Font`
- `Color`

Това е минималната основа, върху която постепенно ще бъде изграден целият интерфейс.

---

## Стъпка 3. Добавяне на горна секция (`top`)

След като е дефиниран основният контейнер, към неговия регион `top` се добавя заглавие на формата. За тази цел се използва елементът `Label`.

Текстът на заглавието е:

`Настройки на системата`

За по-ясно визуално открояване се задава по-голям и удебелен шрифт чрез елемента `font`.

### Код за шрифта

```xml
<font>
    <Font name="Arial Bold" size="24"/>
</font>
```

За да не бъде текстът прилепен до краищата на контейнера, се задават вътрешни отстояния чрез `padding` и `Insets`.

### Код за вътрешните отстояния

```xml
<padding>
    <Insets top="15" right="10" bottom="15" left="10"/>
</padding>
```

#### Резултат от стъпка 3

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<?import javafx.scene.text.Font?>
<?import javafx.geometry.Insets?>
<?import javafx.scene.paint.Color?>

<BorderPane xmlns="http://javafx.com/javafx"
            xmlns:fx="http://javafx.com/fxml"
            fx:controller="bg.tu_varna.sit.ps.lab5.task1.controller.FormController"
            prefHeight="400.0" prefWidth="600.0">

    <top>
        <Label text="Настройки на системата" BorderPane.alignment="CENTER">
            <font>
                <Font name="Arial Bold" size="24"/>
            </font>
            <padding>
                <Insets top="15" right="10" bottom="15" left="10"/>
            </padding>
        </Label>
    </top>

</BorderPane>
```

### Обяснение

Горната секция изпълнява ролята на заглавна част на интерфейса. Чрез `BorderPane.alignment="CENTER"` надписът се центрира в горния регион. Така още в началото на визуализацията се създава ясна ориентация за потребителя относно предназначението на формата.

---

## Стъпка 4. Добавяне на лява секция (`left`)

Левият регион на `BorderPane` ще съдържа полета за въвеждане на основните данни за студента. Понеже е необходимо елементите да бъдат подредени вертикално един под друг, най-подходящ избор е контейнерът `VBox`.

### Основна конфигурация на `VBox`

- `spacing="10"` – разстояние от 10 пиксела между вложените елементи;
- `prefWidth="240"` – предпочитана ширина 240 пиксела.

Добавят се и вътрешни отстояния:

```xml
<left>
    <VBox spacing="10" prefWidth="240">
        <padding>
            <Insets top="15" right="15" bottom="15" left="15"/>
        </padding>

    </VBox>
</left>
```

### Елементи, които трябва да бъдат добавени във `VBox`

1. `Label` със заглавие „Данни за студента“;
2. `TextField` с `fx:id="facultyNumberField"` и `promptText="Факултетен номер"`;
3. `TextField` с `fx:id="studentNameField"` и `promptText="Име на студент"`;
4. `ToggleButton` с `fx:id="studyFormToggle"` и текст `Редовно / Задочно`;
5. `TextField` с `fx:id="specialtyField"` и `promptText="Специалност"`.

#### Резултат от стъпка 4

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<?import javafx.scene.text.Font?>
<?import javafx.geometry.Insets?>
<?import javafx.scene.paint.Color?>

<BorderPane xmlns="http://javafx.com/javafx"
            xmlns:fx="http://javafx.com/fxml"
            fx:controller="bg.tu_varna.sit.ps.lab5.task1.controller.FormController"
            prefHeight="400.0" prefWidth="600.0">

    <top>
        <Label text="Настройки на системата" BorderPane.alignment="CENTER">
            <font>
                <Font name="Arial Bold" size="24"/>
            </font>
            <padding>
                <Insets top="15" right="10" bottom="15" left="10"/>
            </padding>
        </Label>
    </top>

    <left>
        <VBox spacing="10" prefWidth="240">
            <padding>
                <Insets top="15" right="15" bottom="15" left="15"/>
            </padding>

            <Label text="Данни за студента"/>
            <TextField fx:id="facultyNumberField" promptText="Факултетен номер"/>
            <TextField fx:id="studentNameField" promptText="Име на студент"/>
            <ToggleButton fx:id="studyFormToggle" text="Редовно / Задочно"/>
            <TextField fx:id="specialtyField" promptText="Специалност"/>
        </VBox>
    </left>

</BorderPane>
```

### Обяснение

Тази секция реализира входната част на формата. Използването на `promptText` подпомага потребителя, като показва указващ текст вътре в полето преди въвеждане на стойност. Задаването на `fx:id` е необходимо, за да може контролерът да достъпва съответните елементи и да извлича или променя техните стойности.

---

## Стъпка 5. Добавяне на централна секция (`center`)

Централната част на прозореца трябва да визуализира примерен преглед на заявлението. Това е най-съществената област на интерфейса, тъй като тя представя как въведените данни ще се отразят в крайния документ.

Тук е подходящо да се използват два контейнера:
- `VBox` – за подреждане на заглавието, съдържателната част и бутоните;
- `GridPane` – за организиране на предварителния преглед в редове и колони.

### Защо се използва `GridPane`

`GridPane` позволява прецизно позициониране на елементите по редове и колони. Това го прави особено удобен при моделиране на формуляри, таблици и документоподобни оформления.

### Какво съдържа централната секция

1. Заглавие „Преглед на заявлението“;
2. `GridPane` с полета за визуализация на данните;
3. Бутони за потвърждение и изчистване.

### Пояснение за използваните атрибути

- `hgap="10"` – хоризонтално разстояние между колоните;
- `vgap="8"` – вертикално разстояние между редовете;
- `GridPane.rowIndex` – номер на реда;
- `GridPane.columnIndex` – номер на колоната;
- `GridPane.columnSpan` – разполагане върху няколко колони;
- `wrapText="true"` – пренасяне на текста на нов ред при нужда.

Също така се задава бял фон на областта чрез `Background` и `BackgroundFill`, за да се открои визуално зоната на прегледа.

#### Резултат от стъпка 5

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<?import javafx.scene.text.Font?>
<?import javafx.geometry.Insets?>
<?import javafx.scene.paint.Color?>

<BorderPane xmlns="http://javafx.com/javafx"
            xmlns:fx="http://javafx.com/fxml"
            fx:controller="bg.tu_varna.sit.ps.lab5.task1.controller.FormController"
            prefHeight="400.0" prefWidth="600.0">

    <top>
        <Label text="Настройки на системата" BorderPane.alignment="CENTER">
            <font>
                <Font name="Arial Bold" size="24"/>
            </font>
            <padding>
                <Insets top="15" right="10" bottom="15" left="10"/>
            </padding>
        </Label>
    </top>

    <left>
        <VBox spacing="10" prefWidth="240">
            <padding>
                <Insets top="15" right="15" bottom="15" left="15"/>
            </padding>

            <Label text="Данни за студента"/>
            <TextField fx:id="facultyNumberField" promptText="Факултетен номер"/>
            <TextField fx:id="studentNameField" promptText="Име на студент"/>
            <ToggleButton fx:id="studyFormToggle" text="Редовно / Задочно"/>
            <TextField fx:id="specialtyField" promptText="Специалност"/>
        </VBox>
    </left>

    <center>
        <VBox spacing="15">
            <padding>
                <Insets top="15" right="15" bottom="15" left="15"/>
            </padding>

            <Label text="Преглед на заявлението">
                <font>
                    <Font name="Arial Bold" size="18"/>
                </font>
            </Label>

            <GridPane hgap="10" vgap="8">

                <padding>
                    <Insets top="15" right="15" bottom="15" left="15"/>
                </padding>

                <background>
                    <Background>
                        <fills>
                            <BackgroundFill>
                                <fill>
                                    <Color fx:constant="WHITE"/>
                                </fill>
                            </BackgroundFill>
                        </fills>
                    </Background>
                </background>

                <Label text="ДО" GridPane.rowIndex="0" GridPane.columnIndex="0"/>
                <Label fx:id="receiverPreviewLabel"
                       text=".............................................."
                       GridPane.rowIndex="0" GridPane.columnIndex="1"/>

                <Label text="От:" GridPane.rowIndex="2" GridPane.columnIndex="0"/>
                <Label fx:id="studentNamePreviewLabel"
                       text=".............................................."
                       GridPane.rowIndex="2" GridPane.columnIndex="1"/>

                <Label text="Факултетен номер:" GridPane.rowIndex="3" GridPane.columnIndex="0"/>
                <Label fx:id="facultyNumberPreviewLabel"
                       text=".............................................."
                       GridPane.rowIndex="3" GridPane.columnIndex="1"/>

                <Label text="Специалност:" GridPane.rowIndex="4" GridPane.columnIndex="0"/>
                <Label fx:id="specialtyPreviewLabel"
                       text=".............................................."
                       GridPane.rowIndex="4" GridPane.columnIndex="1"/>

                <Label text="Форма на обучение:" GridPane.rowIndex="5" GridPane.columnIndex="0"/>
                <Label fx:id="studyFormPreviewLabel"
                       text=".............................................."
                       GridPane.rowIndex="5" GridPane.columnIndex="1"/>

                <Label text="Уважаеми/а господин/госпожо,"
                       GridPane.rowIndex="7"
                       GridPane.columnIndex="0"
                       GridPane.columnSpan="2"/>

                <Label wrapText="true"
                       text="Моля да бъде разгледано моето заявление относно:"
                       GridPane.rowIndex="8"
                       GridPane.columnIndex="0"
                       GridPane.columnSpan="2"/>

                <Label fx:id="requestTypePreviewLabel"
                       text=".............................................."
                       GridPane.rowIndex="9"
                       GridPane.columnIndex="0"
                       GridPane.columnSpan="2"/>

                <Label fx:id="requestTextLabel"
                       text=".............................................."
                       wrapText="true"
                       GridPane.columnSpan="2"
                       GridPane.rowIndex="10"
                       GridPane.columnIndex="0"/>

                <Label text="Дата: ........................."
                       GridPane.rowIndex="11"
                       GridPane.columnIndex="0"/>

                <Label text="Подпис: ........................."
                       GridPane.rowIndex="11"
                       GridPane.columnIndex="1"
                       GridPane.halignment="RIGHT"/>

            </GridPane>

            <HBox spacing="15" alignment="CENTER">
                <Button fx:id="confirmButton"
                        text="Потвърди"
                        defaultButton="true"
                        onAction="#handleConfirm"/>
                <Button text="Почисти" onAction="#handleClear"/>
            </HBox>
        </VBox>
    </center>

</BorderPane>
```

### Обяснение

До този момент интерфейсът вече има ясна структура: заглавна област, зона за въвеждане на студентски данни и централна зона за преглед на заявлението. Следващите стъпки ще допълнят функционалността чрез контролите в дясната част и информационната лента в долната част.

---

## Стъпка 6. Добавяне на дясна секция (`right`)

Дясната част на приложението трябва да съдържа управляващите елементи, чрез които потребителят ще избира получател на заявлението, неговия тип и ще въвежда основния текст. Подобно на лявата секция, и тук е удачно да се използва `VBox`, тъй като компонентите трябва да бъдат подредени вертикално.

Ширината на тази секция се задава с `prefWidth="270"`, а разстоянието между елементите – с `spacing="10"`.

### Начален код за секцията `right`

```xml
<right>
    <VBox spacing="10" prefWidth="270">
        <padding>
            <Insets top="15" right="15" bottom="15" left="15"/>
        </padding>

    </VBox>
</right>
```

### Елементи, които трябва да бъдат добавени във `VBox`

#### Група 1. Получател

Добавя се заглавен етикет:

```xml
<Label text="Получател"/>
```

След него се добавят три `CheckBox` елемента:

```xml
<CheckBox fx:id="rectorCheckBox" text="Ректор" onAction="#selectedReceivers"/>
<CheckBox fx:id="deanCheckBox" text="Декан" onAction="#selectedReceivers"/>
<CheckBox fx:id="headCheckBox" text="Ръководител катедра" onAction="#selectedReceivers"/>
```

Тези елементи позволяват избор на един или повече получатели. Атрибутът `onAction` указва, че при избор трябва да се извика методът `selectedReceivers` от контролера.

#### Група 2. Относно

Добавя се заглавен етикет:

```xml
<Label text="Относно"/>
```

След това се добавят два `RadioButton` елемента, които трябва да принадлежат към една и съща `ToggleGroup`, за да бъде активен само един избор в даден момент.

Първият `RadioButton` съдържа и самата декларация на `ToggleGroup`:

```xml
<RadioButton fx:id="leaveRadio"
             text="Академичен отпуск"
             onAction="#selectedRequestType">
    <toggleGroup>
        <ToggleGroup fx:id="requestGroup"/>
    </toggleGroup>
</RadioButton>
```

Вторият използва вече създадената група:

```xml
<RadioButton fx:id="hoursRadio"
             text="Отработване на часове"
             toggleGroup="$requestGroup"
             onAction="#selectedRequestType"/>
```

#### Група 3. Текст на заявлението

Добавя се нов етикет:

```xml
<Label text="Текст на заявлението"/>
```

След него се добавя `TextArea`, подходящ за въвеждане на по-дълъг текст:

```xml
<TextArea fx:id="requestTextArea"
          prefRowCount="5"
          wrapText="true"
          promptText="Въведете текста на заявлението"/>
```

### Резултат от стъпка 6

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<?import javafx.scene.text.Font?>
<?import javafx.geometry.Insets?>
<?import javafx.scene.paint.Color?>

<BorderPane xmlns="http://javafx.com/javafx"
            xmlns:fx="http://javafx.com/fxml"
            fx:controller="bg.tu_varna.sit.ps.lab5.task1.controller.FormController"
            prefHeight="400.0" prefWidth="600.0">

    <top>
        <Label text="Настройки на системата" BorderPane.alignment="CENTER">
            <font>
                <Font name="Arial Bold" size="24"/>
            </font>
            <padding>
                <Insets top="15" right="10" bottom="15" left="10"/>
            </padding>
        </Label>
    </top>

    <left>
        <VBox spacing="10" prefWidth="240">
            <padding>
                <Insets top="15" right="15" bottom="15" left="15"/>
            </padding>

            <Label text="Данни за студента"/>
            <TextField fx:id="facultyNumberField" promptText="Факултетен номер"/>
            <TextField fx:id="studentNameField" promptText="Име на студент"/>
            <ToggleButton fx:id="studyFormToggle" text="Редовно / Задочно"/>
            <TextField fx:id="specialtyField" promptText="Специалност"/>
        </VBox>
    </left>

    <center>
        <VBox spacing="15">
            <padding>
                <Insets top="15" right="15" bottom="15" left="15"/>
            </padding>

            <Label text="Преглед на заявлението">
                <font>
                    <Font name="Arial Bold" size="18"/>
                </font>
            </Label>

            <GridPane hgap="10" vgap="8">

                <padding>
                    <Insets top="15" right="15" bottom="15" left="15"/>
                </padding>

                <background>
                    <Background>
                        <fills>
                            <BackgroundFill>
                                <fill>
                                    <Color fx:constant="WHITE"/>
                                </fill>
                            </BackgroundFill>
                        </fills>
                    </Background>
                </background>

                <Label text="ДО" GridPane.rowIndex="0" GridPane.columnIndex="0"/>
                <Label fx:id="receiverPreviewLabel"
                       text=".............................................."
                       GridPane.rowIndex="0" GridPane.columnIndex="1"/>

                <Label text="От:" GridPane.rowIndex="2" GridPane.columnIndex="0"/>
                <Label fx:id="studentNamePreviewLabel"
                       text=".............................................."
                       GridPane.rowIndex="2" GridPane.columnIndex="1"/>

                <Label text="Факултетен номер:" GridPane.rowIndex="3" GridPane.columnIndex="0"/>
                <Label fx:id="facultyNumberPreviewLabel"
                       text=".............................................."
                       GridPane.rowIndex="3" GridPane.columnIndex="1"/>

                <Label text="Специалност:" GridPane.rowIndex="4" GridPane.columnIndex="0"/>
                <Label fx:id="specialtyPreviewLabel"
                       text=".............................................."
                       GridPane.rowIndex="4" GridPane.columnIndex="1"/>

                <Label text="Форма на обучение:" GridPane.rowIndex="5" GridPane.columnIndex="0"/>
                <Label fx:id="studyFormPreviewLabel"
                       text=".............................................."
                       GridPane.rowIndex="5" GridPane.columnIndex="1"/>

                <Label text="Уважаеми/а господин/госпожо,"
                       GridPane.rowIndex="7"
                       GridPane.columnIndex="0"
                       GridPane.columnSpan="2"/>

                <Label wrapText="true"
                       text="Моля да бъде разгледано моето заявление относно:"
                       GridPane.rowIndex="8"
                       GridPane.columnIndex="0"
                       GridPane.columnSpan="2"/>

                <Label fx:id="requestTypePreviewLabel"
                       text=".............................................."
                       GridPane.rowIndex="9"
                       GridPane.columnIndex="0"
                       GridPane.columnSpan="2"/>

                <Label fx:id="requestTextLabel"
                       text=".............................................."
                       wrapText="true" 
                       GridPane.columnSpan="2"
                       GridPane.rowIndex="10"
                       GridPane.columnIndex="0"/>

                <Label text="Дата: ........................."
                       GridPane.rowIndex="11"
                       GridPane.columnIndex="0"/>

                <Label text="Подпис: ........................."
                       GridPane.rowIndex="11"
                       GridPane.columnIndex="1"
                       GridPane.halignment="RIGHT"/>

            </GridPane>

            <HBox spacing="15" alignment="CENTER">
                <Button fx:id="confirmButton"
                        text="Потвърди"
                        defaultButton="true"
                        onAction="#handleConfirm"/>
                <Button text="Почисти" onAction="#handleClear"/>
            </HBox>
        </VBox>
    </center>

    <right>
        <VBox spacing="10" prefWidth="270">
            <padding>
                <Insets top="15" right="15" bottom="15" left="15"/>
            </padding>

            <Label text="Получател"/>
            <CheckBox fx:id="rectorCheckBox" text="Ректор" onAction="#selectedReceivers"/>
            <CheckBox fx:id="deanCheckBox" text="Декан" onAction="#selectedReceivers"/>
            <CheckBox fx:id="headCheckBox" text="Ръководител катедра" onAction="#selectedReceivers"/>

            <Label text="Относно"/>

            <RadioButton fx:id="leaveRadio"
                         text="Академичен отпуск"
                         onAction="#selectedRequestType">
                <toggleGroup>
                    <ToggleGroup fx:id="requestGroup"/>
                </toggleGroup>
            </RadioButton>

            <RadioButton fx:id="hoursRadio"
                         text="Отработване на часове"
                         toggleGroup="$requestGroup"
                         onAction="#selectedRequestType"/>

            <Label text="Текст на заявлението"/>

            <TextArea fx:id="requestTextArea"
                      prefRowCount="5"
                      wrapText="true"
                      promptText="Въведете текста на заявлението"/>

        </VBox>
    </right>

</BorderPane>
```

### Обяснение

След тази стъпка интерфейсът вече позволява не само въвеждане на студентски данни, но и конфигуриране на съдържанието на самото заявление. Дясната секция представлява логическо продължение на формата, тъй като съдържа параметрите, които влияят пряко върху визуализацията в централната област.

---

## Стъпка 7. Добавяне на долна секция (`bottom`)

Последната структурна област на `BorderPane` е регионът `bottom`. Той ще съдържа лента за състояние на приложението, в която могат да се показват кратки съобщения към потребителя, както и информация за версията.

За тази секция се използва `HBox`, тъй като елементите трябва да бъдат подредени хоризонтално.

### Начален код за секцията `bottom`

```xml
<bottom>
    <HBox spacing="10" alignment="CENTER_LEFT">
        <padding>
            <Insets top="10" right="15" bottom="10" left="15"/>
        </padding>

    </HBox>
</bottom>
```

### Елементи, които трябва да бъдат добавени в `HBox`

Първо се добавя `Label`, който ще показва текущото състояние на приложението:

```xml
<Label fx:id="statusLabel" text="Състояние: Готово"/>
```

След това се използва `Region`, който ще заеме свободното пространство между левия и десния елемент:

```xml
<Region HBox.hgrow="ALWAYS"/>
```

Накрая се добавя етикет с информация за версията:

```xml
<Label text="Версия 1.0"/>
```

### Резултат от стъпка 7

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<?import javafx.scene.text.Font?>
<?import javafx.geometry.Insets?>
<?import javafx.scene.paint.Color?>

<BorderPane xmlns="http://javafx.com/javafx"
            xmlns:fx="http://javafx.com/fxml"
            fx:controller="bg.tu_varna.sit.ps.lab5.task1.controller.FormController"
            prefHeight="400.0" prefWidth="600.0">

    <top>
        <Label text="Настройки на системата" BorderPane.alignment="CENTER">
            <font>
                <Font name="Arial Bold" size="24"/>
            </font>
            <padding>
                <Insets top="15" right="10" bottom="15" left="10"/>
            </padding>
        </Label>
    </top>

    <left>
        <VBox spacing="10" prefWidth="240">
            <padding>
                <Insets top="15" right="15" bottom="15" left="15"/>
            </padding>

            <Label text="Данни за студента"/>
            <TextField fx:id="facultyNumberField" promptText="Факултетен номер"/>
            <TextField fx:id="studentNameField" promptText="Име на студент"/>
            <ToggleButton fx:id="studyFormToggle" text="Редовно / Задочно"/>
            <TextField fx:id="specialtyField" promptText="Специалност"/>
        </VBox>
    </left>

    <center>
        <VBox spacing="15">
            <padding>
                <Insets top="15" right="15" bottom="15" left="15"/>
            </padding>

            <Label text="Преглед на заявлението">
                <font>
                    <Font name="Arial Bold" size="18"/>
                </font>
            </Label>

            <GridPane hgap="10" vgap="8">

                <padding>
                    <Insets top="15" right="15" bottom="15" left="15"/>
                </padding>

                <background>
                    <Background>
                        <fills>
                            <BackgroundFill>
                                <fill>
                                    <Color fx:constant="WHITE"/>
                                </fill>
                            </BackgroundFill>
                        </fills>
                    </Background>
                </background>

                <Label text="ДО" GridPane.rowIndex="0" GridPane.columnIndex="0"/>
                <Label fx:id="receiverPreviewLabel"
                       text=".............................................."
                       GridPane.rowIndex="0" GridPane.columnIndex="1"/>

                <Label text="От:" GridPane.rowIndex="2" GridPane.columnIndex="0"/>
                <Label fx:id="studentNamePreviewLabel"
                       text=".............................................."
                       GridPane.rowIndex="2" GridPane.columnIndex="1"/>

                <Label text="Факултетен номер:" GridPane.rowIndex="3" GridPane.columnIndex="0"/>
                <Label fx:id="facultyNumberPreviewLabel"
                       text=".............................................."
                       GridPane.rowIndex="3" GridPane.columnIndex="1"/>

                <Label text="Специалност:" GridPane.rowIndex="4" GridPane.columnIndex="0"/>
                <Label fx:id="specialtyPreviewLabel"
                       text=".............................................."
                       GridPane.rowIndex="4" GridPane.columnIndex="1"/>

                <Label text="Форма на обучение:" GridPane.rowIndex="5" GridPane.columnIndex="0"/>
                <Label fx:id="studyFormPreviewLabel"
                       text=".............................................."
                       GridPane.rowIndex="5" GridPane.columnIndex="1"/>

                <Label text="Уважаеми/а господин/госпожо,"
                       GridPane.rowIndex="7"
                       GridPane.columnIndex="0"
                       GridPane.columnSpan="2"/>

                <Label wrapText="true"
                       text="Моля да бъде разгледано моето заявление относно:"
                       GridPane.rowIndex="8"
                       GridPane.columnIndex="0"
                       GridPane.columnSpan="2"/>

                <Label fx:id="requestTypePreviewLabel"
                       text=".............................................."
                       GridPane.rowIndex="9"
                       GridPane.columnIndex="0"
                       GridPane.columnSpan="2"/>

                <Label fx:id="requestTextLabel"
                       text=".............................................."
                       wrapText="true" 
                       GridPane.columnSpan="2"
                       GridPane.rowIndex="10"
                       GridPane.columnIndex="0"/>

                <Label text="Дата: ........................."
                       GridPane.rowIndex="11"
                       GridPane.columnIndex="0"/>

                <Label text="Подпис: ........................."
                       GridPane.rowIndex="11"
                       GridPane.columnIndex="1"
                       GridPane.halignment="RIGHT"/>

            </GridPane>

            <HBox spacing="15" alignment="CENTER">
                <Button fx:id="confirmButton"
                        text="Потвърди"
                        defaultButton="true"
                        onAction="#handleConfirm"/>
                <Button text="Почисти" onAction="#handleClear"/>
            </HBox>
        </VBox>
    </center>

    <right>
        <VBox spacing="10" prefWidth="270">
            <padding>
                <Insets top="15" right="15" bottom="15" left="15"/>
            </padding>

            <Label text="Получател"/>
            <CheckBox fx:id="rectorCheckBox" text="Ректор" onAction="#selectedReceivers"/>
            <CheckBox fx:id="deanCheckBox" text="Декан" onAction="#selectedReceivers"/>
            <CheckBox fx:id="headCheckBox" text="Ръководител катедра" onAction="#selectedReceivers"/>

            <Label text="Относно"/>

            <RadioButton fx:id="leaveRadio"
                         text="Академичен отпуск"
                         onAction="#selectedRequestType">
                <toggleGroup>
                    <ToggleGroup fx:id="requestGroup"/>
                </toggleGroup>
            </RadioButton>

            <RadioButton fx:id="hoursRadio"
                         text="Отработване на часове"
                         toggleGroup="$requestGroup"
                         onAction="#selectedRequestType"/>

            <Label text="Текст на заявлението"/>

            <TextArea fx:id="requestTextArea"
                      prefRowCount="5"
                      wrapText="true"
                      promptText="Въведете текста на заявлението"/>

        </VBox>
    </right>

    <bottom>
        <HBox spacing="10" alignment="CENTER_LEFT">
            <padding>
                <Insets top="10" right="15" bottom="10" left="15"/>
            </padding>

            <Label fx:id="statusLabel" text="Състояние: Готово"/>
            <Region HBox.hgrow="ALWAYS"/>
            <Label text="Версия 1.0"/>
        </HBox>
    </bottom>

</BorderPane>
```

### Обяснение

С тази стъпка структурата на потребителския интерфейс вече е напълно завършена. Всички региони на `BorderPane` са използвани по предназначение и приложението има както входни контроли, така и област за преглед и статусна информация.

---

## Стъпка 8. Преглед на използваните `fx:id` и връзката с контролера

След завършването на FXML файла е важно да се разгледа как отделните компоненти ще бъдат достъпвани в класа `FormController`. Всеки елемент, върху който контролерът трябва да упражнява логика, трябва да има уникален `fx:id`.

### Идентификатори, използвани във файла

```xml
facultyNumberField
studentNameField
studyFormToggle
specialtyField
receiverPreviewLabel
studentNamePreviewLabel
facultyNumberPreviewLabel
specialtyPreviewLabel
studyFormPreviewLabel
requestTypePreviewLabel
requestTextLabel
confirmButton
rectorCheckBox
deanCheckBox
headCheckBox
leaveRadio
hoursRadio
requestGroup
requestTextArea
statusLabel
```

### Пример как тези елементи ще се свържат в контролера

```java
@FXML
private TextField facultyNumberField;

@FXML
private TextField studentNameField;

@FXML
private ToggleButton studyFormToggle;

@FXML
private TextField specialtyField;

@FXML
private Label receiverPreviewLabel;

@FXML
private Label studentNamePreviewLabel;

@FXML
private Label facultyNumberPreviewLabel;

@FXML
private Label specialtyPreviewLabel;

@FXML
private Label studyFormPreviewLabel;

@FXML
private Label requestTypePreviewLabel;

@FXML
private Label requestTextLabel;

@FXML
private Button confirmButton;

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
private ToggleGroup requestGroup;

@FXML
private TextArea requestTextArea;

@FXML
private Label statusLabel;
```

### Обяснение

Макар че този код не е част от самия FXML файл, той е пряко свързан с него. В академичната практика е важно да се разбира, че `fx:id` не служи само за именуване на елементи, а реализира механизма за свързване между визуалното описание и програмната логика.

---

## Стъпка 9. Дефиниране на обработчиците на събития

Освен идентификатори, FXML файлът съдържа и препратки към методи от контролера чрез атрибута `onAction`. Това са методите, които трябва да бъдат извиквани при взаимодействие на потребителя с отделните контроли.

### Събития, използвани във файла

```xml
<Button fx:id="confirmButton"
        text="Потвърди"
        defaultButton="true"
        onAction="#handleConfirm"/>

<Button text="Почисти" onAction="#handleClear"/>

<CheckBox fx:id="rectorCheckBox" text="Ректор" onAction="#selectedReceivers"/>
<CheckBox fx:id="deanCheckBox" text="Декан" onAction="#selectedReceivers"/>
<CheckBox fx:id="headCheckBox" text="Ръководител катедра" onAction="#selectedReceivers"/>

<RadioButton fx:id="leaveRadio"
             text="Академичен отпуск"
             onAction="#selectedRequestType"/>

<RadioButton fx:id="hoursRadio"
             text="Отработване на часове"
             toggleGroup="$requestGroup"
             onAction="#selectedRequestType"/>
```

### Примерни сигнатури на методите в контролера

```java
@FXML
private void handleConfirm() {
}

@FXML
private void handleClear() {
}

@FXML
private void selectedReceivers() {
}

@FXML
private void selectedRequestType() {
}
```

### Обяснение

В този етап не е задължително методите вече да съдържат пълната логика, но те трябва да съществуват в контролера, за да може приложението да се зареди коректно. Ако някой от методите липсва, JavaFX ще генерира грешка при зареждане на FXML файла.

---

## Стъпка 10. Финален завършен код на `form-view.fxml`

След изпълнение на всички предходни стъпки се получава напълно завършен FXML файл, който описва цялостния потребителски интерфейс.

### Краен резултат

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<?import javafx.scene.text.Font?>
<?import javafx.geometry.Insets?>
<?import javafx.scene.paint.Color?>

<BorderPane xmlns="http://javafx.com/javafx"
            xmlns:fx="http://javafx.com/fxml"
            fx:controller="bg.tu_varna.sit.ps.lab5.task1.controller.FormController"
            prefHeight="400.0" prefWidth="600.0">

    <top>
        <Label text="Настройки на системата" BorderPane.alignment="CENTER">
            <font>
                <Font name="Arial Bold" size="24"/>
            </font>
            <padding>
                <Insets top="15" right="10" bottom="15" left="10"/>
            </padding>
        </Label>
    </top>

    <left>
        <VBox spacing="10" prefWidth="240">
            <padding>
                <Insets top="15" right="15" bottom="15" left="15"/>
            </padding>

            <Label text="Данни за студента"/>
            <TextField fx:id="facultyNumberField" promptText="Факултетен номер"/>
            <TextField fx:id="studentNameField" promptText="Име на студент"/>
            <ToggleButton fx:id="studyFormToggle" text="Редовно / Задочно"/>
            <TextField fx:id="specialtyField" promptText="Специалност"/>
        </VBox>
    </left>

    <center>
        <VBox spacing="15">
            <padding>
                <Insets top="15" right="15" bottom="15" left="15"/>
            </padding>

            <Label text="Преглед на заявлението">
                <font>
                    <Font name="Arial Bold" size="18"/>
                </font>
            </Label>

            <GridPane hgap="10" vgap="8">

                <padding>
                    <Insets top="15" right="15" bottom="15" left="15"/>
                </padding>

                <background>
                    <Background>
                        <fills>
                            <BackgroundFill>
                                <fill>
                                    <Color fx:constant="WHITE"/>
                                </fill>
                            </BackgroundFill>
                        </fills>
                    </Background>
                </background>

                <Label text="ДО" GridPane.rowIndex="0" GridPane.columnIndex="0"/>
                <Label fx:id="receiverPreviewLabel"
                       text=".............................................."
                       GridPane.rowIndex="0" GridPane.columnIndex="1"/>

                <Label text="От:" GridPane.rowIndex="2" GridPane.columnIndex="0"/>
                <Label fx:id="studentNamePreviewLabel"
                       text=".............................................."
                       GridPane.rowIndex="2" GridPane.columnIndex="1"/>

                <Label text="Факултетен номер:" GridPane.rowIndex="3" GridPane.columnIndex="0"/>
                <Label fx:id="facultyNumberPreviewLabel"
                       text=".............................................."
                       GridPane.rowIndex="3" GridPane.columnIndex="1"/>

                <Label text="Специалност:" GridPane.rowIndex="4" GridPane.columnIndex="0"/>
                <Label fx:id="specialtyPreviewLabel"
                       text=".............................................."
                       GridPane.rowIndex="4" GridPane.columnIndex="1"/>

                <Label text="Форма на обучение:" GridPane.rowIndex="5" GridPane.columnIndex="0"/>
                <Label fx:id="studyFormPreviewLabel"
                       text=".............................................."
                       GridPane.rowIndex="5" GridPane.columnIndex="1"/>

                <Label text="Уважаеми/а господин/госпожо,"
                       GridPane.rowIndex="7"
                       GridPane.columnIndex="0"
                       GridPane.columnSpan="2"/>

                <Label wrapText="true"
                       text="Моля да бъде разгледано моето заявление относно:"
                       GridPane.rowIndex="8"
                       GridPane.columnIndex="0"
                       GridPane.columnSpan="2"/>

                <Label fx:id="requestTypePreviewLabel"
                       text=".............................................."
                       GridPane.rowIndex="9"
                       GridPane.columnIndex="0"
                       GridPane.columnSpan="2"/>

                <Label fx:id="requestTextLabel"
                       text=".............................................."
                       wrapText="true" 
                       GridPane.columnSpan="2"
                       GridPane.rowIndex="10"
                       GridPane.columnIndex="0"/>

                <Label text="Дата: ........................."
                       GridPane.rowIndex="11"
                       GridPane.columnIndex="0"/>

                <Label text="Подпис: ........................."
                       GridPane.rowIndex="11"
                       GridPane.columnIndex="1"
                       GridPane.halignment="RIGHT"/>

            </GridPane>

            <HBox spacing="15" alignment="CENTER">
                <Button fx:id="confirmButton"
                        text="Потвърди"
                        defaultButton="true"
                        onAction="#handleConfirm"/>
                <Button text="Почисти" onAction="#handleClear"/>
            </HBox>
        </VBox>
    </center>

    <right>
        <VBox spacing="10" prefWidth="270">
            <padding>
                <Insets top="15" right="15" bottom="15" left="15"/>
            </padding>

            <Label text="Получател"/>
            <CheckBox fx:id="rectorCheckBox" text="Ректор" onAction="#selectedReceivers"/>
            <CheckBox fx:id="deanCheckBox" text="Декан" onAction="#selectedReceivers"/>
            <CheckBox fx:id="headCheckBox" text="Ръководител катедра" onAction="#selectedReceivers"/>

            <Label text="Относно"/>

            <RadioButton fx:id="leaveRadio"
                         text="Академичен отпуск"
                         onAction="#selectedRequestType">
                <toggleGroup>
                    <ToggleGroup fx:id="requestGroup"/>
                </toggleGroup>
            </RadioButton>

            <RadioButton fx:id="hoursRadio"
                         text="Отработване на часове"
                         toggleGroup="$requestGroup"
                         onAction="#selectedRequestType"/>

            <Label text="Текст на заявлението"/>

            <TextArea fx:id="requestTextArea"
                      prefRowCount="5"
                      wrapText="true"
                      promptText="Въведете текста на заявлението"/>

        </VBox>
    </right>

    <bottom>
        <HBox spacing="10" alignment="CENTER_LEFT">
            <padding>
                <Insets top="10" right="15" bottom="10" left="15"/>
            </padding>

            <Label fx:id="statusLabel" text="Състояние: Готово"/>
            <Region HBox.hgrow="ALWAYS"/>
            <Label text="Версия 1.0"/>
        </HBox>
    </bottom>
</BorderPane>
```

### Заключение

Полученият FXML файл е завършено описание на графичен интерфейс за работа със студентско заявление. Структурата му е логически разделена, лесна за четене и готова за интеграция с контролерен клас. По този начин упражнението демонстрира добър модел за разработка на JavaFX интерфейс, при който визуалната част и програмната логика са ясно разграничени, но и коректно свързани чрез `fx:id` и `onAction`.
