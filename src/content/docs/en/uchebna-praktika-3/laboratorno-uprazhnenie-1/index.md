---
title: Laboratory exercise 1
sidebar:
  order: 1
---
# Laboratory exercise 1

## Spring Boot REST Application and Initial Experiments with Context

### Objective 

In this exercise, the initial version of a REST application for managing service requests will be created.

The results produced by an AI assistant will be compared using different levels of specificity in the provided context. The generated code must be reviewed, run, and evaluated before it is used as the basis for the subsequent exercises.

### Brief Review of Spring Boot REST Applications

Spring Boot simplifies the development of Java applications based on Spring by providing ready-to-use configuration and dependency management.

A typical REST application can be organized into several layers:

- Controller – receives HTTP requests and returns HTTP responses;
- Service – contains the logic for performing operations;
- Repository – provides access to stored data;
- Entity – represents objects stored in the database;
- Data Transfer Object (DTO) – is used to transfer data to and from the REST API.

The processing of a request can be represented as follows:

HTTP Request
     ↓
Controller
     ↓
Service
     ↓
Repository
     ↓
Database

*Dependency Injection*

Objects in a Spring application do not need to create the dependencies they require themselves. Instead, these dependencies can be provided through dependency injection.

For example, a controller can receive the required service through its constructor:

```java
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }
}
```

*REST endpoints*

A REST application provides endpoints that are accessible through HTTP requests. The most commonly used HTTP methods are:

| Method  | Purpose                     |
|---------|-----------------------------|
| GET     | retrieve data               |
| POST    | create a new resource       |
| PUT     | update an existing resource |
| DELETE  | delete a resource           |

Request and response data are usually represented in JSON format.

*Структура на Maven проект*

The main directories and files in a Maven project are:

<img
  width="300"
  alt="mvn project structure"
  src="https://github.com/user-attachments/assets/965b6dfe-6296-4b8b-8dfa-657ccf04b9bf"
/>


*pom.xml*

The pom.xml file describes the project and its dependencies, while src/main/resources contains configuration files such as application.properties.
