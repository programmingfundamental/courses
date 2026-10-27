---
title: "Lab 9 — XOR Text Transformation"
sidebar:
  label: "Lab 9"
  order: 9
---


# XOR Text Transformation

XOR compares the bits of two numbers: the resulting bit is 1 when the bits are different, and 0 when they are the same. For example, 5 (0101) XOR 3 (0011) = 6 (0110). In Java, the operator is ^. An important property is (A XOR K) XOR K = A: applying the same key a second time restores the original value. Some resulting characters may be non-printable, so the example displays numerical codes. This is an educational transformation, not a method for real data protection.

## Application

Write a console program that reads text and an integer, creates an array of XOR values, displays them as numbers, and applies XOR with the same key to restore the text.

## Implementation Guidelines

* The expression for each character is text.charAt(i) ^ key.
* Use an int[] with the same length as the text.
* To display a character, convert the value using (char).

## Algorithm

1. For each character, obtain its numerical value.
2. Calculate value XOR key and store the result in an array.
3. Display all resulting numbers.
4. Apply XOR with the same key to each value and convert it back to char.

Pseudocode:

```text id="m8qv4k"
for each character c:
    encoded[i] = c XOR key
for each value in encoded:
    original = value XOR key
    add (char) original to the restored text
```

## Sample Input and Output

```text id="p3nx7r"
Example operation:
5 XOR 3 = 6

Program input:
Text: A
Key: 7

Expected output:
XOR values: 70
Applying XOR again restores: A
```

## Example

Complete the TODO sections. The code is intentionally incomplete and does not represent a complete solution.

```java id="t6cw2b"
public static int[] xorToNumbers(String text, int key) {
    int[] values = new int[text.length()];
    // TODO: store the value of char XOR key at each position
    return values;
}

public static String xorNumbersToText(int[] values, int key) {
    // TODO: apply the same key and build the text
    return "";
}
```

## Expected Result

For the character A (code 65) and key 7, the program displays 70. Applying XOR again produces the original code 65 and restores A.
