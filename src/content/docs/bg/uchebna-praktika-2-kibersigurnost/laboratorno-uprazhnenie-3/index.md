---
title: 'Лабораторно упражнение 3 — Броене на символи, думи и срещания в текст'
sidebar:
  label: 'Упражнение 3'
  order: 3
---

# Лабораторно упражнение №3

## Тема
Броене на символи, думи и срещания в текст

## Продължителност
10 мин. теория + 15 мин. демонстрация + 40 мин. основна задача + 20 мин. самостоятелна работа + 5 мин. обобщение = 90 мин.

## Връзка с предходното упражнение
В упражнение 2 прекратявахме търсенето при първото съвпадение. Сега ще обходим целия текст и ще актуализираме няколко брояча, за да получим цялостна статистика.

## Цел на упражнението
- Пребройте общия брой символи, ASCII букви, цифри и интервали.
- Определете броя на думите чрез следене дали сме вътре в дума.
- Пребройте символ и всички срещания на подниз, включително припокриващи се.

## Необходими предварителни знания
String, for/while, charAt(), условия, броячи и методи; линейно търсене от упражнение 2.

Условията на практическата, самостоятелната и допълнителната задача са на [страницата Задачи](/courses/bg/uchebna-praktika-2-kibersigurnost/laboratorno-uprazhnenie-3/zadachi/).

## Теоретична част
Броячът се увеличава, когато текущият символ удовлетворява условие. За думи следим състояние inWord: при whitespace излизаме от дума, а при първия не-whitespace символ след разделител започва нова дума. Броят срещания на подниз може да включва припокривания: в AAA поднизът AA се среща на позиции 0 и 1.

## Алгоритъм
1. Настрой броячите на 0 и inWord на false.
2. Обходи всички символи и провери дали са буква, цифра или интервал.
3. Променяй inWord при разделител и увеличавай думите при начало на нова дума.
4. Сравни шаблона на всяка възможна позиция и увеличи брояча при съвпадение.

Псевдокод:

```text
за всеки символ c:
    ако c е буква: letters++
    ако c е цифра: digits++
    ако c е интервал: spaces++
    ако c е whitespace: inWord = false
    иначе ако inWord == false: words++; inWord = true
провери pattern от всяка позиция и преброй съвпаденията
```

## Демонстрационен пример

```text
Вход:
Текст: Java 17 Java
Символ: a
Подниз: Java

Очакван резултат:
Символи общо: 12
Букви A-Z: 8
Цифри: 2
Интервали: 2
Думи: 3
Срещания на избрания символ: 4
Срещания на подниза (с припокриване): 2
```

## Начален Java код

Допълнете TODO частите. Кодът е умишлено непълен и не представлява готово решение.

```java
int letters = 0;
int digits = 0;
int spaces = 0;
int words = 0;
boolean inWord = false;

for (int i = 0; i < text.length(); i++) {
    char c = text.charAt(i);
    // TODO: актуализирайте броячите
}
```

## Въпроси за проверка
1. Каква е разликата между символи и букви?
2. Защо е нужна променливата inWord?
3. Кога броят думи се увеличава?
4. Как броим припокриващи се срещания?
5. Защо празният шаблон се изключва?

## Очакван резултат
Програмата извежда статистиката за целия ред. За примерния вход има 12 символа, 8 букви, 2 цифри, 2 интервала и 3 думи; избраните стойности се броят според входа.

## Пълно примерно решение

Работещият пълен пример е даден по-долу и се намира отделно във файла `03-text-counting/src/Main.java`.


```java
import java.util.Scanner;

public class Main {
    public static boolean isLetter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }

    public static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    public static boolean isWhitespace(char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }

    public static int countMatches(String text, String pattern) {
        if (pattern.length() == 0) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i <= text.length() - pattern.length(); i++) {
            int j = 0;
            while (j < pattern.length()
                    && text.charAt(i + j) == pattern.charAt(j)) {
                j++;
            }
            if (j == pattern.length()) {
                count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Въведете текст: ");
        String text = scanner.nextLine();
        System.out.print("Символ за броене: ");
        String symbolInput = scanner.nextLine();
        System.out.print("Подниз за броене: ");
        String pattern = scanner.nextLine();

        int letters = 0;
        int digits = 0;
        int spaces = 0;
        int words = 0;
        boolean inWord = false;
        int selected = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (isLetter(c)) letters++;
            if (isDigit(c)) digits++;
            if (c == ' ') spaces++;

            if (isWhitespace(c)) {
                inWord = false;
            } else if (!inWord) {
                words++;
                inWord = true;
            }

            if (symbolInput.length() > 0 && c == symbolInput.charAt(0)) {
                selected++;
            }
        }

        System.out.println("Символи общо: " + text.length());
        System.out.println("Букви A-Z: " + letters);
        System.out.println("Цифри: " + digits);
        System.out.println("Интервали: " + spaces);
        System.out.println("Думи: " + words);
        System.out.println("Срещания на избрания символ: " + selected);
        System.out.println("Срещания на подниза (с припокриване): "
                + countMatches(text, pattern));
    }
}
```

