---
title: "Подсказки — упражнение 12"
draft: true
pagefind: false
sidebar:
  hidden: true
---

# Мини проект „Text & Crypto Toolkit“

## Практическа задача
Създайте конзолно приложение с меню: 1 анализ на текст, 2 нормализиране, 3 обръщане, 4 проверка за палиндром, 5 честотен анализ, 6/7 Caesar encrypt/decrypt, 8/9 Vigenere encrypt/decrypt, 10 XOR, 11 Caesar brute-force и 0 Exit. Всеки алгоритъм трябва да е отделен метод. Използвайте вариантите от предходните решения.

## Пълно решение на практическата задача
Работещият пълен пример е даден по-долу и се намира отделно във файла `12-text-crypto-toolkit/src/Main.java`.


```java
import java.util.Scanner;

public class Main {
    public static String normalizeText(String text) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= 'a' && c <= 'z') c = (char) (c - 'a' + 'A');
            if (c >= 'A' && c <= 'Z') result.append(c);
        }
        return result.toString();
    }

    public static void analyzeText(String text) {
        int letters = 0, digits = 0, spaces = 0, words = 0;
        boolean inWord = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) letters++;
            if (c >= '0' && c <= '9') digits++;
            if (c == ' ') spaces++;
            if (c == ' ' || c == '\t') {
                inWord = false;
            } else if (!inWord) {
                words++;
                inWord = true;
            }
        }
        System.out.println("Символи: " + text.length() + ", букви: " + letters
                + ", цифри: " + digits + ", интервали: " + spaces
                + ", думи: " + words);
    }

    public static String reverseText(String text) {
        StringBuilder result = new StringBuilder();
        for (int i = text.length() - 1; i >= 0; i--) result.append(text.charAt(i));
        return result.toString();
    }

    public static boolean isLetterOrDigit(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
                || (c >= '0' && c <= '9');
    }

    public static char upperAscii(char c) {
        return (c >= 'a' && c <= 'z') ? (char) (c - 'a' + 'A') : c;
    }

    public static boolean isPalindrome(String text) {
        int left = 0, right = text.length() - 1;
        while (left < right) {
            while (left < right && !isLetterOrDigit(text.charAt(left))) left++;
            while (left < right && !isLetterOrDigit(text.charAt(right))) right--;
            if (upperAscii(text.charAt(left)) != upperAscii(text.charAt(right))) return false;
            left++;
            right--;
        }
        return true;
    }

    public static int[] frequencyAnalysis(String text) {
        int[] frequency = new int[26];
        String clean = normalizeText(text);
        for (int i = 0; i < clean.length(); i++) frequency[clean.charAt(i) - 'A']++;
        return frequency;
    }

    public static void printFrequency(String text) {
        int[] frequency = frequencyAnalysis(text);
        for (int i = 0; i < frequency.length; i++) {
            System.out.println((char) ('A' + i) + " -> " + frequency[i]);
        }
    }

    public static String caesarEncrypt(String text, int key) {
        String clean = normalizeText(text);
        StringBuilder result = new StringBuilder();
        int shift = ((key % 26) + 26) % 26;
        for (int i = 0; i < clean.length(); i++) {
            result.append((char) ('A' + (clean.charAt(i) - 'A' + shift) % 26));
        }
        return result.toString();
    }

    public static String caesarDecrypt(String text, int key) {
        return caesarEncrypt(text, -key);
    }

    public static String vigenereTransform(String text, String key, boolean decrypt) {
        String cleanText = normalizeText(text);
        String cleanKey = normalizeText(key);
        if (cleanKey.length() == 0) return "";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < cleanText.length(); i++) {
            int textValue = cleanText.charAt(i) - 'A';
            int keyValue = cleanKey.charAt(i % cleanKey.length()) - 'A';
            int value = decrypt
                    ? (textValue - keyValue + 26) % 26
                    : (textValue + keyValue) % 26;
            result.append((char) ('A' + value));
        }
        return result.toString();
    }

    public static String vigenereEncrypt(String text, String key) {
        return vigenereTransform(text, key, false);
    }

    public static String vigenereDecrypt(String text, String key) {
        return vigenereTransform(text, key, true);
    }

    public static void xorTransform(String text, int key) {
        System.out.print("XOR стойности: ");
        for (int i = 0; i < text.length(); i++) {
            if (i > 0) System.out.print(" ");
            System.out.print(text.charAt(i) ^ key);
        }
        System.out.println();
        System.out.println("Прилагане на същия XOR ключ отново възстановява текста.");
    }

    public static void caesarBruteForce(String text) {
        for (int key = 1; key <= 25; key++) {
            System.out.println("Key " + key + " -> " + caesarDecrypt(text, key));
        }
    }

    public static int readInteger(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) {
                int value = scanner.nextInt();
                scanner.nextLine();
                return value;
            }
            scanner.nextLine();
            System.out.println("Моля, въведете цяло число.");
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("=========================");
            System.out.println(" TEXT & CRYPTO TOOLKIT");
            System.out.println("=========================");
            System.out.println("1. Анализ на текст");
            System.out.println("2. Нормализиране");
            System.out.println("3. Обръщане на текст");
            System.out.println("4. Проверка за палиндром");
            System.out.println("5. Честотен анализ");
            System.out.println("6. Caesar Encrypt");
            System.out.println("7. Caesar Decrypt");
            System.out.println("8. Vigenere Encrypt");
            System.out.println("9. Vigenere Decrypt");
            System.out.println("10. XOR Transform");
            System.out.println("11. Caesar Brute Force");
            System.out.println("0. Exit");
            System.out.print("Избор: ");
            String choice = scanner.nextLine();

            if (choice.equals("0")) {
                running = false;
            } else if (choice.equals("1")) {
                System.out.print("Текст: ");
                analyzeText(scanner.nextLine());
            } else if (choice.equals("2")) {
                System.out.print("Текст: ");
                System.out.println(normalizeText(scanner.nextLine()));
            } else if (choice.equals("3")) {
                System.out.print("Текст: ");
                System.out.println(reverseText(scanner.nextLine()));
            } else if (choice.equals("4")) {
                System.out.print("Текст: ");
                System.out.println(isPalindrome(scanner.nextLine())
                        ? "Палиндром" : "Не е палиндром");
            } else if (choice.equals("5")) {
                System.out.print("Текст: ");
                printFrequency(scanner.nextLine());
            } else if (choice.equals("6")) {
                System.out.print("Текст: ");
                String text = scanner.nextLine();
                int key = readInteger(scanner, "Ключ: ");
                System.out.println(caesarEncrypt(text, key));
            } else if (choice.equals("7")) {
                System.out.print("Криптиран текст: ");
                String text = scanner.nextLine();
                int key = readInteger(scanner, "Ключ: ");
                System.out.println(caesarDecrypt(text, key));
            } else if (choice.equals("8")) {
                System.out.print("Текст: ");
                String text = scanner.nextLine();
                System.out.print("Ключ: ");
                String key = scanner.nextLine();
                if (normalizeText(key).length() == 0) {
                    System.out.println("Ключът трябва да съдържа букви A-Z.");
                } else {
                    System.out.println(vigenereEncrypt(text, key));
                }
            } else if (choice.equals("9")) {
                System.out.print("Криптиран текст: ");
                String text = scanner.nextLine();
                System.out.print("Ключ: ");
                String key = scanner.nextLine();
                if (normalizeText(key).length() == 0) {
                    System.out.println("Ключът трябва да съдържа букви A-Z.");
                } else {
                    System.out.println(vigenereDecrypt(text, key));
                }
            } else if (choice.equals("10")) {
                System.out.print("Текст: ");
                String text = scanner.nextLine();
                int key = readInteger(scanner, "Числов ключ: ");
                xorTransform(text, key);
            } else if (choice.equals("11")) {
                System.out.print("Криптиран текст: ");
                caesarBruteForce(scanner.nextLine());
            } else {
                System.out.println("Непозната команда.");
            }
        }

        System.out.println("Край на програмата.");
    }
}
```

