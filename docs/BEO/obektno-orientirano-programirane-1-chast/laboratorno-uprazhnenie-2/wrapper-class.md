---
layout: default
title: Wrapper Class
parent: Лабораторно упражнение 2
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 3
---

# Wrapper Class

Обгръщащите класове представляват специални класове от стандартната библиотека на Java, които предоставят обектно представяне на примитивните типове данни.

Всеки примитивен тип има съответстващ обгръщащ клас.

| Примитивен тип | Обгръщащ клас |
| -------------- | ------------- |
| boolean        | Boolean       |
| char           | Character     |
| byte           | Byte          |
| short          | Short         |
| int            | Integer       |
| long           | Long          |
| float          | Float         |
| double         | Double        |

## Необходимост от използване на обгръщащи класове

Обгръщащите класове позволяват примитивните типове данни да бъдат използвани като обекти.

Това е необходимо при:

- работа с колекции (ArrayList, LinkedList, HashMap и др.), които съхраняват само обекти;
- използване на методите, предоставени от съответния обгръщащ клас;
- работа с генерични типове (Generics).

## Полезни методи на обгръщащите класове

Обгръщащите класове предоставят множество готови методи за преобразуване, проверка и обработка на данни.

## Методи за преобразуване

| Метод         | Предназначение                               |
| ------------- | -------------------------------------------- |
| valueOf()     | Създава обект от примитивна стойност или низ |
| parseInt()    | Преобразува символен низ в int               |
| parseDouble() | Преобразува символен низ в double            |
| intValue()    | Връща стойността като int                    |
| doubleValue() | Връща стойността като double                 |
| toString()    | връща текстово представяне на стойността     |

Примери:

```java
String text = "125";

int number = Integer.parseInt(text);

System.out.println(number);
```
```java
double price = Double.parseDouble("12.75");

System.out.println(price);
```
```java
Integer number = 25;

System.out.println(number.doubleValue());
```
```java
int number = 50;

String text = Integer.toString(number);

System.out.println(text);
```


## Методи за проверка

| Метод                       | Предназначение                               |
| --------------------------- | -------------------------------------------- |
| Character.isDigit()         | Проверява дали символът е цифра.             |
| Character.isLetter()        | Проверява дали символът е буква.             |
| Character.isLetterOrDigit() | Проверява дали символът е буква или цифра.   |
| Character.isWhitespace()    | Проверява дали символът е интервален символ. |
| Character.isUpperCase()     | Проверява дали символът е главна буква.      |
| Character.isLowerCase()     | Проверява дали символът е малка буква.       |

Примери:

```java
char symbol = '7';

System.out.println(Character.isDigit(symbol));
```
```java
char symbol = 'A';

System.out.println(Character.isLetter(symbol));
System.out.println(Character.isUpperCase(symbol));
```
```java
char symbol = ' ';

System.out.println(Character.isWhitespace(symbol));
```

## Методи за сравнение

| Метод       | Предназначение                                  |
| ----------- | ----------------------------------------------- |
| equals()    | Проверява две стойности за равенство            |
| compare()   | Сравнява две стойности                          |
| compareTo() | Сравява текущия обект с друг обект от същия тип |

Примери:

```java
Integer first = 10;
Integer second = 10;

System.out.println(first.equals(second));
```
Полученият резултат ще бъде:
```java
true
```

```java
System.out.println(Integer.compare(5, 10));
System.out.println(Integer.compare(10, 5));
System.out.println(Integer.compare(5, 5));
```
Резултатът ще бъде както следва:
```java
-1 // първият операнд е по-малък
1  // вторият операнд е по-малък
0  // двата операнда са с еднакви стойности
```

```java
Integer first = 15;
Integer second = 20;

System.out.println(first.compareTo(second));
```
Резултатът от последния пример ще бъде -1.


## Boxing и Unboxing

Java предоставя механизъм за преобразуване между примитивни типове и обекти от съответните обгръщащи класове.

- **Boxing** – преобразуване на примитивен тип в обект.
- **Unboxing** – преобразуване на обект в примитивен тип.

От Java 5 тези операции могат да се извършват автоматично:

- **Autoboxing** – автоматично преобразуване на примитивен тип в обект.
- **Auto-unboxing** – автоматично преобразуване на обект в примитивен тип.

Пример:

```java
int number = 25;

Integer integerObject = Integer.valueOf(number); // Boxing
Integer autoBox = number;                        // Autoboxing

int primitiveValue = integerObject.intValue();   // Unboxing
int autoUnbox = integerObject;                   // Auto-unboxing
```

