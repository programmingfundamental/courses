---
title: "Подсказки — упражнение 7"
draft: true
pagefind: false
sidebar:
  hidden: true
---

# Шифър на Цезар

## Практическа задача
Реализирайте lettersOnly(text), encrypt(text, key) и decrypt(text, key). Покажете шифротекста и след това декриптирайте същия текст. Програмата приема английски букви A-Z и връща само букви.

## Пълно решение на практическата задача
Работещият пълен пример е даден по-долу и се намира отделно във файла `07-caesar/src/Main.java`.


```java
import java.util.Scanner;

public class Main {
    public static String lettersOnly(String text) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= 'a' && c <= 'z') c = (char) (c - 'a' + 'A');
            if (c >= 'A' && c <= 'Z') result.append(c);
        }
        return result.toString();
    }

    public static String encrypt(String text, int key) {
        String clean = lettersOnly(text);
        StringBuilder result = new StringBuilder();
        int shift = ((key % 26) + 26) % 26;
        for (int i = 0; i < clean.length(); i++) {
            char c = clean.charAt(i);
            result.append((char) ('A' + (c - 'A' + shift) % 26));
        }
        return result.toString();
    }

    public static String decrypt(String text, int key) {
        return encrypt(text, -key);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Текст (английски букви): ");
        String text = scanner.nextLine();
        System.out.print("Ключ (цяло число): ");
        int key = scanner.nextInt();

        String encrypted = encrypt(text, key);
        System.out.println("Криптиран текст: " + encrypted);
        System.out.println("Декриптиран текст: " + decrypt(encrypted, key));
    }
}
```

## Решения на задачите

### Задача 1

Ключът се свежда до остатък при деление на 26. Затова 0 и 26 означават отместване с нула, а 29 е еквивалентен на 3.

```java
System.out.println("Ключ 0:  " + encrypt(text, 0));
System.out.println("Ключ 26: " + encrypt(text, 26));
System.out.println("Ключ 29: " + encrypt(text, 29));
// Първите два резултата са еднакви; ключ 29 съвпада с ключ 3.
```

### Задача 2

Преобразувайте само буквите и копирайте останалите символи без промяна.

```java
public static String transformPreservingText(String text, int key, boolean decrypt) {
    int shift = ((key % 26) + 26) % 26;
    if (decrypt) shift = (26 - shift) % 26;
    StringBuilder result = new StringBuilder();

    for (int i = 0; i < text.length(); i++) {
        char c = text.charAt(i);
        if (c >= 'A' && c <= 'Z') {
            result.append((char) ('A' + (c - 'A' + shift) % 26));
        } else if (c >= 'a' && c <= 'z') {
            result.append((char) ('a' + (c - 'a' + shift) % 26));
        } else {
            result.append(c);
        }
    }
    return result.toString();
}
```

## Въпроси за проверка
1. На коя числова стойност съответства A?
2. Защо използваме остатък при деление на 26?
3. Каква е разликата между encrypt и decrypt?
4. Какво става с интервалите в дадената реализация?
5. Защо нормализираме ключа?
