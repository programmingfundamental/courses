---
layout: default
title: Системен изход в JAVA
parent: Лабораторно упражнение 1
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 12
---
# Системен изход в JAVA

**System.out.print()**

За да отпечатаме текст в прозореца на конзолата, може да използваме предварително създадения метод System.out.print(). Например, ако искаме да отпечатаме текста Hello World!, изписваме:

```
System.out.print("Hello World!");
```

**System.out.println()**

Всяко последващо извикване на метода print() ще изведе текста на същия ред, както и предходния. Ако желаем текстът да се изведе на нов ред, може да бъде използван метода System.out.println(). Пример:

```
System.out.println("Hello " + yourName + "!");
```

**System.out.printf()**

Методът printf() осигурява възможност за форматиран изход в прозореца на конзолата. Извежда форматирани низове с помощта на различни спецификатори. Възможен синтаксис:

* System.out.printf(string);
* System.out.printf(format, arguments);
* System.out.printf(locale, format, arguments);

Методът връща изходния поток и приема до три параметъра в зависимост от overloading-а.

На първия ред методът приема един параметър и изпълнява същата роля, както и метода printIn().

На втория ред методът ще се използва за форматиране на изхода, където arguments ще бъде низът, който искаме да форматираме, а параметърът format определя формата. Примери:

```
System.out.printf("%s%n", "hello world!");
System.out.printf("'%S' %n", "hello world!");
System.out.printf("'%10s' %n", "Hello");
System.out.printf ("'%-10s' %n", "Hello");
System.out.printf("it is an integer: %d%n", 10000);
```

**System.out.printf()**

Форматиращи правила:

%\[flags]\[width]\[.precision]conversion-character

Някои от по-често използваните спецификатори за метода printf():

<figure><img src="https://onlineedu.tu-varna.bg/images/blog/1.6images/1.png" alt=""><figcaption></figcaption></figure>
