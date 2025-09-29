---
layout: default
title: Първа програма в Java
parent: Лабораторно упражнение 1
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 3
---

# Първа програма в Java

Създаване на проект в Intellij.

След като стартирате изпълнимия файл, ще се отвори Intellij Welcome screen, кликвате върху **New Project**. Отваря се следния екран за конфигурация на проекта:

<img width="884" height="797" alt="Screenshot 2025-09-29 201946" src="https://github.com/user-attachments/assets/3fd3a1bb-4649-42bb-b59f-ba6678453b3d" />

По подразбиране е маркиран Java проект. Попълваме полетата за:
* Name - името на проекта, задължително трябва да е значещо за задачата, която ще изпълнява;
* Location - разполагайте проектите си на едно и също място, като ги групирате по цел;
* Build System - IntelliJ.

След като попълните избирате **Create**.

Създава се проекта:

<img width="1107" height="620" alt="Screenshot 2025-09-29 203533" src="https://github.com/user-attachments/assets/8910e628-df65-4c7d-a4aa-4b9394511f2e" />


* Основната директория на проекта носи името на самия проект;
* src - е директорията на файловете с програмния код на Java. Всеки файл трябва да бъде поставен в пакет;
* Main - е класът, който съдържа входната точка на програмата (main методът).

За да създадем пакет, кликваме на основната src папка с десен бутон > New > Package.
 
<img width="1107" height="620" alt="Screenshot 2025-09-29 203913" src="https://github.com/user-attachments/assets/77d28405-53b9-4d40-b7b1-eed74526cde4" />

Изберете име:

<img width="329" height="99" alt="Screenshot 2025-09-29 204423" src="https://github.com/user-attachments/assets/0e20115f-8f2a-4672-b9db-d99a63d40fcd" />


Използвайте значещи имена на пакети, за целите на ООП ще ползваме пакета bg.tu_varna.sit.<половинката><номера на групата>.f<гакултетния номер>.oop1 (bg.tu_varna.sit.a1.f1234567.oop1). 

Всеки програмен файл на java започва с дефиниция на пакета, в който се намира файла. За да преместим класа Main в новосъздадения пакет, чрез провлачване преместваме класа в пакета:

<img width="1107" height="620" alt="Screenshot 2025-09-29 204835" src="https://github.com/user-attachments/assets/52c16946-9941-4a0e-98aa-46c23a21f3cd" />

След това избирамe Refactor:

<img width="554" height="204" alt="Screenshot 2025-09-29 204855" src="https://github.com/user-attachments/assets/9840b797-006e-434c-9a17-12be925208f6" />

Вече имаме готовата структура пакет -> клас, както и дефиницията на пакета в началото на класа:

<img width="1307" height="820" alt="Screenshot 2025-09-29 204908" src="https://github.com/user-attachments/assets/ed52e8ff-bb91-4314-a09b-c908a708c1dc" />

Вече можем да преминем към програмния код, който задължително се пише в класова организация. Променливите и методите трябва да са в class блок. Методът main служи като стартова/входна точка на всяка java програма.

