---
layout: default
title: Spring Security
parent: Laboratory excercise 11
grand_parent: Internet Technologies
nav_order: 1
---

# Spring Security

Security is an essential aspect of software application design. It protects the software from unauthorized access and misuse.

There are two main terms that need to be understood when talking about security. Authentication refers to verifying the user’s credentials. Authorization refers to verifying the user’s ability to perform a particular action.

The Spring Framework provides a special module called Spring Security that focuses on the security aspects of Spring applications. Spring Boot provides easy integration with Spring Security using the spring-boot-starter-security dependency. Some of the default security features offered by Spring Security in a Spring Boot application are as follows:

· Spring Security requires users of the application to be authenticated before accessing it.

· If the application does not have a login page, Spring Security generates a default login page for the user to log in and allows the user to log out of the application.

· Spring Security provides a default user named user and generates a default password (printed in the console log) for form-based login.

· Spring Security provides several password encoders to encode the password and store it in the storage.

· Spring Security prevents session hold attacks by changing the session ID after the user has successfully authenticated.

· Spring Security provides default protection against cross-site request forgery (CSRF) attacks. This is done by including a randomly generated token in the HTTP response. It expects this token to be present in all subsequent form-based requests that intend to perform a state-changing operation in the application. A malicious user will not have access to the token and therefore cannot perform CSRF attacks.

# Adding Spring Security to a Spring Boot application

To get started, you need to add the following dependency to your Spring Boot application:

```xml
	  <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
```

That's all you need to do to add basic security to your application. When you run the application, you can see the following entry:

Using generated security password: b5dd65a5-b0e7-49d0-8f2b-eaa55abf0487

This generated password is for development use only. You should update your security configuration before running the application in a production environment. If you open your application at http://localhost:8080, the browser will go to the default login page.

Let's enter the credentials to access the application. The default user is user, and the password is automatically generated and can be found in the logs, as mentioned. The default role is USER, which means that if no specific role is specified for the user, the user will be associated with the USER role, which is intended for regular users with basic access rights. You can change the default user credentials in application.properties as follows:

```properties
spring.security.user.name=admin
spring.security.user.password=secret
spring.security.user.roles=ADMIN
```

This solution is suitable for a quick demo. You may want to implement a more complex and mature security system such as a registration feature where users can register before accessing the application and accessing resources where the user has the necessary permissions. You may also want to introduce roles such as ADMIN, USER, and others.
