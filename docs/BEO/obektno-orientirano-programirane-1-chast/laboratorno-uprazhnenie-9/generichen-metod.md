---
layout: default
title: Генеричен метод
parent: Лабораторно упражнение 9
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 2
---

# Генеричен метод

Генеричният метод декларира собствен параметър на типа, който е валиден само в рамките на метода.

Параметърът на типа се записва преди типа на връщания резултат.

```java
public class Printer {

    public static <T> void print(T value) {
        System.out.println(value);
    }
}
```

Изпълнение:

```java
Printer.print("Java");
Printer.print(100);
Printer.print(3.14);
```

При всяко извикване компилаторът определя типа въз основа на подадения аргумент.

## Генеричен интерфейс

Генеричният интерфейс е интерфейс, който декларира параметър на типа. Това позволява различни реализации да работят с различни типове данни.

```java
interface Repository<T> {

    void save(T item);

    T findById(int id);
}
```

Примерна реализация:

```java
public class StudentRepository implements Repository<Student> {

    @Override
    public void save(Student item) {
        System.out.println("Saving student: " + item.getName());
    }

    @Override
    public Student findById(int id) {
        return new Student("Ivan", id);
    }
}
```

Примерен клас:

```java
public class Student {

    private String name;
    private int facultyNumber;

    public Student(String name, int facultyNumber) {
        this.name = name;
        this.facultyNumber = facultyNumber;
    }

    public String getName() {
        return name;
    }

    public int getFacultyNumber() {
        return facultyNumber;
    }
}
```

Генеричните интерфейси се използват често при работа с колекции, хранилища на данни, услуги и други структури, които трябва да работят с различни типове обекти.
