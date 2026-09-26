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

### Задача 3

```java
public static int lastCharacterPosition(String text, char target) {
    for (int i = text.length() - 1; i >= 0; i--) {
        if (text.charAt(i) == target) return i;
    }
    return -1;
}
```

### Задача 4

При всяка позиция проверете целия шаблон; индексът се увеличава с единица, за да се допусне припокриване.

```java
public static int countOverlapping(String text, String pattern) {
    if (pattern.isEmpty()) return 0;
    int count = 0;
    for (int i = 0; i <= text.length() - pattern.length(); i++) {
        if (text.startsWith(pattern, i)) count++;
    }
    return count;
}
```

### Задача 5

```java
public static int findCharacterIgnoreCase(String text, char target) {
    char wanted = Character.toLowerCase(target);
    for (int i = 0; i < text.length(); i++) {
        if (Character.toLowerCase(text.charAt(i)) == wanted) return i;
    }
    return -1;
}
```

### Задача 6

```java
for (int i = 0; i <= text.length() - pattern.length(); i++) {
    if (text.startsWith(pattern, i)) System.out.println(i);
}
```

### Задача 7

В Java `indexOf("")` връща 0, защото празният низ се намира в началото на всеки низ. В собствен метод върнете същата стойност преди цикъла.

```java
if (pattern.isEmpty()) return 0;
```

### Задача 8

```java
char target = 'a';
int charPosition = text.indexOf(target);
int stringPosition = text.indexOf(String.valueOf(target));
System.out.println(charPosition + " / " + stringPosition);
```

### Задача 9

```java
int start = 3;
int position = text.indexOf(pattern, start);
System.out.println("Позиция след " + start + ": " + position);
```

## Въпроси за проверка
1. Какво означава резултат -1?
2. Защо поднизът изисква вътрешен цикъл?
3. Как се определя последната възможна начална позиция?
4. Какво връща indexOf(), ако няма съвпадение?
5. Защо задачата изисква собствено линейно търсене?
