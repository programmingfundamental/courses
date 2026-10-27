---
title: "Lab 7 — Caesar Cipher"
sidebar:
  label: "Lab 7"
  order: 7
---


# Caesar Cipher

The Caesar cipher shifts each letter by the same key. With a key of 3, H has a value of 7 and becomes K with a value of 10. After Z, the sequence continues from A using the remainder after division by 26. The example works only with A-Z: the program normalizes the input and skips spaces and punctuation. For decryption, we apply the shift in the opposite direction.

## Application

Implement lettersOnly(text), encrypt(text, key), and decrypt(text, key). Display the ciphertext and then decrypt the same text. The program accepts English letters A-Z and returns letters only.

## Implementation Guidelines

* Convert the text to A-Z before encryption.
* Normalize the key if it is negative or greater than 26.
* Shifting backward can be implemented as encrypt(text, -key).

## Algorithm

1. Normalize the text to uppercase letters A-Z only.
2. Convert each letter to a number using c - 'A'.
3. Add the normalized key and take the remainder after division by 26.
4. Convert the number back to a letter.
5. For decryption, use a negative shift.

Pseudocode:

```text id="d7kp4m"
for each letter c:
    value = c - 'A'
    shifted = (value + key) mod 26
    add 'A' + shifted
for decryption use key -key
```

## Sample Input and Output

```text id="h3nx8q"
Input:
Text: HELLO
Key: 3

Expected output:
Encrypted text: KHOOR
Decrypted text: HELLO
```

## Example

Complete the TODO sections. The code is intentionally incomplete and does not represent a complete solution.

```java id="r9vt2c"
public static String encrypt(String text, int key) {
    StringBuilder result = new StringBuilder();
    // TODO: calculate the cyclic shift for each letter A-Z
    return result.toString();
}

public static String decrypt(String text, int key) {
    // TODO: reverse the shift
    return "";
}
```

## Expected Result

HELLO with a key of 3 produces KHOOR, and decrypting it with a key of 3 returns HELLO. The input is processed as English letters A-Z.
