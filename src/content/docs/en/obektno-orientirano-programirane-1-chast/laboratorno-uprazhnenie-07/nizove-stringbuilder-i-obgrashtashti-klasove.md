---
title: Strings: Immutable and Mutable. Wrapper Classes
sidebar:
  order: 1
---

# Strings: Immutable and Mutable. Wrapper Classes

A Java string is a sequence of characters. The `String` class is used for text, `StringBuilder` for mutable text construction, and wrapper classes represent primitive values as objects.

In the original title, “static” refers to immutable strings (`String`) and “dynamic” to mutable text construction (`StringBuilder`). The precise Java terms are **immutable** and **mutable**. This is unrelated to the `static` modifier: both kinds of objects are created at runtime.

## The `String` class

`String` objects are immutable: their contents cannot change after creation. Operations such as concatenation, replacement, or case conversion return a new string.

```java
String text = "Java";
String result = text + " programming";

System.out.println(text);
System.out.println(result);
```

`text` still refers to `"Java"`; `result` refers to a new string.

## Creating strings

Strings are usually created with literals:

```java
String name = "Ivan";
```

A constructor can also be used, although it is unnecessary in ordinary cases:

```java
String name = new String("Ivan");
```

A string can be assigned from another variable or built from an expression:

```java
String first = "Java";
String second = first;
String message = "Hello, " + first + "!";
```

## Common `String` operations

```java
String text = "Java Programming";

int length = text.length();
boolean containsJava = text.contains("Java");
String upper = text.toUpperCase();
String part = text.substring(0, 4);
```

`length()` returns the number of characters. `contains(String value)` checks whether text occurs in the string. `toUpperCase()` returns an uppercase string. `substring(int start, int end)` extracts a portion.

## Searching in a `String`

```java
String text = "Java Programming";

boolean containsJava = text.contains("Java");
int firstIndex = text.indexOf("a");
int lastIndex = text.lastIndexOf("a");
boolean startsWithJava = text.startsWith("Java");
boolean endsWithIng = text.endsWith("ing");
```

`contains()` returns a boolean. `indexOf()` returns the first matching index, or `-1` if the value was not found.

## Extracting from a `String`

```java
String text = "Java Programming";

String firstWord = text.substring(0, 4);
char firstLetter = text.charAt(0);
```

`substring(int start, int end)` includes the start index and excludes the end index. `charAt(int index)` returns the character at the specified index.

## Replacing and changing a `String`

Because `String` is immutable, methods that appear to change it return a new string.

```java
String text = "Java Programming";

String replaced = text.replace("Java", "Kotlin");
String lower = text.toLowerCase();
String trimmed = " Java ".trim();
```

The original string remains unchanged. Assign the result to a variable if it is needed later.

## Splitting and concatenation

`split()` divides a string into an array of strings.

```java
String names = "Ivan,Petar,Maria";
String[] parts = names.split(",");
```

Concatenation combines text values:

```java
String firstName = "Ivan";
String lastName = "Petrov";
String fullName = firstName + " " + lastName;
```

For many successive text changes, prefer `StringBuilder`.

## Comparing strings

Use `equals()` to compare string contents.

```java
String first = "Java";
String second = "Java";

System.out.println(first.equals(second));
```

The `==` operator compares references, not object contents. Use `equals()` to compare text.

## `==` and `equals()` with objects

For primitive types, `==` compares values:

```java
int first = 10;
int second = 10;

System.out.println(first == second);
```

For reference types, `==` checks whether two variables refer to the same object.

```java
String first = new String("Java");
String second = new String("Java");

System.out.println(first == second);
System.out.println(first.equals(second));
```

The first comparison is `false` because the variables refer to two different objects. The second is `true` because their string contents are equal.

## Escape sequences

Escape sequences allow special characters to be written in a string.

```java
String line = "First line\nSecond line";
String quoted = "He said: \"Java\"";
```

