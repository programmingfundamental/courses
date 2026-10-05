---
title: "Lab 3 — Counting Characters, Words, and Occurrences in Text"
sidebar:
  label: "Lab 3"
  order: 3
---

# Counting Characters, Words, and Occurrences in Text

A counter is incremented when the current character satisfies a condition. To count words, we track the state using inWord: when a whitespace character is encountered, we leave the current word, while the first non-whitespace character after a separator marks the beginning of a new word. The number of substring occurrences may include overlaps: in AAA, the substring AA occurs at positions 0 and 1.

## Application

Create a program that calculates statistics for a single line of text. It should display the total number of characters, the number of ASCII letters A-Z/a-z, digits, regular spaces, words, occurrences of a selected character, and occurrences of a selected substring.

## Implementation Guidelines

* Check letters using the ranges 'A'–'Z' and 'a'–'z', and digits using '0'–'9'.
* Do not increment the word count for every character; increment it only when transitioning from a separator to a word.
* To allow overlapping occurrences, move the starting position by only one step.

## Algorithm

1. Set the counters to 0 and inWord to false.
2. Iterate through all characters and check whether each one is a letter, digit, or space.
3. Update inWord when a separator is encountered and increment the word count when a new word begins.
4. Compare the pattern at each possible position and increment the counter when a match is found.

Pseudocode:

```text id="s4mp8k"
for each character c:
    if c is a letter: letters++
    if c is a digit: digits++
    if c is a space: spaces++
    if c is whitespace: inWord = false
    else if inWord == false: words++; inWord = true
check pattern at each position and count the matches
```

## Sample Input and Output

```text id="n7ct2v"
Input:
Text: Java 17 Java
Character: a
Substring: Java

Expected output:
Total characters: 12
Letters A-Z: 8
Digits: 2
Spaces: 2
Words: 3
Occurrences of the selected character: 4
Substring occurrences (with overlaps): 2
```

## Example

Complete the TODO sections. The code is intentionally incomplete and does not represent a complete solution.

```java id="q5dr9x"
int letters = 0;
int digits = 0;
int spaces = 0;
int words = 0;
boolean inWord = false;

for (int i = 0; i < text.length(); i++) {
    char c = text.charAt(i);
    // TODO: update the counters
}
```

## Expected Result

The program displays statistics for the entire line. For the sample input, there are 12 characters, 8 letters, 2 digits, 2 spaces, and 3 words; the selected values are counted according to the input.
