---
title: "Lab 8 — Vigenère Cipher"
sidebar:
  label: "Lab 8"
  order: 8
---


# Vigenère Cipher

In the Vigenère cipher, each letter of the key determines the shift for the corresponding letter of the text. If the text is HELLOWORLD and the key is KEY, the sequence used is KEYKEYKEYK. Letters are converted to numbers A=0..Z=25; for encryption, the values are added, while for decryption, they are subtracted. This is an educational example and is not suitable for protecting information.

## Application

Create encrypt(text, key) and decrypt(text, key), cleaning both the text and the key to A-Z. Using HELLOWORLD and KEY, verify that decryption restores the original letters.

## Implementation Guidelines

* The position in the repeating key is i % cleanKey.length().
* Before performing the modulo operation, check that the key contains at least one letter.
* When subtracting, use +26 to keep the value non-negative.

## Algorithm

1. Remove all characters that are not A-Z and convert the letters to uppercase.
2. For position i, select key[i % key.length()].
3. For encryption, add the values modulo 26.
4. For decryption, subtract the key value and add 26 before applying modulo.
5. Convert the number back to a letter.

Pseudocode:

```text id="k5mv8r"
for i from 0 to length(text)-1:
    textValue = text[i] - 'A'
    keyValue = key[i mod length(key)] - 'A'
    if encrypting: result = (textValue + keyValue) mod 26
    else: result = (textValue - keyValue + 26) mod 26
    add 'A' + result
```

## Sample Input and Output

```text id="p7nx3q"
Input:
TEXT: HELLOWORLD
KEY: KEY

Repeating key: KEYKEYKEYK
Expected ciphertext: RIJVSUYVJN
Decryption: HELLOWORLD
```

## Example

Complete the TODO sections. The code is intentionally incomplete and does not represent a complete solution.

```java id="t2cw6b"
public static String transform(String text, String key, boolean decrypt) {
    String cleanText = lettersOnly(text);
    String cleanKey = lettersOnly(key);
    if (cleanKey.length() == 0) return "";
    StringBuilder result = new StringBuilder();
    // TODO: use the key cyclically
    return result.toString();
}
```

## Expected Result

For the text HELLOWORLD and the key KEY, the ciphertext is RIJVSUYVJN. Decrypting with KEY returns HELLOWORLD.
