---
title: "Lab 12 — Mini Project: Text & Crypto Toolkit"
sidebar:
  label: "Lab 12"
  order: 12
---

# Mini Project “Text & Crypto Toolkit”

The menu displays the available actions and repeats until the user selects 0. Each algorithm is implemented in a separate method, such as normalizeText() or caesarEncrypt(). This way, main() controls the program flow, while the methods perform the specific processing tasks. The project remains basic: a single Main class, with no class hierarchies or external libraries.

## Application

Create a console application with the following menu: 1 text analysis, 2 normalization, 3 reversing text, 4 palindrome check, 5 frequency analysis, 6/7 Caesar encrypt/decrypt, 8/9 Vigenere encrypt/decrypt, 10 XOR, 11 Caesar brute force, and 0 Exit. Each algorithm should be implemented as a separate method. Use the versions from the previous solutions.

## Implementation Guidelines

* Read the user's choice using nextLine() to avoid leaving newline characters in the Scanner.
* Use a main while loop and separate branches for the different choices.
* Use separate methods with meaningful names.
* Check for an empty Vigenere key and an invalid menu choice.

## Algorithm

1. Display the menu and read the choice as a line.
2. If the choice is 0, exit the loop.
3. For any other valid choice, read the required data and call the corresponding method.
4. Display the result and return to the menu.
5. Handle an unknown choice with a short message.

Pseudocode:

```text id="k6mv3r"
running = true
while running:
    display menu
    choice = read a line
    if choice == "0": running = false
    else if choice == "1": call analyzeText()
    ...
    else: display unknown command
display end of program
```

## Sample Input and Output

```text id="p8nx5q"
Sample session:
=========================
 TEXT & CRYPTO TOOLKIT
=========================
1. Text analysis
2. Normalization
...
0. Exit
Choice: 2
Text: Java, Security!
JAVASECURITY
Choice: 0
Program terminated.
```

## Example

Complete the TODO sections. The code is intentionally incomplete and does not represent a complete solution.

```java id="t4cw9b"
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            // TODO: display the menu and read the choice
            // TODO: call the corresponding method
        }
    }

    public static String normalizeText(String text) {
        // TODO
        return "";
    }
}
```

## Expected Result

The application executes the selected algorithm, displays the result, and then displays the menu again. When 0 is selected, it terminates with a short message. The code is contained in a single clas
