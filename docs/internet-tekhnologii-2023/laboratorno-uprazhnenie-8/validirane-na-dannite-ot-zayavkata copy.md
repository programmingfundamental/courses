---
layout: default
title: Персонализиран валидатор
parent: Лабораторно упражнение 8
grand_parent: Интернет технологии
nav_order: 2
---

# Персонализиран валидатор

Персонализиран валидатор в Spring представлява механизъм за дефиниране на собствени правила за валидация на данни, които надхвърлят стандартните анотации като @NotNull, @Size, @Email и други.

Използва се, когато вградените валидатори не покриват конкретна бизнес логика. Пример: „Потребителското име не трябва да съдържа забранени думи“ или „Паролата трябва да съдържа поне една главна буква, едно число и един специален символ“.

# Персонални валидацтори за зависими полета

 Когато трябва да се направи валидация, която включва повече от едно поле в даден клас. Стандартните и персонализираните валидатори, дефинирани върху отделни полета, работят само с една стойност. Но когато има зависимост между полета (напр. „две пароли трябва да съвпадат“), трябва да се използва класова (обектна) валидация.

- Интерфейсът ConstraintValidator<A, T> се използва, за да се дефинира логика, при която:
    - A е типът на анотацията
    - T е типът на стойността, която се валидира
- Анотацията за валидиране трябва да бъде дефинирана като анотиращ интерфейс @interface 
    - С Методи:
        - String message() default "Часът трябва да е по-голям от 00:00";
        - Class<?>[] groups() default {};
        - Class<? extends Payload>[] payload() default {};
    - Анотации:
        - @Documented — указва, че анотацията ще се включва в JavaDoc документацията.
        - @Constraint(validatedBy = Class<?>) — указва кой клас ще извършва реалната валидация. Tова е калс, който трябва да имплементира ConstraintValidator.
        - @Target({ ElementType }) — Анотацията @Target определя къде може да бъде прилагана друга анотация — върху какъв елемент от кода. Използва се елемент от изброимия тип ElementType.
            | Стойност           | Обяснение                                                                 |
            |--------------------|---------------------------------------------------------------------------|
            | `TYPE`             | За класове, интерфейси, еnum-и, записи                                    |
            | `FIELD`            | За полета (включително и `private`)                                       |
            | `METHOD`           | За методи                                                                 |
            | `PARAMETER`        | За параметри на методи                                                    |
            | `CONSTRUCTOR`      | За конструктори                                                           |
            | `LOCAL_VARIABLE`   | За локални променливи (в методи)                                          |
            | `ANNOTATION_TYPE`  | За други анотации (мета-анотации)                                         |
            | `PACKAGE`          | За пакети                                                                 |
            | `TYPE_USE`         | За използване на типове (напр. `@NotNull List<@Email String>`)            |
            | `TYPE_PARAMETER`   | За параметри на обобщения (generic-и)                                     |
            | `MODULE`           | За модули (в Java 9+)                                                     |
            | `RECORD_COMPONENT` | За компоненти на записи (в Java 14+)                                      |
        - @Retention(RetentionPolicy) — анотацията @Retention определя докога се съхранява анотацията — дали ще е налична само в изходния код, при компилация, или и по време на изпълнение. Използва се стойност от изброимия тип RetentionPolicyn.
            | Стойност  | Обяснение                                                                                                                                 |
            |-----------|-------------------------------------------------------------------------------------------------------------------------------------------|
            | `SOURCE`  | Анотацията се премахва при компилация — използва се само в изходния код. Пример: `@Override`                                             |
            | `CLASS`   | Анотацията се запазва в `.class` файла, но не е достъпна по време на изпълнение. Използва се от компилатора.                            |
            | `RUNTIME` | Анотацията се запазва и по време на изпълнение — достъпна е чрез reflection. Използва се при валидация, dependency injection и други.   |


# Стъпки за създаване на персонализиран валидатор

1. Дефиниране на анотация

```java
@Documented
@Constraint(validatedBy = AfterMidnightValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterMidnight {
    String message() default "Часът трябва да е по-голям от 00:00";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
``

2. Имплементиране на ConstraintValidator

```java
public class AfterMidnightValidator implements ConstraintValidator<AfterMidnight, LocalTime> {

    @Override
    public boolean isValid(LocalTime value, ConstraintValidatorContext context) {
        if (value == null) return true; // @NotNull ще го хване
        return value.isAfter(LocalTime.MIDNIGHT); // MIDNIGHT = 00:00
    }
}
``

3. Прилагане върху поле в DTO клас

```java
@NoArgsConstructor
@Getter
public class ReportRequestDto {
    @NotBlank(message = "Съдържанието не може да бъде празно") //Добавено в лабораторно упражнение 8
    @Size(min = 10, max = 2500, message = "Съдържанието трябва да е поне 10 символа и да не е повече от 2500") //Добавено в лабораторно упражнение 8
    private String content;

    @NotNull(message = "Работното време е задължително") //Добавено в лабораторно упражнение 8
    @AfterMidnight //Добавено в лабораторно упражнение 8
    private LocalTime workTime;
}


4. Активиране на валидацията

```java
@PostMapping("/task/{id}")
public ResponseEntity<ReportResponseDto> create(@PathVariable(name = "id") long taskId, @Valid @RequestBody ReportRequestDto dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(new ReportResponseDto());
}
``



