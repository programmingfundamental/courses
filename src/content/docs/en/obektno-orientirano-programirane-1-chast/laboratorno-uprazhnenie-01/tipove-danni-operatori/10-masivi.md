---
title: Arrays
sidebar:
  order: 10
---

# Arrays

An array is a data structure for storing a fixed number of elements of the same type. Each element is accessed by an index. Indexing starts at 0.

Arrays are covered after loops because array elements are often visited using a loop.

## Declaring an Array

An array declaration specifies the element type and the variable name.

```java
int[] numbers;
String[] names;
```

The declaration `int[] numbers` means that `numbers` can refer to an array of integers.

## Creating an Array

Create an array with the `new` keyword and specify the number of elements.

The `new` keyword allocates memory for a new array. The elements are stored in this memory, and the variable can then refer to the new array.

```java
int[] numbers = new int[5];
```

The array has five elements. `numbers` refers to this array, whose indices range from 0 to 4.

## Initializing an Array with Values

When the initial values are known, create and initialize an array with a list of values:

```java
int[] numbers = {1, 5, 0, 10, 1};
```

This declaration creates an integer array with five elements.

## Accessing an Element

Access an array element by its index.

```java
int[] numbers = {1, 5, 0, 10, 1};

System.out.println(numbers[0]);
numbers[2] = 14;
```

The expression `numbers[0]` accesses the first element. The expression `numbers[2] = 14` changes the third element.

## Array Length

Every array has a `length` property that contains the number of elements.

```java
int[] numbers = {1, 5, 0, 10, 1};

System.out.println(numbers.length);
```

The value of `numbers.length` is 5.

## Iterating over an Array with a Loop

Use a `for` loop to visit array elements:

```java
int[] numbers = {1, 5, 0, 10, 1};

for (int index = 0; index < numbers.length; index++) {
    System.out.println(numbers[index]);
}
```

The `index` variable takes values from 0 to `numbers.length - 1), so every array element is accessed.

## Iterating with Enhanced `for`

When you only need each element's value, use an enhanced `for` loop:

```java
int[] numbers = {1, 5, 0, 10, 1};

for (int number : numbers) {
    System.out.println(number);
}
```

The `number` variable takes the value of the current element. The element's index is not used.

## Array Index Out of Bounds

An element can be accessed only when its index is within the array bounds. For an array of length 5, valid indices are 0 through 4.

```java
int[] numbers = {1, 5, 0, 10, 1};

System.out.println(numbers[5]);
```

Index 5 is invalid because 4 is the last valid index. Running this code causes a runtime error (handling this error is covered in Exercise 6).

## Multidimensional Arrays

A multidimensional array is an array whose elements are also arrays. A two-dimensional array is commonly used to represent tabular data.

```java
int[][] matrix = {
    {1, 2, 3},
    {4, 5, 6}
};
```

Access an element in a two-dimensional array with two indices:

```java
System.out.println(matrix[0][1]);
```

The first index selects a row, and the second selects an element in that row. This example prints 2.

## Iterating over a Two-Dimensional Array

Use nested loops to visit a two-dimensional array:

```java
for (int row = 0; row < matrix.length; row++) {
    for (int column = 0; column < matrix[row].length; column++) {
        System.out.println(matrix[row][column]);
    }
}
```

The outer loop visits the rows. The inner loop visits the elements in the current row.