## Решения на задачите

### Задача 1

Използвайте цикъла за проверка на цяло число в `readInteger`. Той изхвърля нечисловия вход, показва съобщение и връща управление към менюто след обработката на избора.

```java
public static int readInteger(Scanner scanner, String prompt) {
    while (true) {
        System.out.print(prompt);
        if (scanner.hasNextInt()) {
            int value = scanner.nextInt();
            scanner.nextLine(); // изчиства остатъка от реда
            return value;
        }
        scanner.nextLine(); // премахва невалидния вход
        System.out.println("Моля, въведете цяло число.");
    }
}
```

Извикайте `readInteger(scanner, "Ключ: ")` вместо `scanner.nextInt()`.

### Задача 2

Добавете нов избор в менюто. Той изпълнява анализа и нормализирането последователно за един и същ входен текст.

```java
System.out.println("12. Анализ и нормализиране");
```

В обработката на избора добавете:

```java
} else if (choice.equals("12")) {
    System.out.print("Текст: ");
    String text = scanner.nextLine();
    analyzeText(text);
    System.out.println("Нормализиран текст: " + normalizeText(text));
```

### Задача 3

```java
public static int countCharacter(String text, char target) {
    int count = 0;
    for (int i = 0; i < text.length(); i++) {
        if (Character.toLowerCase(text.charAt(i)) == Character.toLowerCase(target)) count++;
    }
    return count;
}
```

### Задача 4

```java
public static void printCaesarVariants(String text) {
    for (int key = 1; key <= 25; key++) {
        System.out.println("Key " + key + " -> " + caesarDecrypt(text, key));
    }
}
```

### Задача 5

```java
if (choice.equals("0")) {
    running = false;
} else if (choice.equals("1")) {
    // обработка на команда 1
} else {
    System.out.println("Непозната команда.");
}
```

### Задача 6

Тази проверка вече се използва преди извикването на Vigenere методите.

```java
String cleanKey = normalizeText(key);
if (cleanKey.isEmpty()) {
    System.out.println("Ключът трябва да съдържа букви A-Z.");
} else {
    System.out.println(vigenereEncrypt(text, cleanKey));
}
```

### Задача 7

```java
int[] frequency = frequencyAnalysis(text);
for (int i = 0; i < frequency.length; i++) {
    if (frequency[i] > 0) System.out.println((char) ('A' + i) + " -> " + frequency[i]);
}
```

### Задача 8

```java
String normalized = normalizeText(text);
System.out.println("Нормализиран текст: " + normalized);
System.out.println("Премахнати символи: " + (text.length() - normalized.length()));
```

### Задача 9

```java
String text = scanner.nextLine();
System.out.println("Обърнат текст: " + reverseText(text));
System.out.println(isPalindrome(text) ? "Палиндром" : "Не е палиндром");
```

## Въпроси за проверка
1. Защо всеки алгоритъм е отделен метод?
2. Какво контролира променливата running?
3. Защо изборът се чете като String?
4. Какво трябва да стане при избор 0?
5. Как проверяваме дали менюто се повтаря правилно?
