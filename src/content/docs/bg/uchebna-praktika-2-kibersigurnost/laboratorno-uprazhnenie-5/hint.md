---
title: "Подсказки — упражнение 5"
draft: true
pagefind: false
sidebar:
  hidden: true
---

# Обръщане на текст и проверка за палиндром

## Практическа задача
Реализирайте reverse(text) и isPalindrome(text). Методът за палиндром трябва да игнорира регистъра, интервалите и пунктуацията, но да сравнява английските букви и цифри.

## Пълно решение на практическата задача
Работещият пълен пример е даден по-долу и се намира отделно във файла `05-reverse-palindrome/src/Main.java`.


```java
import java.util.Scanner;

public class Main {
    public static String reverse(String text) {
        StringBuilder result = new StringBuilder();
        for (int i = text.length() - 1; i >= 0; i--) {
            result.append(text.charAt(i));
        }
        return result.toString();
    }

    public static boolean isLetterOrDigit(char c) {
        return (c >= 'A' && c <= 'Z')
                || (c >= 'a' && c <= 'z')
                || (c >= '0' && c <= '9');
    }

    public static char toUpperAscii(char c) {
        if (c >= 'a' && c <= 'z') {
            return (char) (c - 'a' + 'A');
        }
        return c;
    }

    public static boolean isPalindrome(String text) {
        int left = 0;
        int right = text.length() - 1;

        while (left < right) {
            while (left < right && !isLetterOrDigit(text.charAt(left))) left++;
            while (left < right && !isLetterOrDigit(text.charAt(right))) right--;

            char first = toUpperAscii(text.charAt(left));
            char last = toUpperAscii(text.charAt(right));
            if (first != last) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Въведете текст: ");
        String text = scanner.nextLine();

        System.out.println("Обърнат текст: " + reverse(text));
        System.out.println(isPalindrome(text) ? "Палиндром" : "Не е палиндром");
    }
}
```

## Решения на задачите

### Задача 1

Числото се чете като текст, за да се сравнят символите от двата края.

```java
public static boolean isNumericPalindrome(String value) {
    int left = 0;
    int right = value.length() - 1;
    while (left < right) {
        if (value.charAt(left) != value.charAt(right)) return false;
        left++;
        right--;
    }
    return true;
}

System.out.println(isNumericPalindrome("12321")); // true
```

### Задача 2

Нормализирайте първо текста, после сравнете резултата с обърнатия му вариант.

```java
public static boolean normalizedPalindrome(String text) {
    String normalized = normalizeText(text);
    return normalized.equals(reverse(normalized));
}
```

## Въпроси за проверка
1. Какъв индекс е последният символ?
2. Защо StringBuilder е удобен при обръщане?
3. Кои символи пропускаме в проверката?
4. Как се движат left и right?
5. Какво означава, ако се открие различаваща се двойка?
