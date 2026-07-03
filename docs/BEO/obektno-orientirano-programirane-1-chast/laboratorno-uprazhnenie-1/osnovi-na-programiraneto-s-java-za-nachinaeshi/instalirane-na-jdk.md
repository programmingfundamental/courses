---
layout: default
title: Инсталиране на JDK
parent: Лабораторно упражнение 1
grand_parent: Обектно-ориентирано програмиране - 1 част
has_children: true
nav_order: 2
---

За разработване на Java приложения е необходимо да бъде инсталирана подходяща среда за разработка. В рамките на курса ще бъдат използвани Java Development Kit (JDK) и интегрираната среда за разработка IntelliJ IDEA.

# Инсталиране на JDK

Преди разработването на Java приложения е необходимо да бъде инсталиран Java Development Kit (JDK).

Препоръчително е използването на актуална **LTS (Long-Term Support)** версия на JDK, която може да бъде изтеглена от официалния сайт на Oracle или от алтернативния линк по-долу:

- [https://www.oracle.com/java/technologies/downloads/](https://www.oracle.com/java/technologies/downloads/)
- https://tuvarnabg-my.sharepoint.com/:u:/g/personal/vkolesnichenko_tu-varna_bg/ET92nCILMK9MpO_krBzE8RkBYg90A97t1NTBC-aor3a26A?e=e6PcXa

След стартиране на инсталационната програма се следват стандартните стъпки на инсталационния процес. Препоръчително е JDK да бъде инсталиран в директорията по подразбиране.

## Настройване на променливите на средата

След приключване на инсталацията е необходимо да бъдат конфигурирани системните променливи на средата.

До настройките на променливите на средата в Windows може да се достигне чрез:

## This PC -> Properties -> Advanced system settings -> Environment Variables

или чрез търсене на *"Edit the system environment variables"*:

<img width="554" height="570" alt="image" src="https://github.com/user-attachments/assets/0cc72f8b-114e-4189-b1ee-ce8d9b12145e" />

## Променлива JAVA_HOME

Създава се системна променлива **JAVA_HOME**, която съдържа пътя до директорията, в която е инсталиран JDK:

<img width="834" height="365" alt="image" src="https://github.com/user-attachments/assets/8f4c39ae-4271-442d-9aaf-de6e73fce44b" />

## Променлива Path

Към системната променлива **Path** се добавя пътят до директорията **bin**, намираща се в инсталационната директория на JDK:

<img width="531" height="637" alt="image" src="https://github.com/user-attachments/assets/3a720c45-de22-4122-9167-b4ae5bdb74b1" />

## Проверка на инсталацията

Коректността на инсталацията може да бъде проверена чрез изпълнение на следните команди в **Command Prompt**:

```text
java -version
javac -version
```

Ако инсталацията е извършена успешно, на екрана се извежда информация за използваната версия на Java и компилатора *javac*.

## Управление на версиите на Java

На един компютър е възможно да бъдат инсталирани няколко версии на JDK. В този случай е необходимо да се провери коя версия се използва от операционната система и коя версия е конфигурирана в интегрираната среда за разработка.

Командите от по-горе показват версията на Java. Командата *java -version* извежда информация за версията на Java Runtime, използвана от командния ред, а *javac -version* - за версията на Java компилатора.

За всеки проект в IntelliJ IDEA може да бъде избрана различна версия на JDK. При създаване на нов проект е необходимо да бъде избрана подходящата версия на JDK.

<img width="941" height="364" alt="image" src="https://github.com/user-attachments/assets/db90b7cc-f46c-49c3-a1a5-3621851d8c84" />

Препоръчително е всички проекти в рамките на курса да използват една и съща версия на JDK.
