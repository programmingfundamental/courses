---
layout: default
title: Системен изход в JAVA
parent: Лабораторно упражнение 1
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 13
---
# Системен изход в JAVA

**System.out.print()**

За извеждане на текст в прозореца на конзолата е възможно използването на предварително създадения метод System.out.print(). Следният пример демонстрира извеждането на текста Hello World!:

```
System.out.print("Hello World!");
```

**System.out.println()**

Всяко последващо извикване на метода print() ще изведе текста на същия ред, както и предходния. При необходимост текстът да се изведе на нов ред, може да бъде използван метода System.out.println(). Пример:

```
System.out.println("Hello " + yourName + "!");
```

**System.out.printf()**

Методът printf() осигурява възможност за форматиран изход в прозореца на конзолата. Извежда форматирани низове с помощта на различни спецификатори. Възможен синтаксис:

* System.out.printf(string);
* System.out.printf(format, arguments);
* System.out.printf(locale, format, arguments);

Методът връща изходния поток и приема до три параметъра в зависимост от конкретната избрана реализация.

Посоченият на първи ред метод приема един параметър и изпълнява същата роля, както и метода printIn().

Методът от втори ред се използва за форматиране на изхода, където arguments ще бъде низът, който трябва да се форматира, а параметърът format определя формата. Примери:

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
