---
title: "Lab 6 — Character Frequency Analysis"
sidebar:
  label: "Lab 6"
  order: 6
---


# Character Frequency Analysis

The frequency array has 26 positions: 0 corresponds to A, 1 to B, and so on up to 25 for Z. After converting a lowercase letter, we check whether the character is within A-Z and increment frequency[c - 'A']. This allows a single structure to store the counts for all letters.

## Application

Write a program that reads text and displays a frequency table for all letters A-Z. Lowercase letters should be counted together with their corresponding uppercase letters, while spaces, digits, and punctuation should be ignored.

## Implementation Guidelines

* Use exactly 26 elements.
* The index of A is 0, and the index of B is 1.
* The letter corresponding to index i can be obtained using (char)('A' + i).

## Algorithm

1. Create int[] frequency = new int[26].
2. Iterate through each character and convert a-z to A-Z.
3. If the character is within A-Z, increment the frequency at index c - 'A'.
4. Iterate through the array and display each letter and its count.

Pseudocode:

```text id="m4vk7q"
frequency = array of 26 zeros
for each character c:
    if c is a lowercase letter: convert it to uppercase
    if c is between A and Z: frequency[c - 'A']++
for i from 0 to 25:
    display ('A' + i) and frequency[i]
```

## Sample Input and Output

```text id="t8nr2p"
Input:
BANANA

Output (non-zero entries):
A -> 3
B -> 1
N -> 2

The program's table also includes C -> 0 through Z -> 0.
```

## Example

Complete the TODO sections. The code is intentionally incomplete and does not represent a complete solution.

```java id="c6pw9x"
int[] frequency = new int[26];

for (int i = 0; i < text.length(); i++) {
    char c = text.charAt(i);
    // TODO: convert a lowercase letter
    // TODO: if c is A-Z, increment frequency[c - 'A']
}

// TODO: display A-Z and the corresponding values
```

## Expected Result

For BANANA, the table contains A -> 3, B -> 1, N -> 2, while all other letters have a frequency of 0. The result is the same for banana
