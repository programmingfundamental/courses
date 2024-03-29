---
layout: default
title: Упражнения за извънаудиторна заетост
parent: Лабораторно упражнение 11
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 1
---
# Упражнения за извънаудиторна заетост

Да се създаде програма за книжарница.

\


Предвидете програмата да продуцира изключение InvalidDataException с подходящо съобщение. Приложете по усмотрение.

Ще ви е небходим интерфейс CoverType с метод isHardCover(), който връща булева стойност.

Абстрактен клас Person:

* Атрибути: firstName, lastName (определете модификаторите за достъп);
* Конструктор – според употребата;
* Акселератори: според модификаторите и употребата;
* Методи: equals, hashCode, toString.

Клас Author:

* Наследява Person;
* Атрибути: country, genre (определете модификаторите за достъп);
* Конструктор – според употребата;
* Акселератори: според модификаторите и употребата;
* Методи: equals, hashCode, toString.

Клас Book:

* Имплементира интерфейс CoverType;
* Атрибути: title, author, publishingYear, quantity (по-голямо от 5), price (по-голяма от 9,99) (определете модификаторите за достъп);
* Конструктор – според употребата;
* Акселератори: според модификаторите и употребата;
* Методи: : equals, hashCode, toString;
* Интерфейсният метод връща истина, ако книгата е издадена преди 2000 година и има цена над 14лв.

Клас BookStore:

* Атрибути:  име на книжарницата и колекция от уникални книги (определете модификаторите за достъп);
* Експлицитен конструктор по име на файл, в който се съдържа информация за книгите;

Примерен формат на файла:

„Под игото“ Иван Вазов България класика 1985 12 15,0

„На изток от Рая“ Джон Стайнбек САЩ класика 1998 18 12,58

...

* Методи:
* За добавяне на книга към колекцията;
* За намиране и връщане на цената на наличните книги;
* За намиране и връщане на средната цена на книги от зададен жанр;
* За намиране и връщане на автора с най-много книги;
* За намиране и връщане на броя уникални автори, публикували книга след зададена година;
* За връщане на книгите, подредени по националността на автора;
* За намиране и връщане на броя книги с твърди корици;
* toString.

Клас Main с главна функция:

* създайте обект BookStore с име на файл, в който има поне 3 различни автора с минимум една книга;
* добавете нови книги в книжарницата;
* запишете в нов файл:
* цената на всички книги;
* средната цена на книгите от жанр „Класика“;
* името на автора с най-много книги;
* колко са авторите с книги, издадени след 1990 година;
* книгите, сортирани по националност на автора;
* броя книги с твърди корици.


