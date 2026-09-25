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

## Въпроси за проверка
1. Защо ключът се повтаря?
2. Как се избира текущата позиция в ключа?
3. Какво означават A=0 и Z=25?
4. Защо декриптирането включва +26?
5. Какво правим, ако ключът няма букви A-Z?
