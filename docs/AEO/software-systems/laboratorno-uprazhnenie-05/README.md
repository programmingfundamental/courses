---
layout: default
title: Laboratory Exercise 5
parent: Software Systems
has_children: true
nav_order: 5
#permalink: /docs/software-systems/laboratorno-uprazhnenie-5
---

# Упражнение #5

**1. Базови контроли (UI Controls):**
Контролите са графични елементи, с които потребителят взаимодейства. Всички те наследяват класа Control и притежават характеристики като стилизиране, фокус и състояния.

- Текстови контроли: `Label` (само за четене), `TextField` (едноредов текст), `PasswordField` (скрит текст), `TextArea` (многоредов текст).

- Бутони: `Button` (стандартен), `ToggleButton` (превключващ), `CheckBox` (независим избор), `RadioButton` (избор от група – изисква `ToggleGroup`).

- Списъчни контроли: `ComboBox` (падащо меню), `ListView` (списък), `ChoiceBox`.

**2. Обработка на действия (Event Handling):**
Най-често използваният метод за обработка на взаимодействие е `setOnAction()`. В FXML това се дефинира чрез атрибута `onAction="#methodName"`, а методът се реализира в контролер класа с анотация `@FXML`.

**3. Popup контроли:**
Това са елементи, които се появяват над основното съдържание:

- `Tooltip`: Кратка подсказка при задържане на мишката върху елемент.

- `ContextMenu`: Меню, което се появява при десен бутон на мишката.

**4. Диалогови форми (Dialogs):**
JavaFX предоставя вградения клас Alert за стандартни съобщения и класа Dialog за персонализирани прозорци.

- Типове Alert: `INFORMATION`, `WARNING`, `ERROR`, `CONFIRMATION`.

- Интерактивни диалози: `TextInputDialog` (за въвеждане на стринг) и `ChoiceDialog` (за избор от списък).
