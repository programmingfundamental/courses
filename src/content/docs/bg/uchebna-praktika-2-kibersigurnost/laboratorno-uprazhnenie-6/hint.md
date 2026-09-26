---
title: "Подсказки — упражнение 6"
draft: true
pagefind: false
sidebar:
  hidden: true
---

# Честотен анализ на символи

## Практическа задача
Напишете програма, която чете текст и показва честотна таблица за всички букви A-Z. Малките букви трябва да се броят към съответните главни, а интервали, цифри и пунктуация да се игнорират.

## Пълно решение на практическата задача
Работещият пълен пример е даден по-долу и се намира отделно във файла `06-frequency-analysis/src/Main.java`.


```java
import java.util.Scanner;

public class Main {
    public static int[] frequencyAnalysis(String text) {
        int[] frequency = new int[26];

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= 'a' && c <= 'z') {
                c = (char) (c - 'a' + 'A');
            }
            if (c >= 'A' && c <= 'Z') {
                frequency[c - 'A']++;
            }
        }
        return frequency;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Въведете текст: ");
        String text = scanner.nextLine();
        int[] frequency = frequencyAnalysis(text);

        for (int i = 0; i < frequency.length; i++) {
            char letter = (char) ('A' + i);
            System.out.println(letter + " -> " + frequency[i]);
        }
    }
}
```

## Решения на задачите

### Задача 1

При равен брой не заменяме текущия максимум. Обхождането от A към Z така избира първата буква по азбучен ред.

```java
int best = 0;
for (int i = 1; i < frequency.length; i++) {
    if (frequency[i] > frequency[best]) best = i;
}
if (frequency[best] == 0) {
    System.out.println("Няма отчетени букви.");
} else {
    System.out.println("Най-честа буква: " + (char) ('A' + best)
            + " (" + frequency[best] + ")");
}
```

### Задача 2

```java
int total = 0;
for (int count : frequency) total += count;

if (total == 0) {
    System.out.println("Няма отчетени букви.");
} else {
    for (int i = 0; i < frequency.length; i++) {
        double percent = 100.0 * frequency[i] / total;
        System.out.printf("%c -> %d (%.2f%%)%n", (char) ('A' + i), frequency[i], percent);
    }
}
```

## Въпроси за проверка
1. Защо масивът има точно 26 елемента?
2. Какъв индекс съответства на Z?
3. Какво означава frequency[c - 'A']?
4. Защо трябва да проверим диапазона преди достъпа до масива?
5. Какво се показва за буква, която липсва?