`\n` represents a new line. `\"` allows a quotation mark inside a string.

## `StringBuilder`

`StringBuilder` is mutable and is useful when text must be changed repeatedly.

```java
StringBuilder builder = new StringBuilder();

builder.append("Java");
builder.append(" ");
builder.append("Programming");

String result = builder.toString();
```

`append()` adds text to the current contents. `toString()` returns the result as a `String`.

## Mutable and immutable objects

An immutable object's state cannot change after it is created. `String` is immutable. Concatenation creates a new string instead of changing the existing one.

```java
String text = "Java";
text = text + " language";
```

After the second line, `text` refers to a new object.

A mutable object's state can change after creation. `StringBuilder` is mutable.

```java
StringBuilder builder = new StringBuilder("Java");
builder.append(" language");
```

`append()` changes the existing `StringBuilder` object.

## Common `StringBuilder` methods

```java
StringBuilder builder = new StringBuilder("Java");

builder.append(" Language");
builder.insert(0, "The ");
builder.replace(0, 3, "A");
builder.delete(0, 2);
builder.reverse();
```

`StringBuilder` changes its own contents, which makes it suitable for loops and accumulating text.

| Method | Purpose |
| --- | --- |
| `append()` | Adds a value at the end |
| `insert()` | Inserts a value at a given position |
| `delete()` | Deletes part of the contents |
| `replace()` | Replaces part of the contents |
| `charAt()` | Returns the character at an index |
| `length()` | Returns the current length |
| `capacity()` | Returns the internal buffer size |
| `toString()` | Returns the result as a `String` |

## Comparing `String` and `StringBuilder`

| Property | `String` | `StringBuilder` |
| --- | --- | --- |
| Contents | Immutable after creation | Can be changed |
| Adding text | Concatenation returns a string | `append()` changes the current object |
| Best suited for | Finished text, keys, messages | Building text gradually, especially in a loop |
| Result | Already a `String` | `toString()` returns a `String` |

Choose according to how the text is used. Concatenation is sufficient for a short message; use `StringBuilder` when accumulating text repeatedly in a loop.

## ASCII and Unicode

Java represents characters using Unicode. A `char` stores one Unicode character.

ASCII is an older standard with a limited set of characters, mainly Latin letters, digits, and control characters. Unicode supports a much larger set of characters from different languages.

```java
char latinLetter = 'A';
char greekLetter = 'Ω';
```

Both characters fit in a `char`, because Java uses Unicode.

## Wrapper classes

Wrapper classes represent primitive types as objects.

| Primitive type | Wrapper class |
| --- | --- |
| `boolean` | `Boolean` |
| `char` | `Character` |
| `byte` | `Byte` |
| `short` | `Short` |
| `int` | `Integer` |
| `long` | `Long` |
| `float` | `Float` |
| `double` | `Double` |

Java collections and generic types work with objects, so wrapper classes are used for primitive values.

```java
Integer number = 10;
Double price = 15.50;
Boolean active = true;
```

Wrappers are useful when primitive values must be used as objects, for example in collections, generic types, conversion or comparison methods, and when `null` represents a missing value where permitted by the logic.

## Boxing and unboxing

Boxing converts a primitive value to a wrapper object. Unboxing converts a wrapper object to a primitive value.

```java
Integer number = 10;
int value = number;
```

In the first line, `10` is wrapped in an `Integer`. In the second, the value is extracted as an `int`.

## Useful conversion methods

```java
int number = Integer.parseInt("123");
double price = Double.parseDouble("12.50");
boolean digit = Character.isDigit('5');
boolean letter = Character.isLetter('A');
```

`parseInt()` and `parseDouble()` convert text to numeric primitive values. `Character` methods can inspect characters.

`valueOf()` returns an object of the corresponding wrapper class:

```java
Integer number = Integer.valueOf("123");
Double price = Double.valueOf("12.50");
```

If the text is not a valid number, a `NumberFormatException` is thrown. This exception is covered in the lesson on exception handling.

