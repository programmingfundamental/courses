---
layout: default
title: Builder
parent: Лабораторно упражнение 3
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 1
---

# Builder

### Проблем

Да се разработи клас Book, който съхранява информация за книга.

За всяка книга задължително трябва да бъдат зададени ISBN, заглавие и автор. Останалите характеристики – година на издаване, жанр, анотация, брой страници и цена – са незадължителни.

### Решение без използване на шаблона

**Подход 1 – използване на един конструктор с всички параметри**

Един възможен подход е всички свойства да бъдат зададени чрез един конструктор:

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

    public Book(String isbn,
                String title,
                String author,
                int publishingYear,
                String genre,
                String annotation,
                int pages,
                double price) {

        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publishingYear = publishingYear;
        this.genre = genre;
        this.annotation = annotation;
        this.pages = pages;
        this.price = price;
    }

// getters, setters, etc.
}
```
При създаване на обект незадължителните параметри трябва да бъдат зададени със стойности по подразбиране или с null:
```java
Book firstBook = new Book(
        "123456",
        "East of Eden",
        "John Steinbeck",
        1999,
        "Classics",
        "Novel",
        512,
        30.85);

Book secondBook = new Book(
        "147147",
        "The Great Gatsby",
        "Francis Scott Fitzgerald",
        0,
        null,
        null,
        0,
        28.75);
```
**Подход 2 – използване на претоварени конструктори**

При този подход постепенно се добавят нови конструктори, които позволяват задаването на различни комбинации от свойства:

```java

// същите атрибути

public Book(String isbn,
            String title,
            String author) { ... }

public Book(String isbn,
            String title,
            String author,
            int publishingYear,
            String genre) { ... }

public Book(String isbn,
            String title,
            String author,
            int publishingYear,
            String genre,
            String annotation,
            int pages) { ... }

public Book(String isbn,
            String title,
            String author,
            int publishingYear,
            String genre,
            String annotation,
            int pages,
            double price) { ... }
```
### Недостатъци на решението

И двата подхода имат своите ограничения.

Използването на един конструктор с всички параметри избягва необходимостта от множество претоварени конструктори, но води до дълги списъци от аргументи, които са трудни за четене и използване. Освен това за незадължителните свойства често се налага подаването на null или стойности по подразбиране.

При използване на претоварени конструктори с увеличаване броя на свойствата постепенно нараства и броят на конструкторите. Това усложнява реализацията и поддръжката на класа, а също така не позволява лесно създаване на всички възможни комбинации от незадължителни свойства.

Следователно е необходимо решение, което позволява ясно разграничаване между задължителните и незадължителните свойства, без създаване на множество претоварени конструктори и без използване на дълги списъци от параметри.

И двата подхода са напълно приложими при класове с малък брой свойства. Проблемите започват да се проявяват, когато броят на задължителните и незадължителните параметри постепенно нараства.

### Шаблонът като решение

Шаблонът **Builder** разделя процеса на създаване на обекта от неговото представяне.

При реализацията с вътрешен статичен клас помощният клас Builder съдържа необходимите свойства и предоставя методи за тяхното последователно задаване. Задължителните параметри се подават чрез конструктора на Builder, а незадължителните се задават чрез каскадно извикване на методи. След приключване на конфигурирането методът build() създава крайния обект.

### Дефиниция

Builder е шаблон за проектиране от групата на **шаблоните за създаване (Creational Design Patterns)**. Неговата цел е да отдели процеса на конструиране на сложен обект от неговото представяне и да позволи създаването на различни конфигурации на обекта чрез последователно задаване на неговите свойства.

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
Конструкторът на класа Book е деклариран като private, поради което обекти не могат да бъдат създавани директно чрез оператора new.

Създаването на обекта се извършва чрез вътрешния статичен клас Builder. Задължителните свойства се подават чрез неговия конструктор, а незадължителните се задават чрез последователно извикване на методите with...(). Всеки от тези методи връща текущия обект Builder, което позволява каскадно извикване на методите.

След приключване на конфигурирането методът build() създава и връща окончателния обект Book.

> [!IMPORTANT]
> Builder е подходящ при създаване на обекти с голям брой свойства, особено когато част от тях
> са незадължителни. Ако класът съдържа малък брой параметри, използването на Builder може да
> усложни реализацията без да носи съществени ползи.

### Предимства

* позволява създаване на обекти с голям брой параметри без множество конструктори;
* разделя процеса на конструиране от представянето на обекта;
* подобрява четимостта на кода чрез последователно задаване на параметрите;
* позволява ясно разграничаване между задължителни и незадължителни свойства;
* улеснява бъдещо разширяване на класа с нови свойства.

### Недостатъци

* увеличава количеството код;
* въвежда допълнителен клас;
* не е подходящ за прости класове с малък брой свойства;
* при промяна в структурата на класа обикновено е необходимо актуализиране и на класа Builder.

### Приложение

Шаблонът Builder е подходящ когато:
* обектът съдържа голям брой задължителни и незадължителни свойства;
* е необходимо създаването на различни конфигурации на един и същ тип обект;
* се цели подобряване на четимостта и поддържаемостта на кода;
* се разработват библиотеки и API, при които създаването на обекти трябва да бъде максимално ясно и удобно.



