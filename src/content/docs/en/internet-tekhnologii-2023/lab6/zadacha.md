---
title: Tasks
sidebar:
  order: 100
  label: Tasks
taskPage: true
---
## Task 1

Create a Spring boot project named task-manager.

- Add an echo endpoint with a GET method.
- Add an echo endpoint with a POST method.
- Add an echo endpoint with a DELETE method.
- Add an echo endpoint with a PUT method.

## Task 2

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

## Task 3

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

## Task 1

Download Tomcat image from Docker Hub:

```
docker pull tomcat:latest
```

Check the list of docker images to see if the Tomcat image is available?

```
docker images
```

Start a container with the Tomcat image in the background

```
docker run -t -d --name '<container name>' -p <host port>:8080 <image id>?
```

Check the available containers and get the ID of the created Tomcat image container

```
docker ps -a
```

Download [students.war](/courses/assets/students.war) and copy it to the webapps directory of Tomcat

```
docker cp <path to resource>\students.war d266c8fb5dec:<path to webapps>/students.war
```

Complete the tasks from Exercise 1.
