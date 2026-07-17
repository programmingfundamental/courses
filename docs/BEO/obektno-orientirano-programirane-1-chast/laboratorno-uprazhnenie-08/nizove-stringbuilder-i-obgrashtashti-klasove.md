---
layout: default
title: Символни низове, StringBuilder и обгръщащи класове
parent: Лабораторно упражнение 8
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 1
---

# Символни низове, StringBuilder и обгръщащи класове


Символен низ в Java представлява последователност от символи. Най-често се използва класът `String`. За изменяемо изграждане на текст се използва `StringBuilder`. Обгръщащите класове представят примитивни стойности като обекти.

## Клас `String`

`String` е клас за работа с текст. Обектите от тип `String` са неизменими. След създаване на низ неговото съдържание не се променя. Операции като конкатенация, замяна или преобразуване връщат нов низ.

```java
String text = "Java";
String result = text + " programming";

System.out.println(text);
System.out.println(result);
```

Променливата `text` продължава да сочи към `"Java"`. Променливата `result` сочи към нов низ.

## Създаване на низ

Низ може да се създаде чрез литерал.

```java
String name = "Ivan";
```

Може да се създаде и чрез конструктор, но това не е необходимо в обичайния случай.

```java
String name = new String("Ivan");
```

Литералната форма е по-кратка и се използва за стандартно създаване на текстови стойности.

## Основни операции със `String`

```java
String text = "Java Programming";

int length = text.length();
boolean containsJava = text.contains("Java");
String upper = text.toUpperCase();
String part = text.substring(0, 4);
```

Методът `length()` връща броя символи. Методът `contains(String value)` проверява дали даден текст се съдържа в низа. Методът `toUpperCase()` връща нов низ с главни букви. Методът `substring(int start, int end)` извлича част от низа.

## Сравнение на низове

Низове се сравняват по съдържание чрез `equals()`.

```java
String first = "Java";
String second = "Java";

System.out.println(first.equals(second));
```

Операторът `==` сравнява референции, а не съдържание на обекти. Затова за проверка на текстово съдържание се използва `equals()`.

## Escape последователности

Escape последователностите позволяват запис на специални символи в низ.

```java
String line = "First line\nSecond line";
String quoted = "He said: \"Java\"";
```

Последователността `\n` означава нов ред. Последователността `\"` позволява кавичка вътре в текст.

## `StringBuilder`

`StringBuilder` е изменяем клас за изграждане на текст. Той е подходящ, когато текстът се променя многократно.

```java
StringBuilder builder = new StringBuilder();

builder.append("Java");
builder.append(" ");
builder.append("Programming");

String result = builder.toString();
```

Методът `append()` добавя текст към текущото съдържание. Методът `toString()` връща готовия резултат като `String`.

## Основни методи на `StringBuilder`

```java
StringBuilder builder = new StringBuilder("Java");

builder.append(" Language");
builder.insert(0, "The ");
builder.replace(0, 3, "A");
builder.delete(0, 2);
builder.reverse();
```

`StringBuilder` променя собственото си съдържание. Това го прави по-подходящ за цикли и натрупване на текст.

## `StringBuffer`

`StringBuffer` е подобен на `StringBuilder`, но е синхронизиран. Това означава, че е предназначен за ситуации, в които няколко нишки могат да работят с един и същ обект. При стандартни еднонишкови програми се използва `StringBuilder`.

## Обгръщащи класове

Обгръщащите класове представят примитивните типове като обекти.

| Примитивен тип | Обгръщащ клас |
| -------------- | ------------- |
| `int` | `Integer` |
| `double` | `Double` |
| `boolean` | `Boolean` |
| `char` | `Character` |
| `long` | `Long` |

Колекциите и generic типовете в Java работят с обекти, затова за примитивни стойности се използват обгръщащи класове.

```java
Integer number = 10;
Double price = 15.50;
Boolean active = true;
```

## Boxing и unboxing

Boxing е преобразуване от примитивен тип към обгръщащ клас. Unboxing е преобразуване от обгръщащ клас към примитивен тип.

```java
Integer number = 10;
int value = number;
```

В първия ред стойността `10` се обгръща в `Integer`. Във втория ред стойността се извлича обратно като `int`.

## Полезни методи

```java
int number = Integer.parseInt("123");
double price = Double.parseDouble("12.50");
boolean digit = Character.isDigit('5');
boolean letter = Character.isLetter('A');
```

Методите `parseInt()` и `parseDouble()` преобразуват текст към числова стойност. Методите на `Character` позволяват проверка на символи.
