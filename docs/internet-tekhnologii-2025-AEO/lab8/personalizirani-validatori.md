---
layout: default
title: JavaScript Object Notation
parent: Laboratory excercise 8
grand_parent: Internet Technologies
nav_order: 2
---


# Personalized validator

A custom validator in Spring is a mechanism for defining your own data validation rules that go beyond standard annotations such as @NotNull, @Size, @Email, and others.

It is used when the built-in validators do not cover specific business logic. Example: "Username must not contain prohibited words" or "Password must contain at least one uppercase letter, one number, and one special character."

# Custom validators for dependent fields

When validation needs to be done that involves more than one field in a given class. Standard and custom validators defined on individual fields only work with a single value. But when there is a dependency between fields (e.g. "two passwords must match"), class (object) validation should be used.

- The ConstraintValidator<A, T> interface is used to define logic where:
    - A is the annotation type
    - T is the type of the value being validated
- The validation annotation must be defined as annotating an interface @interface 
    - With methods:
        - String message() default "Time must be greater than 00:00";
        - Class<?>[] groups() default {};
        - Class<? extends Payload>[] payload() default {};
    - Annotations:
        - @Documented — specifies that the annotation will be included in the JavaDoc documentation.
        - @Constraint(validatedBy = Class<?>) — specifies which class will perform the actual validation. This is the class that must implement ConstraintValidator.
        - @Target({ ElementType }) — The @Target annotation defines where another annotation can be applied — on what element of the code. An element of the enumerable type ElementType is used.


| Value| Explanation|
|---|---|
| `TYPE`| For classes, interfaces, enums, records|
| `FIELD`| For fields (including `private`)|
| `METHOD`| For methods|
| `PARAMETER`| For method parameters|
| `CONSTRUCTOR`| For constructors|
| `LOCAL_VARIABLE`| For local variables (in methods)|
| `ANNOTATION_TYPE`| For other annotations (meta-annotations)|
| `PACKAGE`| For packages|
| `TYPE_USE`| To use types (e.g. `@NotNull List<@Email String>`)|
| `TYPE_PARAMETER`| For generic parameters|
| `MODULE`| For modules (in Java 9+)|
| `RECORD_COMPONENT`| For record components (in Java 14+)|

        - @Retention(RetentionPolicy) — The @Retention annotation defines how long the annotation is stored — whether it will be available only in the source code, at compile time, or also at runtime. A value of the enumerable type RetentionPolicy is used.

| Value| Explanation|
|---|---|
| `SOURCE`| The annotation is removed at compile time — it is only used in the source code. Example: `@Override`|
| `CLASS`| The annotation is stored in the `.class` file, but is not accessible at runtime. It is used by the compiler.|
| `RUNTIME`| The annotation is also preserved at runtime — it is accessible via reflection. It is used in validation, dependency injection, and more.|


# Steps to create a custom validator

1. Defining an annotation

```java
@Documented
@Constraint(validatedBy = AfterMidnightValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterMidnight {
    String message() default "The hour must be greater than 00:00";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

2. Implementing ConstraintValidator

```java
public class AfterMidnightValidator implements ConstraintValidator<AfterMidnight, LocalTime> {

    @Override
    public boolean isValid(LocalTime value, ConstraintValidatorContext context) {
        if (value == null) return true; // @NotNull will catch it
        return value.isAfter(LocalTime.MIDNIGHT); // MIDNIGHT = 00:00
    }
}
```

3. Applying to a field in a DTO class

```java
@NoArgsConstructor
@Getter
public class ReportRequestDto {
    @NotBlank(message = "Content cannot be empty.") //Added in lab exercise 8
    @Size(min = 10, max = 2500, message = "Content must be at least 10 characters and no more than 2500") //Added in lab exercise 8
    private String content;

    @NotNull(message = "Working hours are mandatory") //Added in lab exercise 8
    @AfterMidnight //Added in lab exercise 8
    private LocalTime workTime;
}
```

4. Enable validation

```java
@PostMapping("/task/{id}")
public ResponseEntity<ReportResponseDto> create(@PathVariable(name = "id") long taskId, @Valid @RequestBody ReportRequestDto dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(new ReportResponseDto());
}
```



