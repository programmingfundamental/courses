---
title: 'Лабораторно упражнение 5 — Обръщане на текст и проверка за палиндром'
sidebar:
  label: 'Упражнение 5'
  order: 5
---

# Обръщане на текст и проверка за палиндром

Обърнатият низ се изгражда, като започнем от последния индекс и се движим към 0. Палиндромът се чете еднакво отляво надясно и отдясно наляво. За проверката пропускаме символи, които не са ASCII букви или цифри, сравняваме останалите без значение от регистъра и придвижваме два индекса един към друг.

Условията на практическата, самостоятелната и допълнителната задача са на [страницата Задачи](/courses/bg/uchebna-praktika-2-kibersigurnost/laboratorno-uprazhnenie-5/zadachi/).

## Алгоритъм
1. За обръщане обходи низ от length()-1 до 0 и добавяй символите.
2. За палиндром постави left в началото и right в края.
3. Пропускай от двата края знаци, които не са буква или цифра.
4. Сравни символите без значение от регистъра; при разлика върни false.
5. Продължи към средата; ако няма разлика, върни true.

Псевдокод:

```text
reverse: за i от text.length()-1 до 0: добави text[i]
palindrome:
    left = 0; right = text.length()-1
    докато left < right:
        прескочи не-букви/цифри от двата края
        ако uppercase(text[left]) != uppercase(text[right]): върни false
        left++; right--
    върни true
```

## Примерен вход и изход
```text
Входове и очакван изход:
LEVEL -> Обърнат текст: LEVEL; Палиндром
JAVA -> Обърнат текст: AVAJ; Не е палиндром
A man, a plan, a canal: Panama -> Палиндром
```

## Пример
Допълнете TODO частите. Кодът е умишлено непълен и не представлява готово решение.

```java
public static String reverse(String text) {
    StringBuilder result = new StringBuilder();
    // TODO: добавяйте символите от последния към първия
    return result.toString();
}

public static boolean isPalindrome(String text) {
    // TODO: използвайте left и right
    return false;
}
```

## Въпроси за проверка
1. Какъв индекс е последният символ?
2. Защо StringBuilder е удобен при обръщане?
3. Кои символи пропускаме в проверката?
4. Как се движат left и right?
5. Какво означава, ако се открие различаваща се двойка?

## Очакван резултат
LEVEL и фразата A man, a plan, a canal: Panama се разпознават като палиндроми. JAVA не е палиндром, а обърнатият му текст е AVAJ.

## Пълно примерно решение
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
