---
layout: default
title: Service Layer
parent: Laboratory excercise 9
grand_parent: Internet Technologies
nav_order: 4
---

# Service Layer

The service layer is part of the application's business logic. It sits between the controllers (which serve client requests) and the repositories (which work with the database). Its main purpose is to encapsulate and organize the business logic, keeping the architecture clean and modular.

### Annotation

@Service — marks the class as a service component managed by the Spring container.

@Transactional — specifies that a method or class should be executed within a transaction.

Example with transactionality:

```java
@Service
@Transactional
public class OrderService {
// ...
}
```

