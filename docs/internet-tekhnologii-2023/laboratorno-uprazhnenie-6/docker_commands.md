---
layout: default
title: Основни команди в Docker
parent: Лабораторно упражнение 6
grand_parent: Интернет технологии
nav_order: 2
---

# Основни команди в Docker

## Общи команди

Всяка команда в Docker започва с docker. Ако напишеш само docker и изпълниш командата (в CMD/PowerShell или каквото ползваш като терминал), ще видиш списък с възможните изпълними команди в Docker.

```
docker
```

```
//Показва коя е инсталираната версия на Docker и информация за някои компоненти, които се използват от Docker.
docker version   

//Показва само версията на инсталирания Docker.
docker -v (или docker --version)

//Показва системна информация, свързана с Docker
docker info

//Като добавим --help в края на някоя команда, можем да видим за какво и как се използва тя.
docker images --help

//Показва колко на брой имиджа, контейнера и т.н. имаме (както и размера им).
docker system df
 ```


## Работа с Docker image

```
docker pull <image>
```

 Изтегляне на имидж от Docker Hub.

```
docker build -t <name> .
```

Създаване на имидж от Dockerfile в текущата директория

```
docker images
```
Извежда списък с наличните имиджи

```
docker rmi <image>
```

Изтриване на имидж

## Работа с Docker container

```
docker run <image>

```

Стартиране на нов контейнер от имидж

```
docker run -d <image>
```

Стартиране на контейнер във фонов режим

```
docker run --name <name> <image>	
```

Стартиране на контейнер със задаване на име


```
docker run -p <host_port>:<container_port> <image_name>
```

Стартиране на контейнер със задаване на порт

```
docker ps	
```

Извежда списък на активните контейнери

```
docker ps -a	
```

Извежда списък с всички контейнери (включително спрени)

```
docker stop <container>
```

Спиране на контейнер

```
docker start <container>
```

Стартиране на спрян контейнер

```
docker restart <container>
```

Рестартиране на контейнер

```
docker rm <container>
```

Изтриване на контейнер

## Работа с DockerHub

```
docker login -u <username>
```
Вписване с потребителско име

```
docker push <username>/<image_name>
```

Публикуване на имидж в DockerHub
	

```
docker pull <image_name>
```
Изтегляне на имидж от DockerHub
	
	
