---
layout: default
title: Excercises
parent: Laboratory excercise 6
grand_parent: Internet Technologies
nav_order: 3
---

# Spring Boot

Spring Boot is a framework that helps developers build Spring-based applications quickly and easily. It is a major extension of the Spring framework, eliminating the need to manually specify stereotypical configurations required for every Spring application.

Spring Boot was launched in April 2014, with the aim of reducing some of the burden of developing Java web applications. It allows developers to focus more on business logic than on templated technical code and its associated configurations. Spring Boot is used to create Spring-based, ready-to-use, standalone applications with minimal configuration changes required by the developer. It takes an opinionated view of the Spring Framework so that developers can quickly launch applications with what they need. It provides an additional layer between the Spring Framework and the user, simplifying certain aspects of configuration.

<figure><img src="../../../assets/image (18).png" alt=""><figcaption></figcaption></figure>

### Key Features of Spring Boot

Spring Boot has several notable features that set it apart from other frameworks:

* Fast Startup

One of the main goals of Spring Boot is to provide a fast startup for Spring application development. With Spring Boot, you can generate an application by specifying the dependencies you need in your application, and Spring Boot will take care of the rest.

* Automatic Configuration

Spring Boot automatically configures the minimum components required by a Spring application. It does this based on the available JAR files in the classpath or based on properties specified in property files. For example, if Spring Boot detects the presence of a database driver JAR file (e.g., an in-memory H2 database) in the classpath, it automatically configures the corresponding data source to connect to the database.

* Opinionated

Spring Boot is opinionated. It automatically configures a minimum set of Spring application components. Spring Boot takes care of this with a set of starter dependencies. Starter dependencies target specific areas of application development and provide related dependencies. For example, if you are developing a web application, you can configure the spring-boot-starter-web dependency, which ensures that all related web application development dependencies, such as spring-web and spring-webmvc, are available in the application classpath.

* Standalone

Spring Boot applications embed a web server so that they can run independently and do not require an external web server or application server. This allows them to be packaged as an executable JAR file and launched with the java -jar command. This allows Spring Boot applications to be easily containerized and deployed in the cloud.

* Production-ready

Spring Boot provides several useful features for monitoring and managing the application once it is deployed, such as health checks, thread dumps, and other useful metrics.
