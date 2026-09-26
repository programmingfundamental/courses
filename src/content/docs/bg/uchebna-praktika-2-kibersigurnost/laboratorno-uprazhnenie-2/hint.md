---
title: "Подсказки — упражнение 2"
draft: true
pagefind: false
sidebar:
  hidden: true
---

# Търсене на символ и подниз в текст

## Практическа задача
Напишете метод findCharacter(text, target), който връща позицията на първото срещане, и метод findSubstring(text, pattern), който реализира собствено търсене на подниз. В main прочетете трите входни стойности и покажете резултатите.

## Пълно решение на практическата задача
Работещият пълен пример е даден по-долу и се намира отделно във файла `02-text-search/src/Main.java`.


```java
import java.util.Scanner;

public class Main {
    public static int findCharacter(String text, char target) {
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == target) {
                return i;
            }
        }
        return -1;
    }

    public static int findSubstring(String text, String pattern) {
        if (pattern.length() == 0) {
            return 0;
        }

        for (int start = 0; start <= text.length() - pattern.length(); start++) {
            int offset = 0;
            while (offset < pattern.length()
                    && text.charAt(start + offset) == pattern.charAt(offset)) {
                offset++;
            }
            if (offset == pattern.length()) {
                return start;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Текст: ");
        String text = scanner.nextLine();
        System.out.print("Символ за търсене: ");
        String symbolInput = scanner.nextLine();
        System.out.print("Подниз за търсене: ");
        String pattern = scanner.nextLine();

        if (symbolInput.length() == 0) {
            System.out.println("Не е въведен символ.");
            return;
        }

        System.out.println("Позиция на символа: "
                + findCharacter(text, symbolInput.charAt(0)));
        System.out.println("Позиция на подниза: " + findSubstring(text, pattern));
        System.out.println("-1 означава, че няма съвпадение.");
    }
}
```

## Решения на задачите

### Задача 1

Преместването с една позиция след всяко съвпадение позволява да се преброят и припокриващи се срещания.

```java
public static int printCharacterPositions(String text, char target) {
    int count = 0;
    for (int i = 0; i < text.length(); i++) {
        if (text.charAt(i) == target) {
            System.out.println("Съвпадение на позиция: " + i);
            count++;
        }
    }
    return count;
}
```

### Задача 2

Сравнете първата позиция, върната от собствените методи и от `indexOf()`.

```java
int ownCharacter = findCharacter(text, target);
int builtInCharacter = text.indexOf(target);
int ownSubstring = findSubstring(text, pattern);
int builtInSubstring = text.indexOf(pattern);

System.out.println("Символ: " + ownCharacter + " / " + builtInCharacter);
System.out.println("Подниз: " + ownSubstring + " / " + builtInSubstring);
```

## Въпроси за проверка
1. Какво означава резултат -1?
2. Защо поднизът изисква вътрешен цикъл?
3. Как се определя последната възможна начална позиция?
4. Какво връща indexOf(), ако няма съвпадение?
5. Защо задачата изисква собствено линейно търсене?
