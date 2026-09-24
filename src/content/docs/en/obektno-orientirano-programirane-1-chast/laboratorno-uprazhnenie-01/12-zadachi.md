---
title: Tasks
sidebar:
  order: 100
  label: Tasks
taskPage: true
---

## Task 1

Print all odd numbers in the range 0–300.

Create the project with **New Project → Java**. Identify which files were created by the IDE and which ones you added. Put the check in a method named `isOdd(int number)`, and explain how it is similar to a function that accepts a number and returns a boolean value.

## Task 2

Create an array `{5, 6, 7, 9}` containing the quantities of cases of goods in a drugstore, and an array `{2.5, 3.6, 8.9, 7.5}` containing the price of one case of each item. Print the total price of all cases.

## Task 3

For a total vacation budget of 10,000, print the amounts allocated to each category:

1. Accommodation is 50% of the total.
2. Rental of beach equipment is 5% of the total.
3. Restaurant expenses are 30% of the total.
4. Additional entertainment is 10% of the total.
5. Other expenses are 5% of the total.

## Task 4

Print all five-character strings whose first two characters are digits, next two are letters, and fifth character is a digit.

## Task 5

Write a program that prints the prime numbers in the range 1–300.

## Task 6

Write a program that calculates and prints what percentage of the numbers in the range 1–300 are divisible by prime numbers in the same range.

## Task 7

Use a debugger to trace the execution of the following program:

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

Pause the loop for each number whose integer part is even. Find out why the calculated average is incorrect.

Use the [debugging guide](/courses/en/obektno-orientirano-programirane-1-chast/laboratorno-uprazhnenie-01/otkrivane-i-otstranyavane-na-greshki/): set a conditional breakpoint, watch `sum` and `i`, and enter `getAverage()` with **Step Into**. Record the expected and actual values before fixing the code. After the fix, verify the result `4.041666...` with a tolerance of `0.000001`, as well as an array containing one element.

## Task 8

Analyze the task and identify the errors without rewriting all the code. The program should execute the following instructions:

- `INC <operand1>` — increment operand 1;
- `DEC <operand1>` — decrement operand 1;
- `ADD <operand1> <operand2>` — add operand 1 and operand 2;
- `MLA <operand1> <operand2>` — multiply operand 1 and operand 2;
- `END` — end of input.

Use the following program arguments:

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
