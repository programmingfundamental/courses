---
title: 'Лабораторно упражнение 12 — Мини проект „Text & Crypto Toolkit“'
sidebar:
  label: 'Упражнение 12'
  order: 12
---

# Лабораторно упражнение №12

## Тема
Мини проект „Text & Crypto Toolkit“

## Продължителност
10 мин. теория + 15 мин. демонстрация + 40 мин. основна задача + 20 мин. самостоятелна работа + 5 мин. обобщение = 90 мин.

## Връзка с предходното упражнение
В предходните упражнения реализирахме отделни операции за анализ, нормализиране, обръщане, честоти, шифри и brute-force. Финалният проект ги събира в една конзолна програма с меню и отделни методи.

## Цел на упражнението
- Комбинирайте изучените алгоритми в конзолно меню.
- Разделете действията в ясни статични методи.
- Проверявайте входовете и връщайте към менюто до избор Exit.

## Необходими предварителни знания
Всички теми от упражнения 1–11, методи, Scanner, цикъл while, if/else, StringBuilder, масиви и switch/условия.

## Теоретична част
Менюто показва възможните действия и повтаря избора, докато потребителят избере 0. Всеки алгоритъм се държи в самостоятелен метод, например normalizeText() или caesarEncrypt(). Така main() управлява потока, а методите изпълняват конкретната обработка. Проектът остава базов: един клас Main, без йерархии и външни библиотеки.

**Важно за учебната криптография:** Цезаровият шифър, шифърът на Виженер, простото XOR преобразуване и използваните транспозиционни методи са само за обучение. Не ги използвайте за защита на чувствителна информация. Целта е да се упражнят алгоритми, преобразуване на текст, ключове, обратими операции, криптиране, декриптиране и елементарен криптоанализ; не се разглеждат съвременни криптографски системи.

## Алгоритъм
1. Покажи менюто и прочети избора като ред.
2. Ако изборът е 0, приключи цикъла.
3. За друг валиден избор прочети нужните данни и извикай метода.
4. Покажи резултата и върни се към менюто.
5. Обработи непознат избор с кратко съобщение.

Псевдокод:

```text
running = true
докато running:
    покажи меню
    choice = прочети ред
    ако choice == "0": running = false
    иначе ако choice == "1": извикай analyzeText()
    ...
    иначе: покажи непозната команда
покажи край на програмата
```

## Демонстрационен пример

```text
Примерна сесия:
=========================
 TEXT & CRYPTO TOOLKIT
=========================
1. Анализ на текст
2. Нормализиране
...
0. Exit
Избор: 2
Текст: Java, Security!
JAVASECURITY
Избор: 0
Край на програмата.
```

## Практическа задача
Създайте конзолно приложение с меню: 1 анализ на текст, 2 нормализиране, 3 обръщане, 4 проверка за палиндром, 5 честотен анализ, 6/7 Caesar encrypt/decrypt, 8/9 Vigenere encrypt/decrypt, 10 XOR, 11 Caesar brute-force и 0 Exit. Всеки алгоритъм трябва да е отделен метод. Използвайте вариантите от предходните решения.

## Насоки за реализация
- Прочитайте избора с nextLine(), за да не остават нови редове в Scanner.
- Направете основен while цикъл и отделни клонове за изборите.
- Използвайте отделни методи със смислени имена.
- Проверете празен Vigenere ключ и невалиден избор.

## Начален Java код

Допълнете TODO частите. Кодът е умишлено непълен и не представлява готово решение.

```java
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            // TODO: покажете меню и прочетете избора
            // TODO: извикайте съответния метод
        }
    }

    public static String normalizeText(String text) {
        // TODO
        return "";
    }
}
```

## Самостоятелна задача
Добавете проверка за нечислов ключ, която показва съобщение и позволява на потребителя да продължи да използва менюто.

## Допълнителна задача
**По желание:** добавете команда, която комбинира анализ на текст и нормализиране в една последователност.

## Въпроси за проверка
1. Защо всеки алгоритъм е отделен метод?
2. Какво контролира променливата running?
3. Защо изборът се чете като String?
4. Какво трябва да стане при избор 0?
5. Как проверяваме дали менюто се повтаря правилно?

## Очакван резултат
Приложението изпълнява избрания алгоритъм, показва резултат и отново извежда менюто. При избор 0 приключва с кратко съобщение. Кодът е в един клас и използва само стандартната Java библиотека.

## Пълно примерно решение

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
