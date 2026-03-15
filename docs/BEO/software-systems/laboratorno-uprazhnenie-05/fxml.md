---
layout: default
title: Form fxml
parent: Лабораторно упражнение 5
grand_parent: Програмни системи
nav_order: 3
---

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

                <Label fx:id="requestTextLabel" text=".............................................." GridPane.rowIndex="10" GridPane.columnIndex="0"/>

                <Label text="Дата: ........................."
                       GridPane.rowIndex="11"
                       GridPane.columnIndex="0"/>

                <Label text="Подпис: ........................."
                       GridPane.rowIndex="11"
                       GridPane.columnIndex="1"
                       GridPane.halignment="RIGHT"/>

            </GridPane>

            <HBox spacing="15" alignment="CENTER">
                <Button fx:id="confirmButton" text="Потвърди" defaultButton="true" onAction="#handleConfirm"/>
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
                         toggleGroup="$requestGroup"
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