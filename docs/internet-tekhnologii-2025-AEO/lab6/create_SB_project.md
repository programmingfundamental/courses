---
layout: default
title: Create Spring boot project
parent: Laboratory excercise 6
grand_parent: Internet Technologies
nav_order: 4
---

# Creating a Spring Boot Project

### Using Spring Initializr

There are many ways to create a Spring Boot application. The easiest way is to use Spring Initializr at https://start.spring.io/, an online Spring Boot application generator.

<figure><img src="../../../assets/image (50).png" alt=""><figcaption></figcaption></figure>

1\. Select Maven and Spring Boot version

2\. Enter the Maven project details as follows:

a. Group: bg.tu-varna.sit

b. Artifact: tasks-manager

c. Package Name: bg.tu\_varna.sit.tasks\_manager

d. Package: JAR

e. Java Version: 21

f. Language: Java

3\. Click the Add Dependencies button. You will see many starter modules organized into different categories, such as Core, Web, and Data. Select Spring Web.

4\. Click the Generate Project button. Unzip the downloaded ZIP file and open the project in your preferred IDE.

The generated Spring Boot project structure is relatively simple and consists of only the components you need to continue developing your Spring Boot application. It contains the following components:

* A pom.xml file that contains the dependencies you selected during project generation.
* A maven wrapper file that allows you to build the project without installing Maven on your local machine.
* A package structure that contains the source and test Java files. The source package contains a Java class with a main method, and the test package has an empty test class.
* A resources folder to hold additional project artifacts and an empty application.properties file.

Let's discuss the key components of the generated project in detail.

### Running Maven Lifecycle Commands with Maven Wrapper

./mvnw clean - cleans up all compiled files

./mvnw install - starts compiling, building and packaging the project

./mvnw package - starts packaging the project

### Spring Boot starter dependency

Spring Boot starter dependency aims to make developing a Spring Boot application easy, fast and efficient. If you have previous experience developing Java applications with a build tool like Apache Maven or Gradle, you may recall that dependency management is one of the key challenges for an application developer. The first challenge is to identify the libraries (dependencies) that you need to develop a specific component of your application. Once you have identified them, you need to find the correct versions of the libraries. Even if you find the right libraries and versions, in this fast-paced world of application development it is relatively easy to get out of sync with the versions. To further increase your problems, the dependencies you choose have their own dependencies, or more precisely, transitive dependencies.

The Spring Boot starter dependency is a solution that frees you from all the aforementioned problems. A starter dependency groups together a set of dependencies that you might need to develop a part of your application. If you choose to develop a web application with Spring Boot, you will most likely choose the spring-boot-starter-web dependency. It ensures that all the dependencies needed to develop a web application are available. Of course, you get the set of dependencies that the Spring team recommends you have to develop a web application. However, the key part here is that you are free from dependency versions, upgrades, and many other issues. A starter dependency can also depend on another starter dependency. For example, spring-boot-starter-web needs a few common startup dependencies:

* spring-boot-starter
* spring-boot-starter-json
* spring-boot-starter-tomcat
* spring-web
* spring-webmvc

These in turn download another set of dependencies related to Spring Boot, Tomcat, and JSON, respectively.

Spring Boot allows you to create your own starters that you can use in your application. This is useful for large applications to modularize and manage dependencies with respect to custom starters.

In the generated project, we have included two startup dependencies: spring-boot-starter-web and spring-boot-starter-test. The web starter dependency includes the necessary JAR files to build a web application, while the test dependency allows you to write tests for your application.

### Dependency Versioning in Maven

In Maven, the <parent> element in pom.xml indicates that the current project (module) inherits configuration from another parent POM file. This is useful for centralizing dependencies, plugin versions, and common settings.

```XML
<parent>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-parent</artifactId>
<version>3.4.4</version>
<relativePath/>
</parent>
```

<parent> defines:

- Dependency inheritance – written once.

- Plugin inheritance and versions – can be set once in the parent.
- Build settings – e.g. encoding, source/target version, etc.
- Profile – defines common Maven profiles in the parent.
- Version management with <dependencyManagement> – sets versions centrally.

# Main class in Spring Boot

In the generated project, you can find that Spring Initializr has generated a Java class with a Java main() method in it.

```java
package com.manning.sbip.ch01;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootAppDemoApplication {
public static void main(String[] args) {
SpringApplication.run(SpringBootAppDemoApplication.class, args);
}
}
```

Let's look at the following components of the generated Java file:

1\. The main() method.

2\. The use of the @SpringBootApplication annotation

3\. The Role of the SpringApplication Class

### The main() Method

