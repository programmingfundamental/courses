---
layout: default
title: Задача
parent: Лабораторно упражнение 6
grand_parent: Интернет технологии
nav_order: 5
---

# Задача 1

Създайте Spring boot проект с име task-manager.

 - Добавете ехо крайна точка с метод GET.
 - Добавете ехо крайна точка с метод POST.
 - Добавете ехо крайна точка с метод DELETE.
 - Добавете ехо крайна точка с метод PUT.

# Задача 2

Създайте конфигурация за изпълнение и проследяване на грешки в проекта от task-manager.
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