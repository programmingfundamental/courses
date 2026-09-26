---
title: "Подсказки — упражнение 4"
draft: true
pagefind: false
sidebar:
  hidden: true
---

# Преобразуване и нормализиране на текст

## Практическа задача
Напишете normalizeText(text), която премахва интервали и препинателни знаци и връща само главни английски букви A-Z. Покажете резултата за вход от потребителя.

## Пълно решение на практическата задача
Работещият пълен пример е даден по-долу и се намира отделно във файла `04-text-normalization/src/Main.java`.


```java
import java.util.Scanner;

public class Main {
    public static String normalizeText(String text) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= 'a' && c <= 'z') {
                c = (char) (c - 'a' + 'A');
            }
            if (c >= 'A' && c <= 'Z') {
                result.append(c);
            }
        }

        return result.toString();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Въведете текст: ");
        String text = scanner.nextLine();

        System.out.println("Нормализиран текст: " + normalizeText(text));
    }
}
```

## Решения на задачите

### Задача 1

Изчислете разликата след нормализирането.

```java
String normalized = normalizeText(text);
int removed = text.length() - normalized.length();
System.out.println("Премахнати символи: " + removed);
```

### Задача 2

Този вариант запазва латинските и кирилските букви, като преобразува малките в главни. Кирилската азбука има отделни диапазони; `Ё` и `ё` са извън основния диапазон и се обработват изрично.

```java
public static String normalizeLatinAndCyrillic(String text) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < text.length(); i++) {
        char c = text.charAt(i);
        if (c >= 'a' && c <= 'z') c = (char) (c - 'a' + 'A');
        else if (c >= '\u0430' && c <= '\u044F') c = (char) (c - '\u0430' + '\u0410');
        else if (c == '\u0451') c = '\u0401';

        if ((c >= 'A' && c <= 'Z') || (c >= '\u0410' && c <= '\u042F') || c == '\u0401') {
            result.append(c);
        }
    }
    return result.toString();
}
```

Диапазоните `\u0410`–`\u042F` са главните кирилски букви А–Я, `\u0430`–`\u044F` са малките а–я, а `\u0401`/`\u0451` са Ё/ё.

## Въпроси за проверка
1. Какво означава нормализиране в тази задача?
2. Кои символи се запазват?
3. Как се преобразува малка ASCII буква в главна?
4. Защо използваме StringBuilder?
5. Защо пунктуацията не се добавя към резултата?
