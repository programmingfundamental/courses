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
  db:
    container_name: padawan-6156-db
    image: postgres:latest
    ports:
      - 5432:5432
    environment:
      POSTGRES_PASSWORD: R2D2c3p0
      POSTGRES_USER: padawan
      POSTGRES_DB: tasksdb
    networks:
      - jedi

  app:
    container_name: padawan-app
    platform: "linux/amd64"
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - 9000:9000
    depends_on:
      - db
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/tasksdb
      SPRING_DATASOURCE_USERNAME: padawan
      SPRING_DATASOURCE_PASSWORD: R2D2c3p0
    networks:
      - jedi

```

# Обяснение на docker-compose.yml

Този файл се използва от :contentReference[oaicite:0]{index=0} за автоматично стартиране на няколко контейнера едновременно – в случая:

- PostgreSQL база данни
- Spring Boot приложение

---

# Обяснение на docker-compose.yml

Този файл се използва от :contentReference[oaicite:0]{index=0} за автоматично стартиране на няколко контейнера едновременно – в случая:

- PostgreSQL база данни
- Spring Boot приложение

Docker Compose позволява цялата система да бъде стартирана само с една команда. Това улеснява разработката и тестването, защото всички необходими услуги се конфигурират автоматично.

---

# Дефиниране на мрежа

```yaml
networks:
  jedi:
```

Създава виртуална Docker мрежа с име `jedi`.

Всички контейнери, които са свързани към тази мрежа, могат да комуникират помежду си чрез имената на services.

Docker Compose автоматично създава вътрешен DNS сървър, който преобразува имената на контейнерите в IP адреси. Това позволява лесна комуникация между услугите без необходимост от ръчно конфигуриране на мрежови настройки.

Например:
- приложението достъпва базата чрез `db`
- не е необходимо използване на IP адрес

---

# Services

```yaml
services:
```

Секцията `services` описва контейнерите, които ще бъдат стартирани.

Всеки service представлява отделен контейнер със собствена конфигурация. Docker Compose управлява автоматично тяхното създаване, стартиране и свързване в обща среда.

---

# PostgreSQL контейнер

```yaml
db:
```

Създава service с име `db`.

Това име се използва като hostname от другите контейнери.

Контейнерът `db` изпълнява :contentReference[oaicite:1]{index=1} сървър и съхранява данните на приложението. Всички заявки към базата данни се изпращат към този контейнер.

---

## Име на контейнера

```yaml
container_name: padawan-6156-db
```

Задава конкретно име на контейнера.

Това улеснява разпознаването на контейнера при работа с Docker команди като `docker ps` или `docker logs`. Вместо автоматично генерирано име се използва по-четимо и описателно име.

---

## Docker Image

```yaml
image: postgres:latest
```

Използва официалния PostgreSQL image от Docker Hub.

- `postgres` → image
- `latest` → последна версия

Docker image представлява готов шаблон, който съдържа инсталиран PostgreSQL и необходимите зависимости. При стартиране Docker създава контейнер на базата на този image.

---

## Port Mapping

```yaml
ports:
  - 5432:5432
```

Свързва:
- порт `5432` на хоста
- към порт `5432` в контейнера

Така PostgreSQL може да бъде достъпен и извън Docker.

Например tools като pgAdmin, DBeaver или IntelliJ Database Tool могат да се свържат към базата чрез `localhost:5432`.

---

## Environment Variables

```yaml
environment:
  POSTGRES_PASSWORD: R2D2c3p0
  POSTGRES_USER: padawan
  POSTGRES_DB: tasksdb
```

Конфигурира PostgreSQL при стартиране.

- `POSTGRES_USER` → потребител
- `POSTGRES_PASSWORD` → парола
- `POSTGRES_DB` → база данни

Тези променливи се прочитат автоматично от PostgreSQL image-а при първото стартиране. Ако базата не съществува, контейнерът автоматично я създава.

---

## Свързване към мрежата

```yaml
networks:
  - jedi
