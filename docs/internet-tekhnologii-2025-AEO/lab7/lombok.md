---
layout: default
title: Lombok
parent: Laboratory excercise 7
grand_parent: Internet Technologies
nav_order: 4
---


# Lombok

The Lombok project is an annotation-based Java library that allows you to reduce boilerplate code. Lombok offers various annotations aimed at replacing Java code that is boilerplate, repetitive, or tedious to write. Using Lombok, you can avoid writing no-argument constructors, `toString()`, `equals()`, and `hashCode()` methods by adding a few annotations. At compile time, the library injects the bytecode representing the desired boilerplate code into _.class_ files.

Adding a dependency to pom.xml:

```
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.36</version>
    <scope>provided</scope>
</dependency>
```
Adding a plugin for compilation with annotation processor:

```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

### Most commonly used annotations

#### @Getter, @Setter

When a field is annotated with `@Getter` and/or `@Setter`, Lombok automatically generates a default getter and/or setter, respectively. The default method generated will be public unless `AccessLevel` is specified. The possible options for the latter are `PUBLIC, PROTECTED, PACKAGE, and PRIVATE`.

```java
public class Author {
    private int id;

// The library will create methods to access and modify the name field.
    @Getter @Setter	
    private String name;

// The library will create a setter method with the protected access modifier
    @Setter(AccessLevel.PROTECTED)
    private String surname;
}
```

With the `@Getter` and `@Setter` annotations you can also annotate the entire class. In this case the logic will be applied to each field of the class. You can manually disable the generation of getters/setters for a given field by using the `AccessLevel.NONE` option.

```java
@Getter
@Setter
public class Author {
    private int id;
    private String name;
    private String surname;
} 
```

#### @NoArgsConstructor, @RequiredArgsConstructor,@AllArgsConstructor

When a class is annotated with `@NoArgsConstructor`, Lombok automatically generates a parameterless constructor. Similarly, when annotated with `@AllArgsConstructor`, a parameterized constructor will be generated for each field of the class.

`@RequiredArgsConstructor` results in a parameterized constructor for each field that requires special handling - uninitialized final fields, as well as all fields marked as `@NonNull` but uninitialized at the place of their declaration.

Static fields will be ignored by these annotations.

```java
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Author {
    private int id;
    private String name;
    private String surname;
    private final String birthPlace;
}
```

#### @ToString

If a class is annotated with `@ToString`, Lombok will take care of generating the method. By default, a string containing the class name followed by the values ​​of each field, separated by a comma, will be returned. If you set the `includeFieldNames` parameter to true, the name of each field will be placed before its value. By default, all non-static fields will be taken into account when generating the method. Annotate a field with `@ToString.Exclude` if you want Lombok to ignore it.
```java
@ToString(includeFieldNames=true)
public class Author {
    private int id;
    private String name;
    private String surname;
}
```

#### @EqualsAndHashCode

Annotate a class with `@EqualsAndHashCode` and Lombok will automatically create the methods. By default, all non-static, non-transitive fields will be taken into account. You can change which fields are used by annotating them with `@EqualsAndHashCode.Include` or `@EqualsAndHashCode.Exclude`. Alternatively, you can annotate your class with `@EqualsAndHashCode(onlyExplicitlyIncluded=true)` and then specify exactly which fields or methods you want to use by annotating them with `@EqualsAndHashCode.Include`.

```java
@Getter
@Setter
@EqualsAndHashCode
public class Author {
    private int id;
    private String name;
    private String surname;
}
```

#### @NonNull

You can annotate a field, a method parameter, or an entire constructor with `@NonNull`. This way, Lombok will generate null checks for the corresponding component.

```java
public class Author {
    private int id;
    private String name;
    private String surname;

    public Author(
      @NonNull int id,
      @NonNull String name,
      String surname
    ) {
      this.id = id;
      this.name = name;
      this.surname = surname; 
  }
}
```

Without the Lombok library, the code would look like this:

```java
public class Author {
    private int id;
    private String name;
    private String surname;

    public Author(
      int id,
      String name,
      String surname
    ) {
        if (id == null) {
          throw new NullPointerException("id cannot be null");
        }
        this.id = id;
        if (name == null) {
          throw new NullPointerException("name cannot be null");
        }
        this.name = name;
        this.surname = surname; 
  }
}
```

#### @Data

`@Data` is an annotation that combines the annotations `@ToString`, `@Getter`, `@Setter`, `@EqualsAndHashCode` and `@RequiredArgsConstructor`. In this way, it generates all the templates required for a POJO class. Namely: getters for all fields, setters for all non-final fields, implementations of `toString`, `equals` and `hashCode`, including all fields of the class and a constructor for all fields requiring special processing.

```java
@Data
public class Author {
    private final int id;
    private String name;
    private String surname;
}
```
