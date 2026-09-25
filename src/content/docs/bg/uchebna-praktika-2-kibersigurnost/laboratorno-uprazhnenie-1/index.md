---
title: 'Лабораторно упражнение 1 — Основни операции с текстови низове и обхождане на символи в Java'
sidebar:
  label: 'Упражнение 1'
  order: 1
---

# Основни операции с текстови низове и обхождане на символи в Java

String съхранява текст, а length() връща броя символи. Индексите започват от 0, затова последният валиден индекс е length() - 1. charAt(i) връща символа на позиция i. toUpperCase() и toLowerCase() създават текст с променен регистър; оригиналният низ не се променя. Например CYBER има дължина 5 и charAt(0) е C.

Условията на практическата, самостоятелната и допълнителната задача са на [страницата Задачи](/courses/bg/uchebna-praktika-2-kibersigurnost/laboratorno-uprazhnenie-1/zadachi/).

## Алгоритъм
1. Прочети цял ред като текст.
2. Покажи дължината и вариантите с главни и малки букви.
3. За всеки индекс от 0 до length() - 1 покажи charAt(index).

Псевдокод:

```text
text = прочети ред
покажи text.length()
за i от 0 до text.length() - 1:
    покажи text.charAt(i)
```

## Примерен вход и изход
```text
Вход:
CYBER

Очакван изход (съкратено):
Дължина: 5
Главни букви: CYBER
Малки букви: cyber
Символи един по един:
C
Y
B
E
R
```

## Пример
Допълнете TODO частите. Кодът е умишлено непълен и не представлява готово решение.

```java
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Въведете текст: ");
        String text = scanner.nextLine();

        // TODO: покажете дължината и двата варианта на регистъра
        // TODO: обходете текста и покажете всеки символ
    }
}
```

## Въпроси за проверка
1. Какво връща length()?
2. Кой е индексът на първия символ?
3. Какво връща charAt(i)?
4. Променя ли toUpperCase() оригиналния String?
5. Защо условието на цикъла е i < text.length()?

## Очакван резултат
За CYBER програмата показва дължина 5, малки букви cyber и пет реда със символите C, Y, B, E и R. За празен вход цикълът не се изпълнява.

## Пълно примерно решение
Работещият пълен пример е даден по-долу и се намира отделно във файла `01-text-basics/src/Main.java`.


```java
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Въведете текст: ");
        String text = scanner.nextLine();

        System.out.println("Дължина: " + text.length());
        System.out.println("Главни букви: " + text.toUpperCase());
        System.out.println("Малки букви: " + text.toLowerCase());
        System.out.println("Символи един по един:");

        for (int i = 0; i < text.length(); i++) {
            System.out.println(text.charAt(i));
        }
    }
}
```
