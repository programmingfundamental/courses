---
layout: default
title: Обработване на изключения в Spring Boot приложение
parent: Лабораторно упражнение 8
grand_parent: Интернет технологии
nav_order: 3
author: пренареждане
---

# Обработване на изключения в Spring Boot приложение

Spring framework предлага на разработчиците няколко опции за обработка на изключения в техните приложения. Един от тях е global exception handler с анотации `@ControllerAdvice` и `@ExceptionHandler`.

При този вид обработка на изключения е необходимо да разпишете отделен клас, който да обработва всички изключения, които могат да бъдат хвърлени от приложението, на едно централно място:

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

Класът е анотиран с `@ControllerAdvice`, което е вид `@Component`. Той казва на Spring да прихване всички методи на обработка и да ги изпълни, когато бъдат хвърлени съответните изключения. В методите за обработване на изключения можете да регистрирате изключението, да пренасочите към удобни за потребителя страници за грешки, да промените кода на състоянието на отговора и всяка друга персонализирана логика, която искате да обработите, когато възникне съвпадащото изключение.

Пример:

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
//+ полета за други необходими данни
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


