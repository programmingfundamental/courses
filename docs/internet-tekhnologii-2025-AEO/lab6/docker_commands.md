---
layout: default
title: Docker commands
parent: Laboratory excercise 6
grand_parent: Internet Technologies
nav_order: 2
---

# Basic Docker Commands

## Common Commands

Every command in Docker starts with docker. If you just type docker and run the command (in CMD/PowerShell or whatever you use as a terminal), you will see a list of possible executable commands in Docker.

```
docker
```

Shows the installed version of Docker and information about some components used by Docker.

```
docker version
```

Shows only the version of Docker installed.

```
docker -v (or docker --version)
```

Shows system information related to Docker

```
docker info
```

By adding --help to the end of a command, we can see what it is for and how it is used.

```
docker images --help
```

Shows how many images, containers, etc. we have (as well as their size).

```
docker system df
```

## Working with Docker images

Download an image from Docker Hub.

```
docker pull <image>
```

Build an image from a Dockerfile in the current directory

```
docker build -t <name> .
```

List available images

```
docker images
```

Delete an image

```
docker rmi <image>
```

## Working with Docker containers

Start a new container from an image

```
docker run <image>
```

Start a container in the background

```
docker run -d <image>
```

Start a container with a name

```
docker run --name <name> <image>
```
Start a container with a port

```
docker run -p <host_port>:<container_port> <image_name>
```

List active containers

```
docker ps
```

List all containers (including stopped ones)

```
docker ps -a
```

Stop a container

```
docker stop <container>
```

Start a stopped container

```
docker start <container>
```

Restart a container

```
docker restart <container>
```

Delete a container

```
docker rm <container>
```

## Working with DockerHub

Logging in with a username

```
docker login -u <username>
```
Publish an image to DockerHub

```
docker push <username>/<image_name>
```
