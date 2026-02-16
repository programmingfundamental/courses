---
layout: default
title: Лабораторно упражнение 1
parent: Обектно-ориентирано програмиране - 2 част
has_children: true
nav_order: 1
permalink: /docs/obektno-orientirano-programirane-2-chast/laboratorno-uprazhnenie-1
---

# Лабораторно упражнение 1

### Композиция и агрегация

В обектно-ориентираното програмиране композицията и агрегацията представляват форми на асоциация между обекти, чрез които се моделират връзки от тип "част - цяло". Те се използват за изграждане на по-сложни системи чрез комбиниране на по-малки обекти.

Основното предназначение на композицията и агрегацията е:

- моделиране на реални зависимости между обектите

- реализиране на модулност и разделяне на отговорностите

- увеличаване на възможността за преизползване на съществуващ код

- намаляване зависимостите между обектите (в сравнение с използването на наследяване).

## Композиция

Композицията се използва за създаване на силна връзка между обектите и е подходяща в случаите, когато единия обект ("частта") не може да съществува смислено без другия ("цялото"). При унищожаване на композитни обект, частта също се унищожава.

Съществуват различни типове композиция според начина на тяхната реализация:

- класическа композиция, при която "частите" се създават в самия композитен обект

  <img width="176" height="233" alt="classicComposition" src="https://github.com/user-attachments/assets/23ab1905-0fd2-4514-82ec-066756dde617" />
  

```java
public class Address {

  private String town;
  private String streetName;
  private int number;

  // constructor, getters, setters, methods
}

public class Building {

  private Address address;

  public Building() {
    this.address = new Address(); // обектът се създава вътре
  }
        
  // methods
}

```

- композиция с множественост (1-to-many)


<img width="370" height="217" alt="multipleComposition" src="https://github.com/user-attachments/assets/2b5a9c55-5df7-4ed4-ae67-2ed3d2f7b8b5" />



```java
public class DocumentationChapter {
  private int number;
  private String chapterTitle;

  public DocumentationChapter(int number, String chapterTitle) {
    this.number = number;
    this.chapterTitle = chapterTitle;
  }
}

public class ProjectDocumentation {

  private List<DocumentationChapter> documentation = new ArrayList<>();

  public ProjectDocumentation() {
    documentation.add(new DocumentationChapter(1, "Title page"));
    documentation.add(new DocumentationChapter(2, "Project introduction"));
    documentation.add(new DocumentationChapter(3, "Project body"));
    documentation.add(new DocumentationChapter(4, "Conclusion"));
  }
}

```

- композиция с контрол върху достъпа, при която частите са капсулирани и композитния обект не позволява външни модификации


<img width="406" height="234" alt="controlledComposition" src="https://github.com/user-attachments/assets/20068d34-7a35-46f4-89ab-9591f766f145" />



```java
public class DocumentationChapter {

  private int number;
  private String chapterTitle;

  public DocumentationChapter(int number, String chapterTitle) {
    this.number = number;
    this.chapterTitle = chapterTitle;
  }
}

public class ProjectDocumentation {

  private List<DocumentationChapter> documentation;

  public ProjectDocumentation() {
    documentation = List.of(
      new DocumentationChapter(1, "Title page"),
      new DocumentationChapter(2, "Project introduction"),
      new DocumentationChapter(3, "Project body"),
      new DocumentationChapter(4, "Conclusion")
    );
  }

  public List<DocumentationChapter> getDocumentation() {
    return Collections.unmodifiableList(documentation);
  }
}

```



## Агрегация

Агрегацията се използва в случаи, в които връзката между обектите е по-слаба и частта може да съществува самостоятелно извън контекста на сложния обект. Обект, който е използван като част от сложен такъв, може да бъде споделян между различни сложни обекти и в различни контексти, като по този начин той е независим от тяхното съществуване.

Аналогично на композицията, според различната реализация съществуват няколко типа агрегация:

- проста агрегация, при която частите се подават отвън и съществуват независимо от сложния обект


