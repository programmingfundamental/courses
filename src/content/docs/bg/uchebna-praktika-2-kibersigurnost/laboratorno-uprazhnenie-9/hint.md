---
title: "Подсказки — упражнение 9"
draft: true
pagefind: false
sidebar:
  hidden: true
---

# XOR преобразуване на текст

## Практическа задача
Напишете конзолна програма, която прочита текст и цяло число, създава масив от XOR стойности, показва ги като числа и прилага XOR със същия ключ, за да възстанови текста.

## Пълно решение на практическата задача
Работещият пълен пример е даден по-долу и се намира отделно във файла `09-xor/src/Main.java`.


```java
import java.util.Scanner;

public class Main {
    public static int[] xorToNumbers(String text, int key) {
        int[] values = new int[text.length()];
        for (int i = 0; i < text.length(); i++) {
            values[i] = text.charAt(i) ^ key;
        }
        return values;
    }

    public static String xorNumbersToText(int[] values, int key) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            result.append((char) (values[i] ^ key));
        }
        return result.toString();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Въведете текст: ");
        String text = scanner.nextLine();
        System.out.print("Числов ключ (например 7): ");
        int key = scanner.nextInt();

        int[] encrypted = xorToNumbers(text, key);
        System.out.print("XOR стойности: ");
        for (int i = 0; i < encrypted.length; i++) {
            if (i > 0) System.out.print(" ");
            System.out.print(encrypted[i]);
        }
        System.out.println();

        System.out.println("Повторен XOR възстановява: "
                + xorNumbersToText(encrypted, key));
    }
}
```

## Решения на задачите

### Задача 1

В двоичен вид `9 = 1001₂`, `5 = 0101₂`; XOR дава `1100₂`, тоест `12`.

```java
int a = 9;
int b = 5;
System.out.println("Десетично: " + (a ^ b));
System.out.println("Очаквана стойност: 12");
```

### Задача 2

Ограничете входа до малък неотрицателен ключ. След това повторното XOR преобразуване с този ключ възстановява входа.

```java
int key;
do {
    System.out.print("Ключ от 0 до 31: ");
    while (!scanner.hasNextInt()) {
        scanner.nextLine();
        System.out.print("Въведете цяло число от 0 до 31: ");
    }
    key = scanner.nextInt();
    scanner.nextLine();
} while (key < 0 || key > 31);

int[] encrypted = xorToNumbers(text, key);
System.out.println("Възстановен текст: " + xorNumbersToText(encrypted, key));
```

### Задача 3

```java
int first = 'A' ^ 7;
int second = first ^ 7;
System.out.println("A -> " + first + " -> " + (char) second);
int third = 'B' ^ 7;
System.out.println("B -> " + third + " -> " + (char) (third ^ 7));
```

### Задача 4

```java
for (int value : xorToNumbers(text, key)) {
    String bits = String.format("%8s", Integer.toBinaryString(value)).replace(' ', '0');
    System.out.println(bits);
}
```

### Задача 5

При ключ 0 все битове остават непроменени, следователно резултатът е равен на входа.

```java
System.out.println(java.util.Arrays.toString(xorToNumbers(text, 0)));
```

### Задача 6

```java
int[] values = xorToNumbers("", key);
System.out.println("Брой стойности: " + values.length);
```

### Задача 7

```java
int[] encrypted = xorToNumbers(text, key);
String restored = xorNumbersToText(encrypted, key);
System.out.println(restored.equals(text) ? "Възстановяването е успешно." : "Има грешка.");
```

### Задача 8

```java
int[] values = xorToNumbers(text, key);
for (int value : values) System.out.println(value);
```

### Задача 9

```java
for (int key : new int[] {1, 7, 31}) {
    System.out.println("Ключ " + key + ": "
            + java.util.Arrays.toString(xorToNumbers(text, key)));
}
```

## Въпроси за проверка
1. Какво означава XOR за два еднакви бита?
2. Кой оператор в Java изчислява XOR?
3. Защо второто XOR връща оригиналната стойност?
4. Защо изходът се показва като числа?
5. Защо тази проста операция не е подходяща за реална защита?
