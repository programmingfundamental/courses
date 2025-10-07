---
layout: default
title: Handling exceptions
parent: Laboratory excercise 8
grand_parent: Internet Technologies
nav_order: 3
---


# Handling exceptions in a Spring Boot application

The Spring framework offers developers several options for handling exceptions in their applications. One of them is a global exception handler with the `@ControllerAdvice` and `@ExceptionHandler` annotations.

With this type of exception handling, you need to write a separate class that handles all exceptions that may be thrown by the application in one central place:

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception1.class)
    public String handleException1() {
        // handles exception type Exception1  
    }     
    @ExceptionHandler(Exception2.class)
    public String handleException2() {
        // handles exception type Exception2  
    }   
     
    // handlers for other exceptions...
}

```

The class is annotated with `@ControllerAdvice`, which is a type of `@Component`. It tells Spring to intercept all the handling methods and execute them when the corresponding exceptions are thrown. In the exception handling methods, you can log the exception, redirect to user-friendly error pages, change the response status code, and any other custom logic you want to handle when the matching exception occurs.

Example:

CityController.java

```java
@RestController
public class CityController {

    @GetMapping("/{id}")
    public ResponseEntity<CityResponseDto> getById(@PathVariable Long id) throws CityNotFoundException {
        throw new CityNotFoundException(id);
    }
}
```

CityNotFoundException.java

```java
public class CityNotFoundException extends RuntimeException {

    public CityNotFoundException(Long id) {
        super(String.format("City with Id %d not found", id));
    }
}
```

ErrorDetails.java

```java
public class ErrorDetails {
    private Date time;
    private String message;
//+ fields for other required data
}
```

GlobalExceptionHandler.java

```java
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CityNotFoundException.class)
  public ResponseEntity<ErrorDetails> handleCityNotFoundException(CityNotFoundException exception) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), exception.getMessage());
        return  new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
        }
    }
}
```


