---
layout: default
title: Additional tasks
parent: Laboratory excercise 6
grand_parent: Internet Technologies
nav_order: 2
---

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

