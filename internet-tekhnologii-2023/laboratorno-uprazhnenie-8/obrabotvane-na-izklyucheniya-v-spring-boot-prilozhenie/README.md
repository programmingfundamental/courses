# Обработване на изключения в Spring Boot приложение

Spring framework предлага на разработчиците няколко опции за обработка на изключения в техните приложения. Един от тях е global exception handler с анотации `@ControllerAdvice` и `@ExceptionHandler`.&#x20;

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

```
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

ControllerAdvisor.java

```
@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

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
