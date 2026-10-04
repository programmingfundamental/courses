---
title: "Lab 5 — Reversing Text and Checking for Palindromes"
sidebar:
  label: "Lab 5"
  order: 5
---


# Reversing Text and Checking for a Palindrome

A reversed string is built by starting at the last index and moving toward 0. A palindrome reads the same from left to right and from right to left. To check for a palindrome, we skip characters that are not ASCII letters or digits, compare the remaining characters regardless of letter case, and move two indexes toward each other.

## Application

Implement reverse(text) and isPalindrome(text). The palindrome method should ignore letter case, spaces, and punctuation, while comparing English letters and digits.

## Implementation Guidelines

* For reverse, start the index at text.length() - 1.
* Before comparing characters, skip any characters that are not letters or digits.
* Convert a-z to A-Z using a small helper method.

## Algorithm

1. To reverse the text, iterate through the string from length()-1 to 0 and add each character.
2. To check for a palindrome, set left at the beginning and right at the end.
3. From both ends, skip characters that are not letters or digits.
4. Compare the characters regardless of letter case; if they differ, return false.
5. Continue toward the middle; if no difference is found, return true.

Pseudocode:

```text id="x8vm3q"
reverse: for i from text.length()-1 to 0: add text[i]
palindrome:
    left = 0; right = text.length()-1
    while left < right:
        skip non-letter/digit characters from both ends
        if uppercase(text[left]) != uppercase(text[right]): return false
        left++; right--
    return true
```

## Sample Input and Output

```text id="p4kn7c"
Inputs and expected output:
LEVEL -> Reversed text: LEVEL; Palindrome
JAVA -> Reversed text: AVAJ; Not a palindrome
A man, a plan, a canal: Panama -> Palindrome
```

## Example

Complete the TODO sections. The code is intentionally incomplete and does not represent a complete solution.

```java id="w6rt2m"
public static String reverse(String text) {
    StringBuilder result = new StringBuilder();
    // TODO: add the characters from last to first
    return result.toString();
}

public static boolean isPalindrome(String text) {
    // TODO: use left and right
    return false;
}
```

## Expected Result

LEVEL and the phrase A man, a plan, a canal: Panama are recognized as palindromes. JAVA is not a palindrome, and its reversed text is AVAJ.
