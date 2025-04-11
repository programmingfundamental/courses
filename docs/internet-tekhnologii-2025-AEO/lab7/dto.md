---
layout: default
title: Data transfer object (DTO)
parent: Laboratory excercise 7
grand_parent: Internet Technologies
nav_order: 3
---

# Data transfer object (DTO)

The Data Transfer Object Design Pattern is a commonly used design pattern. It is primarily used to pass data with multiple attributes in a single request from a client to a server, to avoid multiple calls to the remote server.

An advantage of using DTOs in RESTful APIs written in Java (and with Spring Boot) is that they can help hide the implementation details of the objects (JPA entities). Exposing these objects through endpoints can become a security issue if we are not careful about which properties can be changed and by which operations.

Let's create a DTO class:
   
```java
@Setter
@Getter
@NoArgsConstructor
public class UserDto {
	private int id;
	private String name;   	
	private String email;
}
```