| Method | Purpose |
| --- | --- |
| `valueOf()` | Creates an object from a primitive value or string |
| `parseInt()` | Converts a string to `int` |
| `parseDouble()` | Converts a string to `double` |
| `intValue()` | Returns the value as `int` |
| `doubleValue()` | Returns the value as `double` |
| `toString()` | Returns a textual representation |

## Character checks in wrapper classes

`Character` includes methods for checking characters.

```java
boolean digit = Character.isDigit('5');
boolean letter = Character.isLetter('A');
boolean whitespace = Character.isWhitespace(' ');
```

These methods return `true` or `false` depending on the character.

| Method | Purpose |
| --- | --- |
| `Character.isDigit()` | Checks whether a character is a digit |
| `Character.isLetter()` | Checks whether a character is a letter |
| `Character.isLetterOrDigit()` | Checks whether a character is a letter or digit |
| `Character.isWhitespace()` | Checks whether a character is whitespace |
| `Character.isUpperCase()` | Checks whether a character is uppercase |
| `Character.isLowerCase()` | Checks whether a character is lowercase |

## Comparison methods in wrapper classes

Wrapper classes provide methods for comparing values.

```java
int result = Integer.compare(10, 20);
int max = Integer.max(10, 20);
int min = Integer.min(10, 20);
```

`Integer.compare(first, second)` returns a negative value, zero, or a positive value depending on whether the first value is less than, equal to, or greater than the second.

| Method | Purpose |
| --- | --- |
| `equals()` | Checks whether two values are equal |
| `compare()` | Compares two values |
| `compareTo()` | Compares this object with another object of the same type |

## The `Math` class

`Math` contains static methods for common mathematical operations. You do not create a `Math` object; call methods using the class name.

```java
int absolute = Math.abs(-10);
double power = Math.pow(2, 3);
double root = Math.sqrt(25);
```

`Math.abs(-10)` returns the absolute value. `Math.pow(2, 3)` raises 2 to the third power. `Math.sqrt(25)` returns the square root.

## Common `Math` methods

| Method | Purpose |
| --- | --- |
| `Math.abs(value)` | Returns the absolute value |
| `Math.max(first, second)` | Returns the larger value |
| `Math.min(first, second)` | Returns the smaller value |
| `Math.pow(base, exponent)` | Raises a value to a power |
| `Math.sqrt(value)` | Returns the square root |
| `Math.round(value)` | Rounds to the nearest integer |
| `Math.ceil(value)` | Rounds up |
| `Math.floor(value)` | Rounds down |

## Constants in `Math`

`Math` also provides common mathematical constants.

```java
double circleArea = Math.PI * radius * radius;
double e = Math.E;
```

`Math.PI` represents π. `Math.E` represents the base of the natural logarithm.

## The `Random` class

`Random` generates pseudorandom values. Unlike with `Math`, you create an object.

```java
import java.util.Random;

Random random = new Random();

int number = random.nextInt();
boolean flag = random.nextBoolean();
double value = random.nextDouble();
```

Values are pseudorandom because they are produced by an algorithm. Each call returns the next value in the sequence.

## Generating a number in a range

`nextInt(int bound)` returns an integer from 0 inclusive to the bound exclusive.

```java
Random random = new Random();
int number = random.nextInt(10);
```

The possible values are 0 through 9. Add 1 to get a range from 1 through 10:

```java
int number = random.nextInt(10) + 1;
```

To generate a value from `min` through `max`, inclusive:

```java
int number = random.nextInt(max - min + 1) + min;
```

## Common `Random` methods

| Method | Purpose |
| --- | --- |
| `nextInt()` | Returns a random `int` |
| `nextInt(bound)` | Returns an `int` from 0 through `bound - 1` |
| `nextDouble()` | Returns a `double` from 0.0 inclusive to 1.0 exclusive |
| `nextBoolean()` | Returns `true` or `false` |
