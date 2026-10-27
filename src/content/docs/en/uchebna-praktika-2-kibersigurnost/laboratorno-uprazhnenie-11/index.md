---
title: "Lab 11 — Breaking the Caesar Cipher with Brute Force and Frequency Analysis"
sidebar:
  label: "Lab 11"
  order: 11
---

# Breaking the Caesar Cipher with Brute Force and Frequency Analysis

The Caesar cipher has only 25 non-zero keys to test. Brute force means trying every key and displaying the result. As an additional hint, we find the most frequent letter in the ciphertext and tentatively assume that it corresponds to E. This assumption may be incorrect, especially for short texts or texts in a different language; a person should examine all variants.

## Application

Write a program that displays all 25 decrypted variants for an input text. Then count the letters, find the most frequent one, and display a suggested key based on the simplified assumption that the most frequent letter in the original text is E.

## Implementation Guidelines

* Use decrypt from Exercise 7.
* In the frequency array, the position of a letter is c - 'A'.
* The formula for the approximate key uses a difference modulo 26.
* Do not present the hint as a certain automatic answer.

## Algorithm

1. For each key from 1 to 25, decrypt the text and display the result.
2. Count the letter frequencies in the ciphertext.
3. Find the most frequent letter.
4. Calculate the suggested key assuming that the most frequent letter was E in the original text.
5. Compare the hint with all displayed variants and identify the meaningful text.

Pseudocode:

```text id="h8kv4m"
for key from 1 to 25:
    display key and decrypt(cipher, key)
frequency = frequencies(cipher)
common = most frequent letter
suggestedKey = (common - 'E' + 26) mod 26
display the hint and compare it with the variants
```

## Sample Input and Output

```text id="p5nx2r"
Input:
KHOOR

Among the results:
Key 3 -> HELLO

The frequency-based hint is only a guideline and may not be reliable for short text.
```

## Example

Complete the TODO sections. The code is intentionally incomplete and does not represent a complete solution.

```java id="t7cw9b"
for (int key = 1; key <= 25; key++) {
    // TODO: display the key and decrypt(cipher, key)
}

int[] frequency = frequencyAnalysis(cipher);
// TODO: find the most frequent letter and display a suggested key
```

## Expected Result

For KHOOR, 25 possible variants are displayed, including Key 3 -> HELLO. The frequency analysis displays the most frequent letter and an approximate key suggestion while explaining its limitations.
