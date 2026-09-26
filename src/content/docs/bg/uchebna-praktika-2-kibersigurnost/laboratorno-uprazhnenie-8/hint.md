---
title: "Подсказки — упражнение 8"
draft: true
pagefind: false
sidebar:
  hidden: true
---

# Шифър на Виженер

## Практическа задача
Създайте encrypt(text, key) и decrypt(text, key), като почистите текста и ключа до A-Z. Проверете с HELLOWORLD и KEY, че декриптирането възстановява първоначалните букви.

## Пълно решение на практическата задача
Работещият пълен пример е даден по-долу и се намира отделно във файла `08-vigenere/src/Main.java`.


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

    public static String transform(String text, String key, boolean decrypt) {
        String cleanText = lettersOnly(text);
        String cleanKey = lettersOnly(key);
        if (cleanKey.length() == 0) return "";

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < cleanText.length(); i++) {
            int textValue = cleanText.charAt(i) - 'A';
            int keyValue = cleanKey.charAt(i % cleanKey.length()) - 'A';
            int resultValue;
            if (decrypt) {
                resultValue = (textValue - keyValue + 26) % 26;
            } else {
                resultValue = (textValue + keyValue) % 26;
            }
            result.append((char) ('A' + resultValue));
        }
        return result.toString();
    }

    public static String encrypt(String text, String key) {
        return transform(text, key, false);
    }

    public static String decrypt(String text, String key) {
        return transform(text, key, true);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Текст (английски букви): ");
        String text = scanner.nextLine();
        System.out.print("Ключ (английски букви): ");
        String key = scanner.nextLine();

        String encrypted = encrypt(text, key);
        System.out.println("Криптиран текст: " + encrypted);
        if (lettersOnly(key).length() == 0) {
            System.out.println("Ключът трябва да съдържа поне една буква A-Z.");
        } else {
            System.out.println("Декриптиран текст: " + decrypt(encrypted, key));
        }
    }
}
```

## Решения на задачите

### Задача 1

При ключа `KEY` редуването за `HELLOWORLD` е `KEYKEYKEYK`.

```java
String text = "HELLOWORLD";
String key = "KEY";
for (int i = 0; i < text.length(); i++) {
    System.out.println(text.charAt(i) + " -> " + key.charAt(i % key.length()));
}
```

### Задача 2

Поставете обработката в цикъл, за да може след encrypt/decrypt менюто да се покаже отново. Методите `encrypt` и `decrypt` са от практическото решение.

```java
boolean running = true;
while (running) {
    System.out.println("1. Encrypt  2. Decrypt  0. Exit");
    String choice = scanner.nextLine();
    if (choice.equals("0")) {
        running = false;
    } else if (choice.equals("1") || choice.equals("2")) {
        System.out.print("Текст: ");
        String text = scanner.nextLine();
        System.out.print("Ключ: ");
        String key = scanner.nextLine();
        if (choice.equals("1")) System.out.println(encrypt(text, key));
        else System.out.println(decrypt(text, key));
    } else {
        System.out.println("Невалиден избор.");
    }
}
```

### Задача 3

```java
System.out.println(encrypt("ATTACKATDAWN", "LEMON")); // LXFOPVEFRNHR
```

### Задача 4

```java
System.out.println(decrypt("LXFOPVEFRNHR", "LEMON")); // ATTACKATDAWN
```

### Задача 5

```java
String text = lettersOnly("HELLOWORLD");
String key = lettersOnly("KEY");
for (int i = 0; i < text.length(); i++) {
    System.out.println(text.charAt(i) + " -> " + key.charAt(i % key.length()));
}
```

### Задача 6

```java
if (lettersOnly(key).isEmpty()) {
    System.out.println("Ключът трябва да съдържа поне една буква A-Z.");
} else {
    System.out.println(encrypt(text, key));
}
```

### Задача 7

Функцията `lettersOnly` преобразува малките английски букви в главни, затова резултатът е с главни букви.

```java
System.out.println(encrypt("Attack at dawn!", "lemon"));
```

### Задача 8

```java
String cipher = encrypt(text, key);
System.out.println(decrypt(cipher, key).equals(lettersOnly(text)));
```

### Задача 9

```java
String cleanText = lettersOnly("HELLO");
String cleanKey = lettersOnly("KEY");
for (int i = 0; i < cleanText.length(); i++) {
    int textValue = cleanText.charAt(i) - 'A';
    int keyValue = cleanKey.charAt(i % cleanKey.length()) - 'A';
    System.out.println(textValue + " + " + keyValue);
}
```

## Въпроси за проверка
1. Защо ключът се повтаря?
2. Как се избира текущата позиция в ключа?
3. Какво означават A=0 и Z=25?
4. Защо декриптирането включва +26?
5. Какво правим, ако ключът няма букви A-Z?
