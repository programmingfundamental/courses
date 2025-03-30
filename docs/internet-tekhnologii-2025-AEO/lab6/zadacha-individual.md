---
layout: default
title: Additional tasks
parent: Laboratory excercise 6
grand_parent: Internet Technologies
nav_order: 2
---

# Zadacha 1 Izteglete Tomcat image ot Docker Hub: ``` docker pull tomcat:latest ``` Proverete v spisŭka s docker images nalichen li e Tomcat image? ``` docker images ``` Startiraĭte konteĭner s izobrazhenieto na Tomcat vŭv fonov rezhim ``` docker run -t -d --name '<ime na konteĭnera>' -p <port na khosta>:8080 <image id>? ``` Proverete nalichnite konteĭneri i vzemete identifikatora na sŭzdadeniya konteĭner s izobrazhenie na Tomcat ``` docker ps -a ``` Izteglete [students.war](../../../assets/students.war) i go kopiraĭte v webapps direktoriyata na Tomcat ``` docker cp <pŭt do resursa>\students.war d266c8fb5dec:<pŭt do webapps>/students.war ``` Izpŭlnete zadachite ot 1 uprazhnenie.
Показване на още
687 / 5 000
# Task 1

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

Download [students.war](../../../assets/students.war) and copy it to the webapps directory of Tomcat

```
docker cp <path to resource>\students.war d266c8fb5dec:<path to webapps>/students.war
```

Complete the tasks from Exercise 1.

