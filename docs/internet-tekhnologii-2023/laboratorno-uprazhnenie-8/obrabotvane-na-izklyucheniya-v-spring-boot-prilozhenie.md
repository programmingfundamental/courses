---
layout: default
title: Обработване на изключения в Spring Boot приложение
parent: Лабораторно упражнение 8
grand_parent: Интернет технологии
nav_order: 1
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

CityService.java

```java
@Service
public class CityService implements ICityService {

    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Override
    public City findById(Long id) {

        return cityRepository.findById(id)
                .orElseThrow(() -> new CityNotFoundException(id));
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

GlobalExceptionHandler.java

```java
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CityNotFoundException.class)
    public ResponseEntity<Object> handleCityNotFoundException(
        CityNotFoundException ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", "City not found");

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
}
```

CityController.java

```java
@RestController
public class CityController {

    private final ICityService cityService;

    public CityController(ICityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping(value = "/cities/{id}")
    public ResponseEntity<City> getCity(@PathVariable Long id) {
        City city = cityService.findById(id);
        return ResponseEntity.ok(city);
    }
}
```
# Клас Optional\<T>

Обект-контейнер, който може да съдържа или да не съдържа стойност, различна от null.

Въведен в Java 8, той се използва за представяне на стойност, която може или не може да бъде налична. С други думи, обектът Optional може или да съдържа ненулева стойност (в този случай се счита за present), или може да не съдържа никаква стойност (в този случай се счита за empty).

Предвид изясненото дотук, обектът Optional може да има едно от следните възможни състояния:

* Present: стойността е налична в обекта Optional и може да бъде достъпна чрез извикване на метода get().
* Empty: обектът Optional не съдържа стойност; не можете да получите достъп до съдържанието му с get().

Optional обикновено се използва като тип на връщане за методи, при които не винаги може да имат резултат за връщане. Например, метод, който търси потребител по ID, може да не намери съвпадение, и в този случай ще върне празен Optional.

Optional може да помогне за намаляване на броя на null pointer exceptions във вашия код. Той не е предназначен като заместител на съществуващи референтни типове, като String или List, а по-скоро като допълнение към системата от типове Java.

### Optional\<T> и хвърляне на изключения

#### Методът orElseThrow() Method

Методът _orElseThrow()_ връща съдържащата се стойност, ако съществува, или хвърля `NoSuchElementException` или предвидено за целта изключение. 

Пример:

```java
public class CourseServiceImpl implements CourseService {

    private CourseRepository courseRepository;
    private ModelMapper mapper;

    public CourseServiceImpl(CourseRepository courseRepository, ModelMapper mapper) {
        this.courseRepository = courseRepository;
        this.mapper = mapper;
    }

@Override
public CourseDto getCourseById(long id) {

    Course course = courseRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));

    return mapToDto(course);
}
```
