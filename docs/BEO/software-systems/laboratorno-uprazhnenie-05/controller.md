---
layout: default
title: Form Controller
parent: Лабораторно упражнение 5
grand_parent: Програмни системи
nav_order: 2
---

### FormController.java

```java

package bg.tu_varna.sit.ps.lab5.task1.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
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

        initValidators();
    }

    private void initValidators() {
        studentNameField.textProperty().addListener((obs,o,n) -> validateName());
        facultyNumberField.textProperty().addListener((obs,o,n) -> validateFaculty());
        specialtyField.textProperty().addListener((obs,o,n) -> validateSpecialty());

        rectorCheckBox.setOnAction(e -> validateReceiver());
        deanCheckBox.setOnAction(e -> validateReceiver());
        headCheckBox.setOnAction(e -> validateReceiver());

        leaveRadio.setOnAction(e -> validateRequest());
        hoursRadio.setOnAction(e -> validateRequest());
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

    private void setStatusLabel(String message, boolean disableButton) {
        statusLabel.setText(message);
        confirmButton.setDisable(disableButton);
    }

    @FXML
    private void handleConfirm() {
        setStatusLabel("Състояние: Данните са потвърдени", false);
    }

    @FXML
    private void handleClear() {
        facultyNumberField.clear();
        studentNameField.clear();
        specialtyField.clear();

        studyFormToggle.setSelected(false);

        rectorCheckBox.setSelected(false);
        deanCheckBox.setSelected(false);
        headCheckBox.setSelected(false);

        leaveRadio.setSelected(false);
        hoursRadio.setSelected(false);

        setStatusLabel("Състояние: Формата е изчистена", true);
    }

    private void validateName() {

        String name = studentNameField.getText().trim();

        if (name.length() < 3) {
            setStatusLabel("Грешка: Името трябва да е повече от 3 символа", true);
        } else {
            setStatusLabel("Състояние: Въведено име", false);
        }
    }

    private void validateFaculty() {

        String faculty = facultyNumberField.getText().trim();

        if (faculty.length() != 8) {
            setStatusLabel("Грешка: Факултетният номер трябва да е 8 цифри", true);
            return;
        }

        for (char c : faculty.toCharArray()) {
            if (!Character.isDigit(c)) {
                setStatusLabel("Грешка: Факултетният номер трябва да съдържа само цифри", true);
                return;
            }
        }

        setStatusLabel("Състояние: Въведен факултетен номер", false);
    }

    private void validateSpecialty() {

        String specialty = specialtyField.getText().trim();

        if (specialty.length() < 2) {
            setStatusLabel("Грешка: Специалността трябва да е повече от 2 букви", true);
        } else {
            setStatusLabel("Състояние: Въведена специалност", false);
        }
    }

    private void validateReceiver() {

        if (!(rectorCheckBox.isSelected() || deanCheckBox.isSelected() || headCheckBox.isSelected())) {
            setStatusLabel("Грешка: Изберете получател", true);
        } else {
            setStatusLabel("Състояние: Избран получател", false);
        }
    }

    private void validateRequest() {

        if (!(leaveRadio.isSelected() || hoursRadio.isSelected())) {
            setStatusLabel("Грешка: Изберете вид заявление", true);
        } else {
            setStatusLabel("Състояние: Избран вид заявление", false);
        }
    }
}

```