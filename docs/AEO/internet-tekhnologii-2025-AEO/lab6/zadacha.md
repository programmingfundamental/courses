---
layout: default
title: Tasks
parent: Laboratory excercise 6
grand_parent: Internet Technologies
nav_order: 10
---

# Task 1

Create a Spring boot project named task-manager.

- Add an echo endpoint with a GET method.
- Add an echo endpoint with a POST method.
- Add an echo endpoint with a DELETE method.
- Add an echo endpoint with a PUT method.

# Task 2

Build a project with Maven

```
./mvnw clean
./mvnw install
./mvnw package
```

Add a Dockerfile to create an image with a Spring boot project.

```Dockerfile
FROM eclipse-temurin:latest
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

Create an image
```
docker build -t demo .
```
Add a compose.yaml file to describe a container to drop the project image into.

```yml
services:
app:
image: <image name>
ports:
- "<host port>:8080"
```

Start the application in Docker:

docker compose up

Open the project and send echo messages.

# Task 3

Create a configuration for executing and tracking errors in the project from task-manager.
To do this, you need to modify compose.yml

image: - section should be replaced with build image:

```yml
build:
context: .
dockerfile: Dockerfile
```
Select edit of the startup configuration

![image](https://github.com/programmingfundamental/courses/assets/10382663/b0d9ee3c-b025-4105-8d05-646ce4cbff44)

Select launch in docker-compose

![image](https://github.com/programmingfundamental/courses/assets/10382663/2dcbe80a-e88c-4ef2-90e6-f0d1f030218a)

As the configuration file, select the compose.yml file in project

![image](https://github.com/programmingfundamental/courses/assets/10382663/988471d2-bf66-4752-a415-74f86c5a0443)

Service the project from the compose file and select next.

![image](https://github.com/programmingfundamental/courses/assets/10382663/ae74eb8c-526e-4e16-ad69-5c375779450b)

Once the image is built select next. On the last step select create.

Select Run to start the container in Docker.