To run a web application, you build and package the application components in a WAR or EAR archive file and deploy it on the web (e.g., in Apache Tomcat) or on an application server (e.g., Red Hat JBoss). Spring Boot simplifies this process to some extent. It does not force you to create a WAR or EAR file for your application. Instead, it allows you to run your Spring Boot application as a regular Java application using a conventional main() method.

Although Spring Boot follows the familiar “Keep it simple” approach to keep things simple for developers, it does a lot of heavy lifting behind the scenes. For example, a servlet-based web application can only run in a container such as Apache Tomcat or Jetty. Spring Boot enables this support by using an embedded Apache Tomcat server in the application by default. Thus, when you start the application using the main() method, Spring Boot starts an embedded instance of the Apache Tomcat server and runs the web application inside it. If you further examine the spring-boot-starter-web dependency, you can find a transitive dependency on the spring-boot-starter-tomcat module.

### The @SpringBootApplication annotation

You may notice that the class in the generated Java file is annotated with the @SpringBootApplication annotation. This is a convenient annotation that contains three annotations: @EnableAutoConfiguration, @ComponentScan, and @SpringBootConfiguration, each of which performs a specific task in the application:

* **@EnableAutoConfiguration** - provides the necessary support for automatically configuring the application based on the available JAR dependencies in its classpath.

* **@ComponentScan** - provides support for scanning packages for Spring components. A component in Spring is a Java bean that is managed by Spring and is annotated with @Component, @Bean, or another specialized component annotation. With the @ComponentScan annotation present, the Spring Boot application scans to identify all components present in the main package and subpackages below it, in order to subsequently manage their lifecycle.

* **@SpringBootConfiguration** - indicates that this class is a Spring configuration class. It is meta-annotated with the Spring @Configuration annotation, so that configurations in the annotated class can be found automatically by Spring Boot.

Note that the main class of the Spring Boot application must be located in the root package, since the @SpringBootApplication annotation is configured on this class. The @SpringBootApplication annotation uses the main package as the base package. Thus, the base package and all other subpackages are automatically scanned by Spring Boot to load Spring components (e.g. classes configured with @Component, @Configuration, and other Spring annotations) and other types.

### The SpringApplication class

The next and final component is the use of SpringApplication in the generated Java file. This class is provided by Spring Boot for convenient loading of a Spring Boot application. Most of the time, you will use the static run() method of SpringApplication to start your application. Spring Boot performs several activities while executing the run() method:

1\. Creates an ApplicationContext instance based on the libraries present in the classpath

2\. Registers a CommandLinePropertySource to expose command-line arguments as properties to Spring

3\. Refreshes the ApplicationContext created in step 1 to load all singleton beans

4\. Triggers the ApplicationRunners and CommandRunners configured in the application

#### ApplicationContext

Most Java applications that you develop are composed of objects. These objects interact with each other and have dependencies between them. To efficiently manage object creation and interdependencies, Spring uses the principle of Dependency Injection (DI). This Dependency Injection, or Inversion of Control (IoC) approach, allows Spring to create the objects (or beans in Spring parlance) and inject the dependencies externally. Bean definitions are presented to Spring either through XML bean definition files (e.g. applicationContext.xml) or through annotation-based configurations (@Configuration annotation). Spring loads bean definitions and keeps them available in the Spring IoC container. The ApplicationContext interface acts as a Spring IoC container. Spring provides a variety of ApplicationContext implementations based on the type of application (Servlet or Reactive application), the bean definition configurations (e.g., to load from classpath or annotation), and so on.

The SpringApplication class attempts to create an ApplicationContext instance based on the JAR dependencies present in the classpath. A Spring Boot web application can be servlet-based or reactive-based. Using Spring's class loading techniques and based on the availability of classes in the classpath, Spring infers the type of the current application. Once the application type is known, Spring Boot loads the application context.

# Configuration Management with application.properties

Spring Initializr generates an empty application.properties file in the src/main/resources folder. This properties file allows you to specify various application configurations (e.g., server details or database details). Although there are multiple ways to set properties for a Spring Boot application, this is the most commonly used approach. This properties file allows you to specify configurations in a key-value pair format, where the key is separated from the associated value by an = sign. The following figure shows a sample configuration in an application.properties file to configure the server address and port of a Spring Boot application.

<figure><img src="../../../assets/image (17).png" alt=""><figcaption></figcaption></figure>

To see the application.properties file in action, you can change the value of server.port in the current application to a different HTTP port value (e.g., to 9090). If you run the application after this modification, you can see that it starts on the updated HTTP port.

In application.properties the following can be configured:

- spring.application.name -> <project name>
- server.port -> <application port>
- server.sevlet.context-path -> <project root path>

You can find a list of supported properties from application.properties on the Spring Boot website ([https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html)).
