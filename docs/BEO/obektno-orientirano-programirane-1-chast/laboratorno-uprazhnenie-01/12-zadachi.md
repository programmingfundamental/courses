---
layout: default
title: Задачи
parent: Лабораторно упражнение 1
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 13
---

# Задачи

## Задача 1

Да се изведат на конзола всички нечетни числа в диапазона 0-300.

## Задача 2

Да се създаде масив {5, 6, 7, 9} с количеството на касети в дрогерия и масив {2.5, 3.6, 8.9, 7.5} с цените на една 1 касета. Да се изведе общата цена на всички касети.

## Задача 3

Да се изведат стойностите на съответните артикули за почивка при обща стойност 10000 ако:

1. Нощувката е 50% от общата стойност;
2. Наемът на плажни артикули е 5% от общата стойност;
3. Разходите за ресторант са 30% от общата стойност;
4. Допълнителните забавления са 10% от общата стойност;
5. Другите разходи са 5% от общата стойност.

## Задача 4

Да се изведат всички комбинации на думи, където първите 2 символа са цифри, вторите 2 букви, а петия символ е цифра.

## Задача 5

Да се напише програма, която извежда простите числа в интервала 1-300.

## Задача 6

Да се напише програма, която изчислява и извежда какъв процент от числата в интервала 1-300 се делят на простите числа в същия интервал.

## Задача 7

С помощта на debugger да се проследи изпълнението на програмата:

```java
package bg.tu_varna.sit;

public class Application {

    public static void main(String[] args) {
        double[] grades = new double[] {5, 3.5, 4.44, 6.00, 2.20, 3.11};
        double average = Calculator.getAverage(grades);
        System.out.println(average);
    }
}
```

```java
package bg.tu_varna.sit;

public class Calculator {

    public static double getAverage(double[] array) {
        double sum = 0;
        for (int i = 0; i < array.length; i++) {
            sum = array[i] + array[i];
        }
        return sum / array.length;
    }
}
```

Изпълнението на цикъла да бъде спряно при всяко число, чиято цяла част е четна. Да се провери защо изчислената средна стойност е неправилна.

## Задача 8

Да се анализира задачата и да се идентифицират грешките, без да се пренаписва целият код. Програмата трябва да изпълнява следните инструкции:

- `INC <операнд1>` - инкрементира операнд 1;
- `DEC <операнд1>` - декрементира операнд 1;
- `ADD <операнд1> <операнд2>` - събира операнд 1 и операнд 2;
- `MLA <операнд1> <операнд2>` - умножава операнд 1 и операнд 2;
- `END` - край на входа.

Като аргументи на програмата да се използват:

```text
"INC 0 END" "ADD 1323134 421315521 END" "DEC 57314183" "MLA 252621 324532 EN"
```

```java
package bg.tu_varna.sit;

public class Calculator {

    public static long arithmeticExpression(String expression) {
        long result = 0;
        while (!expression.equals("END")) {
            String[] codeArgs = expression.split(" ");
            switch (codeArgs[0]) {
                case "INC": {
                    int operandOne = Integer.parseInt(codeArgs[1]);
                    result = operandOne++;
                    break;
                }
                case "DEC": {
                    int operandOne = Integer.parseInt(codeArgs[1]);
                    result = operandOne--;
                    break;
                }
                case "ADD": {
                    int operandOne  = Integer.parseInt(codeArgs[1]);
                    int operandTwo = Integer.parseInt(codeArgs[2]);
                    result = operandOne + operandTwo;
                    break;
                }
                case "MLA": {
                    int operandOne  = Integer.parseInt(codeArgs[1]);
                    int operandTwo = Integer.parseInt(codeArgs[2]);
                    result = (long)(operandOne * operandTwo);
                    break;
                }
                default:
                    break;
            }
        }
        return result;
    }
}
```

```java
package bg.tu_varna.sit;

public class Application {

    public static void main(String[] args) {
        for (int i = 0; i <= args.length; i++) {
            long result = Calculator.arithmeticExpression(args[i]);
            System.out.println(result);
        }
    }
}
```
