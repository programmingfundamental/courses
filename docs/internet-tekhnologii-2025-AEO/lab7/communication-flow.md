---
layout: default
title: Communication flow
parent: Laboratory excercise 7
grand_parent: Internet Technologies
nav_order: 1
---


# Communication flow 

The following diagram demonstrates the flow associated with processing a request and response in a REST API in a Spring Boot application.

<figure><img src="../../../assets/image (97).png" alt=""><figcaption></figcaption></figure>

In Spring Boot (and Spring in general), the communication flow describes how requests pass through the different layers of the application – from the moment the user sends a request to the moment they receive a response. This includes various components and their interactions.

Here is a typical communication flow in a Spring Boot application:

1\.      Sending an HTTP request    
The user or client application sends a request to the REST API, for example GET /tasks/5.

2\.      Controller          
The controller receives the request and processes it. It determines what type of operation needs to be performed and forwards it to the appropriate business layer.

3\.      Service layer     
This layer contains the business logic of the application. This is where validations, additional checks, and data transformations are performed before working with the database.

4\.      Repository layer      
The service layer communicates with the database through a repository layer. This layer is responsible for accessing and managing data by executing queries to the database.

5\.      Processing of results   
Once the data is retrieved or modified, it is processed and prepared for return to the client. This often involves transforming the data to a DTO (Data Transfer Object) to avoid working directly with the entities.

6\.      Return a response    
The controller returns the processed data as a response, usually in JSON format. Spring Boot automatically serializes the objects into JSON so that clients can use them.

