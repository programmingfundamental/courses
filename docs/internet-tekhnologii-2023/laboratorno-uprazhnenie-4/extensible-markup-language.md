---
layout: default
title: Extensible Markup Language
parent: Лабораторно упражнение 4
grand_parent: Интернет технологии
nav_order: 1
---

# Extensible Markup Language

XML документ се състои от елементи, като всеки елемент има начален етикет, съдържание и етикет за край. XML документа трябва да има точно един главен елемент (т.е. един етикет, който обгражда останалите тагове). XML прави разлика между малки и главни букви.

XML файлът трябва да е добре оформен. Това означава, че той трябва да се отговаря на следните условия:

* XML документ винаги започва с prolog, който описва XML файла.

XML документ винаги започва с който описва XML файла. Този пролог може да бъде минимален, например:

```
<?xml version="1.0"?>
```

Но може да съдържа и друга информация, например кодирането

```
<?xml version="1.0"?><?xml version="1.0" encoding="UTF-8" standalone="yes" ?>
```

* Всеки маркер за отваряне има затварящ етикет.

Елемент, който не затваря никое съдържание, е познат като "празен таг", например. `<flag/>`

* Всички тагове са напълно вложени.

Сравнително лесно е да се обработи XML документ в сравнение с двоичен или неструктуриран формат. Това се дължи на следните характеристики:

* XML е обикновен текст;
* XML представлява данни, без да се определя как трябва да се показват данните;
* XML може да се трансформира в други формати чрез XSL;
* XML може лесно да се обработва чрез стандартни анализатори;
* XML файловете са йерархични.

XML форматът е относително многословен, т.е. ако данните са представени като XML, размерът на тези данни е относително голям в сравнение с други формати. В Интернет JSON или двоични формати често се използват за замяна на XML, ако данните са важни.

1.3. XML елементи

| Java Class                              | XML Data Type    |
| --------------------------------------- | ---------------- |
| java.lang.String                        | xs:string        |
| java.math.BigInteger                    | xs:integer       |
| java.math.BigDecimal                    | xs:decimal       |
| java.util.Calendar                      | xs:dateTime      |
| java.util.Date                          | xs:dateTime      |
| javax.xml.namespace.QName               | xs:QName         |
| java.net.URI                            | xs:string        |
| javax.xml.datatype.XMLGregorianCalendar | xs:anySimpleType |
| javax.xml.datatype.Duration             | xs:duration      |
| java.lang.Object                        | xs:anyType       |
| java.awt.Image                          | xs:base64Binary  |
| javax.activation.DataHandler            | xs:base64Binary  |
| javax.xml.transform.Source              | xs:base64Binary  |
| java.util.UUID                          | xs:string        |

2. XML елементи се валидират с XSD схема.

Схемата съдържа описание на синтаксиса на XML файла. Имената на елементите, техните дъщерни елементи типовете данни, който могат да бъдат стойности на елементите.

Примерна схема за валидиране на XML файл с обект person

```
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified">
    <xs:element name="person">
        <xs:complexType>
            <xs:sequence>
                <xs:element ref="name"/>
                <xs:element ref="age"/>
            </xs:sequence>
        </xs:complexType>
    </xs:element>
    <xs:element name="name" type="xs:string"/>
    <xs:element name="age" type="xs:integer"/>
</xs:schema>
```

2.1. Генериране на схема в resources

- Създавате XML входен файл
- Избирате меню Tools -> XML Actions -> Generate XSD Schema from XML file
- Резултатния файл поставяте в директория resources


