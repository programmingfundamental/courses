---
title: Tasks
sidebar:
  order: 100
  label: Tasks
taskPage: true
---

## Task 1

Print all odd numbers in the range 0–300 to the console.

Create the project using **New Project → Java**. Identify which files were created by the IDE and which you added yourself. Extract the check into an `isOdd(int number)` method and explain its analogy with a function that takes a number and returns a boolean value.

## Task 2

Use the debugger to trace the execution of the following program:

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

Pause the loop at every number whose integer part is even. Investigate why the calculated average is incorrect.

Use the [Debugging page](/courses/en/obektno-orientirano-programirane-1-chast/laboratorno-uprazhnenie-01/otkrivane-i-otstranyavane-na-greshki/): set a conditional breakpoint, watch `sum` and `i`, and enter `getAverage()` using **Step Into**. Record the expected and actual values before the fix. After the fix, check for a result of `4.041666...` with a tolerance of `0.000001`, and also test an array containing a single element.

## Task 3

Create an array `{5, 6, 7, 9}` containing the quantities of cartridges in a drugstore and an array `{2.5, 3.6, 8.9, 7.5}` containing the price of one cartridge of each type. Print the total price of all cartridges.

## Task 4

Calculate and print the amounts for the following holiday expenses, given a total budget of 10000:

1. Accommodation accounts for 50% of the total.
2. Beach equipment rental accounts for 5%.
3. Restaurant expenses account for 30%.
4. Additional entertainment accounts for 10%.
5. Other expenses account for 5%.

## Task 5

Print all combinations of five-character strings in which the first two characters are digits, the next two are letters, and the fifth character is a digit.

## Task 6

Write a program that prints the prime numbers in the range 1–300.

## Task 7

Write a program that calculates and prints what percentage of the numbers in the range 1–300 are divisible by the prime numbers in the same range.
