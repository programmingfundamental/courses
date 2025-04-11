---
layout: default
title: Validate request data
parent: Laboratory excercise 8
grand_parent: Internet Technologies
nav_order: 1
---


# Validate request data

Validation of data in the request body should be handled by any REST API to ensure that clients do not send misinformation to the server, which could compromise the integrity of the application's data.

Jakarta Bean Validation is a Java specification that allows you to specify constraints on object models through annotations such as @NotNull, @Null, @Min, @Max, etc. It provides an API for validating objects and object graphs, and you can also write custom constraints if the built-in ones do not meet your needs.

Hibernate Validator is a reference implementation of the Java Bean validation specification, providing additional built-in constraints that give you more options for validating objects.

To use Jakarta Bean validation with Hibernate Validator, you need to add the following dependency to your Maven project. This will add the jakarta.validation-api-VERSION.jar and hibernate-validator-VERSION.jar packages to the project.

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

### Bean Validation Basics

Jakarta Bean Validation works by defining constraints on the fields of a class using certain [ annotations](https://docs.jboss.org/hibernate/beanvalidation/spec/2.0/api/javax/validation/constraints/package-summary.html).

#### Common validation annotations

Some of the most common validation annotations are:

* `@NotNull`**:** specifies that the field must not be null.
* `@NotEmpty`**:** specifies that the list box must not be empty.
* `@NotBlank`**:** specifies that the string field must not be an empty string (i.e. must have at least one character).
* `@Min` **и**  `@Max`**:** specifies that a numeric field is valid only when its value is above or below a certain value.
* `@Size`: specifies that the size of the element (CharSequence, Collection, Map, or Array) must be within specified limits. 
* `@Pattern`**:** specifies that a string field is valid only when it matches a specified regular expression.
* `@Email`**:** specifies that the string field must be a valid email address.

Пример:

```java
class Customer {

  @Email(message = "Email must be valid")
  private String email;

  @NotBlank(message = "Name cannot be blank")
  private String name;
  
  // ...
}
```

#### Request body validation

In POST and PUT requests, it is common to pass JSON in the request body. Spring automatically maps the incoming JSON to a Java object. Now we want to check if the incoming Java object meets our requirements.

```java
class Input {

  @Min(1)
  @Max(10)
  private int numberBetweenOneAndTen;

  @Pattern(regexp = "^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$")
  private String ipAddress;
  
  // ...
}
```

We have an int field that must have a value between 1 and 10 inclusive, as defined by the `@Min` and `@Max` annotations. We also have a String field that must contain an IP address, as defined by the regular expression in the `@Pattern` annotation.

To validate the body of an incoming HTTP request, we annotate the request body with the `@Valid` annotation in the REST controller:

```java
@RestController
class ValidateRequestBodyController {

  @PostMapping("/validateBody")
  ResponseEntity<String> validateBody(@Valid @RequestBody Input input) {
    return ResponseEntity.ok("valid");
  }
}
```

We simply added the `@Valid` annotation to the `Input input` parameter, which is also annotated with `@RequestBody,` to indicate that it should be read from the request body. By doing this, we tell Spring to pass the object to a validator before doing anything else.

If the validation fails, it will throw a `MethodArgumentNotValidException`. By default, Spring will translate this exception into an HTTP status code 400 (Bad request).

**Use `@Valid also on` complex types**

If the `Input` class contains a field of another complex type that needs to be validated, that field must also be annotated with `@Valid`.

#### Validation of path variable and query parameter

Validation of template variables and query parameters works a little differently.

In this case, we do not validate complex Java objects, since path variables and query parameters are usually primitive types like `int;` and their wrapper objects like `Integer` or `String`.

Instead of annotating a class field like above, we add a constraint annotation (in this case `@Min`) directly to the method parameter in the controller:

```java
@RestController
@Validated
class ValidateParametersController {

  @GetMapping("/validatePathVariable/{id}")
  ResponseEntity<String> validatePathVariable(
      @PathVariable("id") @Min(5) int id) {
    return ResponseEntity.ok("valid");
  }
  
  @GetMapping("/validateRequestParameter")
  ResponseEntity<String> validateRequestParameter(
      @RequestParam("param") @Min(5) int param) { 
    return ResponseEntity.ok("valid");
  }
}
```

Note that we need to add an `@Validated` annotation to the class-level controller to tell Spring to evaluate constraint annotations on the method parameters.

Unlike validating the request body, a failed validation will throw a `ConstraintViolationException` instead of a `MethodArgumentNotValidException`. Spring does not register a default exception handler for this exception, so by default it will throw a response with HTTP status 500 (Internal Server Error).

If we instead want to return HTTP status 400 (which makes sense since the client is providing an invalid parameter, making it a bad request), we can add a custom exception handler.

#### Add an exception handling method

```java
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
 
***
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
              HttpHeaders headers, HttpStatusCode status,WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), exception.getMessage());
        return  new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
```