<img width="187" height="185" alt="simpleAggregation" src="https://github.com/user-attachments/assets/943f83f1-acd8-4f40-9500-a7a726a24eac" />



```java

public class Book {

  // properties, constructors, methods

}

public class Library {

  private List<Book> catalog;

  public Library(List<Book> catalog) {
    this.catalog = catalog; // подадени отвън
  }

}

```

- еднопосочна агрегация, при която само единия обект знае за другия


<img width="216" height="234" alt="unidirectionalAggregation" src="https://github.com/user-attachments/assets/a945ce35-c9ba-4f7a-810b-b367d5bb5d13" />


```java

public class Book {

  private String title;

  public Book(String title) {
    this.title = title;
  }

  // methods

}

public class Reader {

  private String name;
  private Set<Book> books = new HashSet<>();

  public Reader(String name) {
    this.name = name;
  }

  public void borrowBook(Book book) {
    books.add(book);
  }

}

```

- двупосочна агрегация, при която и двете страни знаят една за друга


<img width="202" height="234" alt="bDirectionalAgg" src="https://github.com/user-attachments/assets/1de35aa1-f1e4-4cfa-baf0-1a6fc8e82a13" />


```java

public class Book {

  private String title;
  private Set<Reader> readers = new HashSet<>();

  public Book(String title) {
    this.title = title;
  }

  public void addReader(Reader reader) {
    readers.add(reader);
  }

}

public class Reader {

  private String name;
  private Set<Book> books = new HashSet<>();

  public Reader(String name) {
    this.name = name;
  }

  public void borrowBook(Book book) {
    books.add(book);
    book.addReader(this);
  }
        
}

```

- множествена агрегация чрез посредник (в случая, библиотека), при която обектите остават независими един от друг и се подават отвън


<img width="278" height="250" alt="aggregationWithMediator" src="https://github.com/user-attachments/assets/7d607427-3755-4695-a110-8595b2a8d99f" />


```java

public class Book {

  private String title;

  public Book(String title) {
    this.title = title;
  }

}

public class Reader {

  private String name;

  public Reader(String name) {
    this.name = name;
  }
        
}

public class Library {

  private Set<Book> books = new HashSet<>();
  private Set<Reader> readers = new HashSet<>();

  public void addBook(Book book) {
    books.add(book);
  }
        
  public void addReader(Reader reader) {
    readers.add(reader);
  }

}

```


- агрегация чрез асоциативен клас (асоциативна агрегация), при която отново обектите са независими


<img width="278" height="201" alt="associativeAggregation" src="https://github.com/user-attachments/assets/6cc9f9c9-56cc-4fa2-9a19-19e3ea6cf56b" />


```java

public class Book {

  private String title;

  public Book(String title) {
    this.title = title;
  }

}

public class Reader {

  private String name;

  public Reader(String name) {
    this.name = name;
  }
        
}

public class LoanBook {

  private Book book;
  private Reader reader;
  private LocalDate date;

  public LoanBook(Book book, Reader reader, LocalDate date) {
    this.book = book;
    this.reader = reader;
    this.date = date;
  }

}

```


## Композиция vs Агрегация

Композицията моделира структурна зависимост, докато агрегацията моделира логическа принадлежност.

Таблицата по-долу обобщава и сравнява двете форми по няколко критерия.

| Критерий             | Композиция | Агрегация |
|----------------------|------------|-----------|
| степен на свързаност | силна      | слаба     |
| жизнен цикъл         | зависим    | независим |
| собственост          | има        | няма      |
| споделяемост         | не         | да        |

Изборът между асоциация, агрегация и композиция е архитектурно решение, което определя степента на зависимост, гъвкавост и поддържаемост на системата.

В практиката композицията и агрегацията се използват за:

- структуриране на домейн модели
  
- изграждане на микросървисни и слоести архитектури
  
- dependency injection
  
- създаване на гъвкави и заменяеми компоненти.

Тяхното използване подпомага прилагането на принципи като Single Responsibility Principle, Dependency Inversion Principle и Low Coupling / High Cohesion.

