---
title: "Подсказки — упражнение 1"
draft: true
pagefind: false
sidebar:
  hidden: true
---

# Основни операции с текстови низове и обхождане на символи в Java

## Практическа задача
Напишете програма, която чете ред и показва дължината му, текста с главни и малки букви, а след това всеки символ на отделен ред. Проверете и празен ред, и текст с интервал.

## Пълно решение на практическата задача
Работещият пълен пример е даден по-долу и се намира отделно във файла `01-text-basics/src/Main.java`.


```java
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Въведете текст: ");
        String text = scanner.nextLine();

        System.out.println("Дължина: " + text.length());
        System.out.println("Главни букви: " + text.toUpperCase());
        System.out.println("Малки букви: " + text.toLowerCase());
        System.out.println("Символи един по един:");

        for (int i = 0; i < text.length(); i++) {
            System.out.println(text.charAt(i));
        }
    }
}
```

## Решения на задачите

### Задача 1

Преобразуването до главни букви позволява едно сравнение за гласни без значение от регистъра.

```java
int vowels = 0;
String upper = text.toUpperCase();
for (int i = 0; i < upper.length(); i++) {
    char c = upper.charAt(i);
    if (c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U') {
        vowels++;
    }
}
System.out.println("Гласни: " + vowels);
```

### Задача 2

```java
for (int i = text.length() - 1; i >= 0; i--) {
    System.out.println(text.charAt(i));
}
```

### Задача 3

```java
int spaces = 0, tabs = 0;
for (int i = 0; i < text.length(); i++) {
    if (text.charAt(i) == ' ') spaces++;
    else if (text.charAt(i) == '\t') tabs++;
}
System.out.println("Интервали: " + spaces + ", табулации: " + tabs);
```

### Задача 4

```java
if (text.isEmpty()) {
    System.out.println("Празен текст.");
} else {
    System.out.println("Първи: " + text.charAt(0));
    System.out.println("Последен: " + text.charAt(text.length() - 1));
}
```

### Задача 5

```java
int digits = 0;
for (int i = 0; i < text.length(); i++) {
    if (text.charAt(i) >= '0' && text.charAt(i) <= '9') digits++;
}
System.out.println("Цифри: " + digits);
```

### Задача 6

```java
int count = 0;
char wanted = Character.toLowerCase(target);
for (int i = 0; i < text.length(); i++) {
    if (Character.toLowerCase(text.charAt(i)) == wanted) count++;
}
System.out.println("Срещания: " + count);
```

### Задача 7

```java
System.out.println(text.replace(' ', '_'));
```

### Задача 8

```java
boolean containsJava = text.toLowerCase().indexOf("java".toLowerCase()) >= 0;
System.out.println(containsJava ? "Съдържа Java" : "Не съдържа Java");
```

### Задача 9

```java
for (int i = 0; i < text.length(); i += 2) {
    System.out.println(i + ": " + text.charAt(i));
}
```

## Въпроси за проверка
1. Какво връща length()?
2. Кой е индексът на първия символ?
3. Какво връща charAt(i)?
4. Променя ли toUpperCase() оригиналния String?
5. Защо условието на цикъла е i < text.length()?
