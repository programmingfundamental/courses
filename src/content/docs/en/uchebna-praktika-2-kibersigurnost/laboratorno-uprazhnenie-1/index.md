---
title: "Lab 1 — Basic String Operations and Character Iteration in Java"
sidebar:
  label: "Lab 1"
  order: 1
---

# Basic String Operations and Character Iteration in Java

String stores text, while length() returns the number of characters. Indexes start at 0, so the last valid index is length() - 1. charAt(i) returns the character at position i. toUpperCase() and toLowerCase() create text with a different letter case; the original string remains unchanged. For example, CYBER has a length of 5 and charAt(0) is C.

## Application

Write a program that reads a line and displays its length, the text in uppercase and lowercase, and then each character on a separate line. Test the program with both an empty line and text containing a space.

## Implementation Guidelines

* Use Scanner.nextLine() to read spaces as well.
* The loop condition is i < text.length().
* To display a character, use text.charAt(i).

## Algorithm

1. Read an entire line as text.
2. Display its length and its uppercase and lowercase versions.
3. For each index from 0 to length() - 1, display charAt(index).

Pseudocode:

```text
text = read a line
display text.length()
for i from 0 to text.length() - 1:
    display text.charAt(i)
```

## Sample Input and Output

```text
Input:
CYBER

Expected output (abridged):
Length: 5
Uppercase: CYBER
Lowercase: cyber
Characters one by one:
C
Y
B
E
R
```

## Example

Complete the TODO sections. The code is intentionally incomplete and does not represent a complete solution.

```java
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter text: ");
        String text = scanner.nextLine();

        // TODO: display the length and both letter-case versions
        // TODO: iterate through the text and display each character
    }
}
```

## Expected Result

For CYBER, the program displays a length of 5, the lowercase version cyber, and five lines containing the characters C, Y, B, E, and R. For empty input, the loop is not executed.
