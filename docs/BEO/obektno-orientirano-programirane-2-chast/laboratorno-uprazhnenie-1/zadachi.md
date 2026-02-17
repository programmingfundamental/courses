---
layout: default
title: Задачи
parent: Лабораторно упражнение 1
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 2
---

# Задачи 

### Задача 1

Разгледайте примерите от упражнението. Преобразувайте:

- композиция с множественост в агрегация

- еднопосочна агрегация в композиция.


### Задача 2

Разгледайте посочения по-долу пример. Какъв тип връзка реализира той? Пренапишете кода така, че да реализира другия тип връзка и напишете главна програма, с която да тествате логиката.

```java
public class Student {

    private String fNumber;
    private String fullName;
    private int course;
    private String specialty;

    public Student(String fNumber, String fullName, int course, String specialty) {
        this.fNumber = fNumber;
        this.fullName = fullName;
        this.course = course;
        this.specialty = specialty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return course == student.course && Objects.equals(fNumber, student.fNumber) && Objects.equals(fullName, student.fullName) && Objects.equals(specialty, student.specialty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fNumber, fullName, course, specialty);
    }

    @Override
    public String toString() {
        return "Student{" +
                "fNumber='" + fNumber + '\'' +
                ", fullName='" + fullName + '\'' +
                ", course=" + course +
                ", specialty='" + specialty + '\'' +
                '}';
    }
}

public class University {

    private Set<Student> students = new HashSet<>();

    public void addStudent(Student student) {
        students.add(student);
    }

    public String getStudentInfo() {
        StringBuilder info = new StringBuilder();
        for (Student s : students) {
            info.append(s.toString()).append("\n");
        }
        return info.toString();
    }
}
```

### Задача 3

Напишете програма за онлайн платформа (OnlinePlatform). Платформата има потребители (User), всеки от които има потребителски профил (Profile) и може да създава постове (Post). Потребителите могат също така да коментират вече съществуващи постове (Comment). Какъв тип връзки между обектите User-Profile, User-Post и Post-Comment ще се използват и защо?
