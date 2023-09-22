---
layout: default
title: Упражнения за извънаудиторна заетост
parent: Лабораторно упражнение 4
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 3
---
# Упражнения за извънаудиторна заетост

#### Задача 1

С помощта на дебъгера проследете изпълнението на програмата.

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

* Спрете изпълнението на цикъла при всяко число, чиято цяла част е четна.

#### Задача 2

Анализирайте задачата и определете грешката (без да пренаписвате целия код), така че да се изпълняват следните инструкции:

* &#x20;INC <операнд1> - инкрементира операнд 1
* DEC <операнд1> - декрементира операнд 1
* ADD <операнд1> <операнд2> - събира операнд 1 и операнд 2
* MLA <операнд1> <операнд2> - умножава операнд 1 и операнд 2
* END – end of input

Като аргументи на програмата използвайте:

"INC 0 END" "ADD 1323134 421315521 END" "DEC 57314183" "MLA 252621 324532 EN"


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
