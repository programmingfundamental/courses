---
title: 'Лабораторно упражнение 2 — Търсене на символ и подниз в текст'
sidebar:
  label: 'Упражнение 2'
  order: 2
---

# Лабораторно упражнение №2

## Тема
Търсене на символ и подниз в текст

## Продължителност
10 мин. теория + 15 мин. демонстрация + 40 мин. основна задача + 20 мин. самостоятелна работа + 5 мин. обобщение = 90 мин.

## Връзка с предходното упражнение
В предходното упражнение обхождахме текста и разглеждахме всеки символ поотделно. Сега ще използваме същото обхождане, за да намерим първата позиция на търсен символ или последователност от символи.

## Цел на упражнението
- Реализирайте линейно търсене на символ със собствен цикъл.
- Потърсете подниз чрез сравняване на символи.
- Върнете -1, когато няма съвпадение, и сравнете с indexOf().

## Необходими предварителни знания
String, length(), charAt(), for/while, if, методи с параметри и връщана стойност.

Условията на практическата, самостоятелната и допълнителната задача са на [страницата Задачи](/courses/bg/uchebna-praktika-2-kibersigurnost/laboratorno-uprazhnenie-2/zadachi/).

## Теоретична част
Линейното търсене проверява последователно всеки символ, докато намери търсения. За подниз стартираме от всяка възможна позиция и сравняваме следващите символи. Ако всички съвпадат, връщаме началния индекс; ако обхождането приключи без съвпадение, връщаме -1. indexOf() е готов метод за сравнение, но тук алгоритъмът се реализира самостоятелно.

## Алгоритъм
1. Обходи низа отляво надясно и сравнявай символа с търсения.
2. За подниз провери всяка начална позиция, от която целият шаблон може да се побере.
3. Сравнявай символите на шаблона един по един.
4. Върни първата съвпадаща позиция или -1.

Псевдокод:

```text
за i от 0 до дължина(text) - 1:
    ако text[i] == target: върни i
за start от 0 до дължина(text) - дължина(pattern):
    сравни всички символи на pattern с text от start
    ако всички съвпадат: върни start
върни -1
```

## Демонстрационен пример

```text
Вход:
Текст: BANANA
Символ: N
Подниз: ANA

Очакван изход:
Позиция на символа: 2
Позиция на подниза: 1
-1 означава, че няма съвпадение.
```

## Начален Java код

Допълнете TODO частите. Кодът е умишлено непълен и не представлява готово решение.

```java
public static int findCharacter(String text, char target) {
    // TODO: върнете индекса или -1
    return -1;
}

public static int findSubstring(String text, String pattern) {
    // TODO: сравнявайте шаблона при всяка възможна начална позиция
    return -1;
}
```

## Въпроси за проверка
1. Какво означава резултат -1?
2. Защо поднизът изисква вътрешен цикъл?
3. Как се определя последната възможна начална позиция?
4. Какво връща indexOf(), ако няма съвпадение?
5. Защо задачата изисква собствено линейно търсене?

## Очакван резултат
За BANANA методите връщат 2 за първата N и 1 за първата ANA. При липсващ символ или подниз резултатът е -1.

## Пълно примерно решение

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
