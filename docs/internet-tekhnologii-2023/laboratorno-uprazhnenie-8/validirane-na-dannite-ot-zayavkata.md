---
layout: default
title: Валидиране на данните от заявката
parent: Лабораторно упражнение 8
grand_parent: Интернет технологии
nav_order: 2
---

# Валидиране на данните от заявката

Валидирането на данни в тялото на заявката трябва да се обработва от всеки REST API, за да се гарантира, че клиентите няма да изпращат дезинформация към сървъра, което може да наруши целостта на данните на приложението.

Jakarta Bean Validation е спецификация на Java, която ви позволява да задавате ограничения върху обектни модели чрез анотации като @NotNull, @Null, @Min, @Max и др. Предоставя API за валидиране на обекти и обектни графики, като можете да напишете и потребителски ограничения, ако вградените такива не отговарят на вашите нужди.

Hibernate Validator е референтна имплементация на спецификацията за валидиране на Java Bean, като предоставя допълнителни вградени ограничения, които ви дават повече опции за валидиране на обекти.

За да използвате Jakarta Bean validation с Hibernate Validator, трябва да добавите следната зависимост към вашия проект Maven. Това ще добави пакетите  jakarta.validation-api-VERSION.jar и hibernate-validator-VERSION.jar към проекта.

```xml
  implementation 'org.springframework.boot:spring-boot-starter-validation'
```

### Основи на валидирането на Bean

Jakarta Bean Validation работи като дефинира ограничения за полетата на клас с помощта на определени[ анотации](https://docs.jboss.org/hibernate/beanvalidation/spec/2.0/api/javax/validation/constraints/package-summary.html).

#### Общи анотации за валидиране

Някои от най-често срещаните анотации за валидиране са:

* `@NotNull`**:** указва, че полето не трябва да бъде null.
* `@NotEmpty`**:** указва, че полето на списък не трябва да бъде празно.
* `@NotBlank`**:** указва, че полето на низ не трябва да е празен низ (т.е. трябва да има поне един знак).
* `@Min` **и**  `@Max`**:** указва, че едно числово поле е валидно, само когато стойността му е над или под определена стойност.
* `@Size`: указва, че размерът на елемента (CharSequence, Collection, Map или Array) трябва да бъде в зададени граници 
* `@Pattern`**:** указва, че полето на низ е валидно, само когато съответства на определен регулярен израз.
* `@Email`**:** указва, че полето на низ трябва да е валиден имейл адрес.

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

#### Валидиране на тялото на заявката

В POST и PUT заявките е обичайно да се предава JSON в тялото на заявката. Spring автоматично картографира входящия JSON към Java обект. Сега искаме да проверим дали входящият Java обект отговаря на нашите изисквания.

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

Имаме int поле, което трябва да има стойност между 1 и 10 включително, както е дефинирано от анотациите `@Min` и `@Max`. Имаме и String поле, което трябва да съдържа IP адрес, както е дефинирано от регулярния израз в анотацията `@Pattern`.

За да потвърдим тялото на входяща HTTP заявка, ние анотираме тялото на заявката с анотацията `@Valid` в REST контролера:

```java
@RestController
class ValidateRequestBodyController {

  @PostMapping("/validateBody")
  ResponseEntity<String> validateBody(@Valid @RequestBody Input input) {
    return ResponseEntity.ok("valid");
  }
}
```

Ние просто добавихме `@Valid` анотацията към параметъра `Input input` , който е анотиран и с `@RequestBody,` за да отбележи, че трябва да бъде прочетен от тялото на заявката. Правейки това, ние казваме на Spring да предаде обекта на валидатор, преди да направи нещо друго.

Ако проверката е неуспешна, тя ще задейства `MethodArgumentNotValidException`. По подразбиране Spring ще преведе това изключение в HTTP статус 400 (Bad request).

**Използвайте `@Valid и при` сложни типове**

Ако `Input` класът съдържа поле с друг сложен тип, което трябва да бъде валидирано, това поле също трябва да бъде анотирано с `@Valid`.

#### Валидиране на path variable и query parameter

Валидирането на темплейтните променливи и query параметрите на заявката работи малко по-различно.

В този случай не валидираме сложни обекти на Java, тъй като променливите на пътя и параметрите на заявката обикновено са примитивни типове като `int;` техните wrapper обекти като `Integer` или пък`String`.

Вместо да анотираме поле на клас като по-горе, ние добавяме анотация на ограничение (в този случай `@Min`) директно към параметъра на метода в контролера:

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

Имайте предвид, че трябва да добавим анотация `@Validated` към контролера на ниво клас, за да кажем на Spring да оцени анотациите на ограниченията върху параметрите на метода.

За разлика от валидирането на тялото на заявката, неуспешното валидиране ще задейства `ConstraintViolationException` вместо `MethodArgumentNotValidException`. Spring не регистрира манипулатор на изключения по подразбиране за това изключение, така че по подразбиране ще предизвика отговор с HTTP статус 500 (вътрешна сървърна грешка).

Ако вместо това искаме да върнем HTTP статус 400 (което има смисъл, тъй като клиентът предоставя невалиден параметър, което го прави bad request), можем да добавим персонализиран манипулатор на изключения.

#### Добавяне на метод за обработване на изключения

```java
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
 
***
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
              HttpHeaders headers, HttpStatusCode status,WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>(); //Лоша практика
        body.put("timestamp", LocalDate.now());
        body.put("status", status.value());

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());

        body.put("errors", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
```
