---
title: Generic Classes and Methods
sidebar:
  order: 1
---

# Generic Classes and Methods

Here, “generic” means parameterized by types, not an IntelliJ IDEA project template. Java generics are not the C++ `template` mechanism; they are generally implemented through type erasure, without a separate class version for every type argument.

Generics let classes, interfaces, and methods work with a type supplied when they are used. The same code can then serve different types while retaining type safety.

```java
class Box<T> {
    private T value;
    public void setValue(T value) { this.value = value; }
    public T getValue() { return value; }
}
```

`T` is a type parameter. It is replaced by a concrete type when the object is used.

```java
Box<String> textBox = new Box<>();
textBox.setValue("Java");
String value = textBox.getValue();
```

Here, `T` is `String`.

## Type parameters

A type parameter is a name that temporarily represents a concrete type. It is written in angle brackets after a class, interface, or method name.

```java
class Box<T> {
    private T value;
}
```

`T` is not a concrete class; it is a parameter that is supplied when using `Box`.

```java
Box<String> textBox = new Box<>();
Box<Integer> numberBox = new Box<>();
```

In `Box<String>`, `T` is treated as `String`; in `Box<Integer>`, it is treated as `Integer`.

| Name | Meaning | Common use |
| --- | --- | --- |
| `T` | Type | A general type |
| `E` | Element | Elements in collections such as `List` and `Set` |
| `K` | Key | Keys in a `Map` |
| `V` | Value | Values in a `Map` |
| `R` | Result | Result type of an operation or function |
| `U` | A second free type | When `T` is already used |

These names are conventions. They do not change program behavior, but make generic declarations easier to recognize.

## Why generics are needed

Without generics, values often have to be stored as `Object`. This accepts any type and defers errors until runtime.

```java
class Box {
    private Object value;
    public void setValue(Object value) { this.value = value; }
    public Object getValue() { return value; }
}
```

An explicit cast is then needed:

```java
Box box = new Box();
box.setValue("Java");
String value = (String) box.getValue();
```

Generics remove that cast and let the compiler check types.

## Type safety

Type safety means the compiler checks that values of the expected type are used. With generics, an incompatible value is rejected at compile time rather than causing an error later at runtime.

```java
Box<String> box = new Box<>();
box.setValue("Java");
// box.setValue(10); // does not compile
```

Because `box` is a `Box<String>`, it accepts a `String`, not an `int` or `Integer`. Type safety reduces casts and runtime errors.

## A generic class with more than one parameter

```java
final class Pair<K, V> {
    private final K key;
    private final V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
    public K getKey() { return key; }
    public V getValue() { return value; }
}
```

`Pair<K, V>` has two type parameters. `K` can represent a key type and `V` a value type.

```java
Pair<String, Integer> grade = new Pair<>("Ivan", 6);
```

The key and value types are independent. For example, `Pair<Integer, String>` could store a student ID and name, and `Pair<String, Double>` a product code and price. `grade.getKey()` has type `String` and `grade.getValue()` has type `Integer`; no cast is needed.

## Common key–value pair types

A generic declaration can contain more than one type parameter. The following names and roles are common. Only `K, V` specifically suggests a key and value; the other pairs can describe other roles.

| Parameters | Meaning | Common use |
| --- | --- | --- |
| `K, V` | Key, Value | `Map`, `HashMap`, `TreeMap` |
| `T, U` | Two arbitrary types | Pairs, comparisons, helper classes |
| `T, R` | Type, Result | Functions and transformations such as `Function` |
| `E, T` | Element, Type | Collections with an additional type |
| `N, T` | Number, Type | Helpers for numeric operations and mathematics |
| `T, S` | Type, second type | Generics with two free types |

The difference between `T, U` and `T, S` is semantic: it reflects the meaning a programmer gives the names, not Java behavior.

- `T` and `U` can represent two independent arbitrary types. `U` means the second type parameter here; it has no fixed Java meaning such as “Unknown” or “Unused”.
- `T` and `S` can also represent two arbitrary types. `S` may suggest “second” or “secondary”, but that is a naming choice, not a rule that makes `U` unsuitable.

Names do not impose constraints: `S` is not automatically a subtype of `T`, and `N` is not automatically numeric. Constraints must be stated, for example with `S extends T` or `N extends Number`.

A key–value pair can be represented in several ways:

| Representation | Example | Suitable when |
| --- | --- | --- |
| Generic class | `Pair<K, V>` | Custom behavior and control over mutability are needed |
| Generic record | `KeyValue<K, V>` | A compact value with final components and generated equality is suitable |
| Named task-specific type | `StudentGrade` | Descriptive field names are clearer than `key` and `value` |
| Map entry | `Map.Entry<K, V>` | Traversing a `Map`; covered in Lab Exercise 10 |

```java
record KeyValue<K, V>(K key, V value) {}
record StudentGrade(String studentName, int grade) {}
```

```java
KeyValue<String, Integer> result = new KeyValue<>("Ivan", 6);
String name = result.key();
int gradeValue = result.value();
System.out.println(result.equals(new KeyValue<>("Ivan", 6))); // true

StudentGrade studentGrade = new StudentGrade("Ivan", 6);
System.out.println(studentGrade.studentName());
```

`Pair` and `KeyValue` are types defined in these examples, not a standard `Pair` class in `java.util`. A pair stores two values; by itself it does not provide key uniqueness or lookup like a `Map`.

## Good practices for generic classes

- Specify type arguments, for example `Pair<String, Integer>`, instead of using raw `Pair`.
- Use `K` and `V` for a key and value, `T` for a general type, and `E` for an element. Prefer a named type such as `StudentGrade` when the roles are specific.
- Use `<>` with a constructor when the compiler can infer the types.
- Use reference wrapper types such as `Integer`, not `int`, as type arguments.
- Do not bypass compiler checks with `Object` and unnecessary casts. Incompatible types should be rejected during compilation.
- For an immutable pair, use final fields or a `record`. Consider defensive copying if a component is mutable.
- Decide whether `null` is allowed. The `gradeValue` example assumes a non-null `Integer` because unboxing `null` is invalid.

```java
Pair<String, Integer> count = new Pair<>("books", 3);
// Pair<String, Integer> wrong = new Pair<>("books", "three"); // does not compile
```

See [Generic Types — Java Tutorials](https://docs.oracle.com/javase/tutorial/java/generics/types.html) for the fundamentals.

## Generic methods

A generic method declares its own type parameter before the return type.

```java
class Printer {
    public static <T> void print(T value) {
        System.out.println(value);
    }
}
```

It can be called with different types:

```java
Printer.print("Java");
Printer.print(100);
Printer.print(12.5);
```

## Generic interfaces

An interface can also have a type parameter.

```java
interface Repository<T> {
    void save(T item);
    T findById(int id);
}
```

An implementing class supplies a concrete type or remains generic.

```java
class StudentRepository implements Repository<Student> {
    @Override
    public void save(Student item) {}
    @Override
    public Student findById(int id) {
        return null;
    }
}
```

## Generics and reference types

Generics work with reference types. Primitive types such as `int`, `double`, and `boolean` cannot be used directly.

```java
// Box<int> box = new Box<>(); // does not compile
Box<Integer> box = new Box<>();
```

Use wrapper classes such as `Integer`, `Double`, and `Boolean` for primitive values.

## Raw types

A raw type is a generic class used without a type parameter.

```java
Box box = new Box();
```

This removes some compiler checks and should not be used in new code. Use the parameterized form instead:

```java
Box<String> box = new Box<>();
```

## Benefits

Generics provide type safety, reduce explicit casting, and make classes, interfaces, and methods reusable with different types.
