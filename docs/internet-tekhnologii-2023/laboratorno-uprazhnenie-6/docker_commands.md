---
layout: default
title: Основни команди в Docker
parent: Лабораторно упражнение 6
grand_parent: Интернет технологии
nav_order: 5
---

# Основни команди в Docker

## Общи команди

Всяка команда в Docker започва с docker. Ако напишеш само docker и изпълниш командата (в CMD/PowerShell или каквото ползваш като терминал), ще видиш списък с възможните изпълними команди в Docker.

```
docker
```

Показва коя е инсталираната версия на Docker и информация за някои компоненти, които се използват от Docker.

```
docker version   
```

Показва само версията на инсталирания Docker.

```
docker -v (или docker --version)
```

Показва системна информация, свързана с Docker

```
docker info
```

Като добавим --help в края на някоя команда, можем да видим за какво и как се използва тя.

```
docker images --help
```

Показва колко на брой имиджа, контейнера и т.н. имаме (както и размера им).

```
docker system df
 ```


## Работа с Docker image

Изтегляне на имидж от Docker Hub.

```
docker pull <image>
```

Създаване на имидж от Dockerfile в текущата директория

```
docker build -t <name> .
```

Извежда списък с наличните имиджи

```
docker images
```

Изтриване на имидж

```
docker rmi <image>
```

## Работа с Docker container

Стартиране на нов контейнер от имидж

```
docker run <image>
```

Стартиране на контейнер във фонов режим

```
docker run -d <image>
```

Стартиране на контейнер със задаване на име

```
docker run --name <name> <image>	
```
Стартиране на контейнер със задаване на порт

```
docker run -p <host_port>:<container_port> <image_name>
```

Извежда списък на активните контейнери

```
docker ps	
```

Извежда списък с всички контейнери (включително спрени)

```
docker ps -a	
```

Спиране на контейнер

```
docker stop <container>
```

Стартиране на спрян контейнер

```
docker start <container>
```

Рестартиране на контейнер

```
docker restart <container>
```

Изтриване на контейнер

```
docker rm <container>
```

## Работа с DockerHub

Вписване с потребителско име

```
docker login -u <username>
```
Публикуване на имидж в DockerHub

```
docker push <username>/<image_name>
```
Изтегляне на имидж от DockerHub
	
```
docker pull <image_name>
```

	
	
