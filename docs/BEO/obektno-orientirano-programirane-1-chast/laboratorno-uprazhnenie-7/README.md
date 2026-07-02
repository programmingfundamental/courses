---
layout: default
title: Лабораторно упражнение 7
parent: Обектно-ориентирано програмиране - 1 част
has_children: true
nav_order: 7
permalink: /docs/obektno-orientirano-programirane-1-chast/laboratorno-uprazhnenie-7
---

# Лабораторно упражнение 7

# Символни низове в Java

#### Класът String

Класът String от пакета java.lang се използва за представяне и обработка на символни низове в Java.

Всеки обект от тип String представлява последователност от Unicode символи. Тъй като String е клас, променливите от този тип съдържат референции към обекти, а не самите символи.

Една от най-важните особености на класа String е, че неговите обекти са неизменими (immutable). След създаването им тяхното съдържание не може да бъде променяно. Всяка операция, която изглежда като промяна на низа, всъщност създава нов обект.

#### Неизменимост (Immutability)

При работа със символни низове трябва да се има предвид, че всички операции върху String връщат нов обект, без да променят съществуващия.

Пример:

```java
String first = "Java";
String second = first;

first = first + " Programming";

System.out.println(first);
System.out.println(second);
```

Резултат:

```java
Java Programming
Java
```

В примера промяната на променливата first не променя стойността на second, защото вместо модификация на съществуващия обект е създаден нов обект от тип String.

*Забележка:* Символните литерали се съхраняват в специална област на паметта, наречена String Pool. Ако вече съществува низ със същото съдържание, Java използва съществуващия обект вместо да създава нов.

#### Създаване на символен низ

Символните низове могат да бъдат създавани по няколко начина.

**Чрез литерал**

```java
String language = "Java";
```

**Чрез друг символен низ**

```java
String first = "Java";
String second = first;
```

**Чрез резултат от израз**

```java
String name = "Ivan";
String message = "Hello, " + name + "!";
```

#### Основни операции върху String

**Сравнение**

```java
equals()

equalsIgnoreCase()

compareTo()

compareToIgnoreCase()
```

Пример:

```java
String first = "Java";
String second = "JAVA";

System.out.println(first.equals(second));
System.out.println(first.equalsIgnoreCase(second));
```

**Търсене**

Най-често използваните методи са:

- contains()
- indexOf()
- startsWith()
- endsWith()

Пример:

```java
String text = "Introduction to Java";

System.out.println(text.contains("Java"));
System.out.println(text.indexOf("Java"));
System.out.println(text.startsWith("Intro"));
System.out.println(text.endsWith("Java"));
```

**Извличане**

```java
charAt()

substring()
```

Пример:

```java
String text = "Introduction";

System.out.println(text.charAt(0));
System.out.println(text.substring(5));
System.out.println(text.substring(5, 9));
```

**Замяна и промяна**

```java
replace()

toUpperCase()

toLowerCase()

trim()

isEmpty()

isBlank()
```

Пример:

```java
String text = " Java ";

System.out.println(text.trim());
System.out.println(text.toUpperCase());
System.out.println(text.toLowerCase());
```

**Разделяне**

```java
split()
```

Пример:

```java
String list = "dog,cat,lion";

String[] animals = list.split(",");
```

**Конкатенация**

Символните низове могат да бъдат обединявани чрез:

- оператора +;
- оператора +=;
- метода concat().

```java
String first = "Hello";
String second = " Java";

String result = first + second;
```

**Escape последователности**

Някои символи имат специално предназначение и трябва да бъдат записани чрез escape последователности.

| Последователност  | Значение                     |
|-------------------|------------------------------|
| \"                | двойна кавичка               |
| \'                | единична кавичка             |
| \\                | обратна наклонена черта      |
| \n                | нов ред                      |
| \t                | табулация                    |
| \uXXXX            | Unicode символ               |

Пример:

```java
String text = "Book \"Java\"";
```
