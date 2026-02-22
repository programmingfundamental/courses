---
layout: default
title: Създаване на Maven проект в IntelliJ Community Edition
parent: Лабораторно упражнение 2
grand_parent: Интернет технологии
nav_order: 4
---

# Създаване на Maven проект в IntelliJ Community Edition

1. Добавяне на плъгин Smart Tomcat в IntelliJ (еднократно)\


<figure><img src="../../../../assets/1.png" alt=""><figcaption></figcaption></figure>

2. Създаване на Maven проект

<figure><img src="../../../../assets/image (82).png" alt=""><figcaption></figcaption></figure>

<figure><img src="../../../../assets/image (40).png" alt=""><figcaption></figcaption></figure>

<figure><img src="../../../../assets/image (42).png" alt=""><figcaption></figcaption></figure>

3. В pom.xml добавяме servlet dependency

```xml
<dependency>
    <groupId>jakarta.servlet</groupId>
    <artifactId>jakarta.servlet-api</artifactId>
    <version>6.0.0</version>
    <scope>provided</scope>
</dependency>
```

<figure><img src="../../../../assets/image (58).png" alt=""><figcaption></figcaption></figure>

4\. Презареждане на проекта

<figure><img src="../../../../assets/image (34).png" alt=""><figcaption></figcaption></figure>

5\. Добавяне на Source директория

<figure><img src="../../../../assets/image (51).png" alt=""><figcaption></figcaption></figure>

6\.  Избор на Source директория

![](<../../../../assets/image (30).png>)

7\. Конфигуриране на Tomcat

Избираме "Add configuration"

<figure><img src="../../../../assets/image (85).png" alt=""><figcaption></figcaption></figure>

Добавяме нова конфигурация, като избираме Smart Tomcat

<figure><img src="../../../../assets/image (41).png" alt=""><figcaption></figcaption></figure>

Натискаме бутона Configure

<figure><img src="../../../../assets/image (24).png" alt=""><figcaption></figcaption></figure>

Избираме директорията, в която е поставен Tomcat

<figure><img src="../../../../assets/image (47).png" alt=""><figcaption></figcaption></figure>

Попълваме данните на проекта

<figure><img src="../../../../assets/image (54).png" alt=""><figcaption></figcaption></figure>

14\. Билдваме и стартираме проекта
