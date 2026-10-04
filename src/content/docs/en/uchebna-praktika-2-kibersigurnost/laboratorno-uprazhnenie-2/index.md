---
title: "Lab 2 — Searching for Characters and Substrings in Text"
sidebar:
  label: "Lab 2"
  order: 2
---

# Searching for a Character and a Substring in Text

Linear search checks each character sequentially until the target character is found. To search for a substring, we start at each possible position and compare the following characters. If all of them match, we return the starting index; if the search finishes without a match, we return -1. indexOf() is a built-in method that can be used for this purpose, but here the algorithm is implemented manually.

## Application

Write a method findCharacter(text, target) that returns the position of the first occurrence, and a method findSubstring(text, pattern) that implements a custom substring search. In main, read the three input values and display the results.

## Implementation Guidelines

* Indexes start at 0.
* When comparing a substring, use an inner while loop and an offset counter.
* In the given solution, an empty pattern is considered to be found at position 0.

## Algorithm

1. Iterate through the string from left to right and compare each character with the target.
2. For a substring, check each starting position where the entire pattern can fit.
3. Compare the characters of the pattern one by one.
4. Return the first matching position or -1.

Pseudocode:

```text id="f3gk2m"
for i from 0 to length(text) - 1:
    if text[i] == target: return i
for start from 0 to length(text) - length(pattern):
    compare all characters of pattern with text starting at start
    if all match: return start
return -1
```

## Sample Input and Output

```text id="v7qn4b"
Input:
Text: BANANA
Character: N
Substring: ANA

Expected output:
Character position: 2
Substring position: 1
-1 means that there is no match.
```

## Example

Complete the TODO sections. The code is intentionally incomplete and does not represent a complete solution.

```java id="r8pk5c"
public static int findCharacter(String text, char target) {
    // TODO: return the index or -1
    return -1;
}

public static int findSubstring(String text, String pattern) {
    // TODO: compare the pattern at each possible starting position
    return -1;
}
```

## Expected Result

For BANANA, the methods return 2 for the first N and 1 for the first ANA. If the character or substring is not found, the result is -1.
