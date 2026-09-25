---
title: "Подсказки — упражнение 11"
draft: true
pagefind: false
sidebar:
  hidden: true
---

# Разбиване на Цезаров шифър с brute-force и честотен анализ

## Практическа задача
Напишете програма, която извежда всички 25 декриптирани варианта за входен текст. След това пребройте буквите, намерете най-честата и покажете предположение за ключ при опростеното допускане, че най-честата буква в оригинала е E.

## Пълно решение на практическата задача
Работещият пълен пример е даден по-долу и се намира отделно във файла `11-caesar-analysis/src/Main.java`.


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

    public static String decrypt(String text, int key) {
        String clean = lettersOnly(text);
        StringBuilder result = new StringBuilder();
        int shift = ((key % 26) + 26) % 26;
        for (int i = 0; i < clean.length(); i++) {
            char c = clean.charAt(i);
            result.append((char) ('A' + (c - 'A' - shift + 26) % 26));
        }
        return result.toString();
    }

    public static int[] frequencyAnalysis(String text) {
        int[] frequency = new int[26];
        String clean = lettersOnly(text);
        for (int i = 0; i < clean.length(); i++) {
            frequency[clean.charAt(i) - 'A']++;
        }
        return frequency;
    }

    public static char mostFrequentLetter(int[] frequency) {
        int best = 0;
        for (int i = 1; i < frequency.length; i++) {
            if (frequency[i] > frequency[best]) best = i;
        }
        return (char) ('A' + best);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Криптиран текст (A-Z): ");
        String cipher = scanner.nextLine();

        for (int key = 1; key <= 25; key++) {
            System.out.println("Key " + key + " -> " + decrypt(cipher, key));
        }

        int[] frequency = frequencyAnalysis(cipher);
        if (lettersOnly(cipher).length() > 0) {
            char common = mostFrequentLetter(frequency);
            int suggestedKey = (common - 'E' + 26) % 26;
            System.out.println("Най-честа буква в шифротекста: " + common);
            System.out.println("Грубо предположение: ако тя е E в оригинала, ключът е "
                    + suggestedKey + ". Проверете го сред показаните варианти.");
        }
    }
}
```

## Въпроси за проверка
1. Колко ненулеви ключа има Цезаровият шифър за A-Z?
2. Какво означава brute-force?
3. Как намираме най-честата буква?
4. На какво допускане стъпва честотната подсказка?
5. Защо резултатът не е гаранция за правилен ключ?
