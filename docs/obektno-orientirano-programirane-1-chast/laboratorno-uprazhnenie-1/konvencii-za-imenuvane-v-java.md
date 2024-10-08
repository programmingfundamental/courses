---
layout: default
title: Конвенции за именуване в Java
parent: Обектно-ориентирано програмиране - 1 част
has_children: true
nav_order: 2
permalink: /docs/obektno-orientirano-programirane-1-chast/laboratorno-uprazhnenie-1
---

# Конвенции за именуване в Java

**Пакети**

\-         Base package - използвайте значещи имена на пакети, за целите на ООП ще ползваме пакета bg.tu\_varna.sit

**Класове**

\-          За именуването на класове се използват **съществителни имена**, които трябва да описват основната цел на класа (ReverseString).

\-           Трябва ясно да показва какво има в тялото на класа.

\-           Трябва да започват **с главна буква** и не е добра практика да се използват съкратени думи и абревиатури.

\-          Всяка нова дума започва с главна буква (**CamelCase**).

\-          Класовете задължително трябва да бъдат в пакет/и.

**Методи**

\-          Методите извършват действие, следователно за именуването им трябва да се използват **глаголи**.

\-          Първата буква е **малка**, всяка следваща дума започва с **главна** буква.

**Променливи**

\-          Имената на променливите трябва да са **кратки** и достатъчно **значими**.

\-          Могат да започват с „\_“ и „$“.

\-          Променливи с имена от един символ трябва да се избягват, освен ако не се използват за **временни** променливи.

\-          Често срещани имена за променливи са i, j, k, m и n за целочислени стойности, c, d и e за символи.

|Тип идентификатор|Правила|Примери|
|---|---|---|
|Пакет|Имената на пакетите трябва да са уникални и изписани изцяло с малки букви. Префиксът трябва да бъде едно от следните домейн имена: com, edu, gov, mil, net или org.  |	com.sun.eng|
|Клас|Имената на класовете трябва да са съществителни и да започват с главна буква. В случай на име с повече от една дума, то се изписва слято като всяка нова дума започва с главна буква. Името на класа трябва да е подходящо подбрано и да е информативно, като по възможност се избягват абревиатури.|	class Person <br>class ImageEditor|
|Интерфейс|	За имената на интерфейсите важат същите правила както при класовете.|	interface Calculator|
|Метод|	Имената на методите трябва да бъдат глаголи и да започват с малка буква. Ако името е с повече от една дума, първата от тях започва с малка буква, а всяка следваща – с главна.|	run() <br>getRate() addAverageScore()|
|Променлива|	Имената на променливите трябва да започват с малка буква и да са информативни. |	int age; <br> double basePrice;|
|Константа| 	Имената на константите се изписват изцяло с главни букви. Ако името съдържа повече от една дума, думите се разделят със символа “_”	|static final MIN_AGE = 18|
