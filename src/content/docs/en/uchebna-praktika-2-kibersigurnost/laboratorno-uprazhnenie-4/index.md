---
title: "Lab 4 — Text Transformation and Normalization"
sidebar:
  label: "Lab 4"
  order: 4
---


# Text Transformation and Normalization

Normalization here means keeping only the letters A-Z and converting them to uppercase. For example, Java, Security! becomes JAVASECURITY. We iterate through the input; a lowercase letter is converted using the difference between 'a' and 'A', and it is then added only if it is within the range 'A'–'Z'. StringBuilder is convenient for building the result.

## Application

Write a method normalizeText(text) that removes spaces and punctuation marks and returns only uppercase English letters A-Z. Display the result for input entered by the user.

## Implementation Guidelines

* Process one char at a time.
* First convert lowercase letters, then check whether the character is within the A-Z range.
* Add characters using StringBuilder.append(c).

## Algorithm

1. Create an empty StringBuilder.
2. For each character, convert a-z to A-Z.
3. If the result is a letter from A-Z, add it; otherwise, skip it.
4. Return the resulting string.

Pseudocode:

```text id="k7mt3p"
result = empty StringBuilder
for each character c in text:
    if 'a' <= c <= 'z': c = c - 'a' + 'A'
    if 'A' <= c <= 'Z': add c to result
return result as String
```

## Sample Input and Output

```text id="b4nx8q"
Input:
Java, Security!

Expected output:
JAVASECURITY
```

## Example

Complete the TODO sections. The code is intentionally incomplete and does not represent a complete solution.

```java id="r2cw6v"
public static String normalizeText(String text) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < text.length(); i++) {
        char c = text.charAt(i);
        // TODO: convert lowercase letters
        // TODO: add only A-Z
    }
    return result.toString();
}
```

## Expected Result

The input Java, Security! produces JAVASECURITY. The spaces, comma, and exclamation mark are not included in the result.
