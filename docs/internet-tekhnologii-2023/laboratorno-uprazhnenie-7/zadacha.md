![image](https://github.com/programmingfundamental/courses/assets/10382663/2d79e8aa-fdb8-4ffc-a329-be01bd987794)---
layout: default
title: Задача
parent: Лабораторно упражнение 7
grand_parent: Интернет технологии
nav_order: 4
---
# Задача 1

Зъздайте конфигурация за изпълнение и проследяване на грешки в проекта от task-manager.
За целта трябва да се модифицира compose.yml

image: - секцията трябва да се замени с билдване на image:

```
    platform: linux/x86_64
    build:
      context: .
      dockerfile: Dockerfile
```
Избирате edit на конфигурацията за стартиране

![image](https://github.com/programmingfundamental/courses/assets/10382663/b0d9ee3c-b025-4105-8d05-646ce4cbff44)

Избирате стартиране в docker-compose

![image](https://github.com/programmingfundamental/courses/assets/10382663/2dcbe80a-e88c-4ef2-90e6-f0d1f030218a)

Като файл за конфигуриране избирате compose.yml файла в проекта

![image](https://github.com/programmingfundamental/courses/assets/10382663/988471d2-bf66-4752-a415-74f86c5a0443)

Сървиса на проекта от compose файла и избирате напред.

![image](https://github.com/programmingfundamental/courses/assets/10382663/ae74eb8c-526e-4e16-ad69-5c375779450b)

След като се билдне изображението избирате напред. На последната стъпка избирае create.

Избирате Run за да стартирате контейнера в Docker.

# Задача 2

Създайте Spring boot приложение за управление на задачи (ще бъде надграждано в следващите часове). За всяка задача се съхранява следната информация: пореден номер на задачата (id), наименование (title), описание (description), краен срок (deadline).

Предвидете функционалности за:

·        преглед на всички добавени задачи;

·        добавяне на задача;

·        преглед на дадена задача по зададен номер;

·        актуализация на задача по зададени номер и актуални данни;

·        изтриване на задача по зададен номер.

Опишете предвидените точки за достъп в таблицата по-долу.

<figure><img src="../../../assets/image (152).png" alt=""><figcaption></figcaption></figure>

В качеството на repository слой използвайте singleton с предвидена в него подходяща колекция. 