```

Добавя контейнера към мрежата `jedi`.

Това позволява контейнерът да комуникира с другите services в проекта. Без свързване към обща мрежа контейнерите не биха могли да обменят информация.

---

# Spring Boot приложение

```yaml
app:
```

Създава контейнер за Java приложението.

Този контейнер изпълнява Spring Boot приложението и обработва HTTP заявки от потребителите. Приложението комуникира с PostgreSQL контейнера чрез Docker мрежата.

---

## Име на контейнера

```yaml
container_name: padawan-app
```

Задава име на контейнера.

Това име улеснява администрацията и дебъгването на приложението. Може лесно да се използва при преглед на логове или изпълнение на Docker команди.

---

## Платформа

```yaml
platform: "linux/amd64"
```

Указва архитектурата на контейнера.

Използва се при Apple Silicon или различни CPU архитектури.

Тази настройка гарантира, че image-ът ще бъде изграден за конкретна архитектура и ще работи коректно на различни устройства.

---

## Build на приложението

```yaml
build:
  context: .
  dockerfile: Dockerfile
```

Docker Compose build-ва image от:
- текущата папка (`.`)
- използвайки `Dockerfile`

Файлът `Dockerfile` съдържа инструкции как да бъде изграден контейнерът на приложението. По време на build процеса се копира `.jar` файлът и се подготвя средата за изпълнение.

---

## Port Mapping

```yaml
ports:
  - 9000:9000
```

Свързва:
- порт `9000` на компютъра
- към порт `9000` в контейнера

Приложението ще бъде достъпно на:

```text
http://localhost:9000
```

Така потребителят може да използва приложението през браузър или REST client като Postman.

---

## Ред на стартиране

```yaml
depends_on:
  - db
```

Указва, че `db` трябва да стартира преди `app`.

Това е важно, защото приложението се нуждае от активна база данни при стартиране. Ако PostgreSQL не е стартиран, Spring Boot няма да може да създаде връзка към базата.

---

# Environment Variables за Spring Boot

```yaml
environment:
  SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/tasksdb
  SPRING_DATASOURCE_USERNAME: padawan
  SPRING_DATASOURCE_PASSWORD: R2D2c3p0
```

Подават се настройки към Spring Boot приложението.

Тези настройки заместват стойностите от `application.properties` и позволяват конфигурацията да бъде променяна без редакция на source кода.

---

## Database URL

```yaml
SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/tasksdb
```

Връзка към PostgreSQL.

- `db` → името на PostgreSQL service-а
- `5432` → PostgreSQL порт
- `tasksdb` → база данни

Използването на `db` вместо `localhost` е важно, защото приложението работи в отделен контейнер. Docker Compose автоматично намира PostgreSQL контейнера чрез вътрешния DNS.

---

## Username и Password

```yaml
SPRING_DATASOURCE_USERNAME: padawan
SPRING_DATASOURCE_PASSWORD: R2D2c3p0
```

Данни за достъп до базата.

Spring Boot използва тези стойности за създаване на JDBC връзка към PostgreSQL. Ако потребителското име или паролата са грешни, приложението няма да може да стартира.

---

## Мрежа

```yaml
networks:
  - jedi
```

Свързва приложението към Docker мрежата.

Това позволява на Spring Boot приложението да комуникира директно с PostgreSQL контейнера чрез hostname `db`.

---

# Стартиране на проекта

```bash
docker compose up --build
```

Командата:
1. build-ва приложението
2. стартира PostgreSQL
3. стартира Spring Boot приложението
4. свързва контейнерите в обща мрежа

Docker Compose автоматично следи зависимостите между services и управлява целия жизнен цикъл на контейнерите.

---

# Спиране на проекта

```bash
docker compose down
```

Спира и премахва контейнерите, създадени от Docker Compose.

Мрежата също се премахва автоматично, ако не се използва от други контейнери.

---

# Изтриване на containers, images и volumes

```bash
docker compose down --rmi all -v
```

Изтрива:
- контейнерите
- Docker images
- volumes с данни

Тази команда се използва при нужда от напълно чисто стартиране на проекта. Полезна е при проблеми с кеширани images или стари PostgreSQL данни.

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