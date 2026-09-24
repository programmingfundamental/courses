---
title: Tasks
sidebar:
  order: 100
  label: Tasks
taskPage: true
---

## Independent Study Exercises

## Task 1

Define a generic class `Box<T>` with a `value` field of type `T`, `setValue(T value)`, `getValue()`, and `isEmpty()`, which reports whether the value is `null`.

Create `Box<String>`, `Box<Integer>`, and `Box<Double>` objects. Demonstrate that each object works with the specific type supplied when it was created.

## Task 2

Define a generic class `Pair<K, V>` with `key` of type `K`, `value` of type `V`, a constructor, accessors, and `getDescription()`.

Create pairs for a student's name and grade, a product code and price, and a city name and population. Use `Pair<String, Integer>`, `Pair<String, Double>`, and `Pair<String, Long>`. Make the fields `private final`. Add an invalid pair as a comment and explain why the compiler rejects the mismatched type.

Represent the same data using `record KeyValue<K, V>(K key, V value)`. Compare the accessors and `equals()` results for two distinct objects with the same data. Also create `record StudentGrade(String studentName, int grade)` and explain when this named type is clearer than a generic pair.

## Task 3

Define a generic method:

```java
public static <T> void printArray(T[] array)
```

Traverse the array and print all its elements. Test it with arrays of `String`, `Integer`, and `Double`.

## Task 4

Define a generic interface `Repository<T>` with `save(T item)`, `findById(int id)`, and `size()`.

Define `Student` with a student ID, and `StudentRepository` implementing `Repository<Student>`. Use a fixed-capacity `Student[]` array and a record count. If the array is full, `save` should signal the condition using an appropriate exception already covered in class; if an ID is not found, `findById` should return `null`. Using `List<T>` is introduced after Lab Exercise 9. Check an empty repository, successful saving and lookup, a missing ID, and a full repository.

## Task 5

Demonstrate why raw types should not be used. Create an example using raw `Box`, store a value of one type, and then incorrectly cast it to another type. Implement the same example using `Box<String>` or `Box<Integer>` so the compiler catches the error.
