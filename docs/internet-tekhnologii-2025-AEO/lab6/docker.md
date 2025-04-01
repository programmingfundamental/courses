---
layout: default
title: Docker
parent: Laboratory excercise 6
grand_parent: Internet Technologies
nav_order: 1
---

# Docker

Docker is a popular open source containerization tool used to provide a portable and consistent environment for running software applications, while consuming fewer resources than a traditional server or virtual machine. Docker uses containers, isolated user-space environments that run at the operating system level and share system resources such as the kernel and file system

Docker makes it easy to PACKAGE, DELIVERY, and RUN applications. Packaging is the act of putting all the resources needed for an application in one place. This allows the application to be easily portable to different environments running Docker containers. The phases of software development include “design,” “testing,” “maintenance,” and “deployment.” Docker is most commonly used in the “deployment” phase, but it can be used to improve the development process.

# Docker image

A Docker image is a template or image that contains all the components needed to run an application, such as the operating system, libraries, applications, and configuration files. This is a static image that is used to create Docker containers. A Docker image can be created by a user or downloaded from a central repository such as Docker Hub. A Docker image is stateless and cannot be modified after it is created.

# Docker container

A Docker container is an instance based on a Docker image. It is an executable environment that can be started and executed on any Docker-enabled system. The container contains a working copy of the Docker image, adding state (such as temporary files or file system changes) that can be modified at runtime. Docker containers are lightweight, portable, and can be started and stopped very quickly. Each container runs in an isolated space, sharing the kernel of the host operating system, but has its own file system, processes, network, and other resources.

# Docker Volumes

A Docker Volume is a mechanism for persistent data storage outside the container. It allows data to persist even after the container is deleted. This is useful for storing data such as databases, configuration files, and more that require persistence. A Docker volume is used to create a "bridge" between the host file system and the container, thus ensuring data persistence even after the container's lifecycle ends.

# Dockerfile

A Dockerfile is a text file that contains instructions for creating a Docker image. This file defines all the steps and commands needed to create an image, which can then be used to create Docker containers. A Dockerfile contains instructions such as what base image to use, what files to copy to the image, what commands to run during image creation, and other configuration settings.

# Docker compose

Docker Compose is a tool that allows the definition and management of multiple containers for Docker applications. It uses YAML files to define the settings for each container and the relationships between them. With Docker Compose, you can describe aspects such as images, networks, shared volumes, and other configuration settings that are required for your applications.

The main benefits of Docker Compose include:

Definition of Infrastructure as Code (IaC): Docker Compose allows you to define the entire infrastructure of your application in a single YAML file that is easy to understand and maintain.

Easy application startup and shutdown: With Docker Compose, you can easily start and stop your entire application with a single command.

Define connections between containers: Docker Compose allows you to define the connections between different containers in your project, thus you can easily manage the communication between them.

Manage shared volumes and networks: Docker Compose allows you to define shared volumes and networks for your containers, which is useful when sharing data or settings between different containers.

With Docker Compose, you can easily start and manage the entire infrastructure of your application with minimal effort, making it an extremely useful tool for developing and breaking down Docker applications.
