---
title: 'Лабораторно упражнение 11 — Разбиване на Цезаров шифър с brute-force и честотен анализ'
sidebar:
  label: 'Упражнение 11'
  order: 11
---

# Разбиване на Цезаров шифър с brute-force и честотен анализ

Цезаровият шифър има само 25 ненулеви ключа за проверка. Brute-force означава да изпробваме всеки ключ и да покажем резултата. За допълнителна подсказка намираме най-честата буква в шифротекста и пробно приемаме, че тя съответства на E. Това предположение може да е грешно, особено при кратък текст или различен език; човек проверява всички варианти.

**Важно за учебната криптография:** Цезаровият шифър, шифърът на Виженер, простото XOR преобразуване и използваните транспозиционни методи са само за обучение. Не ги използвайте за защита на чувствителна информация. Целта е да се упражнят алгоритми, преобразуване на текст, ключове, обратими операции, криптиране, декриптиране и елементарен криптоанализ; не се разглеждат съвременни криптографски системи.

Условията на практическата, самостоятелната и допълнителната задача са на [страницата Задачи](/courses/bg/uchebna-praktika-2-kibersigurnost/laboratorno-uprazhnenie-11/zadachi/).

## Алгоритъм
1. За key от 1 до 25 декриптирай текста и покажи резултата.
2. Преброй честотите на буквите в шифротекста.
3. Намери най-честата буква.
4. Изчисли предполагаемия ключ, ако най-честата буква е била E в оригинала.
5. Сравни подсказката с всички показани варианти и прецени смисления текст.

Псевдокод:

```text
за key от 1 до 25:
    покажи key и decrypt(cipher, key)
frequency = честоти(cipher)
common = най-честата буква
suggestedKey = (common - 'E' + 26) mod 26
покажи подсказката и провери я спрямо вариантите
```

## Примерен вход и изход
```text
Вход:
KHOOR

Сред резултатите:
Key 3 -> HELLO

Честотната подсказка е само ориентир и може да не е надеждна за кратък текст.
```

## Пример
Допълнете TODO частите. Кодът е умишлено непълен и не представлява готово решение.

```java
for (int key = 1; key <= 25; key++) {
    // TODO: покажете ключа и decrypt(cipher, key)
}

int[] frequency = frequencyAnalysis(cipher);
// TODO: намерете най-честата буква и изведете предположение
```

## Въпроси за проверка
1. Колко ненулеви ключа има Цезаровият шифър за A-Z?
2. Какво означава brute-force?
3. Как намираме най-честата буква?
4. На какво допускане стъпва честотната подсказка?
5. Защо резултатът не е гаранция за правилен ключ?

## Очакван резултат
За KHOOR се показват 25 възможности, сред които Key 3 -> HELLO. Честотният анализ извежда най-честата буква и грубо предположение, като обяснява ограниченията му.

## Пълно примерно решение
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
