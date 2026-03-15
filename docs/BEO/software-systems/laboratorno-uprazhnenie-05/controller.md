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

        initValidators();
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

        studyFormToggle.setSelected(false);

        rectorCheckBox.setSelected(false);
        deanCheckBox.setSelected(false);
        headCheckBox.setSelected(false);

        leaveRadio.setSelected(false);
        hoursRadio.setSelected(false);

        setStatusLabel("Състояние: Формата е изчистена", "", true);
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
