---
layout: default
title: Допълнителни задачи
parent: Лабораторно упражнение 6
grand_parent: Интернет технологии
nav_order: 7
---

# Задача 1

Изтеглете Tomcat image от Docker Hub:

```
docker pull tomcat:latest
```

Проверете в списъка с docker images наличен ли е Tomcat image?

```
docker images
```

Стартирайте контейнер с изображението на Tomcat във фонов режим

```
docker run -t -d --name '<име на контейнера>' -p <порт на хоста>:8080 <image id>?
```

Проверете наличните контейнери и вземете идентификатора на създадения контейнер с изображение на Tomcat

```
docker ps -а
```

Изтеглете [students.war](../../../assets/students.war) и го копирайте в webapps директорията на Tomcat

```
docker cp <път до ресурса>\students.war d266c8fb5dec:<път до webapps>/students.war
```

Изпълнете задачите от 1 упражнение.
