---
layout: default
title: Laboratory exercise 1
parent: Training Practice 3
has_children: true
nav_order: 1
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

### Task

Develop a REST application for managing service requests.

Customers must be able to submit service requests, while office staff must be able to view the submitted requests.

The application will be extended with additional functionality in the subsequent exercises.

### Experiments

*Experiment 1: Minimal Context*

Create an empty Java project. Provide the AI assistant with the following prompt:

```text
Create REST app that handles client requests.
```

Review the generated solution without making any changes to it first.

Determine:

- which technologies the AI assistant selected;
- which classes it created;
- how responsibilities are distributed among them;
- which properties of a request it assumed;
- which operations it implemented;
- which decisions it made even though they were not specified in the prompt.

Compare the structure of the generated project with the structure of a Spring Boot REST application reviewed at the beginning of the exercise.

Which parts of the generated solution result directly from the given task, and which are assumptions made by the AI assistant?

*Experiment 2: Adding Technological Context*

Start again from an empty project. Provide the following prompt:

```text
Create SpringBoot REST app that handles customer requests. It should use Maven, Spring Web, Lombok, H2, validation; the app should be called ServiceRequestManagementSystem.
```

Analyze the generated project.

Check:

- whether a Maven project has been created;
- whether the required dependencies have been added;
- how the packages are organized;
- whether there is a separation between the controller, service, and repository layers;
- whether DTOs are used;
- what model has been created for a customer request;
- which REST endpoints have been implemented;
- which functional decisions the AI assistant made independently.

Compare the result with the result of the first experiment.

Verification of the Generated Solution

Compile and run the application. A statement by the AI assistant that the application compiles or runs successfully must not be accepted as evidence that it actually does.

If an error occurs:

- analyze the error message;
- determine the probable cause;
- check the configuration and the versions being used;
- if necessary, use the AI assistant to help analyze the problem;
- independently verify the proposed solution.

*Experiment 3: Functional, Technological, and Architectural Context*

Start again from an empty project. This time, provide the AI assistant with the technologies to be used as well as the initial structure and functionality of the application.

Provide the following prompt:

```text
Create SpringBoot REST app that handles customer requests. It should use Maven, Spring Web, Lombok, H2, validation; the app should be called ServiceRequestManagementSystem. Necessary packages: controller, dto, exception, entity, repository, service.
Two controllers: RequestController and OfficeRequestController. Endpoints:
POST /api/requests
GET /api/requests/{id}
GET /api/requests?customerEmail={email}
GET /api/office/requests
GET /api/office/requests/{id}
ServiceRequest to be the base entity. Properties: id, customerName, customerEmail, description, serviceType, executionMode, priority, address, urgencyReason, status, submittedAt. serviceType, executionMode, priority and status are enums.
ServiceType: INSTALLATION, REPAIR, DIAGNOSTICS, CONSULTATION.
ExecutionMode: REMOTE, ON_SITE.
Priority: NORMAL, URGENT.
RequestStatus: SUBMITTED, PROCESSING, OFFERED, ACCEPTED, SCHEDULED, IN_PROGRESS, COMPLETED, REJECTED, EXPIRED, CANCELLED.
New request is created with status SUBMITTED, submittedAt is set by the system. Customer doesn't send id, status and submittedAt.

```

Analyze the generated solution and compare it with the results of the previous two experiments.

Pay attention to:

- the package structure;
- the classes that have been created;
- the responsibilities of the individual layers;
- the dependencies between them;
- the use of DTOs;
- the implemented endpoints;
- the representation of enum types;
- where the conversion between entities and DTOs is performed;
- the decisions that the AI assistant has still made independently.

Compile and run the generated application.

How did the generated solution change when the technological, functional, and architectural decisions were specified in advance?

*Experiment 4: Architectural Review with an AI Assistant*

Once the application is running, use the AI assistant not to generate new code, but to evaluate the existing solution.

Prompt:

```text
Review the architecture of the currently generated Spring Boot application. Analyze its package organization, layering, separation of responsibilities, dependencies between components, and maintainability. Identify possible improvements and justify each recommendation. Distinguish between improvements that are justified at the current size of the project and improvements that would be premature at this stage. Do not modify the code and do not add new functionality.
```

Review each recommendation provided by the AI assistant. For each recommendation, determine:

- what problem the AI assistant claims the recommendation solves;
- whether this problem actually exists in the current project;
- whether the structural change is necessary or merely adds additional complexity;
- whether the recommendation should be accepted or rejected, and why.

Do not modify the code simply because the AI assistant has recommended a change.

Select at least one recommendation that requires further justification or that is likely to be rejected.

Continue the conversation with the AI assistant by identifying the recommendation in question, providing any additional relevant context, and requesting further justification without modifying the code.

After receiving the response, make a final decision on whether the recommendation should be implemented.

The goal is not to convince the AI assistant to agree with a predetermined decision, but to use the discussion to evaluate the available alternatives.


*Experiment 5: Applying the Selected Changes*

After deciding which architectural changes will be accepted, ask the AI assistant to apply only those changes.

Use the following prompt as a template:

```text
Please apply the following approved architectural changes to the current project:
[описват се избраните промени]
Update all package declarations, imports, constructor dependencies, and references affected by these changes. 
Do not change the existing REST endpoints, DTOs, entity properties, validation rules, persistence behavior, exception handling, or application functionality.
After the refactoring, run tests/build and report the actual results.

```

After the changes have been applied:

- review the files that were actually modified;
- verify that the AI assistant made only the requested changes;
- compile and run the application;
- verify that the existing functionality has been preserved.

### Lab Result

By the end of the exercise, there should be a working Spring Boot REST application containing the basic project structure and the initial service request model. The resulting project will be used and extended in the subsequent exercises.

