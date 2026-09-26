---
title: "Подсказки — упражнение 10"
draft: true
pagefind: false
sidebar:
  hidden: true
---

# Просто транспозиционно шифриране

## Практическа задача
Напишете encrypt(text, columns) и decrypt(cipher, columns, originalLength). Проверете, че при положителен брой колони възстановеният текст е равен на входа. Обработете и последен непълен ред.

## Пълно решение на практическата задача
Работещият пълен пример е даден по-долу и се намира отделно във файла `10-transposition/src/Main.java`.


```java
import java.util.Scanner;

public class Main {
    public static String encrypt(String text, int columns) {
        if (columns <= 0 || text.length() == 0) return "";
        int rows = (text.length() + columns - 1) / columns;
        char[][] grid = new char[rows][columns];
        int index = 0;

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if (index < text.length()) {
                    grid[row][column] = text.charAt(index);
                    index++;
                } else {
                    grid[row][column] = 'X';
                }
            }
        }

        StringBuilder result = new StringBuilder();
        for (int column = 0; column < columns; column++) {
            for (int row = 0; row < rows; row++) {
                result.append(grid[row][column]);
            }
        }
        return result.toString();
    }

    public static String decrypt(String encrypted, int columns, int originalLength) {
        if (columns <= 0 || originalLength <= 0) return "";
        int rows = (originalLength + columns - 1) / columns;
        char[][] grid = new char[rows][columns];
        int index = 0;

        for (int column = 0; column < columns; column++) {
            for (int row = 0; row < rows; row++) {
                if (index < encrypted.length()) {
                    grid[row][column] = encrypted.charAt(index);
                    index++;
                }
            }
        }

        StringBuilder result = new StringBuilder();
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if (result.length() < originalLength) {
                    result.append(grid[row][column]);
                }
            }
        }
        return result.toString();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Въведете текст: ");
        String text = scanner.nextLine();
        System.out.print("Брой колони: ");
        int columns = scanner.nextInt();

        String encrypted = encrypt(text, columns);
        System.out.println("Транспониран текст: " + encrypted);
        System.out.println("Възстановен текст: "
                + decrypt(encrypted, columns, text.length()));
    }
}
```

## Решения на задачите

### Задача 1

При `text = "HELLO"` и `columns = 3` матрицата се допълва с един `X`. Шифротекстът е `HLELXO`; декриптирането използва първоначалната дължина 5 и връща `HELLO`.

```java
String text = "HELLO";
int columns = 3;
String cipher = encrypt(text, columns);
System.out.println("Шифротекст: " + cipher); // HLELXO
System.out.println("Обратно: " + decrypt(cipher, columns, text.length())); // HELLO
```

### Задача 2

Методът показва матрицата за запис и транспонираната матрица, която съответства на четенето по колони. Празните клетки се запълват с X.

```java
public static void printMatrices(String text, int columns) {
    if (columns <= 0) return;
    int rows = (text.length() + columns - 1) / columns;
    if (rows == 0) return;

    char[][] grid = new char[rows][columns];
    int index = 0;
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < columns; c++) {
            grid[r][c] = index < text.length() ? text.charAt(index++) : 'X';
        }
    }

    System.out.println("Матрица за запис по редове:");
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < columns; c++) System.out.print(grid[r][c] + " ");
        System.out.println();
    }

    System.out.println("Транспонирана матрица:");
    for (int c = 0; c < columns; c++) {
        for (int r = 0; r < rows; r++) System.out.print(grid[r][c] + " ");
        System.out.println();
    }
}
```
### Задача 3

```java
String text = "JAVA";
String cipher = encrypt(text, 1);
System.out.println(cipher);
System.out.println(decrypt(cipher, 1, text.length()));
```

### Задача 4

При точно запълнени редове формулата за допълване не добавя клетки.

```java
String text = "ABCDEFGH";
int columns = 4;
String cipher = encrypt(text, columns);
System.out.println(cipher);
System.out.println("Добавени символи: " + (cipher.length() - text.length())); // 0
```

### Задача 5

```java
String text = "CAT";
int columns = 5;
String cipher = encrypt(text, columns);
System.out.println("Шифротекст с X: " + cipher);
System.out.println("Възстановено: " + decrypt(cipher, columns, text.length()));
```

### Задача 6

```java
if (columns <= 0) {
    System.out.println("Броят колони трябва да е положително число.");
} else {
    System.out.println(encrypt(text, columns));
}
```

### Задача 7

```java
for (int i = 0; i < text.length(); i++) {
    int row = i / columns;
    int column = i % columns;
    System.out.println(text.charAt(i) + " -> ред " + row + ", колона " + column);
}
```

### Задача 8

```java
for (String sample : new String[] {"A", "HELLO", "JAVA COURSE"}) {
    for (int columns = 1; columns <= 4; columns++) {
        String cipher = encrypt(sample, columns);
        String restored = decrypt(cipher, columns, sample.length());
        System.out.println(sample.equals(restored));
    }
}
```

### Задача 9

```java
for (int columns : new int[] {2, 3, 4}) {
    System.out.println(columns + " колони: " + encrypt("PROGRAMMING", columns));
}
```

## Въпроси за проверка
1. Какво се променя при транспозиция?
2. Как се попълва матрицата при шифриране?
3. В какъв ред я четем за шифротекста?
4. Защо е нужна оригиналната дължина?
5. Какво означават запълващите X?
