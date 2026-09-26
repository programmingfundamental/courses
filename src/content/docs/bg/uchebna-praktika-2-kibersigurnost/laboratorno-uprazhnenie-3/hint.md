---
title: "Подсказки — упражнение 3"
draft: true
pagefind: false
sidebar:
  hidden: true
---

# Броене на символи, думи и срещания в текст

## Практическа задача
Създайте програма за статистика на един ред. Тя трябва да показва общия брой символи, броя ASCII букви A-Z/a-z, цифри, обикновени интервали, думи, срещания на избран символ и срещания на избран подниз.

## Пълно решение на практическата задача
Работещият пълен пример е даден по-долу и се намира отделно във файла `03-text-counting/src/Main.java`.


```java
import java.util.Scanner;

public class Main {
    public static boolean isLetter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }

    public static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    public static boolean isWhitespace(char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }

    public static int countMatches(String text, String pattern) {
        if (pattern.length() == 0) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i <= text.length() - pattern.length(); i++) {
            int j = 0;
            while (j < pattern.length()
                    && text.charAt(i + j) == pattern.charAt(j)) {
                j++;
            }
            if (j == pattern.length()) {
                count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Въведете текст: ");
        String text = scanner.nextLine();
        System.out.print("Символ за броене: ");
        String symbolInput = scanner.nextLine();
        System.out.print("Подниз за броене: ");
        String pattern = scanner.nextLine();

        int letters = 0;
        int digits = 0;
        int spaces = 0;
        int words = 0;
        boolean inWord = false;
        int selected = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (isLetter(c)) letters++;
            if (isDigit(c)) digits++;
            if (c == ' ') spaces++;

            if (isWhitespace(c)) {
                inWord = false;
            } else if (!inWord) {
                words++;
                inWord = true;
            }

            if (symbolInput.length() > 0 && c == symbolInput.charAt(0)) {
                selected++;
            }
        }

        System.out.println("Символи общо: " + text.length());
        System.out.println("Букви A-Z: " + letters);
        System.out.println("Цифри: " + digits);
        System.out.println("Интервали: " + spaces);
        System.out.println("Думи: " + words);
        System.out.println("Срещания на избрания символ: " + selected);
        System.out.println("Срещания на подниза (с припокриване): "
                + countMatches(text, pattern));
    }
}
```

## Решения на задачите

### Задача 1

Добавете табулацията към разделителите, преди да обработите започването на нова дума.

```java
public static boolean isWhitespace(char c) {
    return c == ' ' || c == '\t' || c == '\n' || c == '\r';
}
```

### Задача 2

```java
int uppercase = 0;
int lowercase = 0;
for (int i = 0; i < text.length(); i++) {
    char c = text.charAt(i);
    if (c >= 'A' && c <= 'Z') uppercase++;
    else if (c >= 'a' && c <= 'z') lowercase++;
}
System.out.println("Главни ASCII букви: " + uppercase);
System.out.println("Малки ASCII букви: " + lowercase);
```

## Въпроси за проверка
1. Каква е разликата между символи и букви?
2. Защо е нужна променливата inWord?
3. Кога броят думи се увеличава?
4. Как броим припокриващи се срещания?
5. Защо празният шаблон се изключва?
