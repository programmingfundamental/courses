---
layout: default
title: Задача
parent: Лабораторно упражнение 12
grand_parent: Интернет технологии
nav_order: 6
---

# Задача 1

Изграждане на проект с Maven

```
./mvnw clean
./mvnw install
./mvnw package 
```

Добавете Dockerfile за създаване на image със Spring boot проект.

```Dockerfile
FROM eclipse-temurin:latest
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

Създаване на image
```
docker build -t demo .
```
Добавете compose.yaml файл за описание на контейнер за оставяне на изображението на проекта.

```yml
services:
  app:
    image: <име на изображението>
    ports:
      - "<порт на хоста>:8080"
```

Стартирайте приложението в Docker:

docker compose up

# Задача 2

## Миграция на проекта от H2 към PostgreSQL.

Добавете PostgreSQL dependency в `pom.xml`:

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

Променете `application.properties` файла:

```properties
spring.datasource.url=jdbc:postgresql://db:5432/tasksdb
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

## Docker конфигурация (compose.yml)

Добавете следната конфигурация:

```yaml
networks:
  jedi:

services:
  app:
    container_name: padawan-app
    platform: "linux/amd64"
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - 9000:9000
    networks:
      - jedi
  
  db:
    container_name: padawan-6156-db
    image: postgres:latest
    ports:
      - 5432:5432
    environment:
      POSTGRES_PASSWORD: R2D2c3p0
      POSTGRES_USER: padawan
      POSTGRES_DB: tasks
    networks:
      - jedi
```

### Обяснение на конфигурацията:   

### 1.Мрежа (networks)
```yaml
networks:
  jedi:
```

Създава Docker мрежа, в която контейнерите комуникират помежду си по име.

### 2.PostgreSQL контейнер (db)
```yaml
image: postgres:latest
```

Стартира PostgreSQL image.

**Настройки:**     
POSTGRES_DB: tasks → създава база данни tasks    
POSTGRES_USER: padawan → потребител за базата   
POSTGRES_PASSWORD: R2D2c3p0 → парола

Контейнерът е достъпен в мрежата като:    
db:5432

### 3.Spring Boot контейнер (app)

```yaml
build:
  context: .
  dockerfile: Dockerfile
```

Приложението се билдва локално от Dockerfile, вместо да се използва готов image.

## Конфигурация за стартиране

Избирате edit на конфигурацията за стартиране:

![image](https://github.com/programmingfundamental/courses/assets/10382663/b0d9ee3c-b025-4105-8d05-646ce4cbff44)

Избирате стартиране в docker-compose

![image](https://github.com/programmingfundamental/courses/assets/10382663/2dcbe80a-e88c-4ef2-90e6-f0d1f030218a)

Като файл за конфигуриране избирате compose.yml файла в проекта

![image](https://github.com/programmingfundamental/courses/assets/10382663/988471d2-bf66-4752-a415-74f86c5a0443)

Сървиса на проекта от compose файла и избирате напред.

![image](https://github.com/programmingfundamental/courses/assets/10382663/ae74eb8c-526e-4e16-ad69-5c375779450b)

След като се билдне изображението избирате напред. На последната стъпка избирае create.

Избирате Run за да стартирате контейнера в Docker.