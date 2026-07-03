---
layout: default
title: Builder
parent: Лабораторно упражнение 3
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 1
---

# Builder

### Проблем

При разработването на софтуер често се налага създаването на обекти, които съдържат голям брой свойства. Обикновено само част от тях са задължителни, а останалите имат незадължителен характер.

Използването на конструктор с много параметри затруднява четимостта на кода и увеличава вероятността от грешки при подаване на аргументите. От друга страна, създаването на множество претоварени конструктори води до усложняване на реализацията и затруднява нейното поддържане.

### Решение

Шаблонът **Builder** отделя процеса по създаване на обекта от неговото представяне. При реализацията с вътрешен статичен клас помощният клас Builder съдържа необходимите свойства и предоставя методи за тяхното последователно задаване. Задължителните параметри се подават чрез конструктора на Builder класа, а незадължителните се задават чрез каскадно извикване на методи. След приключване на конфигурирането методът *build()* създава крайния обект.

### Дефиниция

Builder е шаблон за проектиране от групата на създаващите (Creational) шаблони, който разделя процеса на конструиране на сложен обект от неговото представяне и позволява един и същ процес на създаване да бъде използван за изграждане на различни конфигурации на обекта.

### UML диаграма

<img width="383" height="560" alt="Builder" src="https://github.com/user-attachments/assets/dd7615b4-4986-40df-8796-f0230c14ba04" />

### Примерна реализация

```java
public class Book {

    private String isbn;
    private String title;
    private String author;
    private int publishingYear;
    private String genre;
    private String annotation;
    private int pages;
    private double price;

    private Book(Builder builder) {
        this.isbn = builder.isbn;
        this.title = builder.title;
        this.author = builder.author;
        this.publishingYear = builder.publishingYear;
        this.genre = builder.genre;
        this.annotation = builder.annotation;
        this.pages = builder.pages;
        this.price = builder.price;
    }

    public static class Builder {

        private String isbn;
        private String title;
        private String author;
        private int publishingYear;
        private String genre;
        private String annotation;
        private int pages;
        private double price;

        public Builder(String isbn, String title, String author) {
            this.isbn = isbn;
            this.title = title;
            this.author = author;
        }

        public Builder withPublishingYear(int publishingYear) {
            this.publishingYear = publishingYear;
            return this;
        }

        public Builder withGenre(String genre) {
            this.genre = genre;
            return this;
        }

        public Builder withAnnotation(String annotation) {
            this.annotation = annotation;
            return this;
        }

        public Builder withPages(int pages) {
            this.pages = pages;
            return this;
        }

        public Builder withPrice(double price) {
            this.price = price;
            return this;
        }

        public Book build() {
            return new Book(this);
        }
    }

    // getters
}
```
Използването на шаблона изглежда по следния начин:
```java
public class Application {

    public static void main(String[] args) {

        Book bookOne = new Book.Builder("123456", "East of Eden", "John Steinbeck")
                .withGenre("Classics")
                .withPublishingYear(1999)
                .withPages(512)
                .withPrice(30.85)
                .build();

        Book bookTwo = new Book.Builder("147147", "The Great Gatsby", "Francis Scott Fitzgerald")
                .withPrice(28.75)
                .build();
    }
}
```
Конструкторът на класа Book е private, което не позволява директното създаване на обекти. Единственият начин за създаване на инстанции е чрез вътрешния клас Builder, който последователно задава необходимите стойности и накрая извиква метода build().

> [!IMPORTANT]
> Builder е подходящ когато обектът има много параметри, особено ако част от тях са незадължителни. Ако класът има малко на брой задължителни полета, използването на Builder може да усложни
> кода без реална необходимост.

### Предимства

* позволява създаване на обекти с голям брой параметри без множество конструктори;
* разделя процеса на конструиране от представянето на обекта;
* подобрява четимостта на кода чрез последователно задаване на параметрите;
* позволява ясно разграничаване между задължителни и незадължителни свойства;
* улеснява бъдещо разширяване на класа с нови свойства.

### Недостатъци

* увеличава броя на класовете и количеството код;
* не е подходящ за прости класове с малък брой свойства;
* при всяка промяна в структурата на класа обикновено се налага актуализиране и на Builder класа.

### Приложение

Шаблонът Builder е подходящ когато:
* обектът съдържа голям брой задължителни и незадължителни свойства;
* е необходимо създаването на различни конфигурации на един и същ тип обект;
* се цели подобряване на четимостта и поддържаемостта на кода;
* се разработват библиотеки и API, при които създаването на обекти трябва да бъде максимално ясно и удобно.



