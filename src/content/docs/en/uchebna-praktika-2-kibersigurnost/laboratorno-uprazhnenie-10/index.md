---
title: "Lab 10 — Simple Transposition Encryption"
sidebar:
  label: "Lab 10"
  order: 10
---


# Simple Transposition Cipher

We choose the number of columns and write the text row by row. We then read the cells column by column. For MEETME with 3 columns, we get the rows MEE and TME; reading by columns produces MTEMEE. If the last row is incomplete, the sample solution fills the remaining cells with X. During decryption, we use the original length to remove the padding. This method is for educational purposes only.

## Application

Write encrypt(text, columns) and decrypt(cipher, columns, originalLength). Verify that, for a positive number of columns, the restored text is equal to the input. Handle an incomplete final row as well.

## Implementation Guidelines

* The number of rows can be calculated as (length + columns - 1) / columns.
* During encryption, fill the matrix row by row, then iterate through it column by column.
* During decryption, fill the matrix column by column; remember to use the original length.

## Algorithm

1. Calculate the number of rows by rounding up.
2. Fill the matrix row by row; fill any remaining cells with X.
3. Read the columns from left to right and add the characters to the result.
4. To restore the text, fill the matrix column by column, then read it row by row.
5. Return only the original number of characters.

Pseudocode:

```text id="k8vr3m"
rows = round up(length(text) / columns)
fill grid[row][column] row by row; missing cells = 'X'
for column: for row: add grid[row][column]

decryption:
fill grid column by column from the ciphertext
read grid row by row up to originalLength
```

## Sample Input and Output

```text id="p5nx9q"
Input:
Text: MEETME
Columns: 3

Matrix:
M E E
T M E

Expected output:
Transposed text: MTEMEE
Restored text: MEETME
```

## Example

Complete the TODO sections. The code is intentionally incomplete and does not represent a complete solution.

```java id="t4cw7b"
public static String encrypt(String text, int columns) {
    // TODO: create and fill a two-dimensional matrix
    return "";
}

public static String decrypt(String encrypted, int columns, int originalLength) {
    // TODO: fill the matrix column by column and read it row by row
    return "";
}
```

## Expected Result

MEETME with 3 columns is transformed into MTEMEE, and decrypting it with an original length of 6 returns MEETME. An incomplete final row is handled using padding.
