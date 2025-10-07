---
layout: default
title: Builder
parent: Лабораторно упражнение 3
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 1
---

# Builder

Шаблоните за проектиране представляват решения на често срещани проблеми.

Шаблонът Builder спада към така наречените creational шаблони, като той предоставя възможност за създаване на сложни обекти без използването на много и различни експлицитни конструктори.

Нека разгледаме следния пример:

```
public class Book {
    private String isbn;
    private String title;
    private String author;
    private int publishingYear;
    private String genre;
    private String annotation;
    private int pages;
    private double price;
    public Book(String isbn, String title, String author, int publishingYear, String genre, String annotation, int pages, double price) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publishingYear = publishingYear;
        this.genre = genre;
        this.annotation = annotation;
        this.pages = pages;
        this.price = price;
    }
    // getters
}
```

Нека обаче приемем, че само първите три атрибута са задължителни (ISBN, заглавие и автор). Това означава, че всеки път, когато създаваме обекти от този клас, ще предаваме нулева стойност ако даден параметър липсва:

```
public class Main {
    public static void main(String[] args) {
        Book bookOne = new Book("123456", "East of Eden", "John Steinbeck", 1999,
                "classics",null, 512, 30.85);
        Book bookTwo = new Book("147147", "The Great Gatsby", "Francis Scott Fitzgerald", 0,
                "classics", null, 0, 28.75);
    }
}
```

Един от възможните подходи за избягване на предаване на нулеви параметри е използването на множество експлицитни конструктори:

```
public class Book {
    private String isbn;
    private String title;
    private String author;
    private int publishingYear;
    private String genre;
    private String annotation;
    private int pages;
    private double price;
    public Book(String isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }
    public Book(String isbn, String title, String author, int publishingYear) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publishingYear = publishingYear;
    }
    public Book(String isbn, String title, String author, String genre) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
    }
    public Book(String isbn, String title, String author, int publishingYear, String genre) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publishingYear = publishingYear;
        this.genre = genre;
    }
    public Book(String isbn, String title, String author, String genre, String annotation) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.annotation = annotation;
    }
    public Book(String isbn, String title, String author, int publishingYear, String annotation, int pages) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publishingYear = publishingYear;
        this.annotation = annotation;
        this.pages = pages;
    }
    public Book(String isbn, String title, String author, double price) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.price = price;
    }
    // getters
}
```

Тук обаче не трябва да се забравя, че не може да съществуват конструктори с еднакви по брой и типове параметри – така например, не може да има конструктор по ISBN, заглавие, автор и година на издаване, и едновременно с това конструктор по ISBN, заглавие, автор и брой страници на книгата.

### _Използване на Builder_

Как би изглеждал класът Book с използване на шаблона Builder:

```
// Some code
public class Book {
    public static class Builder {
        private String isbn;
        private String title;
        private String author;
        private int publishingYear;
        private String genre;
        private String annotation;
        private int pages;
        private double price;
        public Builder (String isbn, String title, String author) {
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
            return new Book();
        }
    }
    private Book() {}
    // getters, setters, toString, etc.
}
```

Необходимо е да се обърне внимание, че конструкторът на класа Book е частен, т.е. е невъзможно създаването на обект чрез неговото директно извикване. Създаването на обектите задължително минава през извикването на Builder.

Тогава създаването на двата обекта от примера по-горе ще изглежда по следния начин:

```
// Some code
public class Main {
    public static void main(String[] args) {
        Book bookOne = new Book.Builder("123456", "East of Eden", "John Steinbeck")
                .withGenre("classics")
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

### _Предимства и недостатъци от използването на Builder_

Със сигурност използването на шаблон Builder води до писане на повече код, но той е по-лесно читаем и проекта става по-гъвкав.

Множеството параметри от конструктора се редуцират и създаването на обекти се свежда до каскадно извикване на методи за отделните параметри. По този начин се избягва предаването на нулеви стойности за атрибутите, които не са задължителни.

Създаването на обекти се управлява от едно място което създава сигурност в проектирането на сафтуера.

